package com.cheatsheet.quiz.api.controller;

import com.cheatsheet.quiz.api.dto.response.AdminOperationResult;
import com.cheatsheet.quiz.api.dto.response.AdminResetAllResult;
import com.cheatsheet.quiz.api.security.SensitiveEndpointAccessService;
import com.cheatsheet.quiz.service.AdminMaintenanceService;
import com.cheatsheet.quiz.service.admin.AdminSeniorRulesService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST-эндпоинты для административных операций.
 *
 * <p>Используются для перегенерации вариантов ответов при смене AI-провайдера
 * или после исправления fallback-логики.</p>
 *
 * <p>Защищены токеном: передаётся через заголовок {@code X-Admin-Token}.</p>
 */
@RestController
@RequestMapping("/api/admin")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AdminController {
    private static final Map<String, String> SENIOR_RULES_HELP_EXAMPLES = Map.of(
            "get", "curl -H \"X-Admin-Token: <token>\" http://localhost:8080/api/admin/senior-rules",
            "put", "curl -X PUT -H \"X-Admin-Token: <token>\" -H \"Content-Type: application/json\" -d '{\"spring-transactional\":8}' http://localhost:8080/api/admin/senior-rules",
            "patch", "curl -X PATCH -H \"X-Admin-Token: <token>\" -H \"Content-Type: application/json\" -d '{\"spring-transactional\":9,\"sql-null-semantics\":null}' http://localhost:8080/api/admin/senior-rules",
            "delete", "curl -X DELETE -H \"X-Admin-Token: <token>\" http://localhost:8080/api/admin/senior-rules/spring-transactional"
    );

    AdminMaintenanceService adminMaintenanceService;
    AdminSeniorRulesService adminSeniorRulesService;
    SensitiveEndpointAccessService accessService;

    public AdminController(
            AdminMaintenanceService adminMaintenanceService,
            AdminSeniorRulesService adminSeniorRulesService,
            SensitiveEndpointAccessService accessService
    ) {
        this.adminMaintenanceService = adminMaintenanceService;
        this.adminSeniorRulesService = adminSeniorRulesService;
        this.accessService = accessService;
    }

    /**
     * Очищает все варианты ответов из БД и кэша.
     *
     * <p>После вызова варианты будут сгенерированы заново при следующем обращении к каждому вопросу.</p>
     *
     * <p>Пример: {@code curl -X POST -H "X-Admin-Token: secret" http://localhost:8080/api/admin/options/clear}</p>
     *
     * @param token значение заголовка X-Admin-Token
     * @return 200 + JSON deleted, message; 403 при неверном/отсутствующем токене
     */
    @PostMapping("/options/clear")
    public ResponseEntity<?> clearOptions(
            @RequestHeader(value = SensitiveEndpointAccessService.ADMIN_TOKEN_HEADER, required = false) String token
    ) {
        ResponseEntity<?> forbidden = accessService.forbiddenIfUnauthorized(token, "очистки вариантов ответов");
        if (forbidden != null) {
            return forbidden;
        }
        int deleted = adminMaintenanceService.clearOptions();
        log.info("Очищены варианты ответов: удалено {} записей, кэш сброшен", deleted);
        return ResponseEntity.ok(new AdminOperationResult(
                "Варианты ответов очищены. При следующем показе вопросов будут сгенерированы заново.",
                Map.of("deleted", deleted)
        ));
    }

    @GetMapping("/senior-rules")
    public ResponseEntity<?> getSeniorRulePriorities(
            @RequestHeader(value = SensitiveEndpointAccessService.ADMIN_TOKEN_HEADER, required = false) String token
    ) {
        ResponseEntity<?> forbidden = accessService.forbiddenIfUnauthorized(token, "чтения senior rule priorities");
        if (forbidden != null) {
            return forbidden;
        }
        Map<String, Integer> sorted = adminSeniorRulesService.getOverridesSorted();
        return ResponseEntity.ok(new AdminOperationResult(
                "Текущие переопределения приоритетов Senior-правил.",
                Map.of("overrides", sorted, "size", sorted.size())
        ));
    }

    @GetMapping("/senior-rules/help")
    public ResponseEntity<?> getSeniorRulesHelp(
            @RequestHeader(value = SensitiveEndpointAccessService.ADMIN_TOKEN_HEADER, required = false) String token
    ) {
        ResponseEntity<?> forbidden = accessService.forbiddenIfUnauthorized(token, "чтения senior rules help");
        if (forbidden != null) {
            return forbidden;
        }
        return ResponseEntity.ok(new AdminOperationResult(
                "Примеры управления приоритетами Senior-правил.",
                Map.of("examples", SENIOR_RULES_HELP_EXAMPLES)
        ));
    }

    @GetMapping("/senior-rules/keys")
    public ResponseEntity<?> getSeniorRuleKeys(
            @RequestHeader(value = SensitiveEndpointAccessService.ADMIN_TOKEN_HEADER, required = false) String token,
            @RequestParam(value = "prefix", required = false) String prefix,
            @RequestParam(value = "q", required = false) String query
    ) {
        ResponseEntity<?> forbidden = accessService.forbiddenIfUnauthorized(token, "чтения senior rule keys");
        if (forbidden != null) {
            return forbidden;
        }
        return ResponseEntity.ok(new AdminOperationResult(
                "Доступные ключи Senior-правил.",
                adminSeniorRulesService.getKeysPayload(prefix, query)
        ));
    }

    @GetMapping("/senior-rules/catalog")
    public ResponseEntity<?> getSeniorRuleCatalog(
            @RequestHeader(value = SensitiveEndpointAccessService.ADMIN_TOKEN_HEADER, required = false) String token,
            @RequestParam(value = "prefix", required = false) String prefix,
            @RequestParam(value = "q", required = false) String query
    ) {
        ResponseEntity<?> forbidden = accessService.forbiddenIfUnauthorized(token, "чтения senior rule catalog");
        if (forbidden != null) {
            return forbidden;
        }
        return ResponseEntity.ok(new AdminOperationResult(
                "Полный каталог Senior-правил.",
                adminSeniorRulesService.getCatalogPayload(prefix, query)
        ));
    }

    @PutMapping("/senior-rules")
    public ResponseEntity<?> updateSeniorRulePriorities(
            @RequestHeader(value = SensitiveEndpointAccessService.ADMIN_TOKEN_HEADER, required = false) String token,
            @RequestBody(required = false) Map<String, Integer> overrides
    ) {
        ResponseEntity<?> forbidden = accessService.forbiddenIfUnauthorized(token, "обновления senior rule priorities");
        if (forbidden != null) {
            return forbidden;
        }
        Map<String, Object> payload = adminSeniorRulesService.replaceOverrides(overrides);
        @SuppressWarnings("unchecked")
        Map<String, Integer> sorted = (Map<String, Integer>) payload.get("overrides");

        log.info("Обновлены senior rule priorities: {} записей", sorted.size());
        return ResponseEntity.ok(new AdminOperationResult(
                "Переопределения приоритетов Senior-правил обновлены.",
                payload
        ));
    }

    @PatchMapping("/senior-rules")
    public ResponseEntity<?> patchSeniorRulePriorities(
            @RequestHeader(value = SensitiveEndpointAccessService.ADMIN_TOKEN_HEADER, required = false) String token,
            @RequestBody(required = false) Map<String, Integer> updates
    ) {
        ResponseEntity<?> forbidden = accessService.forbiddenIfUnauthorized(token, "patch senior rule priorities");
        if (forbidden != null) {
            return forbidden;
        }
        AdminSeniorRulesService.PatchResult patchResult = adminSeniorRulesService.patchOverrides(updates);
        log.info("Patch senior rule priorities: applied={}, removed={}, total={}",
                patchResult.applied(), patchResult.removed(), patchResult.size());
        return ResponseEntity.ok(new AdminOperationResult(
                (updates == null || updates.isEmpty())
                        ? "Patch применён: изменений нет."
                        : "Patch переопределений Senior-правил применён.",
                Map.of(
                        "overrides", patchResult.overrides(),
                        "size", patchResult.size(),
                        "applied", patchResult.applied(),
                        "removed", patchResult.removed()
                )
        ));
    }

    @DeleteMapping("/senior-rules/{key}")
    public ResponseEntity<?> deleteSeniorRulePriority(
            @RequestHeader(value = SensitiveEndpointAccessService.ADMIN_TOKEN_HEADER, required = false) String token,
            @PathVariable("key") String key
    ) {
        ResponseEntity<?> forbidden = accessService.forbiddenIfUnauthorized(token, "удаления senior rule priority");
        if (forbidden != null) {
            return forbidden;
        }
        AdminSeniorRulesService.DeleteResult result = adminSeniorRulesService.deleteOverride(key);
        return ResponseEntity.ok(new AdminOperationResult(
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

    /**
     * Полный сброс всех AI-сгенерированных артефактов:
     * <ul>
     *   <li>Все варианты ответов ({@code answer_options})</li>
     *   <li>Все подсказки ({@code question_hints})</li>
     *   <li>Все Mermaid-диаграммы ({@code diagram_mermaid = NULL})</li>
     *   <li>Сброс счётчиков перегенерации ({@code regen_count = 0})</li>
     * </ul>
     *
     * <p>После вызова все артефакты будут пересозданы AI при следующем показе каждого вопроса.</p>
     *
     * <p>Пример: {@code curl -X POST -H "X-Admin-Token: secret123" http://localhost:8080/api/admin/reset-all}</p>
     *
     * @param token значение заголовка X-Admin-Token
     * @return 200 + JSON с деталями удаления; 403 при неверном/отсутствующем токене
     */
    @PostMapping("/reset-all")
    public ResponseEntity<?> resetAll(
            @RequestHeader(value = SensitiveEndpointAccessService.ADMIN_TOKEN_HEADER, required = false) String token
    ) {
        ResponseEntity<?> forbidden = accessService.forbiddenIfUnauthorized(token, "полного сброса артефактов");
        if (forbidden != null) {
            return forbidden;
        }

        AdminMaintenanceService.ResetAllResult result = adminMaintenanceService.resetAll();

        log.info("Полный сброс артефактов: options={}, hints={}, diagrams={}, regenCount={}",
                result.deletedOptions(), result.deletedHints(), result.clearedDiagrams(), result.resetRegenCount());

        return ResponseEntity.ok(new AdminResetAllResult(
                result.deletedOptions(),
                result.deletedHints(),
                result.clearedDiagrams(),
                result.resetRegenCount(),
                "Все AI-артефакты сброшены. Варианты, подсказки и диаграммы будут пересозданы при следующем показе."
        ));
    }

}
