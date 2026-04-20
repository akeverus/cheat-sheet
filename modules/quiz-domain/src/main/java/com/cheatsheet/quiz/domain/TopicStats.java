package com.cheatsheet.quiz.domain;

import java.util.Objects;
import lombok.Builder;

/**
 * Статистика по одной теме (для графиков и таблицы тем).
 *
 * @param topic    название темы
 * @param total    общее количество вопросов в теме
 * @param due      количество вопросов, готовых к повторению
 * @param learned  количество «выученных» вопросов
 * @param correct  суммарное количество правильных ответов
 * @param wrong    суммарное количество неправильных ответов
 * @param regenSum суммарное количество перезагрузок вариантов по теме
 * @param maturityScore оценка зрелости темы (от 0.0 до 1.0)
 */
@Builder(toBuilder = true)
public record TopicStats(
        String topic,
        long total,
        long due,
        long learned,
        long correct,
        long wrong,
        long regenSum,
        double maturityScore
) {
    /**
     * Compact-конструктор с валидацией обязательных полей.
     */
    public TopicStats {
        Objects.requireNonNull(topic, "topic не может быть null");
        if (total < 0) {
            throw new IllegalArgumentException("total не может быть отрицательным: " + total);
        }
    }

    /** Backwards-compatible constructor for existing callers without maturityScore. */
    public TopicStats(String topic, long total, long due, long learned,
                      long correct, long wrong, long regenSum) {
        this(topic, total, due, learned, correct, wrong, regenSum, 0.0);
    }
}
