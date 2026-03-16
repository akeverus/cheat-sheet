package com.cheatsheet.quiz.common.util;

import lombok.experimental.UtilityClass;

/**
 * Утилиты для работы с фильтрами квиза.
 */
@UtilityClass
public class FilterUtils {

    /**
     * Нормализует значение темы из HTTP-параметра.
     *
     * <p>Убирает пробелы по краям. Возвращает {@code null}
     * для пустых/null значений (означает «все темы»).</p>
     *
     * @param value сырое значение из параметра запроса
     * @return нормализованная тема или {@code null}
     */
    public static String normalizeTopic(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.strip();
    }

    /**
     * Нормализует значение группы тем из HTTP-параметра.
     */
    public static String normalizeGroup(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.strip();
    }
}
