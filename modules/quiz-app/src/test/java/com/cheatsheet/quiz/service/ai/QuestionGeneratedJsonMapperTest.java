package com.cheatsheet.quiz.service.ai;

import com.cheatsheet.quiz.config.app.AppProperties;
import com.cheatsheet.quiz.domain.Difficulty;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.feature.question.engine.mapper.QuestionGeneratedJsonMapper;
import com.cheatsheet.quiz.feature.question.engine.metadata.QuestionGeneratedMetadataSupplier;
import com.cheatsheet.quiz.feature.question.engine.metadata.QuestionGeneratedSlugFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class QuestionGeneratedJsonMapperTest {

    private final AppProperties appProperties = new AppProperties();

    private final QuestionGeneratedJsonMapper mapper = new QuestionGeneratedJsonMapper(
            new ObjectMapper(),
            new QuestionGeneratedSlugFactory(
                    Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneOffset.UTC)
            ),
            new QuestionGeneratedMetadataSupplier(appProperties)
    );

    @Test
    void mapParsesValidQuestionJson() {
        Optional<Question> mapped = mapper.map(validQuestionJson(), "java", QuestionType.CODE, Difficulty.HARD);

        assertThat(mapped).isPresent();
        Question question = mapped.orElseThrow();
        assertThat(question.topic()).isEqualTo("java");
        assertThat(question.slug()).isEqualTo("generated:1704067200000");
        assertThat(question.sourceSlug()).isEqualTo("generated");
        assertThat(question.filePath()).isEqualTo("generated");
        assertThat(question.sourceHash()).isEqualTo("generated");
        assertThat(question.type()).isEqualTo(QuestionType.CODE);
        assertThat(question.difficulty()).isEqualTo(Difficulty.HARD);
        assertThat(question.questionText()).contains("HashMap");
        assertThat(question.options()).hasSize(4);
        assertThat(question.options().stream().filter(option -> option.correct())).hasSize(1);
        assertThat(question.options().get(0).id()).isEqualTo("A");
        assertThat(question.options().get(1).id()).isEqualTo("B");
    }

    @Test
    void mapReturnsEmptyForInvalidJson() {
        Optional<Question> mapped = mapper.map("{not-valid-json", "java", QuestionType.CONCEPT, Difficulty.MEDIUM);

        assertThat(mapped).isEmpty();
    }

    @Test
    void mapReturnsEmptyWhenOptionsHaveLegacyStringFormat() {
        Optional<Question> mapped = mapper.map(jsonWithLegacyStringOptions(), "java", QuestionType.CONCEPT, Difficulty.MEDIUM);

        assertThat(mapped).isEmpty();
    }

    @Test
    void mapAcceptsModelPayloadWhenOptionsCountIsNotFour() {
        Optional<Question> mapped = mapper.map(jsonWithThreeOptions(), "java", QuestionType.CONCEPT, Difficulty.MEDIUM);

        assertThat(mapped).isPresent();
        assertThat(mapped.orElseThrow().options()).hasSize(3);
    }

    @Test
    void mapReturnsEmptyWhenOptionsArrayIsMissing() {
        Optional<Question> mapped = mapper.map(jsonWithoutOptions(), "java", QuestionType.CONCEPT, Difficulty.MEDIUM);

        assertThat(mapped).isEmpty();
    }

    @Test
    void mapReturnsEmptyWhenQuestionFieldIsMissing() {
        Optional<Question> mapped = mapper.map(jsonWithoutQuestionField(), "java", QuestionType.CONCEPT, Difficulty.MEDIUM);

        assertThat(mapped).isEmpty();
    }

    @Test
    void mapReturnsEmptyWhenExplanationFieldIsMissing() {
        Optional<Question> mapped = mapper.map(jsonWithoutExplanationField(), "java", QuestionType.CONCEPT, Difficulty.MEDIUM);

        assertThat(mapped).isEmpty();
    }

    private String validQuestionJson() {
        return """
                {
                  "question":"Почему HashMap может терять производительность при плохом hashCode?",
                  "options":[
                    {"text":"Из-за роста числа коллизий и длинных цепочек поиска","correct":true},
                    {"text":"Потому что HashMap автоматически сортирует ключи","correct":false},
                    {"text":"Потому что hashCode используется только при удалении","correct":false},
                    {"text":"Потому что equals заменяет выбор bucket","correct":false}
                  ],
                  "explanation":"Неравномерный hashCode ведет к коллизиям и росту числа сравнений внутри bucket."
                }
                """;
    }

    private String jsonWithLegacyStringOptions() {
        return """
                {
                  "question":"Почему важно учитывать коллизии в HashMap?",
                  "options":[
                    "A option",
                    "B option",
                    "C option",
                    "D option"
                  ],
                  "explanation":"Коллизии увеличивают стоимость операций и ухудшают прогнозируемость времени доступа."
                }
                """;
    }

    private String jsonWithThreeOptions() {
        return """
                {
                  "question":"Как работает Quick Sort?",
                  "options":[
                    {"text":"Quick Sort выбирает pivot и делит массив на две части.","correct":true},
                    {"text":"Quick Sort всегда требует O(n) дополнительной памяти.","correct":false},
                    {"text":"Quick Sort стабилен во всех реализациях.","correct":false}
                  ],
                  "explanation":"Quick Sort использует partition вокруг pivot и рекурсивную сортировку частей."
                }
                """;
    }

    private String jsonWithoutOptions() {
        return """
                {
                  "question":"Что делает HashMap при коллизиях?",
                  "explanation":"HashMap разрешает коллизии внутри bucket в зависимости от реализации."
                }
                """;
    }

    private String jsonWithoutQuestionField() {
        return """
                {
                  "options":[
                    {"text":"Опция A","correct":true},
                    {"text":"Опция B","correct":false}
                  ],
                  "explanation":"Техническое пояснение."
                }
                """;
    }

    private String jsonWithoutExplanationField() {
        return """
                {
                  "question":"Почему lock-free структура может давать лучший tail latency?",
                  "options":[
                    {"text":"Она уменьшает блокировки и конкуренцию между потоками.","correct":true},
                    {"text":"Она гарантирует отсутствие контекстных переключений ядра.","correct":false},
                    {"text":"Она всегда убирает необходимость в атомарных операциях.","correct":false},
                    {"text":"Она делает GC полностью детерминированным.","correct":false}
                  ]
                }
                """;
    }
}
