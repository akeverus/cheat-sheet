package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.api.dto.ApiError;
import com.cheatsheet.quiz.api.dto.response.RegenerateResponse;
import com.cheatsheet.quiz.api.exception.ApiErrorTypes;
import com.cheatsheet.quiz.api.security.SensitiveEndpointAccessService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegenerateEndpointServiceTest {

    @Mock
    private SensitiveEndpointAccessService accessService;
    @Mock
    private RegenerateService regenerateService;
    @Mock
    private HttpServletRequest httpRequest;

    private RegenerateEndpointService service;

    @BeforeEach
    void setUp() {
        service = new RegenerateEndpointService(accessService, regenerateService);
    }

    @Test
    void executeReturnsForbiddenWhenUnauthorized() {
        String token = "bad-token";
        long questionId = 11L;
        ApiError forbidden = new ApiError(403, ApiErrorTypes.FORBIDDEN, "Недостаточно прав", null);
        when(accessService.forbiddenIfUnauthorized(token, "regenerate вариантов"))
                .thenReturn(ResponseEntity.status(403).body(forbidden));

        RegenerateEndpointService.RegenerateResult result = service.execute(questionId, token, httpRequest);

        assertThat(result.status()).isEqualTo(RegenerateEndpointService.RegenerateResult.Status.FORBIDDEN);
        assertThat(result.error()).isEqualTo(forbidden);
        verify(accessService).forbiddenIfUnauthorized(token, "regenerate вариантов");
        verify(regenerateService, never()).regenerateQuestion(questionId);
    }

    @Test
    void executeReturnsRateLimitedWhenLimiterRejectsRequest() {
        String token = "admin-token";
        long questionId = 22L;
        when(accessService.forbiddenIfUnauthorized(token, "regenerate вариантов")).thenReturn(null);
        when(accessService.allowRegenerate(httpRequest)).thenReturn(false);
        when(accessService.regenerateRetryAfterSeconds()).thenReturn(60);

        RegenerateEndpointService.RegenerateResult result = service.execute(questionId, token, httpRequest);

        assertThat(result.status()).isEqualTo(RegenerateEndpointService.RegenerateResult.Status.RATE_LIMITED);
        assertThat(result.retryAfterSeconds()).isEqualTo(60);
        assertThat(result.error()).isNotNull();
        assertThat(result.error().type()).isEqualTo(ApiErrorTypes.RATE_LIMIT_EXCEEDED);
        verify(regenerateService, never()).regenerateQuestion(questionId);
    }

    @Test
    void executeReturnsSuccessAndDelegatesRegeneration() {
        String token = "admin-token";
        long questionId = 33L;
        when(accessService.forbiddenIfUnauthorized(token, "regenerate вариантов")).thenReturn(null);
        when(accessService.allowRegenerate(httpRequest)).thenReturn(true);

        RegenerateEndpointService.RegenerateResult result = service.execute(questionId, token, httpRequest);

        assertThat(result.status()).isEqualTo(RegenerateEndpointService.RegenerateResult.Status.SUCCESS);
        assertThat(result.payload()).isNotNull();
        assertThat(result.payload().questionId()).isEqualTo(questionId);
        assertThat(result.payload().success()).isTrue();
        verify(regenerateService).regenerateQuestion(questionId);
    }

    @Test
    void toHttpResponseBuildsForbiddenPayload() {
        ApiError error = new ApiError(403, ApiErrorTypes.FORBIDDEN, "Недостаточно прав", null);
        RegenerateEndpointService.RegenerateResult result = RegenerateEndpointService.RegenerateResult.forbidden(error);

        ResponseEntity<?> response = service.toHttpResponse(result);

        assertThat(response.getStatusCode().value()).isEqualTo(403);
        assertThat(response.getBody()).isEqualTo(error);
    }

    @Test
    void toHttpResponseBuildsRateLimitedPayloadWithRetryAfterHeader() {
        ApiError error = new ApiError(429, ApiErrorTypes.RATE_LIMIT_EXCEEDED, "Too many requests", null);
        RegenerateEndpointService.RegenerateResult result =
                RegenerateEndpointService.RegenerateResult.rateLimited(error, 30);

        ResponseEntity<?> response = service.toHttpResponse(result);

        assertThat(response.getStatusCode().value()).isEqualTo(429);
        assertThat(response.getHeaders().getFirst("Retry-After")).isEqualTo("30");
        assertThat(response.getBody()).isEqualTo(error);
    }

    @Test
    void toHttpResponseBuildsSuccessPayload() {
        RegenerateResponse payload = new RegenerateResponse(true, 44L, "ok");
        RegenerateEndpointService.RegenerateResult result =
                RegenerateEndpointService.RegenerateResult.success(payload);

        ResponseEntity<?> response = service.toHttpResponse(result);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(payload);
    }
}
