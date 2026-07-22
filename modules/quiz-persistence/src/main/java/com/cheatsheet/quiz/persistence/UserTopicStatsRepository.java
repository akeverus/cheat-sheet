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

/**
 * Topic mastery stats, scoped by {@link OwnerContext}.
 */
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
                        "SELECT id, topic, correct, incorrect, mastery, last_seen FROM user_topic_stats "
                                + "WHERE topic = ? AND owner_key = ?",
                        (rs, rowNum) -> new UserTopicStats(
                                rs.getLong("id"),
                                rs.getString("topic"),
                                rs.getInt("correct"),
                                rs.getInt("incorrect"),
                                rs.getDouble("mastery"),
                                null
                        ),
                        topic,
                        OwnerContext.current()
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
        jdbcTemplate.update(
                "INSERT INTO user_topic_stats (owner_key, topic, correct, incorrect, mastery, last_seen) "
                        + "VALUES (?, ?, ?, ?, ?, ?) "
                        + "ON CONFLICT (owner_key, topic) DO UPDATE SET "
                        + "correct = user_topic_stats.correct + EXCLUDED.correct, "
                        + "incorrect = user_topic_stats.incorrect + EXCLUDED.incorrect, "
                        + "mastery = COALESCE((user_topic_stats.correct + EXCLUDED.correct)::double precision "
                        + "/ NULLIF(user_topic_stats.correct + EXCLUDED.correct "
                        + "+ user_topic_stats.incorrect + EXCLUDED.incorrect, 0), 0), "
                        + "last_seen = EXCLUDED.last_seen",
                OwnerContext.current(),
                topic,
                correct,
                incorrect,
                (double) correct,
                Timestamp.valueOf(LocalDateTime.now())
        );
    }
}
