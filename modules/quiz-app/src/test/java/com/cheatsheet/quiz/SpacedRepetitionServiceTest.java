package com.cheatsheet.quiz;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

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

    @Test
    void applyUnknownLapsesAndMarksUnknown() {
        Clock fixed = Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneOffset.UTC);
        SpacedRepetitionService service = new SpacedRepetitionService(fixed);
        ReviewState state = new ReviewState(1L, 4, 20, 2.5, 0L, ReviewResult.CORRECT, 4, 1);

        ReviewState updated = service.applyUnknown(state);

        assertThat(updated.repetitions()).isZero();
        assertThat(updated.intervalDays()).isEqualTo(1);
        assertThat(updated.lastResult()).isEqualTo(ReviewResult.UNKNOWN);
        assertThat(updated.wrongCount()).isEqualTo(2);
        assertThat(updated.correctCount()).isEqualTo(4);
        assertThat(updated.nextReviewAt()).isPositive();
    }

    @Test
    void applyUnknownPenalisesEaseHarderThanWrong() {
        Clock fixed = Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneOffset.UTC);
        SpacedRepetitionService service = new SpacedRepetitionService(fixed);
        ReviewState state = new ReviewState(1L, 3, 10, 2.5, 0L, ReviewResult.CORRECT, 3, 0);

        ReviewState afterWrong = service.applyAnswer(state, false);
        ReviewState afterUnknown = service.applyUnknown(state);

        assertThat(afterUnknown.easeFactor()).isLessThan(afterWrong.easeFactor());
    }

    @Test
    void applyUnknownNeverDropsEaseBelowFloor() {
        Clock fixed = Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneOffset.UTC);
        SpacedRepetitionService service = new SpacedRepetitionService(fixed);
        ReviewState state = new ReviewState(1L, 0, 1, 1.3, 0L, ReviewResult.WRONG, 0, 5);

        ReviewState updated = service.applyUnknown(state);

        assertThat(updated.easeFactor()).isGreaterThanOrEqualTo(1.3);
    }

    @Test
    void intervalProgressesOneThenSixThenEaseMultiplied() {
        // Ядро SM-2: 1-й правильный → 1 день, 2-й → 6 дней, далее round(interval·ease).
        Clock fixed = Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneOffset.UTC);
        SpacedRepetitionService service = new SpacedRepetitionService(fixed);

        ReviewState s1 = service.applyAnswer(
                new ReviewState(1L, 0, 0, 2.5, 0L, ReviewResult.NEW, 0, 0), true);
        assertThat(s1.repetitions()).isEqualTo(1);
        assertThat(s1.intervalDays()).isEqualTo(1);

        ReviewState s2 = service.applyAnswer(s1, true);
        assertThat(s2.repetitions()).isEqualTo(2);
        assertThat(s2.intervalDays()).isEqualTo(6);

        ReviewState s3 = service.applyAnswer(s2, true);
        assertThat(s3.repetitions()).isEqualTo(3);
        // ease на входе 3-го вызова = 2.7 (2.5 +0.1 +0.1) → round(6·2.7) = 16.
        assertThat(s3.intervalDays()).isEqualTo(16);
    }

    @Test
    void computeEaseDeltaMatchesSm2Formula() {
        // 0.1 − (5−grade)·(0.08 + (5−grade)·0.02): точные значения формулы SM-2.
        assertThat(SpacedRepetitionService.computeEaseDelta(5)).isCloseTo(0.1, within(1e-9));
        assertThat(SpacedRepetitionService.computeEaseDelta(4)).isCloseTo(0.0, within(1e-9));
        assertThat(SpacedRepetitionService.computeEaseDelta(3)).isCloseTo(-0.14, within(1e-9));
        assertThat(SpacedRepetitionService.computeEaseDelta(2)).isCloseTo(-0.32, within(1e-9));
        assertThat(SpacedRepetitionService.computeEaseDelta(0)).isCloseTo(-0.8, within(1e-9));
    }

    @Test
    void correctAnswerGradeIsClampedToAtLeastThree() {
        // При правильном ответе грейд ниже порога поднимается до 3 (сброса нет).
        Clock fixed = Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneOffset.UTC);
        SpacedRepetitionService service = new SpacedRepetitionService(fixed);
        ReviewState base = new ReviewState(1L, 2, 6, 2.5, 0L, ReviewResult.CORRECT, 2, 0);

        ReviewState grade1 = service.applyAnswer(base, true, 1);
        ReviewState grade3 = service.applyAnswer(base, true, 3);

        assertThat(grade1.easeFactor()).isEqualTo(grade3.easeFactor());
        assertThat(grade1.repetitions()).isEqualTo(3); // правильный ответ — не сброс
    }

    @Test
    void higherConfidenceGradeGivesHigherEase() {
        Clock fixed = Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneOffset.UTC);
        SpacedRepetitionService service = new SpacedRepetitionService(fixed);
        ReviewState base = new ReviewState(1L, 2, 6, 2.5, 0L, ReviewResult.CORRECT, 2, 0);

        double easeGuessed = service.applyAnswer(base, true, 3).easeFactor();
        double easeKnew = service.applyAnswer(base, true, 5).easeFactor();

        assertThat(easeKnew).isGreaterThan(easeGuessed);
    }

    @Test
    void repeatedWrongNeverDropsEaseBelowFloor() {
        Clock fixed = Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneOffset.UTC);
        SpacedRepetitionService service = new SpacedRepetitionService(fixed);
        ReviewState state = new ReviewState(1L, 0, 1, 1.5, 0L, ReviewResult.WRONG, 0, 3);

        for (int i = 0; i < 5; i++) {
            state = service.applyAnswer(state, false);
            assertThat(state.easeFactor()).isGreaterThanOrEqualTo(1.3);
        }
    }
}
