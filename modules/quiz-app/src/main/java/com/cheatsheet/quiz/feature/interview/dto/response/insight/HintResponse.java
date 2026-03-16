package com.cheatsheet.quiz.feature.interview.dto.response.insight;

import lombok.Builder;

/**
 * Ответ API подсказки (POST /api/hint).
 */
@Builder(toBuilder = true)
public record HintResponse(long questionId, int maxLevel, String hint, Integer level) {}
