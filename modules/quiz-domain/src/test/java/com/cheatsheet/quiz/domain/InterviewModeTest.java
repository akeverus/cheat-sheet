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
        assertThat(InterviewMode.fromString("TYPED_RECALL")).isEqualTo(InterviewMode.TYPED_RECALL);
        assertThat(InterviewMode.fromString("interview")).isEqualTo(InterviewMode.INTERVIEW);
        assertThat(InterviewMode.fromString("code_challenge")).isEqualTo(InterviewMode.CODE_CHALLENGE);
    }

    @Test
    void recallModeHelpers() {
        assertThat(InterviewMode.FLASHCARD.isRecallMode()).isTrue();
        assertThat(InterviewMode.TYPED_RECALL.isRecallMode()).isTrue();
        assertThat(InterviewMode.INTERVIEW.isRecallMode()).isTrue();
        assertThat(InterviewMode.CODE_CHALLENGE.isRecallMode()).isTrue();
        assertThat(InterviewMode.TRAINING.isRecallMode()).isFalse();
        assertThat(InterviewMode.EXAM.isRecallMode()).isFalse();

        assertThat(InterviewMode.TYPED_RECALL.usesTypedInput()).isTrue();
        assertThat(InterviewMode.INTERVIEW.usesTypedInput()).isTrue();
        assertThat(InterviewMode.CODE_CHALLENGE.usesTypedInput()).isTrue();
        assertThat(InterviewMode.FLASHCARD.usesTypedInput()).isFalse();

        assertThat(InterviewMode.CODE_CHALLENGE.usesCodeEditor()).isTrue();
        assertThat(InterviewMode.TYPED_RECALL.usesCodeEditor()).isFalse();
    }

    @Test
    void fromStringFallsBackToTrainingForInvalidValue() {
        assertThat(InterviewMode.fromString("unknown")).isEqualTo(InterviewMode.TRAINING);
        assertThat(InterviewMode.fromString("xyz123")).isEqualTo(InterviewMode.TRAINING);
    }
}
