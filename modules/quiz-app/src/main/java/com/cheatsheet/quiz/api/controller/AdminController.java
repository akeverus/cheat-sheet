package com.cheatsheet.quiz.api.controller;

import com.cheatsheet.quiz.api.dto.response.AdminOperationResult;
import com.cheatsheet.quiz.api.dto.response.AdminResetAllResult;
import com.cheatsheet.quiz.api.security.SensitiveEndpointAccessService;
import com.cheatsheet.quiz.service.AdminApiService;
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
@Slf4j
public class AdminController {
    private final AdminApiService adminApiService;

    public AdminController(
            AdminApiService adminApiService
    ) {
        this.adminApiService = adminApiService;
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
        return toResponse(adminApiService.clearOptions(token));
    }

    @GetMapping("/senior-rules")
    public ResponseEntity<?> getSeniorRulePriorities(
            @RequestHeader(value = SensitiveEndpointAccessService.ADMIN_TOKEN_HEADER, required = false) String token
    ) {
        return toResponse(adminApiService.getSeniorRulePriorities(token));
    }

    @GetMapping("/senior-rules/help")
    public ResponseEntity<?> getSeniorRulesHelp(
            @RequestHeader(value = SensitiveEndpointAccessService.ADMIN_TOKEN_HEADER, required = false) String token
    ) {
        return toResponse(adminApiService.getSeniorRulesHelp(token));
    }

    @GetMapping("/senior-rules/keys")
    public ResponseEntity<?> getSeniorRuleKeys(
            @RequestHeader(value = SensitiveEndpointAccessService.ADMIN_TOKEN_HEADER, required = false) String token,
            @RequestParam(value = "prefix", required = false) String prefix,
            @RequestParam(value = "q", required = false) String query
    ) {
        return toResponse(adminApiService.getSeniorRuleKeys(token, prefix, query));
    }

    @GetMapping("/senior-rules/catalog")
    public ResponseEntity<?> getSeniorRuleCatalog(
            @RequestHeader(value = SensitiveEndpointAccessService.ADMIN_TOKEN_HEADER, required = false) String token,
            @RequestParam(value = "prefix", required = false) String prefix,
            @RequestParam(value = "q", required = false) String query
    ) {
        return toResponse(adminApiService.getSeniorRuleCatalog(token, prefix, query));
    }

    @PutMapping("/senior-rules")
    public ResponseEntity<?> updateSeniorRulePriorities(
            @RequestHeader(value = SensitiveEndpointAccessService.ADMIN_TOKEN_HEADER, required = false) String token,
            @RequestBody(required = false) Map<String, Integer> overrides
    ) {
        return toResponse(adminApiService.updateSeniorRulePriorities(token, overrides));
    }

    @PatchMapping("/senior-rules")
    public ResponseEntity<?> patchSeniorRulePriorities(
            @RequestHeader(value = SensitiveEndpointAccessService.ADMIN_TOKEN_HEADER, required = false) String token,
            @RequestBody(required = false) Map<String, Integer> updates
    ) {
        return toResponse(adminApiService.patchSeniorRulePriorities(token, updates));
    }

    @DeleteMapping("/senior-rules/{key}")
    public ResponseEntity<?> deleteSeniorRulePriority(
            @RequestHeader(value = SensitiveEndpointAccessService.ADMIN_TOKEN_HEADER, required = false) String token,
            @PathVariable("key") String key
    ) {
        return toResponse(adminApiService.deleteSeniorRulePriority(token, key));
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
        return toResponse(adminApiService.resetAll(token));
    }

    private ResponseEntity<?> toResponse(AdminApiService.AdminApiResult result) {
        if (result.status().is2xxSuccessful()) {
            Object body = result.body();
            if (body instanceof AdminOperationResult || body instanceof AdminResetAllResult) {
                log.info("admin_endpoint_success status={} bodyType={}", result.status().value(), body.getClass().getSimpleName());
            }
        }
        return ResponseEntity.status(result.status()).body(result.body());
    }

}
