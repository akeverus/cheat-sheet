package com.cheatsheet.quiz.service.ai;

import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * Immutable-снапшот результата quality-check Question Engine.
 */
@Value
@Builder
public class QuestionQualitySnapshot {

    /**
     * Полный список нарушений валидации и policy-check.
     */
    List<String> violations;

    /**
     * Нормализованный список нарушений для retry-prompt.
     */
    List<String> retryFeedback;

    /**
     * Итоговый quality score (0..100).
     */
    int score;

    /**
     * Признак приёмки кандидата.
     */
    boolean accepted;
}
