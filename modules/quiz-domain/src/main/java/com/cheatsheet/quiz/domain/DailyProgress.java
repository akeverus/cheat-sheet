package com.cheatsheet.quiz.domain;
import lombok.Builder;

/**
 * Прогресс за текущий день: ответы, цель, достигнута ли цель.
 *
 * <p>Соответствует одной строке из {@code daily_activity} за сегодня.</p>
 */
@Builder(toBuilder = true)
public record DailyProgress(int questionsAnswered, int correctCount, int goal) {
    public DailyProgress {
        if (questionsAnswered < 0 || correctCount < 0 || goal < 0) {
            throw new IllegalArgumentException("questionsAnswered, correctCount and goal must be >= 0");
        }
    }

    /**
     * Достигнута ли дневная цель (questionsAnswered >= goal).
     */
    public boolean goalReached() {
        return questionsAnswered >= goal;
    }
}
