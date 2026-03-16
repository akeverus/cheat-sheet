package com.cheatsheet.quiz.domain;

import lombok.experimental.UtilityClass;

/**
 * Константы по умолчанию для системы интервального повторения (алгоритм SM-2).
 *
 * <p>Используются при создании начального {@link ReviewState} для нового вопроса
 * и при сбросе прогресса.</p>
 *
 * @see ReviewState
 * @see ReviewResult
 */
@UtilityClass
public class ReviewDefaults {

    /** Начальный ease factor по алгоритму SM-2. */
    public static final double DEFAULT_EASE_FACTOR = 2.5;

    /** Минимально допустимый ease factor (ниже не опускается). */
    public static final double MIN_EASE_FACTOR = 1.3;

    /**
     * Создаёт начальный {@link ReviewState} для нового вопроса.
     *
     * @param questionId идентификатор вопроса
     * @param nowEpoch   текущее время в epoch-секундах
     * @return начальное состояние повторения
     */
    public static ReviewState initialState(long questionId, long nowEpoch) {
        return new ReviewState(
                questionId, 0, 0, DEFAULT_EASE_FACTOR,
                nowEpoch, ReviewResult.NEW, 0, 0
        );
    }
}
