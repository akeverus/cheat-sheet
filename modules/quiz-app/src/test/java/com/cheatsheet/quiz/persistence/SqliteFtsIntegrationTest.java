package com.cheatsheet.quiz.persistence;

import com.cheatsheet.quiz.infrastructure.search.SearchService;
import com.cheatsheet.quiz.service.SearchTestAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/**
 * Интеграционный тест SqliteFtsRepository: поиск по данным из test-interview/sample.md.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class SqliteFtsIntegrationTest {

    @DynamicPropertySource
    static void setInterviewPath(DynamicPropertyRegistry registry) {
        com.cheatsheet.quiz.TestInterviewPath.register(registry);
    }

    @Autowired
    SearchService searchService;

    @Test
    void searchFindsImportedQuestion() {
        SearchTestAssertions.assertSearchFindsTerm(searchService, "Пример");
    }

    @Test
    void searchReturnsEmptyForNonExistentTerm() {
        SearchTestAssertions.assertSearchReturnsEmptyForNonExistent(searchService);
    }
}
