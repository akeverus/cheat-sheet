package com.cheatsheet.quiz.infrastructure.bootstrap;

import com.cheatsheet.quiz.config.app.AppProperties;
import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.feature.interview.service.core.PreloadService;
import com.cheatsheet.quiz.persistence.AnswerOptionRepository;
import com.cheatsheet.quiz.service.cache.OptionCache;
import com.cheatsheet.quiz.service.imports.QuestionImportService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StartupRunner implements ApplicationRunner {

    private final QuestionImportService questionImportService;
    private final AnswerOptionRepository answerOptionRepository;
    private final OptionCache optionCache;
    private final PreloadService preloadService;
    private final AppProperties appProperties;

    /**
     * Точка входа после инициализации контекста.
     */
    @Override
    public void run(ApplicationArguments args) {
        log.info("Запуск импорта вопросов и предзагрузки вариантов...");
        questionImportService.importAll();

        if (appProperties.getInterview().isResetOnStartup()) {
            int deleted = answerOptionRepository.deleteAll();
            optionCache.invalidateAll();
            log.info("Сброс банка ответов: удалено {} записей, кэш очищен", deleted);
        } else {
            log.info("Сброс банка ответов отключён (app.interview.reset-on-startup=false)");
        }

        if (appProperties.getPreload().isStartupPreload()) {
            preloadService.preloadNext(new InterviewFilter(null, null, null));
        } else {
            log.info("Стартовая предзагрузка отключена (app.preload.startup-preload=false)");
        }

        if (appProperties.getPreload().isFullWarmup()) {
            preloadService.warmupAll();
        } else {
            log.info("Полный прогрев отключен (app.preload.full-warmup=false)");
        }
    }
}
