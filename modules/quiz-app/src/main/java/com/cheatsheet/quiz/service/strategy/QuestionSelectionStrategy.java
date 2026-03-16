package com.cheatsheet.quiz.service.strategy;

import com.cheatsheet.quiz.domain.InterviewFilter;

import java.util.Optional;

/**
 * Стратегия выбора следующего вопроса (Strategy pattern).
 *
 * <p>Позволяет менять алгоритм выбора без изменения {@code InterviewService}:</p>
 * <ul>
 *   <li>{@link DefaultSelectionStrategy} — текущая логика (due → next по теме);</li>
 *   <li>{@link WeakTopicsSelectionStrategy} — приоритет слабым темам;</li>
 *   <li>{@link ShuffleSelectionStrategy} — случайный выбор.</li>
 * </ul>
 */
public interface QuestionSelectionStrategy {

    /**
     * Выбирает ID следующего вопроса по заданному фильтру.
     *
     * @param filter   фильтр (тема, важные, только с ошибками, перемешивание)
     * @param nowEpoch текущее время в epoch-секундах
     * @return ID вопроса или пустой Optional, если вопросов нет
     */
    Optional<Long> selectNextQuestionId(InterviewFilter filter, long nowEpoch);
}
