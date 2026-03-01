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

    @Autowired
    public QuestionGenerationPolicy(AppProperties appProperties) {
        this(
                appProperties.getInterview().getQuestionMinQualityScore(),
                appProperties.getInterview().getQuestionMaxTextLength(),
                appProperties.getInterview().getQuestionMaxOptionsTotalTextLength(),
                appProperties.getInterview().getQuestionNearDuplicateSimilarityThreshold()
        );
    }

    public QuestionGenerationPolicy(int minQualityScore) {
        this(minQualityScore, 280, 520, 0.82);
    }

    public QuestionGenerationPolicy(
            int minQualityScore,
            int maxQuestionTextLength,
            int maxOptionsTotalTextLength
    ) {
        this(minQualityScore, maxQuestionTextLength, maxOptionsTotalTextLength, 0.82);
    }

    public QuestionGenerationPolicy(
            int minQualityScore,
            int maxQuestionTextLength,
            int maxOptionsTotalTextLength,
            double nearDuplicateSimilarityThreshold
    ) {
        this.minQualityScore = Math.max(0, Math.min(100, minQualityScore));
        this.maxQuestionTextLength = Math.max(40, maxQuestionTextLength);
        this.maxOptionsTotalTextLength = Math.max(80, maxOptionsTotalTextLength);
        this.nearDuplicateSimilarityThreshold = Math.max(0.6, Math.min(0.99, nearDuplicateSimilarityThreshold));
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

        if (seenFingerprints != null) {
            String fingerprint = fingerprint(candidate);
            if (!seenFingerprints.add(fingerprint)) {
                violations.add("Question candidate duplicates previous generation attempt");
            } else if (isNearDuplicate(candidate, seenFingerprints, fingerprint)) {
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
            String seenQuestionText = seenFingerprint == null
                    ? ""
                    : seenFingerprint.split("::", 2)[0];
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

    private static Set<String> tokenize(String text) {
        return Arrays.stream(text.split(" "))
                .map(String::trim)
                .filter(token -> token.length() > 2)
                .collect(Collectors.toSet());
    }
}
