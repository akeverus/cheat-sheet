package com.cheatsheet.quiz.feature.admin.dto.response;

import lombok.Builder;

/**
 * Ответ API перегенерации вариантов (POST /api/regenerate, 200 OK).
 */
@Builder(toBuilder = true)
public record RegenerateResponse(boolean success, long questionId, String message) {}
