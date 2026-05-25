package com.cheatsheet.quiz.config.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * Базовая инфраструктурная конфигурация: {@link Clock}.
 *
 * <p>AI-клиенты — {@link AiClientConfig};
 * кэш — {@link CacheConfig};
 * пулы потоков — {@link AsyncConfig}.</p>
 *
 * <p>PostgreSQL — единственная поддерживаемая БД, поэтому здесь больше нет
 * SQLite-PRAGMA или data-dir-инициализации.</p>
 */
@Configuration
@Slf4j
public class InfrastructureConfig {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }
}
