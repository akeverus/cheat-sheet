package com.cheatsheet.quiz.persistence;

import com.cheatsheet.quiz.domain.InterviewMode;
import com.cheatsheet.quiz.domain.PausedSessionInfo;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для таблицы {@code paused_session} (FLOW-01).
 *
 * <p>Хранит единственную приостановленную сессию (single-user приложение →
 * singleton-строка с {@code id = 1}). {@code session_blob} — Java-сериализованный
 * {@link com.cheatsheet.quiz.domain.InterviewSession}; денормализованные колонки
 * позволяют показать баннер «продолжить?» без десериализации.</p>
 *
 * <p>Upsert реализован через UPDATE-then-INSERT (а не ON CONFLICT) — тот же
 * подход, что в {@link DailyActivityRepository}: ON CONFLICT в Testcontainers
 * PG 16 с binary-протоколом даёт «bad SQL grammar».</p>
 */
@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PausedSessionRepository {

    JdbcTemplate jdbcTemplate;

    /**
     * Сохраняет (перезаписывает) приостановленную сессию. Так как строка одна,
     * повторный вызов заменяет предыдущую паузу.
     */
    public void save(byte[] sessionBlob, InterviewMode mode, String topic,
                     int total, int answered, Instant pausedAt) {
        Timestamp ts = Timestamp.from(pausedAt);
        int updated = jdbcTemplate.update(
                "UPDATE paused_session SET session_blob = ?, mode = ?, topic = ?, " +
                        "total = ?, answered = ?, paused_at = ? WHERE id = 1",
                sessionBlob, mode.name(), topic, total, answered, ts);
        if (updated == 0) {
            jdbcTemplate.update(
                    "INSERT INTO paused_session (id, session_blob, mode, topic, total, answered, paused_at) " +
                            "VALUES (1, ?, ?, ?, ?, ?, ?)",
                    sessionBlob, mode.name(), topic, total, answered, ts);
        }
    }

    /**
     * Возвращает сериализованный блоб приостановленной сессии или пусто, если паузы нет.
     */
    public Optional<byte[]> findBlob() {
        List<byte[]> rows = jdbcTemplate.query(
                "SELECT session_blob FROM paused_session WHERE id = 1",
                (rs, rowNum) -> rs.getBytes("session_blob"));
        return rows.isEmpty() ? Optional.empty() : Optional.ofNullable(rows.get(0));
    }

    /**
     * Возвращает метаданные приостановленной сессии (без блоба) или пусто.
     */
    public Optional<PausedSessionInfo> findInfo() {
        List<PausedSessionInfo> rows = jdbcTemplate.query(
                "SELECT mode, topic, total, answered, paused_at FROM paused_session WHERE id = 1",
                (rs, rowNum) -> new PausedSessionInfo(
                        InterviewMode.valueOf(rs.getString("mode")),
                        rs.getString("topic"),
                        rs.getInt("total"),
                        rs.getInt("answered"),
                        rs.getTimestamp("paused_at").toInstant()));
        return rows.isEmpty() ? Optional.empty() : Optional.of(rows.get(0));
    }

    /**
     * Есть ли приостановленная сессия.
     */
    public boolean exists() {
        Boolean present = jdbcTemplate.queryForObject(
                "SELECT EXISTS(SELECT 1 FROM paused_session WHERE id = 1)", Boolean.class);
        return Boolean.TRUE.equals(present);
    }

    /**
     * Удаляет приостановленную сессию (после resume или отмены).
     */
    public void clear() {
        jdbcTemplate.update("DELETE FROM paused_session WHERE id = 1");
    }
}
