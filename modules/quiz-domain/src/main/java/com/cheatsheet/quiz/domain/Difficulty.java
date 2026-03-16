package com.cheatsheet.quiz.domain;

/**
 * Уровень сложности вопроса для адаптивной генерации.
 */
public enum Difficulty {
    EASY,
    MEDIUM,
    HARD;

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
