package com.cheatsheet.quiz.feature.interview.dto.response.session;

import lombok.Builder;

/**
 * API DTO состояния тренировочной сессии.
 *
 * @param index текущий индекс вопроса
 * @param total общее число вопросов в сессии
 * @param correct число правильных ответов
 * @param wrong число неправильных ответов
 * @param finished признак завершения сессии
 */
@Builder(toBuilder = true)
public record TrainingSessionDTO(
        int index,
        int total,
        int correct,
        int wrong,
        boolean finished
) {
}
