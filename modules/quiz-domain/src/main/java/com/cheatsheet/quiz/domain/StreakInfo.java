package com.cheatsheet.quiz.domain;
import lombok.Builder;

/**
 * Информация о текущей серии дней (streak).
 *
 * <p>Количество подряд идущих дней до сегодня с хотя бы одним ответом.</p>
 */
@Builder(toBuilder = true)
public record StreakInfo(int currentStreak) {
    public StreakInfo {
        if (currentStreak < 0) {
            throw new IllegalArgumentException("currentStreak must be >= 0");
        }
    }
}
