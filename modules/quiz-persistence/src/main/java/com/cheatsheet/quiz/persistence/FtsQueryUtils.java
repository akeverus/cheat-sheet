package com.cheatsheet.quiz.persistence;

import org.apache.commons.lang3.StringUtils;
import lombok.experimental.UtilityClass;

/**
 * Утилита санитизации запросов для полнотекстового поиска.
 *
 * <p>Общая логика: проверка на null/blank, trim. Специфичная для БД — в отдельных методах.</p>
 *
 * @see SqliteFtsRepository
 * @see PostgresFullTextSearchRepository
 */
@UtilityClass
public class FtsQueryUtils {

    /** Regex: спецсимволы FTS (SQLite MATCH), заменяем на пробел. */
    private static final String REGEX_FTS_SPECIAL_CHARS = "[\"*(){}\\[\\]^~<>]";
    /** Regex: кавычка для экранирования в токенах. */
    private static final String REGEX_FTS_QUOTE_ESCAPE = "\"";
    /** Regex: апостроф и backslash для PostgreSQL plainto_tsquery. */
    private static final String REGEX_POSTGRES_UNSAFE = "['\\\\]";
    /** Regex: пробелы для разбиения на токены. */
    private static final String REGEX_WHITESPACE = "\\s+";

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
     * Санитизация для SQLite FTS5: удаление спецсимволов MATCH, оборачивание токенов в кавычки.
     */
    public static String sanitizeForSqliteFts(String query) {
        String normalized = normalize(query);
        if (normalized.isEmpty()) {
            return StringUtils.EMPTY;
        }
        String cleaned = normalized.replaceAll(REGEX_FTS_SPECIAL_CHARS, StringUtils.SPACE).trim();
        if (cleaned.isBlank()) {
            return StringUtils.EMPTY;
        }
        String[] tokens = cleaned.split(REGEX_WHITESPACE);
        StringBuilder sb = new StringBuilder();
        for (String token : tokens) {
            if (!token.isBlank()) {
                if (!sb.isEmpty()) {
                    sb.append(StringUtils.SPACE);
                }
                sb.append("\"").append(token.replace(REGEX_FTS_QUOTE_ESCAPE, StringUtils.EMPTY)).append("\"");
            }
        }
        return sb.toString();
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
