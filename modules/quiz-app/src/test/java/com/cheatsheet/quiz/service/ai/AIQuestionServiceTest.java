package com.cheatsheet.quiz.service.ai;

import com.cheatsheet.quiz.config.app.AppProperties;
import com.cheatsheet.quiz.domain.AnswerOption;
import com.cheatsheet.quiz.domain.OptionSource;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.persistence.AnswerOptionRepository;
import com.cheatsheet.quiz.service.ai.dto.GeneratedOptions;
import com.cheatsheet.quiz.service.ai.option.AIQuestionService;
import com.cheatsheet.quiz.service.cache.OptionCache;
import com.google.common.util.concurrent.Striped;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AIQuestionServiceTest {
    @Mock
    private AnswerOptionRepository answerOptionRepository;
    @Mock
    private AiQuestionClient aiQuestionClient;
    @Mock
    private OptionCache optionCache;
    @Mock
    private TransactionTemplate transactionTemplate;

    private AIQuestionService service;
    private Question question;
    private SimpleMeterRegistry meterRegistry;

    @BeforeEach
    void setUp() {
        lenient().doAnswer(invocation -> {
            invocation.<java.util.function.Consumer<org.springframework.transaction.TransactionStatus>>getArgument(0)
                    .accept(null);
            return null;
        }).when(transactionTemplate).executeWithoutResult(any());
        // Mockito returns empty List (not null) by default for List-returning methods;
        // explicitly stub cache miss so service proceeds to generate options.
        lenient().when(optionCache.get(anyLong())).thenReturn(null);

        AppProperties appProperties = new AppProperties() {
            @Override
            public boolean isAiEnabled() {
                return true;
            }

            @Override
            public boolean isAiFallbackAllowed() {
                // Тесты этого класса покрывают именно AI-генерацию опций,
                // поэтому fallback включён явно.
                return true;
            }
        };
        appProperties.setInterviewPath("cheatsheets/interview");

        Striped<Lock> questionLocks = Striped.lock(16);
        meterRegistry = new SimpleMeterRegistry();
        service = new AIQuestionService(
                answerOptionRepository,
                aiQuestionClient,
                optionCache,
                questionLocks,
                transactionTemplate,
                appProperties,
                meterRegistry
        );
        question = new Question(1L, "slug", "slug", "f.md", "topic",
                "Что такое X?", "X — это ответ.", false, "hash", QuestionType.TEXT, null, null, 0, null);
    }

    @Test
    void usesSingleLlmResultWithoutPostProcessing() {
        List<AnswerOption> saved = List.of(
                new AnswerOption(1L, 1L, "Correct", true, 0, "OPENAI", "ok"),
                new AnswerOption(2L, 1L, "W1", false, 1, "OPENAI", "a"),
                new AnswerOption(3L, 1L, "W2", false, 2, "OPENAI", "b"),
                new AnswerOption(4L, 1L, "W3", false, 3, "OPENAI", "c")
        );
        when(answerOptionRepository.findByQuestionId(1L)).thenReturn(List.of()).thenReturn(saved);
        when(aiQuestionClient.generateOptions(anyString(), any())).thenReturn(Optional.of(
                new GeneratedOptions(List.of(
                        new GeneratedOptions.GeneratedOption("Correct", true),
                        new GeneratedOptions.GeneratedOption("W1", false),
                        new GeneratedOptions.GeneratedOption("W2", false),
                        new GeneratedOptions.GeneratedOption("W3", false)
                ))
        ));
        when(aiQuestionClient.sourceId()).thenReturn(OptionSource.OPENAI);

        List<AnswerOption> options = service.getOrCreateOptions(question);

        assertThat(options).hasSize(4);
        assertThat(options.stream().filter(AnswerOption::correct).count()).isEqualTo(1);
        assertThat(meterRegistry.find("mcq.ai.fallback").tag("outcome", "called").counter().count())
                .isEqualTo(1.0);
        assertThat(meterRegistry.find("mcq.ai.fallback").tag("outcome", "suppressed").counter().count())
                .isEqualTo(0.0);
    }

    @Test
    void suppressesAiCallWhenFallbackDisabled() {
        AppProperties seedOnly = new AppProperties() {
            @Override
            public boolean isAiEnabled() {
                return false;
            }
            @Override
            public boolean isAiFallbackAllowed() {
                return false;
            }
        };
        seedOnly.setInterviewPath("cheatsheets/interview");
        Striped<Lock> questionLocks = Striped.lock(16);
        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        AIQuestionService seedOnlyService = new AIQuestionService(
                answerOptionRepository, aiQuestionClient, optionCache,
                questionLocks, transactionTemplate, seedOnly, registry);
        when(answerOptionRepository.findByQuestionId(1L)).thenReturn(List.of());

        List<AnswerOption> options = seedOnlyService.getOrCreateOptions(question);

        assertThat(options).isEmpty();
        verify(aiQuestionClient, never()).generateOptions(anyString(), anyString());
        assertThat(registry.find("mcq.ai.fallback").tag("outcome", "suppressed").counter().count())
                .isEqualTo(1.0);
        assertThat(registry.find("mcq.ai.fallback").tag("outcome", "called").counter().count())
                .isEqualTo(0.0);
    }

    @Test
    void acceptsModelPayloadWhenLlmReturnsFewerOptions() {
        List<AnswerOption> saved = List.of(
                new AnswerOption(1L, 1L, "Correct", true, 0, "OPENAI", null),
                new AnswerOption(2L, 1L, "W1", false, 1, "OPENAI", null)
        );
        when(answerOptionRepository.findByQuestionId(1L)).thenReturn(List.of()).thenReturn(saved);
        when(aiQuestionClient.generateOptions(anyString(), any())).thenReturn(Optional.of(
                new GeneratedOptions(List.of(
                        new GeneratedOptions.GeneratedOption("Correct", true),
                        new GeneratedOptions.GeneratedOption("W1", false)
                ))
        ));
        when(aiQuestionClient.sourceId()).thenReturn(OptionSource.OPENAI);

        List<AnswerOption> options = service.getOrCreateOptions(question);

        assertThat(options).hasSize(2);
    }

    @Test
    void returnsCachedEmptyOptionsWithoutRegeneration() {
        when(optionCache.get(1L)).thenReturn(List.of());

        List<AnswerOption> options = service.getOrCreateOptions(question);

        assertThat(options).isEmpty();
        verify(answerOptionRepository, never()).findByQuestionId(1L);
        verify(aiQuestionClient, never()).generateOptions(anyString(), anyString());
    }
}
