package com.cheatsheet.quiz.service.ai;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class QuestionQualityMessageNormalizerTest {

    private final QuestionQualityMessageNormalizer normalizer = new QuestionQualityMessageNormalizer();

    @Test
    void normalizeReturnsEmptyForNullOrEmptyInput() {
        assertThat(normalizer.normalize(null)).isEmpty();
        assertThat(normalizer.normalize(List.of())).isEmpty();
    }

    @Test
    void normalizeTrimsRemovesBlankAndDeduplicatesIgnoringCase() {
        List<String> normalized = normalizer.normalize(List.of(
                "  Duplicate  ",
                "duplicate",
                "   ",
                "Another message",
                "ANOTHER MESSAGE"
        ));

        assertThat(normalized).containsExactly("Duplicate", "Another message");
    }
}
