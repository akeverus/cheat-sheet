package com.cheatsheet.quiz.feature.interview.usecase;

import com.cheatsheet.quiz.domain.QuestionIssue;
import com.cheatsheet.quiz.domain.QuestionIssueCategory;
import com.cheatsheet.quiz.feature.interview.dto.response.progress.ReportIssueResponse;
import com.cheatsheet.quiz.feature.interview.service.report.QuestionIssueService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Use-case orchestration для API endpoint `/api/report` (FLOW-REPORT).
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IssueReportApiService {

    QuestionIssueService questionIssueService;

    public ReportIssueResponse report(long questionId, String category, String comment) {
        QuestionIssue issue = questionIssueService.report(
                questionId, QuestionIssueCategory.fromString(category), comment);
        return new ReportIssueResponse(
                true, issue.id(), issue.questionId(), issue.category().name(), issue.status().name());
    }

    public ResponseEntity<ReportIssueResponse> toHttpResponse(long questionId, String category, String comment) {
        return ResponseEntity.ok(report(questionId, category, comment));
    }
}
