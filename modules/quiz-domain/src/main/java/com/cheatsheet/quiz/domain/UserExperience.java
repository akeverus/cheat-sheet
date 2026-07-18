package com.cheatsheet.quiz.domain;

import lombok.Builder;

/**
 * Накопленный опыт пользователя (таблица {@code user_experience}, синглтон).
 *
 * <p>XP — детерминированная агрегация реальных попыток (см. {@code METRICS_GLOSSARY.md}
 * и {@code ExperienceService.award}), а не выдуманное число. {@code level} в БД не
 * хранится — выводится из {@code xp} формулой {@link #levelForXp(long)}, чтобы
 * определение уровня жило в одном месте.</p>
 *
 * @param xp         суммарный опыт за всю историю (≥ 0)
 * @param bestStreak рекорд серии дней подряд (≥ 0), максимум {@code daily_activity} за историю
 * @param updatedAt  момент последнего начисления, epoch-секунды
 */
@Builder(toBuilder = true)
public record UserExperience(long xp, int bestStreak, long updatedAt) {

    /** XP-порог для уровня {@code level}: {@code 100 × (level−1)²}. */
    private static final double XP_PER_LEVEL_UNIT = 100.0;

    public UserExperience {
        if (xp < 0 || bestStreak < 0) {
            throw new IllegalArgumentException("XP и рекорд серии не могут быть отрицательными");
        }
    }

    /** Пустой опыт (чистая БД). */
    public static final UserExperience EMPTY = new UserExperience(0, 0, 0);

    /** Текущий уровень, выведенный из накопленного XP. */
    public int level() {
        return levelForXp(xp);
    }

    /**
     * Уровень для заданного XP: {@code floor(sqrt(xp / 100)) + 1}. Сублинейный рост —
     * каждый следующий уровень дороже предыдущего. Уровень 1 при xp = 0.
     */
    public static int levelForXp(long xp) {
        if (xp <= 0) {
            return 1;
        }
        return (int) Math.floor(Math.sqrt(xp / XP_PER_LEVEL_UNIT)) + 1;
    }

    /** XP, необходимый для начала уровня {@code level}: {@code 100 × (level−1)²}. */
    public static long xpForLevel(int level) {
        if (level <= 1) {
            return 0;
        }
        long base = level - 1L;
        return (long) (XP_PER_LEVEL_UNIT * base * base);
    }

    /** XP, накопленный внутри текущего уровня (от порога текущего уровня). */
    public long xpIntoLevel() {
        return xp - xpForLevel(level());
    }

    /** Сколько XP отделяет текущий уровень от следующего. */
    public long xpToNextLevel() {
        return xpForLevel(level() + 1) - xpForLevel(level());
    }
}
