package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.domain.ProgressExportRow;
import com.cheatsheet.quiz.feature.export.exception.ExportSerializationException;
import com.cheatsheet.quiz.feature.export.exception.UnsupportedExportFormatException;
import com.cheatsheet.quiz.feature.export.service.ExportService;
import com.cheatsheet.quiz.feature.export.usecase.ExportApiService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExportApiServiceTest {

    @Mock
    private ExportService exportService;

    private ExportApiService service;

    @BeforeEach
    void setUp() {
        service = new ExportApiService(exportService);
    }

    @Test
    void exportRejectsUnsupportedFormatBeforeTouchingStorage() {
        assertThatThrownBy(() -> service.export("xml"))
                .isInstanceOf(UnsupportedExportFormatException.class)
                .hasMessageContaining("json")
                .hasMessageContaining("csv");

        verify(exportService, never()).loadProgressRows();
    }

    @Test
    void exportNormalizesFormatCaseAndWhitespace() {
        List<ProgressExportRow> rows = List.of(new ProgressExportRow("slug", "java", 1, 0, 1_700_000_000L, 2));
        when(exportService.loadProgressRows()).thenReturn(rows);
        when(exportService.exportAsCsv(rows)).thenReturn("slug,topic\na,b");

        ExportApiService.ExportResult result = service.export("  CSV ");

        assertThat(result.filename()).isEqualTo("quiz-progress.csv");
    }

    @Test
    void exportReturnsCsvPayloadWhenFormatIsCsv() {
        List<ProgressExportRow> rows = List.of(new ProgressExportRow("slug", "java", 1, 0, 1_700_000_000L, 2));
        when(exportService.loadProgressRows()).thenReturn(rows);
        when(exportService.exportAsCsv(rows)).thenReturn("slug,topic\na,b");

        ExportApiService.ExportResult result = service.export("csv");

        assertThat(result.filename()).isEqualTo("quiz-progress.csv");
        assertThat(result.contentType()).isEqualTo(new MediaType("text", "csv", StandardCharsets.UTF_8));
        assertThat(new String(result.body(), StandardCharsets.UTF_8)).contains("slug,topic");
        verify(exportService).exportAsCsv(rows);
    }

    @Test
    void exportReturnsJsonPayloadByDefaultFormat() throws JsonProcessingException {
        List<ProgressExportRow> rows = List.of(new ProgressExportRow("slug", "java", 1, 0, 1_700_000_000L, 2));
        when(exportService.loadProgressRows()).thenReturn(rows);
        when(exportService.exportAsJson(rows)).thenReturn("[]".getBytes(StandardCharsets.UTF_8));

        ExportApiService.ExportResult result = service.export("json");

        assertThat(result.filename()).isEqualTo("quiz-progress.json");
        assertThat(result.contentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(new String(result.body(), StandardCharsets.UTF_8)).isEqualTo("[]");
    }

    @Test
    void exportWrapsJsonSerializationFailure() throws JsonProcessingException {
        List<ProgressExportRow> rows = List.of(new ProgressExportRow("slug", "java", 1, 0, 1_700_000_000L, 2));
        when(exportService.loadProgressRows()).thenReturn(rows);
        when(exportService.exportAsJson(eq(rows))).thenThrow(new JsonProcessingException("boom") {
        });

        assertThatThrownBy(() -> service.export("json"))
                .isInstanceOf(ExportSerializationException.class)
                .hasCauseInstanceOf(JsonProcessingException.class);
    }
}
