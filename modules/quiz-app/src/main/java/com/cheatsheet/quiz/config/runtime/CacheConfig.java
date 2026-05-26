package com.cheatsheet.quiz.config.runtime;

import com.cheatsheet.quiz.config.app.AppProperties;
import com.cheatsheet.quiz.domain.AnswerOption;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.util.concurrent.Striped;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.cache.CaffeineCacheMetrics;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * Конфигурация Caffeine-кэша вариантов ответов и Striped-блокировок.
 */
@Configuration
public class CacheConfig {

    @Bean
    Cache<Long, List<AnswerOption>> optionCacheBackend(AppProperties props, MeterRegistry meterRegistry) {
        // recordStats() обязателен — без него CaffeineCacheMetrics видит нули.
        Cache<Long, List<AnswerOption>> cache = Caffeine.newBuilder()
                .maximumSize(props.getCache().getOptionMaxSize())
                .expireAfterWrite(props.getCache().getTtlHours(), TimeUnit.HOURS)
                .recordStats()
                .build();
        // Экспортирует cache.gets{result=hit|miss}, cache.puts, cache.evictions
        // под тегом cache=optionCache — видно через /actuator/metrics.
        CaffeineCacheMetrics.monitor(meterRegistry, cache, "optionCache");
        return cache;
    }

    @Bean
    Striped<Lock> questionLocks() {
        return Striped.lock(128);
    }
}
