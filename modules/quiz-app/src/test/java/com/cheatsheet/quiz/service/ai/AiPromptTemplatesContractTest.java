package com.cheatsheet.quiz.service.ai;

import com.cheatsheet.quiz.service.ai.prompt.AiPrompts;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AiPromptTemplatesContractTest {

    @Test
    void optionTemplateContainsStrictSymmetryAndExplanationRules() {
        assertThat(AiPrompts.USER_PROMPT_TEMPLATE).contains("maxLen <= 1.15 * minLen");
        assertThat(AiPrompts.USER_PROMPT_TEMPLATE).contains("1-2 предложения");
        assertThat(AiPrompts.USER_PROMPT_TEMPLATE).contains("маркеры правильности");
        assertThat(AiPrompts.USER_PROMPT_TEMPLATE).contains("нельзя объяснения");
        assertThat(AiPrompts.USER_PROMPT_TEMPLATE).contains("Верни только валидный JSON");
    }

    @Test
    void codeOptionTemplateContainsStrictSymmetryAndExplanationRules() {
        assertThat(AiPrompts.CODE_OPTIONS_PROMPT_TEMPLATE).contains("maxLen <= 1.15 * minLen");
        assertThat(AiPrompts.CODE_OPTIONS_PROMPT_TEMPLATE).contains("1-2 предложения");
        assertThat(AiPrompts.CODE_OPTIONS_PROMPT_TEMPLATE).contains("Нет явных маркеров правильности");
        assertThat(AiPrompts.CODE_OPTIONS_PROMPT_TEMPLATE).contains("запрещены объяснения");
        assertThat(AiPrompts.CODE_OPTIONS_PROMPT_TEMPLATE).contains("Верни только валидный JSON");
    }
}
