package com.cheatsheet.quiz.feature.interview.dto.response.progress;

import lombok.Builder;

/**
 * Ответ API на обновление уверенности (POST /api/confidence).
 */
@Builder(toBuilder = true)
public record ConfidenceResponse(
        boolean success,
        long questionId,
        int grade
) {}
