package com.cheatsheet.quiz.service.ai;

import com.cheatsheet.quiz.config.AppProperties;
import com.cheatsheet.quiz.domain.OptionSource;
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
    void generateTakeawayUsesNoRetryAndParsesPayload() {
        client.enqueue("{\"takeaway\":\"Фокус на идемпотентности\"}");

        Optional<String> takeaway = client.generateTakeaway("Что такое retry?", "Ответ...");

        assertThat(takeaway).contains("Фокус на идемпотентности");
        assertThat(client.lastWithRetry()).isFalse();
        assertThat(client.lastTimeout()).isEqualTo(Duration.ofSeconds(12));
    }

    @Test
    void generateHintsUsesRetryAndParsesNonEmptyHints() {
        client.enqueue("{\"hints\":[\"Подумай о транзакции\",\"Проверь isolation level\"]}");

        Optional<List<String>> hints = client.generateHints("Q", "A", List.of("x"));

        assertThat(hints).isPresent();
        assertThat(hints.get()).containsExactly("Подумай о транзакции", "Проверь isolation level");
        assertThat(client.lastWithRetry()).isTrue();
    }

    @Test
    void generateAlternativeQuestionsSkipsOutOfRangeCountWithoutAiCall() {
        Optional<List<String>> result = client.generateAlternativeQuestions("Q", "A", 0);

        assertThat(result).isEmpty();
        assertThat(client.calls()).isZero();
    }

    private static final class TestAiClient extends AbstractAiClient {
        private final AppProperties.Ai aiConfig;
        private final ObjectMapper objectMapper = new ObjectMapper();
        private final Queue<Optional<String>> responses = new ArrayDeque<>();
        private Duration lastTimeout;
        private Boolean lastWithRetry;
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

        private Boolean lastWithRetry() {
            return lastWithRetry;
        }

        private int calls() {
            return calls;
        }

        @Override
        public Optional<String> sendChatRequest(ChatRequest request, Duration timeout, boolean withRetry) {
            this.lastTimeout = timeout;
            this.lastWithRetry = withRetry;
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
