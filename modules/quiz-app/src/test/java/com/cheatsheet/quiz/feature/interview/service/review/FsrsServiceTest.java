package com.cheatsheet.quiz.feature.interview.service.review;

import com.cheatsheet.quiz.domain.FsrsRating;
import com.cheatsheet.quiz.domain.FsrsState;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты {@link FsrsService}: детерминизм и монотонные свойства FSRS-4.5,
 * не зависящие от конкретных весов.
 */
class FsrsServiceTest {

    private static final long NOW = 1_800_000_000L; // фикс. epoch
    private static final long DAY = 86_400L;

    private final FsrsService fsrs = new FsrsService();

    private static FsrsState fresh() {
        return FsrsState.builder().questionId(1L).lapses(0).build();
    }

    private static FsrsState established(double stability, double difficulty, int lapses, long lastReviewedAt) {
        return FsrsState.builder()
                .questionId(1L)
                .stability(stability)
                .difficulty(difficulty)
                .lapses(lapses)
                .lastReviewedAt(lastReviewedAt)
                .build();
    }

    @Test
    void firstReviewInitialStabilityOrderedByRating() {
        double again = fsrs.schedule(fresh(), FsrsRating.AGAIN, NOW).stability();
        double hard = fsrs.schedule(fresh(), FsrsRating.HARD, NOW).stability();
        double good = fsrs.schedule(fresh(), FsrsRating.GOOD, NOW).stability();
        double easy = fsrs.schedule(fresh(), FsrsRating.EASY, NOW).stability();

        assertThat(again).isLessThan(hard);
        assertThat(hard).isLessThan(good);
        assertThat(good).isLessThan(easy);
    }

    @Test
    void scheduleIsDeterministic() {
        FsrsService.Result a = fsrs.schedule(established(10.0, 5.0, 1, NOW - 10 * DAY), FsrsRating.GOOD, NOW);
        FsrsService.Result b = fsrs.schedule(established(10.0, 5.0, 1, NOW - 10 * DAY), FsrsRating.GOOD, NOW);

        assertThat(a).isEqualTo(b);
    }

    @Test
    void intervalApproximatesStabilityAtDefaultRetention() {
        // При retention 0.9 интервал ≈ округлённая стабильность.
        FsrsService.Result easy = fsrs.schedule(fresh(), FsrsRating.EASY, NOW);
        assertThat(easy.intervalDays()).isEqualTo((int) Math.round(easy.stability()));
        assertThat(easy.nextReviewAt()).isEqualTo(NOW + (long) easy.intervalDays() * DAY);
    }

    @Test
    void successfulRecallIncreasesStability() {
        FsrsState state = established(10.0, 5.0, 0, NOW - 10 * DAY);
        FsrsService.Result result = fsrs.schedule(state, FsrsRating.GOOD, NOW);

        assertThat(result.stability()).isGreaterThan(10.0);
        assertThat(result.lapses()).isZero();
    }

    @Test
    void forgettingReducesStabilityAndIncrementsLapses() {
        FsrsState state = established(30.0, 5.0, 1, NOW - 30 * DAY);
        FsrsService.Result result = fsrs.schedule(state, FsrsRating.AGAIN, NOW);

        assertThat(result.stability()).isLessThan(30.0);
        assertThat(result.lapses()).isEqualTo(2);
    }

    @Test
    void difficultyRisesOnAgainAndFallsOnEasy() {
        FsrsState state = established(10.0, 5.0, 0, NOW - 10 * DAY);
        double harder = fsrs.schedule(state, FsrsRating.AGAIN, NOW).difficulty();
        double easier = fsrs.schedule(state, FsrsRating.EASY, NOW).difficulty();

        assertThat(harder).isGreaterThan(5.0);
        assertThat(easier).isLessThan(5.0);
    }

    @Test
    void outputsStayWithinBounds() {
        FsrsService.Result result = fsrs.schedule(fresh(), FsrsRating.EASY, NOW);
        assertThat(result.stability()).isGreaterThanOrEqualTo(0.1);
        assertThat(result.difficulty()).isBetween(1.0, 10.0);
        assertThat(result.intervalDays()).isBetween(1, 36500);
    }

    @Test
    void seedFromSm2MapsEaseToDifficultyAndIntervalToStability() {
        FsrsService.Seed hard = fsrs.seedFromSm2(3, 1.3);
        FsrsService.Seed easy = fsrs.seedFromSm2(3, 2.5);

        assertThat(hard.stability()).isEqualTo(3.0);
        assertThat(easy.stability()).isEqualTo(3.0);
        // Низкий ease → высокая сложность; высокий ease → низкая.
        assertThat(hard.difficulty()).isGreaterThan(easy.difficulty());
        assertThat(hard.difficulty()).isBetween(1.0, 10.0);
        assertThat(easy.difficulty()).isBetween(1.0, 10.0);
    }
}
