package com.cheatsheet.quiz.llm;

import com.cheatsheet.quiz.service.ai.parser.AiResponseParser;
import com.cheatsheet.quiz.service.ai.util.OpenAiJsonKeys;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

/**
 * Низкоуровневый парсер ответа OpenAI-совместимого chat endpoint.
 */
@UtilityClass
public class LlmResponseParser {

    /**
     * Извлекает контент ответа из JSON-пейлоада провайдера.
     *
     * @param responseJson сырой JSON-ответ провайдера
     * @param objectMapper object mapper для парсинга JSON
     * @return очищенный текстовый контент модели
     * @throws Exception если JSON невалиден
     */
    public String extractContent(String responseJson, ObjectMapper objectMapper) throws Exception {
        JsonNode root = objectMapper.readTree(responseJson);
        JsonNode contentNode = root.path(OpenAiJsonKeys.CHOICES).path(0).path(OpenAiJsonKeys.MESSAGE).path(OpenAiJsonKeys.CONTENT);
        String content = contentNode.asText(StringUtils.EMPTY);
        return AiResponseParser.stripCodeFences(content);
    }
}
