package com.cheatsheet.quiz.persistence;

import com.cheatsheet.quiz.domain.QuestionIssue;
import com.cheatsheet.quiz.domain.QuestionIssueCategory;
import com.cheatsheet.quiz.domain.QuestionIssueStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты {@link QuestionIssueRepository} на реальном PostgreSQL через Testcontainers.
 */
class QuestionIssueRepositoryTest extends AbstractPostgresRepositoryTest {

    private QuestionIssueRepository repository;

    @BeforeEach
    void initRepository() {
        // Вопрос-заглушка для FK question_issue(question_id) → questions(id). id=1.
        jdbcTemplate.update("""
                INSERT INTO questions (slug, source_slug, file_path, topic, question_text, answer_markdown, source_hash)
                VALUES ('test.md#Q1', 'test.md#Q1', 'test.md', 'Java', 'Question?', 'Answer', 'hash1')
                """);
        repository = new QuestionIssueRepository(jdbcTemplate);
    }

    @Test
    void saveReturnsIdAndPersistsIssue() {
        Instant now = Instant.parse("2026-03-02T10:15:30Z");

        long id = repository.save(1L, QuestionIssueCategory.INCORRECT_ANSWER, "неверный разбор", now);

        assertThat(id).isPositive();
        List<QuestionIssue> issues = repository.findByQuestionId(1L);
        assertThat(issues).hasSize(1);
        QuestionIssue issue = issues.get(0);
        assertThat(issue.id()).isEqualTo(id);
        assertThat(issue.questionId()).isEqualTo(1L);
        assertThat(issue.category()).isEqualTo(QuestionIssueCategory.INCORRECT_ANSWER);
        assertThat(issue.comment()).isEqualTo("неверный разбор");
        assertThat(issue.status()).isEqualTo(QuestionIssueStatus.OPEN);
        assertThat(issue.createdAt()).isEqualTo(now);
    }

    @Test
    void saveAcceptsNullComment() {
        long id = repository.save(1L, QuestionIssueCategory.OTHER, null, Instant.parse("2026-03-02T00:00:00Z"));

        QuestionIssue issue = repository.findByQuestionId(1L).get(0);
        assertThat(issue.id()).isEqualTo(id);
        assertThat(issue.comment()).isNull();
    }

    @Test
    void findByQuestionIdReturnsNewestFirst() {
        repository.save(1L, QuestionIssueCategory.TYPO, "первая", Instant.parse("2026-03-01T00:00:00Z"));
        repository.save(1L, QuestionIssueCategory.UNCLEAR, "вторая", Instant.parse("2026-03-05T00:00:00Z"));

        List<QuestionIssue> issues = repository.findByQuestionId(1L);

        assertThat(issues).hasSize(2);
        assertThat(issues.get(0).comment()).isEqualTo("вторая");
        assertThat(issues.get(1).comment()).isEqualTo("первая");
    }

    @Test
    void findByQuestionIdReturnsEmptyForUnknown() {
        assertThat(repository.findByQuestionId(999L)).isEmpty();
    }

    @Test
    void countByStatusCountsOnlyMatchingStatus() {
        repository.save(1L, QuestionIssueCategory.TYPO, "a", Instant.parse("2026-03-01T00:00:00Z"));
        repository.save(1L, QuestionIssueCategory.OTHER, "b", Instant.parse("2026-03-02T00:00:00Z"));

        assertThat(repository.countByStatus(QuestionIssueStatus.OPEN)).isEqualTo(2);
        assertThat(repository.countByStatus(QuestionIssueStatus.RESOLVED)).isZero();
    }
}
