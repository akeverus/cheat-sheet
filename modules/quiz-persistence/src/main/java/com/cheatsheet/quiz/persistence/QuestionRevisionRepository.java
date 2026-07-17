package com.cheatsheet.quiz.persistence;

import com.cheatsheet.quiz.domain.QuestionRevision;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий append-only лога ревизий контента ({@code question_revision}).
 *
 * <p>{@link #append} создаёт новую ревизию с автоинкрементом
 * {@code revision_number} в рамках вопроса (атомарно одним INSERT …
 * {@code MAX(revision_number)+1}; single-user — гонок нет). {@code created_at}
 * задаётся вызывающим (epoch-секунды) для детерминизма в тестах.</p>
 */
@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuestionRevisionRepository {

    JdbcTemplate jdbcTemplate;

    /**
     * Добавляет новую ревизию контента, автоинкрементируя {@code revision_number}.
     *
     * @param questionId вопрос
     * @param checksum   SHA-256 контента
     * @param createdAt  момент создания, epoch-секунды
     * @return сохранённая ревизия (с присвоенными id и revision_number)
     */
    public QuestionRevision append(long questionId, String checksum, long createdAt) {
        return jdbcTemplate.queryForObject(
                "INSERT INTO question_revision " +
                        "(question_id, revision_number, checksum, publication_status, created_at) " +
                        "VALUES (?, COALESCE((SELECT MAX(revision_number) FROM question_revision " +
                        "WHERE question_id = ?), 0) + 1, ?, ?, ?) " +
                        "RETURNING id, question_id, revision_number, checksum, publication_status, created_at",
                (rs, rowNum) -> mapRow(rs),
                questionId, questionId, checksum, QuestionRevision.STATUS_PUBLISHED, createdAt);
    }

    /**
     * Последняя (с наибольшим {@code revision_number}) ревизия вопроса.
     */
    public Optional<QuestionRevision> findLatest(long questionId) {
        List<QuestionRevision> rows = jdbcTemplate.query(
                "SELECT id, question_id, revision_number, checksum, publication_status, created_at " +
                        "FROM question_revision WHERE question_id = ? " +
                        "ORDER BY revision_number DESC LIMIT 1",
                (rs, rowNum) -> mapRow(rs),
                questionId);
        return rows.stream().findFirst();
    }

    /**
     * Все ревизии вопроса, новые первыми.
     */
    public List<QuestionRevision> findByQuestionId(long questionId) {
        return jdbcTemplate.query(
                "SELECT id, question_id, revision_number, checksum, publication_status, created_at " +
                        "FROM question_revision WHERE question_id = ? " +
                        "ORDER BY revision_number DESC",
                (rs, rowNum) -> mapRow(rs),
                questionId);
    }

    private static QuestionRevision mapRow(ResultSet rs) throws SQLException {
        return new QuestionRevision(
                rs.getLong("id"),
                rs.getLong("question_id"),
                rs.getInt("revision_number"),
                rs.getString("checksum"),
                rs.getString("publication_status"),
                rs.getLong("created_at"));
    }
}
