package com.cheatsheet.quiz.api.security;

import com.cheatsheet.quiz.api.dto.ApiError;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class SensitiveEndpointAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;
    private final SensitiveEndpointAccessService accessService;

    public SensitiveEndpointAccessDeniedHandler(
            ObjectMapper objectMapper,
            SensitiveEndpointAccessService accessService
    ) {
        this.objectMapper = objectMapper;
        this.accessService = accessService;
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException, ServletException {
        if (!isApiLikeRequest(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
            return;
        }
        String token = request.getHeader(SensitiveEndpointAccessService.ADMIN_TOKEN_HEADER);
        ApiError error = accessService.buildForbiddenResponse(token).getBody();
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getOutputStream(), error);
    }

    private static boolean isApiLikeRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri != null && (uri.startsWith("/api/") || uri.startsWith("/export"));
    }
}
