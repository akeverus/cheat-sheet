package com.cheatsheet.quiz.persistence;

import com.cheatsheet.quiz.domain.Attempt;
import com.cheatsheet.quiz.domain.AttemptOutcome;
import com.cheatsheet.quiz.domain.QuestionRevision;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты {@link AttemptRepository} на реальном PostgreSQL через Testcontainers.
 */
class AttemptRepositoryTest extends AbstractPostgresRepositoryTest {

    private AttemptRepository repository;
    private QuestionRevisionRepository revisionRepository;
    private long optionId;

    @BeforeEach
    void initRepository() {
        jdbcTemplate.update("""
                INSERT INTO questions (slug, source_slug, file_path, topic, question_text, answer_markdown, source_hash)
                VALUES ('test.md#Q1', 'test.md#Q1', 'test.md', 'Java', 'Question?', 'Answer', 'hash1')
                """);
        optionId = jdbcTemplate.queryForObject("""
                INSERT INTO answer_options (question_id, option_text, is_correct, display_order, source)
                VALUES (1, 'Option A', 1, 0, 'SEED') RETURNING id
                """, Long.class);
        repository = new AttemptRepository(jdbcTemplate);
        revisionRepository = new QuestionRevisionRepository(jdbcTemplate);
    }

    @Test
    void insertPersistsAllFieldsAndReturnsId() {
        QuestionRevision revision = revisionRepository.append(1L, "hash1", 500L);
        Attempt attempt = Attempt.builder()
                .questionId(1L)
                .questionRevisionId(revision.id())
                .selectedOptionId(optionId)
                .outcome(AttemptOutcome.CORRECT)
                .firstAttempt(true)
                .responseTimeMs(4200)
                .memoryGrade(5)
                .sessionToken("sess-1")
                .idempotencyKey("idem-1")
                .createdAt(1000L)
                .build();

        long id = repository.insert(attempt);

        assertThat(id).isPositive();
        List<Attempt> attempts = repository.findByQuestionId(1L);
        assertThat(attempts).hasSize(1);
        Attempt saved = attempts.get(0);
        assertThat(saved.id()).isEqualTo(id);
        assertThat(saved.questionId()).isEqualTo(1L);
        assertThat(saved.questionRevisionId()).isEqualTo(revision.id());
        assertThat(saved.selectedOptionId()).isEqualTo(optionId);
        assertThat(saved.outcome()).isEqualTo(AttemptOutcome.CORRECT);
        assertThat(saved.firstAttempt()).isTrue();
        assertThat(saved.responseTimeMs()).isEqualTo(4200);
        assertThat(saved.memoryGrade()).isEqualTo(5);
        assertThat(saved.sessionToken()).isEqualTo("sess-1");
        assertThat(saved.idempotencyKey()).isEqualTo("idem-1");
        assertThat(saved.createdAt()).isEqualTo(1000L);
    }

    @Test
    void insertAcceptsNullableFields() {
        Attempt attempt = Attempt.builder()
                .questionId(1L)
                .outcome(AttemptOutcome.SKIP)
                .firstAttempt(false)
                .createdAt(2000L)
                .build();

        long id = repository.insert(attempt);

        Attempt saved = repository.findByQuestionId(1L).get(0);
        assertThat(saved.id()).isEqualTo(id);
        assertThat(saved.questionRevisionId()).isNull();
        assertThat(saved.selectedOptionId()).isNull();
        assertThat(saved.responseTimeMs()).isNull();
        assertThat(saved.memoryGrade()).isNull();
        assertThat(saved.sessionToken()).isNull();
        assertThat(saved.idempotencyKey()).isNull();
        assertThat(saved.outcome()).isEqualTo(AttemptOutcome.SKIP);
    }

    @Test
    void existsByQuestionIdReflectsPresence() {
        assertThat(repository.existsByQuestionId(1L)).isFalse();

        repository.insert(Attempt.builder()
                .questionId(1L).outcome(AttemptOutcome.WRONG).firstAttempt(true).createdAt(1000L).build());

        assertThat(repository.existsByQuestionId(1L)).isTrue();
        assertThat(repository.existsByQuestionId(999L)).isFalse();
    }

    @Test
    void findByIdempotencyKeyReturnsMatch() {
        repository.insert(Attempt.builder()
                .questionId(1L).outcome(AttemptOutcome.CORRECT).firstAttempt(true)
                .idempotencyKey("key-abc").createdAt(1000L).build());

        Optional<Attempt> found = repository.findByIdempotencyKey("key-abc");

        assertThat(found).isPresent();
        assertThat(found.get().idempotencyKey()).isEqualTo("key-abc");
        assertThat(repository.findByIdempotencyKey("no-such-key")).isEmpty();
        assertThat(repository.findByIdempotencyKey(null)).isEmpty();
    }

    @Test
    void findByQuestionIdReturnsNewestFirst() {
        repository.insert(Attempt.builder()
                .questionId(1L).outcome(AttemptOutcome.WRONG).firstAttempt(true).createdAt(1000L).build());
        repository.insert(Attempt.builder()
                .questionId(1L).outcome(AttemptOutcome.CORRECT).firstAttempt(false).createdAt(2000L).build());

        List<Attempt> attempts = repository.findByQuestionId(1L);

        assertThat(attempts).hasSize(2);
        assertThat(attempts.get(0).createdAt()).isEqualTo(2000L);
        assertThat(attempts.get(1).createdAt()).isEqualTo(1000L);
    }
}
