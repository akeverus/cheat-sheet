package com.cheatsheet.quiz.api.security;

import com.cheatsheet.quiz.api.dto.ApiError;
import com.cheatsheet.quiz.api.exception.ApiErrorTypes;
import com.cheatsheet.quiz.config.AppProperties;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.Arrays;

/**
 * Политики доступа для чувствительных endpoint (admin, export, regenerate).
 */
@Component
@Slf4j
public class SensitiveEndpointAccessService {

    public static final String ADMIN_TOKEN_HEADER = "X-Admin-Token";
    private static final Duration REGENERATE_WINDOW = Duration.ofMinutes(1);
    private static final int REGENERATE_WINDOW_SECONDS = 60;
    private static final String REGENERATE_RATE_LIMIT_KEY = "regenerate";
    private static final String UNKNOWN_CLIENT = "unknown";

    private final AppProperties appProperties;
    private final Environment environment;
    private final RequestRateLimiter requestRateLimiter;

    public SensitiveEndpointAccessService(
            AppProperties appProperties,
            Environment environment,
            RequestRateLimiter requestRateLimiter
    ) {
        this.appProperties = appProperties;
        this.environment = environment;
        this.requestRateLimiter = requestRateLimiter;
    }

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

    public boolean isTokenConfigured() {
        String configuredToken = appProperties.getAdminToken();
        return configuredToken != null && !configuredToken.isBlank();
    }

    /**
     * Строит единообразный 403 ответ для чувствительных endpoint.
     *
     * @param token значение заголовка X-Admin-Token (может быть null)
     * @return 403 + ApiError: если токен не настроен — «Операция запрещена: app.admin-token не настроен», иначе «Недостаточно прав»
     */
    public ResponseEntity<ApiError> buildForbiddenResponse(@Nullable String token) {
        String message = isTokenConfigured()
                ? "Недостаточно прав"
                : "Операция запрещена: app.admin-token не настроен";
        ApiError error = new ApiError(HttpStatus.FORBIDDEN.value(), ApiErrorTypes.FORBIDDEN, message, null);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(error);
    }

    /**
     * Возвращает {@code null}, если токен валиден; иначе возвращает стандартный 403 и пишет security-log.
     */
    @Nullable
    public ResponseEntity<ApiError> forbiddenIfUnauthorized(@Nullable String token, String action) {
        if (isAuthorized(token)) {
            return null;
        }
        log.warn("Неавторизованная попытка {}", action);
        return buildForbiddenResponse(token);
    }

    public boolean allowRegenerate(HttpServletRequest request) {
        String clientKey = clientKey(request, appProperties.isTrustForwardedForHeader());
        int maxRequests = Math.max(1, appProperties.getRegenerateRateLimitPerMinute());
        boolean allowed = requestRateLimiter.allow(
                REGENERATE_RATE_LIMIT_KEY + ":" + clientKey,
                maxRequests,
                REGENERATE_WINDOW
        );
        if (!allowed) {
            log.warn("Rate limit exceeded для {} (client={}, max={}/{}s)",
                    REGENERATE_RATE_LIMIT_KEY, clientKey, maxRequests, REGENERATE_WINDOW_SECONDS);
        }
        return allowed;
    }

    public int regenerateRetryAfterSeconds() {
        return REGENERATE_WINDOW_SECONDS;
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
