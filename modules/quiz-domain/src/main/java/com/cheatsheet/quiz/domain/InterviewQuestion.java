package com.cheatsheet.quiz.domain;

import java.util.List;
import java.util.Objects;
import lombok.Builder;

/**
 * Агрегат для отображения вопроса в UI: вопрос + варианты ответов + состояние повторения.
 *
 * @param question    вопрос
 * @param options     варианты ответов (перемешанные)
 * @param reviewState текущее состояние интервального повторения
 */
@Builder(toBuilder = true)
public record InterviewQuestion(
        Question question,
        List<AnswerOption> options,
        ReviewState reviewState
) {
    /**
     * Compact constructor with validation.
     */
    public InterviewQuestion {
        Objects.requireNonNull(question, "question must not be null");
        Objects.requireNonNull(options, "options must not be null");
    }
}
