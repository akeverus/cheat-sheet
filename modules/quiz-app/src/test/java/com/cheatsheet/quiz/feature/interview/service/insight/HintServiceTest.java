package com.cheatsheet.quiz.feature.interview.service.insight;

import com.cheatsheet.quiz.domain.AnswerOption;
import com.cheatsheet.quiz.domain.Hint;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.persistence.AnswerOptionRepository;
import com.cheatsheet.quiz.persistence.HintRepository;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.service.ai.AiGenerationException;
import com.cheatsheet.quiz.service.ai.AiQuestionClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HintServiceTest {

    @Mock private HintRepository hintRepository;
    @Mock private QuestionRepository questionRepository;
    @Mock private AnswerOptionRepository answerOptionRepository;
    @Mock private AiQuestionClient aiQuestionClient;

    private HintService service;
    private final Clock fixedClock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneId.of("UTC"));

    @BeforeEach
    void setUp() {
        service = new HintService(hintRepository, questionRepository, answerOptionRepository, aiQuestionClient, fixedClock);
    }

    @Test
    void returnsCachedHintWhenPresent() {
        Hint cached = new Hint(1L, 42L, 1, "Подумайте о хеш-функции.", 1000L);
        when(hintRepository.findByQuestionIdAndLevel(42L, 1)).thenReturn(Optional.of(cached));

        Optional<Hint> result = service.getHint(42L, 1);

        assertThat(result).contains(cached);
        verifyNoInteractions(aiQuestionClient);
    }

    @Test
    void returnsEmptyWhenQuestionNotFound() {
        when(hintRepository.findByQuestionIdAndLevel(99L, 1)).thenReturn(Optional.empty());
        when(questionRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Hint> result = service.getHint(99L, 1);

        assertThat(result).isEmpty();
        verifyNoInteractions(aiQuestionClient);
    }

    @Test
    void generatesAndSavesHintsFromAi() {
        when(hintRepository.findByQuestionIdAndLevel(1L, 2)).thenReturn(Optional.empty());
        when(questionRepository.findById(1L)).thenReturn(Optional.of(sampleQuestion()));
        when(answerOptionRepository.findByQuestionId(1L)).thenReturn(List.of(
                new AnswerOption(10L, 1L, "Wrong1", false, 0, "OPENAI", null),
                new AnswerOption(11L, 1L, "Correct", true, 1, "OPENAI", null)
        ));
        when(aiQuestionClient.generateHints(anyString(), anyString(), anyList()))
                .thenReturn(Optional.of(List.of("Hint 1", "Hint 2", "Hint 3")));
        when(hintRepository.findByQuestionIdAndLevel(1L, 2))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(new Hint(0, 1L, 2, "Hint 2", 0)));

        Optional<Hint> result = service.getHint(1L, 2);

        assertThat(result).isPresent();
        assertThat(result.get().hintText()).isEqualTo("Hint 2");
        verify(hintRepository).insertAll(anyList());
    }

    @Test
    void throwsWhenAiReturnsEmpty() {
        when(hintRepository.findByQuestionIdAndLevel(1L, 1)).thenReturn(Optional.empty());
        when(questionRepository.findById(1L)).thenReturn(Optional.of(sampleQuestion()));
        when(answerOptionRepository.findByQuestionId(1L)).thenReturn(List.of());
        when(aiQuestionClient.generateHints(anyString(), anyString(), anyList()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getHint(1L, 1))
                .isInstanceOf(AiGenerationException.class);
    }

    @Test
    void clampsLevelToValidRange() {
        Hint cached = new Hint(1L, 42L, 1, "Level 1 hint", 1000L);
        when(hintRepository.findByQuestionIdAndLevel(42L, 1)).thenReturn(Optional.of(cached));

        Optional<Hint> result = service.getHint(42L, 0);

        assertThat(result).contains(cached);
        verify(hintRepository).findByQuestionIdAndLevel(42L, 1);
    }

    @Test
    void clampsLevelAboveMax() {
        Hint cached = new Hint(1L, 42L, 3, "Level 3 hint", 1000L);
        when(hintRepository.findByQuestionIdAndLevel(42L, 3)).thenReturn(Optional.of(cached));

        Optional<Hint> result = service.getHint(42L, 10);

        assertThat(result).contains(cached);
        verify(hintRepository).findByQuestionIdAndLevel(42L, 3);
    }

    private static Question sampleQuestion() {
        return new Question(1L, "q1", "q1", "test.md", "java",
                "Что такое HashMap?", "HashMap — хеш-таблица.",
                false, "hash", QuestionType.TEXT, null, null, 0, null);
    }
}
