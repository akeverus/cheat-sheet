package com.cheatsheet.quiz.api.security;

import com.cheatsheet.quiz.config.app.AppProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Maps IdP group claim values to Spring authorities ({@code ROLE_USER}, optional {@code ROLE_ADMIN}).
 */
public final class OrgIdpGroupAuthoritiesMapper {

    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    private final String groupsClaim;
    private final Set<String> adminGroups;

    public OrgIdpGroupAuthoritiesMapper(AppProperties.Oauth oauth) {
        this.groupsClaim = oauth.getGroupsClaim();
        this.adminGroups = normalizeSet(oauth.getAdminGroups());
    }

    public Collection<GrantedAuthority> map(OAuth2User user) {
        Set<GrantedAuthority> authorities = new LinkedHashSet<>();
        authorities.add(new SimpleGrantedAuthority(ROLE_USER));
        List<String> groups = extractGroups(user);
        for (String group : groups) {
            if (adminGroups.contains(group)) {
                authorities.add(new SimpleGrantedAuthority(ROLE_ADMIN));
                break;
            }
        }
        return List.copyOf(authorities);
    }

    List<String> extractGroups(OAuth2User user) {
        Object raw = null;
        if (user instanceof OidcUser oidc) {
            raw = oidc.getClaim(groupsClaim);
        }
        if (raw == null) {
            raw = user.getAttribute(groupsClaim);
        }
        if (raw == null) {
            Map<String, Object> attributes = user.getAttributes();
            if (attributes != null) {
                raw = attributes.get(groupsClaim);
            }
        }
        return normalizeGroups(raw);
    }

    static List<String> normalizeGroups(Object raw) {
        if (raw == null) {
            return List.of();
        }
        List<String> out = new ArrayList<>();
        if (raw instanceof String s) {
            for (String part : s.split("[,\\s]+")) {
                String trimmed = part.trim();
                if (!trimmed.isEmpty()) {
                    out.add(trimmed);
                }
            }
            return List.copyOf(out);
        }
        if (raw instanceof String[] arr) {
            for (String part : arr) {
                if (part != null && !part.isBlank()) {
                    out.add(part.trim());
                }
            }
            return List.copyOf(out);
        }
        if (raw instanceof Collection<?> coll) {
            for (Object item : coll) {
                if (item != null) {
                    String value = String.valueOf(item).trim();
                    if (!value.isEmpty()) {
                        out.add(value);
                    }
                }
            }
            return List.copyOf(out);
        }
        String single = String.valueOf(raw).trim();
        return single.isEmpty() ? List.of() : List.of(single);
    }

    private static Set<String> normalizeSet(List<String> values) {
        if (values == null || values.isEmpty()) {
            return Set.of();
        }
        Set<String> out = new LinkedHashSet<>();
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                out.add(value.trim());
            }
        }
        return Set.copyOf(out);
    }
}
