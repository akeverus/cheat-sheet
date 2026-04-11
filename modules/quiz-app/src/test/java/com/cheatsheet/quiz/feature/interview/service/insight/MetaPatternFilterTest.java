package com.cheatsheet.quiz.feature.interview.service.insight;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class MetaPatternFilterTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "На интервью ожидают, что вы назовёте критерии выбора.",
            "На интервью это обычно усиливают примерами из продакшена.",
            "на интервью обычно спрашивают про это.",
            "Практическая ценность ответа обычно повышается.",
            "Практический акцент здесь обычно важнее формальной точности."
    })
    void detectsMetaPatterns(String text) {
        assertThat(MetaPatternFilter.containsMetaPattern(text)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "HashMap деградирует при коллизиях hashCode.",
            "volatile гарантирует видимость, но не атомарность.",
            "Используйте SELECT ... FOR UPDATE для pessimistic locking.",
            "На практике это встречается редко."
    })
    void allowsCleanTechnicalText(String text) {
        assertThat(MetaPatternFilter.containsMetaPattern(text)).isFalse();
    }

    @ParameterizedTest
    @NullAndEmptySource
    void returnsFalseForNullAndBlank(String text) {
        assertThat(MetaPatternFilter.containsMetaPattern(text)).isFalse();
    }

    @Test
    void returnsFalseForBlankString() {
        assertThat(MetaPatternFilter.containsMetaPattern("   ")).isFalse();
    }
}
