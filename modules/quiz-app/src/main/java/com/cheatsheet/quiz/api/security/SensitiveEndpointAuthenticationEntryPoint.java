package com.cheatsheet.quiz.api.security;

import lombok.RequiredArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import com.cheatsheet.quiz.common.constants.ApiErrorTypes;
import com.cheatsheet.quiz.common.web.RequestFormat;
import com.cheatsheet.quiz.config.app.AppProperties;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
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

    SensitiveEndpointAccessService accessService;
    AppProperties appProperties;
    AuthenticationEntryPoint formLoginEntryPoint = new LoginUrlAuthenticationEntryPoint("/login");

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {
        if (RequestFormat.isAdminSensitivePath(request)) {
            // Браузерная ссылка/навигация (явный text/html) → error.html.
            // XHR/API без HTML в Accept (MockMvc, apiFetch) → JSON.
            if (RequestFormat.acceptsHtml(request) || !accessService.isApiLikeRequest(request)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
                return;
            }
            accessService.writeForbidden(request, response);
            return;
        }

        boolean multi = "multi".equalsIgnoreCase(appProperties.getSecurity().getMode());
        if (multi && !accessService.isApiLikeRequest(request) && RequestFormat.prefersHtml(request)) {
            formLoginEntryPoint.commence(request, response, authException);
            return;
        }

        if (multi) {
            response.setHeader("WWW-Authenticate", "Basic realm=\"cheat-sheet\"");
        }

        if (!accessService.isApiLikeRequest(request)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            return;
        }

        accessService.writeProblem(request, response, HttpStatus.UNAUTHORIZED,
                ApiErrorTypes.UNAUTHORIZED, "Требуется аутентификация");
    }
}
