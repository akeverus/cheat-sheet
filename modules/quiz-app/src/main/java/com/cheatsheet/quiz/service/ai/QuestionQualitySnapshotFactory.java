package com.cheatsheet.quiz.service.ai;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Фабрика сборки immutable-снапшотов качества Question Engine.
 */
@Component
public class QuestionQualitySnapshotFactory {

    /**
     * Собирает immutable-снапшот качества на основе рассчитанных метрик.
     *
     * @param violations    полный список нарушений
     * @param retryFeedback нормализованный список нарушений для retry-prompt
     * @param score         quality score (0..100)
     * @param accepted      признак приёмки кандидата
     * @return готовый immutable-снапшот качества
     */
    public QuestionQualitySnapshot create(
            List<String> violations,
            List<String> retryFeedback,
            int score,
            boolean accepted
    ) {
        List<String> safeViolations = violations == null ? List.of() : List.copyOf(violations);
        List<String> safeRetryFeedback = retryFeedback == null ? List.of() : List.copyOf(retryFeedback);
        int safeScore = Math.max(0, Math.min(100, score));
        return QuestionQualitySnapshot.builder()
                .violations(safeViolations)
                .retryFeedback(safeRetryFeedback)
                .score(safeScore)
                .accepted(accepted)
                .build();
    }
}
