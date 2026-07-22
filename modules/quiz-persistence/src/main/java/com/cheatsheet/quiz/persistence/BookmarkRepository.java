package com.cheatsheet.quiz.persistence;

import com.cheatsheet.quiz.domain.Bookmark;
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
 * Закладки ({@code bookmark}), scoped by {@link OwnerContext}.
 */
@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookmarkRepository {

    JdbcTemplate jdbcTemplate;

    private static final String COLUMNS =
            "id, question_id, note, created_at, updated_at";

    public Optional<Bookmark> findByQuestionId(long questionId) {
        List<Bookmark> rows = jdbcTemplate.query(
                "SELECT " + COLUMNS + " FROM bookmark WHERE question_id = ? AND owner_key = ? LIMIT 1",
                (rs, rowNum) -> mapRow(rs), questionId, OwnerContext.current());
        return rows.stream().findFirst();
    }

    public List<Bookmark> findAll() {
        return jdbcTemplate.query(
                "SELECT " + COLUMNS + " FROM bookmark WHERE owner_key = ? ORDER BY created_at DESC, id DESC",
                (rs, rowNum) -> mapRow(rs), OwnerContext.current());
    }

    public long count() {
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM bookmark WHERE owner_key = ?",
                Long.class, OwnerContext.current());
        return count == null ? 0L : count;
    }

    public void upsert(long questionId, String note, long now) {
        int updated = jdbcTemplate.update(
                "UPDATE bookmark SET note = ?, updated_at = ? WHERE question_id = ? AND owner_key = ?",
                note, now, questionId, OwnerContext.current());
        if (updated == 0) {
            jdbcTemplate.update(
                    "INSERT INTO bookmark (owner_key, question_id, note, created_at, updated_at) " +
                            "VALUES (?, ?, ?, ?, ?)",
                    OwnerContext.current(), questionId, note, now, now);
        }
    }

    public void delete(long questionId) {
        jdbcTemplate.update(
                "DELETE FROM bookmark WHERE question_id = ? AND owner_key = ?",
                questionId, OwnerContext.current());
    }

    private static Bookmark mapRow(ResultSet rs) throws SQLException {
        return new Bookmark(
                rs.getLong("id"),
                rs.getLong("question_id"),
                rs.getString("note"),
                rs.getLong("created_at"),
                rs.getLong("updated_at"));
    }
}
