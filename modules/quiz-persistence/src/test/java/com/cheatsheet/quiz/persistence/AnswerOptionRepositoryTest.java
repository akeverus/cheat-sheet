package com.cheatsheet.quiz.persistence;

import com.cheatsheet.quiz.domain.AnswerOption;
import com.cheatsheet.quiz.domain.OptionSource;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.persistence.AnswerOptionRepository.AnswerOptionCreate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционные тесты {@link AnswerOptionRepository} на реальном PostgreSQL.
 * Раньше класс не был покрыт; любая регрессия в schema или SQL ловится здесь
 * до старта приложения.
 */
class AnswerOptionRepositoryTest extends AbstractPostgresRepositoryTest {

    private AnswerOptionRepository repository;
    private QuestionRepository questions;

    @BeforeEach
    void initRepository() {
        repository = new AnswerOptionRepository(jdbcTemplate);
        questions = new QuestionRepository(jdbcTemplate);
    }

    private long insertSampleQuestion(String slug) {
        Question q = new Question(0L, slug, slug, "test/" + slug + ".md", "test",
                "Что такое " + slug + "?", "Ответ", false, "hash-" + slug,
                QuestionType.TEXT, null, null, 0, null);
        return questions.insert(q);
    }

    private AnswerOptionCreate option(String label, boolean correct, int order) {
        return new AnswerOptionCreate(
                label + ". option text",
                correct,
                order,
                OptionSource.MARKDOWN.name(),
                "explanation for " + label,
                1, 2, 0);
    }

    @Test
    void insertAllPersistsAllOptionsAndCountMatches() {
        long qid = insertSampleQuestion("a.md#Q1");
        List<AnswerOptionCreate> creates = List.of(
                option("A", true, 0),
                option("B", false, 1),
                option("C", false, 2),
                option("D", false, 3));

        repository.insertAll(qid, creates);

        assertThat(repository.countByQuestionId(qid)).isEqualTo(4);
    }

    @Test
    void findByQuestionIdReturnsOrderedByDisplayOrder() {
        long qid = insertSampleQuestion("b.md#Q1");
        repository.insertAll(qid, List.of(
                option("D", false, 3),
                option("A", true, 0),
                option("C", false, 2),
                option("B", false, 1)));

        List<AnswerOption> found = repository.findByQuestionId(qid);

        assertThat(found).hasSize(4);
        assertThat(found).extracting(AnswerOption::displayOrder)
                .containsExactly(0, 1, 2, 3);
        assertThat(found.get(0).correct()).isTrue();
    }

    @Test
    void deleteByQuestionIdRemovesAllOptionsForThatQuestion() {
        long qid1 = insertSampleQuestion("c.md#Q1");
        long qid2 = insertSampleQuestion("c.md#Q2");
        repository.insertAll(qid1, List.of(option("A", true, 0), option("B", false, 1),
                option("C", false, 2), option("D", false, 3)));
        repository.insertAll(qid2, List.of(option("A", true, 0), option("B", false, 1),
                option("C", false, 2), option("D", false, 3)));

        repository.deleteByQuestionId(qid1);

        assertThat(repository.countByQuestionId(qid1)).isZero();
        assertThat(repository.countByQuestionId(qid2)).isEqualTo(4);
    }

    @Test
    void findByQuestionIdOnUnknownIdReturnsEmpty() {
        assertThat(repository.findByQuestionId(999_999L)).isEmpty();
    }
}
