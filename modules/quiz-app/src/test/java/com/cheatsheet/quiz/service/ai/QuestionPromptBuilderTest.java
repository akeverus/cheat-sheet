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
        assertThat(prompt).contains("technical reasoning, not pure memorization");
        assertThat(prompt).contains("avoid trivial factual recall");
        assertThat(prompt).contains("avoid yes/no question forms");
        assertThat(prompt).contains("distractors realistic, unique, and plausible");
        assertThat(prompt).contains("forbid interview meta-advice");
        assertThat(prompt).contains("output language: Russian");
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

    @Test
    void optionPromptContainsTopicAnchorAndNegationAndExplanationRules() {
        String prompt = com.cheatsheet.quiz.service.ai.prompt.AiPrompts.USER_PROMPT_TEMPLATE;
        assertThat(prompt).contains("той же технической области");
        assertThat(prompt).contains("простым отрицанием или инверсией");
        assertThat(prompt).contains("1–2 предложения");
    }
}
