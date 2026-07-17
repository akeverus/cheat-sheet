package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.domain.FsrsState;
import com.cheatsheet.quiz.domain.ReviewReason;
import com.cheatsheet.quiz.domain.ReviewResult;
import com.cheatsheet.quiz.domain.ReviewState;
import com.cheatsheet.quiz.feature.interview.service.review.ReviewReasonService;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты классификатора explainable-причины повторения. Детерминизм — за счёт
 * фиксированного {@link Clock} (now = 2026-03-10T00:00:00Z, зона UTC).
 */
class ReviewReasonServiceTest {

    private static final long NOW = Instant.parse("2026-03-10T00:00:00Z").getEpochSecond();
    private static final long DAY = 86_400L;

    private final ReviewReasonService service =
            new ReviewReasonService(Clock.fixed(Instant.ofEpochSecond(NOW), ZoneOffset.UTC));

    private static ReviewState stateWith(ReviewResult lastResult, long nextReviewAt) {
        return new ReviewState(1L, 1, 3, 2.5, nextReviewAt, lastResult, 2, 1);
    }

    private static Optional<FsrsState> fsrsWithLastReviewed(Long lastReviewedAt) {
        return Optional.of(new FsrsState(1L, 5.0, 6.0, 0, "fsrs-4.5-default", lastReviewedAt));
    }

    @Test
    void recentLapseWithDateFromLastReviewed() {
        long lastReviewed = Instant.parse("2026-03-05T12:00:00Z").getEpochSecond();
        Optional<ReviewReason> reason = service.classify(
                Optional.of(stateWith(ReviewResult.WRONG, NOW)),
                fsrsWithLastReviewed(lastReviewed),
                90.0);

        assertThat(reason).isPresent();
        assertThat(reason.get().type()).isEqualTo(ReviewReason.Type.RECENT_LAPSE);
        assertThat(reason.get().message()).isEqualTo("Последняя ошибка: 05.03");
    }

    @Test
    void recentLapseUnknownWithoutDateFallsBackToGenericMessage() {
        Optional<ReviewReason> reason = service.classify(
                Optional.of(stateWith(ReviewResult.UNKNOWN, NOW)),
                Optional.empty(),
                90.0);

        assertThat(reason).isPresent();
        assertThat(reason.get().type()).isEqualTo(ReviewReason.Type.RECENT_LAPSE);
        assertThat(reason.get().message()).isEqualTo("Последняя попытка была неверной");
    }

    @Test
    void overdueUsesRussianDayPluralization() {
        assertThat(overdueMessage(1)).isEqualTo("Повторение просрочено на 1 день");
        assertThat(overdueMessage(3)).isEqualTo("Повторение просрочено на 3 дня");
        assertThat(overdueMessage(5)).isEqualTo("Повторение просрочено на 5 дней");
        assertThat(overdueMessage(11)).isEqualTo("Повторение просрочено на 11 дней");
        assertThat(overdueMessage(21)).isEqualTo("Повторение просрочено на 21 день");
    }

    private String overdueMessage(int days) {
        Optional<ReviewReason> reason = service.classify(
                // last_result CORRECT — не лапс, поэтому побеждает просрочка.
                Optional.of(stateWith(ReviewResult.CORRECT, NOW - days * DAY)),
                Optional.empty(),
                90.0);
        assertThat(reason).isPresent();
        assertThat(reason.get().type()).isEqualTo(ReviewReason.Type.OVERDUE);
        return reason.get().message();
    }

    @Test
    void notOverdueWhenNextReviewInFutureOrLessThanOneDay() {
        // next_review через 12 часов от now — не просрочено, слабого навыка нет.
        Optional<ReviewReason> reason = service.classify(
                Optional.of(stateWith(ReviewResult.CORRECT, NOW + DAY / 2)),
                Optional.empty(),
                90.0);
        assertThat(reason).isEmpty();
    }

    @Test
    void weakSkillWhenTopicAccuracyBelowThreshold() {
        Optional<ReviewReason> reason = service.classify(
                Optional.of(stateWith(ReviewResult.CORRECT, NOW)),
                Optional.empty(),
                40.0);

        assertThat(reason).isPresent();
        assertThat(reason.get().type()).isEqualTo(ReviewReason.Type.WEAK_SKILL);
        assertThat(reason.get().message()).isEqualTo("Слабый навык — точность по теме 40%");
    }

    @Test
    void weakSkillWorksEvenWithoutReviewState() {
        Optional<ReviewReason> reason = service.classify(Optional.empty(), Optional.empty(), 25.0);

        assertThat(reason).isPresent();
        assertThat(reason.get().type()).isEqualTo(ReviewReason.Type.WEAK_SKILL);
    }

    @Test
    void noReasonWhenAccuracyHealthyAndScheduleOnTime() {
        Optional<ReviewReason> reason = service.classify(
                Optional.of(stateWith(ReviewResult.CORRECT, NOW)),
                Optional.empty(),
                85.0);
        assertThat(reason).isEmpty();
    }

    @Test
    void noWeakSkillWhenTopicHasNoData() {
        // getTopicAccuracy возвращает -1 при отсутствии ответов — не «слабый навык».
        Optional<ReviewReason> reason = service.classify(
                Optional.of(stateWith(ReviewResult.CORRECT, NOW)),
                Optional.empty(),
                -1.0);
        assertThat(reason).isEmpty();
    }

    @Test
    void recentLapseTakesPriorityOverOverdue() {
        Optional<ReviewReason> reason = service.classify(
                Optional.of(stateWith(ReviewResult.WRONG, NOW - 10 * DAY)),
                Optional.empty(),
                10.0);
        assertThat(reason).isPresent();
        assertThat(reason.get().type()).isEqualTo(ReviewReason.Type.RECENT_LAPSE);
    }

    @Test
    void overdueTakesPriorityOverWeakSkill() {
        Optional<ReviewReason> reason = service.classify(
                Optional.of(stateWith(ReviewResult.CORRECT, NOW - 4 * DAY)),
                Optional.empty(),
                10.0);
        assertThat(reason).isPresent();
        assertThat(reason.get().type()).isEqualTo(ReviewReason.Type.OVERDUE);
    }
}
