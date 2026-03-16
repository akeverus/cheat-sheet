package com.cheatsheet.quiz.service.ai.dto;

import com.cheatsheet.quiz.service.ai.client.AbstractAiClient;
import java.util.List;
import lombok.Builder;

/**
 * Запрос к OpenAI-совместимому Chat API.
 *
 * <p>Используется {@link com.cheatsheet.quiz.service.ai.client.AbstractAiClient}
 * для отправки запросов к DeepSeek и OpenAI.</p>
 *
 * @param model       название модели (например, {@code "gpt-4o-mini"})
 * @param messages    список сообщений (system + user)
 * @param temperature температура генерации (0.0–1.0)
 * @param max_tokens  максимальное число токенов в ответе (null — использовать дефолт API)
 */
@Builder(toBuilder = true)
public record ChatRequest(
        String model,
        List<ChatMessage> messages,
        double temperature,
        @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
        Integer max_tokens
) {
    public ChatRequest(String model, List<ChatMessage> messages, double temperature) {
        this(model, messages, temperature, null);
    }
}
