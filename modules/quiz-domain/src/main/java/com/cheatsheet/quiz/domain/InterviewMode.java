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
 *   <li>{@link #FLASHCARD} — режим флешкарт (active recall): вопрос → мысленный ответ → раскрытие → самооценка;</li>
 *   <li>{@link #TYPED_RECALL} — печатный ответ в textarea → раскрытие → самооценка;</li>
 *   <li>{@link #INTERVIEW} — устный/развёрнутый ответ (textarea как заметки) → раскрытие → самооценка;</li>
 *   <li>{@link #CODE_CHALLENGE} — код в monospace-textarea → раскрытие → самооценка.</li>
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
    FLASHCARD,

    /** Печатный active recall: textarea → раскрытие → самооценка. */
    TYPED_RECALL,

    /** Интервью: развёрнутый ответ (textarea как заметки) → раскрытие → самооценка. */
    INTERVIEW,

    /** Код-челлендж: monospace-textarea → раскрытие → самооценка. */
    CODE_CHALLENGE;

    /** Режимы семейства active recall (флешкарта + typed/interview/code). */
    public boolean isRecallMode() {
        return this == FLASHCARD || this == TYPED_RECALL || this == INTERVIEW || this == CODE_CHALLENGE;
    }

    /** Режимы с обязательным/ожидаемым вводом в textarea перед reveal. */
    public boolean usesTypedInput() {
        return this == TYPED_RECALL || this == INTERVIEW || this == CODE_CHALLENGE;
    }

    /** Режим с monospace-textarea для кода. */
    public boolean usesCodeEditor() {
        return this == CODE_CHALLENGE;
    }

    /**
     * Режим, где ответ ПРОГОВАРИВАЕТСЯ вслух, а поле ввода — лишь заметки к речи.
     *
     * <p>Предикат-близнец {@link #usesCodeEditor()}: оба отвечают на вопрос «какая
     * поверхность нужна работе», а не «как называется режим». До него интервью и
     * код-челлендж отличались от печатного пересказа только подписью поля — рабочее
     * место у всех трёх было одно и то же, хотя работа разная: код пишут, речь
     * репетируют по плану и укладывают во время.
     */
    public boolean usesSpokenRehearsal() {
        return this == INTERVIEW;
    }

    /**
     * Режим без сессии и без счётчика вопросов: идёт, пока пользователь не остановит.
     *
     * <p>Предикат, а не сравнение с константой на стороне вызывающего: от него зависят
     * три разных решения — {@code SessionFlowService} не создаёт сессию, форма старта
     * прячет поле «Вопросов», предпросмотр пишет «без ограничения». Пока это
     * было {@code == TRAINING} в трёх местах (Java, шаблон, JS), любое из них могло
     * разойтись с остальными молча.
     */
    public boolean isUnlimited() {
        return this == TRAINING;
    }

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
