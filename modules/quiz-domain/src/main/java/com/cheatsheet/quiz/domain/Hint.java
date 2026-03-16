package com.cheatsheet.quiz.domain;

import java.util.Objects;
import lombok.Builder;

/**
 * Подсказка к вопросу определённого уровня.
 *
 * @param id         автоинкрементный идентификатор
 * @param questionId FK на вопрос
 * @param level      уровень подсказки (1 — лёгкий намёк, 2 — конкретнее, 3 — почти ответ)
 * @param hintText   текст подсказки
 * @param createdAt  epoch-секунды создания
 */
@Builder(toBuilder = true)
public record Hint(
        long id,
        long questionId,
        int level,
        String hintText,
        long createdAt
) {
    /**
     * Compact-конструктор с валидацией обязательных полей.
     */
    public Hint {
        Objects.requireNonNull(hintText, "hintText не может быть null");
        if (level < 1 || level > 3) {
            throw new IllegalArgumentException("level должен быть от 1 до 3, получено: " + level);
        }
    }
}
