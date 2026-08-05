package com.cheatsheet.quiz.persistence;

import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
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
        return Question.builder()
                .id(0L)
                .slug(slug)
                .sourceSlug(slug)
                .filePath("test/" + slug + ".md")
                .topic(topic)
                .questionText("Что такое " + slug + "?")
                .answerMarkdown("Ответ на " + slug)
                .important(false)
                .sourceHash("hash-" + slug)
                .questionType(QuestionType.TEXT)
                .codeSnippet(null)
                .diagramMermaid(null)
                .takeaway(null)
                .build();
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

        Question updated = Question.builder()
                .id(id)
                .slug(original.slug())
                .sourceSlug(original.sourceSlug())
                .filePath(original.filePath())
                .topic("Spring")
                .questionText("Обновлённый вопрос")
                .answerMarkdown(original.answerMarkdown())
                .important(true)
                .sourceHash("new-hash")
                .questionType(QuestionType.CODE)
                .codeSnippet("int x = 1;")
                .diagramMermaid(null)
                .takeaway(null)
                .build();
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

    /**
     * Вопрос без строки в {@code review_state} — новый, а не несуществующий.
     *
     * <p>Регрессия: все запросы выборки соединялись с {@code review_state} через INNER
     * JOIN, а строку заводил импорт — единожды за жизнь вопроса. Любая потеря таблицы
     * прогресса (чистка, восстановление дампа без неё, прогон QA-фикстур) делала банк
     * невидимым: вопросы в БД есть, выборка пуста, ошибки нет ни одной.
     */
    @Test
    void selectionQueriesSeeQuestionsWithoutReviewState() {
        String topic = "programming-languages/java/java-core-interview";
        long id = repository.insert(sampleQuestion("fresh.md#Q1", topic));

        assertThat(repository.findDueQuestions(topic, false, false, 100L, 10))
                .extracting(Question::id).containsExactly(id);
        assertThat(repository.findNextQuestions(topic, false, false, 100L, 10))
                .extracting(Question::id).containsExactly(id);
        assertThat(repository.findDueQuestionsByTopics(List.of(topic), false, false, 100L, 10))
                .extracting(Question::id).containsExactly(id);
        assertThat(repository.findNextQuestionsByTopics(List.of(topic), false, false, 100L, 10))
                .extracting(Question::id).containsExactly(id);
        assertThat(repository.findQuestionIdsForSession(topic, false, false, 100L, 10))
                .containsExactly(id);
        assertThat(repository.findQuestionIdsForSessionByTopics(List.of(topic), false, false, 100L, 10))
                .containsExactly(id);
        assertThat(repository.findShuffledQuestionIds(false, false, 10)).containsExactly(id);
    }

    /**
     * Просроченное повторение идёт впереди непройденного вопроса.
     *
     * <p>Оба готовы к показу, но у просроченного {@code next_review_at} в прошлом, а
     * непройденному сортировка подставляет текущее время. Без {@code COALESCE} NULL
     * уехал бы в конец (Postgres: {@code NULLS LAST} при ASC) — новые вопросы стали бы
     * недостижимы, пока не кончится очередь повторений.
     */
    @Test
    void overdueRepetitionOutranksUntouchedQuestion() {
        String topic = "programming-languages/java/java-core-interview";
        long overdueId = repository.insert(sampleQuestion("overdue.md#Q1", topic));
        long freshId = repository.insert(sampleQuestion("fresh.md#Q2", topic));
        insertReviewState(overdueId, 10L);

        assertThat(repository.findDueQuestions(topic, false, false, 100L, 10))
                .extracting(Question::id).containsExactly(overdueId, freshId);
    }

    /** Фильтр «только с ошибками» отбрасывает вопрос без прогресса: ошибок у него нет. */
    @Test
    void onlyWrongExcludesQuestionsWithoutProgress() {
        String topic = "programming-languages/java/java-core-interview";
        long withErrors = repository.insert(sampleQuestion("wrong.md#Q1", topic));
        repository.insert(sampleQuestion("fresh.md#Q3", topic));
        insertReviewState(withErrors, 10L);
        jdbcTemplate.update("UPDATE review_state SET wrong_count = 2 WHERE question_id = ?", withErrors);

        assertThat(repository.findNextQuestions(topic, false, true, 100L, 10))
                .extracting(Question::id).containsExactly(withErrors);
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

    @Test
    void findIdByTopicAndQuestionNumberMatchesOnSlugPattern() {
        // McqJsonLoader использует этот метод, чтобы найти question_id
        // для каждого JSON-сидера: slug = «<topic>.md#Q<N>».
        long expected = repository.insert(sampleQuestion(
                "databases/postgres-interview.md#Q5", "databases/postgres-interview"));

        Optional<Long> found = repository.findIdByTopicAndQuestionNumber(
                "databases/postgres-interview", 5);

        assertThat(found).contains(expected);
    }

    @Test
    void findIdByTopicAndQuestionNumberReturnsEmptyForMissing() {
        Optional<Long> found = repository.findIdByTopicAndQuestionNumber(
                "no-such/topic", 42);

        assertThat(found).isEmpty();
    }

    /**
     * Смысл метода — именно в отличии от {@code QuestionStatsRepository.findTopicStats},
     * который делает INNER JOIN с {@code review_state} и потому видит только
     * начатые темы. Здесь review_state нет ни у одной строки, и все темы обязаны
     * оказаться в выдаче: каталог разделов существует, чтобы показать, куда пойти
     * дальше, и нетронутая тема — его главный случай.
     */
    @Test
    void countQuestionsByTopicCoversTopicsWithoutAnyProgress() {
        repository.insert(sampleQuestion("java1.md#Q1", "programming-languages/java/java-core-interview"));
        repository.insert(sampleQuestion("java2.md#Q1", "programming-languages/java/java-core-interview"));
        repository.insert(sampleQuestion("kotlin.md#Q1", "programming-languages/kotlin/kotlin-interview"));

        Map<String, Long> counts = repository.countQuestionsByTopic();

        assertThat(counts).containsExactlyInAnyOrderEntriesOf(Map.of(
                "programming-languages/java/java-core-interview", 2L,
                "programming-languages/kotlin/kotlin-interview", 1L
        ));
    }

    @Test
    void countQuestionsByTopicReturnsEmptyMapForEmptyBank() {
        assertThat(repository.countQuestionsByTopic()).isEmpty();
    }
}
