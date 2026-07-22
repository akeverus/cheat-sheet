package com.cheatsheet.quiz.feature.interview.dto.response.progress;

import lombok.Builder;

/**
 * Ответ API закладки / заметки (UI-014).
 */
@Builder(toBuilder = true)
public record BookmarkResponse(
        long questionId,
        boolean bookmarked,
        String note
) {}
