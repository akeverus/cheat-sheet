package com.cheatsheet.quiz.feature.interview.dto.response.progress;

import lombok.Builder;

/**
 * API DTO статистики по теме.
 *
 * @param topic название темы
 * @param total общее число вопросов по теме
 * @param due число вопросов к повторению
 * @param learned число выученных вопросов
 * @param correct число правильных ответов
 * @param wrong число неправильных ответов
 * @param regenSum суммарное число регенераций вариантов
 */
@Builder(toBuilder = true)
public record TopicStatsResponse(
        String topic,
        long total,
        long due,
        long learned,
        long correct,
        long wrong,
        long regenSum
) {
}
