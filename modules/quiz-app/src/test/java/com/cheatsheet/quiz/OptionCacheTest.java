package com.cheatsheet.quiz;

import static org.assertj.core.api.Assertions.assertThat;

import com.cheatsheet.quiz.domain.AnswerOption;
import com.cheatsheet.quiz.service.cache.CaffeineOptionCache;
import com.cheatsheet.quiz.service.cache.OptionCache;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OptionCacheTest {

    private OptionCache cache;

    @BeforeEach
    void setUp() {
        Cache<Long, List<AnswerOption>> backend = Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(1, TimeUnit.HOURS)
                .build();
        cache = new CaffeineOptionCache(backend);
    }

    @Test
    void storesAndInvalidatesOptions() {
        List<AnswerOption> options = List.of(
                new AnswerOption(1L, 1L, "A", true, 0, "test", null),
                new AnswerOption(2L, 1L, "B", false, 1, "test", null)
        );
        cache.put(1L, options);

        assertThat(cache.get(1L)).hasSize(2);

        cache.invalidate(1L);
        assertThat(cache.get(1L)).isNull();
    }

    @Test
    void putNullIsIgnored() {
        cache.put(1L, null);
        assertThat(cache.get(1L)).isNull();
    }
}
