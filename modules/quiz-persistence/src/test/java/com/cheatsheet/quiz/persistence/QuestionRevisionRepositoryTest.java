package com.cheatsheet.quiz.persistence;

import com.cheatsheet.quiz.domain.QuestionRevision;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты {@link QuestionRevisionRepository} на реальном PostgreSQL через Testcontainers.
 */
class QuestionRevisionRepositoryTest extends AbstractPostgresRepositoryTest {

    private QuestionRevisionRepository repository;

    @BeforeEach
    void initRepository() {
        jdbcTemplate.update("""
                INSERT INTO questions (slug, source_slug, file_path, topic, question_text, answer_markdown, source_hash)
                VALUES ('test.md#Q1', 'test.md#Q1', 'test.md', 'Java', 'Question?', 'Answer', 'hash1')
                """);
        repository = new QuestionRevisionRepository(jdbcTemplate);
    }

    @Test
    void appendAssignsIncrementingRevisionNumbers() {
        QuestionRevision first = repository.append(1L, "hash1", 1000L);
        QuestionRevision second = repository.append(1L, "hash2", 2000L);

        assertThat(first.id()).isPositive();
        assertThat(first.revisionNumber()).isEqualTo(1);
        assertThat(first.checksum()).isEqualTo("hash1");
        assertThat(first.publicationStatus()).isEqualTo("PUBLISHED");
        assertThat(first.createdAt()).isEqualTo(1000L);

        assertThat(second.revisionNumber()).isEqualTo(2);
        assertThat(second.checksum()).isEqualTo("hash2");
    }

    @Test
    void findLatestReturnsHighestRevision() {
        repository.append(1L, "hash1", 1000L);
        repository.append(1L, "hash2", 2000L);

        Optional<QuestionRevision> latest = repository.findLatest(1L);

        assertThat(latest).isPresent();
        assertThat(latest.get().revisionNumber()).isEqualTo(2);
        assertThat(latest.get().checksum()).isEqualTo("hash2");
    }

    @Test
    void findLatestReturnsEmptyForUnknownQuestion() {
        assertThat(repository.findLatest(999L)).isEmpty();
    }

    @Test
    void findByQuestionIdReturnsNewestFirst() {
        repository.append(1L, "hash1", 1000L);
        repository.append(1L, "hash2", 2000L);
        repository.append(1L, "hash3", 3000L);

        List<QuestionRevision> revisions = repository.findByQuestionId(1L);

        assertThat(revisions).hasSize(3);
        assertThat(revisions).extracting(QuestionRevision::revisionNumber).containsExactly(3, 2, 1);
    }
}
