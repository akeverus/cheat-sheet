package com.cheatsheet.quiz;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cheatsheet.quiz.api.security.SensitiveEndpointAccessService;
import com.cheatsheet.quiz.common.constants.ApiErrorTypes;
import com.cheatsheet.quiz.common.exception.GlobalExceptionHandler;
import com.cheatsheet.quiz.feature.export.controller.ExportController;
import com.cheatsheet.quiz.feature.export.exception.ExportSerializationException;
import com.cheatsheet.quiz.feature.export.exception.UnsupportedExportFormatException;
import com.cheatsheet.quiz.feature.export.usecase.ExportApiService;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Ошибочные ветки {@link ExportController}: единый контракт RFC-7807
 * ({@code application/problem+json} + {@code errorCode}) и rate limit с
 * {@code Retry-After}.
 */
@ExtendWith(MockitoExtension.class)
class ExportControllerErrorTest {

    MockMvc mockMvc;

    @Mock
    ExportApiService exportApiService;
    @Mock
    SensitiveEndpointAccessService accessService;

    @BeforeEach
    void setUp() {
        ExportController controller = new ExportController(exportApiService, accessService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void exportJsonWhenSerializationFailsReturnsProblemDetail() throws Exception {
        when(accessService.allowSensitiveRequest(any())).thenReturn(true);
        when(exportApiService.export(any()))
                .thenThrow(new ExportSerializationException("Ошибка сериализации экспорта в JSON", null));

        mockMvc.perform(get("/export").param("format", "json").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.errorCode").value(ApiErrorTypes.EXPORT_SERIALIZATION_ERROR))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    @Test
    void exportWithUnsupportedFormatReturnsBadRequestProblemDetail() throws Exception {
        when(accessService.allowSensitiveRequest(any())).thenReturn(true);
        when(exportApiService.export(any()))
                .thenThrow(new UnsupportedExportFormatException(List.of("json", "csv")));

        mockMvc.perform(get("/export").param("format", "xml").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.errorCode").value(ApiErrorTypes.UNSUPPORTED_FORMAT))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    void exportBeyondRateLimitReturns429WithRetryAfter() throws Exception {
        when(accessService.allowSensitiveRequest(any())).thenReturn(false);
        when(accessService.sensitiveRetryAfterSeconds()).thenReturn(60);

        mockMvc.perform(get("/export").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isTooManyRequests())
                .andExpect(header().string("Retry-After", "60"))
                .andExpect(jsonPath("$.errorCode").value(ApiErrorTypes.RATE_LIMIT_EXCEEDED));

        verify(exportApiService, never()).export(any());
    }
}
