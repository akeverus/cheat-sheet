package com.cheatsheet.quiz.persistence;

import lombok.RequiredArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * Реализация полнотекстового поиска для SQLite FTS5.
 *
 * <p>Активируется при профиле по умолчанию (не postgres).</p>
 *
 * @see FullTextSearchRepository
 */
@Repository
@Profile("!postgres")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SqliteFtsRepository implements FullTextSearchRepository {

    JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void upsert(long questionId, String questionText, String answerMarkdown) {
        delete(questionId);
        jdbcTemplate.update(
                "INSERT INTO questions_fts (question_id, question_text, answer_markdown) VALUES (?, ?, ?)",
                questionId, questionText, answerMarkdown);
    }

    @Override
    public void delete(long questionId) {
        jdbcTemplate.update("DELETE FROM questions_fts WHERE question_id = ?", questionId);
    }

    @Override
    public List<SearchResult> search(String query, int limit) {
        String sanitized = FtsQueryUtils.sanitizeForSqliteFts(query);
        if (sanitized.isBlank()) {
            return List.of();
        }
        try {
            return jdbcTemplate.query(
                    "SELECT question_id, snippet(questions_fts, 0, '[', ']', '...', 10) AS snippet " +
                            "FROM questions_fts WHERE questions_fts MATCH ? LIMIT ?",
                    (rs, rowNum) -> new SearchResult(rs.getLong("question_id"), Objects.requireNonNullElse(rs.getString("snippet"), "")),
                    sanitized, limit);
        } catch (org.springframework.dao.DataAccessException e) {
            log.warn("Ошибка полнотекстового поиска (SQLite FTS): {}", e.getMessage());
            return List.of();
        }
    }
}
