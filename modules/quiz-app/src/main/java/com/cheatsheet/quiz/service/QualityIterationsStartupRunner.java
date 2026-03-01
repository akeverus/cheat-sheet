package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.config.AppProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Опциональный автозапуск цикла quality-итераций при старте приложения.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class QualityIterationsStartupRunner implements ApplicationRunner {
    private final AppProperties appProperties;
    private final QualityIterationRunner qualityIterationRunner;

    public QualityIterationsStartupRunner(
            AppProperties appProperties,
            QualityIterationRunner qualityIterationRunner
    ) {
        this.appProperties = appProperties;
        this.qualityIterationRunner = qualityIterationRunner;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!appProperties.getQualityIterations().isEnabled()) {
            return;
        }
        QualityIterationRunner.QualityRunReport report = qualityIterationRunner.runConfiguredIterations();
        log.info("Quality iterations completed: iterations={}, sampleSize={}, targetScore={}, avgRunScore={}, lastScore={}",
                report.iterations(),
                report.sampleSize(),
                report.targetScore(),
                String.format("%.1f", report.averageRunScore()),
                report.lastIterationScore());
    }
}
