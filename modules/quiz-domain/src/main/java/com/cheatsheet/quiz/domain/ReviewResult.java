package com.cheatsheet.quiz.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Результат последнего ответа на вопрос в системе интервального повторения.
 *
 * <p>Хранится в БД как TEXT (через {@link #name()}).</p>
 */
public enum ReviewResult {

    /** Вопрос ещё ни разу не был отвечен. */
    NEW,

    /** Последний ответ был правильным. */
    CORRECT,

    /** Последний ответ был неправильным. */
    WRONG,

    /** Прогресс по вопросу был сброшен (например, при обновлении контента). */
    RESET;

    private static final Logger log = LoggerFactory.getLogger(ReviewResult.class);

    /**
     * Парсинг строки из БД.
     * При {@code null} или пустом значении возвращает {@link #NEW} (ожидаемый сценарий для новых записей).
     * При невалидном значении бросает {@link IllegalArgumentException}.
     *
     * @param value строковое представление из столбца {@code last_result}
     * @return соответствующий {@link ReviewResult}
     * @throws IllegalArgumentException при неизвестном значении
     */
    public static ReviewResult fromString(String value) {
        if (value == null || value.isBlank()) {
            return NEW;
        }
        try {
            return valueOf(value.strip().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Неизвестный ReviewResult: '" + value + "'. Допустимые: NEW, CORRECT, WRONG, RESET", e);
        }
    }
}
