package com.cheatsheet.quiz.service.ai;

import com.cheatsheet.quiz.service.ai.dto.GeneratedOptions;
import com.cheatsheet.quiz.service.ai.parser.AiResponseParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AiResponseParserTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void parseOptions_strictOptionsContract_parsesCorrectly() throws Exception {
        String json = """
                {"question":"What is HashMap?","options":[
                {"text":"Правильный ответ","correct":true},
                {"text":"Неверный 1","correct":false},
                {"text":"Неверный 2","correct":false},
                {"text":"Неверный 3","correct":false}]}
                """;
        GeneratedOptions opts = AiResponseParser.parseOptions(json, objectMapper);
        assertThat(opts.options()).hasSize(4);
        assertThat(opts.options().stream().filter(GeneratedOptions.GeneratedOption::correct)).hasSize(1);
        assertThat(opts.options().get(0).text()).isEqualTo("Правильный ответ");
    }

    @Test
    void parseOptions_usesFirstCorrectOptionWhenSeveralMarked() throws Exception {
        String json = """
                {"question":"Q","options":[
                {"text":"Первый правильный","correct":true},
                {"text":"Второй тоже помечен","correct":true},
                {"text":"Wrong 1","correct":false},
                {"text":"Wrong 2","correct":false}]}
                """;
        GeneratedOptions opts = AiResponseParser.parseOptions(json, objectMapper);
        assertThat(opts.options()).hasSize(4);
        assertThat(opts.options().stream().filter(GeneratedOptions.GeneratedOption::correct)).hasSize(2);
    }

    @Test
    void parseOptions_throwsForLegacyFormatWithoutOptionsArray() {
        String json = """
                {"correct":"Answer","wrong":["W1","W2","W3"]}
                """;
        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> AiResponseParser.parseOptions(json, objectMapper)
        );
    }

    @Test
    void parseOptions_acceptsEmptyOptionsArray() throws Exception {
        String json = """
                {"question":"Q","options":[]}
                """;
        GeneratedOptions opts = AiResponseParser.parseOptions(json, objectMapper);
        assertThat(opts.options()).isEmpty();
    }

    @Test
    void parseOptions_throwsWhenOptionsContainNonObjectItem() {
        String json = """
                {"question":"Q","options":["A","B"]}
                """;
        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> AiResponseParser.parseOptions(json, objectMapper)
        );
    }

    @Test
    void stripCodeFences_removesJsonFence() {
        String fenced = "```json\n{\"key\":\"value\"}\n```";
        String result = AiResponseParser.stripCodeFences(fenced);
        assertThat(result).isEqualTo("{\"key\":\"value\"}");
    }

    @Test
    void stripCodeFences_passesPlainJson() {
        String plain = "{\"key\":\"value\"}";
        assertThat(AiResponseParser.stripCodeFences(plain)).isEqualTo(plain);
    }

    @Test
    void parseCanonicalQuestion_supportsStrictFencedJson() throws Exception {
        String content = """
                ```json
                {"questionText":"Что такое volatile?","answerMarkdown":"`volatile` гарантирует видимость между потоками.","questionType":"TEXT","codeSnippet":null}
                ```
                """;

        var canonical = AiResponseParser.parseCanonicalQuestion(content, objectMapper);

        assertThat(canonical).isPresent();
        assertThat(canonical.get().questionText()).isEqualTo("Что такое volatile?");
        assertThat(canonical.get().questionType()).isEqualTo("TEXT");
        assertThat(canonical.get().codeSnippet()).isNull();
    }

    @Test
    void parseAlternativeQuestions_normalizesQuestionMark() throws Exception {
        String json = """
                {"questions":["Как работает HashMap","Что такое JVM?"]}
                """;

        var questions = AiResponseParser.parseAlternativeQuestions(json, objectMapper);

        assertThat(questions).containsExactly("Как работает HashMap", "Что такое JVM?");
    }

    @Test
    void parseAlternativeQuestions_keepsModelProvidedTextWithoutLocalFiltering() throws Exception {
        String json = """
                {"questions":["Какие есть подходы к кешированию?","Как работает TTL в cache-aside"]}
                """;

        var questions = AiResponseParser.parseAlternativeQuestions(json, objectMapper);

        assertThat(questions).containsExactly(
                "Какие есть подходы к кешированию?",
                "Как работает TTL в cache-aside"
        );
    }
}
