package com.cheatsheet.quiz.domain;

import lombok.Builder;

/**
 * Состояние памяти FSRS для вопроса (колонки {@code review_state}, добавленные в V18).
 *
 * <p>Живёт в той же строке {@code review_state}, что и SM-2-состояние
 * ({@link ReviewState}), но описывает независимую модель памяти FSRS. Оба
 * планировщика пишут общее {@code next_review_at}; какой из них последним трогал
 * строку — фиксирует {@link #algoVersion}.</p>
 *
 * <p>{@link #stability} == {@code null} означает, что FSRS ещё не считал этот
 * вопрос (первое повторение) — тогда используется инициализация из грейда.</p>
 *
 * @param questionId     вопрос (FK → questions.id)
 * @param stability      стабильность памяти (дни до R=90%); {@code null} до первого FSRS-апдейта
 * @param difficulty     сложность [1..10]; {@code null} до первого FSRS-апдейта
 * @param lapses         счётчик забываний (рейтинг AGAIN)
 * @param algoVersion    версия алгоритма, писавшего строку ({@code null} если ещё не FSRS)
 * @param lastReviewedAt epoch-секунды последнего ответа ({@code null} если не было)
 */
@Builder(toBuilder = true)
public record FsrsState(
        long questionId,
        Double stability,
        Double difficulty,
        int lapses,
        String algoVersion,
        Long lastReviewedAt
) {
    /** Ещё не считался FSRS (нет памяти) — нужна инициализация из грейда. */
    public boolean isNew() {
        return stability == null || difficulty == null;
    }
}
