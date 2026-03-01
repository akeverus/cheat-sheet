package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.api.dto.ApiError;
import com.cheatsheet.quiz.api.exception.ApiErrorTypes;
import com.cheatsheet.quiz.api.security.SensitiveEndpointAccessService;
import com.cheatsheet.quiz.service.admin.AdminSeniorRulesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminApiServiceTest {

    @Mock
    private AdminMaintenanceService adminMaintenanceService;
    @Mock
    private AdminSeniorRulesService adminSeniorRulesService;
    @Mock
    private SensitiveEndpointAccessService accessService;

    private AdminApiService service;

    @BeforeEach
    void setUp() {
        service = new AdminApiService(adminMaintenanceService, adminSeniorRulesService, accessService);
    }

    @Test
    void clearOptionsReturnsForbiddenWhenUnauthorized() {
        ApiError forbidden = new ApiError(403, ApiErrorTypes.FORBIDDEN, "Недостаточно прав", null);
        when(accessService.forbiddenIfUnauthorized("bad", "очистки вариантов ответов"))
                .thenReturn(ResponseEntity.status(403).body(forbidden));

        AdminApiService.AdminApiResult result = service.clearOptions("bad");

        assertThat(result.status()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(result.body()).isEqualTo(forbidden);
        verify(adminMaintenanceService, never()).clearOptions();
    }

    @Test
    void clearOptionsReturnsOperationResultWhenAuthorized() {
        when(accessService.forbiddenIfUnauthorized("ok", "очистки вариантов ответов")).thenReturn(null);
        when(adminMaintenanceService.clearOptions()).thenReturn(12);

        AdminApiService.AdminApiResult result = service.clearOptions("ok");

        assertThat(result.status()).isEqualTo(HttpStatus.OK);
        assertThat(result.body()).isInstanceOf(com.cheatsheet.quiz.api.dto.response.AdminOperationResult.class);
        com.cheatsheet.quiz.api.dto.response.AdminOperationResult body =
                (com.cheatsheet.quiz.api.dto.response.AdminOperationResult) result.body();
        assertThat(body.details()).isEqualTo(Map.of("deleted", 12));
        verify(adminMaintenanceService).clearOptions();
    }

    @Test
    void patchSeniorRulesReturnsNoChangesMessageForEmptyUpdates() {
        when(accessService.forbiddenIfUnauthorized("ok", "patch senior rule priorities")).thenReturn(null);
        AdminSeniorRulesService.PatchResult patchResult =
                new AdminSeniorRulesService.PatchResult(Map.of(), 0, 0, 0);
        when(adminSeniorRulesService.patchOverrides(Map.of())).thenReturn(patchResult);

        AdminApiService.AdminApiResult result = service.patchSeniorRulePriorities("ok", Map.of());

        assertThat(result.status()).isEqualTo(HttpStatus.OK);
        assertThat(result.body()).isInstanceOf(com.cheatsheet.quiz.api.dto.response.AdminOperationResult.class);
        com.cheatsheet.quiz.api.dto.response.AdminOperationResult body =
                (com.cheatsheet.quiz.api.dto.response.AdminOperationResult) result.body();
        assertThat(body.message()).contains("изменений нет");
        verify(adminSeniorRulesService).patchOverrides(Map.of());
    }
}
