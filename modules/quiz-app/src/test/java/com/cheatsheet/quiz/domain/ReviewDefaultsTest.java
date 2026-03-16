package com.cheatsheet.quiz.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Тесты для {@link ReviewDefaults}.
 */
class ReviewDefaultsTest {

    @Test
    void initialStateHasCorrectDefaults() {
        ReviewState state = ReviewDefaults.initialState(42L, 1000L);

        assertThat(state.questionId()).isEqualTo(42L);
        assertThat(state.repetitions()).isZero();
        assertThat(state.intervalDays()).isZero();
        assertThat(state.easeFactor()).isEqualTo(2.5);
        assertThat(state.nextReviewAt()).isEqualTo(1000L);
        // Теперь lastResult — это enum ReviewResult
        assertThat(state.lastResult()).isEqualTo(ReviewResult.NEW);
        assertThat(state.correctCount()).isZero();
        assertThat(state.wrongCount()).isZero();
    }
}
