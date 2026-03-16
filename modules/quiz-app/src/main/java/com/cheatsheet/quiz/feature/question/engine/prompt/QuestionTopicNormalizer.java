package com.cheatsheet.quiz.feature.question.engine.prompt;

import org.springframework.stereotype.Component;

/**
 * Нормализатор темы для Question Engine.
 *
 * <p>Обеспечивает единый детерминированный fallback для пустых значений темы
 * в prompt/generation пайплайне.</p>
 */
@Component
public class QuestionTopicNormalizer {

    /**
     * Нормализует входное значение темы.
     *
     * @param topic исходная тема
     * @return нормализованная тема; для {@code null}/blank возвращается {@code "general"}
     */
    public String normalize(String topic) {
        if (topic == null) {
            return "general";
        }
        String normalized = topic.trim();
        return normalized.isBlank() ? "general" : normalized;
    }
}
