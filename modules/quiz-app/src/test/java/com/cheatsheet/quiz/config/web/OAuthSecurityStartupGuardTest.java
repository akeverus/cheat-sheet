package com.cheatsheet.quiz.config.web;

import com.cheatsheet.quiz.config.app.AppProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OAuthSecurityStartupGuardTest {

    @Test
    void ignoresWhenOauthDisabled() {
        AppProperties props = new AppProperties();
        props.getSecurity().getOauth().setEnabled(false);
        ObjectProvider<ClientRegistrationRepository> provider = mock(ObjectProvider.class);
        OAuthSecurityStartupGuard guard = new OAuthSecurityStartupGuard(props, provider);
        assertThatCode(() -> guard.run(new DefaultApplicationArguments()))
                .doesNotThrowAnyException();
    }

    @Test
    void failsWhenEnabledWithoutMultiMode() {
        AppProperties props = new AppProperties();
        props.getSecurity().setMode("local");
        props.getSecurity().getOauth().setEnabled(true);
        ObjectProvider<ClientRegistrationRepository> provider = mock(ObjectProvider.class);
        OAuthSecurityStartupGuard guard = new OAuthSecurityStartupGuard(props, provider);
        assertThatThrownBy(() -> guard.run(new DefaultApplicationArguments()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("mode=multi");
    }

    @Test
    void failsWhenEnabledWithoutRegistrationRepository() {
        AppProperties props = new AppProperties();
        props.getSecurity().setMode("multi");
        props.getSecurity().getOauth().setEnabled(true);
        ObjectProvider<ClientRegistrationRepository> provider = mock(ObjectProvider.class);
        when(provider.getIfAvailable()).thenReturn(null);
        OAuthSecurityStartupGuard guard = new OAuthSecurityStartupGuard(props, provider);
        assertThatThrownBy(() -> guard.run(new DefaultApplicationArguments()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ClientRegistrationRepository");
    }

    @Test
    void failsWhenRegistrationIdMissing() {
        AppProperties props = new AppProperties();
        props.getSecurity().setMode("multi");
        props.getSecurity().getOauth().setEnabled(true);
        props.getSecurity().getOauth().setRegistrationId("oidc");
        ClientRegistrationRepository repo = mock(ClientRegistrationRepository.class);
        when(repo.findByRegistrationId("oidc")).thenReturn(null);
        ObjectProvider<ClientRegistrationRepository> provider = mock(ObjectProvider.class);
        when(provider.getIfAvailable()).thenReturn(repo);
        OAuthSecurityStartupGuard guard = new OAuthSecurityStartupGuard(props, provider);
        assertThatThrownBy(() -> guard.run(new DefaultApplicationArguments()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("registration-id");
    }

    @Test
    void passesWhenRegistrationPresent() {
        AppProperties props = new AppProperties();
        props.getSecurity().setMode("multi");
        props.getSecurity().getOauth().setEnabled(true);
        props.getSecurity().getOauth().setRegistrationId("oidc");
        ClientRegistration registration = ClientRegistration.withRegistrationId("oidc")
                .clientId("c")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .authorizationUri("https://idp.example/auth")
                .tokenUri("https://idp.example/token")
                .build();
        ClientRegistrationRepository repo = mock(ClientRegistrationRepository.class);
        when(repo.findByRegistrationId("oidc")).thenReturn(registration);
        ObjectProvider<ClientRegistrationRepository> provider = mock(ObjectProvider.class);
        when(provider.getIfAvailable()).thenReturn(repo);
        OAuthSecurityStartupGuard guard = new OAuthSecurityStartupGuard(props, provider);
        assertThatCode(() -> guard.run(new DefaultApplicationArguments()))
                .doesNotThrowAnyException();
    }
}
