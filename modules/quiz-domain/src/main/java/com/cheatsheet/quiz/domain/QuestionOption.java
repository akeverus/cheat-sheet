package com.cheatsheet.quiz.domain;

import java.util.Objects;
import lombok.Builder;

/**
 * Вариант ответа в расширенном формате Question v2.
 */
@Builder(toBuilder = true)
public record QuestionOption(
        String id,
        String text,
        boolean correct,
        String explanation
) {
    public QuestionOption {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(text, "text must not be null");
    }
}
