package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.api.dto.ApiError;
import com.cheatsheet.quiz.api.dto.response.RegenerateResponse;
import com.cheatsheet.quiz.api.exception.ApiErrorTypes;
import com.cheatsheet.quiz.api.security.SensitiveEndpointAccessService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Use-case orchestration для endpoint `/api/regenerate`.
 */
@Service
public class RegenerateEndpointService {

    private final SensitiveEndpointAccessService accessService;
    private final RegenerateService regenerateService;

    public RegenerateEndpointService(
            SensitiveEndpointAccessService accessService,
            RegenerateService regenerateService
    ) {
        this.accessService = accessService;
        this.regenerateService = regenerateService;
    }

    public RegenerateResult execute(long questionId, String token, HttpServletRequest httpRequest) {
        ResponseEntity<ApiError> forbidden = accessService.forbiddenIfUnauthorized(token, "regenerate вариантов");
        if (forbidden != null) {
            return RegenerateResult.forbidden(forbidden.getBody());
        }
        if (!accessService.allowRegenerate(httpRequest)) {
            long retryAfter = accessService.regenerateRetryAfterSeconds();
            ApiError error = new ApiError(429, ApiErrorTypes.RATE_LIMIT_EXCEEDED,
                    "Слишком много запросов к /api/regenerate, повторите позже",
                    Map.of("retryAfterSeconds", retryAfter));
            return RegenerateResult.rateLimited(error, retryAfter);
        }

        regenerateService.regenerateQuestion(questionId);
        return RegenerateResult.success(new RegenerateResponse(
                true,
                questionId,
                "Варианты, подсказки и диаграмма удалены. При следующем показе будут сгенерированы заново."
        ));
    }

    public record RegenerateResult(
            Status status,
            ApiError error,
            Long retryAfterSeconds,
            RegenerateResponse payload
    ) {
        public enum Status {
            FORBIDDEN,
            RATE_LIMITED,
            SUCCESS
        }

        public static RegenerateResult forbidden(ApiError error) {
            return new RegenerateResult(Status.FORBIDDEN, error, null, null);
        }

        public static RegenerateResult rateLimited(ApiError error, long retryAfterSeconds) {
            return new RegenerateResult(Status.RATE_LIMITED, error, retryAfterSeconds, null);
        }

        public static RegenerateResult success(RegenerateResponse payload) {
            return new RegenerateResult(Status.SUCCESS, null, null, payload);
        }
    }
}
