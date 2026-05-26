package com.cheatsheet.quiz.infrastructure.health;

import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
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
    void lowCoverageReportsDown() {
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM questions"), eq(Integer.class)))
                .thenReturn(100);
        when(jdbc.queryForObject(startsWith("SELECT COUNT(DISTINCT question_id)"), eq(Integer.class)))
                .thenReturn(20);

        Health health = indicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails()).containsEntry("questionsTotal", 100);
        assertThat(health.getDetails()).containsEntry("questionsWithOptions", 20);
        assertThat(health.getDetails()).containsEntry("coverageRatio", 0.2);
    }

    @Test
    void exactlyAtThresholdReportsUp() {
        when(jdbc.queryForObject(startsWith("SELECT COUNT(*) FROM questions"), eq(Integer.class)))
                .thenReturn(10);
        when(jdbc.queryForObject(startsWith("SELECT COUNT(DISTINCT question_id)"), eq(Integer.class)))
                .thenReturn(5);

        Health health = indicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.UP);
        assertThat(health.getDetails()).containsEntry("coverageRatio", 0.5);
    }
}
