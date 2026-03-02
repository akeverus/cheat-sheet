package com.cheatsheet.quiz.service.ai;

import com.cheatsheet.quiz.domain.Difficulty;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionOption;
import com.cheatsheet.quiz.domain.QuestionType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Маппер JSON-ответа модели в доменный объект {@link Question}.
 *
 * <p>Изолирует parsing/mapping-логику от orchestration-сервиса генерации.</p>
 */
@Component
@RequiredArgsConstructor
public class QuestionGeneratedJsonMapper {

    private final ObjectMapper objectMapper;
    private final QuestionGeneratedSlugFactory questionGeneratedSlugFactory;

    /**
     * Преобразует сырой JSON-ответ модели в {@link Question}.
     *
     * @param rawContent  сырой ответ модели
     * @param topic       тема вопроса
     * @param type        тип вопроса
     * @param difficulty  сложность вопроса
     * @return распарсенный вопрос или {@link Optional#empty()}, если JSON невалиден
     */
    public Optional<Question> map(String rawContent, String topic, QuestionType type, Difficulty difficulty) {
        try {
            JsonNode root = objectMapper.readTree(
                    AiResponseParser.extractJsonObject(AiResponseParser.stripCodeFences(rawContent))
            );
            String questionText = root.path("questionText").asText("").trim();
            String codeSnippet = root.path("codeSnippet").isNull() ? null : root.path("codeSnippet").asText(null);
            String shortExplanation = root.path("shortExplanation").asText("").trim();
            String detailedExplanation = root.path("detailedExplanation").asText("").trim();
            String commonMistake = root.path("commonMistake").asText("").trim();

            List<QuestionOption> options = new ArrayList<>();
            JsonNode optionsNode = root.path("options");
            if (optionsNode.isArray()) {
                for (JsonNode node : optionsNode) {
                    options.add(new QuestionOption(
                            node.path("id").asText(""),
                            node.path("text").asText(""),
                            node.path("correct").asBoolean(false),
                            node.path("explanation").asText("")
                    ));
                }
            }
            List<String> tags = new ArrayList<>();
            JsonNode tagsNode = root.path("tags");
            if (tagsNode.isArray()) {
                for (JsonNode tagNode : tagsNode) {
                    String value = tagNode.asText("").trim();
                    if (!value.isBlank()) {
                        tags.add(value);
                    }
                }
            }

            QuestionType resolvedType = type == null ? QuestionType.CONCEPT : type;
            Question question = new Question(
                    0L,
                    questionGeneratedSlugFactory.nextSlug(),
                    "generated",
                    "generated",
                    topic,
                    questionText,
                    detailedExplanation,
                    false,
                    "generated",
                    resolvedType,
                    codeSnippet,
                    null,
                    0,
                    null,
                    difficulty,
                    shortExplanation,
                    detailedExplanation,
                    commonMistake,
                    tags,
                    options
            );
            return Optional.of(question);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
