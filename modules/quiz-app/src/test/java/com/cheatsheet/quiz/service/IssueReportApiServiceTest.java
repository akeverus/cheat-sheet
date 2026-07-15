package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.domain.QuestionIssue;
import com.cheatsheet.quiz.domain.QuestionIssueCategory;
import com.cheatsheet.quiz.domain.QuestionIssueStatus;
import com.cheatsheet.quiz.feature.interview.dto.response.progress.ReportIssueResponse;
import com.cheatsheet.quiz.feature.interview.service.report.QuestionIssueService;
import com.cheatsheet.quiz.feature.interview.usecase.IssueReportApiService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IssueReportApiServiceTest {

    @Mock
    QuestionIssueService questionIssueService;

    @InjectMocks
    IssueReportApiService service;

    @Test
    void reportParsesCategoryAndWrapsResponse() {
        QuestionIssue issue = new QuestionIssue(
                9L, 3L, QuestionIssueCategory.OUTDATED, "устарело", QuestionIssueStatus.OPEN, Instant.now());
        when(questionIssueService.report(3L, QuestionIssueCategory.OUTDATED, "устарело")).thenReturn(issue);

        ResponseEntity<ReportIssueResponse> response = service.toHttpResponse(3L, "outdated", "устарело");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ReportIssueResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.success()).isTrue();
        assertThat(body.issueId()).isEqualTo(9L);
        assertThat(body.questionId()).isEqualTo(3L);
        assertThat(body.category()).isEqualTo("OUTDATED");
        assertThat(body.status()).isEqualTo("OPEN");
    }

    @Test
    void reportFallsBackToOtherForUnknownCategory() {
        QuestionIssue issue = new QuestionIssue(
                1L, 2L, QuestionIssueCategory.OTHER, null, QuestionIssueStatus.OPEN, Instant.now());
        when(questionIssueService.report(2L, QuestionIssueCategory.OTHER, null)).thenReturn(issue);

        ResponseEntity<ReportIssueResponse> response = service.toHttpResponse(2L, "gibberish", null);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().category()).isEqualTo("OTHER");
    }
}
