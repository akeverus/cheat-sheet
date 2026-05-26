package com.cheatsheet.quiz.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class OptionSourceTest {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t"})
    void blankOrNullDefaultsToOpenai(String input) {
        assertThat(OptionSource.fromString(input)).isEqualTo(OptionSource.OPENAI);
    }

    @ParameterizedTest
    @CsvSource({
            "OPENAI, OPENAI",
            "openai, OPENAI",
            "OpenAI, OPENAI",
            "DEEPSEEK, DEEPSEEK",
            "deepseek, DEEPSEEK",
            "CLAUDE, CLAUDE",
            "MARKDOWN, MARKDOWN",
            "  openai  , OPENAI",
            "open-ai, OPEN_AI"
    })
    void recognizesKnownValuesCaseInsensitiveAndStrips(String input, String expected) {
        // open-ai → OPEN_AI после normalization, но OPEN_AI не в enum — упадёт в default OPENAI.
        OptionSource source = OptionSource.fromString(input);
        OptionSource expectedSource;
        try {
            expectedSource = OptionSource.valueOf(expected);
        } catch (IllegalArgumentException ignored) {
            expectedSource = OptionSource.OPENAI;
        }
        assertThat(source).isEqualTo(expectedSource);
    }

    @Test
    void unknownValueDefaultsToOpenai() {
        assertThat(OptionSource.fromString("MISTRAL")).isEqualTo(OptionSource.OPENAI);
        assertThat(OptionSource.fromString("GEMINI")).isEqualTo(OptionSource.OPENAI);
        assertThat(OptionSource.fromString("random-text")).isEqualTo(OptionSource.OPENAI);
    }
}
