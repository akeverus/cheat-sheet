package com.cheatsheet.quiz.feature.export.usecase;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import com.cheatsheet.quiz.api.constants.ExportFormats;
import com.cheatsheet.quiz.domain.ProgressExportRow;
import com.cheatsheet.quiz.feature.export.exception.ExportSerializationException;
import com.cheatsheet.quiz.feature.export.exception.UnsupportedExportFormatException;
import com.cheatsheet.quiz.feature.export.service.ExportService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Use-case orchestration для endpoint {@code /export}.
 *
 * <p>Доступ гейтит security-цепочка ({@code SecurityConfig#hasSensitiveAccess}),
 * поэтому сервис проверку токена не дублирует. Ошибки бросаются исключениями и
 * приводятся к общему для API формату {@code ProblemDetail} в
 * {@code GlobalExceptionHandler}.</p>
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExportApiService {

    private static final List<String> ALLOWED_FORMATS = ExportFormats.ALL;

    ExportService exportService;

    public ExportResult export(String format) {
        String normalizedFormat = format == null ? StringUtils.EMPTY : format.strip().toLowerCase();
        if (!ALLOWED_FORMATS.contains(normalizedFormat)) {
            throw new UnsupportedExportFormatException(ALLOWED_FORMATS);
        }

        List<ProgressExportRow> rows = exportService.loadProgressRows();
        if (ExportFormats.CSV.equals(normalizedFormat)) {
            byte[] body = exportService.exportAsCsv(rows).getBytes(StandardCharsets.UTF_8);
            MediaType contentType = new MediaType("text", "csv", StandardCharsets.UTF_8);
            return new ExportResult(body, contentType, "quiz-progress.csv");
        }

        try {
            byte[] body = exportService.exportAsJson(rows);
            return new ExportResult(body, MediaType.APPLICATION_JSON, "quiz-progress.json");
        } catch (JsonProcessingException e) {
            throw new ExportSerializationException("Ошибка сериализации экспорта в JSON", e);
        }
    }

    /**
     * Транспорт «тело + как его отдать» от сервиса к контроллеру.
     *
     * <p>Сравнивать и класть в коллекции нельзя: {@code body} — массив, а
     * сгенерированные record'ом {@code equals}/{@code hashCode} сравнивают его по
     * ссылке. Два одинаковых по содержимому экспорта окажутся неравными, и это
     * молча сломало бы дедупликацию или кэш по ключу. Здесь запись живёт ровно
     * один вызов и до сравнения не доходит.</p>
     */
    public record ExportResult(
            byte[] body,
            MediaType contentType,
            String filename
    ) {
    }
}
