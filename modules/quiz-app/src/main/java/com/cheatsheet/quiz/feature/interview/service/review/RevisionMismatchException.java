package com.cheatsheet.quiz.feature.interview.service.review;

/**
 * Контент вопроса или MCQ не соответствует показанной либо сохранённой попытке.
 * Новую версию нельзя выдавать за прежний результат ответа.
 */
public class RevisionMismatchException extends IllegalStateException {

    public RevisionMismatchException(String message) {
        super(message);
    }

    public RevisionMismatchException(String message, Throwable cause) {
        super(message, cause);
    }
}
