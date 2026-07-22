package com.cheatsheet.quiz.api.security;

import com.cheatsheet.quiz.config.app.AppProperties;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class OrgIdpGroupAuthoritiesMapperTest {

    @Test
    void mapsAdminGroupFromListClaim() {
        AppProperties.Oauth oauth = new AppProperties.Oauth();
        oauth.setGroupsClaim("groups");
        oauth.setAdminGroups(List.of("cheat-sheet-admins"));
        OrgIdpGroupAuthoritiesMapper mapper = new OrgIdpGroupAuthoritiesMapper(oauth);

        OAuth2User user = new DefaultOAuth2User(
                List.of(),
                Map.of("sub", "u1", "groups", List.of("learners", "cheat-sheet-admins")),
                "sub");

        Collection<? extends GrantedAuthority> authorities = mapper.map(user);
        assertThat(authorities).extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
    }

    @Test
    void userOnlyWhenGroupNotAdmin() {
        AppProperties.Oauth oauth = new AppProperties.Oauth();
        oauth.setAdminGroups(List.of("cheat-sheet-admins"));
        OrgIdpGroupAuthoritiesMapper mapper = new OrgIdpGroupAuthoritiesMapper(oauth);

        OAuth2User user = new DefaultOAuth2User(
                List.of(),
                Map.of("sub", "u1", "groups", List.of("learners")),
                "sub");

        assertThat(mapper.map(user)).extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_USER");
    }

    @Test
    void readsOidcClaimAndCommaSeparatedGroups() {
        AppProperties.Oauth oauth = new AppProperties.Oauth();
        oauth.setGroupsClaim("groups");
        oauth.setAdminGroups(List.of("ops"));
        OrgIdpGroupAuthoritiesMapper mapper = new OrgIdpGroupAuthoritiesMapper(oauth);

        OidcIdToken idToken = new OidcIdToken(
                "token",
                Instant.now(),
                Instant.now().plusSeconds(60),
                Map.of("sub", "oidc-1", "groups", "ops,learners"));
        DefaultOidcUser user = new DefaultOidcUser(List.of(), idToken);

        assertThat(mapper.map(user)).extracting(GrantedAuthority::getAuthority)
                .contains("ROLE_ADMIN");
    }
}
