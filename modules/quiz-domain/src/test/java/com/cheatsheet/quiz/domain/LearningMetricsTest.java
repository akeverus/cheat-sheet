package com.cheatsheet.quiz.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

/**
 * Тесты {@link LearningMetrics} — валидации компактного конструктора и семантики
 * сентинела {@link LearningMetrics#NO_DATA}. Фиксируют: 8 счётчиков не могут быть
 * отрицательными (иначе {@code IllegalArgumentException}); доли МОГУТ быть {@code < 0}
 * (это и есть «нет данных»); {@code hasXxx()} = {@code value >= 0} (0 → есть данные,
 * NO_DATA → нет).
 */
class LearningMetricsTest {

    private static LearningMetrics.LearningMetricsBuilder validBuilder() {
        return LearningMetrics.builder()
                .cardsTotal(10).cardsSeen(5).cardsMastered(2)
                .attemptsTotal(20).attemptsCorrect(15).attemptsIncorrect(5)
                .firstAttemptAccuracy(75.0).overallAccuracy(80.0)
                .retention7d(90.0).retention30d(70.0)
                .dueNow(3).overdue(1).averageResponseMs(1200.0);
    }

    @Test
    void emptyConstantHasAllSharesInNoDataState() {
        LearningMetrics empty = LearningMetrics.EMPTY;
        assertThat(empty.hasFirstAttemptAccuracy()).isFalse();
        assertThat(empty.hasOverallAccuracy()).isFalse();
        assertThat(empty.hasRetention7d()).isFalse();
        assertThat(empty.hasRetention30d()).isFalse();
        assertThat(empty.hasAverageResponse()).isFalse();
        assertThat(empty.cardsTotal()).isZero();
        assertThat(empty.attemptsTotal()).isZero();
        assertThat(empty.firstAttemptAccuracy()).isEqualTo(LearningMetrics.NO_DATA);
    }

    @Test
    void fullyPopulatedMetricsReportAllDataPresent() {
        LearningMetrics m = validBuilder().build();
        assertThat(m.hasFirstAttemptAccuracy()).isTrue();
        assertThat(m.hasOverallAccuracy()).isTrue();
        assertThat(m.hasRetention7d()).isTrue();
        assertThat(m.hasRetention30d()).isTrue();
        assertThat(m.hasAverageResponse()).isTrue();
    }

    @Test
    void hasAccessorsAreTrueAtZeroBoundary() {
        // 0 — валидное «есть данные» (0 >= 0), в отличие от NO_DATA (-1).
        LearningMetrics m = validBuilder()
                .firstAttemptAccuracy(0.0).overallAccuracy(0.0)
                .retention7d(0.0).retention30d(0.0).averageResponseMs(0.0)
                .build();
        assertThat(m.hasFirstAttemptAccuracy()).isTrue();
        assertThat(m.hasOverallAccuracy()).isTrue();
        assertThat(m.hasRetention7d()).isTrue();
        assertThat(m.hasRetention30d()).isTrue();
        assertThat(m.hasAverageResponse()).isTrue();
    }

    @Test
    void noDataSentinelIsTreatedAsAbsent() {
        LearningMetrics m = validBuilder()
                .retention7d(LearningMetrics.NO_DATA)
                .averageResponseMs(LearningMetrics.NO_DATA)
                .build();
        assertThat(m.hasRetention7d()).isFalse();
        assertThat(m.hasAverageResponse()).isFalse();
        // Прочие доли остаются присутствующими.
        assertThat(m.hasOverallAccuracy()).isTrue();
    }

    @Test
    void negativeSharesAreAllowedNotValidated() {
        // Доли (double) НЕ валидируются — отрицательное значение = «нет данных», не ошибка.
        LearningMetrics m = validBuilder()
                .firstAttemptAccuracy(-42.0)
                .build();
        assertThat(m.hasFirstAttemptAccuracy()).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "cardsTotal", "cardsSeen", "cardsMastered",
            "attemptsTotal", "attemptsCorrect", "attemptsIncorrect",
            "dueNow", "overdue"
    })
    void negativeCounterThrows(String field) {
        LearningMetrics.LearningMetricsBuilder b = validBuilder();
        switch (field) {
            case "cardsTotal" -> b.cardsTotal(-1);
            case "cardsSeen" -> b.cardsSeen(-1);
            case "cardsMastered" -> b.cardsMastered(-1);
            case "attemptsTotal" -> b.attemptsTotal(-1);
            case "attemptsCorrect" -> b.attemptsCorrect(-1);
            case "attemptsIncorrect" -> b.attemptsIncorrect(-1);
            case "dueNow" -> b.dueNow(-1);
            case "overdue" -> b.overdue(-1);
            default -> throw new IllegalStateException(field);
        }
        assertThatIllegalArgumentException().isThrownBy(b::build);
    }
}
