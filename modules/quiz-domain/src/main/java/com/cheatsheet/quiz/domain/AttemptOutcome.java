package com.cheatsheet.quiz.domain;

/**
 * Исход одной попытки ответа (таблица {@code attempt}).
 *
 * <p>Отдельный enum от {@link ReviewResult}: у попытки есть {@link #SKIP}
 * (вопрос пропущен без ответа, FLOW-03), которого нет в результатах SM-2.
 * Хранится в БД как TEXT.</p>
 */
public enum AttemptOutcome {

    /** Правильный ответ. */
    CORRECT,

    /** Неправильный ответ. */
    WRONG,

    /** Пользователь отметил «не знаю» (флешкарта/reveal). */
    UNKNOWN,

    /** Вопрос пропущен без ответа (FLOW-03). */
    SKIP;

    /**
     * Исход попытки из флага правильности обычного ответа.
     *
     * @param correct правильный ли ответ
     * @return {@link #CORRECT} или {@link #WRONG}
     */
    public static AttemptOutcome ofCorrect(boolean correct) {
        return correct ? CORRECT : WRONG;
    }
}
