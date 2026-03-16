package com.cheatsheet.quiz.common.constants;

import lombok.experimental.UtilityClass;

/**
 * Машиночитаемые коды типов ошибок API.
 *
 * <p>Используются в поле {@code type} ответа {@link com.cheatsheet.quiz.common.model.ApiError}.</p>
 */
@UtilityClass
public class ApiErrorTypes {

    public static final String QUESTION_NOT_FOUND = "QUESTION_NOT_FOUND";
    public static final String OPTION_NOT_FOUND = "OPTION_NOT_FOUND";
    public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
    public static final String INTERNAL_ERROR = "INTERNAL_ERROR";
    public static final String FORBIDDEN = "FORBIDDEN";
    public static final String RATE_LIMIT_EXCEEDED = "RATE_LIMIT_EXCEEDED";
    public static final String UNSUPPORTED_FORMAT = "UNSUPPORTED_FORMAT";
    public static final String EXPORT_SERIALIZATION_ERROR = "EXPORT_SERIALIZATION_ERROR";
    public static final String AI_GENERATION_FAILED = "AI_GENERATION_FAILED";
    public static final String QUESTION_STATE_INVALID = "QUESTION_STATE_INVALID";
    public static final String CONFLICT = "CONFLICT";
}
