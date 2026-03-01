package com.cheatsheet.quiz.service.admin;

import com.cheatsheet.quiz.config.AppProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AdminSeniorRulesServiceTest {

    private AppProperties appProperties;
    private AppProperties.Interview interview;
    private SeniorRulePriorityOverrideStore overrideStore;
    private AdminSeniorRulesService service;

    @BeforeEach
    void setUp() {
        appProperties = new AppProperties();
        interview = new AppProperties.Interview();
        interview.setSeniorRulePriorityOverrides(new ConcurrentHashMap<>());
        appProperties.setInterview(interview);
        overrideStore = new SeniorRulePriorityOverrideStore(appProperties);
        service = new AdminSeniorRulesService(overrideStore);
    }

    @Test
    void replaceOverridesNormalizesKeysAndReplacesExistingValues() {
        interview.getSeniorRulePriorityOverrides().put("spring-transactional", 2);

        AdminSeniorRulesService.ReplaceResult payload = service.replaceOverrides(Map.of(
                " Spring-Transactional ", 9,
                "sql-null-semantics", 6
        ));

        Map<String, Integer> overrides = payload.overrides();
        assertThat(overrides)
                .containsEntry("spring-transactional", 9)
                .containsEntry("sql-null-semantics", 6);
        assertThat(overrides).hasSize(2);
        assertThat(interview.getSeniorRulePriorityOverrides())
                .containsEntry("spring-transactional", 9)
                .containsEntry("sql-null-semantics", 6);
    }

    @Test
    void patchOverridesUpdatesAndRemovesEntries() {
        interview.getSeniorRulePriorityOverrides().put("spring-transactional", 5);
        interview.getSeniorRulePriorityOverrides().put("sql-null-semantics", 4);
        Map<String, Integer> updates = new HashMap<>();
        updates.put("spring-transactional", 8);
        updates.put("sql-null-semantics", null);

        AdminSeniorRulesService.PatchResult result = service.patchOverrides(updates);

        assertThat(result.applied()).isEqualTo(1);
        assertThat(result.removed()).isEqualTo(1);
        assertThat(result.overrides()).containsEntry("spring-transactional", 8);
        assertThat(result.overrides()).doesNotContainKey("sql-null-semantics");
    }

    @Test
    void replaceOverridesRejectsNullPriority() {
        Map<String, Integer> invalid = new HashMap<>();
        invalid.put("spring-transactional", null);

        assertThatThrownBy(() -> service.replaceOverrides(invalid))
                .isInstanceOf(AdminSeniorRulesService.ValidationException.class)
                .hasMessageContaining("priority >= 0");
    }

    @Test
    void patchOverridesRejectsUnknownKey() {
        assertThatThrownBy(() -> service.patchOverrides(Map.of("unknown-rule", 1)))
                .isInstanceOf(AdminSeniorRulesService.ValidationException.class)
                .hasMessageContaining("Неизвестный ключ Senior-правила");
    }

    @Test
    void migratesNonConcurrentOverridesMapToConcurrentHashMap() {
        Map<String, Integer> nonConcurrent = new HashMap<>();
        nonConcurrent.put("spring-transactional", 3);
        interview.setSeniorRulePriorityOverrides(nonConcurrent);
        overrideStore = new SeniorRulePriorityOverrideStore(appProperties);
        service = new AdminSeniorRulesService(overrideStore);

        service.getOverridesSorted();

        assertThat(interview.getSeniorRulePriorityOverrides()).isInstanceOf(ConcurrentHashMap.class);
        assertThat(interview.getSeniorRulePriorityOverrides()).containsEntry("spring-transactional", 3);
    }
}
