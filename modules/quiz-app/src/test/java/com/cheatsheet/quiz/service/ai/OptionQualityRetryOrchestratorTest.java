package com.cheatsheet.quiz.service.ai;

import com.cheatsheet.quiz.config.AppProperties;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.service.ai.dto.GeneratedOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OptionQualityRetryOrchestratorTest {

    @Mock
    private OptionGenerator optionGenerator;
    @Mock
    private OptionQualityValidator optionQualityValidator;

    private OptionQualityRetryOrchestrator orchestrator;
    private Question question;

    @BeforeEach
    void setUp() {
        AppProperties properties = new AppProperties();
        properties.getInterview().setOptionQualityRetryAttempts(1);
        properties.getInterview().setOptionQualityRetryBackoffMs(0);
        properties.getInterview().setOptionQualityRetryMaxElapsedMs(1000);
        orchestrator = new OptionQualityRetryOrchestrator(optionGenerator, optionQualityValidator, properties);
        question = new Question(7L, "slug", "slug", "f.md", "topic", "Q?", "A", false, "h", QuestionType.TEXT, null, null, 0, null);
    }

    @Test
    void retriesAndAppendsQualityFixContextWhenCriticalIssueDetected() {
        GeneratedOptions generated = new GeneratedOptions("Correct", "ok", List.of("A", "B", "C"), List.of("a", "b", "c"));
        when(optionGenerator.generateOptions(anyString(), anyString(), any(), anyString()))
                .thenReturn(Optional.of(generated))
                .thenReturn(Optional.of(generated));
        OptionQualityValidator.ValidationReport critical = new OptionQualityValidator.ValidationReport(List.of(
                new OptionQualityValidator.ValidationIssue(
                        OptionQualityValidator.IssueCode.CORRECT_ANSWER_MISMATCH,
                        OptionQualityValidator.Severity.CRITICAL,
                        "critical mismatch"
                )
        ));
        when(optionQualityValidator.validateQualityReportWithContext(any(), anyString(), anyString(), any()))
                .thenReturn(critical)
                .thenReturn(new OptionQualityValidator.ValidationReport(List.of()));
        when(optionQualityValidator.hasHardBlockIssues(critical)).thenReturn(false);

        OptionQualityRetryOrchestrator.QualityCheckedGeneration result = orchestrator.generateWithQualityRetry(
                question, "Q?", "A", null, "BASE"
        );

        assertThat(result.acceptedWithWarnings()).isFalse();
        ArgumentCaptor<String> contextCaptor = ArgumentCaptor.forClass(String.class);
        verify(optionGenerator, times(2)).generateOptions(anyString(), anyString(), any(), contextCaptor.capture());
        assertThat(contextCaptor.getAllValues().get(1)).contains("QUALITY_FIX_REQUIRED");
        assertThat(contextCaptor.getAllValues().get(1)).contains("critical mismatch");
    }

    @Test
    void throwsWhenHardBlockPersistsUntilLimit() {
        GeneratedOptions generated = new GeneratedOptions("Correct", "ok", List.of("A", "B", "C"), List.of("a", "b", "c"));
        when(optionGenerator.generateOptions(anyString(), anyString(), any(), anyString()))
                .thenReturn(Optional.of(generated))
                .thenReturn(Optional.of(generated));
        OptionQualityValidator.ValidationReport hardBlock = new OptionQualityValidator.ValidationReport(List.of(
                new OptionQualityValidator.ValidationIssue(
                        OptionQualityValidator.IssueCode.VERDICT_LEAKAGE,
                        OptionQualityValidator.Severity.CRITICAL,
                        "hard block"
                )
        ));
        when(optionQualityValidator.validateQualityReportWithContext(any(), anyString(), anyString(), any()))
                .thenReturn(hardBlock)
                .thenReturn(hardBlock);
        when(optionQualityValidator.hasHardBlockIssues(hardBlock)).thenReturn(true);

        assertThatThrownBy(() -> orchestrator.generateWithQualityRetry(question, "Q?", "A", null, "BASE"))
                .isInstanceOf(AiGenerationException.class)
                .hasMessageContaining("quality hard-block");
    }
}
