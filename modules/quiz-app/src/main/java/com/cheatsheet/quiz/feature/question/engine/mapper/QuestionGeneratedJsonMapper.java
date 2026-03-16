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
            String questionText = root.path("question").asText("");
            String explanation = root.path("explanation").asText("");
            List<QuestionOption> options = new ArrayList<>();
            JsonNode optionsNode = root.path("options");
            if (optionsNode.isArray()) {
                for (int i = 0; i < optionsNode.size(); i++) {
                    JsonNode node = optionsNode.get(i);
                    if (!node.isObject()) {
                        return Optional.empty();
                    }
                    String optionText = node.path("text").asText("");
                    options.add(new QuestionOption(
                            optionIdByIndex(i),
                            optionText,
                            node.path("correct").asBoolean(false),
                            ""
                    ));
                }
            }

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
