package com.cheatsheet.quiz.infrastructure.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Health-indicator, отражающий долю вопросов, для которых в базу загружены
 * варианты ответа (через JSON-сидеры). Если seed-импорт молча сломался —
 * coverage упадёт ниже порога и общий статус /actuator/health станет DOWN.
 *
 * <p>Пороги:
 * <ul>
 *   <li>{@code total == 0} → UP с пометкой «no questions imported yet»
 *       (валидное состояние для свежего пустого контейнера).</li>
 *   <li>{@code coverageRatio &lt; 0.5} → DOWN.</li>
 *   <li>иначе → UP с деталями.</li>
 * </ul>
 *
 * <p>Доступен как {@code /actuator/health/seedCoverage} при
 * {@code management.endpoint.health.show-details=always}; без show-details
 * влияет только на агрегированный статус.
 */
@Component("seedCoverage")
public class SeedCoverageHealthIndicator implements HealthIndicator {

    private static final double MIN_COVERAGE_RATIO = 0.5;

    private final JdbcTemplate jdbcTemplate;

    public SeedCoverageHealthIndicator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Health health() {
        Integer total = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM questions", Integer.class);
        Integer withOptions = jdbcTemplate.queryForObject(
                "SELECT COUNT(DISTINCT question_id) FROM answer_options", Integer.class);
        int totalCount = total == null ? 0 : total;
        int withOptionsCount = withOptions == null ? 0 : withOptions;

        if (totalCount == 0) {
            return Health.up()
                    .withDetail("questionsTotal", 0)
                    .withDetail("note", "no questions imported yet")
                    .build();
        }

        double ratio = (double) withOptionsCount / totalCount;
        Health.Builder builder = ratio < MIN_COVERAGE_RATIO ? Health.down() : Health.up();
        return builder
                .withDetail("questionsTotal", totalCount)
                .withDetail("questionsWithOptions", withOptionsCount)
                .withDetail("coverageRatio", round2(ratio))
                .withDetail("minRequiredRatio", MIN_COVERAGE_RATIO)
                .build();
    }

    private static double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
