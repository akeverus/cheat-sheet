package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.api.security.SensitiveEndpointAccessService;
import com.cheatsheet.quiz.common.constants.ApiErrorTypes;
import com.cheatsheet.quiz.common.model.ApiError;
import com.cheatsheet.quiz.domain.ProgressExportRow;
import com.cheatsheet.quiz.feature.export.service.ExportService;
import com.cheatsheet.quiz.feature.export.usecase.ExportApiService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExportApiServiceTest {

    @Mock
    private ExportService exportService;
    @Mock
    private SensitiveEndpointAccessService accessService;

    private ExportApiService service;

    @BeforeEach
    void setUp() {
        service = new ExportApiService(exportService, accessService);
    }

    @Test
    void exportReturnsForbiddenWhenUnauthorized() {
        ApiError forbidden = new ApiError(403, ApiErrorTypes.FORBIDDEN, "Недостаточно прав", null);
        when(accessService.forbiddenIfUnauthorized("bad-token", "export прогресса"))
                .thenReturn(ResponseEntity.status(403).body(forbidden));

        ExportApiService.ExportResult result = service.export("bad-token", "json");

        assertThat(result.success()).isFalse();
        assertThat(result.status()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(result.error()).isEqualTo(forbidden);
        verify(exportService, never()).loadProgressRows();
    }

    @Test
    void exportReturnsBadRequestForUnsupportedFormat() {
        when(accessService.forbiddenIfUnauthorized(any(), any())).thenReturn(null);

        ExportApiService.ExportResult result = service.export("admin", "xml");

        assertThat(result.success()).isFalse();
        assertThat(result.status()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.error()).isNotNull();
        assertThat(result.error().type()).isEqualTo(ApiErrorTypes.UNSUPPORTED_FORMAT);
        verify(exportService, never()).loadProgressRows();
    }

    @Test
    void exportReturnsCsvPayloadWhenFormatIsCsv() {
        List<ProgressExportRow> rows = List.of(new ProgressExportRow("slug", "java", 1, 0, 1_700_000_000L, 2));
        when(accessService.forbiddenIfUnauthorized(any(), any())).thenReturn(null);
        when(exportService.loadProgressRows()).thenReturn(rows);
        when(exportService.exportAsCsv(rows)).thenReturn("slug,topic\na,b");

        ExportApiService.ExportResult result = service.export("admin", "csv");

        assertThat(result.success()).isTrue();
        assertThat(result.status()).isEqualTo(HttpStatus.OK);
        assertThat(result.filename()).isEqualTo("quiz-progress.csv");
        assertThat(result.contentType()).isEqualTo(new MediaType("text", "csv", StandardCharsets.UTF_8));
        assertThat(new String(result.body(), StandardCharsets.UTF_8)).contains("slug,topic");
        verify(exportService).exportAsCsv(rows);
    }

    @Test
    void exportReturnsInternalErrorWhenJsonSerializationFails() throws JsonProcessingException {
        List<ProgressExportRow> rows = List.of(new ProgressExportRow("slug", "java", 1, 0, 1_700_000_000L, 2));
        when(accessService.forbiddenIfUnauthorized(any(), any())).thenReturn(null);
        when(exportService.loadProgressRows()).thenReturn(rows);
        when(exportService.exportAsJson(eq(rows))).thenThrow(new JsonProcessingException("boom") {});

        ExportApiService.ExportResult result = service.export("admin", "json");

        assertThat(result.success()).isFalse();
        assertThat(result.status()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(result.error()).isNotNull();
        assertThat(result.error().type()).isEqualTo(ApiErrorTypes.EXPORT_SERIALIZATION_ERROR);
    }
}
