package com.cheatsheet.quiz.service.cache;

import com.cheatsheet.quiz.domain.AnswerOption;
import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Реализация {@link OptionCache} на основе Caffeine cache.
 *
 * <p>Параметры кэша (max size, TTL) задаются через {@code app.cache.*}
 * и применяются в {@link com.cheatsheet.quiz.config.InfrastructureConfig}.</p>
 */
@Service
public class CaffeineOptionCache implements OptionCache {

    /** Caffeine cache backend (сконфигурирован в InfrastructureConfig). */
    private final Cache<Long, List<AnswerOption>> cache;

    public CaffeineOptionCache(Cache<Long, List<AnswerOption>> cache) {
        this.cache = cache;
    }

    /** {@inheritDoc} */
    @Override
    public List<AnswerOption> get(long questionId) {
        return cache.getIfPresent(questionId);
    }

    /** {@inheritDoc} */
    @Override
    public void put(long questionId, List<AnswerOption> options) {
        if (options == null) {
            return;
        }
        cache.put(questionId, options);
    }

    /** {@inheritDoc} */
    @Override
    public void invalidate(long questionId) {
        cache.invalidate(questionId);
    }

    /** {@inheritDoc} */
    @Override
    public void invalidateAll() {
        cache.invalidateAll();
    }
}
