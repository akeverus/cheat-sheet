package com.cheatsheet.quiz.service.admin;

import com.cheatsheet.quiz.service.ai.SeniorInterviewRuleRegistry;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Сервис управления переопределениями приоритетов Senior-правил.
 */
@Service
public class AdminSeniorRulesService {
    private static final List<String> RULE_KEYS = List.copyOf(SeniorInterviewRuleRegistry.listRuleKeys());
    private static final Set<String> ALLOWED_RULE_KEYS = Set.copyOf(RULE_KEYS);
    private static final List<SeniorInterviewRuleRegistry.RuleInfo> RULE_CATALOG =
            List.copyOf(SeniorInterviewRuleRegistry.listRules());

    private final SeniorRulePriorityOverrideStore overrideStore;

    public AdminSeniorRulesService(SeniorRulePriorityOverrideStore overrideStore) {
        this.overrideStore = overrideStore;
    }

    public Map<String, Integer> getOverridesSorted() {
        return overrideStore.snapshotSorted();
    }

    public OverridesPayload getOverridesPayload() {
        Map<String, Integer> sorted = overrideStore.snapshotSorted();
        return new OverridesPayload(sorted, sorted.size());
    }

    public KeysPayload getKeysPayload(String prefix, String query) {
        String normalizedPrefix = normalize(prefix);
        String normalizedQuery = normalize(query);
        var keys = RULE_KEYS.stream()
                .filter(key -> normalizedPrefix.isBlank() || key.startsWith(normalizedPrefix))
                .filter(key -> normalizedQuery.isBlank() || key.contains(normalizedQuery))
                .toList();
        return new KeysPayload(keys, keys.size(), normalizedPrefix, normalizedQuery);
    }

    public CatalogPayload getCatalogPayload(String prefix, String query) {
        String normalizedPrefix = normalize(prefix);
        String normalizedQuery = normalize(query);
        var rules = RULE_CATALOG.stream()
                .filter(rule -> normalizedPrefix.isBlank() || rule.key().startsWith(normalizedPrefix))
                .filter(rule -> normalizedQuery.isBlank() || matchesCatalogQuery(rule, normalizedQuery))
                .toList();
        return new CatalogPayload(rules, rules.size(), normalizedPrefix, normalizedQuery);
    }

    public ReplaceResult replaceOverrides(Map<String, Integer> overrides) {
        Map<String, Integer> safeOverrides = overrides == null ? Map.of() : overrides;
        validatePayload(safeOverrides, false);

        Map<String, Integer> normalized = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : safeOverrides.entrySet()) {
            normalized.put(normalizeKey(entry.getKey()), entry.getValue());
        }

        overrideStore.replaceAll(normalized);
        Map<String, Integer> sorted = overrideStore.snapshotSorted();
        return new ReplaceResult(sorted, sorted.size());
    }

    public PatchResult patchOverrides(Map<String, Integer> updates) {
        if (updates == null || updates.isEmpty()) {
            Map<String, Integer> sortedCurrent = overrideStore.snapshotSorted();
            return new PatchResult(sortedCurrent, sortedCurrent.size(), 0, 0);
        }

        validatePayload(updates, true);

        int applied = 0;
        int removed = 0;
        for (Map.Entry<String, Integer> entry : updates.entrySet()) {
            String normalizedKey = normalizeKey(entry.getKey());
            Integer value = entry.getValue();
            if (value == null) {
                if (overrideStore.remove(normalizedKey)) {
                    removed++;
                }
            } else {
                overrideStore.put(normalizedKey, value);
                applied++;
            }
        }
        Map<String, Integer> sorted = overrideStore.snapshotSorted();
        return new PatchResult(sorted, sorted.size(), applied, removed);
    }

    public DeleteResult deleteOverride(String key) {
        String normalizedKey = normalize(key);
        if (normalizedKey.isBlank()) {
            throw new ValidationException("Ключ правила не должен быть пустым", Map.of("key", String.valueOf(key)));
        }
        boolean deleted = overrideStore.remove(normalizedKey);
        Map<String, Integer> sorted = overrideStore.snapshotSorted();
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

    public record OverridesPayload(Map<String, Integer> overrides, int size) {
    }

    public record ReplaceResult(Map<String, Integer> overrides, int size) {
    }

    public record KeysPayload(List<String> keys, int size, String prefix, String q) {
    }

    public record CatalogPayload(List<SeniorInterviewRuleRegistry.RuleInfo> rules, int size, String prefix, String q) {
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
