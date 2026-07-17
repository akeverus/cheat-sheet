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
}
