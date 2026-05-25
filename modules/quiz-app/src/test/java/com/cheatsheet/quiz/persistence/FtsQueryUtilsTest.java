package com.cheatsheet.quiz.persistence;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FtsQueryUtilsTest {

    @Test
    void normalizeReturnsTrimmedValue() {
        assertThat(FtsQueryUtils.normalize("  java fts  ")).isEqualTo("java fts");
        assertThat(FtsQueryUtils.normalize("")).isEmpty();
        assertThat(FtsQueryUtils.normalize(null)).isEmpty();
    }

    @Test
    void sanitizeForPostgresRemovesUnsafeCharacters() {
        String sanitized = FtsQueryUtils.sanitizeForPostgres("john's \\query");

        assertThat(sanitized).doesNotContain("'");
        assertThat(sanitized).doesNotContain("\\");
        assertThat(sanitized).contains("john");
        assertThat(sanitized).contains("query");
    }
}
