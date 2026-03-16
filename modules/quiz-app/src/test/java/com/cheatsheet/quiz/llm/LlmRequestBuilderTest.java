package com.cheatsheet.quiz.llm;

import com.cheatsheet.quiz.service.ai.dto.ChatRequest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LlmRequestBuilderTest {

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
