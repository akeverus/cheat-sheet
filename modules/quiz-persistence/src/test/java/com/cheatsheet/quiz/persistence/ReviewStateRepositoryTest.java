package com.cheatsheet.quiz.persistence;

import com.cheatsheet.quiz.domain.ReviewDefaults;
import com.cheatsheet.quiz.domain.ReviewResult;
import com.cheatsheet.quiz.domain.ReviewState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты {@link ReviewStateRepository} с SQLite in-memory.
 */
class ReviewStateRepositoryTest {

    private JdbcTemplate jdbcTemplate;
    private ReviewStateRepository repository;

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
                    regen_count INTEGER NOT NULL DEFAULT 0
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

        // Вставляем вопрос-заглушку для FK
        jdbcTemplate.update("""
                INSERT INTO questions (slug, source_slug, file_path, topic, question_text, answer_markdown, source_hash)
                VALUES ('test.md#Q1', 'test.md#Q1', 'test.md', 'Java', 'Question?', 'Answer', 'hash1')
                """);

        repository = new ReviewStateRepository(jdbcTemplate);
    }

    @Test
    void insertIfAbsentCreatesNewState() {
        repository.insertIfAbsent(1L, 1000L);

        Optional<ReviewState> state = repository.findByQuestionId(1L);
        assertThat(state).isPresent();
        assertThat(state.get().questionId()).isEqualTo(1L);
        assertThat(state.get().nextReviewAt()).isEqualTo(1000L);
        assertThat(state.get().lastResult()).isEqualTo(ReviewResult.NEW);
        assertThat(state.get().repetitions()).isZero();
    }

    @Test
    void insertIfAbsentDoesNotOverwriteExistingState() {
        repository.insertIfAbsent(1L, 1000L);

        ReviewState existing = repository.findByQuestionId(1L).orElseThrow();
        ReviewState updated = existing.withRepetitions(5).withNextReviewAt(2000L);
        repository.update(updated);

        // Повторный insertIfAbsent не должен перезаписать
        repository.insertIfAbsent(1L, 3000L);

        ReviewState reloaded = repository.findByQuestionId(1L).orElseThrow();
        assertThat(reloaded.repetitions()).isEqualTo(5);
        assertThat(reloaded.nextReviewAt()).isEqualTo(2000L);
    }

    @Test
    void findByQuestionIdReturnsEmptyForUnknown() {
        assertThat(repository.findByQuestionId(999L)).isEmpty();
    }

    @Test
    void updateModifiesAllFields() {
        repository.insertIfAbsent(1L, 1000L);

        ReviewState state = new ReviewState(1L, 3, 7, 2.2, 5000L, ReviewResult.CORRECT, 10, 2);
        repository.update(state);

        ReviewState reloaded = repository.findByQuestionId(1L).orElseThrow();
        assertThat(reloaded.repetitions()).isEqualTo(3);
        assertThat(reloaded.intervalDays()).isEqualTo(7);
        assertThat(reloaded.easeFactor()).isEqualTo(2.2);
        assertThat(reloaded.nextReviewAt()).isEqualTo(5000L);
        assertThat(reloaded.lastResult()).isEqualTo(ReviewResult.CORRECT);
        assertThat(reloaded.correctCount()).isEqualTo(10);
        assertThat(reloaded.wrongCount()).isEqualTo(2);
    }
}
