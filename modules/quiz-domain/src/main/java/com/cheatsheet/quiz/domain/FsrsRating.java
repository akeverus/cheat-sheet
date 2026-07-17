package com.cheatsheet.quiz.domain;

/**
 * Оценка припоминания для планировщика FSRS (4 градации, как в оригинале).
 *
 * <p>В отличие от бинарного correct/incorrect, FSRS различает степень усилия
 * припоминания. Маппинг из существующих сигналов тренажёра выполняет вызывающий:
 * неверно/«не знаю» → {@link #AGAIN}, угадал → {@link #HARD}, уверенно → {@link #GOOD},
 * легко → {@link #EASY}.</p>
 */
public enum FsrsRating {

    /** Забыл / не смог вспомнить (лапс). */
    AGAIN(1),

    /** Вспомнил с трудом. */
    HARD(2),

    /** Вспомнил нормально. */
    GOOD(3),

    /** Вспомнил легко. */
    EASY(4);

    private final int value;

    FsrsRating(int value) {
        this.value = value;
    }

    /** Числовое значение 1..4 (индексация весов FSRS). */
    public int value() {
        return value;
    }
}
