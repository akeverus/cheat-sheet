package com.cheatsheet.quiz.service.ai;

import com.cheatsheet.quiz.config.AppProperties;
import com.cheatsheet.quiz.domain.Difficulty;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.service.AdaptiveDifficultyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuestionGenerationServiceTest {

    @Mock
    OptionGenerator optionGenerator;
    @Mock
    QuestionValidationService validationService;
    @Mock
    AdaptiveDifficultyService adaptiveDifficultyService;
    @Mock
    QuestionUniquenessService questionUniquenessService;

    private QuestionGenerationService service;

    @BeforeEach
    void setUp() {
        AppProperties appProperties = new AppProperties();
        appProperties.getInterview().setQuestionGenerationMaxAttempts(3);
        service = new QuestionGenerationService(
                optionGenerator,
                new ObjectMapper(),
                validationService,
                adaptiveDifficultyService,
                new QuestionPromptBuilder(),
                new QuestionGenerationPolicy(70),
                questionUniquenessService,
                appProperties
        );
        when(questionUniquenessService.loadRecentFingerprints(anyString(), org.mockito.ArgumentMatchers.any()))
                .thenReturn(Set.of());
    }

    @Test
    void regeneratesUntilQuestionPassesValidation() {
        when(adaptiveDifficultyService.resolveDifficulty("java")).thenReturn(Difficulty.HARD);
        when(optionGenerator.generateStructuredJson(anyString()))
                .thenReturn(Optional.of(validQuestionJson()))
                .thenReturn(Optional.of(alternativeQuestionJson()));
        when(validationService.validate(org.mockito.ArgumentMatchers.any(Question.class)))
                .thenReturn(List.of("shortExplanation must be at least 30 characters"))
                .thenReturn(List.of());
        when(validationService.qualityScore(anyList())).thenReturn(80);

        Question generated = service.generateQuestion("java", QuestionType.CODE);

        assertThat(generated.topic()).isEqualTo("java");
        assertThat(generated.type()).isEqualTo(QuestionType.CODE);
        assertThat(generated.difficulty()).isEqualTo(Difficulty.HARD);
        assertThat(generated.options()).hasSize(4);
        verify(optionGenerator, times(2)).generateStructuredJson(anyString());
        verify(validationService, times(2)).validate(org.mockito.ArgumentMatchers.any(Question.class));
        verify(questionUniquenessService, times(1))
                .rememberFingerprint(anyString(), org.mockito.ArgumentMatchers.any(), anyString());
    }

    @Test
    void retryPromptIncludesPreviousValidationViolations() {
        when(adaptiveDifficultyService.resolveDifficulty("java")).thenReturn(Difficulty.MEDIUM);
        when(optionGenerator.generateStructuredJson(anyString()))
                .thenReturn(Optional.of(validQuestionJson()))
                .thenReturn(Optional.of(alternativeQuestionJson()));
        when(validationService.validate(org.mockito.ArgumentMatchers.any(Question.class)))
                .thenReturn(List.of("Need deeper detailedExplanation"))
                .thenReturn(List.of());
        when(validationService.qualityScore(anyList())).thenReturn(80);

        service.generateQuestion("java", QuestionType.CONCEPT);

        ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
        verify(optionGenerator, times(2)).generateStructuredJson(promptCaptor.capture());
        List<String> prompts = promptCaptor.getAllValues();
        assertThat(prompts).hasSize(2);
        assertThat(prompts.get(0)).doesNotContain("QUALITY_FEEDBACK_FROM_PREVIOUS_ATTEMPT");
        assertThat(prompts.get(1)).contains("QUALITY_FEEDBACK_FROM_PREVIOUS_ATTEMPT");
        assertThat(prompts.get(1)).contains("Need deeper detailedExplanation");
    }

    @Test
    void retriesWhenCandidateDuplicatesPreviousAttempt() {
        when(adaptiveDifficultyService.resolveDifficulty("java")).thenReturn(Difficulty.MEDIUM);
        when(optionGenerator.generateStructuredJson(anyString()))
                .thenReturn(Optional.of(validQuestionJson()))
                .thenReturn(Optional.of(validQuestionJson()))
                .thenReturn(Optional.of(alternativeQuestionJson()));
        when(validationService.validate(org.mockito.ArgumentMatchers.any(Question.class)))
                .thenReturn(List.of("Need stronger first attempt"))
                .thenReturn(List.of())
                .thenReturn(List.of());
        when(validationService.qualityScore(anyList())).thenReturn(80);

        Question generated = service.generateQuestion("java", QuestionType.CONCEPT);

        assertThat(generated.questionText()).contains("ThreadLocal");
        ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
        verify(optionGenerator, times(3)).generateStructuredJson(promptCaptor.capture());
        List<String> prompts = promptCaptor.getAllValues();
        assertThat(prompts.get(2)).contains("Question candidate duplicates previous generation attempt");
    }

    @Test
    void throwsAfterMaxAttemptsWhenValidationAlwaysFails() {
        when(adaptiveDifficultyService.resolveDifficulty("spring")).thenReturn(Difficulty.MEDIUM);
        when(optionGenerator.generateStructuredJson(anyString())).thenReturn(Optional.of(validQuestionJson()));
        when(validationService.validate(org.mockito.ArgumentMatchers.any(Question.class)))
                .thenReturn(List.of("Question text is trivial and does not require technical reasoning"));
        when(validationService.qualityScore(anyList())).thenReturn(50);

        assertThatThrownBy(() -> service.generateQuestion("spring", QuestionType.CONCEPT))
                .isInstanceOf(AiGenerationException.class)
                .hasMessageContaining("не удалось сгенерировать валидный вопрос за 3 попытки");

        verify(optionGenerator, times(3)).generateStructuredJson(anyString());
        verify(validationService, times(3)).validate(org.mockito.ArgumentMatchers.any(Question.class));
    }

    @Test
    void retriesWhenQualityScoreBelowPolicyThreshold() {
        when(adaptiveDifficultyService.resolveDifficulty("kafka")).thenReturn(Difficulty.MEDIUM);
        when(optionGenerator.generateStructuredJson(anyString())).thenReturn(Optional.of(validQuestionJson()));
        when(validationService.validate(org.mockito.ArgumentMatchers.any(Question.class))).thenReturn(List.of());
        when(validationService.qualityScore(anyList())).thenReturn(65);

        assertThatThrownBy(() -> service.generateQuestion("kafka", QuestionType.ARCHITECTURE))
                .isInstanceOf(AiGenerationException.class)
                .hasMessageContaining("bestQualityScore=65");

        verify(optionGenerator, times(3)).generateStructuredJson(anyString());
        verify(validationService, times(3)).qualityScore(anyList());
    }

    @Test
    void throwsWhenAiDoesNotReturnJson() {
        when(adaptiveDifficultyService.resolveDifficulty("sql")).thenReturn(Difficulty.EASY);
        when(optionGenerator.generateStructuredJson(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.generateQuestion("sql", QuestionType.CONCEPT))
                .isInstanceOf(AiGenerationException.class)
                .hasMessageContaining("AI не вернул валидный JSON вопроса");

        verify(optionGenerator, times(1)).generateStructuredJson(anyString());
    }

    @Test
    void parsesQuestionFromFencedJsonWithExtraText() {
        when(adaptiveDifficultyService.resolveDifficulty("jvm")).thenReturn(Difficulty.MEDIUM);
        when(optionGenerator.generateStructuredJson(anyString())).thenReturn(Optional.of(fencedJsonWithNoise()));
        when(validationService.validate(org.mockito.ArgumentMatchers.any(Question.class))).thenReturn(List.of());
        when(validationService.qualityScore(anyList())).thenReturn(100);

        Question generated = service.generateQuestion("jvm", QuestionType.CONCEPT);

        assertThat(generated.questionText()).contains("HashMap");
        assertThat(generated.options()).hasSize(4);
        assertThat(generated.commonMistake()).isNotBlank();
        verify(optionGenerator, times(1)).generateStructuredJson(anyString());
        verify(validationService, times(1)).validate(org.mockito.ArgumentMatchers.any(Question.class));
    }

    private String validQuestionJson() {
        return """
                {
                  "questionText":"Почему HashMap может терять производительность при плохом hashCode?",
                  "codeSnippet":null,
                  "options":[
                    {"id":"A","text":"Из-за роста числа коллизий и длинных цепочек поиска","correct":true,"explanation":"Плохой hashCode увеличивает коллизии, что ведет к дополнительным сравнениям и деградации времени доступа."},
                    {"id":"B","text":"Потому что HashMap автоматически сортирует ключи по compareTo","correct":false,"explanation":"HashMap не сортирует ключи как TreeMap, поэтому сравнение по compareTo не определяет производительность этой структуры."},
                    {"id":"C","text":"Потому что hashCode используется только при удалении, а не при поиске","correct":false,"explanation":"HashMap использует hashCode и при вставке, и при поиске, и при удалении, поэтому это утверждение технически неверно."},
                    {"id":"D","text":"Потому что equals полностью заменяет hashCode в выборе bucket","correct":false,"explanation":"Сначала выбирается bucket по hashCode, а уже затем применяется equals, поэтому hashCode влияет напрямую."}
                  ],
                  "shortExplanation":"Качество hashCode определяет распределение ключей по bucket и напрямую влияет на среднюю стоимость операций в HashMap.",
                  "detailedExplanation":"Если hashCode распределяет ключи неравномерно, в отдельных bucket скапливается много элементов. Тогда операции get/put/remove требуют большего числа сравнений equals внутри bucket. В результате амортизированная сложность отклоняется от ожидаемой O(1) и приближается к более дорогим сценариям, особенно на горячих ключах и под высокой нагрузкой.",
                  "commonMistake":"Считать, что переопределение equals без качественного hashCode не влияет на производительность коллекций.",
                  "tags":["java","collections","hashmap"]
                }
                """;
    }

    private String fencedJsonWithNoise() {
        return """
                Ниже итоговый JSON.
                ```json
                {
                  "questionText":"Почему HashMap может терять производительность при плохом hashCode?",
                  "codeSnippet":null,
                  "options":[
                    {"id":"A","text":"Из-за роста числа коллизий и длинных цепочек поиска","correct":true,"explanation":"Плохой hashCode увеличивает коллизии, что ведет к дополнительным сравнениям и деградации времени доступа."},
                    {"id":"B","text":"Потому что HashMap автоматически сортирует ключи по compareTo","correct":false,"explanation":"HashMap не сортирует ключи как TreeMap, поэтому сравнение по compareTo не определяет производительность этой структуры."},
                    {"id":"C","text":"Потому что hashCode используется только при удалении, а не при поиске","correct":false,"explanation":"HashMap использует hashCode и при вставке, и при поиске, и при удалении, поэтому это утверждение технически неверно."},
                    {"id":"D","text":"Потому что equals полностью заменяет hashCode в выборе bucket","correct":false,"explanation":"Сначала выбирается bucket по hashCode, а уже затем применяется equals, поэтому hashCode влияет напрямую."}
                  ],
                  "shortExplanation":"Качество hashCode определяет распределение ключей по bucket и напрямую влияет на среднюю стоимость операций в HashMap.",
                  "detailedExplanation":"Если hashCode распределяет ключи неравномерно, в отдельных bucket скапливается много элементов. Тогда операции get/put/remove требуют большего числа сравнений equals внутри bucket. В результате амортизированная сложность отклоняется от ожидаемой O(1) и приближается к более дорогим сценариям, особенно на горячих ключах и под высокой нагрузкой.",
                  "commonMistake":"Считать, что переопределение equals без качественного hashCode не влияет на производительность коллекций.",
                  "tags":["java","collections","hashmap"]
                }
                ```
                Конец ответа.
                """;
    }

    private String alternativeQuestionJson() {
        return """
                {
                  "questionText":"Почему ThreadLocal может создавать утечки памяти в пуле потоков?",
                  "codeSnippet":null,
                  "options":[
                    {"id":"A","text":"Потому что значения могут остаться привязанными к долгоживущим потокам","correct":true,"explanation":"Пулы потоков переиспользуют worker-потоки, и без remove() значения могут жить дольше ожидаемого."},
                    {"id":"B","text":"Потому что ThreadLocal хранит данные только в static памяти класса","correct":false,"explanation":"ThreadLocal хранит значения в структуре конкретного потока, а не в глобальном static-хранилище."},
                    {"id":"C","text":"Потому что JVM всегда очищает ThreadLocal после каждого задания","correct":false,"explanation":"Автоочистки после каждой задачи нет, если код явно не удаляет значение, оно может сохраниться."},
                    {"id":"D","text":"Потому что ThreadLocal работает только в виртуальных потоках","correct":false,"explanation":"ThreadLocal работает и в platform threads, и в virtual threads, это не причина утечки."}
                  ],
                  "shortExplanation":"Утечки возникают при переиспользовании потоков из пула и отсутствии явной очистки ThreadLocal.",
                  "detailedExplanation":"ThreadLocal хранит данные в контексте потока, а не запроса. В thread pool один и тот же поток обслуживает множество задач, поэтому оставленное значение может протечь в следующий контекст и удерживаться дольше жизненного цикла бизнес-операции. Без remove() это ухудшает изоляцию данных и может приводить к росту памяти.",
                  "commonMistake":"Полагаться на завершение метода вместо явной очистки ThreadLocal в блоке finally.",
                  "tags":["java","concurrency","threadlocal"]
                }
                """;
    }
}
