package com.cheatsheet.quiz.persistence;

import com.cheatsheet.quiz.domain.InterviewStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.cheatsheet.quiz.domain.TopicStats;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/**
 * Тесты {@link QuestionStatsRepository} на реальном PostgreSQL через Testcontainers.
 */
class QuestionStatsRepositoryTest extends AbstractPostgresRepositoryTest {

    private QuestionStatsRepository repository;

    @BeforeEach
    void initRepository() {
        repository = new QuestionStatsRepository(jdbcTemplate);
    }

    /**
     * Когда в БД нет ни вопросов, ни строк в review_state, getAggregatedStats не должен бросать исключение,
     * а должен вернуть статистику из нулей.
     */
    @Test
    void getAggregatedStatsOnEmptyDbReturnsZeroes() {
        long nowEpoch = System.currentTimeMillis() / 1000;
        int minRepetitions = 3;

        InterviewStats stats = repository.getAggregatedStats(null, null, null, nowEpoch, minRepetitions);

        assertThat(stats.total()).isZero();
        assertThat(stats.due()).isZero();
        assertThat(stats.learned()).isZero();
        assertThat(stats.correct()).isZero();
        assertThat(stats.wrong()).isZero();
    }

    /**
     * Когда есть questions, но нет review_state (JOIN не даёт строк), тоже возвращаются нули.
     */
    @Test
    void getAggregatedStatsWithQuestionsButNoReviewStateReturnsZeroes() {
        jdbcTemplate.update("""
                INSERT INTO questions (slug, source_slug, file_path, topic, question_text, answer_markdown,
                    is_important, source_hash, question_type, regen_count)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """, "q1", "q1", "test/q1.md", "Java", "Q?", "A", 0, "hash", "TEXT", 0);

        long nowEpoch = System.currentTimeMillis() / 1000;
        InterviewStats stats = repository.getAggregatedStats(null, null, null, nowEpoch, 3);

        assertThat(stats.total()).isZero();
        assertThat(stats.due()).isZero();
        assertThat(stats.learned()).isZero();
        assertThat(stats.correct()).isZero();
        assertThat(stats.wrong()).isZero();
    }

    @Test
    void findTopicStatsIncludesMaturityScore() {
        long nowEpoch = System.currentTimeMillis() / 1000;

        jdbcTemplate.update(
                "INSERT INTO questions (slug, source_slug, file_path, topic, question_text, answer_markdown, source_hash) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)",
                "q1", "q1", "f.md", "java", "Q?", "A.", "h1");
        jdbcTemplate.update(
                "INSERT INTO review_state (question_id, repetitions, interval_days, ease_factor, " +
                "next_review_at, last_result, correct_count, wrong_count) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                1L, 3, 10, 2.5, nowEpoch - 1, "CORRECT", 5, 1);

        List<TopicStats> stats = repository.findTopicStats(nowEpoch, 3);

        assertThat(stats).hasSize(1);
        assertThat(stats.get(0).maturityScore()).isEqualTo(25.0, within(0.01));
        // 2.5 * 10 = 25.0
    }
}
