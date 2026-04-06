package com.cheatsheet.quiz.feature.question.engine.mapper;

import com.cheatsheet.quiz.domain.Difficulty;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionOption;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.feature.question.engine.metadata.QuestionGeneratedMetadataSupplier;
import com.cheatsheet.quiz.feature.question.engine.metadata.QuestionGeneratedSlugFactory;
import com.cheatsheet.quiz.feature.question.engine.model.GeneratedQuestionMetadata;
import com.cheatsheet.quiz.service.ai.parser.AiResponseParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Маппер JSON-ответа модели в доменный объект {@link Question}.
 *
 * <p>Изолирует parsing/mapping-логику от orchestration-сервиса генерации.</p>
 */
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuestionGeneratedJsonMapper {
    private final ObjectMapper objectMapper;
    private final QuestionGeneratedSlugFactory questionGeneratedSlugFactory;
    private final QuestionGeneratedMetadataSupplier questionGeneratedMetadataSupplier;

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
                    AiResponseParser.stripCodeFences(rawContent)
            );

            // Поддержка как одиночного вопроса, так и массива {"questions":[...]}.
            JsonNode payload = root.has("questions") && root.path("questions").isArray() && root.path("questions").size() > 0
                    ? root.path("questions").get(0)
                    : root;

            String questionText = payload.path("question").asText("").trim();
            if (questionText.isBlank()) {
                return Optional.empty();
            }
            String explanation = payload.path("explanation").asText("").trim();
            if (explanation.isBlank()) {
                return Optional.empty();
            }
            List<QuestionOption> options = new ArrayList<>();
            JsonNode optionsNode = payload.path("options");
            if (!optionsNode.isArray()) {
                return Optional.empty();
            }
            if (optionsNode.size() != 4) {
                return Optional.empty();
            }
            int correctCount = 0;
            for (int i = 0; i < optionsNode.size(); i++) {
                JsonNode node = optionsNode.get(i);
                if (!node.isObject()) {
                    return Optional.empty();
                }
                String optionText = node.path("text").asText("").trim();
                if (optionText.isBlank()) {
                    return Optional.empty();
                }
                boolean correct = node.path("correct").asBoolean(false);
                if (correct) {
                    correctCount++;
                }
                options.add(new QuestionOption(
                        optionIdByIndex(i),
                        optionText,
                        correct,
                        ""
                ));
            }
            if (correctCount != 1) {
                return Optional.empty();
            }

            // Перемешиваем порядок вариантов, чтобы правильный не был всегда на одном месте.
            Collections.shuffle(options);

            QuestionType resolvedType = type == null ? QuestionType.CONCEPT : type;
            GeneratedQuestionMetadata metadata = questionGeneratedMetadataSupplier.metadata();
            Question question = new Question(
                    0L,
                    questionGeneratedSlugFactory.nextSlug(),
                    metadata.getSourceSlug(),
                    metadata.getFilePath(),
                    topic,
                    questionText,
                    explanation,
                    false,
                    metadata.getSourceHash(),
                    resolvedType,
                    null,
                    null,
                    0,
                    null,
                    difficulty,
                    explanation,
                    explanation,
                    "",
                    List.of(),
                    options
            );
            return Optional.of(question);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private static String optionIdByIndex(int index) {
        if (index < 0) {
            return "";
        }
        if (index < 26) {
            return String.valueOf((char) ('A' + index));
        }
        return "OPT-" + index;
    }
}
