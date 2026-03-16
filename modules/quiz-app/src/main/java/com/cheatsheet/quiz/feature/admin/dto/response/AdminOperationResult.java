package com.cheatsheet.quiz.feature.admin.dto.response;

import lombok.Builder;

/**
 * Результат административной операции (например, очистка вариантов).
 */
@Builder(toBuilder = true)
public record AdminOperationResult(String message, Object details) {}
