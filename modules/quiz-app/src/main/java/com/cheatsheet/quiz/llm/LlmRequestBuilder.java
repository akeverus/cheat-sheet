package com.cheatsheet.quiz.llm;

import com.cheatsheet.quiz.service.ai.dto.ChatMessage;
import com.cheatsheet.quiz.service.ai.dto.ChatRequest;
import lombok.experimental.UtilityClass;

import java.util.List;

/**
 * Сборщик унифицированных chat-запросов к LLM.
 */
@UtilityClass
public class LlmRequestBuilder {
    private static final String STRICT_JSON_APPENDIX = """
            IMPORTANT:
            - Return strictly one valid JSON object.
            - Do not add markdown fences, comments, or any extra text.
            """;

    /**
     * Создаёт стандартный chat request с системным и пользовательским сообщениями.
     *
     * @param model имя модели
     * @param systemPrompt системный prompt
     * @param userPrompt пользовательский prompt
     * @param temperature температура генерации
     * @return запрос для отправки в LLM
     */
    public ChatRequest build(String model, String systemPrompt, String userPrompt, double temperature) {
        return new ChatRequest(
                model,
                List.of(
                        new ChatMessage("system", systemPrompt),
                        new ChatMessage("user", userPrompt)
                ),
                temperature
        );
    }

    /**
     * Создаёт chat request с лимитом токенов.
     *
     * @param model имя модели
     * @param systemPrompt системный prompt
     * @param userPrompt пользовательский prompt
     * @param temperature температура генерации
     * @param maxTokens лимит токенов
     * @return запрос для отправки в LLM
     */
    public ChatRequest buildWithMaxTokens(
            String model,
            String systemPrompt,
            String userPrompt,
            double temperature,
            int maxTokens
    ) {
        return new ChatRequest(
                model,
                List.of(
                        new ChatMessage("system", systemPrompt),
                        new ChatMessage("user", userPrompt)
                ),
                temperature,
                maxTokens
        );
    }

    /**
     * Усиливает пользовательский prompt для сценариев, где вызывающий код ожидает строго JSON-ответ.
     *
     * @param userPrompt исходный пользовательский prompt
     * @return prompt с дополнительным JSON-контрактом
     */
    public String withStrictJsonContract(String userPrompt) {
        if (userPrompt == null || userPrompt.isBlank()) {
            return STRICT_JSON_APPENDIX;
        }
        return userPrompt + "\n\n" + STRICT_JSON_APPENDIX;
    }
}
