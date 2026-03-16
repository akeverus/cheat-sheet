package com.cheatsheet.quiz.feature.export.controller;

import com.cheatsheet.quiz.api.security.SensitiveEndpointAccessService;
import com.cheatsheet.quiz.feature.export.usecase.ExportApiService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

/**
 * REST-контроллер для экспорта прогресса тестирования.
 *
 * <p>Поддерживает форматы JSON и CSV. Эндпоинт: {@code GET /export?format=json|csv}.</p>
 *
 * <p>Формирование payload и валидация параметров делегированы в {@link ExportApiService}.</p>
 */
@RestController
@RequestMapping("/export")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExportController {
    ExportApiService exportApiService;

    /**
     * Экспортирует прогресс по всем вопросам.
     *
     * @param format формат экспорта ({@code json} или {@code csv}, по умолчанию {@code json})
     * @return файл экспорта в виде HTTP-ответа с заголовком {@code Content-Disposition}
     */
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, "text/csv"})
    public ResponseEntity<?> export(
            @RequestHeader(value = SensitiveEndpointAccessService.ADMIN_TOKEN_HEADER, required = false) String token,
            @RequestParam(value = "format", defaultValue = "json") String format
    ) {
        ExportApiService.ExportResult result = exportApiService.export(token, format);
        if (!result.success()) {
            if (result.status().is5xxServerError()) {
                log.error("Ошибка сериализации экспорта в JSON");
            }
            return ResponseEntity.status(result.status())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(result.error());
        }

        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(result.filename(), StandardCharsets.UTF_8)
                .build();
        return ResponseEntity.ok()
                .contentType(result.contentType())
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .body(result.body());
    }

}
