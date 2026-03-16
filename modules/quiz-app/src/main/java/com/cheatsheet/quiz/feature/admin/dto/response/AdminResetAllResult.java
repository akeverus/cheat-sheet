package com.cheatsheet.quiz.feature.admin.dto.response;

import lombok.Builder;

/**
 * Результат полного сброса AI-артефактов (POST /api/admin/reset-all).
 */
@Builder(toBuilder = true)
public record AdminResetAllResult(
        int deletedOptions,
        int deletedHints,
        int clearedDiagrams,
        int resetRegenCount,
        String message
) {}
