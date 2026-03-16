package com.cheatsheet.quiz.domain;

/**
 * Маппинг UI-оценки flashcard в SM-2 grade.
 *
 * <p>UI grade: 1="Не помню", 2="Плохо", 3="Хорошо", 4="Отлично"</p>
 * <p>SM-2 grade: 1, 2, 4, 5 соответственно</p>
 */
public final class FlashcardGrade {

    private FlashcardGrade() {}

    public static int toSm2Grade(int uiGrade) {
        return switch (uiGrade) {
            case 1 -> 1;
            case 2 -> 2;
            case 3 -> 4;
            default -> 5;
        };
    }

    public static boolean isCorrect(int uiGrade) {
        return uiGrade >= 3;
    }
}
