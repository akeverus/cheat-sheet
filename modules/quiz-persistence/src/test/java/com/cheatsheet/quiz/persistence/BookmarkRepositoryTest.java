package com.cheatsheet.quiz.persistence;

import com.cheatsheet.quiz.domain.Bookmark;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration-тесты {@link BookmarkRepository} на реальном PostgreSQL.
 *
 * <p>{@code upsert} выполняется атомарно через {@code INSERT … ON CONFLICT
 * (owner_key, question_id) DO UPDATE}: повторное сохранение обновляет заметку и
 * {@code updated_at}, но сохраняет {@code created_at} и не создаёт вторую строку
 * (прежняя схема UPDATE→INSERT роняла второе сохранение в DuplicateKeyException).</p>
 */
class BookmarkRepositoryTest extends AbstractPostgresRepositoryTest {

    private BookmarkRepository repository;

    @BeforeEach
    void initRepository() {
        // Вопросы-заглушки для FK bookmark(question_id) → questions(id). id=1, id=2.
        jdbcTemplate.update("""
                INSERT INTO questions (slug, source_slug, file_path, topic, question_text, answer_markdown, source_hash)
                VALUES ('test.md#Q1', 'test.md#Q1', 'test.md', 'Java', 'Question 1?', 'Answer 1', 'hash1'),
                       ('test.md#Q2', 'test.md#Q2', 'test.md', 'Java', 'Question 2?', 'Answer 2', 'hash2')
                """);
        repository = new BookmarkRepository(jdbcTemplate);
    }

    @Test
    void upsertCreatesBookmarkRetrievableByQuestionId() {
        repository.upsert(1L, "важно повторить", 1_000L);

        Optional<Bookmark> found = repository.findByQuestionId(1L);
        assertThat(found).isPresent();
        Bookmark bookmark = found.get();
        assertThat(bookmark.questionId()).isEqualTo(1L);
        assertThat(bookmark.note()).isEqualTo("важно повторить");
        assertThat(bookmark.createdAt()).isEqualTo(1_000L);
        assertThat(bookmark.updatedAt()).isEqualTo(1_000L);
        assertThat(repository.count()).isEqualTo(1L);
    }

    @Test
    void upsertOnExistingUpdatesNoteAndUpdatedAtPreservingCreatedAt() {
        repository.upsert(1L, "первая заметка", 1_000L);
        // Повторное сохранение того же вопроса: раньше падало DuplicateKeyException,
        // теперь ON CONFLICT обновляет note/updated_at одной атомарной операцией.
        repository.upsert(1L, "обновлённая заметка", 2_000L);

        Bookmark bookmark = repository.findByQuestionId(1L).orElseThrow();
        assertThat(bookmark.note()).isEqualTo("обновлённая заметка");
        assertThat(bookmark.updatedAt()).isEqualTo(2_000L);
        // created_at НЕ входит в EXCLUDED-SET → момент первого сохранения сохранён.
        assertThat(bookmark.createdAt()).isEqualTo(1_000L);
        // Ни дубликата строки, ни второй закладки — по-прежнему одна.
        assertThat(repository.count()).isEqualTo(1L);
    }

    @Test
    void findByQuestionIdReturnsEmptyWhenNoBookmark() {
        assertThat(repository.findByQuestionId(1L)).isEmpty();
    }

    @Test
    void deleteRemovesBookmark() {
        repository.upsert(1L, "удалить меня", 1_000L);
        assertThat(repository.count()).isEqualTo(1L);

        repository.delete(1L);

        assertThat(repository.findByQuestionId(1L)).isEmpty();
        assertThat(repository.count()).isZero();
    }

    @Test
    void findAllOrdersNewestFirstAndCountReflectsRows() {
        repository.upsert(1L, "старая", 1_000L);
        repository.upsert(2L, "новая", 2_000L);

        List<Bookmark> all = repository.findAll();

        assertThat(all).hasSize(2);
        // ORDER BY created_at DESC, id DESC → сначала новейшая (q2).
        assertThat(all.get(0).questionId()).isEqualTo(2L);
        assertThat(all.get(1).questionId()).isEqualTo(1L);
        assertThat(repository.count()).isEqualTo(2L);
    }
}
