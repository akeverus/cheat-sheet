package com.cheatsheet.quiz.service.ai;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

/**
 * Сервис расчета quality score для Question Engine.
 *
 * <p>Инкапсулирует правила штрафов по нарушениям, чтобы отделить scoring-политику
 * от структурной валидации вопроса.</p>
 */
@Component
public class QuestionQualityScorer {

    /**
     * Рассчитывает quality score (0..100) по списку нарушений.
     *
     * @param violations список нарушений валидации/политики
     * @return итоговый score, где 100 — идеальное качество, 0 — критический уровень
     */
    public int score(List<String> violations) {
        if (violations == null || violations.isEmpty()) {
            return 100;
        }
        int penalty = 0;
        for (String violation : violations) {
            String normalizedViolation = violation == null ? "" : violation.toLowerCase(Locale.ROOT);
            if (normalizedViolation.contains("exactly one correct option")) {
                penalty += 35;
            } else if (normalizedViolation.contains("exactly 4 options")) {
                penalty += 25;
            } else if (normalizedViolation.contains("duplicate candidate")) {
                penalty += 18;
            } else if (normalizedViolation.contains("cognitive load")) {
                penalty += 14;
            } else if (normalizedViolation.contains("distractors are semantically too similar")) {
                penalty += 14;
            } else if (normalizedViolation.contains("trivial")) {
                penalty += 20;
            } else if (normalizedViolation.contains("required")) {
                penalty += 15;
            } else if (normalizedViolation.contains("duplicate")) {
                penalty += 10;
            } else {
                penalty += 8;
            }
        }
        return Math.max(0, 100 - penalty);
    }
}
