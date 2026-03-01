package com.cheatsheet.quiz.service.ai;

import com.cheatsheet.quiz.config.AppProperties;
import com.cheatsheet.quiz.service.ai.dto.GeneratedOptions;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Валидация качества сгенерированных AI-вариантов ответа.
 *
 * <p>Проверяет длину вариантов, наличие объяснений, отсутствие дубликатов,
 * тематическое сходство и единообразие структуры.</p>
 *
 * @see OptionGenerationService
 */
@Component
public class OptionQualityValidator {
    public enum Severity {
        WARNING,
        CRITICAL
    }

    public enum IssueCode {
        MISSING_EXPLANATION,
        EXPLANATION_TOO_LONG,
        DUPLICATE_OPTIONS,
        OFF_TOPIC_TO_CORRECT,
        STRUCTURE_NON_UNIFORM,
        BOILERPLATE_PREFIX,
        VERDICT_LEAKAGE,
        LECTURE_STYLE_OPTION,
        TOO_MANY_REASONING_OPTIONS,
        VAGUE_OPTION_TEXT,
        CLICHE_FALLBACK_OPTION,
        PATH_LIKE_OPTION_TEXT,
        INTERVIEW_META_ADVICE_TEXT,
        MISSING_COMPARISON_CRITERIA,
        TRUNCATED_OPTION_TEXT,
        QUESTION_CORE_MISMATCH,
        CORRECT_ANSWER_MISMATCH,
        ANSWER_TYPE_MISMATCH,
        AMBIGUOUS_CORRECT_ANSWER,
        WRONG_TOO_CLOSE_TO_CORRECT
    }

    public record ValidationIssue(IssueCode code, Severity severity, String message) {}

    public record ValidationReport(List<ValidationIssue> issues) {
        public ValidationReport {
            issues = issues == null ? List.of() : List.copyOf(issues);
        }

        public boolean hasCritical() {
            for (ValidationIssue issue : issues) {
                if (issue.severity() == Severity.CRITICAL) {
                    return true;
                }
            }
            return false;
        }

        public boolean isClean() {
            return issues.isEmpty();
        }

        public List<String> messages() {
            List<String> messages = new ArrayList<>();
            for (ValidationIssue issue : issues) {
                messages.add(issue.message());
            }
            return messages;
        }

        public int penaltyScore() {
            int total = 0;
            for (ValidationIssue issue : issues) {
                total += issue.severity() == Severity.CRITICAL ? 100 : 20;
                total += switch (issue.code()) {
                    case TRUNCATED_OPTION_TEXT, CORRECT_ANSWER_MISMATCH, ANSWER_TYPE_MISMATCH -> 80;
                    case PATH_LIKE_OPTION_TEXT, CLICHE_FALLBACK_OPTION, QUESTION_CORE_MISMATCH,
                            MISSING_COMPARISON_CRITERIA, DUPLICATE_OPTIONS, WRONG_TOO_CLOSE_TO_CORRECT -> 60;
                    case VAGUE_OPTION_TEXT, OFF_TOPIC_TO_CORRECT, LECTURE_STYLE_OPTION -> 40;
                    default -> 10;
                };
            }
            return total;
        }
    }

    private static final double MIN_TOPIC_SIMILARITY = 0.25;
    private static final int DEFAULT_OPTIONS_COUNT = 4;
    private static final int MIN_BOILERPLATE_PREFIX_WORDS = 5;
    private static final int MAX_BOILERPLATE_PREFIX_WORDS = 8;
    private static final int MIN_BOILERPLATE_HITS = 4;
    private static final List<String> BOILERPLATE_STOP_PHRASES = List.of(
            "предоставляет несколько преимуществ",
            "имеет несколько преимуществ",
            "предлагает несколько преимуществ",
            "используется для",
            "является инструментом"
    );
    private static final List<String> BOILERPLATE_PREFIX_WHITELIST = List.of(
            "код выведет",
            "будет выброшено",
            "будет возвращено",
            "результат выполнения"
    );
    private static final List<String> VAGUE_PLACEHOLDER_PHRASES = List.of(
            "альтернативный подход",
            "другой механизм",
            "иной вариант",
            "другое решение",
            "лучше работает",
            "эффективнее",
            "оптимизирует",
            "ускоряет"
    );
    private static final List<String> CONCRETE_SIGNAL_MARKERS = List.of(
            "o(", "big-o", "api", "ttl", "cache", "fallback", "miss", "invalidate",
            "null", "exception", "ошибк", "исключени",
            "сложност", "врем", "памят", "latency", "throughput", "ms", "сек", "итерац",
            "@transactional", "@bean", "@repository", "@service", "spring", "autoconfiguration",
            "actuator", "hibernate", "jpa", "join", "index", "explain", "query plan"
    );
    private static final List<String> INTERVIEW_META_ADVICE_MARKERS = List.of(
            "практическая ценность ответа обычно повышается",
            "практический акцент в таких вопросах",
            "на интервью ожидают, что вы назовете",
            "на интервью ожидают, что вы назовёте",
            "в интервью полезно коротко обозначить",
            "этот выбор почти всегда подтверждают измерениями"
    );
    private static final List<String> CORRECT_HEDGING_MARKERS = List.of(
            "обычно", "иногда", "может", "как правило", "часто", "в ряде случаев",
            "usually", "sometimes", "maybe", "often", "can be", "in some cases"
    );
    private static final Pattern PATH_LIKE_PATTERN =
            Pattern.compile("(?i)\\b[a-z0-9._-]{2,}/[a-z0-9._-]{2,}/[a-z0-9._/-]{2,}\\b");
    private static final List<String> PATH_LIKE_MARKERS = List.of(
            ".md", "-interview", "src/", "modules/", "cheatsheets/"
    );
    private static final String CRITICAL_TYPE_MISMATCH_PREFIX = "Формат ответов не соответствует типу вопроса";
    private static final String WARNING_TYPE_MISMATCH_PREFIX = "Часть вариантов слабо соответствует ожидаемому формату";

    private final OptionDeduplicator deduplicator;
    private final int maxExplanationLength;
    private final List<String> verdictLeakageMarkers;
    private final List<String> comparisonIntentMarkers;
    private final List<String> codeResultIntentMarkers;
    private final List<String> mechanismIntentMarkers;
    private final List<String> definitionIntentMarkers;

    public OptionQualityValidator(OptionDeduplicator deduplicator, AppProperties appProperties) {
        this.deduplicator = deduplicator;
        this.maxExplanationLength = appProperties.getInterview().getOptionExplanationMaxLength();
        this.verdictLeakageMarkers = normalizeMarkers(appProperties.getInterview().getOptionVerdictLeakageMarkers());
        this.comparisonIntentMarkers = normalizeMarkers(appProperties.getInterview().getOptionComparisonIntentMarkers());
        this.codeResultIntentMarkers = normalizeMarkers(appProperties.getInterview().getOptionCodeResultIntentMarkers());
        this.mechanismIntentMarkers = normalizeMarkers(appProperties.getInterview().getOptionMechanismIntentMarkers());
        this.definitionIntentMarkers = normalizeMarkers(appProperties.getInterview().getOptionDefinitionIntentMarkers());
    }

    /**
     * Валидирует качество сгенерированных вариантов.
     *
     * @return список проблем (пустой если всё ОК)
     */
    public List<String> validateQuality(GeneratedOptions opts) {
        return validateQualityReport(opts).messages();
    }

    /**
     * Валидирует качество с учетом текста вопроса для отбраковки нерелевантных distractor-ов.
     */
    public List<String> validateQualityWithQuestionContext(GeneratedOptions opts, String questionText) {
        return validateQualityReportWithQuestionContext(opts, questionText).messages();
    }

    /**
     * Валидирует качество с учетом вопроса и эталонного ответа.
     */
    public List<String> validateQualityWithContext(GeneratedOptions opts, String questionText, String expectedAnswerText) {
        return validateQualityReportWithContext(opts, questionText, expectedAnswerText).messages();
    }

    public ValidationReport validateQualityReport(GeneratedOptions opts) {
        List<String> allTexts = collectAllTexts(opts);
        List<ValidationIssue> issues = new ArrayList<>();
        issues.addAll(validateExplanations(opts));
        issues.addAll(validateNoDuplicates(allTexts));
        issues.addAll(validateTopicSimilarity(opts != null ? opts.correct() : null, opts != null ? opts.wrong() : null));
        issues.addAll(validateStructureUniformity(allTexts));
        issues.addAll(validateNoBoilerplatePrefix(allTexts));
        issues.addAll(validateNoVerdictLeakage(allTexts));
        issues.addAll(validateNoExplanationLikeOptionText(allTexts));
        issues.addAll(validateNoVagueOptionText(allTexts));
        issues.addAll(validateNoClicheFallbackText(allTexts));
        issues.addAll(validateNoPathLikeOptionText(allTexts));
        issues.addAll(validateNoInterviewMetaAdviceText(allTexts));
        issues.addAll(validateNoTruncatedOptionText(allTexts));
        issues.addAll(validateCorrectAnswerPrecision(opts));
        issues.addAll(validateWrongDistinctFromCorrect(opts));
        return new ValidationReport(issues);
    }

    public ValidationReport validateQualityReportWithQuestionContext(GeneratedOptions opts, String questionText) {
        return validateQualityReportWithQuestionContext(opts, questionText, null);
    }

    public ValidationReport validateQualityReportWithQuestionContext(
            GeneratedOptions opts, String questionText, String topic
    ) {
        ValidationReport base = validateQualityReport(opts);
        List<ValidationIssue> issues = new ArrayList<>(base.issues());
        issues.addAll(validateQuestionCoreRelevance(questionText, null, opts, topic));
        issues.addAll(validateAnswerTypeConsistency(questionText, opts));
        issues.addAll(validateComparisonCriteria(questionText, opts));
        return new ValidationReport(issues);
    }

    public ValidationReport validateQualityReportWithContext(
            GeneratedOptions opts, String questionText, String expectedAnswerText
    ) {
        return validateQualityReportWithContext(opts, questionText, expectedAnswerText, null);
    }

    public ValidationReport validateQualityReportWithContext(
            GeneratedOptions opts, String questionText, String expectedAnswerText, String topic
    ) {
        ValidationReport base = validateQualityReport(opts);
        List<ValidationIssue> issues = new ArrayList<>(base.issues());
        issues.addAll(validateQuestionCoreRelevance(questionText, expectedAnswerText, opts, topic));
        issues.addAll(validateAnswerTypeConsistency(questionText, opts));
        issues.addAll(validateComparisonCriteria(questionText, opts));
        issues.addAll(validateCorrectAnswerAlignment(expectedAnswerText, opts));
        return new ValidationReport(issues);
    }

    public boolean hasCriticalIssues(ValidationReport report) {
        return report != null && report.hasCritical();
    }

    /**
     * Критические ошибки бывают двух типов:
     * - retryable (можно попробовать перегенерировать),
     * - hard-block (нельзя показывать пользователю даже во fallback-режиме).
     */
    public boolean hasHardBlockIssues(ValidationReport report) {
        if (report == null || report.issues().isEmpty()) {
            return false;
        }
        for (ValidationIssue issue : report.issues()) {
            if (issue.severity() != Severity.CRITICAL) {
                if (issue.code() == IssueCode.PATH_LIKE_OPTION_TEXT) {
                    return true;
                }
                continue;
            }
            if (issue.code() == IssueCode.VERDICT_LEAKAGE
                    || issue.code() == IssueCode.TRUNCATED_OPTION_TEXT
                    || issue.code() == IssueCode.CLICHE_FALLBACK_OPTION
                    || issue.code() == IssueCode.WRONG_TOO_CLOSE_TO_CORRECT
                    || issue.code() == IssueCode.AMBIGUOUS_CORRECT_ANSWER) {
                return true;
            }
        }
        return false;
    }

    private static List<String> collectAllTexts(GeneratedOptions opts) {
        List<String> allTexts = new ArrayList<>();
        if (opts == null) {
            return allTexts;
        }
        allTexts.add(opts.correct());
        if (opts.wrong() != null) {
            allTexts.addAll(opts.wrong());
        }
        return allTexts;
    }

    private List<ValidationIssue> validateExplanations(GeneratedOptions opts) {
        List<ValidationIssue> issues = new ArrayList<>();
        if (opts == null) {
            return issues;
        }
        int missingExplanations = 0;
        int tooLongExplanations = 0;
        if (opts.correctExplanation() == null || opts.correctExplanation().isBlank()) {
            missingExplanations++;
        } else if (opts.correctExplanation().length() > maxExplanationLength) {
            tooLongExplanations++;
        }
        if (opts.wrongExplanations() != null) {
            for (String expl : opts.wrongExplanations()) {
                if (expl == null || expl.isBlank()) {
                    missingExplanations++;
                } else if (expl.length() > maxExplanationLength) {
                    tooLongExplanations++;
                }
            }
        } else {
            missingExplanations += (opts.wrong() != null ? opts.wrong().size() : 0);
        }
        if (missingExplanations > 2) {
            issues.add(new ValidationIssue(
                    IssueCode.MISSING_EXPLANATION,
                    Severity.WARNING,
                    "Отсутствуют объяснения у " + missingExplanations + " вариантов"
            ));
        }
        if (tooLongExplanations > 1) {
            issues.add(new ValidationIssue(
                    IssueCode.EXPLANATION_TOO_LONG,
                    Severity.WARNING,
                    "Объяснения слишком длинные (" + tooLongExplanations + " шт. > " + maxExplanationLength + " символов)"
            ));
        }
        return issues;
    }

    private List<ValidationIssue> validateNoDuplicates(List<String> allTexts) {
        List<ValidationIssue> issues = new ArrayList<>();
        Set<String> seen = new LinkedHashSet<>();
        int dupCount = 0;
        for (String text : allTexts) {
            if (text == null || text.isBlank()) continue;
            String trimmed = text.trim();
            if (!seen.isEmpty() && deduplicator.isDuplicate(trimmed, seen)) {
                dupCount++;
            }
            seen.add(trimmed);
        }
        if (dupCount > 0) {
            issues.add(new ValidationIssue(
                    IssueCode.DUPLICATE_OPTIONS,
                    Severity.CRITICAL,
                    "Обнаружены дубликаты: " + dupCount + " вариантов слишком похожи"
            ));
        }
        return issues;
    }

    /**
     * Проверяет, что неверные варианты тематически связаны с правильным ответом.
     * Jaccard similarity по токенам должна быть не ниже порога.
     */
    private List<ValidationIssue> validateTopicSimilarity(String correct, List<String> wrong) {
        List<ValidationIssue> issues = new ArrayList<>();
        if (correct == null || correct.isBlank() || wrong == null || wrong.isEmpty()) {
            return issues;
        }
        Set<String> correctTokens = OptionSemanticSimilaritySupport.tokenizeForSemanticSimilarity(correct);
        if (correctTokens.isEmpty()) {
            return issues;
        }
        int offTopicCount = 0;
        for (String w : wrong) {
            if (w == null || w.isBlank()) continue;
            Set<String> wrongTokens = OptionSemanticSimilaritySupport.tokenizeForSemanticSimilarity(w);
            if (wrongTokens.isEmpty()) continue;
            OptionSemanticSimilaritySupport.SimilarityMetrics metrics =
                    OptionSemanticSimilaritySupport.similarityMetrics(correctTokens, wrongTokens);
            if (metrics.jaccard() < MIN_TOPIC_SIMILARITY) {
                offTopicCount++;
            }
        }
        if (offTopicCount >= 1) {
            issues.add(new ValidationIssue(
                    IssueCode.OFF_TOPIC_TO_CORRECT,
                    Severity.WARNING,
                    "Неверные варианты не по теме: " + offTopicCount
                            + " из " + wrong.size() + " вариантов не связаны с правильным ответом (Jaccard < "
                            + MIN_TOPIC_SIMILARITY + ")"
            ));
        }
        return issues;
    }

    /**
     * Проверяет единообразие начальной структуры вариантов.
     * Если большинство вариантов начинаются с одного и того же первого слова,
     * а один-два — нет, это сигнал о нарушении зеркальной структуры.
     */
    private List<ValidationIssue> validateStructureUniformity(List<String> allTexts) {
        List<ValidationIssue> issues = new ArrayList<>();
        Map<String, Integer> firstWordCounts = new HashMap<>();
        int total = 0;
        for (String text : allTexts) {
            if (text == null || text.isBlank()) continue;
            String stripped = text.replaceAll("^[^a-zA-Zа-яА-Я0-9]+", "");
            String[] words = stripped.split("\\s+");
            if (words.length > 0 && !words[0].isEmpty()) {
                String firstWord = words[0].toLowerCase();
                firstWordCounts.merge(firstWord, 1, Integer::sum);
                total++;
            }
        }
        if (total < 4) return issues;
        int maxCount = firstWordCounts.values().stream().mapToInt(Integer::intValue).max().orElse(0);
        int outliers = total - maxCount;
        if (maxCount >= 4 && outliers >= 2) {
            issues.add(new ValidationIssue(
                    IssueCode.STRUCTURE_NON_UNIFORM,
                    Severity.WARNING,
                    "Нарушена единообразность структуры: " + outliers
                            + " из " + total + " вариантов начинаются иначе, чем остальные"
            ));
        }
        return issues;
    }

    /**
     * Проверяет, что в тексте вариантов нет служебных маркеров объяснений.
     * Модель иногда "склеивает" вариант и explanation в одном поле (например, "Неверно: ..."),
     * что делает ответ очевидным для пользователя.
     */
    private List<ValidationIssue> validateNoVerdictLeakage(List<String> allTexts) {
        List<ValidationIssue> issues = new ArrayList<>();
        int leaked = 0;
        for (String text : allTexts) {
            if (text == null || text.isBlank()) continue;
            String lower = text.toLowerCase(Locale.ROOT);
            if (containsAny(lower, verdictLeakageMarkers)) {
                leaked++;
            }
        }
        if (leaked > 0) {
            issues.add(new ValidationIssue(
                    IssueCode.VERDICT_LEAKAGE,
                    Severity.CRITICAL,
                    "Текст вариантов содержит маркеры объяснений (Верно:/Неверно:/Ключевое отличие): " + leaked + " шт."
            ));
        }
        return issues;
    }

    /**
     * Отлавливает варианты, в которые модель встроила длинный разбор вместо краткого ответа.
     * Такие тексты обычно содержат причинно-следственные связки и выглядят как explanation.
     */
    private List<ValidationIssue> validateNoExplanationLikeOptionText(List<String> allTexts) {
        List<ValidationIssue> issues = new ArrayList<>();
        int explanationLike = 0;
        int withBecause = 0;
        for (String text : allTexts) {
            if (text == null || text.isBlank()) continue;
            String lower = text.toLowerCase(Locale.ROOT);
            int sentenceCount = text.split("[.!?]+").length;
            boolean hasReasoning = lower.contains("потому что")
                    || lower.contains("поэтому")
                    || lower.contains("таким образом")
                    || lower.contains("в данном случае")
                    || lower.contains("это важно");
            if (hasReasoning) {
                withBecause++;
            }
            boolean hasListStyle = text.contains("\n")
                    || text.contains("1.")
                    || text.contains("2.")
                    || text.contains("3.");
            if (sentenceCount >= 3
                    || (sentenceCount >= 2 && hasReasoning)
                    || hasListStyle) {
                explanationLike++;
            }
        }
        if (explanationLike > 0) {
            issues.add(new ValidationIssue(
                    IssueCode.LECTURE_STYLE_OPTION,
                    Severity.CRITICAL,
                    "Варианты слишком длинные или лекционные: " + explanationLike
                            + " шт. выглядят как разбор вместо короткой формулировки"
            ));
        }
        if (withBecause >= 4) {
            issues.add(new ValidationIssue(
                    IssueCode.TOO_MANY_REASONING_OPTIONS,
                    Severity.WARNING,
                    "Слишком много вариантов содержат причинно-следственный разбор прямо в option text: " + withBecause + " шт."
            ));
        }
        return issues;
    }

    private List<ValidationIssue> validateNoVagueOptionText(List<String> allTexts) {
        List<ValidationIssue> issues = new ArrayList<>();
        int vagueCount = 0;
        for (String text : allTexts) {
            if (text == null || text.isBlank()) {
                continue;
            }
            String lower = text.toLowerCase(Locale.ROOT);
            for (String phrase : VAGUE_PLACEHOLDER_PHRASES) {
                if (lower.contains(phrase) && !hasConcreteSignal(lower)) {
                    vagueCount++;
                    break;
                }
            }
        }
        if (vagueCount > 0) {
            issues.add(new ValidationIssue(
                    IssueCode.VAGUE_OPTION_TEXT,
                    Severity.WARNING,
                    "Варианты содержат расплывчатые заглушки без проверяемого факта: " + vagueCount + " шт."
            ));
        }
        return issues;
    }

    private static boolean hasConcreteSignal(String lowerText) {
        if (lowerText == null || lowerText.isBlank()) {
            return false;
        }
        for (String marker : CONCRETE_SIGNAL_MARKERS) {
            if (lowerText.contains(marker)) {
                return true;
            }
        }
        return lowerText.contains("->")
                || lowerText.contains("=")
                || lowerText.matches(".*\\d+.*");
    }

    private List<ValidationIssue> validateNoTruncatedOptionText(List<String> allTexts) {
        List<ValidationIssue> issues = new ArrayList<>();
        int truncated = 0;
        for (String text : allTexts) {
            if (text == null || text.isBlank()) {
                continue;
            }
            String trimmed = text.trim();
            if (trimmed.endsWith("...") || trimmed.endsWith("…")) {
                truncated++;
            }
        }
        if (truncated > 0) {
            issues.add(new ValidationIssue(
                    IssueCode.TRUNCATED_OPTION_TEXT,
                    Severity.CRITICAL,
                    "Обнаружены обрезанные варианты с многоточием: " + truncated + " шт."
            ));
        }
        return issues;
    }

    private List<ValidationIssue> validateNoInterviewMetaAdviceText(List<String> allTexts) {
        List<ValidationIssue> issues = new ArrayList<>();
        int metaAdviceCount = 0;
        for (String text : allTexts) {
            if (text == null || text.isBlank()) {
                continue;
            }
            String lower = text.toLowerCase(Locale.ROOT);
            if (containsAny(lower, INTERVIEW_META_ADVICE_MARKERS)) {
                metaAdviceCount++;
            }
        }
        if (metaAdviceCount > 0) {
            issues.add(new ValidationIssue(
                    IssueCode.INTERVIEW_META_ADVICE_TEXT,
                    Severity.CRITICAL,
                    "Варианты содержат мета-советы для интервью вместо предметного ответа: " + metaAdviceCount + " шт."
            ));
        }
        return issues;
    }

    private List<ValidationIssue> validateNoPathLikeOptionText(List<String> allTexts) {
        List<ValidationIssue> issues = new ArrayList<>();
        int pathLike = 0;
        for (String text : allTexts) {
            if (text == null || text.isBlank()) {
                continue;
            }
            String lower = text.toLowerCase(Locale.ROOT);
            boolean hasMarker = false;
            for (String marker : PATH_LIKE_MARKERS) {
                if (lower.contains(marker)) {
                    hasMarker = true;
                    break;
                }
            }
            if (hasMarker || PATH_LIKE_PATTERN.matcher(lower).find()) {
                pathLike++;
            }
        }
        if (pathLike > 0) {
            issues.add(new ValidationIssue(
                    IssueCode.PATH_LIKE_OPTION_TEXT,
                    Severity.WARNING,
                    "Варианты содержат технические path/slug-фрагменты: " + pathLike + " шт."
            ));
        }
        return issues;
    }

    private List<ValidationIssue> validateNoClicheFallbackText(List<String> allTexts) {
        List<ValidationIssue> issues = new ArrayList<>();
        int clicheCount = 0;
        for (String text : allTexts) {
            if (text == null || text.isBlank()) {
                continue;
            }
            String normalized = text.trim().toLowerCase(Locale.ROOT);
            if (isClicheFallbackText(normalized)) {
                clicheCount++;
            }
        }
        if (clicheCount > 0) {
            issues.add(new ValidationIssue(
                    IssueCode.CLICHE_FALLBACK_OPTION,
                    Severity.CRITICAL,
                    "Варианты содержат клишированные fallback-шаблоны без предметной конкретики: " + clicheCount + " шт."
            ));
        }
        return issues;
    }

    private static boolean isClicheFallbackText(String text) {
        if (text.startsWith("описывают ") && text.contains("как общую идею")) {
            return true;
        }
        if (text.startsWith("дают смежное определение")) {
            return true;
        }
        if (text.startsWith("подменяют смысл")) {
            return true;
        }
        return text.startsWith("для ")
                && (text.contains(" используют ") || text.contains(" применяют ") || text.contains(" выполняют "));
    }

    /**
     * Отлавливает варианты с одинаковым шаблонным началом.
     * Такие варианты выглядят как "копипаст" и превращают задачу в угадайку по середине фразы.
     */
    private List<ValidationIssue> validateNoBoilerplatePrefix(List<String> allTexts) {
        List<ValidationIssue> issues = new ArrayList<>();
        List<String> normalized = new ArrayList<>();
        for (String text : allTexts) {
            if (text == null || text.isBlank()) {
                continue;
            }
            normalized.add(text.toLowerCase(Locale.ROOT).replaceAll("\\s+", " ").trim());
        }
        if (normalized.size() < DEFAULT_OPTIONS_COUNT - 1) {
            return issues;
        }

        Map<String, Integer> suspiciousPrefixCounts = new HashMap<>();
        for (String text : normalized) {
            if (!containsStopPhrase(text)) {
                continue;
            }
            String prefix = leadingWords(text, MIN_BOILERPLATE_PREFIX_WORDS, MAX_BOILERPLATE_PREFIX_WORDS);
            if (prefix == null || isWhitelistedPrefix(prefix)) {
                continue;
            }
            suspiciousPrefixCounts.merge(prefix, 1, Integer::sum);
        }
        int max = suspiciousPrefixCounts.values().stream().mapToInt(Integer::intValue).max().orElse(0);
        if (max >= MIN_BOILERPLATE_HITS) {
            issues.add(new ValidationIssue(
                    IssueCode.BOILERPLATE_PREFIX,
                    Severity.WARNING,
                    "Варианты имеют повторяющийся шаблонный префикс у " + max + " пунктов"
            ));
        }
        return issues;
    }

    private static boolean containsStopPhrase(String text) {
        for (String phrase : BOILERPLATE_STOP_PHRASES) {
            if (text.contains(phrase)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isWhitelistedPrefix(String prefix) {
        for (String allowed : BOILERPLATE_PREFIX_WHITELIST) {
            if (prefix.startsWith(allowed)) {
                return true;
            }
        }
        return false;
    }

    private static String leadingWords(String text, int minWords, int maxWords) {
        String[] words = text.split(" ");
        if (words.length < minWords) {
            return null;
        }
        if (maxWords < minWords) {
            return null;
        }
        // Для устойчивого детектора используем минимальный стабильный префикс,
        // иначе длинный префикс "разъезжается" и не ловит шаблон.
        int limit = Math.min(words.length, minWords);
        return String.join(" ", Arrays.copyOfRange(words, 0, limit));
    }

    private List<ValidationIssue> validateQuestionCoreRelevance(
            String questionText, String expectedAnswerText, GeneratedOptions opts, String topic
    ) {
        List<ValidationIssue> issues = new ArrayList<>();
        if (questionText == null || questionText.isBlank()) {
            return issues;
        }
        Set<String> questionTokens = new HashSet<>(OptionQuestionAnalysisSupport.tokenizeQuestion(questionText));
        if (expectedAnswerText != null && !expectedAnswerText.isBlank()) {
            questionTokens.addAll(OptionQuestionAnalysisSupport.tokenizeQuestion(expectedAnswerText));
        }
        if (questionTokens.size() < 2 || opts == null || opts.wrong() == null || opts.wrong().isEmpty()) {
            return issues;
        }

        int weaklyRelated = 0;
        for (String wrong : opts.wrong()) {
            if (wrong == null || wrong.isBlank()) {
                continue;
            }
            Set<String> wrongTokens = OptionQuestionAnalysisSupport.tokenizeForQuestionRelevance(
                    wrong.toLowerCase(Locale.ROOT)
            );
            if (wrongTokens.isEmpty()) {
                weaklyRelated++;
                continue;
            }
            boolean intersects = false;
            for (String token : wrongTokens) {
                if (questionTokens.contains(token)) {
                    intersects = true;
                    break;
                }
            }
            if (!intersects) {
                weaklyRelated++;
            }
        }
        int mismatchThreshold = questionTokens.size() < 5 ? 2 : 1;
        if (OptionQuestionAnalysisSupport.hasTechnicalFocusTokens(questionTokens)) {
            // Для технических вопросов (JVM/GC/SQL/API и т.п.) допускаем 1 "слабый" distractor,
            // чтобы снизить ложные срабатывания при перефразировании.
            mismatchThreshold = Math.max(mismatchThreshold, 2);
        }
        if (weaklyRelated >= mismatchThreshold) {
            issues.add(new ValidationIssue(
                    IssueCode.QUESTION_CORE_MISMATCH,
                    Severity.WARNING,
                    "Варианты слабо связаны с вопросом: " + weaklyRelated
                            + " distractor-ов не содержат ключевую сущность вопроса"
            ));
        }
        return issues;
    }

    private List<ValidationIssue> validateCorrectAnswerAlignment(String expectedAnswerText, GeneratedOptions opts) {
        List<ValidationIssue> issues = new ArrayList<>();
        if (expectedAnswerText == null || expectedAnswerText.isBlank() || opts == null || opts.correct() == null) {
            return issues;
        }
        Set<String> expectedTokens = OptionQuestionAnalysisSupport.tokenizeQuestion(expectedAnswerText);
        Set<String> correctTokens = OptionQuestionAnalysisSupport.tokenizeForQuestionRelevance(
                opts.correct().toLowerCase(Locale.ROOT)
        );
        if (expectedTokens.size() < 2 || correctTokens.isEmpty()) {
            return issues;
        }
        int overlap = 0;
        for (String token : correctTokens) {
            if (expectedTokens.contains(token)) {
                overlap++;
            }
        }
        double coverage = (double) overlap / Math.max(expectedTokens.size(), 1);
        if (OptionQuestionAnalysisSupport.isWeakCorrectAlignment(expectedTokens.size(), overlap, coverage)) {
            issues.add(new ValidationIssue(
                    IssueCode.CORRECT_ANSWER_MISMATCH,
                    Severity.WARNING,
                    "Правильный вариант слабо связан с эталонным ответом: низкое покрытие ключевых сущностей"
            ));
        }
        return issues;
    }

    private List<ValidationIssue> validateAnswerTypeConsistency(String questionText, GeneratedOptions opts) {
        List<ValidationIssue> issues = new ArrayList<>();
        if (questionText == null || questionText.isBlank() || opts == null) {
            return issues;
        }
        OptionQuestionAnalysisSupport.QuestionIntent intent = OptionQuestionAnalysisSupport.detectQuestionIntent(
                questionText,
                comparisonIntentMarkers,
                codeResultIntentMarkers,
                mechanismIntentMarkers,
                definitionIntentMarkers
        );
        if (intent == OptionQuestionAnalysisSupport.QuestionIntent.UNKNOWN) {
            return issues;
        }
        List<String> all = new ArrayList<>();
        if (opts.correct() != null && !opts.correct().isBlank()) {
            all.add(opts.correct());
        }
        if (opts.wrong() != null) {
            for (String wrong : opts.wrong()) {
                if (wrong != null && !wrong.isBlank()) {
                    all.add(wrong);
                }
            }
        }
        if (all.isEmpty()) {
            return issues;
        }
        int mismatches = 0;
        for (String option : all) {
            if (!OptionQuestionAnalysisSupport.matchesIntent(option, intent)) {
                mismatches++;
            }
        }
        if (mismatches >= 1) {
            int criticalThreshold = switch (intent) {
                case CODE_RESULT -> 1;
                case COMPARISON, MECHANISM, DEFINITION -> 2;
                case UNKNOWN -> Integer.MAX_VALUE;
            };
            Severity severity = mismatches >= criticalThreshold ? Severity.CRITICAL : Severity.WARNING;
            String prefix = severity == Severity.CRITICAL ? CRITICAL_TYPE_MISMATCH_PREFIX : WARNING_TYPE_MISMATCH_PREFIX;
            issues.add(new ValidationIssue(
                    IssueCode.ANSWER_TYPE_MISMATCH,
                    severity,
                    prefix + ": " + mismatches
                            + " вариантов не соблюдают формат " + OptionQuestionAnalysisSupport.intentName(intent)
            ));
        }
        return issues;
    }

    private List<ValidationIssue> validateComparisonCriteria(String questionText, GeneratedOptions opts) {
        List<ValidationIssue> issues = new ArrayList<>();
        if (questionText == null || questionText.isBlank() || opts == null) {
            return issues;
        }
        OptionQuestionAnalysisSupport.QuestionIntent questionIntent = OptionQuestionAnalysisSupport.detectQuestionIntent(
                questionText,
                comparisonIntentMarkers,
                codeResultIntentMarkers,
                mechanismIntentMarkers,
                definitionIntentMarkers
        );
        if (questionIntent != OptionQuestionAnalysisSupport.QuestionIntent.COMPARISON) {
            return issues;
        }
        List<String> all = new ArrayList<>();
        if (opts.correct() != null && !opts.correct().isBlank()) {
            all.add(opts.correct());
        }
        if (opts.wrong() != null) {
            for (String wrong : opts.wrong()) {
                if (wrong != null && !wrong.isBlank()) {
                    all.add(wrong);
                }
            }
        }
        if (all.isEmpty()) {
            return issues;
        }
        int missingCriteria = 0;
        for (String option : all) {
            String lower = option.toLowerCase(Locale.ROOT);
            if (!containsAny(lower,
                    "сложност", "врем", "памят", "гарант", "худш", "лучш", "средн", "edge", "амортиз",
                    "latency", "throughput", "benchmark", "измер", "замер", "данн")) {
                missingCriteria++;
            }
        }
        if (missingCriteria > 0) {
            issues.add(new ValidationIssue(
                    IssueCode.MISSING_COMPARISON_CRITERIA,
                    Severity.WARNING,
                    "Для вопроса-сравнения не хватает явного критерия у " + missingCriteria + " вариантов"
            ));
        }
        return issues;
    }

    private static boolean containsAny(String text, String... needles) {
        for (String needle : needles) {
            if (text.contains(needle)) {
                return true;
            }
        }
        return false;
    }

    private static boolean containsAny(String text, List<String> needles) {
        for (String needle : needles) {
            if (text.contains(needle)) {
                return true;
            }
        }
        return false;
    }

    private List<ValidationIssue> validateCorrectAnswerPrecision(GeneratedOptions opts) {
        List<ValidationIssue> issues = new ArrayList<>();
        if (opts == null || opts.correct() == null || opts.correct().isBlank()) {
            return issues;
        }
        String correct = opts.correct().toLowerCase(Locale.ROOT);
        if (!containsAny(correct, CORRECT_HEDGING_MARKERS)) {
            return issues;
        }
        if (containsAny(correct, CONCRETE_SIGNAL_MARKERS)) {
            return issues;
        }
        issues.add(new ValidationIssue(
                IssueCode.AMBIGUOUS_CORRECT_ANSWER,
                Severity.WARNING,
                "Correct содержит неуверенную формулировку (обычно/может/иногда) без проверяемого факта"
        ));
        return issues;
    }

    private List<ValidationIssue> validateWrongDistinctFromCorrect(GeneratedOptions opts) {
        List<ValidationIssue> issues = new ArrayList<>();
        if (opts == null || opts.correct() == null || opts.correct().isBlank() || opts.wrong() == null || opts.wrong().isEmpty()) {
            return issues;
        }
        Set<String> correctTokens = OptionQuestionAnalysisSupport.tokenizeForQuestionRelevance(
                opts.correct().toLowerCase(Locale.ROOT)
        );
        if (correctTokens.size() < 4) {
            return issues;
        }
        int tooCloseCount = 0;
        for (String wrong : opts.wrong()) {
            if (wrong == null || wrong.isBlank()) {
                continue;
            }
            Set<String> wrongTokens = OptionQuestionAnalysisSupport.tokenizeForQuestionRelevance(
                    wrong.toLowerCase(Locale.ROOT)
            );
            if (wrongTokens.size() < 4) {
                continue;
            }
            OptionSemanticSimilaritySupport.SimilarityMetrics metrics =
                    OptionSemanticSimilaritySupport.similarityMetrics(correctTokens, wrongTokens);
            if (metrics.intersection() == 0) {
                continue;
            }
            if (metrics.minCoverage() >= 0.75 || metrics.jaccard() >= 0.62) {
                tooCloseCount++;
            }
        }
        if (tooCloseCount > 0) {
            issues.add(new ValidationIssue(
                    IssueCode.WRONG_TOO_CLOSE_TO_CORRECT,
                    Severity.WARNING,
                    "Часть wrong-вариантов слишком близка к correct по формулировке: " + tooCloseCount
                            + " вариантов могут сделать правильный ответ неоднозначным"
            ));
        }
        return issues;
    }

    private static List<String> normalizeMarkers(List<String> markers) {
        if (markers == null || markers.isEmpty()) {
            return List.of();
        }
        List<String> normalized = new ArrayList<>();
        for (String marker : markers) {
            if (marker == null || marker.isBlank()) {
                continue;
            }
            normalized.add(marker.toLowerCase(Locale.ROOT).trim());
        }
        return normalized;
    }
}
