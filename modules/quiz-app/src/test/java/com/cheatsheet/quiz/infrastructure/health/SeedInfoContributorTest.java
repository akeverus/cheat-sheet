package com.cheatsheet.quiz.infrastructure.health;

import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.info.Info;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class SeedInfoContributorTest {

    @Test
    void contributesSeedSummaryFromClasspath() {
        SeedInfoContributor contributor = new SeedInfoContributor();
        Info.Builder builder = new Info.Builder();

        contributor.contribute(builder);
        Map<String, Object> details = builder.build().getDetails();

        @SuppressWarnings("unchecked")
        Map<String, Object> seeds = (Map<String, Object>) details.get("seeds");
        assertThat(seeds).isNotNull();
        // На classpath лежит и main, и test seed дерево. Просто проверяем что
        // секция собралась и есть положительный total.
        assertThat((Integer) seeds.get("totalTopics")).isPositive();

        @SuppressWarnings("unchecked")
        Map<String, Integer> byCategory = (Map<String, Integer>) seeds.get("byCategory");
        assertThat(byCategory).isNotEmpty();
        // Хотя бы одна реальная production-категория должна попасть в выдачу.
        assertThat(byCategory.keySet()).anyMatch(k -> !k.equals("test"));
    }
}
