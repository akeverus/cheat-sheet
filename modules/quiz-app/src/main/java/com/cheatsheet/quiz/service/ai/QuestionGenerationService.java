package com.cheatsheet.quiz.service.ai;

import com.cheatsheet.quiz.config.AppProperties;
import com.cheatsheet.quiz.domain.Difficulty;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.service.AdaptiveDifficultyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class QuestionGenerationService {

    private final OptionGenerator optionGenerator;
    private final QuestionGeneratedJsonMapper questionGeneratedJsonMapper;
    private final QuestionQualityEvaluator questionQualityEvaluator;
    private final QuestionTopicNormalizer questionTopicNormalizer;
    private final AdaptiveDifficultyService adaptiveDifficultyService;
    private final QuestionPromptBuilder questionPromptBuilder;
    private final QuestionGenerationPolicy questionGenerationPolicy;
    private final QuestionUniquenessService questionUniquenessService;
    private final int maxGenerationAttempts;

    public QuestionGenerationService(
            OptionGenerator optionGenerator,
            QuestionGeneratedJsonMapper questionGeneratedJsonMapper,
            QuestionQualityEvaluator questionQualityEvaluator,
            QuestionTopicNormalizer questionTopicNormalizer,
            AdaptiveDifficultyService adaptiveDifficultyService,
            QuestionPromptBuilder questionPromptBuilder,
            QuestionGenerationPolicy questionGenerationPolicy,
            QuestionUniquenessService questionUniquenessService,
            AppProperties appProperties
    ) {
        this.optionGenerator = optionGenerator;
        this.questionGeneratedJsonMapper = questionGeneratedJsonMapper;
        this.questionQualityEvaluator = questionQualityEvaluator;
        this.questionTopicNormalizer = questionTopicNormalizer;
        this.adaptiveDifficultyService = adaptiveDifficultyService;
        this.questionPromptBuilder = questionPromptBuilder;
        this.questionGenerationPolicy = questionGenerationPolicy;
        this.questionUniquenessService = questionUniquenessService;
        this.maxGenerationAttempts = Math.max(1, appProperties.getInterview().getQuestionGenerationMaxAttempts());
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
        String safeTopic = questionTopicNormalizer.normalize(topic);
        QuestionType safeType = type == null ? QuestionType.CONCEPT : type;
        log.info("question_generation_started topic={} type={} difficulty={}", safeTopic, safeType, difficulty);
        List<String> previousViolations = List.of();
        Set<String> seenFingerprints = new HashSet<>(questionUniquenessService.loadRecentFingerprints(safeTopic, safeType));
        int bestScore = 0;
        for (int attempt = 1; attempt <= maxGenerationAttempts; attempt++) {
            Optional<Question> generatedOpt = requestQuestion(safeTopic, safeType, difficulty, previousViolations);
            if (generatedOpt.isEmpty()) {
                log.warn("question_generation_request_failed topic={} type={} difficulty={} attempt={}/{}",
                        safeTopic, safeType, difficulty, attempt, maxGenerationAttempts);
                previousViolations = questionQualityEvaluator.retryFeedbackForRequestFailure();
                continue;
            }
            Question generated = generatedOpt.get();
            String candidateFingerprint = questionGenerationPolicy.fingerprint(generated);
            QuestionQualitySnapshot qualitySnapshot =
                    questionQualityEvaluator.evaluateCandidate(generated, seenFingerprints);
            List<String> violations = qualitySnapshot.getViolations();
            int score = qualitySnapshot.getScore();
            bestScore = Math.max(bestScore, score);
            seenFingerprints.add(candidateFingerprint);
            if (qualitySnapshot.isAccepted()) {
                log.info("question_generation_succeeded topic={} type={} difficulty={} attempt={} qualityScore={}",
                        safeTopic, safeType, difficulty, attempt, score);
                questionUniquenessService.rememberFingerprint(
                        safeTopic,
                        safeType,
                        candidateFingerprint
                );
                return generated;
            }
            log.warn("question_generation_validation_failed topic={} type={} difficulty={} attempt={} qualityScore={} minQualityScore={} violations={}",
                    safeTopic, safeType, difficulty, attempt, score, questionGenerationPolicy.minQualityScore(),
                    String.join("; ", violations));
            previousViolations = qualitySnapshot.getRetryFeedback();
        }
        throw new AiGenerationException(
                "QuestionGenerationService: не удалось сгенерировать валидный вопрос за "
                        + maxGenerationAttempts + " попытки (bestQualityScore=" + bestScore + ")"
        );
    }

    private Optional<Question> requestQuestion(String topic, QuestionType type, Difficulty difficulty, List<String> previousViolations) {
        String prompt = questionPromptBuilder.buildQuestionPrompt(difficulty, type, topic, previousViolations);
        Optional<String> raw = optionGenerator.generateStructuredJson(prompt);
        return raw.flatMap(content -> questionGeneratedJsonMapper.map(content, topic, type, difficulty));
    }

}
