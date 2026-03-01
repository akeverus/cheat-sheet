package com.cheatsheet.quiz.service.admin;

import com.cheatsheet.quiz.config.AppProperties;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;

class SeniorRulePriorityOverrideStoreTest {

    @Test
    void migratesConfiguredMapToConcurrentRuntimeStore() {
        AppProperties appProperties = new AppProperties();
        AppProperties.Interview interview = new AppProperties.Interview();
        Map<String, Integer> nonConcurrent = new HashMap<>();
        nonConcurrent.put("spring-transactional", 7);
        interview.setSeniorRulePriorityOverrides(nonConcurrent);
        appProperties.setInterview(interview);

        SeniorRulePriorityOverrideStore store = new SeniorRulePriorityOverrideStore(appProperties);

        assertThat(store.view()).isInstanceOf(ConcurrentHashMap.class);
        assertThat(appProperties.getInterview().getSeniorRulePriorityOverrides()).isSameAs(store.view());
        assertThat(store.snapshotSorted()).containsEntry("spring-transactional", 7);
    }

    @Test
    void replaceAllOverwritesPreviousValues() {
        AppProperties appProperties = new AppProperties();
        SeniorRulePriorityOverrideStore store = new SeniorRulePriorityOverrideStore(appProperties);
        store.put("spring-transactional", 3);

        store.replaceAll(Map.of("sql-null-semantics", 9));

        assertThat(store.snapshotSorted())
                .hasSize(1)
                .containsEntry("sql-null-semantics", 9);
    }
}
