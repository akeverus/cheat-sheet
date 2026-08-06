package com.cheatsheet.quiz.api.security;

import lombok.RequiredArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import com.cheatsheet.quiz.common.web.RequestFormat;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Возвращает стандартизированный JSON-ответ для denied-доступа к чувствительным API endpoint.
 */
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SensitiveEndpointAccessDeniedHandler implements AccessDeniedHandler {

    SensitiveEndpointAccessService accessService;

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException, ServletException {
        // Браузерная навигация (явный text/html) → error.html.
        // XHR/API (/export, /api/*) без HTML в Accept → JSON.
        if (RequestFormat.acceptsHtml(request) || !accessService.isApiLikeRequest(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
            return;
        }
        accessService.writeForbidden(request, response);
    }
}
