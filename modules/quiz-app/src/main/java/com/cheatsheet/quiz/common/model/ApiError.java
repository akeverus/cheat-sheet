package com.cheatsheet.quiz.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

/**
 * Стандартный формат ответа об ошибке API.
 *
 * <p>Используется в {@link com.cheatsheet.quiz.common.exception.GlobalExceptionHandler}
 * и {@link com.cheatsheet.quiz.feature.export.controller.ExportController}.</p>
 *
 * @param status  HTTP-статус код (например, 400, 404, 500)
 * @param type    машиночитаемый код ошибки (например, {@code QUESTION_NOT_FOUND})
 * @param message человекочитаемое описание ошибки
 * @param details допустимые значения или детали ошибки (List&lt;String&gt; для валидации, Map для доп. полей и т.д.)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder(toBuilder = true)
public record ApiError(
        int status,
        String type,
        String message,
        Object details
) {
}
