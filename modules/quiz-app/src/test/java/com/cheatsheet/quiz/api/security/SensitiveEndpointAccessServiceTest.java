package com.cheatsheet.quiz.api.security;

import com.cheatsheet.quiz.common.constants.ApiErrorTypes;
import com.cheatsheet.quiz.common.web.CorrelationIdFilter;
import com.cheatsheet.quiz.config.app.AppProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
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
        appProperties.setSensitiveRateLimitPerMinute(20);
        lenient().when(environment.getActiveProfiles()).thenReturn(new String[]{"test"});
        service = new SensitiveEndpointAccessService(appProperties, environment, requestRateLimiter, new ObjectMapper());
    }

    @Test
    void isAuthorizedRejectsWrongToken() {
        assertThat(service.isAuthorized("wrong-token")).isFalse();
    }

    @Test
    void isAuthorizedAcceptsConfiguredToken() {
        assertThat(service.isAuthorized("top-secret")).isTrue();
    }

    @Test
    void hasAdminRoleDetectsAdminAuthority() {
        org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        "admin-user",
                        "n/a",
                        java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority(
                                SensitiveEndpointAccessService.ROLE_ADMIN))));
        try {
            assertThat(service.hasAdminRole()).isTrue();
        } finally {
            org.springframework.security.core.context.SecurityContextHolder.clearContext();
        }
    }

    @Test
    void allowSensitiveRequestUsesRemoteAddrWhenForwardedHeaderIsNotTrusted() {
        appProperties.setTrustForwardedForHeader(false);
        when(request.getRemoteAddr()).thenReturn("10.1.2.3");
        when(requestRateLimiter.allow(anyString(), anyInt(), any(Duration.class))).thenReturn(true);

        boolean allowed = service.allowSensitiveRequest(request);

        assertThat(allowed).isTrue();
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        verify(requestRateLimiter).allow(keyCaptor.capture(), anyInt(), any(Duration.class));
        assertThat(keyCaptor.getValue()).isEqualTo("sensitive:10.1.2.3");
    }

    @Test
    void allowSensitiveRequestUsesForwardedHeaderWhenTrusted() {
        appProperties.setTrustForwardedForHeader(true);
        when(request.getHeader("X-Forwarded-For")).thenReturn("198.51.100.9, 10.0.0.10");
        when(requestRateLimiter.allow(anyString(), anyInt(), any(Duration.class))).thenReturn(true);

        boolean allowed = service.allowSensitiveRequest(request);

        assertThat(allowed).isTrue();
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        verify(requestRateLimiter).allow(keyCaptor.capture(), anyInt(), any(Duration.class));
        assertThat(keyCaptor.getValue()).isEqualTo("sensitive:198.51.100.9");
    }

    @Test
    void writeForbiddenDoesNotRevealAdminTokenConfiguration() throws Exception {
        appProperties.setAdminToken(" ");
        MockHttpServletResponse response = new MockHttpServletResponse();

        service.writeForbidden(request, response);

        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(response.getContentAsString())
                .contains("Недостаточно прав")
                .doesNotContain("app.admin-token");
    }

    @Test
    void isApiLikeRequestTrueForApiAndExportPaths() {
        when(request.getRequestURI()).thenReturn("/api/admin/reset");
        assertThat(service.isApiLikeRequest(request)).isTrue();
    }

    @Test
    void isApiLikeRequestTrueForExportPath() {
        when(request.getRequestURI()).thenReturn("/export/all.zip");
        assertThat(service.isApiLikeRequest(request)).isTrue();
    }

    @Test
    void isApiLikeRequestFalseForBrowserPathAndNullUri() {
        when(request.getRequestURI()).thenReturn("/interview/start");
        assertThat(service.isApiLikeRequest(request)).isFalse();
        when(request.getRequestURI()).thenReturn(null);
        assertThat(service.isApiLikeRequest(request)).isFalse();
    }

    @Test
    void writeProblemEmitsRfc7807BodyWithErrorCodeAndCorrelationId() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(request.getAttribute(CorrelationIdFilter.REQUEST_ATTRIBUTE)).thenReturn("cid-42");

        service.writeProblem(request, response, HttpStatus.UNAUTHORIZED,
                ApiErrorTypes.UNAUTHORIZED, "Требуется аутентификация");

        assertThat(response.getStatus()).isEqualTo(401);
        // MockHttpServletResponse дописывает ;charset=UTF-8 к content-type.
        assertThat(response.getContentType()).startsWith(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        assertThat(response.getCharacterEncoding()).isEqualToIgnoringCase("UTF-8");
        // Кириллица не должна биться → UTF-8 round-trip.
        assertThat(response.getContentAsString())
                .contains("Требуется аутентификация")
                .contains(ApiErrorTypes.UNAUTHORIZED)
                .contains("cid-42");
    }

    @Test
    void validateProductionTokenConfigurationThrowsWhenTokenMissing() {
        appProperties.setAdminToken(" ");
        when(environment.getActiveProfiles()).thenReturn(new String[]{"prod"});
        SensitiveEndpointAccessService prodService =
                new SensitiveEndpointAccessService(appProperties, environment, requestRateLimiter, new ObjectMapper());

        assertThatThrownBy(prodService::validateProductionTokenConfiguration)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("app.admin-token");
    }
}
