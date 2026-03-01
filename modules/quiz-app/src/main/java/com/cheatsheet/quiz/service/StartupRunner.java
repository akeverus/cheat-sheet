package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.config.AppProperties;
import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.persistence.AnswerOptionRepository;
import com.cheatsheet.quiz.service.cache.OptionCache;
import com.cheatsheet.quiz.service.imports.QuestionImportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Инициализатор приложения: выполняется после запуска Spring-контекста.
 *
 * <p>Выполняет:</p>
 * <ol>
 *   <li>Импорт вопросов из markdown-файлов ({@link QuestionImportService});</li>
 *   <li>Условный сброс банка вариантов ответов ({@code app.interview.reset-on-startup});</li>
 *   <li>Запуск фоновой предзагрузки вариантов ответов ({@link PreloadService}).</li>
 * </ol>
 */
@Component
@Slf4j
public class StartupRunner implements ApplicationRunner {

    private final QuestionImportService questionImportService;
    private final AnswerOptionRepository answerOptionRepository;
    private final OptionCache optionCache;
    private final PreloadService preloadService;
    private final boolean resetOnStartup;
    private final boolean fullWarmup;

    public StartupRunner(
            QuestionImportService questionImportService,
            AnswerOptionRepository answerOptionRepository,
            OptionCache optionCache,
            PreloadService preloadService,
            AppProperties appProperties
    ) {
        this.questionImportService = questionImportService;
        this.answerOptionRepository = answerOptionRepository;
        this.optionCache = optionCache;
        this.preloadService = preloadService;
        this.resetOnStartup = appProperties.getInterview().isResetOnStartup();
        this.fullWarmup = appProperties.getPreload().isFullWarmup();
    }

    /**
     * Точка входа после инициализации контекста.
     */
    @Override
    public void run(ApplicationArguments args) {
        log.info("Запуск импорта вопросов и предзагрузки вариантов...");
        questionImportService.importAll();

        if (resetOnStartup) {
            int deleted = answerOptionRepository.deleteAll();
            optionCache.invalidateAll();
            log.info("Сброс банка ответов: удалено {} записей, кэш очищен", deleted);
        } else {
            log.info("Сброс банка ответов отключён (app.interview.reset-on-startup=false)");
        }

        preloadService.preloadNext(new InterviewFilter(null, null, null));

        if (fullWarmup) {
            preloadService.warmupAll();
        }
    }
}
