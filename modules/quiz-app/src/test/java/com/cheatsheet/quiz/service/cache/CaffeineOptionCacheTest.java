package com.cheatsheet.quiz.service.cache;

import com.cheatsheet.quiz.domain.AnswerOption;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CaffeineOptionCacheTest {

    private CaffeineOptionCache optionCache;

    @BeforeEach
    void setUp() {
        Cache<Long, List<AnswerOption>> backing = Caffeine.newBuilder().maximumSize(100).build();
        optionCache = new CaffeineOptionCache(backing);
    }

    private List<AnswerOption> sampleOptions(long qid) {
        return List.of(
                new AnswerOption(1L, qid, "A", true, 0, "MARKDOWN", "ok"),
                new AnswerOption(2L, qid, "B", false, 1, "MARKDOWN", "no")
        );
    }

    @Test
    void getReturnsNullForUnknownKey() {
        assertThat(optionCache.get(42L)).isNull();
    }

    @Test
    void putThenGetReturnsSameList() {
        List<AnswerOption> options = sampleOptions(7L);

        optionCache.put(7L, options);

        assertThat(optionCache.get(7L)).isEqualTo(options);
    }

    @Test
    void putAcceptsEmptyListAndKeepsIt() {
        // Empty list — это валидный сигнал «опций нет», не cache-miss.
        optionCache.put(8L, List.of());

        assertThat(optionCache.get(8L)).isNotNull().isEmpty();
    }

    @Test
    void putWithNullIsNoopAndDoesNotPoisonCache() {
        optionCache.put(9L, null);

        assertThat(optionCache.get(9L)).isNull();
    }

    @Test
    void invalidateRemovesSingleKey() {
        optionCache.put(1L, sampleOptions(1L));
        optionCache.put(2L, sampleOptions(2L));

        optionCache.invalidate(1L);

        assertThat(optionCache.get(1L)).isNull();
        assertThat(optionCache.get(2L)).isNotNull();
    }

    @Test
    void invalidateAllWipesEverything() {
        optionCache.put(1L, sampleOptions(1L));
        optionCache.put(2L, sampleOptions(2L));

        optionCache.invalidateAll();

        assertThat(optionCache.get(1L)).isNull();
        assertThat(optionCache.get(2L)).isNull();
    }
}
