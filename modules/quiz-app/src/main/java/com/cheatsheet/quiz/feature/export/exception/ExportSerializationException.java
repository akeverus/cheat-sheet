package com.cheatsheet.quiz.feature.export.exception;

/**
 * Экспорт не удалось сериализовать. Обрабатывается глобально
 * ({@code GlobalExceptionHandler}) и отдаётся как 500 {@code ProblemDetail}.
 */
public class ExportSerializationException extends RuntimeException {

    public ExportSerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
