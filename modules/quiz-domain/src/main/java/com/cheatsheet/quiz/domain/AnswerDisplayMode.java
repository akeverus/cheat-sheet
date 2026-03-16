package com.cheatsheet.quiz.domain;

/**
 * Режим отображения полного ответа после ответа пользователя.
 *
 * В текущем UX всегда используется {@link #FULL}.
 */
public enum AnswerDisplayMode {

    /** Полный ответ развёрнут. */
    FULL,

    /** Ответ в свёрнутом блоке с кнопкой «Показать». */
    SUMMARY,

    /** Только объяснения вариантов, ответ скрыт за кнопкой. */
    MINIMAL;

    /**
     * Определяет режим на основании правильности ответа и количества повторений.
     *
     * @param correct     пользователь ответил верно
     * @param repetitions количество последовательных правильных ответов (после обновления)
     * @return режим отображения
     */
    public static AnswerDisplayMode resolve(boolean correct, int repetitions) {
        return FULL;
    }
}
