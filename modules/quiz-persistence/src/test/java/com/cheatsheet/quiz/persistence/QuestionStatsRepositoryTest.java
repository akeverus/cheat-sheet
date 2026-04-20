package com.cheatsheet.quiz.persistence;

import com.cheatsheet.quiz.domain.InterviewStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.cheatsheet.quiz.domain.TopicStats;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/**
 * Тесты {@link QuestionStatsRepository}, в том числе getAggregatedStats при пустой БД.
 */
class QuestionStatsRepositoryTest {

    private JdbcTemplate jdbcTemplate;
    private QuestionStatsRepository repository;

    @BeforeEach
    void setUp() {
        SingleConnectionDataSource ds = new SingleConnectionDataSource("jdbc:sqlite::memory:", true);
        jdbcTemplate = new JdbcTemplate(ds);

        jdbcTemplate.execute("""
                CREATE TABLE questions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    slug TEXT NOT NULL UNIQUE,
                    source_slug TEXT,
                    file_path TEXT NOT NULL,
                    topic TEXT NOT NULL,
                    question_text TEXT NOT NULL,
                    answer_markdown TEXT NOT NULL,
                    is_important INTEGER NOT NULL DEFAULT 0,
                    source_hash TEXT NOT NULL,
                    question_type TEXT NOT NULL DEFAULT 'TEXT',
                    code_snippet TEXT,
                    diagram_mermaid TEXT,
                    regen_count INTEGER NOT NULL DEFAULT 0,
                    takeaway TEXT
                )
                """);
        jdbcTemplate.execute("""
                CREATE TABLE review_state (
                    question_id INTEGER PRIMARY KEY,
                    repetitions INTEGER NOT NULL DEFAULT 0,
                    interval_days INTEGER NOT NULL DEFAULT 0,
                    ease_factor REAL NOT NULL DEFAULT 2.5,
                    next_review_at INTEGER NOT NULL,
                    last_result TEXT NOT NULL DEFAULT 'NEW',
                    correct_count INTEGER NOT NULL DEFAULT 0,
                    wrong_count INTEGER NOT NULL DEFAULT 0,
                    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
                )
                """);
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
