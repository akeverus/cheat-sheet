package com.cheatsheet.quiz.persistence;

import com.cheatsheet.quiz.persistence.model.UserTopicStats;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserTopicStatsRepository {

    JdbcTemplate jdbcTemplate;

    public Optional<UserTopicStats> findByTopic(String topic) {
        if (topic == null || topic.isBlank()) {
            return Optional.empty();
        }
        return jdbcTemplate.query(
                        "SELECT id, topic, correct, incorrect, mastery, last_seen FROM user_topic_stats WHERE topic = ?",
                        (rs, rowNum) -> new UserTopicStats(
                                rs.getLong("id"),
                                rs.getString("topic"),
                                rs.getInt("correct"),
                                rs.getInt("incorrect"),
                                rs.getDouble("mastery"),
                                null
                        ),
                        topic
                )
                .stream()
                .findFirst();
    }

    @Transactional
    public void recordAnswer(String topic, boolean correctAnswer) {
        if (topic == null || topic.isBlank()) {
            return;
        }
        int correct = correctAnswer ? 1 : 0;
        int incorrect = correctAnswer ? 0 : 1;
        // Атомарный upsert вместо read-modify-write (findByTopic + INSERT/UPDATE).
        // Прежний вариант имел гонку: два конкурентных первых ответа по одной теме
        // оба видели empty → оба INSERT → второй падал на UNIQUE(topic), откатывая
        // весь @Transactional submitAnswer вместе с SM-2 ReviewState; в ветке UPDATE
        // параллельные ответы давали lost update, искажая mastery (питает границы
        // AdaptiveDifficultyService). ON CONFLICT инкрементирует и пересчитывает
        // mastery одним statement. PostgreSQL-only (CLAUDE.md) → ON CONFLICT доступен.
        // На первичной вставке total=1 → mastery = correct (1.0|0.0), как раньше;
        // COALESCE/NULLIF — защита от деления на 0 (теоретическая, total всегда ≥1).
        jdbcTemplate.update(
                "INSERT INTO user_topic_stats (topic, correct, incorrect, mastery, last_seen) "
                        + "VALUES (?, ?, ?, ?, ?) "
                        + "ON CONFLICT (topic) DO UPDATE SET "
                        + "correct = user_topic_stats.correct + EXCLUDED.correct, "
                        + "incorrect = user_topic_stats.incorrect + EXCLUDED.incorrect, "
                        + "mastery = COALESCE((user_topic_stats.correct + EXCLUDED.correct)::double precision "
                        + "/ NULLIF(user_topic_stats.correct + EXCLUDED.correct "
                        + "+ user_topic_stats.incorrect + EXCLUDED.incorrect, 0), 0), "
                        + "last_seen = EXCLUDED.last_seen",
                topic,
                correct,
                incorrect,
                (double) correct,
                Timestamp.valueOf(LocalDateTime.now())
        );
    }
}
