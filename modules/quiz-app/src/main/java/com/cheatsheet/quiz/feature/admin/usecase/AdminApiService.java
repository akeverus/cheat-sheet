package com.cheatsheet.quiz.feature.admin.usecase;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import com.cheatsheet.quiz.common.model.ApiError;
import com.cheatsheet.quiz.feature.admin.dto.response.AdminOperationResult;
import com.cheatsheet.quiz.feature.admin.dto.response.AdminResetAllResult;
import com.cheatsheet.quiz.api.security.SensitiveEndpointAccessService;
import com.cheatsheet.quiz.feature.admin.service.AdminMaintenanceService;
import com.cheatsheet.quiz.service.admin.AdminSeniorRulesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import lombok.Builder;

import java.util.Map;

/**
 * Use-case orchestration для административных API endpoint-ов.
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminApiService {

    private static final Map<String, String> SENIOR_RULES_HELP_EXAMPLES = Map.of(
            "get", "curl -H \"X-Admin-Token: <token>\" http://localhost:8080/api/admin/senior-rules",
            "put", "curl -X PUT -H \"X-Admin-Token: <token>\" -H \"Content-Type: application/json\" -d '{\"spring-transactional\":8}' http://localhost:8080/api/admin/senior-rules",
            "patch", "curl -X PATCH -H \"X-Admin-Token: <token>\" -H \"Content-Type: application/json\" -d '{\"spring-transactional\":9,\"sql-null-semantics\":null}' http://localhost:8080/api/admin/senior-rules",
            "delete", "curl -X DELETE -H \"X-Admin-Token: <token>\" http://localhost:8080/api/admin/senior-rules/spring-transactional"
    );

    AdminMaintenanceService adminMaintenanceService;
    AdminSeniorRulesService adminSeniorRulesService;
    SensitiveEndpointAccessService accessService;

    public AdminApiResult clearOptions(String token) {
        AdminApiResult forbidden = forbiddenIfUnauthorized(token, "очистки вариантов ответов");
        if (forbidden != null) {
            return forbidden;
        }
        int deleted = adminMaintenanceService.clearOptions();
        return AdminApiResult.ok(new AdminOperationResult(
                "Варианты ответов очищены. При следующем показе вопросов будут сгенерированы заново.",
                Map.of("deleted", deleted)
        ));
    }

    public AdminApiResult getSeniorRulePriorities(String token) {
        AdminApiResult forbidden = forbiddenIfUnauthorized(token, "чтения senior rule priorities");
        if (forbidden != null) {
            return forbidden;
        }
        AdminSeniorRulesService.OverridesPayload result = adminSeniorRulesService.getOverridesPayload();
        return AdminApiResult.ok(new AdminOperationResult(
                "Текущие переопределения приоритетов Senior-правил.",
                result
        ));
    }

    public AdminApiResult getSeniorRulesHelp(String token) {
        AdminApiResult forbidden = forbiddenIfUnauthorized(token, "чтения senior rules help");
        if (forbidden != null) {
            return forbidden;
        }
        return AdminApiResult.ok(new AdminOperationResult(
                "Примеры управления приоритетами Senior-правил.",
                Map.of("examples", SENIOR_RULES_HELP_EXAMPLES)
        ));
    }

    public AdminApiResult getSeniorRuleKeys(String token, String prefix, String query) {
        AdminApiResult forbidden = forbiddenIfUnauthorized(token, "чтения senior rule keys");
        if (forbidden != null) {
            return forbidden;
        }
        return AdminApiResult.ok(new AdminOperationResult(
                "Доступные ключи Senior-правил.",
                adminSeniorRulesService.getKeysPayload(prefix, query)
        ));
    }

    public AdminApiResult getSeniorRuleCatalog(String token, String prefix, String query) {
        AdminApiResult forbidden = forbiddenIfUnauthorized(token, "чтения senior rule catalog");
        if (forbidden != null) {
            return forbidden;
        }
        return AdminApiResult.ok(new AdminOperationResult(
                "Полный каталог Senior-правил.",
                adminSeniorRulesService.getCatalogPayload(prefix, query)
        ));
    }

    public AdminApiResult updateSeniorRulePriorities(String token, Map<String, Integer> overrides) {
        AdminApiResult forbidden = forbiddenIfUnauthorized(token, "обновления senior rule priorities");
        if (forbidden != null) {
            return forbidden;
        }
        AdminSeniorRulesService.ReplaceResult result = adminSeniorRulesService.replaceOverrides(overrides);
        return AdminApiResult.ok(new AdminOperationResult(
                "Переопределения приоритетов Senior-правил обновлены.",
                result
        ));
    }

    public AdminApiResult patchSeniorRulePriorities(String token, Map<String, Integer> updates) {
        AdminApiResult forbidden = forbiddenIfUnauthorized(token, "patch senior rule priorities");
        if (forbidden != null) {
            return forbidden;
        }
        AdminSeniorRulesService.PatchResult patchResult = adminSeniorRulesService.patchOverrides(updates);
        String message = CollectionUtils.isEmpty(updates)
                ? "Patch применён: изменений нет."
                : "Patch переопределений Senior-правил применён.";
        return AdminApiResult.ok(new AdminOperationResult(
                message,
                Map.of(
                        "overrides", patchResult.overrides(),
                        "size", patchResult.size(),
                        "applied", patchResult.applied(),
                        "removed", patchResult.removed()
                )
        ));
    }

    public AdminApiResult deleteSeniorRulePriority(String token, String key) {
        AdminApiResult forbidden = forbiddenIfUnauthorized(token, "удаления senior rule priority");
        if (forbidden != null) {
            return forbidden;
        }
        AdminSeniorRulesService.DeleteResult result = adminSeniorRulesService.deleteOverride(key);
        return AdminApiResult.ok(new AdminOperationResult(
                result.deleted()
                        ? "Переопределение приоритета удалено."
                        : "Переопределение не найдено, изменений нет.",
                Map.of(
                        "key", result.key(),
                        "deleted", result.deleted(),
                        "overrides", result.overrides(),
                        "size", result.size()
                )
        ));
    }

    public AdminApiResult resetAll(String token) {
        AdminApiResult forbidden = forbiddenIfUnauthorized(token, "полного сброса артефактов");
        if (forbidden != null) {
            return forbidden;
        }
        AdminMaintenanceService.ResetAllResult result = adminMaintenanceService.resetAll();
        return AdminApiResult.ok(new AdminResetAllResult(
                result.deletedOptions(),
                result.deletedHints(),
                result.clearedDiagrams(),
                result.resetRegenCount(),
                "Все AI-артефакты сброшены. Варианты, подсказки и диаграммы будут пересозданы при следующем показе."
        ));
    }

    private AdminApiResult forbiddenIfUnauthorized(String token, String action) {
        ResponseEntity<ApiError> forbidden = accessService.forbiddenIfUnauthorized(token, action);
        if (forbidden == null || forbidden.getBody() == null) {
            return null;
        }
        return new AdminApiResult(HttpStatus.FORBIDDEN, forbidden.getBody());
    }

    @Builder(toBuilder = true)
    public record AdminApiResult(HttpStatus status, Object body) {
        public static AdminApiResult ok(Object body) {
            return new AdminApiResult(HttpStatus.OK, body);
        }
    }
}
