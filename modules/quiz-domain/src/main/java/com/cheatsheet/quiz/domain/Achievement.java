package com.cheatsheet.quiz.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Знак отличия — ФАКТ, вычисленный из накопленных данных, а не начисленная награда.
 *
 * <h2>Почему факт, а не награда</h2>
 * Награду надо где-то хранить, и с этого момента она живёт отдельно от того, что описывает:
 * таблица говорит «знак получен», а данные — что порог не пройден, и разойтись они могут молча.
 * Здесь знак — чистая функция от {@link LearningMetrics} и {@link Progression}: он не
 * записывается, а ВЫЧИСЛЯЕТСЯ на каждом показе. Потерять его нельзя, подделать тоже.
 *
 * <p>Отсюда же следует, чего здесь нет: знаков за «зайти в приложение», «нажать кнопку» и
 * прочие действия, которые ничего не говорят о подготовке. Каждый порог — это результат
 * работы, который человек может проверить на соседнем экране.</p>
 *
 * @param id      идентификатор (он же имя глифа в спрайте)
 * @param title   название знака
 * @param goal    что нужно сделать — прозой, тем же словом, что и на экране прогресса
 * @param target  порог: сколько нужно
 */
public record Achievement(String id, String title, String goal, long target) {

    /** Состояние знака у конкретного человека. */
    public record Earned(Achievement achievement, long current, boolean earned) {

        /**
         * Доля выполнения в процентах, 0..100. Округление ВНИЗ: «99%» обязано означать,
         * что порог ещё не взят, иначе полоса врёт ровно там, где на неё смотрят.
         */
        public int percent() {
            if (achievement.target() <= 0) {
                return earned ? 100 : 0;
            }
            return (int) Math.min(100, Math.floorDiv(current * 100, achievement.target()));
        }

        /** Сколько осталось до порога; 0 — если знак уже получен. */
        public long remaining() {
            return earned ? 0 : Math.max(0, achievement.target() - current);
        }
    }

    /**
     * Каталог знаков. Порядок — порядок показа: от того, что берётся первым, к тому, что
     * требует месяцев.
     */
    public static final List<Achievement> CATALOG = List.of(
            new Achievement("badge-first-step", "Первый шаг", "ответить на первый вопрос", 1),
            new Achievement("badge-hundred", "Сотня", "ответить на 100 вопросов", 100),
            new Achievement("badge-steady-hand", "Твёрдая рука",
                    "держать 70% верных с первой попытки на дистанции в 50 ответов", 70),
            new Achievement("badge-week", "Неделя подряд", "заниматься семь дней подряд", 7),
            new Achievement("badge-mastery", "Полсотни освоенных",
                    "довести 50 вопросов до состояния «выучено»", 50),
            new Achievement("badge-month", "Месяц подряд", "заниматься тридцать дней подряд", 30));

    /**
     * Считает состояние всех знаков.
     *
     * <p>«Твёрдая рука» — единственный знак с ДВУМЯ условиями: точность считается только на
     * дистанции, иначе один верный ответ давал бы 100% и знак «за меткость» выдавался бы за
     * первый же клик. Пока ответов меньше пятидесяти, показывается сама дистанция — то, что
     * человек и набирает.</p>
     */
    public static List<Earned> evaluate(LearningMetrics metrics, Progression progression) {
        LearningMetrics m = metrics == null ? LearningMetrics.EMPTY : metrics;
        int streak = progression == null ? 0 : Math.max(progression.currentStreak(), progression.bestStreak());

        List<Earned> result = new ArrayList<>(CATALOG.size());
        for (Achievement badge : CATALOG) {
            long current;
            boolean earned;
            switch (badge.id()) {
                case "badge-first-step" -> {
                    current = Math.min(m.attemptsTotal(), 1);
                    earned = m.attemptsTotal() >= 1;
                }
                case "badge-hundred" -> {
                    current = m.attemptsTotal();
                    earned = current >= badge.target();
                }
                case "badge-steady-hand" -> {
                    boolean farEnough = m.attemptsTotal() >= 50;
                    current = farEnough && m.hasFirstAttemptAccuracy()
                            ? Math.round(m.firstAttemptAccuracy())
                            : Math.min(m.attemptsTotal(), 49);
                    earned = farEnough && m.hasFirstAttemptAccuracy()
                            && m.firstAttemptAccuracy() >= badge.target();
                }
                case "badge-week" -> {
                    current = streak;
                    earned = streak >= badge.target();
                }
                case "badge-mastery" -> {
                    current = m.cardsMastered();
                    earned = current >= badge.target();
                }
                case "badge-month" -> {
                    current = streak;
                    earned = streak >= badge.target();
                }
                default -> throw new IllegalStateException("знак без правила: " + badge.id());
            }
            result.add(new Earned(badge, current, earned));
        }
        return result;
    }

    /** Сколько знаков получено — для подписи витрины. */
    public static long earnedCount(List<Earned> badges) {
        return badges.stream().filter(Earned::earned).count();
    }
}
