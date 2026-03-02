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

        QuestionQualitySnapshot snapshot =
                factory.create(violations, retryFeedback, 85, false);

        assertThat(snapshot.getViolations()).isEqualTo(violations);
        assertThat(snapshot.getRetryFeedback()).isEqualTo(retryFeedback);
        assertThat(snapshot.getScore()).isEqualTo(85);
        assertThat(snapshot.isAccepted()).isFalse();
    }
}
