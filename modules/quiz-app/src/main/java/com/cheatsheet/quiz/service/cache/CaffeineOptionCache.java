package com.cheatsheet.quiz.service.cache;

import lombok.RequiredArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import com.cheatsheet.quiz.domain.AnswerOption;
import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Реализация {@link OptionCache} на основе Caffeine cache.
 *
 * <p>Параметры кэша (max size, TTL) задаются через {@code app.cache.*}
 * и применяются в {@link com.cheatsheet.quiz.config.app.InfrastructureConfig}.</p>
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CaffeineOptionCache implements OptionCache {

    /** Caffeine cache backend (сконфигурирован в InfrastructureConfig). */
    private final Cache<Long, List<AnswerOption>> cache;

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
