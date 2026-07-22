package com.cheatsheet.quiz.feature.interview.service.review;

/**
 * Клиент прислал {@code questionRevisionId}, который не существует или
 * не принадлежит вопросу — попытка не должна писаться с «чужой» ревизией.
 */
public class RevisionMismatchException extends IllegalStateException {

    public RevisionMismatchException(String message) {
        super(message);
    }
}
