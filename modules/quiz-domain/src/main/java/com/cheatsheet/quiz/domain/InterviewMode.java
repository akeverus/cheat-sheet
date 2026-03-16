package com.cheatsheet.quiz.domain;

import lombok.extern.slf4j.Slf4j;

/**
 * Режим прохождения тестирования перед интервью.
 *
 * <ul>
 *   <li>{@link #TRAINING} — свободная тренировка (бесконечные вопросы по одному);</li>
 *   <li>{@link #EXAM} — экзамен с фиксированным числом вопросов и штрафными при ошибках;</li>
 *   <li>{@link #MARATHON} — марафон с большим количеством вопросов подряд;</li>
 *   <li>{@link #STUDY} — двухфазный режим: сначала изучение (показ ответа), затем проверка;</li>
 *   <li>{@link #FLASHCARD} — режим флешкарт (active recall): вопрос → мысленный ответ → раскрытие → самооценка.</li>
 * </ul>
 */
@Slf4j
public enum InterviewMode {

    /** Свободная тренировка без ограничений. */
    TRAINING,

    /** Экзамен: фиксированный набор вопросов + штрафные при ошибках. */
    EXAM,

    /** Марафон: длинная серия вопросов. */
    MARATHON,

    /** Изучение: сначала учебная карточка, затем тест. */
    STUDY,

    /** Флешкарты: вопрос → раскрытие ответа → самооценка (как Anki). */
    FLASHCARD;

    /**
     * Парсинг строки в {@link InterviewMode}.
     * При {@code null} или пустом значении возвращает {@link #TRAINING} (значение по умолчанию).
     * При невалидном значении логирует предупреждение и возвращает {@link #TRAINING}.
     *
     * @param mode строковое представление (из HTTP-параметра)
     * @return режим тестирования
     */
    public static InterviewMode fromString(String mode) {
        if (mode == null || mode.isBlank()) {
            return TRAINING;
        }
        try {
            return valueOf(mode.strip().toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Неизвестный InterviewMode: '{}', используется TRAINING по умолчанию", mode);
            return TRAINING;
        }
    }
}
