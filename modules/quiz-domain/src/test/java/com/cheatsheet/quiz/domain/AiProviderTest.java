package com.cheatsheet.quiz.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты парсинга {@link AiProvider#fromString(String)}.
 */
class AiProviderTest {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void fromStringReturnsOpenAiForNullOrBlank(String input) {
        assertThat(AiProvider.fromString(input)).isEqualTo(AiProvider.OPENAI);
    }

    @Test
    void fromStringParsesValidValues() {
        assertThat(AiProvider.fromString("openai")).isEqualTo(AiProvider.OPENAI);
        assertThat(AiProvider.fromString("DEEPSEEK")).isEqualTo(AiProvider.DEEPSEEK);
    }

    @Test
    void fromStringHandlesChatGptAlias() {
        assertThat(AiProvider.fromString("chatgpt")).isEqualTo(AiProvider.OPENAI);
        assertThat(AiProvider.fromString("CHATGPT")).isEqualTo(AiProvider.OPENAI);
    }

    @Test
    void fromStringFallsBackToOpenAiForUnknown() {
        assertThat(AiProvider.fromString("unknown_provider")).isEqualTo(AiProvider.OPENAI);
    }
}
