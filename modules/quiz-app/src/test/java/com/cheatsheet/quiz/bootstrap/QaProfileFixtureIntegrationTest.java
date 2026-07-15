package com.cheatsheet.quiz.bootstrap;

import com.cheatsheet.quiz.TestInterviewPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.Clock;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Контракт профиля {@code qa}: контекст поднимается, {@link Clock} зафиксирован,
 * а {@code QaFixtureRunner} засеял детерминированный набор due/learned/new.
 *
 * <p>Активирует {@code test} (Testcontainers datasource) + {@code qa}
 * (фиксированный Clock + фикстуры) — так qa-инфраструктура проверяется полностью
 * без запуска браузера.</p>
 */
@SpringBootTest
@ActiveProfiles({"test", "qa"})
class QaProfileFixtureIntegrationTest {

    private static final Instant FIXED = Instant.parse("2026-07-15T12:00:00Z");

    @DynamicPropertySource
    static void setProps(DynamicPropertyRegistry registry) {
        TestInterviewPath.register(registry);
    }

    @Autowired
    Clock clock;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void qaProfileProvidesFixedClock() {
        // @Primary qa-бин перекрывает Clock.systemUTC() из InfrastructureConfig.
        assertThat(clock.instant()).isEqualTo(FIXED);
    }

    @Test
    void qaFixturesSeedDeterministicDueLearnedNew() {
        long nowEpoch = FIXED.getEpochSecond();

        Long due = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM review_state WHERE next_review_at <= ?", Long.class, nowEpoch);
        Long learned = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM review_state WHERE next_review_at > ?", Long.class, nowEpoch);
        Long totalQuestions = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM questions", Long.class);

        // application-qa.yml: due-fixtures=2, learned-fixtures=1.
        assertThat(due).isEqualTo(2L);
        assertThat(learned).isEqualTo(1L);
        // Остальные вопросы — новые (без строки review_state).
        assertThat(totalQuestions).isGreaterThanOrEqualTo(3L);
        long newCount = totalQuestions - due - learned;
        assertThat(newCount).isEqualTo(totalQuestions - 3L);
    }

    @Test
    void qaFixturesClearProgressTables() {
        // Сидер владеет состоянием прогресса → зависящие таблицы чистые и детерминированные.
        Long activity = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM daily_activity", Long.class);
        Long topicStats = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM user_topic_stats", Long.class);
        assertThat(activity).isZero();
        assertThat(topicStats).isZero();
    }
}
