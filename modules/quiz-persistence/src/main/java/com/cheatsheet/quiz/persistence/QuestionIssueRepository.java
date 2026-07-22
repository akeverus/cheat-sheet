package com.cheatsheet.quiz.persistence;

import com.cheatsheet.quiz.domain.QuestionIssue;
import com.cheatsheet.quiz.domain.QuestionIssueCategory;
import com.cheatsheet.quiz.domain.QuestionIssueStatus;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

/**
 * Репозиторий {@code question_issue}, scoped by {@link OwnerContext}.
 */
@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuestionIssueRepository {

    JdbcTemplate jdbcTemplate;

    /**
     * Сохраняет новую жалобу (статус OPEN) и возвращает её идентификатор.
     */
    public long save(long questionId, QuestionIssueCategory category, String comment, Instant createdAt) {
        Long id = jdbcTemplate.queryForObject(
                "INSERT INTO question_issue (owner_key, question_id, category, comment, status, created_at) " +
                        "VALUES (?, ?, ?, ?, ?, ?) RETURNING id",
                Long.class,
                OwnerContext.current(),
                questionId, category.name(), comment, QuestionIssueStatus.OPEN.name(), Timestamp.from(createdAt));
        return id == null ? 0L : id;
    }

    /**
     * Возвращает жалобы текущего владельца по вопросу, новые первыми.
     */
    public List<QuestionIssue> findByQuestionId(long questionId) {
        return jdbcTemplate.query(
                "SELECT id, question_id, category, comment, status, created_at " +
                        "FROM question_issue WHERE question_id = ? AND owner_key = ? " +
                        "ORDER BY created_at DESC, id DESC",
                (rs, rowNum) -> mapRow(rs),
                questionId, OwnerContext.current());
    }

    /**
     * Количество жалоб текущего владельца в заданном статусе.
     */
    public long countByStatus(QuestionIssueStatus status) {
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM question_issue WHERE status = ? AND owner_key = ?",
                Long.class, status.name(), OwnerContext.current());
        return count == null ? 0L : count;
    }

    private static QuestionIssue mapRow(java.sql.ResultSet rs) throws java.sql.SQLException {
        return new QuestionIssue(
                rs.getLong("id"),
                rs.getLong("question_id"),
                QuestionIssueCategory.valueOf(rs.getString("category")),
                rs.getString("comment"),
                QuestionIssueStatus.valueOf(rs.getString("status")),
                rs.getTimestamp("created_at").toInstant());
    }
}
