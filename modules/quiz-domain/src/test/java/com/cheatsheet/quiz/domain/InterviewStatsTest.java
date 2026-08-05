package com.cheatsheet.quiz.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

/**
 * Тесты {@link InterviewStats} — валидации и view-адаптера {@link InterviewStats#from}.
 * Ключевое: {@code from} должен маппить каждое поле {@link LearningMetrics} в правильное
 * поле InterviewStats (cardsTotal→total, dueNow→due, dueFresh→fresh, cardsMastered→learned,
 * attemptsCorrect→correct, attemptsIncorrect→wrong) — различающиеся значения ловят swap.
 */
class InterviewStatsTest {

    @Test
    void fromNullMetricsYieldsAllZero() {
        InterviewStats stats = InterviewStats.from(null);
        assertThat(stats.total()).isZero();
        assertThat(stats.due()).isZero();
        assertThat(stats.fresh()).isZero();
        assertThat(stats.learned()).isZero();
        assertThat(stats.correct()).isZero();
        assertThat(stats.wrong()).isZero();
    }

    @Test
    void fromMapsEachMetricFieldToCorrectStatsField() {
        // Различающиеся значения → перестановка любых двух полей провалит тест.
        LearningMetrics metrics = LearningMetrics.builder()
                .cardsTotal(11).dueNow(22).dueFresh(6).cardsMastered(33)
                .attemptsCorrect(44).attemptsIncorrect(55)
                // прочие поля не участвуют в маппинге:
                .cardsSeen(7).attemptsTotal(99).overdue(3)
                .firstAttemptAccuracy(LearningMetrics.NO_DATA)
                .overallAccuracy(LearningMetrics.NO_DATA)
                .retention7d(LearningMetrics.NO_DATA)
                .retention30d(LearningMetrics.NO_DATA)
                .averageResponseMs(LearningMetrics.NO_DATA)
                .build();

        InterviewStats stats = InterviewStats.from(metrics);

        assertThat(stats.total()).isEqualTo(11);
        assertThat(stats.due()).isEqualTo(22);
        assertThat(stats.fresh()).isEqualTo(6);
        assertThat(stats.learned()).isEqualTo(33);
        assertThat(stats.correct()).isEqualTo(44);
        assertThat(stats.wrong()).isEqualTo(55);
        // «К повтору» — это очередь минус то, что ни разу не открывали.
        assertThat(stats.dueRepeats()).isEqualTo(16);
    }

    /**
     * Новых больше, чем всей очереди, быть не может, но при рассинхроне источников
     * запись обязана вернуть ноль, а не отрицательный счётчик в интерфейсе.
     */
    @Test
    void dueRepeatsNeverGoesNegative() {
        assertThat(new InterviewStats(10, 3, 8, 0, 0, 0).dueRepeats()).isZero();
    }

    @ParameterizedTest
    @ValueSource(strings = {"total", "due", "fresh", "learned", "correct", "wrong"})
    void negativeFieldThrows(String field) {
        long t = 1, d = 1, f = 1, l = 1, c = 1, w = 1;
        switch (field) {
            case "total" -> t = -1;
            case "due" -> d = -1;
            case "fresh" -> f = -1;
            case "learned" -> l = -1;
            case "correct" -> c = -1;
            case "wrong" -> w = -1;
            default -> throw new IllegalStateException(field);
        }
        long ft = t, fd = d, ff = f, fl = l, fc = c, fw = w;
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new InterviewStats(ft, fd, ff, fl, fc, fw))
                .withMessageContaining(field);
    }

    @Test
    void validCountersConstructSuccessfully() {
        InterviewStats stats = new InterviewStats(100, 10, 4, 40, 300, 50);
        assertThat(stats.total()).isEqualTo(100);
        assertThat(stats.wrong()).isEqualTo(50);
    }
}
