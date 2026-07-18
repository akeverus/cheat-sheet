package com.cheatsheet.quiz.api.exception;

import com.cheatsheet.quiz.common.constants.ApiErrorTypes;
import com.cheatsheet.quiz.common.exception.GlobalExceptionHandler;
import com.cheatsheet.quiz.api.dto.request.interview.QuestionIdRequest;
import com.cheatsheet.quiz.domain.exception.OptionNotFoundException;
import com.cheatsheet.quiz.domain.exception.QuestionNotFoundException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Controller;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
                        new ApiTypeMismatchController(),
                        new ApiConflictController(),
                        new FaviconController(),
                        new ApiMissingResourceController(),
                        new UiMissingResourceController(),
                        new ApiQuestionNotFoundController(),
                        new UiQuestionNotFoundController(),
                        new ApiOptionNotFoundController(),
                        new ApiConstraintViolationController(),
                        new ApiIllegalStateController(),
                        new UiIllegalStateController(),
                        new ApiGenericErrorController()
                )
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void apiValidationErrorReturnsStandardJsonPayload() throws Exception {
        mockMvc.perform(post("/api/test/validation").param("questionId", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errorCode").value(ApiErrorTypes.VALIDATION_ERROR))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    @Test
    void uiValidationErrorRedirectsToHome() throws Exception {
        mockMvc.perform(post("/test/validation").param("questionId", "0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void apiTypeMismatchReturnsValidationErrorPayload() throws Exception {
        mockMvc.perform(post("/api/test/type-mismatch").param("questionId", "not-a-number"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errorCode").value(ApiErrorTypes.VALIDATION_ERROR))
                .andExpect(jsonPath("$.errors[0]").value("questionId: unsupported format"));
    }

    @Test
    void apiConflictReturnsStandardConflictPayload() throws Exception {
        mockMvc.perform(post("/api/test/conflict"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.errorCode").value(ApiErrorTypes.CONFLICT))
                .andExpect(jsonPath("$.detail").value("Конфликт данных, обновите страницу и повторите действие"));
    }

    @Test
    void faviconMissingReturnsNoContentWithoutServerError() throws Exception {
        mockMvc.perform(get("/favicon.ico"))
                .andExpect(status().isNoContent());
    }

    @Test
    void apiNoResourceReturns404WithResourceNotFoundType() throws Exception {
        // ApiMissingResourceController бросает NoResourceFoundException для
        // /api/test/missing-resource — handler из cycle #94 должен вернуть
        // 404 + RESOURCE_NOT_FOUND вместо дефолтного 500.
        mockMvc.perform(get("/api/test/missing-resource"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.errorCode").value(ApiErrorTypes.RESOURCE_NOT_FOUND));
    }

    @Test
    void uiNoResourceRendersErrorViewWith404Status() throws Exception {
        // Не-API путь → handler возвращает ModelAndView("error") с 404 status.
        mockMvc.perform(get("/test/missing-resource"))
                .andExpect(status().isNotFound());
    }

    @Test
    void apiQuestionNotFoundReturns404WithType() throws Exception {
        mockMvc.perform(get("/api/test/q-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.errorCode").value(ApiErrorTypes.QUESTION_NOT_FOUND));
    }

    @Test
    void uiQuestionNotFoundRedirectsToHome() throws Exception {
        mockMvc.perform(get("/test/q-not-found"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void apiOptionNotFoundReturns404WithType() throws Exception {
        mockMvc.perform(get("/api/test/o-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(ApiErrorTypes.OPTION_NOT_FOUND));
    }

    @Test
    void apiConstraintViolationReturnsValidationError() throws Exception {
        mockMvc.perform(get("/api/test/constraint-violation"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(ApiErrorTypes.VALIDATION_ERROR));
    }

    @Test
    void apiIllegalStateReturnsConflict() throws Exception {
        mockMvc.perform(post("/api/test/illegal-state"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value(ApiErrorTypes.QUESTION_STATE_INVALID));
    }

    @Test
    void uiIllegalStateRedirectsToHome() throws Exception {
        mockMvc.perform(post("/test/illegal-state"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void apiGenericErrorReturnsInternalServerError() throws Exception {
        mockMvc.perform(post("/api/test/generic-error"))
                .andExpect(status().isInternalServerError());
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

    @RestController
    static class FaviconController {
        @GetMapping("/favicon.ico")
        String fail() throws NoResourceFoundException {
            throw new NoResourceFoundException(HttpMethod.GET, "/favicon.ico");
        }
    }

    @RestController
    static class ApiMissingResourceController {
        @GetMapping("/api/test/missing-resource")
        String fail() throws NoResourceFoundException {
            throw new NoResourceFoundException(HttpMethod.GET, "/api/test/missing-resource");
        }
    }

    @Controller
    static class UiMissingResourceController {
        @GetMapping("/test/missing-resource")
        String fail() throws NoResourceFoundException {
            throw new NoResourceFoundException(HttpMethod.GET, "/test/missing-resource");
        }
    }

    @RestController
    static class ApiQuestionNotFoundController {
        @GetMapping("/api/test/q-not-found")
        String fail() {
            throw new QuestionNotFoundException("Вопрос 42 не найден");
        }
    }

    @Controller
    static class UiQuestionNotFoundController {
        @GetMapping("/test/q-not-found")
        String fail() {
            throw new QuestionNotFoundException("Вопрос 42 не найден");
        }
    }

    @RestController
    static class ApiOptionNotFoundController {
        @GetMapping("/api/test/o-not-found")
        String fail() {
            throw new OptionNotFoundException("Вариант не найден");
        }
    }

    @RestController
    static class ApiConstraintViolationController {
        @GetMapping("/api/test/constraint-violation")
        String fail() {
            // jakarta.validation.ConstraintViolationException с пустым набором — handler корректно отдаст 400
            throw new ConstraintViolationException("validation failed", Set.of());
        }
    }

    @RestController
    static class ApiIllegalStateController {
        @PostMapping("/api/test/illegal-state")
        String fail() {
            throw new IllegalStateException("Невозможный переход состояния");
        }
    }

    @Controller
    static class UiIllegalStateController {
        @PostMapping("/test/illegal-state")
        String fail() {
            throw new IllegalStateException("Невозможный переход состояния");
        }
    }

    @RestController
    static class ApiGenericErrorController {
        @PostMapping("/api/test/generic-error")
        String fail() {
            throw new RuntimeException("Неожиданная ошибка");
        }
    }
}
