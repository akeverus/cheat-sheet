package com.cheatsheet.quiz.service.ai;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Вспомогательные алгоритмы анализа текста вопроса для quality-валидации опций.
 */
final class OptionQuestionAnalysisSupport {
    private static final int MIN_QUESTION_RELEVANCE_TOKEN_LENGTH = 2;
    private static final int MIN_QUESTION_TOKEN_LENGTH = 4;
    private static final Set<String> QUESTION_STOP_WORDS = Set.of(
            "какой", "какая", "какие", "какое", "когда", "почему", "зачем", "что", "как",
            "если", "или", "для", "это", "этот", "эта", "эти", "чем", "где",
            "which", "what", "when", "why", "how", "this", "that"
    );
    private static final Set<String> SHORT_TECHNICAL_QUESTION_TOKENS = Set.of(
            "jvm", "gc", "sql", "api", "jpa", "ttl", "dto", "orm", "cpu", "ram", "jdk", "n+1"
    );
    private static final List<String> COMPARISON_OPTION_MARKERS = List.of(
            "сравн", "сложност", "врем", "памят", "лучше", "хуже", "в среднем",
            "compare", "complexity", "faster", "slower", "memory", "trade-off"
    );
    private static final List<String> CODE_OPTION_MARKERS = List.of(
            "вывед", "вернет", "бросит", "исключени", "значени", "null", "true", "false",
            "prints", "returns", "throws", "exception", "output", "value"
    );
    private static final List<String> MECHANISM_OPTION_MARKERS = List.of(
            "использ", "примен", "через", "за счет", "механизм", "шаг",
            "uses", "via", "because", "flow", "step"
    );
    private static final List<String> DEFINITION_OPTION_MARKERS = List.of(
            "это", "представля", "называ", "означа",
            "is", "refers", "means", "defined"
    );

    enum QuestionIntent {
        COMPARISON,
        CODE_RESULT,
        MECHANISM,
        DEFINITION,
        UNKNOWN
    }

    private OptionQuestionAnalysisSupport() {
    }

    static QuestionIntent detectQuestionIntent(
            String questionText,
            List<String> comparisonIntentMarkers,
            List<String> codeResultIntentMarkers,
            List<String> mechanismIntentMarkers,
            List<String> definitionIntentMarkers
    ) {
        String lower = questionText.toLowerCase(Locale.ROOT);
        Map<QuestionIntent, Integer> scores = new EnumMap<>(QuestionIntent.class);
        scores.put(QuestionIntent.COMPARISON, score(lower, comparisonIntentMarkers));
        scores.put(QuestionIntent.CODE_RESULT, score(lower, codeResultIntentMarkers));
        scores.put(QuestionIntent.MECHANISM, score(lower, mechanismIntentMarkers));
        scores.put(QuestionIntent.DEFINITION, score(lower, definitionIntentMarkers));

        QuestionIntent bestIntent = QuestionIntent.UNKNOWN;
        int bestScore = 0;
        for (Map.Entry<QuestionIntent, Integer> entry : scores.entrySet()) {
            if (entry.getValue() > bestScore) {
                bestIntent = entry.getKey();
                bestScore = entry.getValue();
            }
        }
        return bestScore >= 1 ? bestIntent : QuestionIntent.UNKNOWN;
    }

    static boolean matchesIntent(String optionText, QuestionIntent intent) {
        String lower = optionText.toLowerCase(Locale.ROOT);
        return switch (intent) {
            case COMPARISON -> containsAny(lower, COMPARISON_OPTION_MARKERS);
            case CODE_RESULT -> containsAny(lower, CODE_OPTION_MARKERS);
            case MECHANISM -> containsAny(lower, MECHANISM_OPTION_MARKERS);
            case DEFINITION -> lower.startsWith("это ") || containsAny(lower, DEFINITION_OPTION_MARKERS);
            case UNKNOWN -> true;
        };
    }

    static String intentName(QuestionIntent intent) {
        return switch (intent) {
            case COMPARISON -> "сравнения";
            case CODE_RESULT -> "результата выполнения";
            case MECHANISM -> "механизма";
            case DEFINITION -> "определения";
            case UNKNOWN -> "ответа";
        };
    }

    static Set<String> tokenizeQuestion(String questionText) {
        String normalized = OptionTextNormalizer.normalizeComparable(questionText)
                .replaceAll("[^a-zA-Z0-9а-яА-Я_+#]+", " ")
                .trim();
        if (normalized.isBlank()) {
            return Set.of();
        }
        String[] parts = normalized.split("\\s+");
        Set<String> tokens = new HashSet<>();
        for (String part : parts) {
            if (part.length() < MIN_QUESTION_TOKEN_LENGTH && !SHORT_TECHNICAL_QUESTION_TOKENS.contains(part)) {
                continue;
            }
            if (QUESTION_STOP_WORDS.contains(part)) {
                continue;
            }
            tokens.add(part);
        }
        return tokens;
    }

    static Set<String> tokenizeForQuestionRelevance(String text) {
        Set<String> tokens = new HashSet<>(OptionTextNormalizer.tokenize(text, MIN_QUESTION_RELEVANCE_TOKEN_LENGTH));
        String normalized = OptionTextNormalizer.normalizeComparable(text)
                .replaceAll("[^a-zA-Z0-9а-яА-Я_+#]+", " ")
                .trim();
        if (normalized.isBlank()) {
            return tokens;
        }
        for (String part : normalized.split("\\s+")) {
            if (SHORT_TECHNICAL_QUESTION_TOKENS.contains(part)) {
                tokens.add(part);
            }
        }
        return tokens;
    }

    static boolean hasTechnicalFocusTokens(Set<String> tokens) {
        for (String token : tokens) {
            if (SHORT_TECHNICAL_QUESTION_TOKENS.contains(token)) {
                return true;
            }
            if (token.contains("spring") || token.contains("hibernate") || token.contains("kafka")) {
                return true;
            }
        }
        return false;
    }

    static boolean isWeakCorrectAlignment(int expectedTokenCount, int overlap, double coverage) {
        if (expectedTokenCount <= 3) {
            return overlap == 0;
        }
        return overlap == 0 || (overlap < 2 && coverage < 0.15);
    }

    static int score(String text, List<String> markers) {
        int score = 0;
        for (String marker : markers) {
            if (text.contains(marker)) {
                score++;
            }
        }
        return score;
    }

    static boolean containsAny(String text, List<String> needles) {
        for (String needle : needles) {
            if (text.contains(needle)) {
                return true;
            }
        }
        return false;
    }
}
