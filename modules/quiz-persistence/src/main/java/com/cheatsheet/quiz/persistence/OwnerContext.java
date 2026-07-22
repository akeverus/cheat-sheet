package com.cheatsheet.quiz.persistence;

import java.util.regex.Pattern;

/**
 * Request-scoped owner for Hybrid multi-user progress tables.
 * Default {@code local} matches {@code CurrentUserAccessor.ownershipKey()} in local mode.
 */
public final class OwnerContext {

    /** Usernames plus OAuth keys {@code oauth:{registrationId}:{subject}}. */
    private static final Pattern SAFE = Pattern.compile("[a-zA-Z0-9._@+:-]{1,256}");
    private static final ThreadLocal<String> HOLDER = new ThreadLocal<>();

    private OwnerContext() {
    }

    public static void set(String ownerKey) {
        HOLDER.set(sanitize(ownerKey));
    }

    public static String current() {
        String value = HOLDER.get();
        return value == null || value.isBlank() ? "local" : value;
    }

    public static void clear() {
        HOLDER.remove();
    }

    /** True when {@code ownerKey} is a non-blank SAFE ownership key. */
    public static boolean isSafe(String ownerKey) {
        if (ownerKey == null || ownerKey.isBlank()) {
            return false;
        }
        return SAFE.matcher(ownerKey.trim()).matches();
    }

    static String sanitize(String ownerKey) {
        if (ownerKey == null || ownerKey.isBlank()) {
            return "local";
        }
        String trimmed = ownerKey.trim();
        if (!SAFE.matcher(trimmed).matches()) {
            // Never collapse bad keys to shared "local" (would mix tenants).
            throw new IllegalArgumentException("Invalid owner_key: rejected by SAFE pattern");
        }
        return trimmed;
    }
}
