package com.cheatsheet.quiz.service.ai;

import com.cheatsheet.quiz.service.ai.prompt.AiPrompts;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AiPromptTemplatesContractTest {

    @Test
    void questionV2TemplateContainsTopicAnchoringDifficultyAndLeakageRules() {
        assertThat(AiPrompts.GENERAL_PROMPT_TEMPLATE).contains("ДОЛЖНЫ строго оставаться внутри");
        assertThat(AiPrompts.GENERAL_PROMPT_TEMPLATE).contains("maxLen <= 1.10 * minLen");
        assertThat(AiPrompts.GENERAL_PROMPT_TEMPLATE).contains("Типичная ошибка");
        assertThat(AiPrompts.GENERAL_PROMPT_TEMPLATE).contains("Частично верное утверждение");
        assertThat(AiPrompts.GENERAL_PROMPT_TEMPLATE).contains("НЕ повторяет правильную опцию дословно");
        assertThat(AiPrompts.GENERAL_PROMPT_TEMPLATE).contains("Язык вывода: русский");
    }
}
