package com.cheatsheet.quiz.api.security;

import lombok.RequiredArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import com.cheatsheet.quiz.common.model.ApiError;
import com.cheatsheet.quiz.config.app.AppProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Auth entry point: admin-token gate; form-login redirect for browsers in multi
 * (preserves SavedRequest); HTTP Basic / JSON 401 for APIs.
 */
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SensitiveEndpointAuthenticationEntryPoint implements AuthenticationEntryPoint {

    ObjectMapper objectMapper;
    SensitiveEndpointAccessService accessService;
    AppProperties appProperties;
    AuthenticationEntryPoint formLoginEntryPoint = new LoginUrlAuthenticationEntryPoint("/login");

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {
        if (isAdminSensitive(request)) {
            // Browser link/nav (Accept includes text/html) → error.html.
            // XHR/API without HTML Accept (MockMvc, apiFetch) → JSON.
            if (acceptsHtml(request) || !isApiLikeRequest(request)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
                return;
            }
            String token = request.getHeader(SensitiveEndpointAccessService.ADMIN_TOKEN_HEADER);
            ApiError error = accessService.buildForbiddenResponse(token).getBody();
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            objectMapper.writeValue(response.getOutputStream(), error);
            return;
        }

        boolean multi = "multi".equalsIgnoreCase(appProperties.getSecurity().getMode());
        if (multi && !isApiLikeRequest(request) && prefersHtml(request)) {
            formLoginEntryPoint.commence(request, response, authException);
            return;
        }

        if (multi) {
            response.setHeader("WWW-Authenticate", "Basic realm=\"cheat-sheet\"");
        }

        if (!isApiLikeRequest(request)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            return;
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getOutputStream(),
                new ApiError(HttpServletResponse.SC_UNAUTHORIZED, "UNAUTHORIZED",
                        "Authentication required", null));
    }

    private static boolean prefersHtml(HttpServletRequest request) {
        String accept = request.getHeader("Accept");
        if (accept == null || accept.isBlank()) {
            return true;
        }
        return accept.contains(MediaType.TEXT_HTML_VALUE)
                || (!accept.contains(MediaType.APPLICATION_JSON_VALUE) && accept.contains("*/*"));
    }

    /** Explicit HTML Accept only — used for admin-gated /export browser navigation. */
    private static boolean acceptsHtml(HttpServletRequest request) {
        String accept = request.getHeader("Accept");
        return accept != null && accept.contains(MediaType.TEXT_HTML_VALUE);
    }

    private static boolean isAdminSensitive(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri == null) {
            return false;
        }
        return uri.startsWith("/api/admin")
                || uri.startsWith("/api/regenerate")
                || uri.startsWith("/export")
                || uri.startsWith("/settings/reset-options");
    }

    private static boolean isApiLikeRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri != null && (uri.startsWith("/api/") || uri.startsWith("/export"));
    }
}
