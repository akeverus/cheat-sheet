package com.cheatsheet.quiz.feature.interview.usecase.stats;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MetricDefinitionsTest {

    @Test
    void catalogContainsHonestDenominatorsForCoreKpis() {
        var catalog = MetricDefinitions.catalog();

        assertThat(catalog).containsKeys(
                "overallAccuracy", "firstAttemptAccuracy", "cardsMastered", "dueNow", "averageResponseMs");
        assertThat(catalog.get("overallAccuracy").denominator()).isEqualTo("nonSkipAttempts");
        assertThat(catalog.get("firstAttemptAccuracy").denominator()).isEqualTo("firstAttempts");
        assertThat(catalog.get("overallAccuracy").period()).isNotBlank();
        assertThat(catalog.get("retention7d").period()).isEqualTo("7d");
    }
}
