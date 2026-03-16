package com.cheatsheet.quiz.domain;

import java.util.List;
import java.util.Objects;
import lombok.Builder;

/**
 * Результат ответа на вопрос.
 *
 * @param question          вопрос
 * @param options           все варианты ответа
 * @param selected          выбранный пользователем вариант
 * @param correct           правильный вариант
 * @param correctAnswer     true если пользователь ответил верно
 * @param updatedState      состояние интервального повторения после ответа
 * @param answerDisplayMode режим отображения полного ответа
 */
@Builder(toBuilder = true)
public record AnswerResult(
        Question question,
        List<AnswerOption> options,
        AnswerOption selected,
        AnswerOption correct,
        boolean correctAnswer,
        ReviewState updatedState,
        AnswerDisplayMode answerDisplayMode
) {
    public AnswerResult {
        Objects.requireNonNull(question, "question");
        Objects.requireNonNull(options, "options");
        Objects.requireNonNull(selected, "selected");
        Objects.requireNonNull(correct, "correct");
        Objects.requireNonNull(updatedState, "updatedState");
        Objects.requireNonNull(answerDisplayMode, "answerDisplayMode");
    }
}
