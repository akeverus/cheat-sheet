package com.cheatsheet.quiz.domain;
import lombok.Builder;

/**
 * Агрегированная статистика тестирования (используется в UI для панели информации).
 *
 * <p>Предпочтительно строить через {@link #from(LearningMetrics)}, чтобы знаменатели
 * карточек и попыток совпадали с {@link LearningMetrics} на всех экранах.</p>
 *
 * @param total   общее количество вопросов (по фильтру) / {@code cardsTotal}
 * @param due     количество вопросов, готовых к повторению / {@code dueNow}
 * @param learned количество «выученных» вопросов (repetitions ≥ порога) / {@code cardsMastered}
 * @param correct суммарное количество правильных попыток / {@code attemptsCorrect}
 * @param wrong   суммарное количество ошибочных попыток / {@code attemptsIncorrect}
 */
@Builder(toBuilder = true)
public record InterviewStats(
        long total,
        long due,
        long learned,
        long correct,
        long wrong
) {

    public InterviewStats {
        if (total < 0) throw new IllegalArgumentException("total must be >= 0");
        if (due < 0) throw new IllegalArgumentException("due must be >= 0");
        if (learned < 0) throw new IllegalArgumentException("learned must be >= 0");
        if (correct < 0) throw new IllegalArgumentException("correct must be >= 0");
        if (wrong < 0) throw new IllegalArgumentException("wrong must be >= 0");
    }

    /**
     * View-adapter поверх единых {@link LearningMetrics}: одни и те же формулы
     * для sidebar/settings/result без дублирования расчётов в шаблонах.
     */
    public static InterviewStats from(LearningMetrics metrics) {
        if (metrics == null) {
            return new InterviewStats(0, 0, 0, 0, 0);
        }
        return new InterviewStats(
                metrics.cardsTotal(),
                metrics.dueNow(),
                metrics.cardsMastered(),
                metrics.attemptsCorrect(),
                metrics.attemptsIncorrect()
        );
    }
}
