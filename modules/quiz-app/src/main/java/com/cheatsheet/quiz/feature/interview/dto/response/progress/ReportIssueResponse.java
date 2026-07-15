package com.cheatsheet.quiz.feature.interview.dto.response.progress;

import lombok.Builder;

/**
 * Ответ API на жалобу о проблеме с вопросом (POST /api/report, FLOW-REPORT).
 */
@Builder(toBuilder = true)
public record ReportIssueResponse(
        boolean success,
        long issueId,
        long questionId,
        String category,
        String status
) {}
