package com.cheatsheet.quiz.service.ai;

import com.cheatsheet.quiz.service.ai.prompt.AiPrompts;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AiPromptTemplatesContractTest {

    @Test
    void optionTemplateContainsStrictSymmetryAndExplanationRules() {
        assertThat(AiPrompts.USER_PROMPT_TEMPLATE).contains("maxLen <= 1.10 * minLen");
        assertThat(AiPrompts.USER_PROMPT_TEMPLATE).contains("Запрещены маркеры правильности");
        assertThat(AiPrompts.USER_PROMPT_TEMPLATE).contains("правдоподобны и требуют рассуждения");
        assertThat(AiPrompts.USER_PROMPT_TEMPLATE).contains("All answer options must contain roughly the same amount of information.");
        assertThat(AiPrompts.USER_PROMPT_TEMPLATE).contains("Верни только валидный JSON");
    }

    @Test
    void codeOptionTemplateContainsStrictSymmetryAndExplanationRules() {
        assertThat(AiPrompts.CODE_OPTIONS_PROMPT_TEMPLATE).contains("maxLen <= 1.10 * minLen");
        assertThat(AiPrompts.CODE_OPTIONS_PROMPT_TEMPLATE).contains("Дан вопрос и код. Сгенерируй варианты полностью самостоятельно.");
        assertThat(AiPrompts.CODE_OPTIONS_PROMPT_TEMPLATE).contains("Дистракторы правдоподобны, технически похожи на возможные ошибки интерпретации");
        assertThat(AiPrompts.CODE_OPTIONS_PROMPT_TEMPLATE).contains("All answer options must contain roughly the same amount of information.");
        assertThat(AiPrompts.CODE_OPTIONS_PROMPT_TEMPLATE).contains("Верни только валидный JSON");
    }
}
