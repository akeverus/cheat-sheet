package com.cheatsheet.quiz.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты парсинга {@link InterviewMode#fromString(String)}.
 */
class InterviewModeTest {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t"})
    void fromStringReturnsTrainingForNullOrBlank(String input) {
        assertThat(InterviewMode.fromString(input)).isEqualTo(InterviewMode.TRAINING);
    }

    @Test
    void fromStringParsesValidValues() {
        assertThat(InterviewMode.fromString("TRAINING")).isEqualTo(InterviewMode.TRAINING);
        assertThat(InterviewMode.fromString("exam")).isEqualTo(InterviewMode.EXAM);
        assertThat(InterviewMode.fromString("Marathon")).isEqualTo(InterviewMode.MARATHON);
    }

    @Test
    void fromStringFallsBackToTrainingForInvalidValue() {
        assertThat(InterviewMode.fromString("unknown")).isEqualTo(InterviewMode.TRAINING);
        assertThat(InterviewMode.fromString("xyz123")).isEqualTo(InterviewMode.TRAINING);
    }
}
