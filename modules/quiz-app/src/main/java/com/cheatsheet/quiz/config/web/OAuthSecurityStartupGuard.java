package com.cheatsheet.quiz.config.web;

import com.cheatsheet.quiz.config.app.AppProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Component;

/**
 * Fail-fast when OAuth is enabled without multi mode or without a matching client registration.
 */
@Component
@Order(20)
public class OAuthSecurityStartupGuard implements ApplicationRunner {

    private final AppProperties appProperties;
    private final ObjectProvider<ClientRegistrationRepository> clientRegistrations;

    public OAuthSecurityStartupGuard(
            AppProperties appProperties,
            ObjectProvider<ClientRegistrationRepository> clientRegistrations
    ) {
        this.appProperties = appProperties;
        this.clientRegistrations = clientRegistrations;
    }

    @Override
    public void run(ApplicationArguments args) {
        AppProperties.Oauth oauth = appProperties.getSecurity().getOauth();
        if (!oauth.isEnabled()) {
            return;
        }
        if (!"multi".equalsIgnoreCase(appProperties.getSecurity().getMode())) {
            throw new IllegalStateException(
                    "app.security.oauth.enabled=true requires app.security.mode=multi");
        }
        ClientRegistrationRepository repo = clientRegistrations.getIfAvailable();
        if (repo == null) {
            throw new IllegalStateException(
                    "app.security.oauth.enabled=true but spring.security.oauth2.client.registration.* "
                            + "is missing (no ClientRegistrationRepository)");
        }
        String registrationId = oauth.getRegistrationId();
        ClientRegistration registration = repo.findByRegistrationId(registrationId);
        if (registration == null) {
            throw new IllegalStateException(
                    "app.security.oauth.registration-id='" + registrationId
                            + "' not found in spring.security.oauth2.client.registration");
        }
    }
}
