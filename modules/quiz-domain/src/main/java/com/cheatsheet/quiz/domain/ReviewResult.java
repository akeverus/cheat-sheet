package com.cheatsheet.quiz.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Результат последнего ответа на вопрос в системе интервального повторения.
 *
 * <p>Хранится в БД как TEXT (через {@link #name()}).</p>
 *
 * <p>Подпись ({@link #label()}) — здесь, по критерию {@link Difficulty} и
 * {@code ReviewReason.message}: она свойство самой константы, а не копирайт конкретного
 * экрана. Раньше {@code result.html} расписывал все пять значений через
 * {@code th:switch} с фоллбэком {@code th:case="*"}, печатающим {@link #name()} —
 * новая константа немедленно выводила в полностью русский интерфейс сырое английское
 * имя, ничего при этом не ломая.</p>
 */
public enum ReviewResult {

    /** Вопрос ещё ни разу не был отвечен. */
    NEW("Ещё не отвечали"),

    /** Последний ответ был правильным. */
    CORRECT("Верно"),

    /** Последний ответ был неправильным. */
    WRONG("Неверно"),

    /** Пользователь отметил «не знаю» — полный провал припоминания (лапс, отличный от WRONG). */
    UNKNOWN("Не знаю"),

    /** Прогресс по вопросу был сброшен (например, при обновлении контента). */
    RESET("Сброшен");

    private static final Logger log = LoggerFactory.getLogger(ReviewResult.class);

    private final String label;

    ReviewResult(String label) {
        this.label = label;
    }

    /** Подпись для строки «Последний результат» на экране разбора. */
    public String label() {
        return label;
    }

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
                    "Неизвестный ReviewResult: '" + value + "'. Допустимые: NEW, CORRECT, WRONG, UNKNOWN, RESET", e);
        }
    }
}
