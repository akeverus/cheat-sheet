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
        m.put("cardsTotal", def("Карточки всего", "count", "все карточки в каталоге", "lifetime", null));
        m.put("cardsSeen", def("Карточки с попыткой", "count", "карточки с ≥1 попыткой", "lifetime", "cardsTotal"));
        m.put("cardsMastered", def("Освоенные", "count", "repetitions ≥ порога mastered", "lifetime", "cardsTotal"));
        m.put("attemptsTotal", def("Попытки всего", "count", "все попытки в журнале", "lifetime", null));
        m.put("attemptsCorrect", def("Верные попытки", "count", "outcome=CORRECT", "lifetime", "attemptsTotal"));
        m.put("attemptsIncorrect", def("Неверные попытки", "count", "WRONG+UNKNOWN", "lifetime", "attemptsTotal"));
        m.put("firstAttemptAccuracy", def("Точность первой попытки", "percent",
                "верные среди первых попыток / число первых попыток", "lifetime", "firstAttempts"));
        m.put("overallAccuracy", def("Общая точность", "percent",
                "верные среди non-SKIP попыток / non-SKIP попытки", "lifetime", "nonSkipAttempts"));
        m.put("retention7d", def("Удержание 7д", "percent",
                "верные повторные попытки за 7 дней / повторные за 7 дней", "7d", "reviews7d"));
        m.put("retention30d", def("Удержание 30д", "percent",
                "верные повторные попытки за 30 дней / повторные за 30 дней", "30d", "reviews30d"));
        m.put("dueNow", def("К повторению", "count", "nextReview ≤ now", "now", null));
        m.put("overdue", def("Просрочено", "count", "nextReview ≤ now−1d", "now", null));
        m.put("averageResponseMs", def("Среднее время ответа", "ms",
                "среднее response_time_ms по попыткам с временем", "lifetime", "timedAttempts"));
        return m;
    }

    private static Definition def(String title, String unit, String formula, String period, String denominator) {
        return new Definition(title, unit, formula, period, denominator);
    }

    public record Definition(String title, String unit, String formula, String period, String denominator) {
    }
}
