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
        return normalize(value);
    }

    /**
     * Нормализует значение группы тем из HTTP-параметра.
     */
    public static String normalizeGroup(String value) {
        return normalize(value);
    }

    /**
     * Общая нормализация фильтр-значения из HTTP-параметра: обрезает пробелы по краям,
     * пустое/{@code null} → {@code null} (означает «без ограничения»).
     *
     * <p>Повторённый HTTP-параметр Spring биндит в String СКЛЕЙКОЙ через запятую:
     * живой репро (iter-189) — форма /train с серверным hidden topic активного
     * фильтра плюс второй одноимённый параметр дали тему
     * {@code «a,a»}, которой в банке нет, и сессия стартовала с 0 вопросов
     * («Сейчас нет вопросов» сразу после старта). Слаги тем и групп запятых не
     * содержат, поэтому склейка распознаваема однозначно: берём ПЕРВОЕ значение —
     * то, что стояло в форме раньше (серверный пресет фильтра).</p>
     */
    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        for (String part : value.split(",")) {
            if (!part.isBlank()) {
                return part.strip();
            }
        }
        return null;
    }
}
