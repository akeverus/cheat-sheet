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
 * Приостановленная сессия ({@code paused_session}), one row per {@link OwnerContext}.
 */
@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PausedSessionRepository {

    JdbcTemplate jdbcTemplate;

    public void save(byte[] sessionBlob, InterviewMode mode, String topic,
                     int total, int answered, Instant pausedAt) {
        Timestamp ts = Timestamp.from(pausedAt);
        String owner = OwnerContext.current();
        int updated = jdbcTemplate.update(
                "UPDATE paused_session SET session_blob = ?, mode = ?, topic = ?, " +
                        "total = ?, answered = ?, paused_at = ? WHERE owner_key = ?",
                sessionBlob, mode.name(), topic, total, answered, ts, owner);
        if (updated == 0) {
            jdbcTemplate.update(
                    "INSERT INTO paused_session (owner_key, session_blob, mode, topic, total, answered, paused_at) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)",
                    owner, sessionBlob, mode.name(), topic, total, answered, ts);
        }
    }

    public Optional<byte[]> findBlob() {
        List<byte[]> rows = jdbcTemplate.query(
                "SELECT session_blob FROM paused_session WHERE owner_key = ?",
                (rs, rowNum) -> rs.getBytes("session_blob"),
                OwnerContext.current());
        return rows.isEmpty() ? Optional.empty() : Optional.ofNullable(rows.get(0));
    }

    public Optional<PausedSessionInfo> findInfo() {
        List<PausedSessionInfo> rows = jdbcTemplate.query(
                "SELECT mode, topic, total, answered, paused_at FROM paused_session WHERE owner_key = ?",
                (rs, rowNum) -> new PausedSessionInfo(
                        InterviewMode.valueOf(rs.getString("mode")),
                        rs.getString("topic"),
                        rs.getInt("total"),
                        rs.getInt("answered"),
                        rs.getTimestamp("paused_at").toInstant()),
                OwnerContext.current());
        return rows.isEmpty() ? Optional.empty() : Optional.of(rows.get(0));
    }

    public boolean exists() {
        Boolean present = jdbcTemplate.queryForObject(
                "SELECT EXISTS(SELECT 1 FROM paused_session WHERE owner_key = ?)",
                Boolean.class, OwnerContext.current());
        return Boolean.TRUE.equals(present);
    }

    public void clear() {
        jdbcTemplate.update("DELETE FROM paused_session WHERE owner_key = ?", OwnerContext.current());
    }
}
