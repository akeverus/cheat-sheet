package com.cheatsheet.quiz.api.security;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayDeque;

/**
 * In-memory rate limiter со скользящим окном и автоматическим eviction.
 *
 * <p>Используется для защиты чувствительных endpoint от abuse без внешнего Redis.</p>
 *
 * <p>Записи автоматически удаляются через 5 минут неактивности (Caffeine expireAfterAccess),
 * предотвращая утечку памяти при большом количестве уникальных клиентов.</p>
 */
@Component
public class RequestRateLimiter {

    private static final int MAX_ENTRIES = 10_000;
    private static final Duration EVICTION_AFTER_ACCESS = Duration.ofMinutes(5);

    private final Cache<String, ArrayDeque<Long>> windows = Caffeine.newBuilder()
            .maximumSize(MAX_ENTRIES)
            .expireAfterAccess(EVICTION_AFTER_ACCESS)
            .build();

    /**
     * Проверяет, разрешён ли запрос по ключу в заданном окне.
     *
     * @param key         идентификатор клиента/endpoint
     * @param maxRequests лимит запросов за окно
     * @param window      окно лимитирования
     * @return true, если запрос разрешён
     */
    public boolean allow(String key, int maxRequests, Duration window) {
        long now = System.currentTimeMillis();
        long cutoff = now - window.toMillis();
        ArrayDeque<Long> timestamps = windows.get(key, ignored -> new ArrayDeque<>());

        synchronized (timestamps) {
            while (!timestamps.isEmpty() && timestamps.peekFirst() < cutoff) {
                timestamps.pollFirst();
            }
            if (timestamps.size() >= maxRequests) {
                return false;
            }
            timestamps.addLast(now);
            return true;
        }
    }
}
