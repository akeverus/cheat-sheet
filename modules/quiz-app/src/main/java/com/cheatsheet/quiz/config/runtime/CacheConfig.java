package com.cheatsheet.quiz.config.runtime;

import com.cheatsheet.quiz.config.app.AppProperties;
import com.cheatsheet.quiz.domain.AnswerOption;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.util.concurrent.Striped;
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
    Cache<Long, List<AnswerOption>> optionCacheBackend(AppProperties props) {
        return Caffeine.newBuilder()
                .maximumSize(props.getCache().getOptionMaxSize())
                .expireAfterWrite(props.getCache().getTtlHours(), TimeUnit.HOURS)
                .build();
    }

    @Bean
    Striped<Lock> questionLocks() {
        return Striped.lock(128);
    }
}
