package com.cheatsheet.quiz.service.ai.profile;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.Builder;

public final class SeniorInterviewFocusProfile {
    @Builder(toBuilder = true)
    private record FocusRule(String key, List<String> scopeTokens, List<String> questionTokens, String trapHint, int priority) {}

    private static final List<FocusRule> FOCUS_RULES = SeniorInterviewRuleCatalog.rules().stream()
            .map(rule -> new FocusRule(
                    normalize(rule.key()),
                    rule.scopeTokens().stream().map(SeniorInterviewFocusProfile::normalize).toList(),
                    rule.questionTokens().stream().map(SeniorInterviewFocusProfile::normalize).toList(),
                    rule.trapHint(),
                    rule.priority()
            ))
            .toList();

    private SeniorInterviewFocusProfile() {
    }

    public static String focusContext(String slug, String filePath, String topic, String questionText, boolean important) {
        return focusContext(slug, filePath, topic, questionText, important, Map.of());
    }

    public static String focusContext(
            String slug,
            String filePath,
            String topic,
            String questionText,
            boolean important,
            Map<String, Integer> priorityOverrides
    ) {
        String scopeText = normalize(slug) + " " + normalize(filePath) + " " + normalize(topic);
        String normalizedQuestion = normalize(questionText);
        FocusRule matched = pickBestRule(scopeText, normalizedQuestion, FOCUS_RULES, normalizeOverrideMap(priorityOverrides));
        if (matched == null) {
            return "";
        }
        String seniority = important ? "Senior-фокус (критичный вопрос)" : "Senior-фокус";
        return seniority + " [" + matched.key() + "]: " + matched.trapHint();
    }

    public static String focusContext(String topic, String questionText, boolean important) {
        return focusContext("", "", topic, questionText, important, Map.of());
    }

    public static int rulesCountForTest() {
        return FOCUS_RULES.size();
    }

    public static String pickBestRuleKeyForTest(
            String slug,
            String filePath,
            String topic,
            String questionText,
            List<SeniorInterviewRuleCatalog.RuleDefinition> rules,
            Map<String, Integer> priorityOverrides
    ) {
        String scopeText = normalize(slug) + " " + normalize(filePath) + " " + normalize(topic);
        String normalizedQuestion = normalize(questionText);
        List<FocusRule> focusRules = rules.stream()
                .map(rule -> new FocusRule(
                        normalize(rule.key()),
                        rule.scopeTokens().stream().map(SeniorInterviewFocusProfile::normalize).toList(),
                        rule.questionTokens().stream().map(SeniorInterviewFocusProfile::normalize).toList(),
                        rule.trapHint(),
                        rule.priority()
                ))
                .toList();
        FocusRule matched = pickBestRule(scopeText, normalizedQuestion, focusRules, normalizeOverrideMap(priorityOverrides));
        return matched == null ? "" : matched.key();
    }

    private static FocusRule pickBestRule(
            String scopeText,
            String normalizedQuestion,
            List<FocusRule> rules,
            Map<String, Integer> priorityOverrides
    ) {
        return rules.stream()
                .filter(rule -> matchesAnyToken(scopeText, rule.scopeTokens()))
                .filter(rule -> matchesAllTokens(normalizedQuestion, rule.questionTokens()))
                .max(java.util.Comparator.comparingInt(rule -> scoreRule(rule, scopeText, normalizedQuestion, priorityOverrides)))
                .orElse(null);
    }

    private static int scoreRule(
            FocusRule rule,
            String scopeText,
            String normalizedQuestion,
            Map<String, Integer> priorityOverrides
    ) {
        int matchedScopeTokens = countMatchedTokens(scopeText, rule.scopeTokens());
        int matchedQuestionTokens = countMatchedTokens(normalizedQuestion, rule.questionTokens());
        int effectivePriority = priorityOverrides.getOrDefault(rule.key(), rule.priority());
        return effectivePriority * 1_000 + matchedQuestionTokens * 100 + matchedScopeTokens;
    }

    private static boolean matchesAllTokens(String text, List<String> tokens) {
        for (String token : tokens) {
            if (!text.contains(token)) {
                return false;
            }
        }
        return true;
    }

    private static boolean matchesAnyToken(String text, List<String> tokens) {
        for (String token : tokens) {
            if (text.contains(token)) {
                return true;
            }
        }
        return false;
    }

    private static int countMatchedTokens(String text, List<String> tokens) {
        int matched = 0;
        for (String token : tokens) {
            if (text.contains(token)) {
                matched++;
            }
        }
        return matched;
    }

    private static Map<String, Integer> normalizeOverrideMap(Map<String, Integer> priorityOverrides) {
        if (priorityOverrides == null || priorityOverrides.isEmpty()) {
            return Map.of();
        }
        return priorityOverrides.entrySet().stream()
                .filter(entry -> entry.getKey() != null && entry.getValue() != null)
                .collect(java.util.stream.Collectors.toMap(
                        entry -> normalize(entry.getKey()),
                        Map.Entry::getValue
                ));
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.toLowerCase(Locale.ROOT).trim();
    }
}
