package com.cheatsheet.quiz.service.admin;

import com.cheatsheet.quiz.service.ai.SeniorInterviewRuleRegistry;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Сервис управления переопределениями приоритетов Senior-правил.
 */
@Service
public class AdminSeniorRulesService {
    private static final List<String> RULE_KEYS = List.copyOf(SeniorInterviewRuleRegistry.listRuleKeys());
    private static final List<SeniorInterviewRuleRegistry.RuleInfo> RULE_CATALOG =
            List.copyOf(SeniorInterviewRuleRegistry.listRules());

    private final SeniorRulePriorityOverrideStore overrideStore;
    private final SeniorRuleOverridePayloadProcessor payloadProcessor;

    public AdminSeniorRulesService(
            SeniorRulePriorityOverrideStore overrideStore,
            SeniorRuleOverridePayloadProcessor payloadProcessor
    ) {
        this.overrideStore = overrideStore;
        this.payloadProcessor = payloadProcessor;
    }

    public Map<String, Integer> getOverridesSorted() {
        return overrideStore.snapshotSorted();
    }

    public OverridesPayload getOverridesPayload() {
        Map<String, Integer> sorted = overrideStore.snapshotSorted();
        return new OverridesPayload(sorted, sorted.size());
    }

    public KeysPayload getKeysPayload(String prefix, String query) {
        String normalizedPrefix = payloadProcessor.normalizeSearchValue(prefix);
        String normalizedQuery = payloadProcessor.normalizeSearchValue(query);
        var keys = RULE_KEYS.stream()
                .filter(key -> normalizedPrefix.isBlank() || key.startsWith(normalizedPrefix))
                .filter(key -> normalizedQuery.isBlank() || key.contains(normalizedQuery))
                .toList();
        return new KeysPayload(keys, keys.size(), normalizedPrefix, normalizedQuery);
    }

    public CatalogPayload getCatalogPayload(String prefix, String query) {
        String normalizedPrefix = payloadProcessor.normalizeSearchValue(prefix);
        String normalizedQuery = payloadProcessor.normalizeSearchValue(query);
        var rules = RULE_CATALOG.stream()
                .filter(rule -> normalizedPrefix.isBlank() || rule.key().startsWith(normalizedPrefix))
                .filter(rule -> normalizedQuery.isBlank() || payloadProcessor.matchesCatalogQuery(rule, normalizedQuery))
                .toList();
        return new CatalogPayload(rules, rules.size(), normalizedPrefix, normalizedQuery);
    }

    public ReplaceResult replaceOverrides(Map<String, Integer> overrides) {
        Map<String, Integer> normalized = payloadProcessor.normalizeAndValidateReplace(overrides);
        overrideStore.replaceAll(normalized);
        Map<String, Integer> sorted = overrideStore.snapshotSorted();
        return new ReplaceResult(sorted, sorted.size());
    }

    public PatchResult patchOverrides(Map<String, Integer> updates) {
        if (updates == null || updates.isEmpty()) {
            Map<String, Integer> sortedCurrent = overrideStore.snapshotSorted();
            return new PatchResult(sortedCurrent, sortedCurrent.size(), 0, 0);
        }

        Map<String, Integer> normalizedUpdates = payloadProcessor.normalizeAndValidatePatch(updates);

        int applied = 0;
        int removed = 0;
        for (Map.Entry<String, Integer> entry : normalizedUpdates.entrySet()) {
            String normalizedKey = entry.getKey();
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
        String normalizedKey = payloadProcessor.normalizeAndValidateDeleteKey(key);
        boolean deleted = overrideStore.remove(normalizedKey);
        Map<String, Integer> sorted = overrideStore.snapshotSorted();
        return new DeleteResult(normalizedKey, deleted, sorted, sorted.size());
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
