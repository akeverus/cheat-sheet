package com.cheatsheet.quiz.domain;

import lombok.Builder;

/**
 * Семантически разведённые метрики обучения. В отличие от {@link InterviewStats}
 * (где {@code total} — карточки, а {@code correct/wrong} — суммы попыток, т.е.
 * смешанные знаменатели), здесь группы явно разделены, и у каждой доли —
 * собственный знаменатель:
 *
 * <ul>
 *   <li><b>Карточки:</b> {@code cardsTotal} (всего), {@code cardsSeen} (≥1 попытка),
 *       {@code cardsMastered} (repetitions ≥ порога).</li>
 *   <li><b>Попытки:</b> {@code attemptsTotal}, {@code attemptsCorrect},
 *       {@code attemptsIncorrect} (WRONG+UNKNOWN).</li>
 *   <li><b>Точность:</b> {@code firstAttemptAccuracy} (только первые попытки —
 *       честная память) vs {@code overallAccuracy} (все попытки, non-SKIP).</li>
 *   <li><b>Удержание:</b> {@code retention7d}/{@code retention30d} — доля верных
 *       среди повторений (не-первых попыток) в окне.</li>
 *   <li><b>Расписание:</b> {@code dueNow} (готовы сейчас) vs {@code overdue}
 *       (просрочены ≥ суток), {@code averageResponseMs}.</li>
 * </ul>
 *
 * <p>Доли — в процентах 0..100; значение {@code < 0} (константа {@link #NO_DATA})
 * означает «нет данных для знаменателя» — UI показывает «—», а не «0%».</p>
 */
@Builder(toBuilder = true)
public record LearningMetrics(
        long cardsTotal,
        long cardsSeen,
        long cardsMastered,
        long attemptsTotal,
        long attemptsCorrect,
        long attemptsIncorrect,
        double firstAttemptAccuracy,
        double overallAccuracy,
        double retention7d,
        double retention30d,
        long dueNow,
        long overdue,
        double averageResponseMs
) {

    /** Сентинел «нет данных» для долей и среднего времени. */
    public static final double NO_DATA = -1.0;

    /** Пустые метрики (пустая БД) — все доли в состоянии «нет данных». */
    public static final LearningMetrics EMPTY = new LearningMetrics(
            0, 0, 0, 0, 0, 0, NO_DATA, NO_DATA, NO_DATA, NO_DATA, 0, 0, NO_DATA);

    public LearningMetrics {
        if (cardsTotal < 0 || cardsSeen < 0 || cardsMastered < 0
                || attemptsTotal < 0 || attemptsCorrect < 0 || attemptsIncorrect < 0
                || dueNow < 0 || overdue < 0) {
            throw new IllegalArgumentException("Счётчики метрик не могут быть отрицательными");
        }
    }

    public boolean hasFirstAttemptAccuracy() {
        return firstAttemptAccuracy >= 0;
    }

    public boolean hasOverallAccuracy() {
        return overallAccuracy >= 0;
    }

    public boolean hasRetention7d() {
        return retention7d >= 0;
    }

    public boolean hasRetention30d() {
        return retention30d >= 0;
    }

    public boolean hasAverageResponse() {
        return averageResponseMs >= 0;
    }
}
