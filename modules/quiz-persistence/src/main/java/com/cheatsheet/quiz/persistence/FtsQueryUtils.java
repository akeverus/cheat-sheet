package com.cheatsheet.quiz.persistence;

import org.apache.commons.lang3.StringUtils;
import lombok.experimental.UtilityClass;

/**
 * Утилита санитизации запросов для полнотекстового поиска.
 *
 * @see PostgresFullTextSearchRepository
 */
@UtilityClass
public class FtsQueryUtils {

    /** Regex: апостроф и backslash для PostgreSQL plainto_tsquery. */
    private static final String REGEX_POSTGRES_UNSAFE = "['\\\\]";

    /**
     * Нормализация запроса: null → пустая строка, trim.
     */
    public static String normalize(String query) {
        if (query == null || query.isBlank()) {
            return StringUtils.EMPTY;
        }
        return query.trim();
    }

    /**
     * Санитизация для PostgreSQL plainto_tsquery: удаление кавычек и backslash.
     */
    public static String sanitizeForPostgres(String query) {
        String normalized = normalize(query);
        if (normalized.isEmpty()) {
            return StringUtils.EMPTY;
        }
        return normalized.replaceAll(REGEX_POSTGRES_UNSAFE, StringUtils.SPACE).trim();
    }
}
