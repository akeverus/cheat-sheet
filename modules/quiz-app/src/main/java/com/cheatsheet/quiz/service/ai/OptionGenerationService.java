package com.cheatsheet.quiz.service.ai;

import com.cheatsheet.quiz.config.AppProperties;
import com.cheatsheet.quiz.domain.AnswerOption;
import com.cheatsheet.quiz.domain.OptionSource;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.persistence.AnswerOptionRepository;
import com.cheatsheet.quiz.persistence.AnswerOptionRepository.AnswerOptionCreate;
import com.cheatsheet.quiz.service.admin.SeniorRulePriorityOverrideStore;
import com.cheatsheet.quiz.service.ai.dto.GeneratedOptions;
import com.cheatsheet.quiz.service.cache.OptionCache;
import com.google.common.util.concurrent.Striped;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;
import java.util.concurrent.locks.Lock;

/**
 * Оркестратор генерации и управления вариантами ответов для вопросов квиза.
 *
 * <p>Координирует:</p>
 * <ul>
 *   <li>кэш и БД ({@link OptionCache}, {@link AnswerOptionRepository});</li>
 *   <li>AI-генерацию ({@link OptionGenerator});</li>
 *   <li>дедупликацию AI-вариантов ({@link OptionDeduplicator}).</li>
 * </ul>
 *
 * @see OptionGenerator
 * @see OptionDeduplicator
 */
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OptionGenerationService {

    public record GenerationOutcome(boolean acceptedWithWarnings, boolean reducedOptions) {
        static GenerationOutcome clean() {
            return new GenerationOutcome(false, false);
        }
    }
    private record OptionsFetchResult(List<AnswerOption> options, GenerationOutcome outcome) {}

    private record MergedOptions(String correct, String correctExplanation, List<String> wrong, List<String> wrongExplanations) {}
    private record GeneratedBuildResult(
            List<AnswerOptionCreate> created,
            boolean acceptedWithWarnings,
            boolean reducedOptions
    ) {}
    private record DedupSelection(OptionDeduplicator.DedupResult dedupResult, boolean reducedOptions) {}
    private static final int CURRENT_OPTION_PROMPT_VERSION = AiPrompts.OPTION_PROMPT_VERSION;
    private static final int CURRENT_OPTION_QUALITY_PROFILE_VERSION = AiPrompts.OPTION_QUALITY_PROFILE_VERSION;
    AnswerOptionRepository answerOptionRepository;
    OptionGenerator optionGenerator;
    OptionCache optionCache;
    TransactionTemplate transactionTemplate;
    OptionDeduplicator deduplicator;
    DomainDifficultyContextBuilder domainDifficultyContextBuilder;
    OptionQualityValidator optionQualityValidator;

    int optionsCount;
    int minAcceptedOptions;
    int optionQualityRetryAttempts;
    long optionQualityRetryBackoffMs;
    long optionQualityRetryMaxElapsedMs;
    Map<String, Integer> seniorRulePriorityOverrides;
    Striped<Lock> questionLocks;

    public OptionGenerationService(
            AnswerOptionRepository answerOptionRepository,
            OptionGenerator optionGenerator,
            OptionCache optionCache,
            AppProperties appProperties,
            SeniorRulePriorityOverrideStore seniorRulePriorityOverrideStore,
            Striped<Lock> questionLocks,
            TransactionTemplate transactionTemplate,
            OptionDeduplicator deduplicator,
            DomainDifficultyContextBuilder domainDifficultyContextBuilder,
            OptionQualityValidator optionQualityValidator
    ) {
        this.answerOptionRepository = answerOptionRepository;
        this.optionGenerator = optionGenerator;
        this.optionCache = optionCache;
        this.optionsCount = appProperties.getInterview().getOptionsCount();
        this.minAcceptedOptions = appProperties.getInterview().getOptionMinAcceptedCount();
        this.optionQualityRetryAttempts = appProperties.getInterview().getOptionQualityRetryAttempts();
        this.optionQualityRetryBackoffMs = appProperties.getInterview().getOptionQualityRetryBackoffMs();
        this.optionQualityRetryMaxElapsedMs = appProperties.getInterview().getOptionQualityRetryMaxElapsedMs();
        this.seniorRulePriorityOverrides = seniorRulePriorityOverrideStore.view();
        this.questionLocks = questionLocks;
        this.transactionTemplate = transactionTemplate;
        this.deduplicator = deduplicator;
        this.domainDifficultyContextBuilder = domainDifficultyContextBuilder;
        this.optionQualityValidator = optionQualityValidator;
    }

    public List<AnswerOption> getOrCreateOptions(Question question) {
        return getOrCreateOptionsInternal(question, true).options();
    }

    public void ensureOptions(Question question) {
        ensureOptionsWithOutcome(question);
    }

    public GenerationOutcome ensureOptionsWithOutcome(Question question) {
        return getOrCreateOptionsInternal(question, false).outcome();
    }

    private OptionsFetchResult getOrCreateOptionsInternal(Question question, boolean useCache) {
        GenerationOutcome outcome = GenerationOutcome.clean();
        if (useCache) {
            List<AnswerOption> cached = optionCache.get(question.id());
            if (cached != null && cached.size() >= optionsCount && hasCurrentQualityProfile(cached)) {
                return new OptionsFetchResult(cached, outcome);
            }
        }

        Lock lock = questionLocks.get(question.id());
        lock.lock();
        try {
            List<AnswerOption> existing = answerOptionRepository.findByQuestionId(question.id());
            if (existing.size() >= optionsCount && hasCurrentQualityProfile(existing)) {
                optionCache.put(question.id(), existing);
                return new OptionsFetchResult(existing, outcome);
            }

            GeneratedBuildResult buildResult = generateOptions(question);
            if (!buildResult.created().isEmpty()) {
                transactionTemplate.executeWithoutResult(status -> {
                    answerOptionRepository.deleteByQuestionId(question.id());
                    answerOptionRepository.insertAll(question.id(), buildResult.created());
                });
            }
            outcome = new GenerationOutcome(
                    buildResult.acceptedWithWarnings(),
                    buildResult.reducedOptions()
            );

            List<AnswerOption> options = answerOptionRepository.findByQuestionId(question.id());
            optionCache.put(question.id(), options);
            return new OptionsFetchResult(options, outcome);
        } finally {
            lock.unlock();
        }
    }

    public int requiredOptionsCount() {
        return optionsCount;
    }

    private GeneratedBuildResult generateOptions(Question question) {
        String codeSnippet = question.questionType() == QuestionType.CODE ? question.codeSnippet() : null;
        String questionText = question.questionText();
        String answerContext = domainDifficultyContextBuilder.sanitizeAnswerContext(question.answerMarkdown());
        String baseQualityContext = domainDifficultyContextBuilder.build(question, seniorRulePriorityOverrides);

        QualityCheckedGeneration qualityChecked = generateWithQualityRetry(
                question, questionText, answerContext, codeSnippet, baseQualityContext
        );
        GeneratedOptions aiGenerated = qualityChecked.generatedOptions();
        OptionSource source = optionGenerator.sourceId();

        MergedOptions merged = mergeCorrectAndWrong(aiGenerated);
        List<String> allTexts = new ArrayList<>();
        List<String> allExplanations = new ArrayList<>();
        allTexts.add(merged.correct());
        allExplanations.add(merged.correctExplanation());
        for (int i = 0; i < merged.wrong().size(); i++) {
            allTexts.add(merged.wrong().get(i));
            allExplanations.add(i < merged.wrongExplanations().size() ? merged.wrongExplanations().get(i) : null);
        }

        OptionDeduplicator.DedupResult dedupResult = deduplicator.deduplicateAndFill(allTexts, allExplanations, question);
        DedupSelection dedupSelection = pickDedupResult(question, dedupResult, allTexts, allExplanations);
        List<String> cleaned = new ArrayList<>();
        for (String opt : dedupSelection.dedupResult().texts()) {
            cleaned.add(opt == null ? null : opt.trim());
        }
        return new GeneratedBuildResult(
                shuffleAndBuildCreates(cleaned, dedupSelection.dedupResult().explanations(), source),
                qualityChecked.acceptedWithWarnings(),
                dedupSelection.reducedOptions()
        );
    }

    private record QualityCheckedGeneration(GeneratedOptions generatedOptions, boolean acceptedWithWarnings) {}

    private QualityCheckedGeneration generateWithQualityRetry(
            Question question,
            String questionText,
            String answerContext,
            String codeSnippet,
            String baseQualityContext
    ) {
        int maxAttempts = Math.max(1, optionQualityRetryAttempts + 1);
        long startedAt = System.currentTimeMillis();
        String qualityContext = baseQualityContext;
        OptionQualityValidator.ValidationReport lastReport = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            GeneratedOptions generated = requestOptions(questionText, answerContext, codeSnippet, qualityContext)
                    .orElseThrow(() -> new AiGenerationException(
                            "AI не вернул валидные варианты ответа для вопроса id=" + question.id()));

            OptionQualityValidator.ValidationReport report = optionQualityValidator.validateQualityReportWithContext(
                    generated,
                    questionText,
                    answerContext,
                    question.topic()
            );
            lastReport = report;
            int qualityScore = Math.max(0, 100 - report.penaltyScore());

            if (report.isClean()) {
                log.info("options_quality_passed questionId={} attempt={} score={} issues=0",
                        question.id(), attempt, qualityScore);
                return new QualityCheckedGeneration(generated, false);
            }

            boolean critical = report.hasCritical();
            boolean hardBlock = optionQualityValidator.hasHardBlockIssues(report);
            log.warn("options_quality_issues questionId={} attempt={} score={} critical={} hardBlock={} issues={}",
                    question.id(), attempt, qualityScore, critical, hardBlock, String.join("; ", report.messages()));

            if (!critical && !hardBlock) {
                return new QualityCheckedGeneration(generated, true);
            }

            boolean hasNextAttempt = attempt < maxAttempts;
            long elapsed = System.currentTimeMillis() - startedAt;
            if (!hasNextAttempt || elapsed >= optionQualityRetryMaxElapsedMs) {
                if (hardBlock) {
                    throw new AiGenerationException("AI вернул quality hard-block варианты для вопроса id=" + question.id());
                }
                return new QualityCheckedGeneration(generated, true);
            }

            qualityContext = appendQualityFixContext(baseQualityContext, report, attempt);
            sleepBackoffIfNeeded(startedAt);
        }

        if (lastReport != null && optionQualityValidator.hasHardBlockIssues(lastReport)) {
            throw new AiGenerationException("AI вернул quality hard-block варианты для вопроса id=" + question.id());
        }
        throw new AiGenerationException("AI не смог сгенерировать качественные варианты для вопроса id=" + question.id());
    }

    private String appendQualityFixContext(
            String baseQualityContext,
            OptionQualityValidator.ValidationReport report,
            int attempt
    ) {
        StringBuilder sb = new StringBuilder();
        if (baseQualityContext != null && !baseQualityContext.isBlank()) {
            sb.append(baseQualityContext).append('\n');
        }
        sb.append("QUALITY_RETRY_ATTEMPT=").append(attempt).append('\n');
        sb.append("QUALITY_FIX_REQUIRED:\n");
        int maxHints = Math.min(5, report.messages().size());
        for (int i = 0; i < maxHints; i++) {
            sb.append("- ").append(report.messages().get(i)).append('\n');
        }
        return sb.toString();
    }

    private void sleepBackoffIfNeeded(long startedAtMs) {
        if (optionQualityRetryBackoffMs <= 0) {
            return;
        }
        long elapsed = System.currentTimeMillis() - startedAtMs;
        long remaining = optionQualityRetryMaxElapsedMs - elapsed;
        if (remaining <= 0) {
            return;
        }
        long sleepMs = Math.min(optionQualityRetryBackoffMs, remaining);
        try {
            Thread.sleep(sleepMs);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    private DedupSelection pickDedupResult(
            Question question,
            OptionDeduplicator.DedupResult dedupResult,
            List<String> allTexts,
            List<String> allExplanations
    ) {
        if (dedupResult.texts().size() >= optionsCount) {
            return new DedupSelection(dedupResult, false);
        }

        List<String> fallbackTexts = new ArrayList<>(dedupResult.texts());
        List<String> fallbackExplanations = new ArrayList<>(dedupResult.explanations());
        Set<String> seen = new LinkedHashSet<>();
        for (String text : fallbackTexts) {
            if (text != null && !text.isBlank()) {
                seen.add(text.trim());
            }
        }
        for (int i = 0; i < allTexts.size() && fallbackTexts.size() < optionsCount; i++) {
            String candidate = allTexts.get(i);
            if (candidate == null || candidate.isBlank()) {
                continue;
            }
            String trimmed = candidate.trim();
            if (!seen.isEmpty() && deduplicator.isDuplicate(trimmed, seen)) {
                continue;
            }
            seen.add(trimmed);
            fallbackTexts.add(candidate);
            fallbackExplanations.add(i < allExplanations.size() ? allExplanations.get(i) : null);
        }

        int minAcceptedSize = Math.min(optionsCount, minAcceptedOptions);
        if (fallbackTexts.size() < minAcceptedSize) {
            throw new AiGenerationException("AI вернул недостаточно уникальных вариантов для вопроса id=" + question.id());
        }
        if (fallbackTexts.size() < optionsCount) {
            log.info("Question {} accepted with reduced options: requested={}, accepted={}",
                    question.id(), optionsCount, fallbackTexts.size());
        }
        return new DedupSelection(
                new OptionDeduplicator.DedupResult(fallbackTexts, fallbackExplanations),
                fallbackTexts.size() < optionsCount
        );
    }

    private MergedOptions mergeCorrectAndWrong(GeneratedOptions generated) {
        String correct = generated.correct();
        String correctExplanation = generated.correctExplanation();
        List<String> wrong = generated.wrong();
        List<String> wrongExplanations = generated.wrongExplanations() != null
                ? generated.wrongExplanations()
                : Collections.emptyList();
        return new MergedOptions(correct, correctExplanation, wrong, wrongExplanations);
    }

    private List<AnswerOptionCreate> shuffleAndBuildCreates(List<String> cleaned, List<String> explanations, OptionSource source) {
        final int correctIndexBeforeShuffle = 0;
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < cleaned.size(); i++) {
            indices.add(i);
        }
        Collections.shuffle(indices);
        List<AnswerOptionCreate> results = new ArrayList<>();
        for (int displayOrder = 0; displayOrder < indices.size(); displayOrder++) {
            int originalIdx = indices.get(displayOrder);
            boolean isCorrectOption = (originalIdx == correctIndexBeforeShuffle);
            String explanation = originalIdx < explanations.size() ? explanations.get(originalIdx) : null;
            results.add(new AnswerOptionCreate(
                    cleaned.get(originalIdx),
                    isCorrectOption,
                    displayOrder,
                    source.name(),
                    explanation,
                    CURRENT_OPTION_PROMPT_VERSION,
                    CURRENT_OPTION_QUALITY_PROFILE_VERSION
            ));
        }
        return results;
    }

    private static boolean hasCurrentQualityProfile(List<AnswerOption> options) {
        for (AnswerOption option : options) {
            if (option.promptVersion() != CURRENT_OPTION_PROMPT_VERSION
                    || option.qualityProfileVersion() != CURRENT_OPTION_QUALITY_PROFILE_VERSION) {
                return false;
            }
        }
        return true;
    }

    private Optional<GeneratedOptions> requestOptions(
            String questionText,
            String answerMarkdown,
            String codeSnippet,
            String qualityContext
    ) {
        if (qualityContext == null || qualityContext.isBlank()) {
            return optionGenerator.generateOptions(questionText, answerMarkdown, codeSnippet);
        }
        return optionGenerator.generateOptions(questionText, answerMarkdown, codeSnippet, qualityContext);
    }

}
