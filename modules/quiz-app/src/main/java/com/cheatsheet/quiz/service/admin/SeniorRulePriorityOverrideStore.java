package com.cheatsheet.quiz.service.admin;

import com.cheatsheet.quiz.config.app.AppProperties;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Потокобезопасное runtime-хранилище override-приоритетов Senior-правил.
 */
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SeniorRulePriorityOverrideStore {

    ConcurrentHashMap<String, Integer> overrides;

    public SeniorRulePriorityOverrideStore(AppProperties appProperties) {
        AppProperties.Interview interview = appProperties.getInterview();
        if (interview == null) {
            interview = new AppProperties.Interview();
            appProperties.setInterview(interview);
        }
        Map<String, Integer> configured = interview.getSeniorRulePriorityOverrides();
        this.overrides = configured instanceof ConcurrentHashMap<String, Integer> concurrent
                ? concurrent
                : new ConcurrentHashMap<>(configured == null ? Map.of() : configured);
        interview.setSeniorRulePriorityOverrides(this.overrides);
    }

    public Map<String, Integer> snapshotSorted() {
        return new TreeMap<>(overrides);
    }

    public void replaceAll(Map<String, Integer> values) {
        overrides.clear();
        if (values != null && !values.isEmpty()) {
            overrides.putAll(values);
        }
    }

    public boolean remove(String key) {
        return overrides.remove(key) != null;
    }

    public void put(String key, int value) {
        overrides.put(key, value);
    }

    public Map<String, Integer> view() {
        return overrides;
    }
}
