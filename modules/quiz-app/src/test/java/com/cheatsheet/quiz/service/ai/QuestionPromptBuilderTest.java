package com.cheatsheet.quiz.service.ai;

import com.cheatsheet.quiz.domain.Difficulty;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.feature.question.engine.prompt.QuestionPromptBuilder;
import com.cheatsheet.quiz.feature.question.engine.prompt.QuestionTopicNormalizer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class QuestionPromptBuilderTest {

    private final QuestionPromptBuilder builder =
            new QuestionPromptBuilder(new QuestionTopicNormalizer());

    @Test
    void buildsDeterministicPromptContract() {
        String prompt = builder.buildQuestionPrompt(
                Difficulty.HARD,
                QuestionType.CODE,
                "java"
        );

        assertThat(prompt).contains("HARD");
        assertThat(prompt).contains("CODE");
        assertThat(prompt).contains("java");
        assertThat(prompt).contains("\"question\"");
        assertThat(prompt).contains("\"options\"");
        assertThat(prompt).contains("\"correct\":true");
        assertThat(prompt).contains("\"explanation\"");
        assertThat(prompt).contains("token-efficient");
        assertThat(prompt).contains("all options must answer this exact question context");
        assertThat(prompt).contains("question-core anchors");
        assertThat(prompt).contains("every option must include at least one question-core anchor");
        assertThat(prompt).contains("must appear verbatim in each option");
        assertThat(prompt).contains("same entity X");
        assertThat(prompt).contains("forbid meta-interview advice in option text");
        assertThat(prompt).contains("forbid distractors about unrelated algorithms or data-structures outside X");
        assertThat(prompt).contains("keep one operation family");
        assertThat(prompt).contains("forbid generic overview options");
        assertThat(prompt).contains("must explicitly stay in sorting family");
        assertThat(prompt).contains("must not exceed 1.15");
        assertThat(prompt).contains("max sentence count difference");
        assertThat(prompt).contains("forbid explanation meta-advice");
        assertThat(prompt).contains("strict ban-list for explanation meta-advice");
        assertThat(prompt).contains("операционный контекст");
        assertThat(prompt).contains("под нагрузкой и при сбоях");
    }

    @Test
    void usesSafeDefaultsForNullInputs() {
        String prompt = builder.buildQuestionPrompt(null, null, null);

        assertThat(prompt).contains("MEDIUM");
        assertThat(prompt).contains("CONCEPT");
        assertThat(prompt).contains("general");
    }

    @Test
    void normalizesBlankTopicToGeneral() {
        String prompt = builder.buildQuestionPrompt(
                Difficulty.EASY,
                QuestionType.CONCEPT,
                "   "
        );

        assertThat(prompt).contains("general");
    }
}
