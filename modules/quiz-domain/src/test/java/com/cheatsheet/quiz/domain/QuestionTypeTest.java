package com.cheatsheet.quiz.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты {@link QuestionType#fromString(String)} — парсера типа вопроса. Фиксируют:
 * null/blank/неизвестное → {@link QuestionType#TEXT}, распознавание всех 6 значений
 * case-insensitive со strip, и тонкость — дефисы НЕ нормализуются (в отличие от
 * {@link OptionSource}), поэтому {@code "code-analysis"} → TEXT, а не CODE_ANALYSIS.
 */
class QuestionTypeTest {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t"})
    void blankOrNullDefaultsToText(String input) {
        assertThat(QuestionType.fromString(input)).isEqualTo(QuestionType.TEXT);
    }

    @ParameterizedTest
    @CsvSource({
            "CONCEPT, CONCEPT",
            "concept, CONCEPT",
            "CODE_ANALYSIS, CODE_ANALYSIS",
            "code_analysis, CODE_ANALYSIS",
            "DEBUGGING, DEBUGGING",
            "ARCHITECTURE, ARCHITECTURE",
            "TEXT, TEXT",
            "CODE, CODE",
            "  code  , CODE",
            "CoDe, CODE"
    })
    void recognizesKnownValuesCaseInsensitiveAndStripped(String input, QuestionType expected) {
        assertThat(QuestionType.fromString(input)).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(strings = {"MYSTERY", "123", "code analysis"})
    void unknownValueDefaultsToText(String input) {
        assertThat(QuestionType.fromString(input)).isEqualTo(QuestionType.TEXT);
    }

    @Test
    void hyphensAreNotNormalizedUnlikeOptionSource() {
        // Дефис НЕ заменяется на '_' → "code-analysis" не совпадает с CODE_ANALYSIS.
        assertThat(QuestionType.fromString("code-analysis")).isEqualTo(QuestionType.TEXT);
    }
}
