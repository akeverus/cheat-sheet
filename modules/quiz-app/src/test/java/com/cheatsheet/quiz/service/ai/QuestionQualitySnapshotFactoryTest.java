package com.cheatsheet.quiz.service.ai;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class QuestionQualitySnapshotFactoryTest {

    private final QuestionQualitySnapshotFactory factory = new QuestionQualitySnapshotFactory();

    @Test
    void createBuildsSnapshotWithProvidedValues() {
        List<String> violations = List.of("Topic is required");
        List<String> retryFeedback = List.of("Topic is required");

        QuestionQualityEvaluator.QualitySnapshot snapshot =
                factory.create(violations, retryFeedback, 85, false);

        assertThat(snapshot.violations()).isEqualTo(violations);
        assertThat(snapshot.retryFeedback()).isEqualTo(retryFeedback);
        assertThat(snapshot.score()).isEqualTo(85);
        assertThat(snapshot.accepted()).isFalse();
    }
}
