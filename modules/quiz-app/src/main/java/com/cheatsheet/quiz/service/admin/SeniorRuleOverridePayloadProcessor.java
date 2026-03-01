package com.cheatsheet.quiz.service.admin;

import com.cheatsheet.quiz.service.ai.SeniorInterviewRuleRegistry;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Нормализация и валидация payload-ов override-приоритетов Senior-правил.
 */
@Component
public class SeniorRuleOverridePayloadProcessor {

    private final Set<String> allowedRuleKeys = Set.copyOf(SeniorInterviewRuleRegistry.listRuleKeys());

    public Map<String, Integer> normalizeAndValidateReplace(Map<String, Integer> payload) {
        Map<String, Integer> safePayload = payload == null ? Map.of() : payload;
        validatePayload(safePayload, false);
        return normalizePayload(safePayload);
    }

    public Map<String, Integer> normalizeAndValidatePatch(Map<String, Integer> payload) {
        Map<String, Integer> safePayload = payload == null ? Map.of() : payload;
        validatePayload(safePayload, true);
        return normalizePayload(safePayload);
    }

    public String normalizeAndValidateDeleteKey(String key) {
        String normalizedKey = normalizeKey(key);
        if (normalizedKey.isBlank()) {
            throw new AdminSeniorRulesService.ValidationException(
                    "Ключ правила не должен быть пустым",
                    Map.of("key", String.valueOf(key))
            );
        }
        return normalizedKey;
    }

    public String normalizeSearchValue(String value) {
        return normalize(value);
    }

    public boolean matchesCatalogQuery(SeniorInterviewRuleRegistry.RuleInfo rule, String normalizedQuery) {
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

    private void validatePayload(Map<String, Integer> payload, boolean nullAllowed) {
        for (Map.Entry<String, Integer> entry : payload.entrySet()) {
            String key = entry.getKey() == null ? "" : entry.getKey().trim();
            String normalizedKey = normalizeKey(entry.getKey());
            Integer value = entry.getValue();
            boolean invalidValue = nullAllowed ? value != null && value < 0 : value == null || value < 0;
            if (key.isBlank() || invalidValue) {
                throw new AdminSeniorRulesService.ValidationException(
                        nullAllowed
                                ? "Некорректный payload senior-rule-priority-overrides: ключ не должен быть пустым, priority >= 0 или null для удаления"
                                : "Некорректный payload senior-rule-priority-overrides: ключ не должен быть пустым, priority >= 0",
                        Map.of("entry", String.valueOf(entry))
                );
            }
            if (!allowedRuleKeys.contains(normalizedKey)) {
                throw new AdminSeniorRulesService.ValidationException(
                        "Неизвестный ключ Senior-правила: " + key,
                        Map.of("allowedKeys", allowedRuleKeys)
                );
            }
        }
    }

    private Map<String, Integer> normalizePayload(Map<String, Integer> payload) {
        Map<String, Integer> normalized = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : payload.entrySet()) {
            normalized.put(normalizeKey(entry.getKey()), entry.getValue());
        }
        return normalized;
    }

    private String normalizeKey(String key) {
        return normalize(key);
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}
