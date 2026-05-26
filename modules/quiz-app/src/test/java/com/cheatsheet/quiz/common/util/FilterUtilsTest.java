package com.cheatsheet.quiz.common.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class FilterUtilsTest {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   ", "\t"})
    void normalizeTopicReturnsNullForBlank(String input) {
        assertThat(FilterUtils.normalizeTopic(input)).isNull();
    }

    @ParameterizedTest
    @CsvSource({
            "java, java",
            "' java ', java",
            "'  java/collections  ', java/collections"
    })
    void normalizeTopicTrimsAndKeepsValue(String input, String expected) {
        assertThat(FilterUtils.normalizeTopic(input)).isEqualTo(expected);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void normalizeGroupReturnsNullForBlank(String input) {
        assertThat(FilterUtils.normalizeGroup(input)).isNull();
    }

    @ParameterizedTest
    @CsvSource({
            "ai-ml, ai-ml",
            "' interview ', interview"
    })
    void normalizeGroupTrimsAndKeepsValue(String input, String expected) {
        assertThat(FilterUtils.normalizeGroup(input)).isEqualTo(expected);
    }
}
