package com.cheatsheet.quiz.api.security;

import lombok.RequiredArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import com.cheatsheet.quiz.common.constants.ApiErrorTypes;
import com.cheatsheet.quiz.common.model.ApiError;
import com.cheatsheet.quiz.config.app.AppProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
 * Политики доступа для чувствительных endpoint (admin, export, regenerate).
 */
@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SensitiveEndpointAccessService {

    public static final String ADMIN_TOKEN_HEADER = "X-Admin-Token";
    public static final String ROLE_ADMIN = OrgIdpGroupAuthoritiesMapper.ROLE_ADMIN;
    private static final Duration REGENERATE_WINDOW = Duration.ofMinutes(1);
    private static final int REGENERATE_WINDOW_SECONDS = 60;
    private static final String REGENERATE_RATE_LIMIT_KEY = "regenerate";
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

    /**
     * Sensitive endpoints: valid {@code X-Admin-Token} or authenticated principal with {@code ROLE_ADMIN}.
     */
    public boolean isAuthorizedForSensitive(@Nullable String token) {
        return isAuthorized(token) || hasAdminRole();
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
     * Строит единообразный 403 ответ для чувствительных endpoint (без раскрытия конфигурации).
     */
    public ResponseEntity<ApiError> buildForbiddenResponse(@Nullable String token) {
        ApiError error = new ApiError(HttpStatus.FORBIDDEN.value(), ApiErrorTypes.FORBIDDEN,
                "Недостаточно прав", null);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(error);
    }

    /**
     * True, если запрос адресован API-подобному пути ({@code /api/**} или {@code /export}).
     * Общий предикат для security-хендлеров: определяет JSON-ответ vs HTML/redirect.
     */
    public boolean isApiLikeRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri != null && (uri.startsWith("/api/") || uri.startsWith("/export"));
    }

    /**
     * Единая точка сериализации {@link ApiError} в JSON-тело ответа (UTF-8) с заданным статусом.
     * Централизует идентичную запись, ранее продублированную в обоих security-хендлерах.
     */
    public void writeJson(HttpServletResponse response, int status, ApiError error) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        objectMapper.writeValue(response.getOutputStream(), error);
    }

    /**
     * Возвращает {@code null}, если доступ разрешён; иначе возвращает стандартный 403 и пишет security-log.
     */
    @Nullable
    public ResponseEntity<ApiError> forbiddenIfUnauthorized(@Nullable String token, String action) {
        if (isAuthorizedForSensitive(token)) {
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
