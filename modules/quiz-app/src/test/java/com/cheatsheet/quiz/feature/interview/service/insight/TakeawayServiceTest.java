package com.cheatsheet.quiz.feature.interview.service.insight;

import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.service.ai.AiQuestionClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TakeawayServiceTest {

    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private AiQuestionClient aiQuestionClient;

    private TakeawayService service;

    @BeforeEach
    void setUp() {
        service = new TakeawayService(questionRepository, aiQuestionClient);
    }

    @Test
    void returnsCachedTakeawayWhenItIsClean() {
        Question question = questionWithTakeaway("HashMap деградирует при коллизиях hashCode.");

        Optional<String> takeaway = service.getOrGenerate(question);

        assertThat(takeaway).contains("HashMap деградирует при коллизиях hashCode.");
        verify(aiQuestionClient, never()).generateTakeaway(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void regeneratesTakeawayWhenCachedValueContainsMetaPattern() {
        Question question = questionWithTakeaway("На интервью ожидают, что вы назовёте критерии выбора.");
        when(aiQuestionClient.generateTakeaway(question.questionText(), question.answerMarkdown()))
                .thenReturn(Optional.of("HashMap теряет амортизированную O(1) при массовых коллизиях."));

        Optional<String> takeaway = service.getOrGenerate(question);

        assertThat(takeaway).contains("HashMap теряет амортизированную O(1) при массовых коллизиях.");
        verify(questionRepository).updateTakeaway(question.id(), "HashMap теряет амортизированную O(1) при массовых коллизиях.");
    }

    @Test
    void clearsCachedTakeawayWhenRegeneratedValueStillContainsMetaPattern() {
        Question question = questionWithTakeaway("Практическая ценность ответа обычно повышается.");
        when(aiQuestionClient.generateTakeaway(question.questionText(), question.answerMarkdown()))
                .thenReturn(Optional.of("На интервью ожидают, что вы назовёте критерии выбора."));

        Optional<String> takeaway = service.getOrGenerate(question);

        assertThat(takeaway).isEmpty();
        verify(questionRepository).updateTakeaway(question.id(), "");
    }

    @Test
    void clearsCachedTakeawayWhenRegeneratedValueContainsInterviewUsuallyPattern() {
        Question question = questionWithTakeaway("На интервью ожидают, что вы назовёте критерии выбора.");
        when(aiQuestionClient.generateTakeaway(anyString(), anyString()))
                .thenReturn(Optional.of("На интервью это обычно усиливают примерами из продакшена."));

        Optional<String> takeaway = service.getOrGenerate(question);

        assertThat(takeaway).isEmpty();
        verify(questionRepository).updateTakeaway(question.id(), "");
    }

    @Test
    void clearsCachedTakeawayWhenRegeneratedValueContainsPracticalAccentPattern() {
        Question question = questionWithTakeaway("На интервью ожидают, что вы назовёте критерии выбора.");
        when(aiQuestionClient.generateTakeaway(anyString(), anyString()))
                .thenReturn(Optional.of("Практический акцент здесь обычно важнее формальной точности формулировки."));

        Optional<String> takeaway = service.getOrGenerate(question);

        assertThat(takeaway).isEmpty();
        verify(questionRepository).updateTakeaway(question.id(), "");
    }

    private static Question questionWithTakeaway(String takeaway) {
        return new Question(
                42L,
                "algorithms#q1",
                "algorithms#q1",
                "cheatsheets/interview/algorithms/algorithms-interview.md",
                "algorithms",
                "Почему HashMap может терять производительность?",
                "Ответ markdown",
                false,
                "hash",
                QuestionType.TEXT,
                null,
                null,
                0,
                takeaway
        );
    }
}
