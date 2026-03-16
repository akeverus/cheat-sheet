package com.cheatsheet.quiz.service.imports;

import com.cheatsheet.quiz.config.app.AppProperties;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.persistence.FullTextSearchRepository;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.persistence.ReviewStateRepository;
import com.cheatsheet.quiz.service.ai.AiQuestionClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuestionExpansionServiceTest {

    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private ReviewStateRepository reviewStateRepository;
    @Mock
    private FullTextSearchRepository fullTextSearchRepository;
    @Mock
    private AiQuestionClient aiQuestionClient;

    private QuestionExpansionService service;

    @BeforeEach
    void setUp() {
        AppProperties properties = new AppProperties();
        properties.getInterview().setExpandPerSource(2);
        Clock fixedClock = Clock.fixed(Instant.parse("2026-03-02T12:00:00Z"), ZoneOffset.UTC);
        service = new QuestionExpansionService(
                questionRepository,
                reviewStateRepository,
                fullTextSearchRepository,
                aiQuestionClient,
                properties,
                fixedClock
        );
    }

    @Test
    void expandFromBaseCreatesMissingVariantsOnly() {
        Question base = baseQuestion();
        when(aiQuestionClient.generateAlternativeQuestions(base.questionText(), base.answerMarkdown(), 2))
                .thenReturn(Optional.of(List.of("Вариант 1", "Вариант 2")));
        when(questionRepository.findBySlug("topic/file.md#Q1-v1"))
                .thenReturn(Optional.of(base));
        when(questionRepository.findBySlug("topic/file.md#Q1-v2"))
                .thenReturn(Optional.empty());
        when(questionRepository.insert(any())).thenReturn(100L);

        int created = service.expandFromBase(base, "topic/file", "topic/file.md");

        assertThat(created).isEqualTo(1);
        ArgumentCaptor<Question> captor = ArgumentCaptor.forClass(Question.class);
        verify(questionRepository).insert(captor.capture());
        assertThat(captor.getValue().slug()).isEqualTo("topic/file.md#Q1-v2");
        verify(reviewStateRepository).insertIfAbsent(100L, Instant.parse("2026-03-02T12:00:00Z").getEpochSecond());
        verify(fullTextSearchRepository).upsert(100L, "Вариант 2", base.answerMarkdown());
    }

    @Test
    void expandFromBaseSkipsWhenAlternativesAreEmpty() {
        Question base = baseQuestion();
        when(aiQuestionClient.generateAlternativeQuestions(base.questionText(), base.answerMarkdown(), 2))
                .thenReturn(Optional.of(List.of()));

        int created = service.expandFromBase(base, "topic/file", "topic/file.md");

        assertThat(created).isZero();
        verify(questionRepository, never()).insert(any());
        verify(reviewStateRepository, never()).insertIfAbsent(anyLong(), anyLong());
    }

    private Question baseQuestion() {
        return new Question(
                42L,
                "topic/file.md#Q1",
                "topic/file.md#Q1",
                "topic/file.md",
                "topic/file",
                "Базовый вопрос",
                "Базовый ответ",
                false,
                "hash",
                QuestionType.TEXT,
                null,
                null,
                0,
                null
        );
    }
}
