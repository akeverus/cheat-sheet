package com.cheatsheet.quiz.feature.interview.service.insight;

import com.cheatsheet.quiz.domain.AnswerOption;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.persistence.AnswerOptionRepository;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.service.ai.AiQuestionClient;
import com.cheatsheet.quiz.service.event.AnswerEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WrongAnswerFeedbackServiceTest {

    @Mock
    private AiQuestionClient aiQuestionClient;
    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private AnswerOptionRepository answerOptionRepository;

    private WrongAnswerFeedbackService service;

    @BeforeEach
    void setUp() {
        service = new WrongAnswerFeedbackService(aiQuestionClient, questionRepository, answerOptionRepository);
    }

    @Test
    void getFeedbackReturnsEmptyWhenGeneratedFeedbackContainsMetaPattern() {
        AnswerEvent event = new AnswerEvent(
                this,
                42L,
                4201L,
                4202L,
                false,
                "Почему HashMap может деградировать?",
                "Неверный вариант",
                "Правильный вариант",
                "Ответ markdown",
                "java"
        );
        when(aiQuestionClient.generateWrongAnswerFeedback(
                event.getQuestionText(),
                event.getSelectedOptionText(),
                event.getCorrectOptionText(),
                event.getAnswerMarkdown()
        )).thenReturn(Optional.of("На интервью это обычно усиливают примерами из продакшена."));

        service.onAnswer(event);
        Optional<String> feedback = service.getFeedback(event.getQuestionId(), event.getSelectedOptionId());

        assertThat(feedback).isEmpty();
    }

    @Test
    void generateComparisonReturnsEmptyWhenGeneratedComparisonContainsMetaPattern() {
        long questionId = 77L;
        long selectedOptionId = 7701L;
        Question question = sampleQuestion(questionId);
        List<AnswerOption> options = List.of(
                new AnswerOption(selectedOptionId, questionId, "Неверный ответ", false, 0, "OPENAI", null),
                new AnswerOption(7702L, questionId, "Правильный ответ", true, 1, "OPENAI", null)
        );
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        when(answerOptionRepository.findByQuestionId(questionId)).thenReturn(options);
        when(aiQuestionClient.generateComparison(question.questionText(), "Неверный ответ", "Правильный ответ"))
                .thenReturn(Optional.of("Практический акцент здесь обычно важнее формальной точности."));

        Optional<String> comparison = service.generateComparison(questionId, selectedOptionId);

        assertThat(comparison).isEmpty();
    }

    @Test
    void generateComparisonReturnsValueWhenComparisonIsClean() {
        long questionId = 88L;
        long selectedOptionId = 8801L;
        Question question = sampleQuestion(questionId);
        List<AnswerOption> options = List.of(
                new AnswerOption(selectedOptionId, questionId, "Неверный ответ", false, 0, "OPENAI", null),
                new AnswerOption(8802L, questionId, "Правильный ответ", true, 1, "OPENAI", null)
        );
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        when(answerOptionRepository.findByQuestionId(questionId)).thenReturn(options);
        when(aiQuestionClient.generateComparison(question.questionText(), "Неверный ответ", "Правильный ответ"))
                .thenReturn(Optional.of("Правильный вариант учитывает ограничение по сложности O(n log n)."));

        Optional<String> comparison = service.generateComparison(questionId, selectedOptionId);

        assertThat(comparison).hasValueSatisfying(value -> assertThat(value).contains("O(n log n)"));
        verify(aiQuestionClient).generateComparison(question.questionText(), "Неверный ответ", "Правильный ответ");
    }

    private static Question sampleQuestion(long id) {
        return new Question(
                id,
                "q-" + id,
                "q-" + id,
                "test.md",
                "java",
                "Почему решение может быть неэффективным?",
                "Ответ markdown",
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
