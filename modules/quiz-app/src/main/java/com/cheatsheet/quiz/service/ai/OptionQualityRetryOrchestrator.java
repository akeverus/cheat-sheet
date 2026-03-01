package com.cheatsheet.quiz.service.ai;

import com.cheatsheet.quiz.config.AppProperties;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.service.ai.dto.GeneratedOptions;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
final class OptionQualityRetryOrchestrator {

    private final OptionGenerator optionGenerator;
    private final OptionQualityValidator optionQualityValidator;
    private final int optionQualityRetryAttempts;
    private final long optionQualityRetryBackoffMs;
    private final long optionQualityRetryMaxElapsedMs;

    OptionQualityRetryOrchestrator(
            OptionGenerator optionGenerator,
            OptionQualityValidator optionQualityValidator,
            AppProperties appProperties
    ) {
        this.optionGenerator = optionGenerator;
        this.optionQualityValidator = optionQualityValidator;
        this.optionQualityRetryAttempts = appProperties.getInterview().getOptionQualityRetryAttempts();
        this.optionQualityRetryBackoffMs = appProperties.getInterview().getOptionQualityRetryBackoffMs();
        this.optionQualityRetryMaxElapsedMs = appProperties.getInterview().getOptionQualityRetryMaxElapsedMs();
    }

    QualityCheckedGeneration generateWithQualityRetry(
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

    private static String appendQualityFixContext(
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

    record QualityCheckedGeneration(GeneratedOptions generatedOptions, boolean acceptedWithWarnings) {
    }
}
