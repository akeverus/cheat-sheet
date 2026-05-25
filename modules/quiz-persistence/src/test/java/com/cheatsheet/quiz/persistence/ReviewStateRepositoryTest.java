package com.cheatsheet.quiz.persistence;

import com.cheatsheet.quiz.domain.ReviewDefaults;
import com.cheatsheet.quiz.domain.ReviewResult;
import com.cheatsheet.quiz.domain.ReviewState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты {@link ReviewStateRepository} на реальном PostgreSQL через Testcontainers.
 */
class ReviewStateRepositoryTest extends AbstractPostgresRepositoryTest {

    private ReviewStateRepository repository;

    @BeforeEach
    void initRepository() {
        // Вопрос-заглушка для FK на review_state(question_id) → questions(id).
        // BIGSERIAL даст id=1, его и используем дальше в тестах.
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
