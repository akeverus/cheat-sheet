package com.cheatsheet.quiz.service.ai;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
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

    @Test
    void createPerformsDefensiveCopyForMutableInputs() {
        ArrayList<String> violations = new ArrayList<>(List.of("v1"));
        ArrayList<String> retryFeedback = new ArrayList<>(List.of("r1"));

        QuestionQualitySnapshot snapshot = factory.create(violations, retryFeedback, 90, true);
        violations.add("v2");
        retryFeedback.add("r2");

        assertThat(snapshot.getViolations()).containsExactly("v1");
        assertThat(snapshot.getRetryFeedback()).containsExactly("r1");
    }

    @Test
    void createReturnsEmptyCollectionsForNullInputs() {
        QuestionQualitySnapshot snapshot = factory.create(null, null, 70, false);

        assertThat(snapshot.getViolations()).isEmpty();
        assertThat(snapshot.getRetryFeedback()).isEmpty();
    }

    @Test
    void createClampsScoreToAllowedRange() {
        QuestionQualitySnapshot low = factory.create(List.of(), List.of(), -15, false);
        QuestionQualitySnapshot high = factory.create(List.of(), List.of(), 150, true);

        assertThat(low.getScore()).isEqualTo(0);
        assertThat(high.getScore()).isEqualTo(100);
    }
}
