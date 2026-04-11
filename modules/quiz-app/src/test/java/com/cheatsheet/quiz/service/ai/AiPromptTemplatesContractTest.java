package com.cheatsheet.quiz.service.ai;

import com.cheatsheet.quiz.service.ai.prompt.AiPrompts;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AiPromptTemplatesContractTest {

    @Test
    void optionTemplateContainsStrictSymmetryAndExplanationRules() {
        assertThat(AiPrompts.USER_PROMPT_TEMPLATE).contains("maxLen <= 1.10 * minLen");
        assertThat(AiPrompts.USER_PROMPT_TEMPLATE).contains("1 предложение");
        assertThat(AiPrompts.USER_PROMPT_TEMPLATE).contains("маркеры правильности");
        assertThat(AiPrompts.USER_PROMPT_TEMPLATE).contains("Запрещены дубли");
        assertThat(AiPrompts.USER_PROMPT_TEMPLATE).contains("Верни только валидный JSON");
    }

    @Test
    void codeOptionTemplateContainsStrictSymmetryAndExplanationRules() {
        assertThat(AiPrompts.CODE_OPTIONS_PROMPT_TEMPLATE).contains("maxLen <= 1.10 * minLen");
        assertThat(AiPrompts.CODE_OPTIONS_PROMPT_TEMPLATE).contains("1 предложение");
        assertThat(AiPrompts.CODE_OPTIONS_PROMPT_TEMPLATE).contains("correct заметно длиннее");
        assertThat(AiPrompts.CODE_OPTIONS_PROMPT_TEMPLATE).contains("Запрещены дубли");
        assertThat(AiPrompts.CODE_OPTIONS_PROMPT_TEMPLATE).contains("Верни только валидный JSON");
    }
}
