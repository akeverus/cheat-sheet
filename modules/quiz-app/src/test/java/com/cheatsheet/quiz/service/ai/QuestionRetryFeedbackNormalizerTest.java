package com.cheatsheet.quiz.service.ai;

import com.cheatsheet.quiz.config.AppProperties;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class QuestionRetryFeedbackNormalizerTest {

    @Test
    void normalizeMakesFeedbackDeterministicAndUnique() {
        AppProperties appProperties = new AppProperties();
        appProperties.getInterview().setQuestionRetryFeedbackMaxItems(10);
        QuestionRetryFeedbackNormalizer normalizer = new QuestionRetryFeedbackNormalizer(appProperties);

        List<String> normalized = normalizer.normalize(List.of(
                "  Duplicate option text found ",
                "shortExplanation must be at least 30 characters",
                "duplicate option text found",
                "",
                "  "
        ));

        assertThat(normalized).containsExactly(
                "Duplicate option text found",
                "shortExplanation must be at least 30 characters"
        );
    }

    @Test
    void normalizeAppliesConfiguredLimit() {
        AppProperties appProperties = new AppProperties();
        appProperties.getInterview().setQuestionRetryFeedbackMaxItems(2);
        QuestionRetryFeedbackNormalizer normalizer = new QuestionRetryFeedbackNormalizer(appProperties);

        List<String> normalized = normalizer.normalize(List.of(
                "z item",
                "a item",
                "m item"
        ));

        assertThat(normalized).containsExactly("a item", "m item");
    }
}
