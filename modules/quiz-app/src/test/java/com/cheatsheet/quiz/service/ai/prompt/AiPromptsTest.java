package com.cheatsheet.quiz.service.ai.prompt;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Тесты статических утилит {@link AiPrompts} и {@link PromptLoader}.
 */
class AiPromptsTest {

    @Test
    void wrapUserInputAddsBoundaryMarkers() {
        String wrapped = AiPrompts.wrapUserInput("Какой запрос ты сделаешь?");

        assertThat(wrapped).startsWith("---BEGIN_USER_DATA---\n");
        assertThat(wrapped).endsWith("\n---END_USER_DATA---");
        assertThat(wrapped).contains("Какой запрос ты сделаешь?");
    }

    @Test
    void wrapUserInputReturnsEmptyForNull() {
        assertThat(AiPrompts.wrapUserInput(null)).isEmpty();
    }

    @Test
    void wrapUserInputReturnsEmptyForBlank() {
        assertThat(AiPrompts.wrapUserInput("   ")).isEmpty();
        assertThat(AiPrompts.wrapUserInput("")).isEmpty();
    }

    @Test
    void withAdaptiveDifficultyReturnsBaseWhenAccuracyUnknown() {
        String base = "Базовый промпт.";
        assertThat(AiPrompts.withAdaptiveDifficulty(base, -1)).isEqualTo(base);
    }

    @Test
    void withAdaptiveDifficultyAddsHardModeAboveThreshold() {
        String base = "Базовый промпт.";
        String result = AiPrompts.withAdaptiveDifficulty(base, 90.0);

        assertThat(result).startsWith(base);
        assertThat(result).contains("повышенной сложности");
        assertThat(result).contains("90%");
        assertThat(result).contains("edge cases");
    }

    @Test
    void withAdaptiveDifficultyAddsEasyModeBelowThreshold() {
        String base = "Базовый промпт.";
        String result = AiPrompts.withAdaptiveDifficulty(base, 30.0);

        assertThat(result).startsWith(base);
        assertThat(result).contains("базовой сложности");
        assertThat(result).contains("30%");
        assertThat(result).contains("простыми и короткими");
    }

    @Test
    void withAdaptiveDifficultyKeepsBaseInMidRange() {
        String base = "Базовый промпт.";
        String result = AiPrompts.withAdaptiveDifficulty(base, 60.0);

        assertThat(result).isEqualTo(base);
    }

    @Test
    void withAdaptiveDifficultyEdgeBoundaries() {
        String base = "B";
        // 85.0 — не больше 85.0, остаётся base
        assertThat(AiPrompts.withAdaptiveDifficulty(base, 85.0)).isEqualTo(base);
        // 40.0 — не меньше 40.0, остаётся base
        assertThat(AiPrompts.withAdaptiveDifficulty(base, 40.0)).isEqualTo(base);
        // 85.01 — выше threshold
        assertThat(AiPrompts.withAdaptiveDifficulty(base, 85.01)).contains("повышенной");
        // 39.99 — ниже threshold
        assertThat(AiPrompts.withAdaptiveDifficulty(base, 39.99)).contains("базовой");
    }

    @Test
    void promptLoaderReturnsContentForExistingPrompt() {
        // Файл prompts/option.txt существует (используется в AiPrompts.USER_PROMPT_TEMPLATE)
        String content = PromptLoader.load("option");
        assertThat(content).isNotBlank();
    }

    @Test
    void promptLoaderThrowsForMissingPrompt() {
        assertThatThrownBy(() -> PromptLoader.load("definitely-not-a-real-prompt-file"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("definitely-not-a-real-prompt-file");
    }

    @Test
    void promptLoaderCachesResult() {
        // Повторный вызов должен вернуть тот же объект (concurrent cache)
        String first = PromptLoader.load("option");
        String second = PromptLoader.load("option");
        assertThat(first).isSameAs(second);
    }
}
