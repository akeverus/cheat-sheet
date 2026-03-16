package com.cheatsheet.quiz.service.cache;

import com.cheatsheet.quiz.domain.AnswerOption;

import java.util.List;

/**
 * Контракт in-memory кэша вариантов ответов.
 *
 * <p>Кэш ускоряет повторные запросы вариантов для одного и того же вопроса,
 * исключая лишние обращения к БД.</p>
 *
 * <p>Реализация: {@link CaffeineOptionCache} (Caffeine cache).</p>
 */
public interface OptionCache {

    /**
     * Получает варианты из кэша.
     *
     * @param questionId идентификатор вопроса
     * @return список вариантов или {@code null} если не в кэше
     */
    List<AnswerOption> get(long questionId);

    /**
     * Помещает варианты в кэш.
     *
     * @param questionId идентификатор вопроса
     * @param options    список вариантов
     */
    void put(long questionId, List<AnswerOption> options);

    /**
     * Инвалидирует запись кэша (при обновлении контента вопроса).
     *
     * @param questionId идентификатор вопроса
     */
    void invalidate(long questionId);

    /**
     * Очищает весь кэш (при перегенерации вариантов ответов).
     */
    void invalidateAll();
}
