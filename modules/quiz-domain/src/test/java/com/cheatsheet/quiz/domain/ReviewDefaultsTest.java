package com.cheatsheet.quiz.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты начального состояния {@link ReviewDefaults}.
 */
class ReviewDefaultsTest {

    @Test
    void initialStateHasCorrectDefaults() {
        ReviewState state = ReviewDefaults.initialState(42L, 1000L);

        assertThat(state.questionId()).isEqualTo(42L);
        assertThat(state.correctCount()).isZero();
        assertThat(state.wrongCount()).isZero();
        assertThat(state.easeFactor()).isEqualTo(ReviewDefaults.DEFAULT_EASE_FACTOR);
        assertThat(state.nextReviewAt()).isEqualTo(1000L);
        assertThat(state.lastResult()).isEqualTo(ReviewResult.NEW);
        assertThat(state.repetitions()).isZero();
        assertThat(state.intervalDays()).isZero();
    }

    @Test
    void minEaseFactorIsLessThanDefault() {
        assertThat(ReviewDefaults.MIN_EASE_FACTOR).isLessThan(ReviewDefaults.DEFAULT_EASE_FACTOR);
    }
}
