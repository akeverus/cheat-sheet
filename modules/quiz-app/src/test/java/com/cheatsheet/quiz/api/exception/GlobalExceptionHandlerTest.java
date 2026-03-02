package com.cheatsheet.quiz.api.exception;

import com.cheatsheet.quiz.api.dto.request.QuestionIdRequest;
import com.cheatsheet.quiz.service.admin.AdminSeniorRulesService;
import com.cheatsheet.quiz.service.ai.AiGenerationException;
import jakarta.validation.Valid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Controller;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(
                        new ApiValidationController(),
                        new UiValidationController(),
                        new ApiAiErrorController(),
                        new ApiAdminValidationController(),
                        new ApiTypeMismatchController(),
                        new ApiConflictController()
                )
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void apiValidationErrorReturnsStandardJsonPayload() throws Exception {
        mockMvc.perform(post("/api/test/validation").param("questionId", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.type").value(ApiErrorTypes.VALIDATION_ERROR))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void uiValidationErrorRedirectsToHome() throws Exception {
        mockMvc.perform(post("/test/validation").param("questionId", "0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void apiAiGenerationErrorReturnsTypedServiceUnavailable() throws Exception {
        mockMvc.perform(post("/api/test/ai-failure"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value(503))
                .andExpect(jsonPath("$.type").value(ApiErrorTypes.AI_GENERATION_FAILED))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void apiAdminValidationErrorReturnsStructuredDetails() throws Exception {
        mockMvc.perform(post("/api/test/admin-validation"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.type").value(ApiErrorTypes.VALIDATION_ERROR))
                .andExpect(jsonPath("$.message").value("Некорректный payload"))
                .andExpect(jsonPath("$.details[0]").value("entry: spring-transactional=-1"));
    }

    @Test
    void apiTypeMismatchReturnsValidationErrorPayload() throws Exception {
        mockMvc.perform(post("/api/test/type-mismatch").param("questionId", "not-a-number"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.type").value(ApiErrorTypes.VALIDATION_ERROR))
                .andExpect(jsonPath("$.details[0]").value("questionId: unsupported format"));
    }

    @Test
    void apiConflictReturnsStandardConflictPayload() throws Exception {
        mockMvc.perform(post("/api/test/conflict"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.type").value(ApiErrorTypes.CONFLICT))
                .andExpect(jsonPath("$.message").value("Конфликт данных, обновите страницу и повторите действие"));
    }

    @RestController
    static class ApiValidationController {
        @PostMapping("/api/test/validation")
        String validate(@Valid @ModelAttribute QuestionIdRequest request) {
            return "ok";
        }
    }

    @Controller
    static class UiValidationController {
        @PostMapping("/test/validation")
        String validate(@Valid @ModelAttribute QuestionIdRequest request) {
            return "ok";
        }
    }

    @RestController
    static class ApiAiErrorController {
        @PostMapping("/api/test/ai-failure")
        String fail() {
            throw new AiGenerationException("AI временно недоступен");
        }
    }

    @RestController
    static class ApiAdminValidationController {
        @PostMapping("/api/test/admin-validation")
        String fail() {
            throw new AdminSeniorRulesService.ValidationException(
                    "Некорректный payload",
                    java.util.Map.of("entry", "spring-transactional=-1")
            );
        }
    }

    @RestController
    static class ApiTypeMismatchController {
        @PostMapping("/api/test/type-mismatch")
        String fail(@RequestParam("questionId") long questionId) {
            return "ok";
        }
    }

    @RestController
    static class ApiConflictController {
        @PostMapping("/api/test/conflict")
        String fail() {
            throw new DataIntegrityViolationException("duplicate key");
        }
    }
}
