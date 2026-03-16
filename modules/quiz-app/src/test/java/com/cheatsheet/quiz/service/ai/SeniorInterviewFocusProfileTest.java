package com.cheatsheet.quiz.service.ai;

import com.cheatsheet.quiz.service.ai.profile.SeniorInterviewFocusProfile;
import com.cheatsheet.quiz.service.ai.profile.SeniorInterviewRuleCatalog;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class SeniorInterviewFocusProfileTest {

    @Test
    void containsThirtyFocusRules() {
        assertThat(SeniorInterviewFocusProfile.rulesCountForTest()).isEqualTo(30);
    }

    @Test
    void returnsSeniorHintForEqualsHashCodeQuestion() {
        String context = SeniorInterviewFocusProfile.focusContext(
                "programming-languages/java/java-core#q12",
                "cheatsheets/programming-languages/java/java-core.md",
                "programming-languages/java",
                "Объясни разницу между equals и hashCode в HashMap",
                true
        );

        assertThat(context)
                .contains("Senior-фокус")
                .contains("equals-vs-hashcode")
                .contains("equals")
                .contains("hashCode");
    }

    @Test
    void returnsEmptyContextForUnmatchedQuestion() {
        String context = SeniorInterviewFocusProfile.focusContext(
                "linux/shell#q1",
                "cheatsheets/linux/shell.md",
                "linux/shell",
                "Как работает ls -la",
                false
        );

        assertThat(context).isBlank();
    }

    @Test
    void doesNotMatchWhenScopeDiffersEvenIfQuestionTokensMatch() {
        String context = SeniorInterviewFocusProfile.focusContext(
                "linux/shell#q2",
                "cheatsheets/linux/shell.md",
                "linux/shell",
                "Объясни разницу между equals и hashCode",
                true
        );

        assertThat(context).isBlank();
    }

    @Test
    void choosesHigherPriorityRuleWhenSeveralMatch() {
        List<SeniorInterviewRuleCatalog.RuleDefinition> customRules = List.of(
                new SeniorInterviewRuleCatalog.RuleDefinition(
                        "low-priority",
                        List.of("java"),
                        List.of("equals"),
                        "low",
                        0
                ),
                new SeniorInterviewRuleCatalog.RuleDefinition(
                        "high-priority",
                        List.of("java"),
                        List.of("equals"),
                        "high",
                        3
                )
        );

        String key = SeniorInterviewFocusProfile.pickBestRuleKeyForTest(
                "programming-languages/java#q1",
                "cheatsheets/programming-languages/java/core.md",
                "programming-languages/java",
                "Что важно про equals в HashMap?",
                customRules,
                java.util.Map.of()
        );

        assertThat(key).isEqualTo("high-priority");
    }

    @Test
    void overridePriorityCanChangeWinnerRule() {
        List<SeniorInterviewRuleCatalog.RuleDefinition> customRules = List.of(
                new SeniorInterviewRuleCatalog.RuleDefinition(
                        "low-priority",
                        List.of("java"),
                        List.of("equals"),
                        "low",
                        0
                ),
                new SeniorInterviewRuleCatalog.RuleDefinition(
                        "high-priority",
                        List.of("java"),
                        List.of("equals"),
                        "high",
                        3
                )
        );

        String key = SeniorInterviewFocusProfile.pickBestRuleKeyForTest(
                "programming-languages/java#q1",
                "cheatsheets/programming-languages/java/core.md",
                "programming-languages/java",
                "Что важно про equals в HashMap?",
                customRules,
                java.util.Map.of("low-priority", 9)
        );

        assertThat(key).isEqualTo("low-priority");
    }
}
