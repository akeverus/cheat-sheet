package com.cheatsheet.quiz.service.ai;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class QuestionQualityScorerTest {

    private final QuestionQualityScorer scorer = new QuestionQualityScorer();

    @Test
    void scoreReturnsHundredForEmptyViolations() {
        assertThat(scorer.score(List.of())).isEqualTo(100);
    }

    @Test
    void scoreAppliesPenaltyWeightsForKnownViolations() {
        int score = scorer.score(List.of(
                "Exactly one correct option is required",
                "Question text is trivial and does not require technical reasoning",
                "Topic is required"
        ));

        assertThat(score).isEqualTo(30);
    }
}
