package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.config.app.AppProperties;
import com.cheatsheet.quiz.feature.interview.service.core.PreloadService;
import com.cheatsheet.quiz.infrastructure.bootstrap.StartupRunner;
import com.cheatsheet.quiz.service.imports.QuestionImportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StartupRunnerTest {

    @Mock
    private QuestionImportService questionImportService;
    @Mock
    private PreloadService preloadService;

    private AppProperties appProperties;

    @BeforeEach
    void setUp() {
        appProperties = new AppProperties();
    }

    @Test
    void runStartsFullWarmupWhenConfigured() {
        appProperties.getPreload().setStartupPreload(true);
        appProperties.getPreload().setFullWarmup(true);
        StartupRunner runner = new StartupRunner(
                questionImportService,
                preloadService,
                appProperties
        );

        runner.run(null);

        verify(questionImportService).importAll();
        verify(preloadService).preloadNext(any());
        verify(preloadService).warmupAll();
    }

    @Test
    void runSkipsPreloadAndFullWarmupWhenDisabled() {
        appProperties.getPreload().setStartupPreload(false);
        appProperties.getPreload().setFullWarmup(false);
        StartupRunner runner = new StartupRunner(
                questionImportService,
                preloadService,
                appProperties
        );

        runner.run(null);

        verify(questionImportService).importAll();
        verify(preloadService, never()).preloadNext(any());
        verify(preloadService, never()).warmupAll();
    }
}
