package com.cheatsheet.quiz.config.runtime;

import com.cheatsheet.quiz.TestInterviewPath;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Контракт: Caffeine OptionCache должен экспортировать метрики через Micrometer
 * под тегом {@code cache=optionCache}. Без этого теста легко молча сломать
 * вызов CaffeineCacheMetrics.monitor() при рефакторинге CacheConfig.
 */
@SpringBootTest
@ActiveProfiles("test")
class CacheMetricsIntegrationTest {

    @DynamicPropertySource
    static void setInterviewPath(DynamicPropertyRegistry registry) {
        TestInterviewPath.register(registry);
    }

    @Autowired
    MeterRegistry meterRegistry;

    @Test
    void caffeineCacheMetricsAreRegistered() {
        // CaffeineCacheMetrics регистрирует семейство cache.* метрик с тегом cache=optionCache.
        // Конкретно cache.size — gauge, всегда присутствует после monitor().
        assertThat(meterRegistry.find("cache.size").tag("cache", "optionCache").gauge())
                .as("cache.size{cache=optionCache} должен быть зарегистрирован")
                .isNotNull();

        // cache.gets — счётчик; result-теги (hit/miss) создаются на лету при
        // первом обращении. Сам meter family может ещё не быть; gauge size достаточно.
    }
}
