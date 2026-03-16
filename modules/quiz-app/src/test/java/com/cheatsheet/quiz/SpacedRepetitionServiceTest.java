package com.cheatsheet.quiz;

import static org.assertj.core.api.Assertions.assertThat;

import com.cheatsheet.quiz.domain.ReviewResult;
import com.cheatsheet.quiz.domain.ReviewState;
import com.cheatsheet.quiz.feature.interview.service.review.SpacedRepetitionService;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

/**
 * Тесты алгоритма SM-2 ({@link SpacedRepetitionService}).
 */
class SpacedRepetitionServiceTest {

    @Test
    void updatesStateOnCorrectAnswer() {
        Clock fixed = Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneOffset.UTC);
        SpacedRepetitionService service = new SpacedRepetitionService(fixed);
        ReviewState state = new ReviewState(1L, 0, 0, 2.5, 0L, ReviewResult.NEW, 0, 0);

        ReviewState updated = service.applyAnswer(state, true);

        assertThat(updated.repetitions()).isEqualTo(1);
        assertThat(updated.intervalDays()).isEqualTo(1);
        assertThat(updated.easeFactor()).isGreaterThanOrEqualTo(1.3);
        assertThat(updated.nextReviewAt()).isPositive();
        assertThat(updated.lastResult()).isEqualTo(ReviewResult.CORRECT);
        assertThat(updated.correctCount()).isEqualTo(1);
    }

    @Test
    void resetsIntervalOnWrongAnswer() {
        Clock fixed = Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneOffset.UTC);
        SpacedRepetitionService service = new SpacedRepetitionService(fixed);
        ReviewState state = new ReviewState(1L, 3, 10, 2.5, 0L, ReviewResult.CORRECT, 3, 0);

        ReviewState updated = service.applyAnswer(state, false);

        assertThat(updated.repetitions()).isZero();
        assertThat(updated.intervalDays()).isEqualTo(1);
        assertThat(updated.lastResult()).isEqualTo(ReviewResult.WRONG);
        assertThat(updated.wrongCount()).isEqualTo(1);
    }
}
