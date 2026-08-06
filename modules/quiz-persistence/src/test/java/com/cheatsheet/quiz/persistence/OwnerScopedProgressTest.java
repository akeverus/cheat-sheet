package com.cheatsheet.quiz.persistence;

import com.cheatsheet.quiz.domain.ReviewResult;
import com.cheatsheet.quiz.domain.ReviewState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OwnerScopedProgressTest extends AbstractPostgresRepositoryTest {

    private ReviewStateRepository reviewStateRepository;
    private BookmarkRepository bookmarkRepository;
    private long questionId;

    @BeforeEach
    void seedQuestion() {
        jdbcTemplate.update("""
                INSERT INTO questions (slug, source_slug, file_path, topic, question_text, answer_markdown, source_hash)
                VALUES ('owner-q', 'owner-q', 'f.md', 'java', 'Q?', 'A', 'hash')
                """);
        questionId = jdbcTemplate.queryForObject("SELECT id FROM questions WHERE slug = 'owner-q'", Long.class);
        reviewStateRepository = new ReviewStateRepository(jdbcTemplate);
        bookmarkRepository = new BookmarkRepository(jdbcTemplate);
    }

    @AfterEach
    void clearOwner() {
        OwnerContext.clear();
    }

    @Test
    void reviewStateIsIsolatedPerOwner() {
        OwnerContext.set("alice");
        reviewStateRepository.update(new ReviewState(
                questionId, 2, 3, 2.5, 2000L, ReviewResult.CORRECT, 1, 0));

        OwnerContext.set("bob");
        assertThat(reviewStateRepository.findByQuestionId(questionId)).isEmpty();
        reviewStateRepository.update(new ReviewState(
                questionId, 1, 1, 2.5, 1000L, ReviewResult.WRONG, 0, 1));
        assertThat(reviewStateRepository.findByQuestionId(questionId)).isPresent();

        OwnerContext.set("alice");
        assertThat(reviewStateRepository.findByQuestionId(questionId))
                .get()
                .extracting(ReviewState::repetitions)
                .isEqualTo(2);
    }

    @Test
    void bookmarksAreIsolatedPerOwner() {
        OwnerContext.set("alice");
        bookmarkRepository.upsert(questionId, "alice note", 1000L);

        OwnerContext.set("bob");
        assertThat(bookmarkRepository.findByQuestionId(questionId)).isEmpty();
        bookmarkRepository.upsert(questionId, "bob note", 1000L);

        OwnerContext.set("alice");
        assertThat(bookmarkRepository.findByQuestionId(questionId))
                .get()
                .extracting(b -> b.note())
                .isEqualTo("alice note");
        assertThat(bookmarkRepository.count()).isEqualTo(1L);
    }
}
