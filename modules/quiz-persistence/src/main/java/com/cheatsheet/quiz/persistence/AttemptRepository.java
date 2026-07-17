package com.cheatsheet.quiz.persistence;

import com.cheatsheet.quiz.domain.Attempt;
import com.cheatsheet.quiz.domain.AttemptOutcome;
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
 * Репозиторий построчного лога попыток ответа ({@code attempt}).
 *
 * <p>{@link #insert} пишет одну попытку и возвращает её id. Nullable-поля
 * (ревизия, вариант, время ответа, грейд, токены) передаются как {@code null}.
 * {@code created_at} — epoch-секунды, задаётся вызывающим для детерминизма.</p>
 */
@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AttemptRepository {

    JdbcTemplate jdbcTemplate;

    private static final String COLUMNS =
            "id, question_id, question_revision_id, selected_option_id, outcome, " +
                    "is_first_attempt, response_time_ms, memory_grade, session_token, " +
                    "idempotency_key, created_at";

    /**
     * Сохраняет попытку и возвращает присвоенный идентификатор.
     */
    public long insert(Attempt attempt) {
        Long id = jdbcTemplate.queryForObject(
                "INSERT INTO attempt " +
                        "(question_id, question_revision_id, selected_option_id, outcome, " +
                        "is_first_attempt, response_time_ms, memory_grade, session_token, " +
                        "idempotency_key, created_at) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id",
                Long.class,
                attempt.questionId(),
                attempt.questionRevisionId(),
                attempt.selectedOptionId(),
                attempt.outcome().name(),
                attempt.firstAttempt(),
                attempt.responseTimeMs(),
                attempt.memoryGrade(),
                attempt.sessionToken(),
                attempt.idempotencyKey(),
                attempt.createdAt());
        return id == null ? 0L : id;
    }

    /**
     * Все попытки по вопросу, новые первыми.
     */
    public List<Attempt> findByQuestionId(long questionId) {
        return jdbcTemplate.query(
                "SELECT " + COLUMNS + " FROM attempt WHERE question_id = ? " +
                        "ORDER BY created_at DESC, id DESC",
                (rs, rowNum) -> mapRow(rs),
                questionId);
    }

    /**
     * Была ли уже хоть одна попытка по вопросу (для {@code is_first_attempt}).
     */
    public boolean existsByQuestionId(long questionId) {
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM attempt WHERE question_id = ?",
                Long.class, questionId);
        return count != null && count > 0;
    }

    /**
     * Попытка по ключу идемпотентности (дедуп двойного POST), если есть.
     */
    public Optional<Attempt> findByIdempotencyKey(String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            return Optional.empty();
        }
        List<Attempt> rows = jdbcTemplate.query(
                "SELECT " + COLUMNS + " FROM attempt WHERE idempotency_key = ? LIMIT 1",
                (rs, rowNum) -> mapRow(rs),
                idempotencyKey);
        return rows.stream().findFirst();
    }

    private static Attempt mapRow(ResultSet rs) throws SQLException {
        return new Attempt(
                rs.getLong("id"),
                rs.getLong("question_id"),
                getNullableLong(rs, "question_revision_id"),
                getNullableLong(rs, "selected_option_id"),
                AttemptOutcome.valueOf(rs.getString("outcome")),
                rs.getBoolean("is_first_attempt"),
                getNullableInt(rs, "response_time_ms"),
                getNullableInt(rs, "memory_grade"),
                rs.getString("session_token"),
                rs.getString("idempotency_key"),
                rs.getLong("created_at"));
    }

    private static Long getNullableLong(ResultSet rs, String column) throws SQLException {
        long value = rs.getLong(column);
        return rs.wasNull() ? null : value;
    }

    private static Integer getNullableInt(ResultSet rs, String column) throws SQLException {
        int value = rs.getInt(column);
        return rs.wasNull() ? null : value;
    }
}
