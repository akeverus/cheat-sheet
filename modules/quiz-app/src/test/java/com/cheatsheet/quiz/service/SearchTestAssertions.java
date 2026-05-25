package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.infrastructure.search.SearchService;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Общие проверки для интеграционных тестов полнотекстового поиска
 * (PostgresFullTextSearchRepository).
 */
public final class SearchTestAssertions {

    private static final String NON_EXISTENT_TERM = "xyznonexistent123";

    private SearchTestAssertions() {}

    /**
     * Проверяет, что поиск по term возвращает хотя бы один результат с этим термином в тексте.
     */
    public static void assertSearchFindsTerm(SearchService searchService, String term) {
        var results = searchService.search(term, 10);
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).questionText()).contains(term);
    }

    /**
     * Проверяет, что поиск по несуществующему термину возвращает пустой список.
     */
    public static void assertSearchReturnsEmptyForNonExistent(SearchService searchService) {
        assertThat(searchService.search(NON_EXISTENT_TERM, 10)).isEmpty();
    }
}
