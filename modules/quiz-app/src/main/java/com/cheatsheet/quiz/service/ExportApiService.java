package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.api.dto.ApiError;
import com.cheatsheet.quiz.api.dto.ExportFormats;
import com.cheatsheet.quiz.api.exception.ApiErrorTypes;
import com.cheatsheet.quiz.api.security.SensitiveEndpointAccessService;
import com.cheatsheet.quiz.domain.ProgressExportRow;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Use-case orchestration для endpoint `/export`.
 */
@Service
public class ExportApiService {

    private static final List<String> ALLOWED_FORMATS = ExportFormats.ALL;

    private final ExportService exportService;
    private final SensitiveEndpointAccessService accessService;

    public ExportApiService(
            ExportService exportService,
            SensitiveEndpointAccessService accessService
    ) {
        this.exportService = exportService;
        this.accessService = accessService;
    }

    public ExportResult export(String token, String format) {
        var forbidden = accessService.forbiddenIfUnauthorized(token, "export прогресса");
        if (forbidden != null && forbidden.getBody() != null) {
            return ExportResult.error(HttpStatus.FORBIDDEN, forbidden.getBody());
        }

        String normalizedFormat = format == null ? StringUtils.EMPTY : format.strip().toLowerCase();
        if (!ALLOWED_FORMATS.contains(normalizedFormat)) {
            ApiError error = new ApiError(
                    HttpStatus.BAD_REQUEST.value(),
                    ApiErrorTypes.UNSUPPORTED_FORMAT,
                    "Формат экспорта не поддерживается. Допустимые значения: json, csv.",
                    ALLOWED_FORMATS
            );
            return ExportResult.error(HttpStatus.BAD_REQUEST, error);
        }

        List<ProgressExportRow> rows = exportService.loadProgressRows();
        if (ExportFormats.CSV.equals(normalizedFormat)) {
            byte[] body = exportService.exportAsCsv(rows).getBytes(StandardCharsets.UTF_8);
            MediaType contentType = new MediaType("text", "csv", StandardCharsets.UTF_8);
            return ExportResult.success(body, contentType, "quiz-progress.csv");
        }

        try {
            byte[] body = exportService.exportAsJson(rows);
            return ExportResult.success(body, MediaType.APPLICATION_JSON, "quiz-progress.json");
        } catch (JsonProcessingException e) {
            ApiError error = new ApiError(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ApiErrorTypes.EXPORT_SERIALIZATION_ERROR,
                    "Ошибка сериализации экспорта в JSON",
                    null
            );
            return ExportResult.error(HttpStatus.INTERNAL_SERVER_ERROR, error);
        }
    }

    public record ExportResult(
            HttpStatus status,
            ApiError error,
            byte[] body,
            MediaType contentType,
            String filename
    ) {
        public boolean success() {
            return error == null;
        }

        static ExportResult error(HttpStatus status, ApiError error) {
            return new ExportResult(status, error, null, MediaType.APPLICATION_JSON, null);
        }

        static ExportResult success(byte[] body, MediaType contentType, String filename) {
            return new ExportResult(HttpStatus.OK, null, body, contentType, filename);
        }
    }
}
