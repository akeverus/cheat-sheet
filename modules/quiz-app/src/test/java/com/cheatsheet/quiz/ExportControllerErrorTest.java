package com.cheatsheet.quiz;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cheatsheet.quiz.api.controller.ExportController;
import com.cheatsheet.quiz.api.exception.ApiErrorTypes;
import com.cheatsheet.quiz.api.security.SensitiveEndpointAccessService;
import com.cheatsheet.quiz.domain.ProgressExportRow;
import com.cheatsheet.quiz.service.ExportService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
 * Тесты обработки ошибок в {@link ExportController}.
 */
@ExtendWith(MockitoExtension.class)
class ExportControllerErrorTest {

    MockMvc mockMvc;

    @Mock
    ExportService exportService;

    @Mock
    ObjectMapper objectMapper;

    @Mock
    SensitiveEndpointAccessService accessService;

    @BeforeEach
    void setUp() {
        when(exportService.loadProgressRows()).thenReturn(List.of(
                new ProgressExportRow("slug", "topic", 0, 0, 0L, 0)
        ));
        when(accessService.forbiddenIfUnauthorized(any(), any())).thenReturn(null);

        ExportController controller = new ExportController(exportService, objectMapper, accessService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void exportJsonWhenSerializationFailsReturns500WithStandardErrorBody() throws Exception {
        when(exportService.exportAsJson(any())).thenThrow(new JsonProcessingException("test") {});
        when(objectMapper.writeValueAsString(any())).thenReturn(
                "{\"status\":500,\"type\":\"EXPORT_SERIALIZATION_ERROR\",\"message\":\"Ошибка сериализации экспорта в JSON\"}"
        );

        mockMvc.perform(get("/export").param("format", "json").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.type").value(ApiErrorTypes.EXPORT_SERIALIZATION_ERROR))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }
}
