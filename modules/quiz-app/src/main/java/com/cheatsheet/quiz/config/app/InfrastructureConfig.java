package com.cheatsheet.quiz.config.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.util.List;

/**
 * Базовая инфраструктурная конфигурация: {@link Clock} и {@link ApplicationRunner}-ы.
 *
 * <p>AI-клиенты — {@link AiClientConfig};
 * кэш — {@link CacheConfig};
 * пулы потоков — {@link AsyncConfig}.</p>
 */
@Configuration
@Slf4j
public class InfrastructureConfig {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    @Profile("!postgres")
    ApplicationRunner dataDirInitRunner(AppProperties appProperties) {
        return args -> {
            String dataDirName = appProperties.getDataDir() != null ? appProperties.getDataDir() : "data";
            List<Path> requiredDirectories = List.of(
                    Path.of(dataDirName),
                    Path.of(dataDirName, "db"),
                    Path.of(dataDirName, "cache"),
                    Path.of(dataDirName, "snapshots"),
                    Path.of(dataDirName, "export"),
                    Path.of(dataDirName, "import")
            );

            for (Path directory : requiredDirectories) {
                if (Files.exists(directory)) {
                    continue;
                }
                try {
                    Files.createDirectories(directory);
                    log.info("Создана директория данных: {}", directory.toAbsolutePath());
                } catch (IOException e) {
                    log.warn("Не удалось создать директорию данных {}: {}", directory.toAbsolutePath(), e.getMessage());
                }
            }
        };
    }

    @Bean
    @Profile("!postgres")
    ApplicationRunner sqlitePragmasRunner(JdbcTemplate jdbcTemplate, AppProperties props) {
        return args -> {
            try {
                jdbcTemplate.execute("PRAGMA foreign_keys = ON");
                if (props.getSqlite().isEnableWal()) {
                    jdbcTemplate.execute("PRAGMA journal_mode = WAL");
                }
                log.info("SQLite PRAGMA применены (WAL={})", props.getSqlite().isEnableWal());
            } catch (DataAccessException e) {
                log.warn("Не удалось применить SQLite PRAGMA: {}", e.getMessage());
            }
        };
    }
}
