package com.cheatsheet.quiz.service.admin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SeniorRuleOverridePayloadProcessorTest {

    private SeniorRuleOverridePayloadProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new SeniorRuleOverridePayloadProcessor();
    }

    @Test
    void normalizeAndValidateReplaceNormalizesKeys() {
        Map<String, Integer> payload = new HashMap<>();
        payload.put(" Spring-Transactional ", 7);
        payload.put("sql-null-semantics", 4);

        Map<String, Integer> normalized = processor.normalizeAndValidateReplace(payload);

        assertThat(normalized)
                .containsEntry("spring-transactional", 7)
                .containsEntry("sql-null-semantics", 4);
    }

    @Test
    void normalizeAndValidatePatchAllowsNullForDelete() {
        Map<String, Integer> payload = new HashMap<>();
        payload.put("spring-transactional", null);

        Map<String, Integer> normalized = processor.normalizeAndValidatePatch(payload);

        assertThat(normalized).containsEntry("spring-transactional", null);
    }

    @Test
    void normalizeAndValidateReplaceRejectsNullPriority() {
        Map<String, Integer> payload = new HashMap<>();
        payload.put("spring-transactional", null);

        assertThatThrownBy(() -> processor.normalizeAndValidateReplace(payload))
                .isInstanceOf(AdminSeniorRulesService.ValidationException.class)
                .hasMessageContaining("priority >= 0");
    }

    @Test
    void normalizeAndValidateDeleteKeyRejectsBlankKey() {
        assertThatThrownBy(() -> processor.normalizeAndValidateDeleteKey("   "))
                .isInstanceOf(AdminSeniorRulesService.ValidationException.class)
                .hasMessageContaining("Ключ правила не должен быть пустым");
    }
}
