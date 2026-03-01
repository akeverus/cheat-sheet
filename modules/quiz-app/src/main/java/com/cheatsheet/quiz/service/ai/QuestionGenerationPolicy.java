package com.cheatsheet.quiz.service.ai;

import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Политика приёма сгенерированного вопроса.
 */
@Component
public class QuestionGenerationPolicy {

    private final int minQualityScore;
    private final int maxQuestionTextLength;
    private final int maxOptionsTotalTextLength;

    public QuestionGenerationPolicy(
            @Value("${app.interview.question-min-quality-score:70}") int minQualityScore,
            @Value("${app.interview.question-max-text-length:280}") int maxQuestionTextLength,
            @Value("${app.interview.question-max-options-total-text-length:520}") int maxOptionsTotalTextLength
    ) {
        this.minQualityScore = Math.max(0, Math.min(100, minQualityScore));
        this.maxQuestionTextLength = Math.max(40, maxQuestionTextLength);
        this.maxOptionsTotalTextLength = Math.max(80, maxOptionsTotalTextLength);
    }

    public QuestionGenerationPolicy(int minQualityScore) {
        this(minQualityScore, 280, 520);
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
            }
        }
        return violations;
    }

    private static String fingerprint(Question question) {
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
}
