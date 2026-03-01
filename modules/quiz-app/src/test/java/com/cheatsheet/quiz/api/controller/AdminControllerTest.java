package com.cheatsheet.quiz.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cheatsheet.quiz.api.dto.ApiError;
import com.cheatsheet.quiz.api.exception.ApiErrorTypes;
import com.cheatsheet.quiz.api.security.SensitiveEndpointAccessService;
import com.cheatsheet.quiz.config.AppProperties;
import com.cheatsheet.quiz.service.AdminMaintenanceService;
import com.cheatsheet.quiz.service.admin.AdminSeniorRulesService;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Тесты авторизации и логики очистки вариантов ответа для {@link AdminController}.
 */
@WebMvcTest(AdminController.class)
@TestPropertySource(properties = "app.admin-token=test-secret-token")
@Import(AdminSeniorRulesService.class)
class AdminControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AdminMaintenanceService adminMaintenanceService;

    @MockBean
    SensitiveEndpointAccessService accessService;

    @MockBean
    AppProperties appProperties;

    AppProperties.Interview interview;

    @BeforeEach
    void setUpForbiddenResponse() {
        ApiError forbiddenError = new ApiError(403, ApiErrorTypes.FORBIDDEN, "Недостаточно прав", null);
        when(accessService.buildForbiddenResponse(any()))
                .thenReturn(ResponseEntity.status(403).contentType(MediaType.APPLICATION_JSON).body(forbiddenError));
        interview = new AppProperties.Interview();
        interview.setSeniorRulePriorityOverrides(new ConcurrentHashMap<>());
        when(appProperties.getInterview()).thenReturn(interview);
    }

    @Test
    void clearOptionsWithValidTokenReturnsOk() throws Exception {
        when(adminMaintenanceService.clearOptions()).thenReturn(42);
        when(accessService.isAuthorized("test-secret-token")).thenReturn(true);

        mockMvc.perform(post("/api/admin/options/clear")
                        .header("X-Admin-Token", "test-secret-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.details.deleted").value(42));

        verify(adminMaintenanceService).clearOptions();
    }

    @Test
    void clearOptionsWithInvalidTokenReturnsForbidden() throws Exception {
        when(accessService.isAuthorized("wrong-token")).thenReturn(false);

        mockMvc.perform(post("/api/admin/options/clear")
                        .header("X-Admin-Token", "wrong-token"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").exists());

        verify(adminMaintenanceService, never()).clearOptions();
    }

    @Test
    void clearOptionsWithMissingTokenReturnsForbidden() throws Exception {
        when(accessService.isAuthorized(null)).thenReturn(false);

        mockMvc.perform(post("/api/admin/options/clear"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").exists());

        verify(adminMaintenanceService, never()).clearOptions();
    }

    @Test
    void getSeniorRulesWithValidTokenReturnsConfiguredOverrides() throws Exception {
        interview.getSeniorRulePriorityOverrides().put("spring-transactional", 7);
        when(accessService.isAuthorized("test-secret-token")).thenReturn(true);

        mockMvc.perform(get("/api/admin/senior-rules")
                        .header("X-Admin-Token", "test-secret-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.details.size").value(1))
                .andExpect(jsonPath("$.details.overrides.spring-transactional").value(7));
    }

    @Test
    void getSeniorRulesHelpWithValidTokenReturnsExamples() throws Exception {
        when(accessService.isAuthorized("test-secret-token")).thenReturn(true);

        mockMvc.perform(get("/api/admin/senior-rules/help")
                        .header("X-Admin-Token", "test-secret-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.details.examples.get").exists())
                .andExpect(jsonPath("$.details.examples.put").exists())
                .andExpect(jsonPath("$.details.examples.patch").exists())
                .andExpect(jsonPath("$.details.examples.delete").exists());
    }

    @Test
    void getSeniorRuleKeysWithValidTokenReturnsCatalogKeys() throws Exception {
        when(accessService.isAuthorized("test-secret-token")).thenReturn(true);

        mockMvc.perform(get("/api/admin/senior-rules/keys")
                        .header("X-Admin-Token", "test-secret-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.details.size").value(30))
                .andExpect(jsonPath("$.details.keys[0]").exists());
    }

    @Test
    void getSeniorRuleKeysWithPrefixReturnsFilteredSubset() throws Exception {
        when(accessService.isAuthorized("test-secret-token")).thenReturn(true);

        mockMvc.perform(get("/api/admin/senior-rules/keys")
                        .header("X-Admin-Token", "test-secret-token")
                        .param("prefix", "sql-"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.details.prefix").value("sql-"))
                .andExpect(jsonPath("$.details.size").value(6))
                .andExpect(jsonPath("$.details.keys[0]").value("sql-deadlock"));
    }

    @Test
    void getSeniorRuleKeysWithQueryReturnsContainsSubset() throws Exception {
        when(accessService.isAuthorized("test-secret-token")).thenReturn(true);

        mockMvc.perform(get("/api/admin/senior-rules/keys")
                        .header("X-Admin-Token", "test-secret-token")
                        .param("q", "transaction"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.details.q").value("transaction"))
                .andExpect(jsonPath("$.details.size").value(2))
                .andExpect(jsonPath("$.details.keys[0]").value("spring-transactional"));
    }

    @Test
    void getSeniorRuleCatalogWithValidTokenReturnsDetailedRules() throws Exception {
        when(accessService.isAuthorized("test-secret-token")).thenReturn(true);

        mockMvc.perform(get("/api/admin/senior-rules/catalog")
                        .header("X-Admin-Token", "test-secret-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.details.size").value(30))
                .andExpect(jsonPath("$.details.rules[0].key").exists())
                .andExpect(jsonPath("$.details.rules[0].scopeTokens").isArray())
                .andExpect(jsonPath("$.details.rules[0].questionTokens").isArray())
                .andExpect(jsonPath("$.details.rules[0].defaultPriority").exists())
                .andExpect(jsonPath("$.details.rules[0].trapHint").exists());
    }

    @Test
    void getSeniorRuleCatalogWithPrefixReturnsFilteredSubset() throws Exception {
        when(accessService.isAuthorized("test-secret-token")).thenReturn(true);

        mockMvc.perform(get("/api/admin/senior-rules/catalog")
                        .header("X-Admin-Token", "test-secret-token")
                        .param("prefix", "spring-"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.details.prefix").value("spring-"))
                .andExpect(jsonPath("$.details.size").value(5))
                .andExpect(jsonPath("$.details.rules[0].key").value("spring-autoconfiguration"));
    }

    @Test
    void getSeniorRuleCatalogWithPrefixAndQueryReturnsIntersection() throws Exception {
        when(accessService.isAuthorized("test-secret-token")).thenReturn(true);

        mockMvc.perform(get("/api/admin/senior-rules/catalog")
                        .header("X-Admin-Token", "test-secret-token")
                        .param("prefix", "sql-")
                        .param("q", "transaction"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.details.prefix").value("sql-"))
                .andExpect(jsonPath("$.details.q").value("transaction"))
                .andExpect(jsonPath("$.details.size").value(1))
                .andExpect(jsonPath("$.details.rules[0].key").value("sql-transaction-isolation"));
    }

    @Test
    void getSeniorRuleCatalogWithQueryMatchesTrapHintAndNotOnlyKey() throws Exception {
        when(accessService.isAuthorized("test-secret-token")).thenReturn(true);

        mockMvc.perform(get("/api/admin/senior-rules/catalog")
                        .header("X-Admin-Token", "test-secret-token")
                        .param("q", "READONLY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.details.q").value("readonly"))
                .andExpect(jsonPath("$.details.size").value(1))
                .andExpect(jsonPath("$.details.rules[0].key").value("spring-transactional"));
    }

    @Test
    void putSeniorRulesWithValidTokenReplacesOverrides() throws Exception {
        interview.getSeniorRulePriorityOverrides().put("old-rule", 1);
        when(accessService.isAuthorized("test-secret-token")).thenReturn(true);

        mockMvc.perform(put("/api/admin/senior-rules")
                        .header("X-Admin-Token", "test-secret-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "spring-transactional": 8,
                                  "sql-null-semantics": 6
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.details.size").value(2))
                .andExpect(jsonPath("$.details.overrides.spring-transactional").value(8))
                .andExpect(jsonPath("$.details.overrides.sql-null-semantics").value(6));

        assertThat(interview.getSeniorRulePriorityOverrides())
                .containsEntry("spring-transactional", 8)
                .containsEntry("sql-null-semantics", 6)
                .doesNotContainKey("old-rule");
    }

    @Test
    void putSeniorRulesWithInvalidPayloadReturnsBadRequest() throws Exception {
        when(accessService.isAuthorized("test-secret-token")).thenReturn(true);

        mockMvc.perform(put("/api/admin/senior-rules")
                        .header("X-Admin-Token", "test-secret-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "spring-transactional": -1
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION_ERROR"));
    }

    @Test
    void putSeniorRulesWithUnknownKeyReturnsBadRequest() throws Exception {
        when(accessService.isAuthorized("test-secret-token")).thenReturn(true);

        mockMvc.perform(put("/api/admin/senior-rules")
                        .header("X-Admin-Token", "test-secret-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "unknown-rule": 3
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION_ERROR"));
    }

    @Test
    void patchSeniorRulesCanUpdateAndRemoveEntries() throws Exception {
        interview.getSeniorRulePriorityOverrides().put("spring-transactional", 5);
        interview.getSeniorRulePriorityOverrides().put("sql-null-semantics", 4);
        when(accessService.isAuthorized("test-secret-token")).thenReturn(true);

        mockMvc.perform(patch("/api/admin/senior-rules")
                        .header("X-Admin-Token", "test-secret-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "spring-transactional": 9,
                                  "sql-null-semantics": null
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.details.applied").value(1))
                .andExpect(jsonPath("$.details.removed").value(1))
                .andExpect(jsonPath("$.details.overrides.spring-transactional").value(9));

        assertThat(interview.getSeniorRulePriorityOverrides())
                .containsEntry("spring-transactional", 9)
                .doesNotContainKey("sql-null-semantics");
    }

    @Test
    void patchSeniorRulesWithInvalidPayloadReturnsBadRequest() throws Exception {
        when(accessService.isAuthorized("test-secret-token")).thenReturn(true);

        mockMvc.perform(patch("/api/admin/senior-rules")
                        .header("X-Admin-Token", "test-secret-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "spring-transactional": -2
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION_ERROR"));
    }

    @Test
    void patchSeniorRulesWithUnknownKeyReturnsBadRequest() throws Exception {
        when(accessService.isAuthorized("test-secret-token")).thenReturn(true);

        mockMvc.perform(patch("/api/admin/senior-rules")
                        .header("X-Admin-Token", "test-secret-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "unknown-rule": 2
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION_ERROR"));
    }

    @Test
    void deleteSeniorRuleRemovesExistingOverride() throws Exception {
        interview.getSeniorRulePriorityOverrides().put("spring-transactional", 7);
        when(accessService.isAuthorized("test-secret-token")).thenReturn(true);

        mockMvc.perform(delete("/api/admin/senior-rules/spring-transactional")
                        .header("X-Admin-Token", "test-secret-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.details.deleted").value(true))
                .andExpect(jsonPath("$.details.key").value("spring-transactional"));

        assertThat(interview.getSeniorRulePriorityOverrides()).doesNotContainKey("spring-transactional");
    }

    @Test
    void deleteSeniorRuleWithUnknownKeyReturnsNoChange() throws Exception {
        when(accessService.isAuthorized("test-secret-token")).thenReturn(true);

        mockMvc.perform(delete("/api/admin/senior-rules/unknown-rule")
                        .header("X-Admin-Token", "test-secret-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.details.deleted").value(false))
                .andExpect(jsonPath("$.details.key").value("unknown-rule"));
    }
}
