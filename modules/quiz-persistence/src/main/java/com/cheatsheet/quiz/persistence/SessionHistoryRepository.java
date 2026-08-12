package com.cheatsheet.quiz.persistence;

import com.cheatsheet.quiz.domain.InterviewMode;
import com.cheatsheet.quiz.domain.SessionRecord;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Репозиторий {@code session_history}, scoped by {@link OwnerContext}.
 *
 * <p>Пишется один раз на завершённую сессию, читается списком последних. Ничего, кроме
 * итогов, здесь не хранится: подробности попыток лежат в {@code attempt}.</p>
 */
@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SessionHistoryRepository {

    JdbcTemplate jdbcTemplate;

    /** Записывает завершённую сессию. */
    public void save(SessionRecord record) {
        jdbcTemplate.update(
                "INSERT INTO session_history (owner_key, finished_at, mode, total_questions, "
                        + "correct_count, wrong_count, unknown_count, duration_seconds, session_xp) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                OwnerContext.current(),
                record.finishedAt().getEpochSecond(),
                record.mode() == null ? InterviewMode.TRAINING.name() : record.mode().name(),
                record.totalQuestions(),
                record.correctCount(),
                record.wrongCount(),
                record.unknownCount(),
                record.duration() == null ? 0L : record.duration().toSeconds(),
                record.sessionXp());
    }

    /**
     * Последние сессии владельца, свежие сверху.
     *
     * @param limit сколько строк вернуть; значения ≤ 0 трактуются как «ничего»
     */
    public List<SessionRecord> findRecent(int limit) {
        if (limit <= 0) {
            return List.of();
        }
        return jdbcTemplate.query(
                "SELECT id, finished_at, mode, total_questions, correct_count, wrong_count, "
                        + "unknown_count, duration_seconds, session_xp FROM session_history "
                        + "WHERE owner_key = ? ORDER BY finished_at DESC, id DESC LIMIT ?",
                (rs, rowNum) -> new SessionRecord(
                        rs.getLong("id"),
                        Instant.ofEpochSecond(rs.getLong("finished_at")),
                        parseMode(rs.getString("mode")),
                        rs.getInt("total_questions"),
                        rs.getInt("correct_count"),
                        rs.getInt("wrong_count"),
                        rs.getInt("unknown_count"),
                        Duration.ofSeconds(rs.getLong("duration_seconds")),
                        rs.getLong("session_xp")),
                OwnerContext.current(), limit);
    }

    /** Сколько сессий записано у владельца — для подписи «всего N». */
    public long count() {
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM session_history WHERE owner_key = ?", Long.class,
                OwnerContext.current());
        return count == null ? 0L : count;
    }

    /**
     * Режим из строки. Неизвестное имя (переименовали константу, правили руками) не
     * должно ронять страницу истории — такая строка читается как тренировка.
     */
    private static InterviewMode parseMode(String raw) {
        if (raw == null || raw.isBlank()) {
            return InterviewMode.TRAINING;
        }
        try {
            return InterviewMode.valueOf(raw);
        } catch (IllegalArgumentException e) {
            return InterviewMode.TRAINING;
        }
    }
}
