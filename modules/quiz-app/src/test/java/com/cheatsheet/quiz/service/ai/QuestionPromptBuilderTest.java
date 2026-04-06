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
        assertThat(prompt).contains("\"correct\"");
        assertThat(prompt).contains("\"explanation\"");
        assertThat(prompt).contains("ДОЛЖНЫ строго оставаться внутри");
        assertThat(prompt).contains("maxLen <= 1.10 * minLen");
        assertThat(prompt).contains("Типичная ошибка");
        assertThat(prompt).contains("Частично верное утверждение");
        assertThat(prompt).contains("НЕ повторяет правильную опцию дословно");
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
