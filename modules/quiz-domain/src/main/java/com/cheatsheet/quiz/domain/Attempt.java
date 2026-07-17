package com.cheatsheet.quiz.domain;

import lombok.Builder;

/**
 * Построчный факт одной попытки ответа (таблица {@code attempt}).
 *
 * <p>Появляется в Фазе 1 как фундамент для честной аналитики (first-attempt
 * accuracy, response time, retention) и истории грейдов FSRS. В отличие от
 * агрегатов {@link ReviewState}, хранит каждый ответ отдельной строкой.</p>
 *
 * @param id                 идентификатор записи (BIGSERIAL); {@code 0} у ещё не сохранённой
 * @param questionId         вопрос, на который отвечали (FK → questions.id)
 * @param questionRevisionId ревизия контента, на которой дан ответ (может быть {@code null})
 * @param selectedOptionId   выбранный вариант (FK → answer_options.id; {@code null} для флешкарт/skip)
 * @param outcome            исход попытки
 * @param firstAttempt       первая ли это попытка по вопросу
 * @param responseTimeMs     время ответа в мс (может быть {@code null})
 * @param memoryGrade        SM-2 grade 0–5 (может быть {@code null})
 * @param sessionToken       токен сессии тренажёра (может быть {@code null})
 * @param idempotencyKey     ключ идемпотентности для дедупа двойного POST (может быть {@code null})
 * @param createdAt          момент попытки, epoch-секунды
 */
@Builder(toBuilder = true)
public record Attempt(
        long id,
        long questionId,
        Long questionRevisionId,
        Long selectedOptionId,
        AttemptOutcome outcome,
        boolean firstAttempt,
        Integer responseTimeMs,
        Integer memoryGrade,
        String sessionToken,
        String idempotencyKey,
        long createdAt
) {}
