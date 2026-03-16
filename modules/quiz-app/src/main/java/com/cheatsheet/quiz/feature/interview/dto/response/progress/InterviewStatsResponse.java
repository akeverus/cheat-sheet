package com.cheatsheet.quiz.feature.interview.dto.response.progress;

import lombok.Builder;

/**
 * API DTO агрегированной статистики интервью.
 *
 * @param total общее число вопросов в выборке
 * @param due число вопросов, готовых к повторению
 * @param learned число выученных вопросов
 * @param correct число правильных ответов
 * @param wrong число неправильных ответов
 */
@Builder(toBuilder = true)
public record InterviewStatsResponse(
        long total,
        long due,
        long learned,
        long correct,
        long wrong
) {
}
