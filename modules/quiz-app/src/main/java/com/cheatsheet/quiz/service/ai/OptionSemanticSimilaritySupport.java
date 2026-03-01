package com.cheatsheet.quiz.service.ai;

import java.util.Locale;
import java.util.Set;

/**
 * Вспомогательные вычисления semantic similarity для option quality-валидации.
 */
final class OptionSemanticSimilaritySupport {

    private static final int MIN_TOKEN_LENGTH = 3;

    private OptionSemanticSimilaritySupport() {
    }

    static Set<String> tokenizeForSemanticSimilarity(String text) {
        if (text == null || text.isBlank()) {
            return Set.of();
        }
        return OptionTextNormalizer.tokenize(text.toLowerCase(Locale.ROOT), MIN_TOKEN_LENGTH);
    }

    static SimilarityMetrics similarityMetrics(Set<String> leftTokens, Set<String> rightTokens) {
        if (leftTokens == null || leftTokens.isEmpty() || rightTokens == null || rightTokens.isEmpty()) {
            return SimilarityMetrics.empty();
        }
        int intersection = 0;
        for (String token : rightTokens) {
            if (leftTokens.contains(token)) {
                intersection++;
            }
        }
        int union = leftTokens.size() + rightTokens.size() - intersection;
        int minTokenCount = Math.min(leftTokens.size(), rightTokens.size());
        double jaccard = union == 0 ? 0 : (double) intersection / union;
        double minCoverage = minTokenCount == 0 ? 0 : (double) intersection / minTokenCount;
        return new SimilarityMetrics(intersection, union, jaccard, minCoverage);
    }

    record SimilarityMetrics(int intersection, int union, double jaccard, double minCoverage) {
        static SimilarityMetrics empty() {
            return new SimilarityMetrics(0, 0, 0, 0);
        }
    }
}
