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
import org.springframework.jdbc.core.JdbcTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StartupRunnerTest {

    @Mock
    private QuestionImportService questionImportService;
    @Mock
    private PreloadService preloadService;
    @Mock
    private JdbcTemplate jdbcTemplate;

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
                appProperties,
                jdbcTemplate
        );

        runner.run(null);

        verify(questionImportService).importAll();
        verify(preloadService).preloadNext(any());
        verify(preloadService).warmupAll();
        verify(jdbcTemplate, never()).execute(anyString());
    }

    @Test
    void runSkipsPreloadAndFullWarmupWhenDisabled() {
        appProperties.getPreload().setStartupPreload(false);
        appProperties.getPreload().setFullWarmup(false);
        StartupRunner runner = new StartupRunner(
                questionImportService,
                preloadService,
                appProperties,
                jdbcTemplate
        );

        runner.run(null);

        verify(questionImportService).importAll();
        verify(preloadService, never()).preloadNext(any());
        verify(preloadService, never()).warmupAll();
        verify(jdbcTemplate, never()).execute(anyString());
    }

    @Test
    void runTruncatesDataWhenResetOnStartupEnabled() {
        appProperties.setInterviewResetOnStartup(true);
        StartupRunner runner = new StartupRunner(
                questionImportService,
                preloadService,
                appProperties,
                jdbcTemplate
        );

        runner.run(null);

        verify(jdbcTemplate).execute(anyString());
        verify(questionImportService).importAll();
    }
}
