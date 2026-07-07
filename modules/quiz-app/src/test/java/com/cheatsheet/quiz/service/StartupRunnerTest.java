package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.config.app.AppProperties;
import com.cheatsheet.quiz.infrastructure.bootstrap.StartupRunner;
import com.cheatsheet.quiz.service.imports.QuestionImportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StartupRunnerTest {

    @Mock
    private QuestionImportService questionImportService;
    @Mock
    private JdbcTemplate jdbcTemplate;

    private AppProperties appProperties;

    @BeforeEach
    void setUp() {
        appProperties = new AppProperties();
    }

    @Test
    void runImportsQuestionsWithoutTruncateWhenResetDisabled() {
        appProperties.setInterviewResetOnStartup(false);
        StartupRunner runner = new StartupRunner(
                questionImportService,
                appProperties,
                jdbcTemplate
        );

        runner.run(null);

        verify(questionImportService).importAll();
        verify(jdbcTemplate, never()).execute(anyString());
    }

    @Test
    void runTruncatesDataWhenResetOnStartupEnabled() {
        appProperties.setInterviewResetOnStartup(true);
        StartupRunner runner = new StartupRunner(
                questionImportService,
                appProperties,
                jdbcTemplate
        );

        runner.run(null);

        verify(jdbcTemplate).execute(anyString());
        verify(questionImportService).importAll();
    }
}
