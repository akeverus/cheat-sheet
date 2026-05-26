package com.cheatsheet.quiz.llm;

import com.cheatsheet.quiz.service.ai.dto.ChatRequest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LlmRequestBuilderTest {

    @Test
    void buildsBasicRequestWithSystemAndUserMessages() {
        ChatRequest request = LlmRequestBuilder.build(
                "gpt-4o-mini",
                "You are a strict reviewer.",
                "Critique this code.",
                0.0
        );

        assertThat(request.model()).isEqualTo("gpt-4o-mini");
        assertThat(request.messages()).hasSize(2);
        assertThat(request.messages().get(0).role()).isEqualTo("system");
        assertThat(request.messages().get(0).content()).isEqualTo("You are a strict reviewer.");
        assertThat(request.messages().get(1).role()).isEqualTo("user");
        assertThat(request.messages().get(1).content()).isEqualTo("Critique this code.");
        assertThat(request.temperature()).isEqualTo(0.0);
        assertThat(request.max_tokens()).isNull();
    }

    @Test
    void buildsRequestWithMaxTokens() {
        ChatRequest request = LlmRequestBuilder.buildWithMaxTokens(
                "gpt-4o-mini",
                "system",
                "user",
                0.2,
                1000
        );

        assertThat(request.model()).isEqualTo("gpt-4o-mini");
        assertThat(request.messages()).hasSize(2);
        assertThat(request.max_tokens()).isEqualTo(1000);
    }

    @Test
    void withStrictJsonContractAppendsRulesToPrompt() {
        String prompt = "Generate question JSON";

        String result = LlmRequestBuilder.withStrictJsonContract(prompt);

        assertThat(result).contains("Generate question JSON");
        assertThat(result).contains("Return strictly one valid JSON object.");
        assertThat(result).contains("Do not add markdown fences, comments, or any extra text.");
    }

    @Test
    void withStrictJsonContractReturnsOnlyContractForBlankPrompt() {
        String result = LlmRequestBuilder.withStrictJsonContract("   ");

        assertThat(result).contains("IMPORTANT:");
        assertThat(result).contains("Return strictly one valid JSON object.");
        assertThat(result).doesNotContain("Generate question JSON");
    }
}
