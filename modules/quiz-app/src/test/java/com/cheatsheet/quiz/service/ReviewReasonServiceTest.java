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
        assertThat(reason.get().message())
                .isEqualTo("Недавно была ошибка (05.03) — повторим, пока не закрепится");
    }

    @Test
    void recentLapseUnknownWithoutDateFallsBackToGenericMessage() {
        Optional<ReviewReason> reason = service.classify(
                Optional.of(stateWith(ReviewResult.UNKNOWN, NOW)),
                Optional.empty(),
                90.0);

        assertThat(reason).isPresent();
        assertThat(reason.get().type()).isEqualTo(ReviewReason.Type.RECENT_LAPSE);
        assertThat(reason.get().message()).isEqualTo("Последний ответ был неверным — повторим сейчас");
    }

    @Test
    void overdueUsesRussianDayPluralization() {
        assertThat(overdueMessage(1)).isEqualTo("Повторение просрочено на 1 день — интервал уже вышел");
        assertThat(overdueMessage(3)).isEqualTo("Повторение просрочено на 3 дня — интервал уже вышел");
        assertThat(overdueMessage(5)).isEqualTo("Повторение просрочено на 5 дней — интервал уже вышел");
        assertThat(overdueMessage(11)).isEqualTo("Повторение просрочено на 11 дней — интервал уже вышел");
        assertThat(overdueMessage(21)).isEqualTo("Повторение просрочено на 21 день — интервал уже вышел");
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
        assertThat(reason.get().message())
                .isEqualTo("Тема пока слабая (точность 40%) — подтянем в этой сессии");
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
    void bookmarkWhenImportantAndNoStrongerReason() {
        Optional<ReviewReason> reason = service.classify(
                Optional.of(stateWith(ReviewResult.CORRECT, NOW)),
                Optional.empty(),
                90.0,
                true);

        assertThat(reason).isPresent();
        assertThat(reason.get().type()).isEqualTo(ReviewReason.Type.BOOKMARK);
        assertThat(reason.get().message())
                .isEqualTo("В избранном — ты сам отметил этот вопрос для повтора");
    }

    @Test
    void newCardWhenNoReviewStateAndHealthyAccuracy() {
        Optional<ReviewReason> reason = service.classify(Optional.empty(), Optional.empty(), 90.0);

        assertThat(reason).isPresent();
        assertThat(reason.get().type()).isEqualTo(ReviewReason.Type.NEW);
        assertThat(reason.get().message())
                .isEqualTo("Новый вопрос — ещё не отвечал, самое время познакомиться");
    }

    @Test
    void weakSkillTakesPriorityOverNew() {
        Optional<ReviewReason> reason = service.classify(Optional.empty(), Optional.empty(), 40.0);

        assertThat(reason).isPresent();
        assertThat(reason.get().type()).isEqualTo(ReviewReason.Type.WEAK_SKILL);
    }

    @Test
    void examModeReturnsExamReason() {
        Optional<ReviewReason> reason = service.classify(
                Optional.of(stateWith(ReviewResult.CORRECT, NOW)),
                Optional.empty(),
                90.0,
                false,
                true);

        assertThat(reason).isPresent();
        assertThat(reason.get().type()).isEqualTo(ReviewReason.Type.EXAM);
        assertThat(reason.get().message()).isEqualTo("Часть экзаменационной сессии");
    }

    @Test
    void examModeTakesPriorityOverLapse() {
        Optional<ReviewReason> reason = service.classify(
                Optional.of(stateWith(ReviewResult.WRONG, NOW)),
                Optional.empty(),
                10.0,
                true,
                true);

        assertThat(reason).isPresent();
        assertThat(reason.get().type()).isEqualTo(ReviewReason.Type.EXAM);
    }
}
