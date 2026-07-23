package com.cheatsheet.quiz.api.security;

import lombok.RequiredArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import com.cheatsheet.quiz.common.model.ApiError;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
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
        // Browser navigation (Accept: text/html) → error.html.
        // XHR/API (/export download, /api/*) without HTML Accept → JSON.
        if (prefersHtml(request) || !accessService.isApiLikeRequest(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
            return;
        }
        String token = request.getHeader(SensitiveEndpointAccessService.ADMIN_TOKEN_HEADER);
        ApiError error = accessService.buildForbiddenResponse(token).getBody();
        accessService.writeJson(response, HttpServletResponse.SC_FORBIDDEN, error);
    }

    /** True only when the client explicitly asks for HTML (browser address bar / link). */
    private static boolean prefersHtml(HttpServletRequest request) {
        String accept = request.getHeader("Accept");
        return accept != null && accept.contains(MediaType.TEXT_HTML_VALUE);
    }
}
