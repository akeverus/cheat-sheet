package com.cheatsheet.quiz.feature.interview.dto.response.progress;

import lombok.Builder;

/**
 * Ответ API на переключение избранного (POST /api/favorite).
 */
@Builder(toBuilder = true)
public record FavoriteResponse(
        boolean favorite,
        boolean synced,
        long questionId
) {}
