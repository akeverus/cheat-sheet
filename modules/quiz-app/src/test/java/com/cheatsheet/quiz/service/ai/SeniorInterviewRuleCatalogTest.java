package com.cheatsheet.quiz.service.ai;

import com.cheatsheet.quiz.service.ai.profile.SeniorInterviewRuleCatalog;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class SeniorInterviewRuleCatalogTest {

    @Test
    void hasThirtyRulesWithUniqueKeys() {
        List<SeniorInterviewRuleCatalog.RuleDefinition> rules = SeniorInterviewRuleCatalog.rules();
        List<String> keys = rules.stream().map(SeniorInterviewRuleCatalog.RuleDefinition::key).toList();

        assertThat(rules).hasSize(30);
        assertThat(keys).doesNotHaveDuplicates();
    }

    @Test
    void eachRuleHasScopeQuestionTokensAndTrapHint() {
        List<SeniorInterviewRuleCatalog.RuleDefinition> rules = SeniorInterviewRuleCatalog.rules();

        assertThat(rules).allSatisfy(rule -> {
            assertThat(rule.scopeTokens()).isNotEmpty();
            assertThat(rule.questionTokens()).isNotEmpty();
            assertThat(rule.trapHint()).isNotBlank();
            assertThat(rule.priority()).isGreaterThanOrEqualTo(0);
        });
    }

    @Test
    void hasAtLeastOneBoostedPriorityRule() {
        List<SeniorInterviewRuleCatalog.RuleDefinition> rules = SeniorInterviewRuleCatalog.rules();
        assertThat(rules).anyMatch(rule -> rule.priority() > 0);
    }
}
