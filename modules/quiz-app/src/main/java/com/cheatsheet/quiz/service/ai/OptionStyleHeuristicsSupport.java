package com.cheatsheet.quiz.service.ai;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Вспомогательные эвристики для проверки стиля option text.
 */
final class OptionStyleHeuristicsSupport {

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
    private static final Pattern PATH_LIKE_PATTERN =
            Pattern.compile("(?i)\\b[a-z0-9._-]{2,}/[a-z0-9._-]{2,}/[a-z0-9._/-]{2,}\\b");
    private static final List<String> PATH_LIKE_MARKERS = List.of(
            ".md", "-interview", "src/", "modules/", "cheatsheets/"
    );

    private OptionStyleHeuristicsSupport() {
    }

    static boolean containsStopPhrase(String text) {
        for (String phrase : BOILERPLATE_STOP_PHRASES) {
            if (text.contains(phrase)) {
                return true;
            }
        }
        return false;
    }

    static boolean isWhitelistedPrefix(String prefix) {
        for (String allowed : BOILERPLATE_PREFIX_WHITELIST) {
            if (prefix.startsWith(allowed)) {
                return true;
            }
        }
        return false;
    }

    static String leadingWords(String text, int minWords, int maxWords) {
        String[] words = text.split(" ");
        if (words.length < minWords) {
            return null;
        }
        if (maxWords < minWords) {
            return null;
        }
        int limit = Math.min(words.length, minWords);
        return String.join(" ", Arrays.copyOfRange(words, 0, limit));
    }

    static boolean isVaguePlaceholderWithoutConcreteSignal(String lowerText) {
        for (String phrase : VAGUE_PLACEHOLDER_PHRASES) {
            if (lowerText.contains(phrase) && !hasConcreteSignal(lowerText)) {
                return true;
            }
        }
        return false;
    }

    static boolean hasConcreteSignal(String lowerText) {
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

    static boolean isPathLikeOptionText(String text) {
        if (text == null || text.isBlank()) {
            return false;
        }
        String lower = text.toLowerCase(Locale.ROOT);
        for (String marker : PATH_LIKE_MARKERS) {
            if (lower.contains(marker)) {
                return true;
            }
        }
        return PATH_LIKE_PATTERN.matcher(lower).find();
    }

    static boolean isClicheFallbackText(String text) {
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
}
