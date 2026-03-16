package com.cheatsheet.quiz.api.security;

import lombok.RequiredArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import com.cheatsheet.quiz.common.model.ApiError;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Возвращает JSON-ошибку авторизации для чувствительных API endpoint.
 */
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SensitiveEndpointAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;
    private final SensitiveEndpointAccessService accessService;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {
        if (!isApiLikeRequest(request)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
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
