package com.cheatsheet.quiz.infrastructure.health;

import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SeedCoverageHealthIndicatorTest {

    private final JdbcTemplate jdbc = mock(JdbcTemplate.class);
    private final SeedCoverageHealthIndicator indicator = new SeedCoverageHealthIndicator(jdbc);

    @Test
    void emptyDatabaseReportsUpWithZeroCount() {
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM questions"), eq(Integer.class)))
                .thenReturn(0);
        when(jdbc.queryForObject(startsWith("SELECT COUNT(DISTINCT question_id)"), eq(Integer.class)))
                .thenReturn(0);

        Health health = indicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.UP);
        assertThat(health.getDetails()).containsEntry("questionsTotal", 0);
        assertThat(health.getDetails()).containsEntry("note", "no questions imported yet");
    }

    @Test
    void highCoverageReportsUpWithRatio() {
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM questions"), eq(Integer.class)))
                .thenReturn(100);
        when(jdbc.queryForObject(startsWith("SELECT COUNT(DISTINCT question_id)"), eq(Integer.class)))
                .thenReturn(80);

        Health health = indicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.UP);
        assertThat(health.getDetails()).containsEntry("questionsTotal", 100);
        assertThat(health.getDetails()).containsEntry("questionsWithOptions", 80);
        assertThat(health.getDetails()).containsEntry("coverageRatio", 0.8);
    }

    @Test
    void catastrophicImportFailureReportsDown() {
        // 2% покрытия — сидеры практически не загрузились (реальный сбой импорта),
        // а не штатная in-progress миграция. Индикатор обязан дать DOWN.
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM questions"), eq(Integer.class)))
                .thenReturn(100);
        when(jdbc.queryForObject(startsWith("SELECT COUNT(DISTINCT question_id)"), eq(Integer.class)))
                .thenReturn(2);

        Health health = indicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails()).containsEntry("questionsTotal", 100);
        assertThat(health.getDetails()).containsEntry("questionsWithOptions", 2);
        assertThat(health.getDetails()).containsEntry("coverageRatio", 0.02);
    }

    @Test
    void partialMigrationCoverageReportsUp() {
        // ~40% покрытия — текущая реальность in-progress миграции на v3-сидеры.
        // Это НЕ сбой: индикатор должен быть UP, чтобы не давать false-alarm и
        // не валить агрегатный /actuator/health на здоровой системе.
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM questions"), eq(Integer.class)))
                .thenReturn(100);
        when(jdbc.queryForObject(startsWith("SELECT COUNT(DISTINCT question_id)"), eq(Integer.class)))
                .thenReturn(40);

        Health health = indicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.UP);
        assertThat(health.getDetails()).containsEntry("coverageRatio", 0.4);
    }

    @Test
    void databaseFailureReportsDownInsteadOfThrowing() {
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM questions"), eq(Integer.class)))
                .thenThrow(new DataAccessResourceFailureException("connection refused"));

        Health health = indicator.health();

        // Контракт: если БД лежит, indicator возвращает DOWN с reason, а не
        // даёт исключению уйти наружу (иначе /actuator/health сломается целиком).
        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails()).containsEntry("reason", "database query failed");
        assertThat(health.getDetails()).containsEntry("error", "DataAccessResourceFailureException");
    }

    @Test
    void exactlyAtThresholdReportsUp() {
        // Ровно 5% (порог) — граница «не катастрофа»: ratio < 0.05 даёт DOWN,
        // равенство порогу остаётся UP.
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM questions"), eq(Integer.class)))
                .thenReturn(100);
        when(jdbc.queryForObject(startsWith("SELECT COUNT(DISTINCT question_id)"), eq(Integer.class)))
                .thenReturn(5);

        Health health = indicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.UP);
        assertThat(health.getDetails()).containsEntry("coverageRatio", 0.05);
    }
}
