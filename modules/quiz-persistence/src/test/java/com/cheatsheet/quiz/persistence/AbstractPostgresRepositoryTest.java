package com.cheatsheet.quiz.persistence;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;

/**
 * База для unit-тестов JdbcTemplate-репозиториев.
 *
 * <p>Поднимает один shared PostgreSQL-контейнер на JVM (reuse). Перед каждым
 * тестом таблицы чистятся через TRUNCATE … RESTART IDENTITY CASCADE — это в
 * разы быстрее пересоздания контейнера и сохраняет схему между прогонами.</p>
 */
@Testcontainers(disabledWithoutDocker = true)
public abstract class AbstractPostgresRepositoryTest {

    @SuppressWarnings("resource")
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("interview_test")
            .withUsername("interview")
            .withPassword("interview")
            .withReuse(true);

    private static boolean schemaApplied;

    protected DataSource dataSource;
    protected JdbcTemplate jdbcTemplate;

    @BeforeEach
    void initSchemaAndDataSource() {
        if (!POSTGRES.isRunning()) {
            POSTGRES.start();
        }
        dataSource = new SingleConnectionDataSource(
                POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword(), true);
        jdbcTemplate = new JdbcTemplate(dataSource);

        if (!schemaApplied) {
            Flyway.configure()
                    .dataSource(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword())
                    .locations("classpath:db/migration")
                    .load()
                    .migrate();
            schemaApplied = true;
        }
        // Чистим данные между тестами, схему оставляем. Список таблиц = всё что создаёт V*.sql.
        jdbcTemplate.execute(
                "TRUNCATE TABLE answer_options, question_hints, daily_activity, " +
                        "user_topic_stats, review_state, paused_session, question_issue, questions RESTART IDENTITY CASCADE");
    }
}
