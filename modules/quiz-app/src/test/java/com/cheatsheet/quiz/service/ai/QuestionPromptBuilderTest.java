package com.cheatsheet.quiz.service.ai;

import com.cheatsheet.quiz.domain.Difficulty;
import com.cheatsheet.quiz.domain.QuestionType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class QuestionPromptBuilderTest {

    private final QuestionPromptBuilder builder = new QuestionPromptBuilder();

    @Test
    void buildsBasePromptWithoutQualityFeedbackWhenNoViolations() {
        String prompt = builder.buildQuestionPrompt(
                Difficulty.HARD,
                QuestionType.CODE,
                "java",
                List.of()
        );

        assertThat(prompt).contains("HARD");
        assertThat(prompt).contains("CODE");
        assertThat(prompt).contains("java");
        assertThat(prompt).doesNotContain("QUALITY_FEEDBACK_FROM_PREVIOUS_ATTEMPT");
    }

    @Test
    void appendsQualityFeedbackBlockWhenViolationsExist() {
        String prompt = builder.buildQuestionPrompt(
                Difficulty.MEDIUM,
                QuestionType.CONCEPT,
                "spring",
                List.of("Need deeper detailedExplanation", "Distractors are too obvious")
        );

        assertThat(prompt).contains("QUALITY_FEEDBACK_FROM_PREVIOUS_ATTEMPT");
        assertThat(prompt).contains("1. Need deeper detailedExplanation");
        assertThat(prompt).contains("2. Distractors are too obvious");
        assertThat(prompt).contains("Regenerate the question JSON and fix every listed violation.");
    }

    @Test
    void usesSafeDefaultsForNullInputs() {
        String prompt = builder.buildQuestionPrompt(null, null, null, null);

        assertThat(prompt).contains("MEDIUM");
        assertThat(prompt).contains("CONCEPT");
        assertThat(prompt).contains("general");
    }
}
