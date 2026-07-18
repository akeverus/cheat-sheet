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
 * Репозиторий закладок на вопросы ({@code bookmark}, UI-014).
 *
 * <p>Одна закладка на вопрос (UNIQUE {@code question_id}). Upsert реализован
 * UPDATE-first-then-INSERT — тем же паттерном, что {@code DailyActivityRepository},
 * чтобы не зависеть от диалектных особенностей ON CONFLICT в рантайме.</p>
 */
@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookmarkRepository {

    JdbcTemplate jdbcTemplate;

    private static final String COLUMNS =
            "id, question_id, note, created_at, updated_at";

    /** Закладка по вопросу, если есть. */
    public Optional<Bookmark> findByQuestionId(long questionId) {
        List<Bookmark> rows = jdbcTemplate.query(
                "SELECT " + COLUMNS + " FROM bookmark WHERE question_id = ? LIMIT 1",
                (rs, rowNum) -> mapRow(rs), questionId);
        return rows.stream().findFirst();
    }

    /** Все закладки, новые первыми. */
    public List<Bookmark> findAll() {
        return jdbcTemplate.query(
                "SELECT " + COLUMNS + " FROM bookmark ORDER BY created_at DESC, id DESC",
                (rs, rowNum) -> mapRow(rs));
    }

    /** Число закладок. */
    public long count() {
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM bookmark", Long.class);
        return count == null ? 0L : count;
    }

    /**
     * Создаёт закладку или обновляет заметку существующей (UPDATE-first).
     * {@code createdAt} у существующей закладки сохраняется.
     */
    public void upsert(long questionId, String note, long now) {
        int updated = jdbcTemplate.update(
                "UPDATE bookmark SET note = ?, updated_at = ? WHERE question_id = ?",
                note, now, questionId);
        if (updated == 0) {
            jdbcTemplate.update(
                    "INSERT INTO bookmark (question_id, note, created_at, updated_at) " +
                            "VALUES (?, ?, ?, ?)",
                    questionId, note, now, now);
        }
    }

    /** Удаляет закладку вопроса (no-op если её нет). */
    public void delete(long questionId) {
        jdbcTemplate.update("DELETE FROM bookmark WHERE question_id = ?", questionId);
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
