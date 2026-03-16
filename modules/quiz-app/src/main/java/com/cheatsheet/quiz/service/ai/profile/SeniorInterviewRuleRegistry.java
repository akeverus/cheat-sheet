package com.cheatsheet.quiz.service.ai.profile;

import java.util.List;
import lombok.experimental.UtilityClass;
import lombok.Builder;

@UtilityClass
public class SeniorInterviewRuleRegistry {
    @Builder(toBuilder = true)
    public record RuleInfo(
            String key,
            List<String> scopeTokens,
            List<String> questionTokens,
            int defaultPriority,
            String trapHint
    ) {}

    public static List<String> listRuleKeys() {
        return SeniorInterviewRuleCatalog.rules().stream()
                .map(SeniorInterviewRuleCatalog.RuleDefinition::key)
                .sorted()
                .toList();
    }

    public static List<RuleInfo> listRules() {
        return SeniorInterviewRuleCatalog.rules().stream()
                .map(rule -> new RuleInfo(
                        rule.key(),
                        List.copyOf(rule.scopeTokens()),
                        List.copyOf(rule.questionTokens()),
                        rule.priority(),
                        rule.trapHint()
                ))
                .sorted(java.util.Comparator.comparing(RuleInfo::key))
                .toList();
    }
}
