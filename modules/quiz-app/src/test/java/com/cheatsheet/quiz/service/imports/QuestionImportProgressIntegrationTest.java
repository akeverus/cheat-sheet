package com.cheatsheet.quiz.service.imports;

import com.cheatsheet.quiz.persistence.OwnerContext;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "spring.datasource.url=${IMPORT_PROGRESS_TEST_DATABASE_URL:jdbc:tc:postgresql:16-alpine:///import_progress}")
class QuestionImportProgressIntegrationTest {

    private static final String FILE_PATH = "topic/review-state.md";
    private static final String ORIGINAL = "## Q1. Что делает импорт?\n\nОбновляет учебный материал.\n";

    @TempDir
    static Path sourceRoot;

    @DynamicPropertySource
    static void interviewPath(DynamicPropertyRegistry registry) throws IOException {
        Files.createDirectories(sourceRoot.resolve("topic"));
        Files.writeString(sourceRoot.resolve(FILE_PATH), ORIGINAL);
        registry.add("app.interview-path", () -> sourceRoot.toString());
    }

    @Autowired QuestionImportService importer;
    @Autowired QuestionRepository questions;
    @Autowired JdbcTemplate jdbc;

    long questionId;

    @BeforeEach
    void prepareLearnedQuestionInTheIsolatedDatabase() throws IOException {
        OwnerContext.clear();
        jdbc.update("DELETE FROM questions WHERE file_path = ?", FILE_PATH);
        Files.writeString(sourceRoot.resolve(FILE_PATH), ORIGINAL);
        importer.importAll();
        questionId = questions.findBySlug(FILE_PATH + "#Q1").orElseThrow().id();
        for (String owner : List.of("local", "another-student")) {
            jdbc.update("""
                    INSERT INTO review_state (owner_key, question_id, repetitions, interval_days,
                        ease_factor, next_review_at, last_result, correct_count, wrong_count,
                        stability, difficulty, lapses, algo_version, last_reviewed_at)
                    VALUES (?, ?, 6, 47, 2.8, 1800500000, 'CORRECT', 12, 3,
                        19.5, 5.7, 2, 'fsrs-4.5-default', 1800000000)
                    """, owner, questionId);
        }
    }

    @AfterEach
    void clearOwner() {
        OwnerContext.clear();
    }

    @Test
    void changedContentPreservesSm2AndFsrsProgressForEveryOwner() throws IOException {
        var before = savedProgress();
        var original = questions.findBySlug(FILE_PATH + "#Q1").orElseThrow();
        long revisionsBefore = revisionCount();
        Files.writeString(sourceRoot.resolve(FILE_PATH),
                "## Q1. Как обновляется учебный материал?\n\nИмпорт создаёт новую ревизию содержания.\n");

        importer.importAll();

        var updated = questions.findBySlug(FILE_PATH + "#Q1").orElseThrow();
        assertThat(updated.id()).isEqualTo(questionId);
        assertThat(updated.questionText()).isEqualTo("Как обновляется учебный материал?");
        assertThat(updated.answerMarkdown()).contains("новую ревизию");
        assertThat(updated.sourceHash()).isNotEqualTo(original.sourceHash());
        assertThat(revisionCount()).isEqualTo(revisionsBefore + 1);
        assertThat(savedProgress()).containsExactlyElementsOf(before);
    }

    @Test
    void repeatedImportPreservesProgressAndDoesNotAppendRevisions() {
        var before = savedProgress();
        long revisionsBefore = revisionCount();

        importer.importAll();
        importer.importAll();

        assertThat(savedProgress()).containsExactlyElementsOf(before);
        assertThat(revisionCount()).isEqualTo(revisionsBefore);
    }

    @Test
    void newQuestionDoesNotCreateProgressOrChangeExistingProgress() throws IOException {
        var before = savedProgress();
        Files.writeString(sourceRoot.resolve(FILE_PATH),
                ORIGINAL + "\n## Q2. Когда появляется прогресс?\n\nПосле первого ответа ученика.\n");

        importer.importAll();

        long newQuestionId = questions.findBySlug(FILE_PATH + "#Q2").orElseThrow().id();
        assertThat(jdbc.queryForObject("SELECT count(*) FROM review_state WHERE question_id = ?",
                Long.class, newQuestionId)).isZero();
        assertThat(savedProgress()).containsExactlyElementsOf(before);
    }

    private List<String> savedProgress() {
        // Сравниваем всю строку: кроме SM-2 здесь лежат FSRS, срок и статистика ответов.
        return jdbc.queryForList("""
                SELECT row_to_json(r)::text FROM review_state r
                WHERE question_id = ? ORDER BY owner_key
                """, String.class, questionId);
    }

    private long revisionCount() {
        return jdbc.queryForObject("SELECT count(*) FROM question_revision WHERE question_id = ?",
                Long.class, questionId);
    }
}
