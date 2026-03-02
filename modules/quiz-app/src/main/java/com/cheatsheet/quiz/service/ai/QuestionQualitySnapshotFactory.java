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
    public QuestionQualityEvaluator.QualitySnapshot create(
            List<String> violations,
            List<String> retryFeedback,
            int score,
            boolean accepted
    ) {
        return new QuestionQualityEvaluator.QualitySnapshot(violations, retryFeedback, score, accepted);
    }
}
