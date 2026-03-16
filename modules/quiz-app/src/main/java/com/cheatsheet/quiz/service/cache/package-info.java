/**
 * Кэширование вариантов ответов (in-memory, Caffeine).
 *
 * <p>Интерфейс {@link com.cheatsheet.quiz.service.cache.OptionCache} уменьшает нагрузку на БД и AI
 * при повторном показе одного и того же вопроса.</p>
 *
 * @see com.cheatsheet.quiz.service.cache.OptionCache
 * @see com.cheatsheet.quiz.service.cache.CaffeineOptionCache
 */
package com.cheatsheet.quiz.service.cache;
