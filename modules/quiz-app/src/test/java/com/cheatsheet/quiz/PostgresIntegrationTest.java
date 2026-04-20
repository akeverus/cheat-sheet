package com.cheatsheet.quiz;

import static org.assertj.core.api.Assertions.assertThat;

import com.cheatsheet.quiz.infrastructure.search.SearchService;
import com.cheatsheet.quiz.service.SearchTestAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Smoke-тест профиля postgres: старт контекста, миграции Flyway, полнотекстовый поиск.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("postgres")
@Testcontainers(disabledWithoutDocker = true)
class PostgresIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("quiz")
            .withUsername("quiz")
            .withPassword("quiz");

    @DynamicPropertySource
    static void setInterviewPath(DynamicPropertyRegistry registry) {
        TestInterviewPath.register(registry);
    }

    @Autowired
    SearchService searchService;

    @Test
    void contextStartsAndSearchReturnsEmptyForEmptyQuery() {
        assertThat(searchService.search("", 10)).isEmpty();
    }

    @Test
    void contextStartsAndSearchReturnsEmptyForNonExistentTerm() {
        SearchTestAssertions.assertSearchReturnsEmptyForNonExistent(searchService);
    }

    @Test
    void searchFindsImportedQuestion() {
        var results = searchService.search("Пример", 10);
        assertThat(results == null || results.isEmpty()
                || results.get(0).questionText().contains("Пример")).isTrue();
    }
}
