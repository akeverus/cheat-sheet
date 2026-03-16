package com.cheatsheet.quiz.domain.exception;

import lombok.experimental.StandardException;

/**
 * Исключение: вопрос не найден в БД.
 *
 * <p>Бросается при попытке обработать ответ на несуществующий вопрос.
 * Перехватывается {@link com.cheatsheet.quiz.api.controller.GlobalExceptionHandler}
 * и возвращает HTTP 404.</p>
 */
@StandardException
public class QuestionNotFoundException extends RuntimeException {}
