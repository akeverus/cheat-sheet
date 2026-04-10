package com.cheatsheet.quiz.feature.question.engine.prompt;

import com.cheatsheet.quiz.domain.Difficulty;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.service.ai.prompt.AiPrompts;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

/**
 * Строит детерминированный prompt для генерации вопросов Question V2.
 */
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuestionPromptBuilder {

    private final QuestionTopicNormalizer questionTopicNormalizer;

    /**
     * Собирает единый prompt-контракт:
     * строгий JSON, симметрия опций и запрет stylistic leakage.
     */
    public String buildQuestionPrompt(Difficulty difficulty, QuestionType type, String topic) {
        Difficulty safeDifficulty = difficulty == null ? Difficulty.MEDIUM : difficulty;
        QuestionType safeType = type == null ? QuestionType.CONCEPT : type;
        String safeTopic = questionTopicNormalizer.normalize(topic);
        return AiPrompts.QUESTION_V2_PROMPT_TEMPLATE.formatted(
                safeDifficulty.name(),
                safeType.name(),
                safeTopic,
                safeDifficulty.name()
        );
    }
}
