package com.cheatsheet.quiz.service.admin;

import com.cheatsheet.quiz.config.AppProperties;
import com.cheatsheet.quiz.service.ai.SeniorInterviewRuleRegistry;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Сервис управления переопределениями приоритетов Senior-правил.
 */
@Service
public class AdminSeniorRulesService {
    private static final List<String> RULE_KEYS = List.copyOf(SeniorInterviewRuleRegistry.listRuleKeys());
    private static final Set<String> ALLOWED_RULE_KEYS = Set.copyOf(RULE_KEYS);
    private static final List<SeniorInterviewRuleRegistry.RuleInfo> RULE_CATALOG =
            List.copyOf(SeniorInterviewRuleRegistry.listRules());

    private final AppProperties appProperties;

    public AdminSeniorRulesService(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    public Map<String, Integer> getOverridesSorted() {
        return new TreeMap<>(getOrInitOverrides());
    }

    public Map<String, Object> getKeysPayload(String prefix, String query) {
        String normalizedPrefix = normalize(prefix);
        String normalizedQuery = normalize(query);
        var keys = RULE_KEYS.stream()
                .filter(key -> normalizedPrefix.isBlank() || key.startsWith(normalizedPrefix))
                .filter(key -> normalizedQuery.isBlank() || key.contains(normalizedQuery))
                .toList();
        return Map.of("keys", keys, "size", keys.size(), "prefix", normalizedPrefix, "q", normalizedQuery);
    }

    public Map<String, Object> getCatalogPayload(String prefix, String query) {
        String normalizedPrefix = normalize(prefix);
        String normalizedQuery = normalize(query);
        var rules = RULE_CATALOG.stream()
                .filter(rule -> normalizedPrefix.isBlank() || rule.key().startsWith(normalizedPrefix))
                .filter(rule -> normalizedQuery.isBlank() || matchesCatalogQuery(rule, normalizedQuery))
                .toList();
        return Map.of("rules", rules, "size", rules.size(), "prefix", normalizedPrefix, "q", normalizedQuery);
    }

    public Map<String, Object> replaceOverrides(Map<String, Integer> overrides) {
        Map<String, Integer> safeOverrides = overrides == null ? Map.of() : overrides;
        validatePayload(safeOverrides, false);

        Map<String, Integer> normalized = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : safeOverrides.entrySet()) {
            normalized.put(normalizeKey(entry.getKey()), entry.getValue());
        }

        Map<String, Integer> target = getOrInitOverrides();
        target.clear();
        target.putAll(normalized);
        Map<String, Integer> sorted = new TreeMap<>(target);
        return Map.of("overrides", sorted, "size", sorted.size());
    }

    public PatchResult patchOverrides(Map<String, Integer> updates) {
        if (updates == null || updates.isEmpty()) {
            Map<String, Integer> sortedCurrent = new TreeMap<>(getOrInitOverrides());
            return new PatchResult(sortedCurrent, sortedCurrent.size(), 0, 0);
        }

        validatePayload(updates, true);

        int applied = 0;
        int removed = 0;
        Map<String, Integer> target = getOrInitOverrides();
        for (Map.Entry<String, Integer> entry : updates.entrySet()) {
            String normalizedKey = normalizeKey(entry.getKey());
            Integer value = entry.getValue();
            if (value == null) {
                if (target.remove(normalizedKey) != null) {
                    removed++;
                }
            } else {
                target.put(normalizedKey, value);
                applied++;
            }
        }
        Map<String, Integer> sorted = new TreeMap<>(target);
        return new PatchResult(sorted, sorted.size(), applied, removed);
    }

    public DeleteResult deleteOverride(String key) {
        String normalizedKey = normalize(key);
        if (normalizedKey.isBlank()) {
            throw new ValidationException("Ключ правила не должен быть пустым", Map.of("key", String.valueOf(key)));
        }
        Map<String, Integer> target = getOrInitOverrides();
        boolean deleted = target.remove(normalizedKey) != null;
        Map<String, Integer> sorted = new TreeMap<>(target);
        return new DeleteResult(normalizedKey, deleted, sorted, sorted.size());
    }

    private void validatePayload(Map<String, Integer> payload, boolean nullAllowed) {
        for (Map.Entry<String, Integer> entry : payload.entrySet()) {
            String key = entry.getKey() == null ? "" : entry.getKey().trim();
            String normalizedKey = normalizeKey(entry.getKey());
            Integer value = entry.getValue();
            boolean invalidValue = nullAllowed ? value != null && value < 0 : value == null || value < 0;
            if (key.isBlank() || invalidValue) {
                throw new ValidationException(
                        nullAllowed
                                ? "Некорректный payload senior-rule-priority-overrides: ключ не должен быть пустым, priority >= 0 или null для удаления"
                                : "Некорректный payload senior-rule-priority-overrides: ключ не должен быть пустым, priority >= 0",
                        Map.of("entry", String.valueOf(entry))
                );
            }
            if (!ALLOWED_RULE_KEYS.contains(normalizedKey)) {
                throw new ValidationException(
                        "Неизвестный ключ Senior-правила: " + key,
                        Map.of("allowedKeys", ALLOWED_RULE_KEYS)
                );
            }
        }
    }

    private Map<String, Integer> getOrInitOverrides() {
        Map<String, Integer> configured = appProperties.getInterview().getSeniorRulePriorityOverrides();
        if (configured == null) {
            ConcurrentHashMap<String, Integer> initialized = new ConcurrentHashMap<>();
            appProperties.getInterview().setSeniorRulePriorityOverrides(initialized);
            return initialized;
        }
        if (configured instanceof ConcurrentHashMap) {
            return configured;
        }
        ConcurrentHashMap<String, Integer> concurrent = new ConcurrentHashMap<>(configured);
        appProperties.getInterview().setSeniorRulePriorityOverrides(concurrent);
        return concurrent;
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeKey(String key) {
        return normalize(key);
    }

    private static boolean matchesCatalogQuery(SeniorInterviewRuleRegistry.RuleInfo rule, String normalizedQuery) {
        if (rule.key().contains(normalizedQuery)
                || rule.trapHint().toLowerCase(Locale.ROOT).contains(normalizedQuery)) {
            return true;
        }
        boolean scopeMatch = rule.scopeTokens().stream()
                .map(token -> token.toLowerCase(Locale.ROOT))
                .anyMatch(token -> token.contains(normalizedQuery));
        if (scopeMatch) {
            return true;
        }
        return rule.questionTokens().stream()
                .map(token -> token.toLowerCase(Locale.ROOT))
                .anyMatch(token -> token.contains(normalizedQuery));
    }

    public record PatchResult(Map<String, Integer> overrides, int size, int applied, int removed) {
    }

    public record DeleteResult(String key, boolean deleted, Map<String, Integer> overrides, int size) {
    }

    public static class ValidationException extends RuntimeException {
        private final transient Map<String, ?> details;

        public ValidationException(String message, Map<String, ?> details) {
            super(message);
            this.details = details;
        }

        public Map<String, ?> details() {
            return details;
        }
    }
}
