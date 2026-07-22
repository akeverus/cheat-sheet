package com.cheatsheet.quiz.api.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class CurrentUserAccessorTest {

    private final CurrentUserAccessor accessor = new CurrentUserAccessor();

    @Test
    void ownershipKeyDefaultsToLocalWhenAnonymous() {
        SecurityContextHolder.clearContext();
        assertThat(accessor.ownershipKey()).isEqualTo("local");
        assertThat(accessor.currentUsername()).isEmpty();
    }

    @Test
    void ownershipKeyUsesAuthenticatedUsername() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("alice", "n/a", List.of()));
        try {
            assertThat(accessor.currentUsername()).contains("alice");
            assertThat(accessor.ownershipKey()).isEqualTo("alice");
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    void ownershipKeyUsesOauthRegistrationAndSubject() {
        OAuth2User principal = new DefaultOAuth2User(
                List.of(),
                Map.of("sub", "user-42", "name", "Ada"),
                "sub");
        SecurityContextHolder.getContext().setAuthentication(
                new OAuth2AuthenticationToken(principal, List.of(), "oidc"));
        try {
            assertThat(accessor.currentUsername()).contains("user-42");
            assertThat(accessor.ownershipKey()).isEqualTo("oauth:oidc:user-42");
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    void ownershipKeySanitizesUnsafeOauthSubject() {
        OAuth2User principal = new DefaultOAuth2User(
                List.of(),
                Map.of("sub", "user/42|admin", "name", "Ada"),
                "sub");
        SecurityContextHolder.getContext().setAuthentication(
                new OAuth2AuthenticationToken(principal, List.of(), "oidc"));
        try {
            assertThat(accessor.ownershipKey()).isEqualTo("oauth:oidc:user_42_admin");
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
