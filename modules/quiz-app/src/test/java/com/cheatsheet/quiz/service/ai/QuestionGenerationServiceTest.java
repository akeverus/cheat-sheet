package com.cheatsheet.quiz.service.ai;

import com.cheatsheet.quiz.config.app.AppProperties;
import com.cheatsheet.quiz.domain.Difficulty;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.feature.interview.service.topic.AdaptiveDifficultyService;
import com.cheatsheet.quiz.feature.question.engine.mapper.QuestionGeneratedJsonMapper;
import com.cheatsheet.quiz.feature.question.engine.metadata.QuestionGeneratedMetadataSupplier;
import com.cheatsheet.quiz.feature.question.engine.metadata.QuestionGeneratedSlugFactory;
import com.cheatsheet.quiz.feature.question.engine.prompt.QuestionPromptBuilder;
import com.cheatsheet.quiz.feature.question.engine.prompt.QuestionTopicNormalizer;
import com.cheatsheet.quiz.feature.question.engine.service.QuestionGenerationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuestionGenerationServiceTest {

    @Mock
    AiQuestionClient aiQuestionClient;
    @Mock
    AdaptiveDifficultyService adaptiveDifficultyService;

    private QuestionGenerationService service;

    @BeforeEach
    void setUp() {
        service = new QuestionGenerationService(
                aiQuestionClient,
                new QuestionGeneratedJsonMapper(
                        new ObjectMapper(),
                        new QuestionGeneratedSlugFactory(Clock.systemUTC()),
                        new QuestionGeneratedMetadataSupplier(new AppProperties())
                ),
                new QuestionTopicNormalizer(),
                adaptiveDifficultyService,
                new QuestionPromptBuilder(new QuestionTopicNormalizer())
        );
    }

    @Test
    void generatesQuestionWithSingleLlmCall() {
        when(adaptiveDifficultyService.resolveDifficulty("java")).thenReturn(Difficulty.HARD);
        when(aiQuestionClient.generateStructuredJson(anyString())).thenReturn(Optional.of(validQuestionJson()));

        Question generated = service.generateQuestion("java", QuestionType.CODE);

        assertThat(generated.topic()).isEqualTo("java");
        assertThat(generated.type()).isEqualTo(QuestionType.CODE);
        assertThat(generated.difficulty()).isEqualTo(Difficulty.HARD);
        assertThat(generated.questionText()).contains("HashMap");
        assertThat(generated.options()).hasSize(4);
        assertThat(generated.options().stream().filter(option -> option.correct())).hasSize(1);
        verify(aiQuestionClient).generateStructuredJson(anyString());
    }

    @Test
    void promptUsesGeneralForBlankTopic() {
        when(adaptiveDifficultyService.resolveDifficulty("   ")).thenReturn(Difficulty.MEDIUM);
        when(aiQuestionClient.generateStructuredJson(anyString())).thenReturn(Optional.of(validQuestionJson()));

        service.generateQuestion("   ", QuestionType.CONCEPT);

        ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
        verify(aiQuestionClient).generateStructuredJson(promptCaptor.capture());
        assertThat(promptCaptor.getValue()).contains("general");
    }

    @Test
    void throwsWhenModelReturnsEmptyResponse() {
        when(adaptiveDifficultyService.resolveDifficulty("sql")).thenReturn(Difficulty.EASY);
        when(aiQuestionClient.generateStructuredJson(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.generateQuestion("sql", QuestionType.CONCEPT))
                .isInstanceOf(AiGenerationException.class)
                .hasMessageContaining("failed to parse question JSON");
    }

    @Test
    void throwsWhenModelReturnsNonJsonPayload() {
        when(adaptiveDifficultyService.resolveDifficulty("sql")).thenReturn(Difficulty.EASY);
        when(aiQuestionClient.generateStructuredJson(anyString())).thenReturn(Optional.of("not-json"));

        assertThatThrownBy(() -> service.generateQuestion("sql", QuestionType.CONCEPT))
                .isInstanceOf(AiGenerationException.class)
                .hasMessageContaining("failed to parse question JSON");
    }

    private String validQuestionJson() {
        return """
                {
                  "question":"Почему HashMap может терять производительность при плохом hashCode?",
                  "options":[
                    {"text":"Из-за роста числа коллизий и длинных цепочек поиска","correct":true},
                    {"text":"Потому что HashMap автоматически сортирует ключи по compareTo","correct":false},
                    {"text":"Потому что hashCode используется только при удалении, а не при поиске","correct":false},
                    {"text":"Потому что equals полностью заменяет hashCode в выборе bucket","correct":false}
                  ],
                  "explanation":"Плохой hashCode увеличивает коллизии и глубину поиска, поэтому операции в среднем становятся дороже."
                }
                """;
    }
}
