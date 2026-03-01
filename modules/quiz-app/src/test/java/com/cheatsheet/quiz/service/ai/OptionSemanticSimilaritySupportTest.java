package com.cheatsheet.quiz.service.ai;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class OptionSemanticSimilaritySupportTest {

    @Test
    void computesJaccardAndMinCoverageForOverlappingSets() {
        Set<String> left = Set.of("spring", "transaction", "rollback", "proxy");
        Set<String> right = Set.of("spring", "proxy", "aop");

        OptionSemanticSimilaritySupport.SimilarityMetrics metrics =
                OptionSemanticSimilaritySupport.similarityMetrics(left, right);

        assertThat(metrics.intersection()).isEqualTo(2);
        assertThat(metrics.union()).isEqualTo(5);
        assertThat(metrics.jaccard()).isEqualTo(0.4);
        assertThat(metrics.minCoverage()).isEqualTo(2.0 / 3.0);
    }

    @Test
    void returnsEmptyMetricsForMissingTokens() {
        OptionSemanticSimilaritySupport.SimilarityMetrics metrics =
                OptionSemanticSimilaritySupport.similarityMetrics(Set.of(), Set.of("x"));

        assertThat(metrics.intersection()).isZero();
        assertThat(metrics.union()).isZero();
        assertThat(metrics.jaccard()).isZero();
        assertThat(metrics.minCoverage()).isZero();
    }
}
