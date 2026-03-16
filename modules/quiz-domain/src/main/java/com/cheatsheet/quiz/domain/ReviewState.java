package com.cheatsheet.quiz.domain;

import lombok.With;
import lombok.Builder;

/**
 * Состояние интервального повторения (spaced repetition) для конкретного вопроса.
 *
 * <p>Хранит параметры алгоритма SM-2: количество повторений, интервал,
 * ease-фактор, дату следующего повторения, а также статистику ответов.</p>
 *
 * @param questionId  идентификатор вопроса (FK → questions.id)
 * @param repetitions количество последовательных правильных ответов
 * @param intervalDays текущий интервал повторения в днях
 * @param easeFactor  коэффициент лёгкости (≥ {@link ReviewDefaults#MIN_EASE_FACTOR})
 * @param nextReviewAt epoch-секунды следующего запланированного повторения
 * @param lastResult  результат последнего ответа
 * @param correctCount общее количество правильных ответов
 * @param wrongCount   общее количество неправильных ответов
 */
@With
@Builder(toBuilder = true)
public record ReviewState(
        long questionId,
        int repetitions,
        int intervalDays,
        double easeFactor,
        long nextReviewAt,
        ReviewResult lastResult,
        int correctCount,
        int wrongCount
) {
    /**
     * Compact constructor with validation.
     */
    public ReviewState {
        if (easeFactor < 1.3) {
            throw new IllegalArgumentException("easeFactor must be >= 1.3");
        }
        if (repetitions < 0) {
            throw new IllegalArgumentException("repetitions must be non-negative: " + repetitions);
        }
        if (correctCount < 0) {
            throw new IllegalArgumentException("correctCount must be non-negative: " + correctCount);
        }
        if (wrongCount < 0) {
            throw new IllegalArgumentException("wrongCount must be non-negative: " + wrongCount);
        }
    }
}
