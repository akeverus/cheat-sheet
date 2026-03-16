package com.cheatsheet.quiz;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cheatsheet.quiz.feature.export.controller.ExportController;
import com.cheatsheet.quiz.common.model.ApiError;
import com.cheatsheet.quiz.common.constants.ApiErrorTypes;
import com.cheatsheet.quiz.feature.export.usecase.ExportApiService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
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
    ExportApiService exportApiService;

    @BeforeEach
    void setUp() {
        ExportController controller = new ExportController(exportApiService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void exportJsonWhenSerializationFailsReturns500WithStandardErrorBody() throws Exception {
        ApiError error = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ApiErrorTypes.EXPORT_SERIALIZATION_ERROR,
                "Ошибка сериализации экспорта в JSON",
                null
        );
        when(exportApiService.export(any(), any()))
                .thenReturn(new ExportApiService.ExportResult(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        error,
                        null,
                        MediaType.APPLICATION_JSON,
                        null
                ));

        mockMvc.perform(get("/export").param("format", "json").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.type").value(ApiErrorTypes.EXPORT_SERIALIZATION_ERROR))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }
}
