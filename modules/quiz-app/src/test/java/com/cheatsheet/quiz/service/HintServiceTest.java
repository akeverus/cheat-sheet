package com.cheatsheet.quiz.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cheatsheet.quiz.domain.AnswerOption;
import com.cheatsheet.quiz.domain.Hint;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.persistence.AnswerOptionRepository;
import com.cheatsheet.quiz.persistence.HintRepository;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.feature.interview.service.insight.HintService;
import com.cheatsheet.quiz.service.ai.AiGenerationException;
import com.cheatsheet.quiz.service.ai.AiQuestionClient;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Тесты для {@link HintService}: edge cases, fallback-логика, кэширование.
 */
@ExtendWith(MockitoExtension.class)
class HintServiceTest {

    @Mock HintRepository hintRepository;
    @Mock QuestionRepository questionRepository;
    @Mock AnswerOptionRepository answerOptionRepository;
    @Mock AiQuestionClient aiQuestionClient;

    private final Clock clock = Clock.fixed(Instant.ofEpochSecond(1700000000), ZoneOffset.UTC);

    private HintService hintService;
    private Question question;

    @BeforeEach
    void setUp() {
        hintService = new HintService(hintRepository, questionRepository, answerOptionRepository, aiQuestionClient, clock);
        question = new Question(1L, "slug", "slug", "f.md", "topic",
                "Что такое X?", "X — это ответ.", false, "hash", QuestionType.TEXT, null, null, 0, null);
    }

    @Test
    void returnsCachedHintFromDb() {
        Hint cached = new Hint(10, 1L, 2, "Кэшированная подсказка", 1699999000);
        when(hintRepository.findByQuestionIdAndLevel(1L, 2)).thenReturn(Optional.of(cached));

        Optional<Hint> result = hintService.getHint(1L, 2);

        assertThat(result).isPresent();
        assertThat(result.get().hintText()).isEqualTo("Кэшированная подсказка");
        verify(aiQuestionClient, never()).generateHints(anyString(), anyString(), anyList());
    }

    @Test
    void generatesHintsViaAiWhenNotCached() {
        when(hintRepository.findByQuestionIdAndLevel(1L, 1)).thenReturn(Optional.empty());
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        when(answerOptionRepository.findByQuestionId(1L)).thenReturn(List.of(
                new AnswerOption(1L, 1L, "Правильный", true, 0, "OPENAI", null),
                new AnswerOption(2L, 1L, "Неправильный", false, 1, "OPENAI", null)
        ));
        when(aiQuestionClient.generateHints(anyString(), anyString(), anyList()))
                .thenReturn(Optional.of(List.of("Подсказка 1", "Подсказка 2", "Подсказка 3")));
        when(hintRepository.findByQuestionIdAndLevel(1L, 1))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(new Hint(100, 1L, 1, "Подсказка 1", 1700000000)));

        Optional<Hint> result = hintService.getHint(1L, 1);

        assertThat(result).isPresent();
        verify(hintRepository).insertAll(any());
    }

    @Test
    void throwsWhenAiReturnsEmpty() {
        when(hintRepository.findByQuestionIdAndLevel(1L, 1)).thenReturn(Optional.empty());
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        when(answerOptionRepository.findByQuestionId(1L)).thenReturn(List.of());
        when(aiQuestionClient.generateHints(anyString(), anyString(), anyList()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> hintService.getHint(1L, 1))
                .isInstanceOf(AiGenerationException.class)
                .hasMessageContaining("не вернул валидные подсказки");
    }

    @Test
    void acceptsWhenAiReturnsTooFewHints() {
        when(hintRepository.findByQuestionIdAndLevel(1L, 2)).thenReturn(Optional.empty());
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        when(answerOptionRepository.findByQuestionId(1L)).thenReturn(List.of());
        when(aiQuestionClient.generateHints(anyString(), anyString(), anyList()))
                .thenReturn(Optional.of(List.of("Only one hint")));
        when(hintRepository.findByQuestionIdAndLevel(1L, 2))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(new Hint(101, 1L, 2, "Only one hint", 1700000000)));

        Optional<Hint> result = hintService.getHint(1L, 2);

        assertThat(result).isPresent();
        assertThat(result.get().hintText()).isEqualTo("Only one hint");
        verify(hintRepository).insertAll(any());
    }

    @Test
    void returnsEmptyWhenQuestionNotFound() {
        when(hintRepository.findByQuestionIdAndLevel(999L, 1)).thenReturn(Optional.empty());
        when(questionRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Hint> result = hintService.getHint(999L, 1);

        assertThat(result).isEmpty();
    }

    @Test
    void clampsLevelToValidRange() {
        // Level 0 should be clamped to 1
        Hint hint = new Hint(10, 1L, 1, "Уровень 1", 1700000000);
        when(hintRepository.findByQuestionIdAndLevel(1L, 1)).thenReturn(Optional.of(hint));

        Optional<Hint> result = hintService.getHint(1L, 0);

        assertThat(result).isPresent();
        assertThat(result.get().level()).isEqualTo(1);
    }

    @Test
    void clampsHighLevelToMax() {
        // Level 10 should be clamped to 3
        Hint hint = new Hint(10, 1L, 3, "Уровень 3", 1700000000);
        when(hintRepository.findByQuestionIdAndLevel(1L, 3)).thenReturn(Optional.of(hint));

        Optional<Hint> result = hintService.getHint(1L, 10);

        assertThat(result).isPresent();
        assertThat(result.get().level()).isEqualTo(3);
    }

    @Test
    void returnsHintEvenWhenDbInsertFails() {
        when(hintRepository.findByQuestionIdAndLevel(1L, 1)).thenReturn(Optional.empty());
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        when(answerOptionRepository.findByQuestionId(1L)).thenReturn(List.of());
        when(aiQuestionClient.generateHints(anyString(), anyString(), anyList()))
                .thenReturn(Optional.of(List.of("Hint 1", "Hint 2", "Hint 3")));
        doThrow(new RuntimeException("DB error")).when(hintRepository).insertAll(any());

        Optional<Hint> result = hintService.getHint(1L, 1);

        // Should still return the hint even though DB insert failed
        assertThat(result).isPresent();
        assertThat(result.get().hintText()).isEqualTo("Hint 1");
    }
}
