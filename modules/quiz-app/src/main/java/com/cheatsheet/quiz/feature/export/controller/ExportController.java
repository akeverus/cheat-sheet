package com.cheatsheet.quiz.feature.export.controller;

import com.cheatsheet.quiz.api.security.SensitiveEndpointAccessService;
import com.cheatsheet.quiz.common.exception.RateLimitExceededException;
import com.cheatsheet.quiz.feature.export.usecase.ExportApiService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

/**
 * REST-контроллер для экспорта прогресса тестирования.
 *
 * <p>Поддерживает форматы JSON и CSV. Эндпоинт: {@code GET /export?format=json|csv}.</p>
 *
 * <p>Доступ гейтит security-цепочка (валидный {@code X-Admin-Token} либо
 * {@code ROLE_ADMIN}); контроллер повторную проверку не делает. Полный дамп
 * прогресса — дорогая операция, поэтому дополнительно действует лимит частоты
 * на клиента.</p>
 */
@RestController
@RequestMapping("/export")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExportController {

    ExportApiService exportApiService;
    SensitiveEndpointAccessService accessService;

    /**
     * Экспортирует прогресс по всем вопросам.
     *
     * @param format формат экспорта ({@code json} или {@code csv}, по умолчанию {@code json})
     * @return файл экспорта в виде HTTP-ответа с заголовком {@code Content-Disposition}
     */
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, "text/csv"})
    public ResponseEntity<byte[]> export(
            @RequestParam(value = "format", defaultValue = "json") String format,
            HttpServletRequest request
    ) {
        if (!accessService.allowSensitiveRequest(request)) {
            throw new RateLimitExceededException(accessService.sensitiveRetryAfterSeconds());
        }

        ExportApiService.ExportResult result = exportApiService.export(format);

        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(result.filename(), StandardCharsets.UTF_8)
                .build();
        return ResponseEntity.ok()
                .contentType(result.contentType())
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .body(result.body());
    }
}
