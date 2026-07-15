package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionIssue;
import com.cheatsheet.quiz.domain.QuestionIssueCategory;
import com.cheatsheet.quiz.domain.QuestionIssueStatus;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.domain.exception.QuestionNotFoundException;
import com.cheatsheet.quiz.feature.interview.service.report.QuestionIssueService;
import com.cheatsheet.quiz.persistence.QuestionIssueRepository;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuestionIssueServiceTest {

    @Mock
    QuestionIssueRepository questionIssueRepository;
    @Mock
    QuestionRepository questionRepository;

    private final Clock clock = Clock.fixed(Instant.ofEpochSecond(1_700_000_000L), ZoneOffset.UTC);
    private QuestionIssueService service;

    @BeforeEach
    void setUp() {
        service = new QuestionIssueService(questionIssueRepository, questionRepository, clock);
    }

    @Test
    void reportPersistsIssueAndReturnsOpenRecord() {
        when(questionRepository.findById(5L)).thenReturn(Optional.of(sampleQuestion(5L)));
        when(questionIssueRepository.save(eq(5L), eq(QuestionIssueCategory.TYPO), eq("опечатка"), any()))
                .thenReturn(42L);

        QuestionIssue issue = service.report(5L, QuestionIssueCategory.TYPO, "  опечатка  ");

        assertThat(issue.id()).isEqualTo(42L);
        assertThat(issue.questionId()).isEqualTo(5L);
        assertThat(issue.category()).isEqualTo(QuestionIssueCategory.TYPO);
        assertThat(issue.comment()).isEqualTo("опечатка");
        assertThat(issue.status()).isEqualTo(QuestionIssueStatus.OPEN);
        assertThat(issue.createdAt()).isEqualTo(clock.instant());
        verify(questionIssueRepository).save(5L, QuestionIssueCategory.TYPO, "опечатка", clock.instant());
    }

    @Test
    void reportNormalisesBlankCommentToNull() {
        when(questionRepository.findById(5L)).thenReturn(Optional.of(sampleQuestion(5L)));
        when(questionIssueRepository.save(eq(5L), eq(QuestionIssueCategory.OTHER), eq(null), any()))
                .thenReturn(7L);

        QuestionIssue issue = service.report(5L, QuestionIssueCategory.OTHER, "   ");

        assertThat(issue.comment()).isNull();
        verify(questionIssueRepository).save(5L, QuestionIssueCategory.OTHER, null, clock.instant());
    }

    @Test
    void reportThrowsWhenQuestionMissing() {
        when(questionRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.report(404L, QuestionIssueCategory.OTHER, "x"))
                .isInstanceOf(QuestionNotFoundException.class);
        verify(questionIssueRepository, never()).save(anyLong(), any(), any(), any());
    }

    private Question sampleQuestion(long id) {
        return new Question(
                id, "slug-" + id, "source-" + id, "topic.md", "topic",
                "What is Java?", "Java is a language", false, "hash",
                QuestionType.TEXT, null, null, 0, null);
    }
}
