package com.cheatsheet.quiz.domain;

/**
 * Уровень сложности вопроса.
 *
 * <p>Подпись ({@link #label()}) лежит здесь, а не в разметке, по той же причине, что и
 * {@code ReviewReason.message}: пока она собирается тернарником в шаблоне, добавленный
 * уровень не ломает сборку — он молча печатается как один из старых.</p>
 */
public enum Difficulty {
    EASY("Лёгкий"),
    MEDIUM("Средний"),
    HARD("Сложный");

    private final String label;

    Difficulty(String label) {
        this.label = label;
    }

    /** Подпись для бейджа — про один вопрос, поэтому единственное число. */
    public String label() {
        return label;
    }

    /**
     * Говорит ли значение что-то об ЭТОМ вопросе.
     *
     * <p>{@link #MEDIUM} — значение по умолчанию: в него сводятся {@code null}, пустая
     * строка и любой нераспознанный уровень (см. {@link #fromString}). Поэтому «Средний»
     * неотличим от «неизвестно» и утверждением быть не может: показывать его — значит
     * держать в шапке вопроса строку, одинаковую для всего банка.</p>
     *
     * <p>Тот же критерий уже применён к аналитике: донат распределения по сложности
     * скрывается, когда уровень в банке ровно один ({@code StatsPageService}). Экран
     * вопроса обязан отвечать так же, иначе продукт утверждает на одном экране то, что
     * на другом сам назвал неинформативным.</p>
     */
    public boolean informative() {
        return this != MEDIUM;
    }

    public static Difficulty fromString(String raw) {
        if (raw == null || raw.isBlank()) {
            return MEDIUM;
        }
        try {
            return Difficulty.valueOf(raw.strip().toUpperCase());
        } catch (IllegalArgumentException ignored) {
            return MEDIUM;
        }
    }
}
