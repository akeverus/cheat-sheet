package com.cheatsheet.quiz.persistence;

import com.cheatsheet.quiz.domain.Hint;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration-тесты {@link HintRepository} на реальном PostgreSQL.
 * Раньше класс не был покрыт ни одним тестом — регрессии в SQL или
 * RowMapper ловились только при ручной проверке /api/hint endpoint.
 */
class HintRepositoryTest extends AbstractPostgresRepositoryTest {

    private HintRepository repository;
    private QuestionRepository questions;

    @BeforeEach
    void initRepository() {
        repository = new HintRepository(jdbcTemplate);
        questions = new QuestionRepository(jdbcTemplate);
    }

    private long insertSampleQuestion(String slug) {
        Question q = new Question(0L, slug, slug, "test/" + slug + ".md", "test",
                "Q?", "A", false, "h-" + slug,
                QuestionType.TEXT, null, null, 0, null);
        return questions.insert(q);
    }

    @Test
    void insertAllPersistsThreeHintsAndFindByQuestionIdReturnsOrderedByLevel() {
        long qid = insertSampleQuestion("a.md#Q1");
        repository.insertAll(List.of(
                new Hint(0L, qid, 3, "почти ответ", 3_000L),
                new Hint(0L, qid, 1, "лёгкий намёк", 1_000L),
                new Hint(0L, qid, 2, "конкретнее", 2_000L)));

        List<Hint> found = repository.findByQuestionId(qid);

        assertThat(found).hasSize(3);
        assertThat(found).extracting(Hint::level).containsExactly(1, 2, 3);
        assertThat(found.get(0).hintText()).isEqualTo("лёгкий намёк");
    }

    @Test
    void findByQuestionIdAndLevelReturnsSpecificHint() {
        long qid = insertSampleQuestion("b.md#Q1");
        repository.insertAll(List.of(
                new Hint(0L, qid, 1, "easy", 1_000L),
                new Hint(0L, qid, 2, "med",  2_000L)));

        Optional<Hint> level2 = repository.findByQuestionIdAndLevel(qid, 2);

        assertThat(level2).isPresent();
        assertThat(level2.get().hintText()).isEqualTo("med");
    }

    @Test
    void findByQuestionIdAndLevelReturnsEmptyWhenAbsent() {
        long qid = insertSampleQuestion("c.md#Q1");
        repository.insertAll(List.of(new Hint(0L, qid, 1, "x", 1_000L)));

        assertThat(repository.findByQuestionIdAndLevel(qid, 3)).isEmpty();
    }

    @Test
    void deleteByQuestionIdRemovesOnlyHintsForThatQuestion() {
        long qid1 = insertSampleQuestion("d.md#Q1");
        long qid2 = insertSampleQuestion("d.md#Q2");
        repository.insertAll(List.of(new Hint(0L, qid1, 1, "h", 0L), new Hint(0L, qid2, 1, "h", 0L)));

        repository.deleteByQuestionId(qid1);

        assertThat(repository.findByQuestionId(qid1)).isEmpty();
        assertThat(repository.findByQuestionId(qid2)).hasSize(1);
    }

    @Test
    void deleteAllReturnsTotalDeleted() {
        long qid = insertSampleQuestion("e.md#Q1");
        repository.insertAll(List.of(
                new Hint(0L, qid, 1, "x", 0L),
                new Hint(0L, qid, 2, "y", 0L)));

        int deleted = repository.deleteAll();

        assertThat(deleted).isEqualTo(2);
        assertThat(repository.findByQuestionId(qid)).isEmpty();
    }
}
