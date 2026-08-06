package com.cheatsheet.quiz.persistence;

import com.cheatsheet.quiz.domain.FsrsState;
import com.cheatsheet.quiz.domain.ReviewDefaults;
import com.cheatsheet.quiz.domain.ReviewResult;
import com.cheatsheet.quiz.domain.ReviewState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты {@link ReviewStateRepository} на реальном PostgreSQL через Testcontainers.
 */
class ReviewStateRepositoryTest extends AbstractPostgresRepositoryTest {

    private ReviewStateRepository repository;

    @BeforeEach
    void initRepository() {
        // Вопрос-заглушка для FK на review_state(question_id) → questions(id).
        // BIGSERIAL даст id=1, его и используем дальше в тестах.
        jdbcTemplate.update("""
                INSERT INTO questions (slug, source_slug, file_path, topic, question_text, answer_markdown, source_hash)
                VALUES ('test.md#Q1', 'test.md#Q1', 'test.md', 'Java', 'Question?', 'Answer', 'hash1')
                """);
        repository = new ReviewStateRepository(jdbcTemplate);
    }

    /**
     * Первый ответ на вопрос, у которого строки прогресса ещё нет, обязан её создать.
     *
     * <p>Строку заводил показ вопроса, а {@code update} был голым UPDATE. Когда показ
     * перестал писать в БД, такой UPDATE трогал бы ноль строк и первый ответ пропадал бы
     * молча — без исключения и без следа в статистике.
     */
    @Test
    void updateCreatesRowWhenProgressAbsent() {
        assertThat(repository.findByQuestionId(1L)).isEmpty();

        repository.update(new ReviewState(1L, 1, 1, 2.5, 2000L, ReviewResult.CORRECT, 1, 0));

        Optional<ReviewState> state = repository.findByQuestionId(1L);
        assertThat(state).isPresent();
        assertThat(state.get().correctCount()).isEqualTo(1);
        assertThat(state.get().nextReviewAt()).isEqualTo(2000L);
        assertThat(state.get().lastResult()).isEqualTo(ReviewResult.CORRECT);
    }

    /** То же для FSRS-ветки планировщика: она пишет своей командой, а не через {@code update}. */
    @Test
    void updateFsrsCreatesRowWhenProgressAbsent() {
        assertThat(repository.findByQuestionId(1L)).isEmpty();

        repository.updateFsrs(
                new FsrsState(1L, 3.0, 5.0, 0, "fsrs-4.5-default", 1_800_000_000L),
                1_800_500_000L, ReviewResult.CORRECT, 1, 0);

        ReviewState sm2 = repository.findByQuestionId(1L).orElseThrow();
        assertThat(sm2.correctCount()).isEqualTo(1);
        assertThat(sm2.nextReviewAt()).isEqualTo(1_800_500_000L);
        // Поля SM-2 не заданы FSRS-командой — их проставила схема значениями по умолчанию.
        assertThat(sm2.repetitions()).isZero();
        assertThat(sm2.easeFactor()).isEqualTo(ReviewDefaults.DEFAULT_EASE_FACTOR);
    }

    @Test
    void findByQuestionIdReturnsEmptyForUnknown() {
        assertThat(repository.findByQuestionId(999L)).isEmpty();
    }

    @Test
    void findFsrsStateReturnsNullMemoryBeforeFirstFsrsUpdate() {
        repository.update(ReviewDefaults.initialState(1L, 1000L));

        FsrsState fsrs = repository.findFsrsState(1L).orElseThrow();
        assertThat(fsrs.questionId()).isEqualTo(1L);
        assertThat(fsrs.isNew()).isTrue();
        assertThat(fsrs.stability()).isNull();
        assertThat(fsrs.difficulty()).isNull();
        assertThat(fsrs.lapses()).isZero();
        assertThat(fsrs.algoVersion()).isNull();
        assertThat(fsrs.lastReviewedAt()).isNull();
    }

    @Test
    void updateFsrsPersistsMemoryAndSharedScheduleWithoutTouchingSm2() {
        // Кладём SM-2-прогресс, который FSRS-апдейт трогать НЕ должен.
        repository.update(new ReviewState(1L, 4, 9, 2.4, 1000L, ReviewResult.CORRECT, 3, 1));

        FsrsState memory = new FsrsState(1L, 12.5, 6.0, 2, "fsrs-4.5-default", 1_800_000_000L);
        repository.updateFsrs(memory, 1_800_500_000L, ReviewResult.WRONG, 3, 2);

        FsrsState fsrs = repository.findFsrsState(1L).orElseThrow();
        assertThat(fsrs.stability()).isEqualTo(12.5);
        assertThat(fsrs.difficulty()).isEqualTo(6.0);
        assertThat(fsrs.lapses()).isEqualTo(2);
        assertThat(fsrs.algoVersion()).isEqualTo("fsrs-4.5-default");
        assertThat(fsrs.lastReviewedAt()).isEqualTo(1_800_000_000L);

        ReviewState sm2 = repository.findByQuestionId(1L).orElseThrow();
        // Общие поля обновлены FSRS-апдейтом…
        assertThat(sm2.nextReviewAt()).isEqualTo(1_800_500_000L);
        assertThat(sm2.lastResult()).isEqualTo(ReviewResult.WRONG);
        assertThat(sm2.wrongCount()).isEqualTo(2);
        // …а SM-2-память осталась нетронутой (fallback).
        assertThat(sm2.repetitions()).isEqualTo(4);
        assertThat(sm2.intervalDays()).isEqualTo(9);
        assertThat(sm2.easeFactor()).isEqualTo(2.4);
    }

    @Test
    void resetClearsBothSm2AndFsrsMemory() {
        // Наполняем и SM-2, и FSRS-память «выученным» прогрессом.
        repository.update(new ReviewState(1L, 6, 30, 2.6, 1000L, ReviewResult.CORRECT, 8, 1));
        repository.updateFsrs(
                new FsrsState(1L, 20.0, 7.5, 3, "fsrs-4.5-default", 1_800_000_000L),
                1_800_500_000L, ReviewResult.CORRECT, 8, 1);

        repository.reset(1L, 9_000L);

        // SM-2 сброшен к дефолтам.
        ReviewState sm2 = repository.findByQuestionId(1L).orElseThrow();
        assertThat(sm2.repetitions()).isZero();
        assertThat(sm2.intervalDays()).isZero();
        assertThat(sm2.easeFactor()).isEqualTo(ReviewDefaults.DEFAULT_EASE_FACTOR);
        assertThat(sm2.nextReviewAt()).isEqualTo(9_000L);
        assertThat(sm2.lastResult()).isEqualTo(ReviewResult.RESET);

        // FSRS-память тоже сброшена в «свежую»: до фикса stability/difficulty/lapses/algo_version
        // переживали reset, и в FSRS-режиме изменённый вопрос сохранял устаревшую mastery.
        FsrsState fsrs = repository.findFsrsState(1L).orElseThrow();
        assertThat(fsrs.isNew()).isTrue();
        assertThat(fsrs.stability()).isNull();
        assertThat(fsrs.difficulty()).isNull();
        assertThat(fsrs.lapses()).isZero();
        assertThat(fsrs.algoVersion()).isNull();
    }

    @Test
    void updateModifiesAllFields() {
        ReviewState state = new ReviewState(1L, 3, 7, 2.2, 5000L, ReviewResult.CORRECT, 10, 2);
        repository.update(state);

        ReviewState reloaded = repository.findByQuestionId(1L).orElseThrow();
        assertThat(reloaded.repetitions()).isEqualTo(3);
        assertThat(reloaded.intervalDays()).isEqualTo(7);
        assertThat(reloaded.easeFactor()).isEqualTo(2.2);
        assertThat(reloaded.nextReviewAt()).isEqualTo(5000L);
        assertThat(reloaded.lastResult()).isEqualTo(ReviewResult.CORRECT);
        assertThat(reloaded.correctCount()).isEqualTo(10);
        assertThat(reloaded.wrongCount()).isEqualTo(2);
    }
}
