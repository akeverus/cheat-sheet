package com.cheatsheet.quiz.domain;

/**
 * Уровень сложности вопроса, рассчитанный на основе статистики ответов пользователя.
 */
public enum QuestionDifficulty {

    /** Ещё не отвечали. */
    NEW("Новый", "new"),

    /** accuracy >= 80% при >= 3 попытках. */
    EASY("Лёгкий", "easy"),

    /** accuracy 50-80% или < 3 попыток. */
    MEDIUM("Средний", "medium"),

    /** accuracy < 50% при >= 3 попытках. */
    HARD("Сложный", "hard");

    /** Минимальное количество попыток для определения уровня (кроме NEW). */
    public static final int MIN_ATTEMPTS_FOR_LEVEL = 3;

    /** Порог точности (в процентах) для уровня EASY. */
    public static final double EASY_ACCURACY_THRESHOLD = 80.0;

    private final String label;
    private final String cssClass;

    QuestionDifficulty(String label, String cssClass) {
        this.label = label;
        this.cssClass = cssClass;
    }

    public String getLabel() {
        return label;
    }

    public String getCssClass() {
        return cssClass;
    }

    /**
     * Определяет сложность по статистике.
     *
     * @param correctCount количество правильных ответов
     * @param wrongCount   количество неправильных ответов
     * @return уровень сложности
     */
    public static QuestionDifficulty fromStats(int correctCount, int wrongCount) {
        int total = correctCount + wrongCount;
        if (total == 0) {
            return NEW;
        }
        double accuracy = (double) correctCount / total * 100.0;
        if (total < MIN_ATTEMPTS_FOR_LEVEL) {
            return MEDIUM;
        }
        if (accuracy >= EASY_ACCURACY_THRESHOLD) {
            return EASY;
        }
        if (accuracy >= 50.0) {
            return MEDIUM;
        }
        return HARD;
    }
}
