package com.cheatsheet.quiz.service.ai;

import com.cheatsheet.quiz.domain.Difficulty;
import com.cheatsheet.quiz.domain.QuestionType;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Строит детерминированный prompt для генерации вопросов Question V2.
 */
@Component
public class QuestionPromptBuilder {

    /**
     * Собирает базовый prompt и, при наличии, добавляет quality-feedback с предыдущей попытки.
     */
    public String buildQuestionPrompt(
            Difficulty difficulty,
            QuestionType type,
            String topic,
            List<String> previousViolations
    ) {
        Difficulty safeDifficulty = difficulty == null ? Difficulty.MEDIUM : difficulty;
        QuestionType safeType = type == null ? QuestionType.CONCEPT : type;
        String safeTopic = topic == null ? "general" : topic.trim();
        String basePrompt = AiPrompts.QUESTION_V2_PROMPT_TEMPLATE.formatted(
                safeDifficulty.name(),
                safeType.name(),
                safeTopic
        );

        if (previousViolations == null || previousViolations.isEmpty()) {
            return basePrompt;
        }

        StringBuilder qualityFeedback = new StringBuilder()
                .append("\n\nQUALITY_FEEDBACK_FROM_PREVIOUS_ATTEMPT:\n");
        for (int i = 0; i < previousViolations.size(); i++) {
            qualityFeedback.append(i + 1).append(". ").append(previousViolations.get(i)).append('\n');
        }
        qualityFeedback.append("Regenerate the question JSON and fix every listed violation.");
        return basePrompt + qualityFeedback;
    }
}
