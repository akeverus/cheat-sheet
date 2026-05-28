package com.cheatsheet.quiz.infrastructure.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Health-indicator, отражающий долю вопросов, для которых в базу загружены
 * варианты ответа (через JSON-сидеры). Назначение — поймать <b>катастрофический
 * сбой seed-импорта</b> (сидеры вообще не загрузились), а не неполноту контента.
 *
 * <p>Порог намеренно низкий ({@value #MIN_COVERAGE_RATIO}): проект мигрирует на
 * v3-сидеры постепенно, и значительная доля вопросов осознанно остаётся
 * flashcard-only (без MCQ). Высокий порог давал бы постоянный false-alarm на
 * здоровой in-progress миграции. DOWN означает «импорт реально сломан»
 * (почти ноль вариантов), а не «контент ещё не дозалит».
 *
 * <p>Этот индикатор НЕ входит в readiness-группу (см. application.yml): он не
 * гейтит трафик, а служит сигналом мониторинга в агрегатном
 * {@code /actuator/health}. Приложение обслуживает запросы во flashcard-режиме
 * при любом coverage.
 *
 * <p>Пороги:
 * <ul>
 *   <li>{@code total == 0} → UP с пометкой «no questions imported yet»
 *       (валидное состояние для свежего пустого контейнера).</li>
 *   <li>{@code coverageRatio &lt; }{@value #MIN_COVERAGE_RATIO} → DOWN
 *       (катастрофический сбой импорта).</li>
 *   <li>иначе → UP с деталями.</li>
 * </ul>
 *
 * <p>Доступен как {@code /actuator/health/seedCoverage} при
 * {@code management.endpoint.health.show-details=always}; без show-details
 * влияет только на агрегированный статус.
 */
@Component("seedCoverage")
public class SeedCoverageHealthIndicator implements HealthIndicator {

    private static final double MIN_COVERAGE_RATIO = 0.05;

    private final JdbcTemplate jdbcTemplate;

    public SeedCoverageHealthIndicator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Health health() {
        int totalCount;
        int withOptionsCount;
        try {
            Integer total = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM questions", Integer.class);
            Integer withOptions = jdbcTemplate.queryForObject(
                    "SELECT COUNT(DISTINCT question_id) FROM answer_options", Integer.class);
            totalCount = total == null ? 0 : total;
            withOptionsCount = withOptions == null ? 0 : withOptions;
        } catch (DataAccessException e) {
            // БД недоступна — отдаём DOWN, не палим stack trace. Сам Spring `db`
            // indicator уже скажет почему БД лежит; мы лишь говорим, что
            // seed-coverage невозможно посчитать.
            return Health.down()
                    .withDetail("reason", "database query failed")
                    .withDetail("error", e.getClass().getSimpleName())
                    .build();
        }

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
