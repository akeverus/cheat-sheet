package com.cheatsheet.quiz.api.security;

import com.cheatsheet.quiz.api.dto.ApiError;
import com.cheatsheet.quiz.config.AppProperties;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SensitiveEndpointAccessServiceTest {

    @Mock
    private Environment environment;
    @Mock
    private RequestRateLimiter requestRateLimiter;
    @Mock
    private HttpServletRequest request;

    private AppProperties appProperties;
    private SensitiveEndpointAccessService service;

    @BeforeEach
    void setUp() {
        appProperties = new AppProperties();
        appProperties.setAdminToken("top-secret");
        appProperties.setRegenerateRateLimitPerMinute(20);
        service = new SensitiveEndpointAccessService(appProperties, environment, requestRateLimiter);
    }

    @Test
    void forbiddenIfUnauthorizedReturnsForbiddenResponse() {
        ResponseEntity<ApiError> response = service.forbiddenIfUnauthorized("wrong-token", "test action");

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().value()).isEqualTo(403);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(403);
    }

    @Test
    void forbiddenIfUnauthorizedReturnsNullForValidToken() {
        ResponseEntity<ApiError> response = service.forbiddenIfUnauthorized("top-secret", "test action");

        assertThat(response).isNull();
    }

    @Test
    void allowRegenerateUsesRemoteAddrWhenForwardedHeaderIsNotTrusted() {
        appProperties.setTrustForwardedForHeader(false);
        when(request.getRemoteAddr()).thenReturn("10.1.2.3");
        when(requestRateLimiter.allow(anyString(), anyInt(), any(Duration.class))).thenReturn(true);

        boolean allowed = service.allowRegenerate(request);

        assertThat(allowed).isTrue();
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        verify(requestRateLimiter).allow(keyCaptor.capture(), anyInt(), any(Duration.class));
        assertThat(keyCaptor.getValue()).isEqualTo("regenerate:10.1.2.3");
    }

    @Test
    void allowRegenerateUsesForwardedHeaderWhenTrusted() {
        appProperties.setTrustForwardedForHeader(true);
        when(request.getHeader("X-Forwarded-For")).thenReturn("198.51.100.9, 10.0.0.10");
        when(requestRateLimiter.allow(anyString(), anyInt(), any(Duration.class))).thenReturn(true);

        boolean allowed = service.allowRegenerate(request);

        assertThat(allowed).isTrue();
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        verify(requestRateLimiter).allow(keyCaptor.capture(), anyInt(), any(Duration.class));
        assertThat(keyCaptor.getValue()).isEqualTo("regenerate:198.51.100.9");
    }
}
