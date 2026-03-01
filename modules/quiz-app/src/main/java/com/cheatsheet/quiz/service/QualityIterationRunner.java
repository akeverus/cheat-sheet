package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.config.AppProperties;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.persistence.AnswerOptionRepository;
import com.cheatsheet.quiz.persistence.HintRepository;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.service.ai.GeneratedOptionsMapper;
import com.cheatsheet.quiz.service.ai.OptionGenerationService;
import com.cheatsheet.quiz.service.ai.OptionQualityValidator;
import com.cheatsheet.quiz.service.ai.OptionQualityValidator.IssueCode;
import com.cheatsheet.quiz.service.ai.OptionQualityValidator.ValidationIssue;
import com.cheatsheet.quiz.service.ai.OptionQualityValidator.ValidationReport;
import com.cheatsheet.quiz.service.ai.PromptAutoTuningService;
import com.cheatsheet.quiz.service.ai.dto.GeneratedOptions;
import com.cheatsheet.quiz.service.cache.OptionCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Выполняет цикл улучшения качества: reset -> generate -> score -> prompt tuning.
 */
@Service
@Slf4j
public class QualityIterationRunner {
    private final AppProperties appProperties;
    private final QuestionRepository questionRepository;
    private final AnswerOptionRepository answerOptionRepository;
    private final HintRepository hintRepository;
    private final OptionCache optionCache;
    private final OptionGenerationService optionGenerationService;
    private final OptionQualityValidator optionQualityValidator;
    private final GeneratedOptionsMapper generatedOptionsMapper;
    private final PromptAutoTuningService promptAutoTuningService;

    public QualityIterationRunner(
            AppProperties appProperties,
            QuestionRepository questionRepository,
            AnswerOptionRepository answerOptionRepository,
            HintRepository hintRepository,
            OptionCache optionCache,
            OptionGenerationService optionGenerationService,
            OptionQualityValidator optionQualityValidator,
            GeneratedOptionsMapper generatedOptionsMapper,
            PromptAutoTuningService promptAutoTuningService
    ) {
        this.appProperties = appProperties;
        this.questionRepository = questionRepository;
        this.answerOptionRepository = answerOptionRepository;
        this.hintRepository = hintRepository;
        this.optionCache = optionCache;
        this.optionGenerationService = optionGenerationService;
        this.optionQualityValidator = optionQualityValidator;
        this.generatedOptionsMapper = generatedOptionsMapper;
        this.promptAutoTuningService = promptAutoTuningService;
    }

    public QualityRunReport runConfiguredIterations() {
        AppProperties.QualityIterations cfg = appProperties.getQualityIterations();
        return runIterations(
                cfg.getIterations(),
                cfg.getSampleSize(),
                cfg.getTargetScore(),
                cfg.getSleepBetweenIterationsMs()
        );
    }

    public QualityRunReport runIterations(int iterations, int sampleSize, int targetScore, long sleepBetweenIterationsMs) {
        int safeIterations = Math.max(1, iterations);
        int safeSampleSize = Math.max(1, sampleSize);
        int safeTargetScore = Math.max(0, Math.min(100, targetScore));
        long safeSleepMs = Math.max(0, sleepBetweenIterationsMs);

        promptAutoTuningService.reset();
        List<IterationResult> results = new ArrayList<>();
        for (int iteration = 1; iteration <= safeIterations; iteration++) {
            resetGeneratedArtifacts();
            List<Long> ids = questionRepository.findShuffledQuestionIds(false, false, safeSampleSize);
            if (ids.isEmpty()) {
                log.warn("Quality iteration {}: пропуск — не найдено вопросов для выборки", iteration);
                results.add(new IterationResult(iteration, 0, 0, 0, 0, 0, false, Map.of(), promptAutoTuningService.currentQualityContext()));
                continue;
            }
            List<Question> questions = loadQuestionsPreservingOrder(ids);
            List<Question> scorableQuestions = questions.stream()
                    .filter(this::isScorableForQualityIteration)
                    .toList();
            if (scorableQuestions.isEmpty()) {
                log.warn("Quality iteration {}: пропуск — выборка не содержит технических вопросов для объективной оценки", iteration);
                results.add(new IterationResult(iteration, 0, 0, 0, 0, 0, false, Map.of(), promptAutoTuningService.currentQualityContext()));
                continue;
            }
            int generationFailures = 0;
            for (Question question : scorableQuestions) {
                try {
                    optionGenerationService.ensureOptionsWithOutcome(question);
                } catch (Exception exception) {
                    generationFailures++;
                    log.warn("Quality iteration {}: генерация упала для questionId={} ('{}'): {}",
                            iteration,
                            question.id(),
                            preview(question.questionText(), 90),
                            exception.getMessage());
                }
            }

            IterationResult result = evaluateIteration(iteration, scorableQuestions, safeTargetScore);
            results.add(result);
            String tunedContext = promptAutoTuningService.applyTopIssues(result.issueCounts());
            log.info("Quality iteration {}/{}: avgScore={}, scored={}/{}, generationFailed={}, targetReached={}, tunedRules={}, topIssues={}",
                    iteration, safeIterations, result.averageScore(), result.scoredQuestions(), result.requestedQuestions(),
                    generationFailures,
                    result.targetReached(),
                    tunedContext.isBlank() ? 0 : tunedContext.split("\\n").length,
                    topIssuesSummary(result.issueCounts(), 5));

            if (iteration < safeIterations && safeSleepMs > 0) {
                sleepQuietly(safeSleepMs);
            }
        }

        double avgRunScore = results.stream().mapToInt(IterationResult::averageScore).average().orElse(0.0);
        int lastScore = results.isEmpty() ? 0 : results.get(results.size() - 1).averageScore();
        return new QualityRunReport(
                safeIterations,
                safeSampleSize,
                safeTargetScore,
                avgRunScore,
                lastScore,
                results
        );
    }

    private IterationResult evaluateIteration(int iteration, List<Question> questions, int targetScore) {
        Map<IssueCode, Integer> issueCounts = new EnumMap<>(IssueCode.class);
        int scored = 0;
        int minScore = 100;
        int maxScore = 0;
        int totalScore = 0;
        for (Question question : questions) {
            var options = answerOptionRepository.findByQuestionId(question.id());
            var mapped = generatedOptionsMapper.fromAnswerOptions(options);
            if (mapped.isEmpty()) {
                continue;
            }
            GeneratedOptions generatedOptions = mapped.get();
            ValidationReport report = optionQualityValidator.validateQualityReportWithContext(
                    generatedOptions,
                    question.questionText(),
                    question.answerMarkdown(),
                    question.topic()
            );
            for (ValidationIssue issue : report.issues()) {
                issueCounts.merge(issue.code(), 1, Integer::sum);
            }
            int score = toScore(report.penaltyScore());
            totalScore += score;
            minScore = Math.min(minScore, score);
            maxScore = Math.max(maxScore, score);
            scored++;
        }
        int averageScore = scored == 0 ? 0 : Math.round((float) totalScore / scored);
        if (scored == 0) {
            minScore = 0;
        }
        Map<IssueCode, Integer> immutableIssueCounts = new LinkedHashMap<>();
        issueCounts.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .forEach(entry -> immutableIssueCounts.put(entry.getKey(), entry.getValue()));
        return new IterationResult(
                iteration,
                questions.size(),
                scored,
                averageScore,
                minScore,
                maxScore,
                averageScore >= targetScore,
                Map.copyOf(immutableIssueCounts),
                promptAutoTuningService.currentQualityContext()
        );
    }

    private void resetGeneratedArtifacts() {
        answerOptionRepository.deleteAll();
        hintRepository.deleteAll();
        questionRepository.clearAllDiagrams();
        questionRepository.resetAllRegenCount();
        optionCache.invalidateAll();
    }

    private List<Question> loadQuestionsPreservingOrder(List<Long> ids) {
        Map<Long, Question> byId = new LinkedHashMap<>();
        for (Question question : questionRepository.findByIds(ids)) {
            byId.put(question.id(), question);
        }
        List<Question> ordered = new ArrayList<>();
        for (Long id : ids) {
            Question question = byId.get(id);
            if (question != null) {
                ordered.add(question);
            }
        }
        return ordered;
    }

    private static int toScore(int penaltyScore) {
        int clampedPenalty = Math.max(0, Math.min(100, penaltyScore));
        return 100 - clampedPenalty;
    }

    private static String preview(String value, int maxLength) {
        if (value == null) {
            return "";
        }
        return value.length() > maxLength ? value.substring(0, maxLength) + "…" : value;
    }

    private boolean isScorableForQualityIteration(Question question) {
        String text = question.questionText();
        if (text == null || text.isBlank()) {
            return false;
        }
        String normalized = text.toLowerCase(Locale.ROOT);
        return !(normalized.contains("опишите ситуацию")
                || normalized.contains("опишите опыт")
                || normalized.contains("приведите пример")
                || normalized.contains("что вы сделали")
                || normalized.contains("как вы")
                || normalized.contains("конфликт")
                || normalized.contains("провал проекта")
                || normalized.contains("оценк") && normalized.contains("performance review"));
    }

    private static String topIssuesSummary(Map<IssueCode, Integer> issueCounts, int limit) {
        if (issueCounts == null || issueCounts.isEmpty()) {
            return "none";
        }
        StringBuilder result = new StringBuilder();
        int index = 0;
        for (Map.Entry<IssueCode, Integer> entry : issueCounts.entrySet()) {
            if (index >= limit) {
                break;
            }
            if (index > 0) {
                result.append(", ");
            }
            result.append(entry.getKey().name()).append("=").append(entry.getValue());
            index++;
        }
        return result.toString();
    }

    private void sleepQuietly(long sleepMs) {
        try {
            Thread.sleep(sleepMs);
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
        }
    }

    public record IterationResult(
            int iteration,
            int requestedQuestions,
            int scoredQuestions,
            int averageScore,
            int minScore,
            int maxScore,
            boolean targetReached,
            Map<IssueCode, Integer> issueCounts,
            String promptContextUsed
    ) {}

    public record QualityRunReport(
            int iterations,
            int sampleSize,
            int targetScore,
            double averageRunScore,
            int lastIterationScore,
            List<IterationResult> iterationResults
    ) {}
}
