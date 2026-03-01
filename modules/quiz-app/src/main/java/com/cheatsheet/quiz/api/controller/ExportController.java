package com.cheatsheet.quiz.api.controller;

import com.cheatsheet.quiz.api.security.SensitiveEndpointAccessService;
import com.cheatsheet.quiz.api.dto.ApiError;
import com.cheatsheet.quiz.api.dto.ExportFormats;
import com.cheatsheet.quiz.api.exception.ApiErrorTypes;
import com.cheatsheet.quiz.domain.ProgressExportRow;
import com.cheatsheet.quiz.service.ExportService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * REST-контроллер для экспорта прогресса тестирования.
 *
 * <p>Поддерживает форматы JSON и CSV. Эндпоинт: {@code GET /export?format=json|csv}.</p>
 *
 * <p>Использует общий {@link ObjectMapper}, внедрённый Spring Boot.
 * Для pretty-print экспорта вызывается {@link ObjectMapper#writerWithDefaultPrettyPrinter()}.</p>
 */
@RestController
@RequestMapping("/export")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ExportController {

    /** Допустимые форматы экспорта. */
    private static final List<String> ALLOWED_FORMATS = ExportFormats.ALL;

    /** Запасной JSON при ошибке сериализации ApiError. */
    private static final String FALLBACK_ERROR_JSON = "{\"status\":500,\"type\":\"INTERNAL_ERROR\",\"message\":\"Ошибка формирования ответа\"}";

    /** Сервис экспорта в JSON/CSV. */
    ExportService exportService;

    /** Общий ObjectMapper приложения (для сериализации ApiError). */
    ObjectMapper objectMapper;

    /** Guard для защиты чувствительных endpoint. */
    SensitiveEndpointAccessService accessService;

    /**
     * Экспортирует прогресс по всем вопросам.
     *
     * @param format формат экспорта ({@code json} или {@code csv}, по умолчанию {@code json})
     * @return файл экспорта в виде HTTP-ответа с заголовком {@code Content-Disposition}
     */
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, "text/csv"})
    public ResponseEntity<byte[]> export(
            @RequestHeader(value = SensitiveEndpointAccessService.ADMIN_TOKEN_HEADER, required = false) String token,
            @RequestParam(value = "format", defaultValue = "json") String format
    ) {
        if (!accessService.isAuthorized(token)) {
            ResponseEntity<?> forbidden = accessService.buildForbiddenResponse(token);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(toJsonBytes((ApiError) forbidden.getBody()));
        }

        String normalizedFormat = format == null ? StringUtils.EMPTY : format.strip().toLowerCase();
        if (!ALLOWED_FORMATS.contains(normalizedFormat)) {
            ApiError error = new ApiError(
                    HttpStatus.BAD_REQUEST.value(),
                    ApiErrorTypes.UNSUPPORTED_FORMAT,
                    "Формат экспорта не поддерживается. Допустимые значения: json, csv.",
                    ALLOWED_FORMATS
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(toJsonBytes(error));
        }

        List<ProgressExportRow> rows = exportService.loadProgressRows();
        byte[] body;
        String filename;
        MediaType contentType;

        if (ExportFormats.CSV.equals(normalizedFormat)) {
            body = exportService.exportAsCsv(rows).getBytes(StandardCharsets.UTF_8);
            filename = "quiz-progress.csv";
            contentType = new MediaType("text", "csv", StandardCharsets.UTF_8);
        } else {
            try {
                body = exportService.exportAsJson(rows);
            } catch (JsonProcessingException e) {
                log.error("Ошибка сериализации экспорта в JSON", e);
                ApiError error = new ApiError(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        ApiErrorTypes.EXPORT_SERIALIZATION_ERROR,
                        "Ошибка сериализации экспорта в JSON",
                        null
                );
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(toJsonBytes(error));
            }
            filename = "quiz-progress.json";
            contentType = MediaType.APPLICATION_JSON;
        }

        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(filename, StandardCharsets.UTF_8)
                .build();
        return ResponseEntity.ok()
                .contentType(contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .body(body);
    }

    /**
     * Сериализует объект ошибки в JSON-байты.
     * При ошибке сериализации возвращает захардкоженный JSON.
     */
    private byte[] toJsonBytes(ApiError error) {
        try {
            return objectMapper.writeValueAsString(error).getBytes(StandardCharsets.UTF_8);
        } catch (JsonProcessingException e) {
            log.warn("Не удалось сериализовать ApiError", e);
            return FALLBACK_ERROR_JSON.getBytes(StandardCharsets.UTF_8);
        }
    }

}
