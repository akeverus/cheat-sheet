package com.cheatsheet.quiz.api.exception;

import com.cheatsheet.quiz.api.dto.ApiError;
import com.cheatsheet.quiz.domain.exception.OptionNotFoundException;
import com.cheatsheet.quiz.domain.exception.QuestionNotFoundException;
import com.cheatsheet.quiz.service.admin.AdminSeniorRulesService;
import com.cheatsheet.quiz.service.ai.AiGenerationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

/**
 * Глобальный обработчик исключений API и MVC.
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(QuestionNotFoundException.class)
    public Object handleQuestionNotFound(QuestionNotFoundException ex, HttpServletRequest request) {
        if (isApiRequest(request)) {
            return apiErrorResponse(HttpStatus.NOT_FOUND, ApiErrorTypes.QUESTION_NOT_FOUND, ex.getMessage(), null);
        }
        log.warn("Вопрос не найден: {}", ex.getMessage());
        return redirectToHome();
    }

    @ExceptionHandler(OptionNotFoundException.class)
    public Object handleOptionNotFound(OptionNotFoundException ex, HttpServletRequest request) {
        if (isApiRequest(request)) {
            return apiErrorResponse(HttpStatus.NOT_FOUND, ApiErrorTypes.OPTION_NOT_FOUND, ex.getMessage(), null);
        }
        log.warn("Вариант не найден: {}", ex.getMessage());
        return redirectToHome();
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public Object handleBindingValidationException(Exception ex, HttpServletRequest request) {
        List<String> errors = new ArrayList<>();
        BindingResult bindingResult = ex instanceof MethodArgumentNotValidException manve
                ? manve.getBindingResult()
                : ((BindException) ex).getBindingResult();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            String message = fieldError.getDefaultMessage() != null
                    ? fieldError.getDefaultMessage()
                    : "invalid value";
            errors.add(fieldError.getField() + ": " + message);
        }
        bindingResult.getGlobalErrors().forEach(globalError -> {
            String message = globalError.getDefaultMessage() != null
                    ? globalError.getDefaultMessage()
                    : "invalid value";
            errors.add(globalError.getObjectName() + ": " + message);
        });
        return badRequest(request, ApiErrorTypes.VALIDATION_ERROR, "Некорректные параметры запроса", errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Object handleConstraintViolationException(ConstraintViolationException ex, HttpServletRequest request) {
        List<String> errors = ex.getConstraintViolations().stream()
                .map(this::toConstraintMessage)
                .toList();
        return badRequest(request, ApiErrorTypes.VALIDATION_ERROR, "Некорректные параметры запроса", errors);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Object handleTypeMismatchException(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String message = ex.getName() + ": unsupported format";
        return badRequest(request, ApiErrorTypes.VALIDATION_ERROR, "Некорректные параметры запроса", List.of(message));
    }

    @ExceptionHandler(AdminSeniorRulesService.ValidationException.class)
    public Object handleAdminValidationException(AdminSeniorRulesService.ValidationException ex, HttpServletRequest request) {
        List<String> details = toDetailsList(ex.details());
        return badRequest(request, ApiErrorTypes.VALIDATION_ERROR, ex.getMessage(), details);
    }

    @ExceptionHandler(AiGenerationException.class)
    public Object handleAiGenerationException(AiGenerationException ex, HttpServletRequest request) {
        if (isApiRequest(request)) {
            return apiErrorResponse(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    ApiErrorTypes.AI_GENERATION_FAILED,
                    ex.getMessage(),
                    null
            );
        }
        log.warn("AI-генерация недоступна на {}: {}", safeUri(request), safeMessage(ex));
        ModelAndView mav = new ModelAndView("error");
        mav.setStatus(HttpStatus.SERVICE_UNAVAILABLE);
        return mav;
    }

    @ExceptionHandler(IllegalStateException.class)
    public Object handleIllegalStateException(IllegalStateException ex, HttpServletRequest request) {
        log.warn("Неконсистентное состояние на {}: {}", safeUri(request), safeMessage(ex));
        if (isApiRequest(request)) {
            return apiErrorResponse(HttpStatus.CONFLICT, ApiErrorTypes.QUESTION_STATE_INVALID, ex.getMessage(), null);
        }
        return redirectToHome();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public Object handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest request) {
        log.warn("Конфликт данных на {}: {}", safeUri(request), safeMessage(ex));
        if (isApiRequest(request)) {
            return apiErrorResponse(
                    HttpStatus.CONFLICT,
                    ApiErrorTypes.CONFLICT,
                    "Конфликт данных, обновите страницу и повторите действие",
                    null
            );
        }
        return redirectToHome();
    }

    @ExceptionHandler(Exception.class)
    public Object handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Необработанное исключение на {}: {}", safeUri(request), safeMessage(ex), ex);
        if (isApiRequest(request)) {
            return apiErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ApiErrorTypes.INTERNAL_ERROR,
                    "Внутренняя ошибка сервера",
                    null
            );
        }
        ModelAndView mav = new ModelAndView("error");
        mav.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        return mav;
    }

    private boolean isApiRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String accept = request.getHeader("Accept");
        return uri.startsWith("/export") || uri.startsWith("/api/")
                || (accept != null && accept.contains(MediaType.APPLICATION_JSON_VALUE));
    }

    private Object badRequest(HttpServletRequest request, String type, String message, List<String> details) {
        if (isApiRequest(request)) {
            return apiErrorResponse(HttpStatus.BAD_REQUEST, type, message, details);
        }
        log.warn("Некорректный запрос на {}: {}", safeUri(request), details);
        return redirectToHome();
    }

    private ResponseEntity<ApiError> apiErrorResponse(HttpStatus status, String type, String message, List<String> details) {
        ApiError error = new ApiError(status.value(), type, message, details);
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(error);
    }

    private String toConstraintMessage(ConstraintViolation<?> violation) {
        String property = violation.getPropertyPath() != null ? violation.getPropertyPath().toString() : "parameter";
        String msg = violation.getMessage() != null ? violation.getMessage() : "invalid value";
        return property + ": " + msg;
    }

    private ModelAndView redirectToHome() {
        return new ModelAndView("redirect:/");
    }

    private String safeUri(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri == null ? "" : uri;
    }

    private String safeMessage(Throwable throwable) {
        if (throwable == null || throwable.getMessage() == null) {
            return "";
        }
        String compact = throwable.getMessage()
                .replace('\n', ' ')
                .replace('\r', ' ')
                .replace('\t', ' ')
                .trim();
        return compact.length() > 300 ? compact.substring(0, 300) + "..." : compact;
    }

    private List<String> toDetailsList(java.util.Map<String, ?> details) {
        if (details == null || details.isEmpty()) {
            return null;
        }
        return details.entrySet().stream()
                .sorted(java.util.Map.Entry.comparingByKey())
                .map(entry -> entry.getKey() + ": " + String.valueOf(entry.getValue()))
                .toList();
    }
}
