package com.cheatsheet.quiz.api.security;

import lombok.RequiredArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import com.cheatsheet.quiz.common.constants.ApiErrorTypes;
import com.cheatsheet.quiz.common.web.CorrelationIdFilter;
import com.cheatsheet.quiz.common.web.RequestFormat;
import com.cheatsheet.quiz.config.app.AppProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.Arrays;

/**
 * Политики доступа для чувствительных endpoint ({@code /export},
 * {@code /settings/reset-options}): admin-token, роль администратора,
 * ограничение частоты и единый формат отказа.
 */
@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SensitiveEndpointAccessService {

    public static final String ADMIN_TOKEN_HEADER = "X-Admin-Token";
    public static final String ROLE_ADMIN = OrgIdpGroupAuthoritiesMapper.ROLE_ADMIN;
    private static final Duration RATE_LIMIT_WINDOW = Duration.ofMinutes(1);
    private static final int RATE_LIMIT_WINDOW_SECONDS = 60;
    private static final String SENSITIVE_RATE_LIMIT_KEY = "sensitive";
    private static final String UNKNOWN_CLIENT = "unknown";

    private final AppProperties appProperties;
    private final Environment environment;
    private final RequestRateLimiter requestRateLimiter;
    private final ObjectMapper objectMapper;

    @PostConstruct
    void validateProductionTokenConfiguration() {
        boolean productionProfile = Arrays.stream(environment.getActiveProfiles())
                .anyMatch(profile -> "prod".equalsIgnoreCase(profile));
        if (productionProfile && !isTokenConfigured()) {
            throw new IllegalStateException("Для production профиля обязателен app.admin-token (env APP_ADMIN_TOKEN)");
        }
    }

    public boolean isAuthorized(@Nullable String token) {
        String configuredToken = appProperties.getAdminToken();
        if (configuredToken == null || configuredToken.isBlank()) {
            return false;
        }
        if (token == null || token.isBlank()) {
            return false;
        }
        return MessageDigest.isEqual(
                configuredToken.getBytes(StandardCharsets.UTF_8),
                token.getBytes(StandardCharsets.UTF_8)
        );
    }

    public boolean hasAdminRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (ROLE_ADMIN.equals(authority.getAuthority())) {
                return true;
            }
        }
        return false;
    }

    public boolean isTokenConfigured() {
        String configuredToken = appProperties.getAdminToken();
        return configuredToken != null && !configuredToken.isBlank();
    }

    /**
     * True, если запрос адресован API-подобному пути ({@code /api/**} или {@code /export}).
     * Тонкая обёртка над {@link RequestFormat#isMachineFacingPath}: security-хендлеры
     * получают предикат через инжектированный сервис, а не статикой.
     */
    public boolean isApiLikeRequest(HttpServletRequest request) {
        return RequestFormat.isMachineFacingPath(request);
    }

    /**
     * Отказ в доступе к чувствительному endpoint. Тело — то же RFC-7807
     * {@link ProblemDetail}, что отдаёт {@code GlobalExceptionHandler}: клиенту
     * не нужно различать «ошибку из security-фильтра» и «ошибку из контроллера».
     * Сообщение намеренно не раскрывает, настроен ли admin-token.
     */
    public void writeForbidden(HttpServletRequest request, HttpServletResponse response) throws IOException {
        writeProblem(request, response, HttpStatus.FORBIDDEN, ApiErrorTypes.FORBIDDEN, "Недостаточно прав");
    }

    /**
     * Единая точка сериализации {@link ProblemDetail} в тело ответа (UTF-8).
     * Security-фильтры работают до MVC, поэтому {@code @ControllerAdvice} их не
     * покрывает — контракт приходится воспроизводить здесь вручную.
     */
    public void writeProblem(
            HttpServletRequest request,
            HttpServletResponse response,
            HttpStatus status,
            String errorCode,
            String detail
    ) throws IOException {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(status.getReasonPhrase());
        problem.setProperty("errorCode", errorCode);
        Object correlationId = request.getAttribute(CorrelationIdFilter.REQUEST_ATTRIBUTE);
        if (correlationId != null) {
            problem.setProperty("correlationId", correlationId.toString());
        }

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        objectMapper.writeValue(response.getOutputStream(), problem);
    }

    /**
     * Лимит частоты обращений к чувствительным endpoint (сейчас — полный дамп
     * прогресса в {@code /export}). Ключ — клиентский адрес, окно — минута.
     */
    public boolean allowSensitiveRequest(HttpServletRequest request) {
        String clientKey = clientKey(request, appProperties.isTrustForwardedForHeader());
        int maxRequests = Math.max(1, appProperties.getSensitiveRateLimitPerMinute());
        boolean allowed = requestRateLimiter.allow(
                SENSITIVE_RATE_LIMIT_KEY + ":" + clientKey,
                maxRequests,
                RATE_LIMIT_WINDOW
        );
        if (!allowed) {
            log.warn("Rate limit exceeded для {} (client={}, max={}/{}s)",
                    SENSITIVE_RATE_LIMIT_KEY, clientKey, maxRequests, RATE_LIMIT_WINDOW_SECONDS);
        }
        return allowed;
    }

    public int sensitiveRetryAfterSeconds() {
        return RATE_LIMIT_WINDOW_SECONDS;
    }

    private static String clientKey(HttpServletRequest request, boolean trustForwardedForHeader) {
        if (trustForwardedForHeader) {
            String forwardedFor = request.getHeader("X-Forwarded-For");
            if (forwardedFor != null && !forwardedFor.isBlank()) {
                String first = forwardedFor.split(",")[0].trim();
                if (!first.isBlank()) {
                    return first;
                }
            }
        }
        String remoteAddr = request.getRemoteAddr();
        return (remoteAddr == null || remoteAddr.isBlank()) ? UNKNOWN_CLIENT : remoteAddr;
    }
}
