package com.cheatsheet.quiz.service.ai;

import com.cheatsheet.quiz.TestInterviewPath;
import com.cheatsheet.quiz.domain.OptionSource;
import com.cheatsheet.quiz.service.ai.dto.GeneratedOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Интеграционный тест LLM pipeline через API с мокнутым AiQuestionClient.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LlmPipelineIntegrationTest {

    @DynamicPropertySource
    static void setInterviewPath(DynamicPropertyRegistry registry) {
        TestInterviewPath.register(registry);
        registry.add("app.openai.api-key", () -> "test-key");
        // Тест проверяет именно LLM-пайплайн, поэтому AI-fallback включён
        // явно (по умолчанию seed-first, AI отключён).
        registry.add("app.ai.fallback-enabled", () -> "true");
    }

    @Autowired
    MockMvc mockMvc;

    @MockBean(name = "aiQuestionClient")
    AiQuestionClient aiQuestionClient;

    @BeforeEach
    void setUpMocks() {
        when(aiQuestionClient.sourceId()).thenReturn(OptionSource.OPENAI);
        when(aiQuestionClient.generateOptions(anyString(), any()))
                .thenReturn(Optional.of(new GeneratedOptions(
                        List.of(
                                new GeneratedOptions.GeneratedOption("Правильный ответ", true),
                                new GeneratedOptions.GeneratedOption("Использовать synchronized на уровне метода", false),
                                new GeneratedOptions.GeneratedOption("Хранить состояние сессии в статическом поле", false),
                                new GeneratedOptions.GeneratedOption("Запускать обработчик в daemon-потоке без ожидания", false)
                        )
                )));
    }

    @Test
    void apiNextReturnsQuestionWhenLlmPipelineIsMocked() throws Exception {
        mockMvc.perform(get("/api/next"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.questionId").isNumber())
                .andExpect(jsonPath("$.questionText").isString())
                .andExpect(jsonPath("$.options").isArray())
                .andExpect(jsonPath("$.options.length()").value(4));
    }
}
