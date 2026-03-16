package com.cheatsheet.quiz.feature.interview.dto.response.progress;

import lombok.Builder;

/**
 * Ответ API с данными о дневной цели и стрике (GET /api/streak).
 */
@Builder(toBuilder = true)
public record StreakResponse(
        int today,
        int goal,
        int streak,
        boolean goalReached,
        int correct
) {}
