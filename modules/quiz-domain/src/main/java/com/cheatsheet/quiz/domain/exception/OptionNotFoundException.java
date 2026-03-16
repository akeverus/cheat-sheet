package com.cheatsheet.quiz.domain.exception;

import lombok.experimental.StandardException;

/**
 * Исключение: вариант ответа не найден в БД.
 *
 * <p>Бросается при попытке выбрать несуществующий вариант.
 * Перехватывается {@link com.cheatsheet.quiz.api.controller.GlobalExceptionHandler}
 * и возвращает HTTP 404.</p>
 */
@StandardException
public class OptionNotFoundException extends RuntimeException {}
