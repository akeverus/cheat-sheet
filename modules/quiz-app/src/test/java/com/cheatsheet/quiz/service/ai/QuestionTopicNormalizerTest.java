package com.cheatsheet.quiz.service.ai;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class QuestionTopicNormalizerTest {

    private final QuestionTopicNormalizer normalizer = new QuestionTopicNormalizer();

    @Test
    void normalizeReturnsGeneralForNullOrBlank() {
        assertThat(normalizer.normalize(null)).isEqualTo("general");
        assertThat(normalizer.normalize("")).isEqualTo("general");
        assertThat(normalizer.normalize("   ")).isEqualTo("general");
    }

    @Test
    void normalizeTrimsTopicValue() {
        assertThat(normalizer.normalize("  java-core  ")).isEqualTo("java-core");
    }
}
