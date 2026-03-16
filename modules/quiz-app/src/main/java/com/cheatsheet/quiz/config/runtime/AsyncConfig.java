package com.cheatsheet.quiz.config.runtime;

import com.cheatsheet.quiz.config.app.AppProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Конфигурация пулов потоков для фоновых задач (предзагрузка, warmup).
 */
@Configuration
public class AsyncConfig {

    @Bean
    TaskExecutor preloadExecutor(AppProperties props) {
        AppProperties.Preload cfg = props.getPreload();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(cfg.getCorePoolSize());
        executor.setMaxPoolSize(cfg.getMaxPoolSize());
        executor.setQueueCapacity(cfg.getQueueCapacity());
        executor.setThreadNamePrefix("preload-");
        executor.initialize();
        return executor;
    }

    @Bean
    TaskExecutor warmupExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setQueueCapacity(1);
        executor.setThreadNamePrefix("warmup-");
        executor.initialize();
        return executor;
    }
}
