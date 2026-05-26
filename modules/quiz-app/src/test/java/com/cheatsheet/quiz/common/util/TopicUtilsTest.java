package com.cheatsheet.quiz.common.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class TopicUtilsTest {

    private final TopicUtils utils = new TopicUtils();

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void displayNameReturnsEmptyForBlank(String input) {
        assertThat(utils.displayName(input)).isEmpty();
    }

    @ParameterizedTest
    @CsvSource({
            "java/collections-interview.md, collections",
            "java/collections-interview, collections",
            "java/collections.md, collections",
            "collections-interview, collections",
            "collections, collections",
            "ai-ml/rag-interview.md, rag",
            "deep/nested/path/topic-interview.md, topic"
    })
    void displayNameStripsPrefixAndSuffix(String input, String expected) {
        assertThat(utils.displayName(input)).isEqualTo(expected);
    }

    @Test
    void displayNameKeepsTrailingSlashLiterally() {
        // Документирует текущее поведение: slug с trailing slash не «зачёркивается»
        // (для реальных тем такой кейс не встречается, но тест фиксирует контракт).
        assertThat(utils.displayName("category/")).isEqualTo("category/");
    }
}
