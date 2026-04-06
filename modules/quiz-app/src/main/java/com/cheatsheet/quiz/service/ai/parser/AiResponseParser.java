package com.cheatsheet.quiz.service.ai.parser;
import com.cheatsheet.quiz.service.ai.dto.GeneratedOptions;
import com.cheatsheet.quiz.service.ai.util.CodeFenceConstants;
import com.cheatsheet.quiz.service.ai.util.OpenAiJsonKeys;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Утилитарный класс для парсинга ответов AI-модели.
 *
 * <p>Содержит общую логику:</p>
 * <ul>
 *   <li>парсинг JSON-ответа с вариантами ответов ({@link #parseOptions});</li>
 *   <li>удаление markdown code-fences ({@link #stripCodeFences});</li>
 *   <li>обрезание и превью текста ({@link #truncate}, {@link #preview}).</li>
 * </ul>
 */
@UtilityClass
public class AiResponseParser {

    /**
     * Парсит JSON-ответ модели в {@link GeneratedOptions}.
     *
     * <p>Строгий контракт: {@code {"question":"...","options":[{"text":"...","correct":true|false}, ...]}}.</p>
     *
     * @param content      сырой JSON-ответ модели
     * @param objectMapper ObjectMapper для парсинга
     * @return распарсенные варианты
     */
    public static GeneratedOptions parseOptions(String content, ObjectMapper objectMapper) throws Exception {
        JsonNode node = objectMapper.readTree(content);
        JsonNode optionsNode = node.path("options");
        if (optionsNode.isArray()) {
            return parseOptionsArrayPayload(optionsNode);
        }
        throw new IllegalArgumentException("LLM options payload must contain options[] with text/correct fields");
    }

    private static GeneratedOptions parseOptionsArrayPayload(JsonNode optionsNode) {
        List<GeneratedOptions.GeneratedOption> options = new ArrayList<>();
        for (JsonNode item : optionsNode) {
            if (!item.isObject()) {
                throw new IllegalArgumentException("LLM options payload must contain object items in options[]");
            }
            String text = item.path(OpenAiJsonKeys.TEXT_FIELD).asText(StringUtils.EMPTY).trim();
            if (text.isBlank()) {
                throw new IllegalArgumentException("LLM options payload must contain non-blank text in options[]");
            }
            boolean isCorrect = item.path("correct").asBoolean(false);
            options.add(new GeneratedOptions.GeneratedOption(text, isCorrect));
        }
        return new GeneratedOptions(options);
    }

    /**
     * Удаляет markdown code-fences (```json ... ```) из ответа модели.
     *
     * @param text сырой текст ответа
     * @return текст без обёртки code-fence
     */
    public static String stripCodeFences(String text) {
        if (text == null) {
            return "";
        }
        String value = text.trim();
        if (value.startsWith(CodeFenceConstants.CODE_FENCE)) {
            value = value.replaceAll(CodeFenceConstants.REGEX_CODE_FENCE_LEADING, StringUtils.EMPTY)
                    .replaceAll(CodeFenceConstants.REGEX_CODE_FENCE_TRAILING, StringUtils.EMPTY)
                    .trim();
        }
        return value;
    }

    /**
     * Обрезает текст до заданной длины для защиты от чрезмерно длинных AI-запросов.
     * Суффикс «...» добавляется при обрезке.
     *
     * @param text      исходный текст
     * @param maxLength максимальная длина
     * @return обрезанный текст или исходный, если короче maxLength
     */
    public static String truncate(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        return text.length() > maxLength ? text.substring(0, maxLength) + "..." : text;
    }

    /**
     * Возвращает превью текста заданной длины с суффиксом «…» при обрезке.
     * Используется для логирования и отображения краткого фрагмента.
     *
     * @param text      исходный текст
     * @param maxLength максимальная длина превью
     * @return превью строки
     */
    public static String preview(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        return text.length() > maxLength ? text.substring(0, maxLength) + "…" : text;
    }
}
