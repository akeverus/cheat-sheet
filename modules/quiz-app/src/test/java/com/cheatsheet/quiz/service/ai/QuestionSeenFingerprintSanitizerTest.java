package com.cheatsheet.quiz.service.ai;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class QuestionSeenFingerprintSanitizerTest {

    private final QuestionSeenFingerprintSanitizer sanitizer = new QuestionSeenFingerprintSanitizer();

    @Test
    void sanitizeReturnsEmptyForNullOrEmptyInput() {
        assertThat(sanitizer.sanitize(null)).isEmpty();
        assertThat(sanitizer.sanitize(Set.of())).isEmpty();
    }

    @Test
    void sanitizeTrimsAndRemovesInvalidValues() {
        Set<String> raw = new HashSet<>();
        raw.add(" fp-1 ");
        raw.add("fp-1");
        raw.add(" ");
        raw.add(null);

        Set<String> sanitized = sanitizer.sanitize(raw);

        assertThat(sanitized).containsExactly("fp-1");
    }
}
