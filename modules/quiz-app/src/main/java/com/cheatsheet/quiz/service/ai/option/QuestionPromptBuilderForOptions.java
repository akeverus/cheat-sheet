package com.cheatsheet.quiz.service.ai.option;

import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.service.ai.prompt.AiPrompts;

/**
 * Временный адаптер, который переиспользует основной general промпт
 * для регенерации вариантов ответов к уже существующему вопросу.
 */
public final class QuestionPromptBuilderForOptions {

    private QuestionPromptBuilderForOptions() {
    }

    public static String build(Question question) {
        // Используем уже сохранённый текст вопроса и тему как input;
        // сложность и тип задаём по умолчанию, чтобы не плодить новые параметры.
        String topic = question.topic() == null ? "general" : question.topic();
        String difficulty = question.difficulty() != null ? question.difficulty().name() : "MEDIUM";
        String type = question.type() != null ? question.type().name() : "CONCEPT";
        return AiPrompts.GENERAL_PROMPT_TEMPLATE.formatted(
                difficulty,
                type,
                topic,
                difficulty
        );
    }
}

