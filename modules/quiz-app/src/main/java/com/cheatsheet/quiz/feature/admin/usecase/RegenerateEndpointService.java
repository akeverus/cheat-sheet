package com.cheatsheet.quiz.feature.admin.usecase;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import com.cheatsheet.quiz.common.model.ApiError;
import com.cheatsheet.quiz.feature.admin.dto.response.RegenerateResponse;
import com.cheatsheet.quiz.common.constants.ApiErrorTypes;
import com.cheatsheet.quiz.api.security.SensitiveEndpointAccessService;
import com.cheatsheet.quiz.feature.admin.service.RegenerateService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import lombok.Builder;

import java.util.Map;

/**
 * Use-case orchestration для endpoint `/api/regenerate`.
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RegenerateEndpointService {
    SensitiveEndpointAccessService accessService;
    RegenerateService regenerateService;

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

    public ResponseEntity<?> toHttpResponse(RegenerateResult result) {
        if (result.status() == RegenerateResult.Status.FORBIDDEN) {
            return ResponseEntity.status(403).body(result.error());
        }
        if (result.status() == RegenerateResult.Status.RATE_LIMITED) {
            return ResponseEntity.status(429)
                    .header("Retry-After", String.valueOf(result.retryAfterSeconds()))
                    .body(result.error());
        }
        return ResponseEntity.ok(result.payload());
    }

    @Builder(toBuilder = true)
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
