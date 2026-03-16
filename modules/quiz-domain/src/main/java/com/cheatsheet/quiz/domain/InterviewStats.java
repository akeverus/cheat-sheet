package com.cheatsheet.quiz.domain;
import lombok.Builder;

/**
 * Агрегированная статистика тестирования (используется в UI для панели информации).
 *
 * @param total   общее количество вопросов (по фильтру)
 * @param due     количество вопросов, готовых к повторению
 * @param learned количество «выученных» вопросов (repetitions ≥ порога)
 * @param correct суммарное количество правильных ответов
 * @param wrong   суммарное количество неправильных ответов
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
}
