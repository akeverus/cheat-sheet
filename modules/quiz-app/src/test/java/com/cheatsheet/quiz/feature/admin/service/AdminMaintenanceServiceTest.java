package com.cheatsheet.quiz.feature.admin.service;

import com.cheatsheet.quiz.persistence.AnswerOptionRepository;
import com.cheatsheet.quiz.persistence.HintRepository;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.service.cache.OptionCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

class AdminMaintenanceServiceTest {

    private AnswerOptionRepository answerOptions;
    private HintRepository hints;
    private QuestionRepository questions;
    private OptionCache optionCache;
    private AdminMaintenanceService service;

    @BeforeEach
    void setUp() {
        answerOptions = mock(AnswerOptionRepository.class);
        hints = mock(HintRepository.class);
        questions = mock(QuestionRepository.class);
        optionCache = mock(OptionCache.class);
        service = new AdminMaintenanceService(answerOptions, hints, questions, optionCache);
    }

    @Test
    void clearOptionsDeletesAllAndInvalidatesCache() {
        when(answerOptions.deleteAll()).thenReturn(42);

        int deleted = service.clearOptions();

        assertThat(deleted).isEqualTo(42);
        verify(answerOptions).deleteAll();
        verify(optionCache).invalidateAll();
        verify(hints, never()).deleteAll();
        verify(questions, never()).clearAllDiagrams();
    }

    @Test
    void resetAllClearsEverythingAndReturnsCounts() {
        when(answerOptions.deleteAll()).thenReturn(100);
        when(hints.deleteAll()).thenReturn(50);
        when(questions.clearAllDiagrams()).thenReturn(30);
        when(questions.resetAllRegenCount()).thenReturn(150);

        AdminMaintenanceService.ResetAllResult result = service.resetAll();

        assertThat(result.deletedOptions()).isEqualTo(100);
        assertThat(result.deletedHints()).isEqualTo(50);
        assertThat(result.clearedDiagrams()).isEqualTo(30);
        assertThat(result.resetRegenCount()).isEqualTo(150);
        verify(optionCache).invalidateAll();
    }

    @Test
    void resetAllResultRecordSupportsToBuilder() {
        AdminMaintenanceService.ResetAllResult original =
                new AdminMaintenanceService.ResetAllResult(1, 2, 3, 4);
        AdminMaintenanceService.ResetAllResult patched = original.toBuilder()
                .deletedOptions(99)
                .build();

        assertThat(patched.deletedOptions()).isEqualTo(99);
        assertThat(patched.deletedHints()).isEqualTo(2);
        assertThat(patched.clearedDiagrams()).isEqualTo(3);
        assertThat(patched.resetRegenCount()).isEqualTo(4);
    }
}
