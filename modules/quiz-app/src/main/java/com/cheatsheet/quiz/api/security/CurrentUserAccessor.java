package com.cheatsheet.quiz.api.security;

import com.cheatsheet.quiz.persistence.OwnerContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Ownership hook for Hybrid multi mode. Local mode → {@code local}.
 * Form/Basic → username. OAuth2 → {@code oauth:{registrationId}:{subject}}
 * with SAFE segments so {@link OwnerContext} never rejects the key mid-request.
 */
@Component
public class CurrentUserAccessor {

    private static final int OWNER_KEY_MAX = 256;

    public Optional<String> currentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return Optional.empty();
        }
        String name = auth.getName();
        if (name == null || name.isBlank() || "anonymousUser".equals(name)) {
            return Optional.empty();
        }
        return Optional.of(name);
    }

    /** Session/attempt scoping key: username / oauth subject in multi, else {@code local}. */
    public String ownershipKey() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof OAuth2AuthenticationToken oauth) {
            String subject = oauth.getName();
            if (subject == null || subject.isBlank() || "anonymousUser".equals(subject)) {
                return "local";
            }
            String reg = safeSegment(oauth.getAuthorizedClientRegistrationId(), "oidc");
            String sub = safeSegment(subject, "user");
            return truncate("oauth:" + reg + ":" + sub, OWNER_KEY_MAX);
        }
        return currentUsername()
                .map(name -> {
                    String safe = safeSegment(name, "user");
                    return OwnerContext.isSafe(safe) ? safe : "local";
                })
                .orElse("local");
    }

    static String safeSegment(String raw, String fallback) {
        if (raw == null || raw.isBlank()) {
            return fallback;
        }
        String cleaned = raw.trim().replaceAll("[^a-zA-Z0-9._@+-]", "_");
        if (cleaned.isBlank()) {
            return fallback;
        }
        return cleaned;
    }

    private static String truncate(String value, int max) {
        return value.length() <= max ? value : value.substring(0, max);
    }
}
