package com.cheatsheet.quiz.common.util;

import com.cheatsheet.quiz.domain.InterviewMode;
import org.springframework.stereotype.Component;

/**
 * Преобразование {@link InterviewMode} в человекочитаемое русское название для UI.
 *
 * <p>Единый источник правды для отображаемых имён режимов: раньше
 * {@code session-summary.html} и {@code result.html} выводили сырое имя enum-константы
 * ({@code TRAINING}/{@code MARATHON}/…), хотя {@code settings.html} уже показывал
 * русские лейблы — пользователь, выбравший «Интенсив», на итогах видел «MARATHON»
 * (критика round-01 C22). Лейблы совпадают с {@code settings.html} (select «Режим»).
 *
 * <p>Презентационная утилита живёт в app-слое (как {@code topicUtils}), а не в
 * pure-domain enum — чтобы русские UI-строки не протекали в доменную модель.
 */
@Component("modeUtils")
public class ModeUtils {

    /**
     * @param mode режим тестирования (может быть {@code null})
     * @return отображаемое русское имя; для {@code null} — пустая строка
     */
    public String displayName(InterviewMode mode) {
        if (mode == null) {
            return "";
        }
        return switch (mode) {
            case TRAINING -> "Тренировка";
            case STUDY -> "Изучение";
            case FLASHCARD -> "Флешкарты";
            case EXAM -> "Экзамен";
            case MARATHON -> "Интенсив";
        };
    }
}
