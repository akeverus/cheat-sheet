package com.cheatsheet.quiz.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * OAuth2 login skeleton: opt-in client registration + login page link + authorize redirect.
 */
@SpringBootTest(properties = {
        "app.admin-token=test-admin-token",
        "app.security.mode=multi",
        "app.security.multi.username=trainer",
        "app.security.multi.password=trainer",
        "app.security.oauth.enabled=true",
        "app.security.oauth.registration-id=oidc",
        "app.security.oauth.display-name=Org SSO",
        "app.security.oauth.groups-claim=groups",
        "app.security.oauth.admin-groups[0]=cheat-sheet-admins",
        "spring.security.oauth2.client.registration.oidc.client-id=test-client",
        "spring.security.oauth2.client.registration.oidc.client-secret=test-secret",
        "spring.security.oauth2.client.registration.oidc.authorization-grant-type=authorization_code",
        "spring.security.oauth2.client.registration.oidc.redirect-uri={baseUrl}/login/oauth2/code/{registrationId}",
        "spring.security.oauth2.client.registration.oidc.scope=openid,profile",
        "spring.security.oauth2.client.registration.oidc.provider=oidc",
        "spring.security.oauth2.client.provider.oidc.authorization-uri=https://idp.example/authorize",
        "spring.security.oauth2.client.provider.oidc.token-uri=https://idp.example/token",
        "spring.security.oauth2.client.provider.oidc.user-info-uri=https://idp.example/userinfo",
        "spring.security.oauth2.client.provider.oidc.user-name-attribute=sub"
})
@AutoConfigureMockMvc
class OAuth2SecurityWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void loginPageShowsOauthButton() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/login"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String body = result.getResponse().getContentAsString();
                    assertThat(body).contains("/oauth2/authorization/oidc");
                    assertThat(body).contains("Войти через Org SSO");
                });
    }

    @Test
    void oauthAuthorizationEndpointRedirectsToIdp() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/oauth2/authorization/oidc"))
                .andExpect(status().is3xxRedirection())
                .andExpect(result -> assertThat(result.getResponse().getRedirectedUrl())
                        .startsWith("https://idp.example/authorize"));
    }

    @Test
    void exportAllowedForOidcAdminRoleWithoutAdminToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/export").param("format", "json")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
                                .oidcLogin()
                                .authorities(new org.springframework.security.core.authority.SimpleGrantedAuthority(
                                        "ROLE_ADMIN"))))
                .andExpect(result -> assertThat(result.getResponse().getStatus())
                        .isNotIn(401, 403));
    }

    @Test
    void exportForbiddenForOidcUserWithoutAdminRole() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/export").param("format", "json")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
                                .oidcLogin()
                                .authorities(new org.springframework.security.core.authority.SimpleGrantedAuthority(
                                        "ROLE_USER"))))
                .andExpect(status().isForbidden());
    }
}
