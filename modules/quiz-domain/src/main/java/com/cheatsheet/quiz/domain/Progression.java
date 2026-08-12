package com.cheatsheet.quiz.domain;

/**
 * Место в пути — состояние прогрессии ВНЕ сессии.
 *
 * <h2>Найденный дефект, а не гипотеза</h2>
 * Прогрессия в продукте уже была честной ({@link UserExperience}: XP суммируется из реальных
 * попыток, уровень выводится формулой), но видел её человек ровно в одном месте — в подписи
 * под числом опыта на экране итогов сессии, 11px серым. Замер на живой сессии: «Уровень 7 · до
 * следующего 902 XP». Дальше этого экрана уровень не показывался нигде: ни в аналитике, ни на
 * тренировке. А до экрана итогов доезжают не все режимы — {@code TRAINING} работает вовсе без
 * сессии, то есть у ежедневного сценария прогрессии не было вообще.
 *
 * <p>Этот объект отвечает на вопрос «где я сейчас» без всякой сессии: ступень, уровень,
 * заполненность уровня и расстояние до следующей ступени. Он ничего не начисляет и ничего не
 * хранит — только собирает уже посчитанное.</p>
 *
 * @param rank         ступень гильдии для текущего уровня
 * @param level        текущий уровень
 * @param xpIntoLevel  опыт, накопленный внутри текущего уровня
 * @param xpToNextLevel «цена» текущего уровня — сколько XP отделяет его от следующего
 * @param totalXp      весь накопленный опыт
 * @param currentStreak серия дней подряд с ответами
 * @param bestStreak   рекорд серии за всю историю
 */
public record Progression(GuildRank rank, int level, long xpIntoLevel, long xpToNextLevel,
                          long totalXp, int currentStreak, int bestStreak) {

    public Progression {
        if (level < 1) {
            throw new IllegalArgumentException("Уровень начинается с 1");
        }
        if (xpIntoLevel < 0 || xpToNextLevel <= 0 || totalXp < 0 || currentStreak < 0 || bestStreak < 0) {
            throw new IllegalArgumentException("Отрицательных величин у прогрессии не бывает");
        }
    }

    /** Состояние на чистой базе: первая ступень, нулевой опыт. */
    public static Progression of(UserExperience experience, int currentStreak) {
        int level = experience.level();
        return new Progression(
                GuildRank.forLevel(level),
                level,
                experience.xpIntoLevel(),
                experience.xpToNextLevel(),
                experience.xp(),
                currentStreak,
                experience.bestStreak()
        );
    }

    /**
     * Заполненность текущего уровня в процентах, 0..100.
     *
     * <p>Округление вниз намеренно: «100%» обязано означать взятый уровень, а не 99.6,
     * округлённые вверх, — иначе полоса врёт ровно в той точке, где на неё смотрят.</p>
     */
    public int levelPercent() {
        return (int) Math.min(100, Math.floorDiv(xpIntoLevel * 100, xpToNextLevel));
    }

    /** Сколько XP осталось до следующего уровня. */
    public long xpLeftToNextLevel() {
        return Math.max(0, xpToNextLevel - xpIntoLevel);
    }

    /** Сколько уровней осталось до следующей ступени; 0 — если ступень последняя. */
    public int levelsToNextRank() {
        return rank.next().map(next -> Math.max(0, next.minLevel() - level)).orElse(0);
    }

    /** Название следующей ступени или {@code null}, если ступень последняя. */
    public String nextRankTitle() {
        return rank.next().map(GuildRank::title).orElse(null);
    }
}
