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

    /**
     * Формула `dueNow` печатается в карточке «Очередь» прямо под числом, а число
     * включает вопросы без расписания. Пока формула говорила только про «срок
     * повторения наступил», подпись противоречила соседней строке «N к повтору ·
     * M новых» — и на пустом прогрессе обещала повторить весь банк.
     */
    @Test
    void dueNowFormulaNamesBothHalvesOfTheQueue() {
        var catalog = MetricDefinitions.catalog();

        assertThat(catalog.get("dueNow").formula())
                .contains("срок повторения наступил")
                .contains("не открывали");
        assertThat(catalog).containsKey("dueFresh");
        assertThat(catalog.get("dueFresh").denominator()).isEqualTo("dueNow");
    }
}
