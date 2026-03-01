package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.config.AppProperties;
import com.cheatsheet.quiz.persistence.AnswerOptionRepository;
import com.cheatsheet.quiz.service.cache.OptionCache;
import com.cheatsheet.quiz.service.imports.QuestionImportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StartupRunnerTest {

    @Mock
    private QuestionImportService questionImportService;
    @Mock
    private AnswerOptionRepository answerOptionRepository;
    @Mock
    private OptionCache optionCache;
    @Mock
    private PreloadService preloadService;

    private AppProperties appProperties;

    @BeforeEach
    void setUp() {
        appProperties = new AppProperties();
    }

    @Test
    void runResetsOptionsAndStartsFullWarmupWhenConfigured() {
        appProperties.getInterview().setResetOnStartup(true);
        appProperties.getPreload().setFullWarmup(true);
        when(answerOptionRepository.deleteAll()).thenReturn(12);
        StartupRunner runner = new StartupRunner(
                questionImportService,
                answerOptionRepository,
                optionCache,
                preloadService,
                appProperties
        );

        runner.run(null);

        verify(questionImportService).importAll();
        verify(answerOptionRepository).deleteAll();
        verify(optionCache).invalidateAll();
        verify(preloadService).preloadNext(any());
        verify(preloadService).warmupAll();
    }

    @Test
    void runSkipsResetAndFullWarmupWhenDisabled() {
        appProperties.getInterview().setResetOnStartup(false);
        appProperties.getPreload().setFullWarmup(false);
        StartupRunner runner = new StartupRunner(
                questionImportService,
                answerOptionRepository,
                optionCache,
                preloadService,
                appProperties
        );

        runner.run(null);

        verify(questionImportService).importAll();
        verify(answerOptionRepository, never()).deleteAll();
        verify(optionCache, never()).invalidateAll();
        verify(preloadService).preloadNext(any());
        verify(preloadService, never()).warmupAll();
    }
}
