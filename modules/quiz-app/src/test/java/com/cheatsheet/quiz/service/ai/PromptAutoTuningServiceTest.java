package com.cheatsheet.quiz.service.ai;

import com.cheatsheet.quiz.config.AppProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PromptAutoTuningServiceTest {

    private PromptAutoTuningService service;

    @BeforeEach
    void setUp() {
        AppProperties properties = new AppProperties();
        properties.getQualityIterations().setMaxPromptRules(2);
        service = new PromptAutoTuningService(properties);
    }

    @Test
    void applyTopIssuesLimitsNumberOfRulesByConfig() {
        String context = service.applyTopIssues(Map.of(
                OptionQualityValidator.IssueCode.VAGUE_OPTION_TEXT, 4,
                OptionQualityValidator.IssueCode.DUPLICATE_OPTIONS, 3,
                OptionQualityValidator.IssueCode.TRUNCATED_OPTION_TEXT, 2
        ));

        assertThat(context).contains("AUTO_TUNED_RULES:");
        assertThat(context).contains("Строго запрети любые семантические и лексические дубли");
        assertThat(context).contains("Каждый вариант обязан содержать конкретный проверяемый факт");
        assertThat(context).doesNotContain("Запрещены обрезанные варианты и многоточия");
    }

    @Test
    void resetClearsGeneratedContext() {
        service.applyTopIssues(Map.of(OptionQualityValidator.IssueCode.VAGUE_OPTION_TEXT, 1));

        service.reset();

        assertThat(service.currentQualityContext()).isEmpty();
    }
}
