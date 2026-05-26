package com.cheatsheet.quiz.persistence;

import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration-тесты {@link PostgresFullTextSearchRepository} — проверяют что
 * tsvector-триггер из V2__fulltext.sql действительно индексирует вопросы и
 * что поиск возвращает их с подсветкой.
 */
class PostgresFullTextSearchRepositoryTest extends AbstractPostgresRepositoryTest {

    private PostgresFullTextSearchRepository repository;
    private QuestionRepository questions;

    @BeforeEach
    void initRepository() {
        repository = new PostgresFullTextSearchRepository(jdbcTemplate);
        questions = new QuestionRepository(jdbcTemplate);
    }

    private long insertQuestion(String slug, String text, String answer) {
        Question q = new Question(0L, slug, slug, "test/" + slug + ".md", "test",
                text, answer, false, "h-" + slug,
                QuestionType.TEXT, null, null, 0, null);
        return questions.insert(q);
    }

    @Test
    void searchBlankQueryReturnsEmpty() {
        assertThat(repository.search("", 10)).isEmpty();
        assertThat(repository.search("   ", 10)).isEmpty();
    }

    @Test
    void searchFindsQuestionByQuestionText() {
        long qid = insertQuestion("a.md#Q1",
                "Что такое транзакция в базе данных?",
                "ACID-свойства и изоляция уровней.");

        List<FullTextSearchRepository.SearchResult> results = repository.search("транзакция", 10);

        assertThat(results).extracting(FullTextSearchRepository.SearchResult::questionId)
                .contains(qid);
    }

    @Test
    void searchFindsQuestionByAnswerMarkdown() {
        long qid = insertQuestion("b.md#Q1",
                "Что такое X?",
                "X — это компонент Spring Framework для управления зависимостями.");

        List<FullTextSearchRepository.SearchResult> results = repository.search("Spring", 10);

        assertThat(results).extracting(FullTextSearchRepository.SearchResult::questionId)
                .contains(qid);
    }

    @Test
    void searchHonoursLimit() {
        for (int i = 1; i <= 5; i++) {
            insertQuestion("c.md#Q" + i,
                    "Question about HashMap " + i,
                    "HashMap implementation details for case " + i);
        }

        List<FullTextSearchRepository.SearchResult> results = repository.search("HashMap", 3);

        assertThat(results).hasSizeLessThanOrEqualTo(3);
    }

    @Test
    void searchReturnsEmptyWhenNoMatch() {
        insertQuestion("d.md#Q1", "Что такое Spring?", "Это framework.");

        List<FullTextSearchRepository.SearchResult> results =
                repository.search("MaybeNoSuchWordExists12345", 10);

        assertThat(results).isEmpty();
    }
}
