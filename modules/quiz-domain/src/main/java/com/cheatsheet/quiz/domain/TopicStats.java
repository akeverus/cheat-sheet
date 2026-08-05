package com.cheatsheet.quiz.domain;

import java.util.Objects;
import lombok.Builder;

/**
 * Статистика по одной теме (для графиков и таблицы тем).
 *
 * <p>{@code due} — вопросы, готовые к показу: и просроченные повторения, и те, на
 * которые ещё не отвечали. {@code fresh} — только вторая часть, то есть {@code fresh}
 * всегда не больше {@code due}. Разведены ради копирайта: «к повторению» на теме, где
 * не было ни одного ответа, обещает повторить то, чего пользователь не видел.
 * Собственно отбор вопросов работает по {@code due} и на разделение не опирается.
 *
 * @param topic    название темы
 * @param total    общее количество вопросов в теме
 * @param due      количество вопросов, готовых к показу (повторения + новые)
 * @param fresh    количество вопросов без единого ответа
 * @param learned  количество «выученных» вопросов
 * @param correct  суммарное количество правильных ответов
 * @param wrong    суммарное количество неправильных ответов
 * @param maturityScore средняя зрелость темы: {@code ease_factor × interval_days}
 */
@Builder(toBuilder = true)
public record TopicStats(
        String topic,
        long total,
        long due,
        long fresh,
        long learned,
        long correct,
        long wrong,
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

    /** Вопросы, у которых уже есть история и наступил срок повторения. */
    public long dueRepeats() {
        return Math.max(0L, due - fresh);
    }
}
