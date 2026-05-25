package com.cheatsheet.quiz.persistence;

import lombok.RequiredArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

/**
 * Реализация полнотекстового поиска для PostgreSQL (tsvector + GIN).
 * PostgreSQL — единственная поддерживаемая БД.
 *
 * @see FullTextSearchRepository
 */
@Repository
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostgresFullTextSearchRepository implements FullTextSearchRepository {

    JdbcTemplate jdbcTemplate;

    @Override
    public void upsert(long questionId, String questionText, String answerMarkdown) {
        // Parameters intentionally unused — trigger questions_search_vector_update in V2__fulltext.sql
        // updates search_vector on INSERT/UPDATE of questions.
    }

    @Override
    public void delete(long questionId) {
        // No-op: search_vector хранится в таблице questions, удаляется вместе с записью.
    }

    @Override
    public List<SearchResult> search(String query, int limit) {
        String sanitized = FtsQueryUtils.sanitizeForPostgres(query);
        if (sanitized.isBlank()) {
            return List.of();
        }
        try {
            return jdbcTemplate.query(
                    "SELECT q.id AS question_id, " +
                            "ts_headline('russian', q.question_text || ' ' || q.answer_markdown, " +
                            "plainto_tsquery('russian', ?), 'MaxWords=10, MinWords=5, StartSel=<mark>, StopSel=</mark>') AS snippet " +
                            "FROM questions q " +
                            "WHERE q.search_vector @@ plainto_tsquery('russian', ?) " +
                            "LIMIT ?",
                    (rs, rowNum) -> new SearchResult(rs.getLong("question_id"), Objects.requireNonNullElse(rs.getString("snippet"), "")),
                    sanitized, sanitized, limit);
        } catch (org.springframework.dao.DataAccessException e) {
            log.warn("Ошибка полнотекстового поиска (PostgreSQL): {}", e.getMessage());
            return List.of();
        }
    }
}
