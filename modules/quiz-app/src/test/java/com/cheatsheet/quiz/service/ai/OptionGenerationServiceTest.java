package com.cheatsheet.quiz.service.ai;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cheatsheet.quiz.config.AppProperties;
import com.cheatsheet.quiz.domain.AnswerOption;
import com.cheatsheet.quiz.domain.OptionSource;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.persistence.AnswerOptionRepository;
import com.cheatsheet.quiz.service.admin.SeniorRulePriorityOverrideStore;
import com.cheatsheet.quiz.service.ai.dto.GeneratedOptions;
import com.cheatsheet.quiz.service.cache.OptionCache;
import com.google.common.util.concurrent.Striped;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionTemplate;

@ExtendWith(MockitoExtension.class)
class OptionGenerationServiceTest {

    @Mock
    AnswerOptionRepository answerOptionRepository;

    @Mock
    OptionGenerator optionGenerator;

    @Mock
    OptionCache optionCache;

    @Mock
    AppProperties appProperties;
    @Mock
    SeniorRulePriorityOverrideStore seniorRulePriorityOverrideStore;

    @Mock
    TransactionTemplate transactionTemplate;

    @Mock
    OptionDeduplicator deduplicator;

    @Mock
    DomainDifficultyContextBuilder domainDifficultyContextBuilder;

    @Mock
    OptionQualityValidator optionQualityValidator;

    OptionGenerationService optionGenerationService;

    private Question question;

    @BeforeEach
    void setUp() {
        var interview = new AppProperties.Interview();
        interview.setOptionsCount(4);
        interview.setOptionQualityRetryAttempts(0);
        interview.setOptionQualityRetryBackoffMs(0);
        interview.setOptionQualityRetryMaxElapsedMs(1000);
        when(appProperties.getInterview()).thenReturn(interview);

        lenient().doAnswer(invocation -> {
            invocation.<java.util.function.Consumer<org.springframework.transaction.TransactionStatus>>getArgument(0)
                    .accept(null);
            return null;
        }).when(transactionTemplate).executeWithoutResult(any());
        lenient().when(domainDifficultyContextBuilder.sanitizeAnswerContext(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
        lenient().when(domainDifficultyContextBuilder.build(any(), any())).thenReturn("");
        lenient().when(optionQualityValidator.validateQualityReportWithContext(any(), anyString(), anyString(), any()))
                .thenReturn(new OptionQualityValidator.ValidationReport(List.of()));
        lenient().when(optionQualityValidator.hasHardBlockIssues(any())).thenReturn(false);
        lenient().when(seniorRulePriorityOverrideStore.view()).thenReturn(interview.getSeniorRulePriorityOverrides());

        Striped<Lock> questionLocks = Striped.lock(16);
        optionGenerationService = new OptionGenerationService(
                answerOptionRepository,
                optionGenerator,
                optionCache,
                appProperties,
                seniorRulePriorityOverrideStore,
                questionLocks,
                transactionTemplate,
                deduplicator,
                domainDifficultyContextBuilder,
                optionQualityValidator
        );

        question = new Question(1L, "slug", "slug", "f.md", "topic",
                "Что такое X?", "X — это ответ.", false, "hash", QuestionType.TEXT, null, null, 0, null);
    }

    @Test
    void usesAiOptionsWhenAvailable() {
        List<AnswerOption> savedOptions = List.of(
                new AnswerOption(1L, 1L, "Правильный", true, 0, "OPENAI", "Это верно"),
                new AnswerOption(2L, 1L, "A", false, 1, "OPENAI", "Неверно: A"),
                new AnswerOption(3L, 1L, "B", false, 2, "OPENAI", "Неверно: B"),
                new AnswerOption(4L, 1L, "C", false, 3, "OPENAI", "Неверно: C")
        );
        when(answerOptionRepository.findByQuestionId(1L))
                .thenReturn(List.of())
                .thenReturn(savedOptions);
        when(optionGenerator.generateOptions(anyString(), anyString(), any()))
                .thenReturn(Optional.of(new GeneratedOptions(
                        "Правильный", "Это верно", List.of("A", "B", "C"), List.of("Неверно: A", "Неверно: B", "Неверно: C"))));
        lenient().when(optionGenerator.generateOptions(anyString(), anyString(), any(), anyString()))
                .thenReturn(Optional.of(new GeneratedOptions(
                        "Правильный", "Это верно", List.of("A", "B", "C"), List.of("Неверно: A", "Неверно: B", "Неверно: C"))));
        when(optionGenerator.sourceId()).thenReturn(OptionSource.OPENAI);
        when(deduplicator.deduplicateAndFill(anyList(), anyList(), any()))
                .thenReturn(new OptionDeduplicator.DedupResult(
                        List.of("Правильный", "A", "B", "C"),
                        List.of("Это верно", "Неверно: A", "Неверно: B", "Неверно: C")));

        List<AnswerOption> result = optionGenerationService.getOrCreateOptions(question);

        assertThat(result).hasSize(4);
        assertThat(result.stream().filter(AnswerOption::correct).findFirst())
                .get().extracting(AnswerOption::optionText).isEqualTo("Правильный");
    }

    @Test
    void throwsWhenAiReturnsEmptyOptions() {
        when(answerOptionRepository.findByQuestionId(1L)).thenReturn(List.of());
        when(optionGenerator.generateOptions(anyString(), anyString(), any()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> optionGenerationService.getOrCreateOptions(question))
                .isInstanceOf(AiGenerationException.class)
                .hasMessageContaining("не вернул валидные варианты");
    }

    @Test
    void regeneratesWhenStoredOptionsUseOldProfileVersion() {
        List<AnswerOption> oldVersionOptions = List.of(
                new AnswerOption(1L, 1L, "Старый правильный", true, 0, "OPENAI", "ok", 1, 1),
                new AnswerOption(2L, 1L, "Старый A", false, 1, "OPENAI", "a", 1, 1),
                new AnswerOption(3L, 1L, "Старый B", false, 2, "OPENAI", "b", 1, 1),
                new AnswerOption(4L, 1L, "Старый C", false, 3, "OPENAI", "c", 1, 1)
        );
        List<AnswerOption> freshOptions = List.of(
                new AnswerOption(11L, 1L, "Новый правильный", true, 0, "OPENAI", "ok", 9, 9),
                new AnswerOption(12L, 1L, "Новый A", false, 1, "OPENAI", "a", 9, 9),
                new AnswerOption(13L, 1L, "Новый B", false, 2, "OPENAI", "b", 9, 9),
                new AnswerOption(14L, 1L, "Новый C", false, 3, "OPENAI", "c", 9, 9)
        );
        when(answerOptionRepository.findByQuestionId(1L))
                .thenReturn(oldVersionOptions)
                .thenReturn(freshOptions);
        when(optionGenerator.generateOptions(anyString(), anyString(), any()))
                .thenReturn(Optional.of(new GeneratedOptions(
                        "Новый правильный",
                        "Проверяется на новом профиле",
                        List.of("Новый A", "Новый B", "Новый C"),
                        List.of("a", "b", "c")
                )));
        when(optionGenerator.sourceId()).thenReturn(OptionSource.OPENAI);
        when(deduplicator.deduplicateAndFill(anyList(), anyList(), any()))
                .thenReturn(new OptionDeduplicator.DedupResult(
                        List.of("Новый правильный", "Новый A", "Новый B", "Новый C"),
                        List.of("ok", "a", "b", "c")
                ));

        List<AnswerOption> result = optionGenerationService.getOrCreateOptions(question);

        assertThat(result).hasSize(4);
        verify(optionGenerator, times(1)).generateOptions(anyString(), anyString(), any());
        verify(answerOptionRepository).deleteByQuestionId(1L);
        verify(answerOptionRepository).insertAll(eq(1L), anyList());
    }

    @Test
    void usesSingleShotGenerationWithoutQualityRetry() {
        List<AnswerOption> savedOptions = List.of(
                new AnswerOption(1L, 1L, "Правильный", true, 0, "OPENAI", "ok"),
                new AnswerOption(2L, 1L, "Wrong1", false, 1, "OPENAI", "x"),
                new AnswerOption(3L, 1L, "Wrong2", false, 2, "OPENAI", "y"),
                new AnswerOption(4L, 1L, "Wrong3", false, 3, "OPENAI", "z")
        );
        when(answerOptionRepository.findByQuestionId(1L))
                .thenReturn(List.of())
                .thenReturn(savedOptions);
        when(optionGenerator.generateOptions(anyString(), anyString(), any()))
                .thenReturn(Optional.of(new GeneratedOptions(
                        "Правильный", "ok", List.of("Wrong1", "Wrong2", "Wrong3"), List.of("x", "y", "z")
                )));
        when(optionGenerator.sourceId()).thenReturn(OptionSource.OPENAI);
        when(deduplicator.deduplicateAndFill(anyList(), anyList(), any()))
                .thenReturn(new OptionDeduplicator.DedupResult(
                        List.of("Правильный", "Wrong1", "Wrong2", "Wrong3"),
                        Arrays.asList("ok", "x", "y", "z")));

        List<AnswerOption> result = optionGenerationService.getOrCreateOptions(question);

        assertThat(result).hasSize(4);
        verify(optionGenerator, times(1)).generateOptions(anyString(), anyString(), any());
    }

    @Test
    void retriesWithQualityFixContextWhenCriticalIssuesDetected() {
        var interview = new AppProperties.Interview();
        interview.setOptionsCount(4);
        interview.setOptionQualityRetryAttempts(1);
        interview.setOptionQualityRetryBackoffMs(0);
        interview.setOptionQualityRetryMaxElapsedMs(2000);
        when(appProperties.getInterview()).thenReturn(interview);

        Striped<Lock> questionLocks = Striped.lock(16);
        optionGenerationService = new OptionGenerationService(
                answerOptionRepository,
                optionGenerator,
                optionCache,
                appProperties,
                seniorRulePriorityOverrideStore,
                questionLocks,
                transactionTemplate,
                deduplicator,
                domainDifficultyContextBuilder,
                optionQualityValidator
        );

        List<AnswerOption> savedOptions = List.of(
                new AnswerOption(1L, 1L, "Правильный", true, 0, "OPENAI", "ok"),
                new AnswerOption(2L, 1L, "A", false, 1, "OPENAI", "a"),
                new AnswerOption(3L, 1L, "B", false, 2, "OPENAI", "b"),
                new AnswerOption(4L, 1L, "C", false, 3, "OPENAI", "c")
        );
        when(answerOptionRepository.findByQuestionId(1L))
                .thenReturn(List.of())
                .thenReturn(savedOptions);
        when(domainDifficultyContextBuilder.build(eq(question), any())).thenReturn("- base quality context");
        GeneratedOptions generated = new GeneratedOptions("Правильный", "ok", List.of("A", "B", "C"), List.of("a", "b", "c"));
        when(optionGenerator.generateOptions(anyString(), anyString(), any(), anyString()))
                .thenReturn(Optional.of(generated))
                .thenReturn(Optional.of(generated));
        when(optionGenerator.sourceId()).thenReturn(OptionSource.OPENAI);
        when(deduplicator.deduplicateAndFill(anyList(), anyList(), any()))
                .thenReturn(new OptionDeduplicator.DedupResult(
                        List.of("Правильный", "A", "B", "C"),
                        List.of("ok", "a", "b", "c")));

        OptionQualityValidator.ValidationIssue issue = new OptionQualityValidator.ValidationIssue(
                OptionQualityValidator.IssueCode.CORRECT_ANSWER_MISMATCH,
                OptionQualityValidator.Severity.CRITICAL,
                "Правильный ответ не совпадает с expected answer"
        );
        OptionQualityValidator.ValidationReport criticalReport =
                new OptionQualityValidator.ValidationReport(List.of(issue));
        OptionQualityValidator.ValidationReport cleanReport =
                new OptionQualityValidator.ValidationReport(List.of());
        when(optionQualityValidator.validateQualityReportWithContext(any(), anyString(), anyString(), any()))
                .thenReturn(criticalReport)
                .thenReturn(cleanReport);
        when(optionQualityValidator.hasHardBlockIssues(criticalReport)).thenReturn(false);

        List<AnswerOption> result = optionGenerationService.getOrCreateOptions(question);

        assertThat(result).hasSize(4);
        ArgumentCaptor<String> qualityContextCaptor = ArgumentCaptor.forClass(String.class);
        verify(optionGenerator, times(2)).generateOptions(anyString(), anyString(), any(), qualityContextCaptor.capture());
        List<String> contexts = qualityContextCaptor.getAllValues();
        assertThat(contexts.get(0)).contains("base quality context");
        assertThat(contexts.get(1)).contains("QUALITY_FIX_REQUIRED");
        assertThat(contexts.get(1)).contains("Правильный ответ не совпадает с expected answer");
    }

    @Test
    void stripsInterviewMetaNoiseFromAnswerContextBeforeGeneration() {
        Question noisyQuestion = new Question(2L, "slug-2", "slug-2", "f.md", "topic",
                "Что такое Spring Boot?",
                "Фактическая часть ответа про автонастройку.\n\n" +
                        "Практическая ценность ответа обычно повышается, если дополнить определение операционным контекстом.\n" +
                        "На интервью ожидают, что вы назовёте критерии выбора и способ валидации решения через метрики.",
                false, "hash-2", QuestionType.TEXT, null, null, 0, null);
        List<AnswerOption> savedOptions = List.of(
                new AnswerOption(21L, 2L, "Правильный", true, 0, "OPENAI", "ok"),
                new AnswerOption(22L, 2L, "A", false, 1, "OPENAI", "a"),
                new AnswerOption(23L, 2L, "B", false, 2, "OPENAI", "b"),
                new AnswerOption(24L, 2L, "C", false, 3, "OPENAI", "c")
        );
        when(answerOptionRepository.findByQuestionId(2L))
                .thenReturn(List.of())
                .thenReturn(savedOptions);
        when(optionGenerator.generateOptions(anyString(), anyString(), any()))
                .thenReturn(Optional.of(new GeneratedOptions(
                        "Правильный", "ok", List.of("A", "B", "C"), List.of("a", "b", "c")
                )));
        when(optionGenerator.sourceId()).thenReturn(OptionSource.OPENAI);
        when(deduplicator.deduplicateAndFill(anyList(), anyList(), any()))
                .thenReturn(new OptionDeduplicator.DedupResult(
                        List.of("Правильный", "A", "B", "C"),
                        List.of("ok", "a", "b", "c")
                ));

        List<AnswerOption> result = optionGenerationService.getOrCreateOptions(noisyQuestion);
        assertThat(result).hasSize(4);
    }

    @Test
    void addsDomainDifficultyContextForImportantJavaQuestion() {
        Question javaQuestion = new Question(3L, "slug-3", "slug-3", "f.md", "programming-languages/java/java-core-interview",
                "В чем разница equals() и hashCode()?",
                "equals/hashCode должны быть согласованы по контракту.",
                true, "hash-3", QuestionType.TEXT, null, null, 0, null);
        List<AnswerOption> savedOptions = List.of(
                new AnswerOption(31L, 3L, "Правильный", true, 0, "OPENAI", "ok"),
                new AnswerOption(32L, 3L, "A", false, 1, "OPENAI", "a"),
                new AnswerOption(33L, 3L, "B", false, 2, "OPENAI", "b"),
                new AnswerOption(34L, 3L, "C", false, 3, "OPENAI", "c")
        );
        when(answerOptionRepository.findByQuestionId(3L))
                .thenReturn(List.of())
                .thenReturn(savedOptions);
        when(domainDifficultyContextBuilder.build(eq(javaQuestion), any()))
                .thenReturn("- java profile context");
        when(optionGenerator.generateOptions(anyString(), anyString(), any(), anyString()))
                .thenReturn(Optional.of(new GeneratedOptions(
                        "Правильный", "ok", List.of("A", "B", "C"), List.of("a", "b", "c")
                )));
        when(optionGenerator.sourceId()).thenReturn(OptionSource.OPENAI);
        when(deduplicator.deduplicateAndFill(anyList(), anyList(), any()))
                .thenReturn(new OptionDeduplicator.DedupResult(
                        List.of("Правильный", "A", "B", "C"),
                        List.of("ok", "a", "b", "c")
                ));

        List<AnswerOption> result = optionGenerationService.getOrCreateOptions(javaQuestion);
        assertThat(result).hasSize(4);
    }

    @Test
    void keepsLlmTextsUnchangedBeforeSavingOptions() {
        List<AnswerOption> savedOptions = List.of(
                new AnswerOption(1L, 1L, "Правильный вариант про сравнение.", true, 0, "OPENAI", "ok"),
                new AnswerOption(2L, 1L, "Wrong1", false, 1, "OPENAI", "a"),
                new AnswerOption(3L, 1L, "Wrong2", false, 2, "OPENAI", "b"),
                new AnswerOption(4L, 1L, "Wrong3", false, 3, "OPENAI", "c")
        );
        when(answerOptionRepository.findByQuestionId(1L))
                .thenReturn(List.of())
                .thenReturn(savedOptions);
        when(optionGenerator.generateOptions(anyString(), anyString(), any()))
                .thenReturn(Optional.of(new GeneratedOptions(
                        "Сравнивают по времени и памяти на одинаковых входах.",
                        "ok",
                        List.of(
                                "Сравнивают algorithms/algorithms-interview по времени и памяти на тех же входах.",
                                "Сравнивают src/main/java как эталон производительности на тех же входах.",
                                "Сравнивают по average-case, не проверяя worst-case и ограничения."
                        ),
                        List.of("a", "b", "c")
                )));
        when(optionGenerator.sourceId()).thenReturn(OptionSource.OPENAI);
        when(deduplicator.deduplicateAndFill(anyList(), anyList(), any()))
                .thenReturn(new OptionDeduplicator.DedupResult(
                        List.of(
                                "Сравнивают по времени и памяти на одинаковых входах.",
                                "Сравнивают algorithms/algorithms-interview по времени и памяти на тех же входах.",
                                "Сравнивают src/main/java как эталон производительности на тех же входах.",
                                "Сравнивают по average-case, не проверяя worst-case и ограничения."
                        ),
                        List.of("ok", "a", "b", "c")
                ));

        optionGenerationService.getOrCreateOptions(question);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<AnswerOptionRepository.AnswerOptionCreate>> captor =
                (ArgumentCaptor<List<AnswerOptionRepository.AnswerOptionCreate>>) (ArgumentCaptor<?>)
                        ArgumentCaptor.forClass(List.class);
        verify(answerOptionRepository).insertAll(eq(1L), captor.capture());
        List<AnswerOptionRepository.AnswerOptionCreate> inserted = captor.getValue();
        assertThat(inserted).isNotEmpty();
        assertThat(inserted.stream().map(AnswerOptionRepository.AnswerOptionCreate::optionText))
                .anyMatch(text -> text.contains("algorithms/algorithms-interview")
                        || text.contains("src/main/java"));
    }

    @Test
    void throwsWhenDedupLeavesInsufficientOptions() {
        when(answerOptionRepository.findByQuestionId(1L)).thenReturn(List.of());
        when(optionGenerator.generateOptions(anyString(), anyString(), any()))
                .thenReturn(Optional.of(new GeneratedOptions(
                        "Правильный вариант",
                        "ok",
                        List.of("Дубликат", "Дубликат", "Дубликат"),
                        List.of("a", "b", "c")
                )));
        when(optionGenerator.sourceId()).thenReturn(OptionSource.OPENAI);
        when(deduplicator.deduplicateAndFill(anyList(), anyList(), any()))
                .thenReturn(new OptionDeduplicator.DedupResult(
                        List.of("Правильный вариант", "Дубликат"),
                        Arrays.asList("ok", "a")));
        when(deduplicator.isDuplicate(anyString(), any())).thenReturn(true);

        assertThatThrownBy(() -> optionGenerationService.getOrCreateOptions(question))
                .isInstanceOf(AiGenerationException.class)
                .hasMessageContaining("недостаточно уникальных вариантов");
    }

    @Test
    void acceptsReducedOptionsWhenDedupHasAtLeastThreeUniqueVariants() {
        List<AnswerOption> savedOptions = List.of(
                new AnswerOption(1L, 1L, "Правильный вариант", true, 0, "OPENAI", "ok"),
                new AnswerOption(2L, 1L, "Дубликат A", false, 1, "OPENAI", "a"),
                new AnswerOption(3L, 1L, "Дубликат B", false, 2, "OPENAI", "b")
        );
        when(answerOptionRepository.findByQuestionId(1L))
                .thenReturn(List.of())
                .thenReturn(savedOptions);
        when(optionGenerator.generateOptions(anyString(), anyString(), any()))
                .thenReturn(Optional.of(new GeneratedOptions(
                        "Правильный вариант",
                        "ok",
                        List.of("Дубликат A", "Дубликат B", "Дубликат C"),
                        List.of("a", "b", "c")
                )));
        when(optionGenerator.sourceId()).thenReturn(OptionSource.OPENAI);
        when(deduplicator.deduplicateAndFill(anyList(), anyList(), any()))
                .thenReturn(new OptionDeduplicator.DedupResult(
                        List.of("Правильный вариант", "Дубликат A", "Дубликат B"),
                        Arrays.asList("ok", "a", "b")));

        List<AnswerOption> result = optionGenerationService.getOrCreateOptions(question);

        assertThat(result).hasSize(3);
        verify(answerOptionRepository).insertAll(eq(1L), anyList());
    }

    @Test
    void acceptsNoisyFencedGeneratedOptionsWhenDedupProvidesCleanSet() {
        List<AnswerOption> savedOptions = List.of(
                new AnswerOption(11L, 1L, "Правильный вариант", true, 0, "OPENAI", "ok"),
                new AnswerOption(12L, 1L, "Wrong A", false, 1, "OPENAI", "a"),
                new AnswerOption(13L, 1L, "Wrong B", false, 2, "OPENAI", "b"),
                new AnswerOption(14L, 1L, "Wrong C", false, 3, "OPENAI", "c")
        );
        when(answerOptionRepository.findByQuestionId(1L))
                .thenReturn(List.of())
                .thenReturn(savedOptions);
        when(optionGenerator.generateOptions(anyString(), anyString(), any()))
                .thenReturn(Optional.of(new GeneratedOptions(
                        "  ```json\nПравильный вариант\n```  ",
                        "ok",
                        List.of(
                                "  ```json\nWrong A\n```  ",
                                "  ```json\nWrong B\n```  ",
                                "  ```json\nWrong C\n```  "
                        ),
                        List.of("a", "b", "c")
                )));
        when(optionGenerator.sourceId()).thenReturn(OptionSource.OPENAI);
        when(deduplicator.deduplicateAndFill(anyList(), anyList(), any()))
                .thenReturn(new OptionDeduplicator.DedupResult(
                        List.of("Правильный вариант", "Wrong A", "Wrong B", "Wrong C"),
                        List.of("ok", "a", "b", "c")
                ));

        List<AnswerOption> result = optionGenerationService.getOrCreateOptions(question);

        assertThat(result).hasSize(4);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<AnswerOptionRepository.AnswerOptionCreate>> captor =
                (ArgumentCaptor<List<AnswerOptionRepository.AnswerOptionCreate>>) (ArgumentCaptor<?>)
                        ArgumentCaptor.forClass(List.class);
        verify(answerOptionRepository).insertAll(eq(1L), captor.capture());
        assertThat(captor.getValue().stream().map(AnswerOptionRepository.AnswerOptionCreate::optionText))
                .containsExactlyInAnyOrder("Правильный вариант", "Wrong A", "Wrong B", "Wrong C");
    }

    @Test
    void throwsWhenNoisyOptionsRemainInsufficientAfterFallbackDedup() {
        when(answerOptionRepository.findByQuestionId(1L)).thenReturn(List.of());
        when(optionGenerator.generateOptions(anyString(), anyString(), any()))
                .thenReturn(Optional.of(new GeneratedOptions(
                        "  ```json\nПравильный вариант\n```  ",
                        "ok",
                        List.of(" \n ", "  ```json\ndup\n```", "  ```json\ndup\n```  "),
                        List.of("a", "b", "c")
                )));
        when(optionGenerator.sourceId()).thenReturn(OptionSource.OPENAI);
        when(deduplicator.deduplicateAndFill(anyList(), anyList(), any()))
                .thenReturn(new OptionDeduplicator.DedupResult(
                        List.of("Правильный вариант"),
                        List.of("ok")
                ));
        when(deduplicator.isDuplicate(anyString(), any())).thenReturn(true);

        assertThatThrownBy(() -> optionGenerationService.getOrCreateOptions(question))
                .isInstanceOf(AiGenerationException.class)
                .hasMessageContaining("недостаточно уникальных вариантов");
    }
}
