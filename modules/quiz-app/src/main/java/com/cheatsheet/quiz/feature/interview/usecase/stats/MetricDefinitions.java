package com.cheatsheet.quiz.feature.interview.usecase.stats;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Metric definitions for honest analytics UI (Wave 2 / P1-05).
 * Source of truth for denominators — mirrors LearningMetrics domain record.
 */
public final class MetricDefinitions {

    private MetricDefinitions() {
    }

    public static Map<String, Definition> catalog() {
        Map<String, Definition> m = new LinkedHashMap<>();
        // formula — user-facing проза для карточек аналитики: человекочитаемое
        // определение метрики (что честно считается), БЕЗ имён DB-колонок/enum/
        // код-полей. Точный знаменатель несёт поле denominator, период — period;
        // «формула» их не дублирует машинными токенами (иначе UI = debug-вывод).
        m.put("cardsTotal", def("Карточки всего", "count", "все карточки в каталоге", "lifetime", null));
        m.put("cardsSeen", def("Карточки с попыткой", "count", "карточки хотя бы с одной попыткой", "lifetime", "cardsTotal"));
        m.put("cardsMastered", def("Освоенные", "count", "повторений не меньше порога освоения", "lifetime", "cardsTotal"));
        m.put("attemptsTotal", def("Попытки всего", "count", "все попытки в журнале", "lifetime", null));
        m.put("attemptsCorrect", def("Верные попытки", "count", "отмечены как верные", "lifetime", "attemptsTotal"));
        m.put("attemptsIncorrect", def("Неверные попытки", "count", "неверные и «не знаю»", "lifetime", "attemptsTotal"));
        m.put("firstAttemptAccuracy", def("Точность первой попытки", "percent",
                "верные среди первых попыток / число первых попыток", "lifetime", "firstAttempts"));
        // «Все попытки» — правда словаря пользователя: outcome SKIP в знаменатель
        // не входит, но его не пишет ни один обработчик (кнопка «Не знаю» пишет
        // UNKNOWN, и он ошибка). Проза про «без пропусков» объясняла мёртвую ветвь
        // и молчала про живую — читатель решал, что его «не знаю» не считаются.
        m.put("overallAccuracy", def("Общая точность", "percent",
                "верные попытки / все попытки; «не знаю» считается ошибкой", "lifetime", "nonSkipAttempts"));
        m.put("retention7d", def("Удержание 7д", "percent",
                "верные повторные попытки за 7 дней / повторные за 7 дней", "7d", "reviews7d"));
        m.put("retention30d", def("Удержание 30д", "percent",
                "верные повторные попытки за 30 дней / повторные за 30 дней", "30d", "reviews30d"));
        // dueNow отвечает на вопрос «что тренажёр может показать сейчас», поэтому
        // включает и вопросы без расписания. Формула обязана называть обе половины:
        // карточка «Очередь» печатает её прямо под числом, и «срок повторения
        // наступил» было бы ложью про ту часть, которую пользователь не открывал —
        // на пустом прогрессе это весь банк. Разбивку несут dueFresh и dueRepeats().
        m.put("dueNow", def("Готовы к показу", "count",
                "срок повторения наступил либо вопрос ещё не открывали", "now", null));
        m.put("dueFresh", def("Новые", "count", "вопрос ни разу не открывали", "now", "dueNow"));
        m.put("overdue", def("Просрочено", "count", "срок повторения просрочен больше суток", "now", null));
        m.put("averageResponseMs", def("Среднее время ответа", "ms",
                "среднее время ответа по попыткам с замером", "lifetime", "timedAttempts"));
        return m;
    }

    private static Definition def(String title, String unit, String formula, String period, String denominator) {
        return new Definition(title, unit, formula, period, denominator);
    }

    public record Definition(String title, String unit, String formula, String period, String denominator) {
    }
}
