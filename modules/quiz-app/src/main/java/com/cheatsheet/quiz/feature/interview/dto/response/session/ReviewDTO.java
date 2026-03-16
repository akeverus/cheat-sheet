package com.cheatsheet.quiz.feature.interview.dto.response.session;

import lombok.Builder;

/**
 * API DTO состояния интервального повторения вопроса.
 *
 * @param repetitions число последовательных повторений
 * @param intervalDays интервал в днях
 * @param easeFactor коэффициент лёгкости
 * @param nextReviewAt timestamp следующего повторения
 * @param correctCount число правильных ответов
 * @param wrongCount число неправильных ответов
 */
@Builder(toBuilder = true)
public record ReviewDTO(
        int repetitions,
        int intervalDays,
        double easeFactor,
        long nextReviewAt,
        int correctCount,
        int wrongCount
) {
}
