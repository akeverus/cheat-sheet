package com.cheatsheet.quiz.domain;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты знаменателя точности {@link SessionSummary} (Фаза 4). Точность считается
 * от числа ОТВЕЧЕННЫХ вопросов (correct+wrong+unknown), а не запланированного total.
 */
class SessionSummaryTest {

    private static SessionSummary.Builder base() {
        return SessionSummary.builder()
                .mode(InterviewMode.EXAM)
                .duration(Duration.ofMinutes(1));
    }

    @Test
    void accuracyUsesAnsweredNotPlannedTotal() {
        // Запланировано 5, отвечено 3 (2 верных, 1 неверный) — сессия прервана.
        SessionSummary summary = base()
                .totalQuestions(5)
                .correctCount(2)
                .wrongCount(1)
                .unknownCount(0)
                .build();

        // 2/3, а не 2/5=40 — неотвеченные не размывают точность.
        assertThat(summary.getAccuracy()).isEqualTo(200.0 / 3.0);
    }

    @Test
    void unknownCountsAgainstAccuracyDenominator() {
        SessionSummary summary = base()
                .totalQuestions(2)
                .correctCount(1)
                .wrongCount(0)
                .unknownCount(1)
                .build();

        // 1/(1+0+1) = 50%.
        assertThat(summary.getAccuracy()).isEqualTo(50.0);
    }

    @Test
    void zeroAnsweredYieldsZeroAccuracy() {
        SessionSummary summary = base()
                .totalQuestions(3)
                .correctCount(0)
                .wrongCount(0)
                .unknownCount(0)
                .build();

        assertThat(summary.getAccuracy()).isZero();
    }

    /**
     * Число в кольце-гейдже ограничено не габаритом кольца, а его внутренним просветом,
     * и десятичная доля в этот бюджет не влезала: «100,0%» ложилось крайними знаками на
     * дугу того же цвета, из-за чего идеальный результат читался как «00,0%». Гарантия
     * со стороны домена — не длиннее «100%», то есть максимум четыре знака с процентом.
     */
    @Test
    void gaugeAccuracyNeverExceedsFourCharactersWithPercent() {
        SessionSummary perfect = base()
                .totalQuestions(1).correctCount(1).wrongCount(0).unknownCount(0).build();

        assertThat(perfect.getAccuracyRounded()).isEqualTo(100);
        assertThat(perfect.getAccuracyRounded() + "%").hasSizeLessThanOrEqualTo(4);
    }

    /**
     * Округление не имеет права выдать «100%» сессии с ошибкой: весь смысл этого экрана —
     * отличить безошибочную сессию от почти безошибочной. Симметрично 0 % оставлен за
     * сессией, где не угадано ничего.
     */
    @Test
    void gaugeAccuracyNeverRoundsIntoAPerfectOrEmptyScore() {
        // 249/250 = 99,6 % — Math.round дал бы 100.
        SessionSummary almostPerfect = base()
                .totalQuestions(250).correctCount(249).wrongCount(1).unknownCount(0).build();
        assertThat(almostPerfect.getAccuracyRounded())
                .as("сессия с ошибкой не должна показывать 100%%")
                .isEqualTo(99);

        // 1/250 = 0,4 % — Math.round дал бы 0.
        SessionSummary almostEmpty = base()
                .totalQuestions(250).correctCount(1).wrongCount(249).unknownCount(0).build();
        assertThat(almostEmpty.getAccuracyRounded())
                .as("сессия с верным ответом не должна показывать 0%%")
                .isEqualTo(1);

        SessionSummary nothingRight = base()
                .totalQuestions(2).correctCount(0).wrongCount(2).unknownCount(0).build();
        assertThat(nothingRight.getAccuracyRounded()).isZero();
    }

    /**
     * KPI-тайл на итогах — колонка фиксированной сетки, и его бюджет строки (~101px)
     * длинная форма «12 мин 30 сек» (152px) превышает при любой сессии длиннее минуты.
     * Гарантия со стороны домена — компактная запись не длиннее «ч:мм:сс».
     */
    @Test
    void compactDurationStaysWithinTheKpiTileBudget() {
        assertThat(durationOf(Duration.ofSeconds(36)).getCompactDuration()).isEqualTo("0:36");
        assertThat(durationOf(Duration.ofSeconds(750)).getCompactDuration()).isEqualTo("12:30");
        assertThat(durationOf(Duration.ofSeconds(3905)).getCompactDuration()).isEqualTo("1:05:05");

        assertThat(durationOf(Duration.ofSeconds(750)).getCompactDuration())
                .as("минуты и секунды не расходятся по разным «словам» — тайл не переносится")
                .doesNotContain(" ");
    }

    /**
     * Полная форма остаётся: она живёт в свободной строке метрик, где ширина не
     * зажата, и там «12 мин 30 сек» читается лучше таймерной записи.
     */
    @Test
    void formattedDurationKeepsTheVerboseFormForFreeFlowingMetrics() {
        assertThat(durationOf(Duration.ofSeconds(750)).getFormattedDuration()).isEqualTo("12 мин 30 сек");
        assertThat(durationOf(Duration.ofSeconds(36)).getFormattedDuration()).isEqualTo("36 сек");
    }

    private static SessionSummary durationOf(Duration duration) {
        return SessionSummary.builder()
                .mode(InterviewMode.EXAM)
                .duration(duration)
                .totalQuestions(1)
                .correctCount(1)
                .build();
    }
}
