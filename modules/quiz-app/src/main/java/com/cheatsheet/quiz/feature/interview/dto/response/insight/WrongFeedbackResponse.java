package com.cheatsheet.quiz.feature.interview.dto.response.insight;

import lombok.Builder;

/**
 * Ответ API с фидбеком по неправильному ответу (POST /api/wrong-feedback).
 */
@Builder(toBuilder = true)
public record WrongFeedbackResponse(
        long questionId,
        long optionId,
        String feedback,
        boolean available
) {}
