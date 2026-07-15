package com.cheatsheet.quiz.infrastructure.bootstrap;

import com.cheatsheet.quiz.config.app.AppProperties;
import com.cheatsheet.quiz.service.imports.QuestionImportService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Инициализатор приложения: выполняется после запуска Spring-контекста.
 *
 * <p>Выполняет:</p>
 * <ol>
 *   <li>Если {@code app.interview-reset-on-startup=true} — TRUNCATE вопросов и
 *       вариантов в БД (схема Flyway остаётся);</li>
 *   <li>Импорт вопросов из markdown-файлов и MCQ из JSON-сидеров
 *       ({@link QuestionImportService}).</li>
 * </ol>
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StartupRunner implements ApplicationRunner {

    private final QuestionImportService questionImportService;
    private final AppProperties appProperties;
    private final JdbcTemplate jdbcTemplate;

    /**
     * Точка входа после инициализации контекста.
     */
    @Override
    public void run(ApplicationArguments args) {
        if (appProperties.isInterviewResetOnStartup()) {
            log.warn("INTERVIEW_RESET_ON_STARTUP=true — чищу базу перед переимпортом из MD/JSON");
            // Список = всё что наполняется при импорте + связные таблицы.
            // Schema (Flyway) остаётся, потому что чистим только данные.
            jdbcTemplate.execute(
                    "TRUNCATE TABLE answer_options, question_hints, daily_activity, " +
                            "user_topic_stats, review_state, questions RESTART IDENTITY CASCADE");
        }
        log.info("Запуск импорта вопросов из MD/JSON...");
        questionImportService.importAll();
    }
}
