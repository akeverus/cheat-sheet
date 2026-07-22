package com.cheatsheet.quiz.persistence;

import com.cheatsheet.quiz.domain.QuestionIssueCategory;
import com.cheatsheet.quiz.domain.QuestionIssueStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class OwnerScopedQuestionIssueTest extends AbstractPostgresRepositoryTest {

    private QuestionIssueRepository repository;
    private long questionId;

    @BeforeEach
    void seed() {
        jdbcTemplate.update("""
                INSERT INTO questions (slug, source_slug, file_path, topic, question_text, answer_markdown, source_hash)
                VALUES ('issue-q', 'issue-q', 'f.md', 'java', 'Q?', 'A', 'hash')
                """);
        questionId = jdbcTemplate.queryForObject("SELECT id FROM questions WHERE slug = 'issue-q'", Long.class);
        repository = new QuestionIssueRepository(jdbcTemplate);
    }

    @AfterEach
    void clearOwner() {
        OwnerContext.clear();
    }

    @Test
    void issuesAreIsolatedPerOwner() {
        OwnerContext.set("alice");
        repository.save(questionId, QuestionIssueCategory.TYPO, "alice typo", Instant.parse("2026-07-21T00:00:00Z"));
        assertThat(repository.findByQuestionId(questionId)).hasSize(1);
        assertThat(repository.countByStatus(QuestionIssueStatus.OPEN)).isEqualTo(1);

        OwnerContext.set("bob");
        assertThat(repository.findByQuestionId(questionId)).isEmpty();
        assertThat(repository.countByStatus(QuestionIssueStatus.OPEN)).isZero();
        repository.save(questionId, QuestionIssueCategory.OUTDATED, "bob", Instant.parse("2026-07-21T01:00:00Z"));
        assertThat(repository.findByQuestionId(questionId)).hasSize(1);

        OwnerContext.set("alice");
        assertThat(repository.findByQuestionId(questionId)).hasSize(1);
        assertThat(repository.findByQuestionId(questionId).get(0).comment()).isEqualTo("alice typo");
    }
}
