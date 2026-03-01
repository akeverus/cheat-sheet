package com.cheatsheet.quiz.service.ai;

import com.cheatsheet.quiz.domain.Difficulty;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionOption;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.service.AdaptiveDifficultyService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class QuestionGenerationService {

    private static final int MAX_REGEN_ATTEMPTS = 3;

    private final OptionGenerator optionGenerator;
    private final ObjectMapper objectMapper;
    private final QuestionValidationService validationService;
    private final AdaptiveDifficultyService adaptiveDifficultyService;
    private final QuestionPromptBuilder questionPromptBuilder;
    private final QuestionGenerationPolicy questionGenerationPolicy;

    public QuestionGenerationService(
            OptionGenerator optionGenerator,
            ObjectMapper objectMapper,
            QuestionValidationService validationService,
            AdaptiveDifficultyService adaptiveDifficultyService,
            QuestionPromptBuilder questionPromptBuilder,
            QuestionGenerationPolicy questionGenerationPolicy
    ) {
        this.optionGenerator = optionGenerator;
        this.objectMapper = objectMapper;
        this.validationService = validationService;
        this.adaptiveDifficultyService = adaptiveDifficultyService;
        this.questionPromptBuilder = questionPromptBuilder;
        this.questionGenerationPolicy = questionGenerationPolicy;
    }

    /**
     * Генерирует новый вопрос с адаптивной сложностью и обязательной валидацией качества.
     *
     * @param topic тема вопроса
     * @param type  тип вопроса
     * @return валидный вопрос в формате v2
     */
    public Question generateQuestion(String topic, QuestionType type) {
        Difficulty difficulty = adaptiveDifficultyService.resolveDifficulty(topic);
        String safeTopic = topic == null ? "general" : topic.trim();
        QuestionType safeType = type == null ? QuestionType.CONCEPT : type;
        log.info("question_generation_started topic={} type={} difficulty={}", safeTopic, safeType, difficulty);
        List<String> previousViolations = List.of();
        Set<String> seenFingerprints = new HashSet<>();
        int bestScore = 0;
        for (int attempt = 1; attempt <= MAX_REGEN_ATTEMPTS; attempt++) {
            Question generated = requestQuestion(safeTopic, safeType, difficulty, previousViolations)
                    .orElseThrow(() -> new AiGenerationException("AI не вернул валидный JSON вопроса"));
            List<String> baseViolations = validationService.validate(generated);
            List<String> violations = questionGenerationPolicy.enrichViolations(generated, baseViolations, seenFingerprints);
            int score = validationService.qualityScore(violations);
            bestScore = Math.max(bestScore, score);
            if (questionGenerationPolicy.isAccepted(score, violations)) {
                log.info("question_generation_succeeded topic={} type={} difficulty={} attempt={} qualityScore={}",
                        safeTopic, safeType, difficulty, attempt, score);
                return generated;
            }
            log.warn("question_generation_validation_failed topic={} type={} difficulty={} attempt={} qualityScore={} minQualityScore={} violations={}",
                    safeTopic, safeType, difficulty, attempt, score, questionGenerationPolicy.minQualityScore(),
                    String.join("; ", violations));
            previousViolations = List.copyOf(violations);
        }
        throw new AiGenerationException("QuestionGenerationService: не удалось сгенерировать валидный вопрос за "
                + MAX_REGEN_ATTEMPTS + " попытки (bestQualityScore=" + bestScore + ")");
    }

    private Optional<Question> requestQuestion(String topic, QuestionType type, Difficulty difficulty, List<String> previousViolations) {
        String prompt = questionPromptBuilder.buildQuestionPrompt(difficulty, type, topic, previousViolations);
        Optional<String> raw = optionGenerator.generateStructuredJson(prompt);
        if (raw.isEmpty()) {
            return Optional.empty();
        }
        try {
            JsonNode root = objectMapper.readTree(AiResponseParser.extractJsonObject(AiResponseParser.stripCodeFences(raw.get())));
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
                    "generated:" + System.currentTimeMillis(),
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
