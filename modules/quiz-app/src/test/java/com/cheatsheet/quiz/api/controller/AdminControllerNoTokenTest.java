package com.cheatsheet.quiz.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cheatsheet.quiz.api.dto.ApiError;
import com.cheatsheet.quiz.api.exception.ApiErrorTypes;
import com.cheatsheet.quiz.api.security.SensitiveEndpointAccessService;
import com.cheatsheet.quiz.config.AppProperties;
import com.cheatsheet.quiz.service.AdminMaintenanceService;
import com.cheatsheet.quiz.service.admin.AdminSeniorRulesService;
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
 * Тесты AdminController при незаданном токене — доступ должен быть запрещен.
 */
@WebMvcTest(AdminController.class)
@TestPropertySource(properties = "app.admin-token=")
@Import(AdminSeniorRulesService.class)
class AdminControllerNoTokenTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AdminMaintenanceService adminMaintenanceService;

    @MockBean
    SensitiveEndpointAccessService accessService;

    @MockBean
    AppProperties appProperties;

    @BeforeEach
    void setUpForbiddenResponse() {
        ApiError forbiddenError = new ApiError(403, ApiErrorTypes.FORBIDDEN, "Операция запрещена: app.admin-token не настроен", null);
        when(accessService.buildForbiddenResponse(any()))
                .thenReturn(ResponseEntity.status(403).contentType(MediaType.APPLICATION_JSON).body(forbiddenError));
    }

    @Test
    void clearOptionsWithoutConfiguredTokenReturnsForbidden() throws Exception {
        when(accessService.isAuthorized(null)).thenReturn(false);

        mockMvc.perform(post("/api/admin/options/clear"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").exists());

        verify(adminMaintenanceService, org.mockito.Mockito.never()).clearOptions();
    }

    @Test
    void getSeniorRulesWithoutConfiguredTokenReturnsForbidden() throws Exception {
        when(accessService.isAuthorized(null)).thenReturn(false);

        mockMvc.perform(get("/api/admin/senior-rules"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getSeniorRulesHelpWithoutConfiguredTokenReturnsForbidden() throws Exception {
        when(accessService.isAuthorized(null)).thenReturn(false);

        mockMvc.perform(get("/api/admin/senior-rules/help"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getSeniorRuleKeysWithoutConfiguredTokenReturnsForbidden() throws Exception {
        when(accessService.isAuthorized(null)).thenReturn(false);

        mockMvc.perform(get("/api/admin/senior-rules/keys"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void getSeniorRuleCatalogWithoutConfiguredTokenReturnsForbidden() throws Exception {
        when(accessService.isAuthorized(null)).thenReturn(false);

        mockMvc.perform(get("/api/admin/senior-rules/catalog"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void putSeniorRulesWithoutConfiguredTokenReturnsForbidden() throws Exception {
        when(accessService.isAuthorized(null)).thenReturn(false);

        mockMvc.perform(put("/api/admin/senior-rules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"spring-transactional\":5}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void patchSeniorRulesWithoutConfiguredTokenReturnsForbidden() throws Exception {
        when(accessService.isAuthorized(null)).thenReturn(false);

        mockMvc.perform(patch("/api/admin/senior-rules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"spring-transactional\":null}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void deleteSeniorRuleWithoutConfiguredTokenReturnsForbidden() throws Exception {
        when(accessService.isAuthorized(null)).thenReturn(false);

        mockMvc.perform(delete("/api/admin/senior-rules/spring-transactional"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").exists());
    }
}
