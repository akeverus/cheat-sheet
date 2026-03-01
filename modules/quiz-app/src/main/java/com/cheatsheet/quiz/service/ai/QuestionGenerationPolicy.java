package com.cheatsheet.quiz.service.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Политика приёма сгенерированного вопроса.
 */
@Component
public class QuestionGenerationPolicy {

    private final int minQualityScore;

    public QuestionGenerationPolicy(
            @Value("${app.interview.question-min-quality-score:70}") int minQualityScore
    ) {
        this.minQualityScore = Math.max(0, Math.min(100, minQualityScore));
    }

    public int minQualityScore() {
        return minQualityScore;
    }

    public boolean isAccepted(int score, List<String> violations) {
        return (violations == null || violations.isEmpty()) && score >= minQualityScore;
    }
}
