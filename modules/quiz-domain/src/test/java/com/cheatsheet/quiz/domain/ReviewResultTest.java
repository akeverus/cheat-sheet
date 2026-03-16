package com.cheatsheet.quiz.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Тесты парсинга {@link ReviewResult#fromString(String)}.
 */
class ReviewResultTest {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t"})
    void fromStringReturnsNewForNullOrBlank(String input) {
        assertThat(ReviewResult.fromString(input)).isEqualTo(ReviewResult.NEW);
    }

    @Test
    void fromStringParsesValidValues() {
        assertThat(ReviewResult.fromString("NEW")).isEqualTo(ReviewResult.NEW);
        assertThat(ReviewResult.fromString("correct")).isEqualTo(ReviewResult.CORRECT);
        assertThat(ReviewResult.fromString("Wrong")).isEqualTo(ReviewResult.WRONG);
        assertThat(ReviewResult.fromString("RESET")).isEqualTo(ReviewResult.RESET);
    }

    @Test
    void fromStringThrowsForInvalidValue() {
        assertThatThrownBy(() -> ReviewResult.fromString("invalid"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Неизвестный ReviewResult");
    }
}
