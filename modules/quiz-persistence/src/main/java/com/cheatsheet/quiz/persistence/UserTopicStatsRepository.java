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
        Optional<UserTopicStats> existing = findByTopic(topic);
        if (existing.isEmpty()) {
            int correct = correctAnswer ? 1 : 0;
            int incorrect = correctAnswer ? 0 : 1;
            double mastery = calculateMastery(correct, incorrect);
            jdbcTemplate.update(
                    "INSERT INTO user_topic_stats (topic, correct, incorrect, mastery, last_seen) VALUES (?, ?, ?, ?, ?)",
                    topic,
                    correct,
                    incorrect,
                    mastery,
                    Timestamp.valueOf(LocalDateTime.now())
            );
            return;
        }
        UserTopicStats current = existing.get();
        int nextCorrect = current.getCorrect() + (correctAnswer ? 1 : 0);
        int nextIncorrect = current.getIncorrect() + (correctAnswer ? 0 : 1);
        jdbcTemplate.update(
                "UPDATE user_topic_stats SET correct = ?, incorrect = ?, mastery = ?, last_seen = ? WHERE topic = ?",
                nextCorrect,
                nextIncorrect,
                calculateMastery(nextCorrect, nextIncorrect),
                Timestamp.valueOf(LocalDateTime.now()),
                topic
        );
    }

    private static double calculateMastery(int correct, int incorrect) {
        int total = correct + incorrect;
        if (total == 0) {
            return 0.0;
        }
        return (double) correct / total;
    }
}
