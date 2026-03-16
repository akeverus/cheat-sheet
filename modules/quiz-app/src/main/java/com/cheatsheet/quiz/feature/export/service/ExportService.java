package com.cheatsheet.quiz.feature.export.service;

import com.cheatsheet.quiz.domain.ProgressExportRow;
import com.cheatsheet.quiz.persistence.QuestionStatsRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис экспорта прогресса в JSON и CSV.
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExportService {

    /** Разделитель полей в CSV. */
    public static final String CSV_COMMA = ",";
    /** Кавычка в CSV (экранирование). */
    public static final String CSV_QUOTE = "\"";
    /** Перенос строки в CSV. */
    public static final String CSV_NEWLINE = "\n";
    /** Экранированная кавычка в CSV (удвоенная кавычка). */
    public static final String CSV_ESCAPED_QUOTE = "\"\"";

    ObjectMapper objectMapper;
    QuestionStatsRepository questionStatsRepository;

    /**
     * Загружает строки прогресса для экспорта.
     */
    public List<ProgressExportRow> loadProgressRows() {
        return questionStatsRepository.findProgressForExport();
    }

    /**
     * Сериализует строки экспорта в pretty-print JSON.
     *
     * @param rows строки данных
     * @return байты JSON (UTF-8)
     * @throws JsonProcessingException при ошибке сериализации
     */
    public byte[] exportAsJson(List<ProgressExportRow> rows) throws JsonProcessingException {
        return objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(rows)
                .getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    /**
     * Формирует CSV с BOM, заголовком и экранированием полей.
     *
     * @param rows строки данных
     * @return CSV-строка
     */
    public String exportAsCsv(List<ProgressExportRow> rows) {
        String bom = "\uFEFF";
        String header = "slug,topic,correct_count,wrong_count,next_review_at,repetitions";
        String lines = rows.stream()
                .map(r -> escapeCsv(r.slug()) + CSV_COMMA + escapeCsv(r.topic()) + CSV_COMMA
                        + r.correctCount() + CSV_COMMA + r.wrongCount() + CSV_COMMA
                        + r.nextReviewAt() + CSV_COMMA + r.repetitions())
                .collect(Collectors.joining(CSV_NEWLINE));
        return bom + header + CSV_NEWLINE + lines;
    }

    /**
     * Экранирует значение для CSV (оборачивает в кавычки при наличии спецсимволов).
     */
    public static String escapeCsv(String value) {
        if (value == null) return StringUtils.EMPTY;
        if (value.contains(CSV_COMMA) || value.contains(CSV_QUOTE) || value.contains(CSV_NEWLINE)) {
            return CSV_QUOTE + value.replace(CSV_QUOTE, CSV_ESCAPED_QUOTE) + CSV_QUOTE;
        }
        return value;
    }
}
