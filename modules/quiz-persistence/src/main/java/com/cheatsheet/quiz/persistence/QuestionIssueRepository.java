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
 * Репозиторий для таблицы {@code question_issue} (FLOW-REPORT).
 *
 * <p>Хранит жалобы пользователя на вопросы. Запись создаётся со статусом
 * {@link QuestionIssueStatus#OPEN}; смена статуса — вручную (single-user
 * инструмент). {@code created_at} задаётся вызывающим (через {@code Clock}),
 * чтобы поведение было детерминированным в тестах.</p>
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
                "INSERT INTO question_issue (question_id, category, comment, status, created_at) " +
                        "VALUES (?, ?, ?, ?, ?) RETURNING id",
                Long.class,
                questionId, category.name(), comment, QuestionIssueStatus.OPEN.name(), Timestamp.from(createdAt));
        return id == null ? 0L : id;
    }

    /**
     * Возвращает все жалобы по вопросу, новые первыми.
     */
    public List<QuestionIssue> findByQuestionId(long questionId) {
        return jdbcTemplate.query(
                "SELECT id, question_id, category, comment, status, created_at " +
                        "FROM question_issue WHERE question_id = ? ORDER BY created_at DESC, id DESC",
                (rs, rowNum) -> mapRow(rs),
                questionId);
    }

    /**
     * Количество жалоб в заданном статусе (например, сколько OPEN на разбор).
     */
    public long countByStatus(QuestionIssueStatus status) {
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM question_issue WHERE status = ?",
                Long.class, status.name());
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
