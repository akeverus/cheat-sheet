package com.cheatsheet.quiz.service.ai;

import com.cheatsheet.quiz.config.AppProperties;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Политика приёма сгенерированного вопроса.
 */
@Component
public class QuestionGenerationPolicy {

    private final int minQualityScore;
    private final int maxQuestionTextLength;
    private final int maxOptionsTotalTextLength;
    private final double nearDuplicateSimilarityThreshold;
    private final double distractorSimilarityThreshold;

    @Autowired
    public QuestionGenerationPolicy(AppProperties appProperties) {
        this(
                appProperties.getInterview().getQuestionMinQualityScore(),
                appProperties.getInterview().getQuestionMaxTextLength(),
                appProperties.getInterview().getQuestionMaxOptionsTotalTextLength(),
                appProperties.getInterview().getQuestionNearDuplicateSimilarityThreshold(),
                appProperties.getInterview().getQuestionDistractorSimilarityThreshold()
        );
    }

    public QuestionGenerationPolicy(int minQualityScore) {
        this(minQualityScore, 280, 520, 0.82, 0.78);
    }

    public QuestionGenerationPolicy(
            int minQualityScore,
            int maxQuestionTextLength,
            int maxOptionsTotalTextLength
    ) {
        this(minQualityScore, maxQuestionTextLength, maxOptionsTotalTextLength, 0.82, 0.78);
    }

    public QuestionGenerationPolicy(
            int minQualityScore,
            int maxQuestionTextLength,
            int maxOptionsTotalTextLength,
            double nearDuplicateSimilarityThreshold
    ) {
        this(minQualityScore, maxQuestionTextLength, maxOptionsTotalTextLength, nearDuplicateSimilarityThreshold, 0.78);
    }

    public QuestionGenerationPolicy(
            int minQualityScore,
            int maxQuestionTextLength,
            int maxOptionsTotalTextLength,
            double nearDuplicateSimilarityThreshold,
            double distractorSimilarityThreshold
    ) {
        this.minQualityScore = Math.max(0, Math.min(100, minQualityScore));
        this.maxQuestionTextLength = Math.max(40, maxQuestionTextLength);
        this.maxOptionsTotalTextLength = Math.max(80, maxOptionsTotalTextLength);
        this.nearDuplicateSimilarityThreshold = Math.max(0.6, Math.min(0.99, nearDuplicateSimilarityThreshold));
        this.distractorSimilarityThreshold = Math.max(0.5, Math.min(0.98, distractorSimilarityThreshold));
    }

    public int minQualityScore() {
        return minQualityScore;
    }

    public boolean isAccepted(int score, List<String> violations) {
        return (violations == null || violations.isEmpty()) && score >= minQualityScore;
    }

    public List<String> enrichViolations(Question candidate, List<String> baseViolations, Set<String> seenFingerprints) {
        List<String> violations = new ArrayList<>();
        if (baseViolations != null) {
            violations.addAll(baseViolations);
        }
        Set<String> safeSeenFingerprints = sanitizeSeenFingerprints(seenFingerprints);
        if (candidate == null) {
            violations.add("Question candidate is null");
            return violations;
        }

        String questionText = candidate.questionText() == null ? "" : candidate.questionText().trim();
        if (questionText.length() > maxQuestionTextLength) {
            violations.add("Question cognitive load is too high: question text is too long");
        }

        int optionsTotalLength = candidate.options() == null
                ? 0
                : candidate.options().stream()
                .map(QuestionOption::text)
                .map(text -> text == null ? "" : text.trim())
                .mapToInt(String::length)
                .sum();
        if (optionsTotalLength > maxOptionsTotalTextLength) {
            violations.add("Question cognitive load is too high: options are too verbose");
        }
        if (hasLowDistractorDiversity(candidate)) {
            violations.add("Question distractors are semantically too similar to each other");
        }

        if (!safeSeenFingerprints.isEmpty()) {
            String fingerprint = fingerprint(candidate);
            if (safeSeenFingerprints.contains(fingerprint)) {
                violations.add("Question candidate duplicates previous generation attempt");
            } else if (isNearDuplicate(candidate, safeSeenFingerprints, fingerprint)) {
                violations.add("Question candidate is semantically too close to previous generation attempt");
            }
        }
        return violations;
    }

    public String fingerprint(Question question) {
        return buildFingerprint(question);
    }

    public int maxQuestionTextLength() {
        return maxQuestionTextLength;
    }

    public int maxOptionsTotalTextLength() {
        return maxOptionsTotalTextLength;
    }

    public double nearDuplicateSimilarityThreshold() {
        return nearDuplicateSimilarityThreshold;
    }

    public double distractorSimilarityThreshold() {
        return distractorSimilarityThreshold;
    }

    private static String buildFingerprint(Question question) {
        String questionText = question.questionText() == null
                ? ""
                : question.questionText().trim().toLowerCase(Locale.ROOT);
        String optionsFingerprint = question.options() == null
                ? ""
                : question.options().stream()
                .map(option -> option == null || option.text() == null
                        ? ""
                        : option.text().trim().toLowerCase(Locale.ROOT))
                .sorted(Comparator.naturalOrder())
                .reduce((left, right) -> left + "|" + right)
                .orElse("");
        return questionText + "::" + optionsFingerprint;
    }

    private boolean isNearDuplicate(Question candidate, Set<String> seenFingerprints, String currentFingerprint) {
        String candidateText = normalizeForSimilarity(candidate.questionText());
        if (candidateText.isBlank()) {
            return false;
        }
        for (String seenFingerprint : seenFingerprints) {
            if (seenFingerprint == null || seenFingerprint.equals(currentFingerprint)) {
                continue;
            }
            String seenQuestionText = extractQuestionText(seenFingerprint);
            if (seenQuestionText.isBlank()) {
                continue;
            }
            double similarity = jaccardSimilarity(candidateText, seenQuestionText);
            if (similarity >= nearDuplicateSimilarityThreshold) {
                return true;
            }
        }
        return false;
    }

    private static String normalizeForSimilarity(String text) {
        if (text == null) {
            return "";
        }
        return text.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-zа-я0-9\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private static double jaccardSimilarity(String left, String right) {
        Set<String> leftTokens = tokenize(left);
        Set<String> rightTokens = tokenize(right);
        if (leftTokens.isEmpty() || rightTokens.isEmpty()) {
            return 0.0;
        }
        long intersection = leftTokens.stream().filter(rightTokens::contains).count();
        long union = leftTokens.size() + rightTokens.size() - intersection;
        return union == 0 ? 0.0 : (double) intersection / union;
    }

    private boolean hasLowDistractorDiversity(Question candidate) {
        if (candidate.options() == null || candidate.options().isEmpty()) {
            return false;
        }
        List<String> wrongOptionTexts = candidate.options().stream()
                .filter(option -> option != null && !option.correct())
                .map(QuestionOption::text)
                .map(QuestionGenerationPolicy::normalizeForSimilarity)
                .filter(text -> !text.isBlank())
                .toList();
        if (wrongOptionTexts.size() < 2) {
            return false;
        }
        for (int i = 0; i < wrongOptionTexts.size(); i++) {
            for (int j = i + 1; j < wrongOptionTexts.size(); j++) {
                double similarity = jaccardSimilarity(wrongOptionTexts.get(i), wrongOptionTexts.get(j));
                if (similarity >= distractorSimilarityThreshold) {
                    return true;
                }
            }
        }
        return false;
    }

    private static Set<String> tokenize(String text) {
        return Arrays.stream(text.split(" "))
                .map(String::trim)
                .filter(token -> token.length() > 2)
                .collect(Collectors.toSet());
    }

    private static Set<String> sanitizeSeenFingerprints(Set<String> seenFingerprints) {
        if (seenFingerprints == null || seenFingerprints.isEmpty()) {
            return Set.of();
        }
        return seenFingerprints.stream()
                .filter(value -> value != null && !value.isBlank())
                .map(String::trim)
                .map(value -> value.toLowerCase(Locale.ROOT))
                .collect(Collectors.toUnmodifiableSet());
    }

    private static String extractQuestionText(String fingerprint) {
        if (fingerprint == null || fingerprint.isBlank()) {
            return "";
        }
        String normalized = fingerprint.trim().toLowerCase(Locale.ROOT);
        return normalized.split("::", 2)[0].trim();
    }
}
