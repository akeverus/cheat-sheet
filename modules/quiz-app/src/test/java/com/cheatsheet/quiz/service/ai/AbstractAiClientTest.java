package com.cheatsheet.quiz.service.ai;

import com.cheatsheet.quiz.config.app.AppProperties;
import com.cheatsheet.quiz.domain.OptionSource;
import com.cheatsheet.quiz.service.ai.client.AbstractAiClient;
import com.cheatsheet.quiz.service.ai.dto.ChatRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThat;

class AbstractAiClientTest {

    private TestAiClient client;

    @BeforeEach
    void setUp() {
        AppProperties.Ai ai = new AppProperties.Ai();
        ai.setTimeoutSeconds(12);
        client = new TestAiClient(ai);
    }

    @Test
    void generateTakeawayParsesPayload() {
        client.enqueue("{\"takeaway\":\"Фокус на идемпотентности\"}");

        Optional<String> takeaway = client.generateTakeaway("Что такое retry?", "Ответ...");

        assertThat(takeaway).contains("Фокус на идемпотентности");
        assertThat(client.lastTimeout()).isEqualTo(Duration.ofSeconds(12));
    }

    @Test
    void generateHintsParsesNonEmptyHints() {
        client.enqueue("{\"hints\":[\"Подумай о транзакции\",\"Проверь isolation level\"]}");

        Optional<List<String>> hints = client.generateHints("Q", "A", List.of("x"));

        assertThat(hints).isPresent();
        assertThat(hints.get()).containsExactly("Подумай о транзакции", "Проверь isolation level");
    }

    @Test
    void generateAlternativeQuestionsSkipsOutOfRangeCountWithoutAiCall() {
        Optional<List<String>> result = client.generateAlternativeQuestions("Q", "A", 0);

        assertThat(result).isEmpty();
        assertThat(client.calls()).isZero();
    }

    @Test
    void generateStructuredJsonReturnsContent() {
        client.enqueue("{\"questionText\":\"Q\",\"answerMarkdown\":\"A\"}");

        Optional<String> result = client.generateStructuredJson("Верни JSON");

        assertThat(result).contains("{\"questionText\":\"Q\",\"answerMarkdown\":\"A\"}");
        assertThat(client.lastTimeout()).isEqualTo(Duration.ofSeconds(12));
    }

    @Test
    void canonicalizeQuestionReturnsParsedPayload() {
        client.enqueue("""
                {"questionText":"Что такое DI?",
                 "answerMarkdown":"Внедрение зависимостей.",
                 "questionType":"TEXT",
                 "codeSnippet":null}
                """);

        Optional<com.cheatsheet.quiz.service.ai.AiQuestionClient.CanonicalQuestion> result =
                client.canonicalizeQuestion("Что такое DI", "DI это паттерн", "java/di.md");

        assertThat(result).isPresent();
        assertThat(result.get().questionText()).contains("DI");
        assertThat(result.get().answerMarkdown()).contains("Внедрение");
    }

    @Test
    void canonicalizeQuestionReturnsEmptyOnAiSilence() {
        // Ничего не enqueue — sendChatRequest вернёт Optional.empty()
        Optional<com.cheatsheet.quiz.service.ai.AiQuestionClient.CanonicalQuestion> result =
                client.canonicalizeQuestion("Q", "A", "p");
        assertThat(result).isEmpty();
    }

    @Test
    void generateDiagramReturnsMermaid() {
        client.enqueue("{\"needed\":true,\"mermaid\":\"sequenceDiagram\\n  A->>B: hi\"}");

        Optional<String> diagram = client.generateDiagram("Q", "A", "java");

        assertThat(diagram).hasValueSatisfying(s -> assertThat(s).contains("sequenceDiagram"));
    }

    @Test
    void generateDiagramReturnsEmptyWhenAiDeclines() {
        client.enqueue("{\"needed\":false}");

        Optional<String> diagram = client.generateDiagram("Q", "A", null);

        assertThat(diagram).isEmpty();
    }

    @Test
    void cleanCodeSnippetReturnsCleanedCode() {
        client.enqueue("```java\npublic class Demo { void run() { /* logic */ } }\n```");

        Optional<String> cleaned = client.cleanCodeSnippet(
                "public class Demo { void run() { System.out.println(\"hint\"); } }",
                "Что выведет код?");

        assertThat(cleaned).isPresent();
        assertThat(cleaned.get()).contains("public class Demo");
        assertThat(cleaned.get()).doesNotContain("```");
    }

    @Test
    void cleanCodeSnippetReturnsEmptyForBlankInput() {
        Optional<String> cleaned = client.cleanCodeSnippet("", "Q");
        assertThat(cleaned).isEmpty();
        assertThat(client.calls()).isZero();
    }

    @Test
    void cleanCodeSnippetReturnsEmptyForTooShortResult() {
        client.enqueue("```\nx\n```"); // меньше 10 символов после очистки fences

        Optional<String> cleaned = client.cleanCodeSnippet("public class A {}", "Q");

        assertThat(cleaned).isEmpty();
    }

    @Test
    void generateWrongAnswerFeedbackReturnsContent() {
        client.enqueue("Этот вариант описывает другой паттерн.");

        Optional<String> feedback = client.generateWrongAnswerFeedback(
                "Q", "Wrong choice", "Right choice", "Answer markdown");

        assertThat(feedback).hasValueSatisfying(s -> assertThat(s).contains("другой паттерн"));
    }

    @Test
    void generateComparisonReturnsContent() {
        client.enqueue("Strong vs eventual: ...");

        Optional<String> comparison = client.generateComparison("Q", "Wrong", "Right");

        assertThat(comparison).hasValueSatisfying(s -> assertThat(s).contains("Strong"));
    }

    @Test
    void generateCodeTraceReturnsContent() {
        client.enqueue("Шаг 1: создаётся объект...");

        Optional<String> trace = client.generateCodeTrace("Q", "int x = 0; x++;");

        assertThat(trace).hasValueSatisfying(s -> assertThat(s).contains("Шаг 1"));
    }

    @Test
    void generateCodeTraceReturnsEmptyForBlankCode() {
        Optional<String> trace = client.generateCodeTrace("Q", "");
        assertThat(trace).isEmpty();
        assertThat(client.calls()).isZero();
    }

    @Test
    void generateStructuredJsonReturnsEmptyForBlankPrompt() {
        Optional<String> result = client.generateStructuredJson("");
        assertThat(result).isEmpty();
        assertThat(client.calls()).isZero();
    }

    @Test
    void allMethodsReturnEmptyWhenApiKeyMissing() {
        AppProperties.Ai ai = new AppProperties.Ai();
        ai.setTimeoutSeconds(12);
        TestAiClient noKey = new TestAiClient(ai) {
            @Override protected String apiKey() { return ""; }
        };
        // enqueue заведомо непустые ответы — должны быть проигнорированы
        noKey.enqueue("любой ответ");
        noKey.enqueue("ещё ответ");

        assertThat(noKey.canonicalizeQuestion("Q", "A", "p")).isEmpty();
        assertThat(noKey.generateDiagram("Q", "A", "topic")).isEmpty();
        assertThat(noKey.cleanCodeSnippet("public class A {}", "Q")).isEmpty();
        assertThat(noKey.generateWrongAnswerFeedback("Q", "w", "r", "a")).isEmpty();
        assertThat(noKey.generateTakeaway("Q", "A")).isEmpty();
        assertThat(noKey.generateComparison("Q", "w", "r")).isEmpty();
        assertThat(noKey.generateCodeTrace("Q", "code")).isEmpty();
        assertThat(noKey.generateStructuredJson("prompt")).isEmpty();
        assertThat(noKey.generateAlternativeQuestions("Q", "A", 3)).isEmpty();
        assertThat(noKey.generateHints("Q", "A", List.of())).isEmpty();
        assertThat(noKey.calls()).isZero();
    }

    @Test
    void generateOptionsSupportsPercentSignsInPromptTemplate() {
        client.enqueue("""
                {"question":"Как сравнить алгоритмы?",
                "options":[{"text":"Верный вариант про сравнение алгоритмов.","correct":true},
                {"text":"Неверный вариант 1.","correct":false},
                {"text":"Неверный вариант 2.","correct":false},
                {"text":"Неверный вариант 3.","correct":false}]}
                """);

        assertThatCode(() -> client.generateOptions("Как сравнить алгоритмы?", "Сравнить по метрикам."))
                .doesNotThrowAnyException();
    }

    private static class TestAiClient extends AbstractAiClient {
        private final AppProperties.Ai aiConfig;
        private final ObjectMapper objectMapper = new ObjectMapper();
        private final Queue<Optional<String>> responses = new ArrayDeque<>();
        private Duration lastTimeout;
        private int calls;

        private TestAiClient(AppProperties.Ai aiConfig) {
            this.aiConfig = aiConfig;
        }

        private void enqueue(String content) {
            responses.add(Optional.ofNullable(content));
        }

        private Duration lastTimeout() {
            return lastTimeout;
        }

        private int calls() {
            return calls;
        }

        @Override
        public Optional<String> sendChatRequest(ChatRequest request, Duration timeout) {
            this.lastTimeout = timeout;
            this.calls++;
            return responses.isEmpty() ? Optional.empty() : responses.poll();
        }

        @Override
        public OptionSource sourceId() {
            return OptionSource.OPENAI;
        }

        @Override
        protected WebClient webClient() {
            return WebClient.builder().baseUrl("http://localhost").build();
        }

        @Override
        protected String apiKey() {
            return "test-key";
        }

        @Override
        protected String model() {
            return "test-model";
        }

        @Override
        protected String endpoint() {
            return "/v1/chat/completions";
        }

        @Override
        protected double temperature() {
            return 0.2;
        }

        @Override
        protected AppProperties.Ai aiConfig() {
            return aiConfig;
        }

        @Override
        protected ObjectMapper objectMapper() {
            return objectMapper;
        }
    }
}
