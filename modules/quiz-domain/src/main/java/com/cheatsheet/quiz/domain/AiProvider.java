package com.cheatsheet.quiz.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Перечисление поддерживаемых AI-провайдеров для генерации вариантов ответов.
 *
 * <ul>
 *   <li>{@link #OPENAI} — OpenAI ChatGPT (gpt-4o-mini и др.);</li>
 *   <li>{@link #DEEPSEEK} — DeepSeek API.</li>
 * </ul>
 *
 * <p>Настраивается через {@code app.ai-provider} в {@code application.yml}.</p>
 */
public enum AiProvider {

    /** OpenAI (ChatGPT). */
    OPENAI,

    /** DeepSeek. */
    DEEPSEEK;

    private static final Logger log = LoggerFactory.getLogger(AiProvider.class);

    /**
     * Безопасный парсинг строки в {@link AiProvider}.
     * При невалидном или {@code null} значении возвращает {@link #OPENAI}
     * и логирует предупреждение.
     *
     * @param value строковое представление провайдера (например, {@code "openai"}, {@code "deepseek"})
     * @return соответствующий {@link AiProvider} или {@link #OPENAI} по умолчанию
     */
    public static AiProvider fromString(String value) {
        if (value == null || value.isBlank()) {
            return OPENAI;
        }
        String normalized = value.strip().toUpperCase().replace("-", "_");
        // Обратная совместимость: "chatgpt" -> OPENAI
        if ("CHATGPT".equals(normalized)) {
            return OPENAI;
        }
        try {
            return valueOf(normalized);
        } catch (IllegalArgumentException e) {
            log.warn("Неизвестный AI-провайдер '{}', используется OPENAI по умолчанию", value);
            return OPENAI;
        }
    }
}
