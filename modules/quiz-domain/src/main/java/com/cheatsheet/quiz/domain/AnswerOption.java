package com.cheatsheet.quiz.domain;

import java.util.Objects;
import lombok.Builder;

/**
 * Вариант ответа на вопрос квиза.
 *
 * @param id           автоинкрементный идентификатор (PK)
 * @param questionId   FK → questions.id
 * @param optionText   текст варианта
 * @param correct      {@code true} если это правильный вариант
 * @param displayOrder порядок отображения (0-based)
 * @param source       источник генерации ({@link OptionSource#name()}: OPENAI, DEEPSEEK)
 * @param explanation  объяснение, почему вариант правильный/неправильный (nullable)
 * @param promptVersion версия prompt-контракта генерации
 * @param qualityProfileVersion версия quality-профиля валидации
 */
@Builder(toBuilder = true)
public record AnswerOption(
        long id,
        long questionId,
        String optionText,
        boolean correct,
        int displayOrder,
        String source,
        String explanation,
        int promptVersion,
        int qualityProfileVersion
) {
    public AnswerOption(
            long id,
            long questionId,
            String optionText,
            boolean correct,
            int displayOrder,
            String source,
            String explanation
    ) {
        this(id, questionId, optionText, correct, displayOrder, source, explanation, 1, 1);
    }

    /**
     * Compact-конструктор с валидацией обязательных полей.
     */
    public AnswerOption {
        Objects.requireNonNull(optionText, "optionText не может быть null");
        Objects.requireNonNull(source, "source must not be null");
        if (displayOrder < 0) {
            throw new IllegalArgumentException("displayOrder не может быть отрицательным: " + displayOrder);
        }
        if (promptVersion < 1) {
            throw new IllegalArgumentException("promptVersion не может быть < 1: " + promptVersion);
        }
        if (qualityProfileVersion < 1) {
            throw new IllegalArgumentException("qualityProfileVersion не может быть < 1: " + qualityProfileVersion);
        }
    }
}
