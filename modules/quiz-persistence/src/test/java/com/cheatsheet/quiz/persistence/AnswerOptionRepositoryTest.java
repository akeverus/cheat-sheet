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
        Question q = Question.builder()
                .id(0L)
                .slug(slug)
                .sourceSlug(slug)
                .filePath("test/" + slug + ".md")
                .topic("test")
                .questionText("Что такое " + slug + "?")
                .answerMarkdown("Ответ")
                .important(false)
                .sourceHash("hash-" + slug)
                .questionType(QuestionType.TEXT)
                .codeSnippet(null)
                .diagramMermaid(null)
                .takeaway(null)
                .build();
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

    @Test
    void insertAllStripsNulBytesFromTextAndExplanation() {
        // PostgreSQL не хранит 0x00 и обрезал бы строку на нём (теряя хвост).
        // Репозиторий обязан вычищать NUL до записи — иначе тихая потеря данных
        // и поломка идемпотентной сверки в McqJsonLoader.
        long qid = insertSampleQuestion("nul.md#Q1");
        AnswerOptionCreate withNul = new AnswerOptionCreate(
                "A. до" + (char) 0 + "после",
                true, 0, OptionSource.MARKDOWN.name(),
                "expl-до" + (char) 0 + "expl-после",
                1, 2, 0);

        repository.insertAll(qid, List.of(withNul));

        List<AnswerOption> found = repository.findByQuestionId(qid);
        assertThat(found).hasSize(1);
        AnswerOption stored = found.get(0);
        // NUL удалён, хвост сохранён (а не обрезан).
        assertThat(stored.optionText()).isEqualTo("A. допосле");
        assertThat(stored.explanation()).isEqualTo("expl-доexpl-после");
        assertThat(stored.optionText()).doesNotContain(String.valueOf((char) 0));
        assertThat(stored.explanation()).doesNotContain(String.valueOf((char) 0));
    }
}
