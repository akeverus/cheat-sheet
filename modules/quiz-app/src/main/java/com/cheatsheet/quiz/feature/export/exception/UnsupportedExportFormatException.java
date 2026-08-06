package com.cheatsheet.quiz.feature.export.exception;

import java.util.List;

/**
 * Запрошен формат экспорта вне белого списка. Обрабатывается глобально
 * ({@code GlobalExceptionHandler}) и отдаётся как 400 {@code ProblemDetail} —
 * единый контракт ошибок для всего API.
 */
public class UnsupportedExportFormatException extends RuntimeException {

    private final transient List<String> allowedFormats;

    public UnsupportedExportFormatException(List<String> allowedFormats) {
        super("Формат экспорта не поддерживается. Допустимые значения: "
                + String.join(", ", allowedFormats) + ".");
        this.allowedFormats = List.copyOf(allowedFormats);
    }

    public List<String> allowedFormats() {
        return allowedFormats;
    }
}
