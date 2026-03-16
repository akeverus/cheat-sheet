package com.cheatsheet.quiz.service.ai.parser;
import com.cheatsheet.quiz.service.ai.AiQuestionClient;
import com.cheatsheet.quiz.service.ai.client.AbstractAiClient;
import com.cheatsheet.quiz.service.ai.dto.GeneratedOptions;
import com.cheatsheet.quiz.service.ai.util.CodeFenceConstants;
import com.cheatsheet.quiz.service.ai.util.OpenAiJsonKeys;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Утилитарный класс для парсинга ответов AI-модели.
 *
 * <p>Содержит общую логику:</p>
 * <ul>
 *   <li>парсинг JSON-ответа с вариантами ответов ({@link #parseOptions});</li>
 *   <li>удаление markdown code-fences ({@link #stripCodeFences});</li>
 *   <li>обрезание и превью текста ({@link #truncate}, {@link #preview}).</li>
 * </ul>
 *
 * <p>Используется в {@link AbstractAiClient} для устранения дублирования.</p>
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
            String text = item.path(OpenAiJsonKeys.TEXT_FIELD).asText(StringUtils.EMPTY);
            boolean isCorrect = item.path("correct").asBoolean(false);
            options.add(new GeneratedOptions.GeneratedOption(text, isCorrect));
        }
        return new GeneratedOptions(options);
    }

    /**
     * Парсит JSON-ответ с подсказками: {@code {"hints":["...","...","..."]}}.
     *
     * @param content      сырой JSON-ответ модели
     * @param objectMapper ObjectMapper для парсинга
     * @return список непустых подсказок (может быть пустым при ошибке или пустом массиве)
     */
    public static List<String> parseHints(String content, ObjectMapper objectMapper) throws Exception {
        JsonNode node = objectMapper.readTree(content);
        JsonNode arr = node.path(OpenAiJsonKeys.HINTS);
        List<String> result = new ArrayList<>();
        if (arr.isArray()) {
            for (int i = 0; i < arr.size(); i++) {
                String h = arr.get(i).asText(StringUtils.EMPTY).trim();
                if (!h.isBlank()) {
                    result.add(h);
                }
            }
        }
        return result;
    }

    /**
     * Парсит JSON-ответ с диаграммой: {@code {"needed": true, "mermaid": "..."}}.
     *
     * @param content      сырой JSON-ответ модели
     * @param objectMapper ObjectMapper для парсинга
     * @return Mermaid-код диаграммы или empty, если диаграмма не нужна или пуста
     */
    public static Optional<String> parseDiagram(String content, ObjectMapper objectMapper) throws Exception {
        JsonNode node = objectMapper.readTree(content);
        boolean needed = node.path(OpenAiJsonKeys.NEEDED).asBoolean(false);
        if (!needed) {
            return Optional.empty();
        }
        String mermaid = node.path(OpenAiJsonKeys.MERMAID).asText(StringUtils.EMPTY).trim();
        return mermaid.isBlank() ? Optional.empty() : Optional.of(mermaid);
    }

    /**
     * Парсит JSON-ответ с ключевым выводом: {@code {"takeaway": "..."}}.
     *
     * @param content      сырой JSON-ответ модели
     * @param objectMapper ObjectMapper для парсинга
     * @return текст takeaway или empty, если пусто
     */
    public static Optional<String> parseTakeaway(String content, ObjectMapper objectMapper) throws Exception {
        JsonNode node = objectMapper.readTree(content);
        String takeaway = node.path(OpenAiJsonKeys.TAKEAWAY).asText(StringUtils.EMPTY).trim();
        return takeaway.isBlank() ? Optional.empty() : Optional.of(takeaway);
    }

    /**
     * Парсит JSON-ответ с альтернативными вопросами: {@code {"questions": ["...", "..."]}}.
     *
     * @param content      сырой JSON-ответ модели
     * @param objectMapper ObjectMapper для парсинга
     * @return список непустых вопросов (может быть пустым)
     */
    public static List<String> parseAlternativeQuestions(String content, ObjectMapper objectMapper) throws Exception {
        JsonNode node = objectMapper.readTree(content);
        JsonNode arr = node.path(OpenAiJsonKeys.QUESTIONS);
        List<String> result = new ArrayList<>();
        if (arr.isArray()) {
            for (int i = 0; i < arr.size(); i++) {
                String raw = arr.get(i).asText(StringUtils.EMPTY).trim();
                if (!raw.isBlank()) {
                    result.add(raw);
                }
            }
        }
        return result;
    }

    public static Optional<AiQuestionClient.CanonicalQuestion> parseCanonicalQuestion(
            String content,
            ObjectMapper objectMapper
    ) throws Exception {
        JsonNode node = objectMapper.readTree(stripCodeFences(content));
        String questionText = node.path("questionText").asText(StringUtils.EMPTY).trim();
        String answerMarkdown = node.path("answerMarkdown").asText(StringUtils.EMPTY).trim();
        String questionType = node.path("questionType").asText("TEXT").trim();
        JsonNode codeNode = node.path("codeSnippet");
        String codeSnippet = codeNode.isNull() ? null : codeNode.asText(null);
        if (questionText.isBlank() || answerMarkdown.isBlank()) {
            return Optional.empty();
        }
        if (!"CODE".equalsIgnoreCase(questionType)) {
            questionType = "TEXT";
            codeSnippet = null;
        } else {
            questionType = "CODE";
            if (codeSnippet != null && codeSnippet.isBlank()) {
                codeSnippet = null;
            }
        }
        return Optional.of(new AiQuestionClient.CanonicalQuestion(
                questionText,
                answerMarkdown,
                questionType,
                codeSnippet
        ));
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
