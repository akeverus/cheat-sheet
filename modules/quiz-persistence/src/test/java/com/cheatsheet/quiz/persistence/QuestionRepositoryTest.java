package com.cheatsheet.quiz.persistence;

import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Тесты {@link QuestionRepository} на реальном PostgreSQL через Testcontainers
 * (shared, reused). Между тестами таблицы чистятся в {@link AbstractPostgresRepositoryTest}.
 */
class QuestionRepositoryTest extends AbstractPostgresRepositoryTest {

    private QuestionRepository repository;

    @BeforeEach
    void initRepository() {
        repository = new QuestionRepository(jdbcTemplate);
    }

    private Question sampleQuestion(String slug, String topic) {
        return new Question(0L, slug, slug, "test/" + slug + ".md", topic,
                "Что такое " + slug + "?", "Ответ на " + slug,
                false, "hash-" + slug, QuestionType.TEXT, null, null, 0, null);
    }

    private void insertReviewState(long questionId, long nextReviewAt) {
        jdbcTemplate.update("""
                INSERT INTO review_state (question_id, repetitions, interval_days, ease_factor, next_review_at, last_result, correct_count, wrong_count)
                VALUES (?, 0, 0, 2.5, ?, 'NEW', 0, 0)
                """, questionId, nextReviewAt);
    }

    @Test
    void insertAndFindById() {
        Question q = sampleQuestion("java.md#Q1", "Java");
        long id = repository.insert(q);

        assertThat(id).isPositive();

        Optional<Question> found = repository.findById(id);
        assertThat(found).isPresent();
        assertThat(found.get().slug()).isEqualTo("java.md#Q1");
        assertThat(found.get().topic()).isEqualTo("Java");
        assertThat(found.get().questionType()).isEqualTo(QuestionType.TEXT);
    }

    @Test
    void findBySlugReturnsCorrectQuestion() {
        repository.insert(sampleQuestion("spring.md#Q1", "Spring"));

        Optional<Question> found = repository.findBySlug("spring.md#Q1");
        assertThat(found).isPresent();
        assertThat(found.get().topic()).isEqualTo("Spring");
    }

    @Test
    void findBySlugReturnsEmptyForUnknown() {
        assertThat(repository.findBySlug("nonexistent")).isEmpty();
    }

    @Test
    void findByIdReturnsEmptyForUnknown() {
        assertThat(repository.findById(999L)).isEmpty();
    }

    @Test
    void updateModifiesQuestion() {
        long id = repository.insert(sampleQuestion("q.md#Q1", "Java"));
        Question original = repository.findById(id).orElseThrow();

        Question updated = new Question(id, original.slug(), original.sourceSlug(), original.filePath(),
                "Spring", "Обновлённый вопрос", original.answerMarkdown(), true,
                "new-hash", QuestionType.CODE, "int x = 1;", null, 0, null);
        repository.update(updated);

        Question reloaded = repository.findById(id).orElseThrow();
        assertThat(reloaded.topic()).isEqualTo("Spring");
        assertThat(reloaded.questionText()).isEqualTo("Обновлённый вопрос");
        assertThat(reloaded.important()).isTrue();
        assertThat(reloaded.questionType()).isEqualTo(QuestionType.CODE);
        assertThat(reloaded.codeSnippet()).isEqualTo("int x = 1;");
    }

    @Test
    void insertMultipleAndFindBySlug() {
        repository.insert(sampleQuestion("a.md#Q1", "Java"));
        repository.insert(sampleQuestion("b.md#Q1", "Spring"));

        assertThat(repository.findBySlug("a.md#Q1")).isPresent();
        assertThat(repository.findBySlug("b.md#Q1")).isPresent();
        assertThat(repository.findBySlug("c.md#Q1")).isEmpty();
    }

    @Test
    void mapQuestionThrowsForInvalidQuestionType() {
        // Вставляем строку с невалидным question_type напрямую через SQL
        jdbcTemplate.update("""
                INSERT INTO questions (slug, source_slug, file_path, topic, question_text, answer_markdown,
                    is_important, source_hash, question_type)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                "bad.md#Q1", "bad.md#Q1", "test/bad.md", "Bad", "test?", "answer",
                0, "hash", "INVALID_TYPE");

        assertThatThrownBy(() -> repository.findBySlug("bad.md#Q1"))
                .isInstanceOf(Exception.class);
    }

    @Test
    void findDueQuestionsByTopicsRespectsTopicOrder() {
        long javaId = repository.insert(sampleQuestion("java.md#Q1", "programming-languages/java/java-core-interview"));
        long kotlinId = repository.insert(sampleQuestion("kotlin.md#Q1", "programming-languages/kotlin/kotlin-interview"));
        insertReviewState(javaId, 10L);
        insertReviewState(kotlinId, 10L);

        List<Question> due = repository.findDueQuestionsByTopics(
                List.of("programming-languages/kotlin/kotlin-interview", "programming-languages/java/java-core-interview"),
                false,
                false,
                100L,
                10
        );

        assertThat(due).hasSize(2);
        assertThat(due.get(0).id()).isEqualTo(kotlinId);
        assertThat(due.get(1).id()).isEqualTo(javaId);
    }

    @Test
    void findDueQuestionsByTopicsWithEmptyListReturnsEmptyWithoutSqlError() {
        long questionId = repository.insert(sampleQuestion("java.md#Q2", "programming-languages/java/java-core-interview"));
        insertReviewState(questionId, 10L);

        List<Question> due = repository.findDueQuestionsByTopics(
                List.of(),
                false,
                false,
                100L,
                10
        );

        assertThat(due).isEmpty();
    }

    @Test
    void findNextQuestionsByTopicsWithEmptyListReturnsEmpty() {
        long questionId = repository.insert(sampleQuestion("java.md#Q3", "programming-languages/java/java-core-interview"));
        insertReviewState(questionId, 10L);

        List<Question> next = repository.findNextQuestionsByTopics(
                List.of(),
                false,
                false,
                100L,
                10
        );

        assertThat(next).isEmpty();
    }

    @Test
    void findQuestionIdsForPreloadByTopicsWithEmptyListReturnsEmpty() {
        long questionId = repository.insert(sampleQuestion("java.md#Q4", "programming-languages/java/java-core-interview"));
        insertReviewState(questionId, 10L);

        List<Long> ids = repository.findQuestionIdsForPreloadByTopics(
                List.of(),
                false,
                false,
                10
        );

        assertThat(ids).isEmpty();
    }

    @Test
    void findQuestionIdsExcludingByTopicsWithEmptyListReturnsEmpty() {
        long questionId = repository.insert(sampleQuestion("java.md#Q5", "programming-languages/java/java-core-interview"));
        insertReviewState(questionId, 10L);

        List<Long> ids = repository.findQuestionIdsExcludingByTopics(
                List.of(questionId),
                List.of(),
                false,
                false,
                100L,
                10
        );

        assertThat(ids).isEmpty();
    }

    @Test
    void findQuestionIdsForSessionByTopicsWithEmptyListReturnsEmpty() {
        long questionId = repository.insert(sampleQuestion("java.md#Q6", "programming-languages/java/java-core-interview"));
        insertReviewState(questionId, 10L);

        List<Long> ids = repository.findQuestionIdsForSessionByTopics(
                List.of(),
                false,
                false,
                100L,
                10
        );

        assertThat(ids).isEmpty();
    }
}
