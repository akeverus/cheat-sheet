package com.cheatsheet.quiz.feature.interview.dto.response.progress;

import lombok.Builder;

/**
 * API DTO статистики по теме.
 *
 * @param topic название темы
 * @param total общее число вопросов по теме
 * @param due число вопросов, готовых к показу (повторения + новые)
 * @param fresh число вопросов без единого ответа (подмножество {@code due})
 * @param learned число выученных вопросов
 * @param correct число правильных ответов
 * @param wrong число неправильных ответов
 */
@Builder(toBuilder = true)
public record TopicStatsResponse(
        String topic,
        long total,
        long due,
        long fresh,
        long learned,
        long correct,
        long wrong
) {
}
