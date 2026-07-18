package com.cheatsheet.quiz.common.exception;

import com.cheatsheet.quiz.common.constants.ApiErrorTypes;
import com.cheatsheet.quiz.common.web.CorrelationIdFilter;
import com.cheatsheet.quiz.domain.exception.OptionNotFoundException;
import com.cheatsheet.quiz.domain.exception.QuestionNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;

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
        log.warn("Question not found: {}", ex.getMessage());
        return redirectToHome();
    }

    @ExceptionHandler(OptionNotFoundException.class)
    public Object handleOptionNotFound(OptionNotFoundException ex, HttpServletRequest request) {
        if (isApiRequest(request)) {
            return apiErrorResponse(HttpStatus.NOT_FOUND, ApiErrorTypes.OPTION_NOT_FOUND, ex.getMessage(), null);
        }
        log.warn("Option not found: {}", ex.getMessage());
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

    @ExceptionHandler(IllegalStateException.class)
    public Object handleIllegalStateException(IllegalStateException ex, HttpServletRequest request) {
        log.warn("Invalid state on {}: {}", safeUri(request), safeMessage(ex));
        if (isApiRequest(request)) {
            return apiErrorResponse(HttpStatus.CONFLICT, ApiErrorTypes.QUESTION_STATE_INVALID, ex.getMessage(), null);
        }
        return redirectToHome();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public Object handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest request) {
        log.warn("Data integrity conflict on {}: {}", safeUri(request), safeMessage(ex));
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

    @ExceptionHandler(NoResourceFoundException.class)
    public Object handleNoResourceFound(NoResourceFoundException ex, HttpServletRequest request) {
        String uri = safeUri(request);
        if ("/favicon.ico".equals(uri)) {
            // Не зашумляем логи на стандартный запрос браузера к favicon.
            return ResponseEntity.noContent().build();
        }
        // 404 (не 500) для несуществующих routes/ресурсов. Без явного маппинга
        // handleGenericException возвращал 500 — это давало false alarm в
        // мониторинге на любую опечатку в URL и сбивало с толку при
        // диагностике реальных 500.
        if (isApiRequest(request)) {
            return apiErrorResponse(
                    HttpStatus.NOT_FOUND,
                    ApiErrorTypes.RESOURCE_NOT_FOUND,
                    "Ресурс не найден: " + uri,
                    null
            );
        }
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("status", 404);
        mav.setStatus(HttpStatus.NOT_FOUND);
        return mav;
    }

    @ExceptionHandler(Exception.class)
    public Object handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception on {}: {}", safeUri(request), safeMessage(ex), ex);
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
        log.warn("Bad request on {}: {}", safeUri(request), details);
        return redirectToHome();
    }

    /**
     * Ответ об ошибке API в формате RFC-7807 {@link ProblemDetail} (хендофф-3,
     * Этап 9). Помимо стандартных полей несёт машиночитаемый {@code errorCode}
     * (из {@link ApiErrorTypes}), {@code correlationId} (для сопоставления с логами)
     * и {@code errors} (список деталей валидации).
     */
    private ResponseEntity<ProblemDetail> apiErrorResponse(HttpStatus status, String type, String message, List<String> details) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                status, message == null ? status.getReasonPhrase() : message);
        problem.setTitle(status.getReasonPhrase());
        problem.setProperty("errorCode", type);
        String correlationId = currentCorrelationId();
        if (correlationId != null) {
            problem.setProperty("correlationId", correlationId);
        }
        if (details != null && !details.isEmpty()) {
            problem.setProperty("errors", details);
        }
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }

    /** Correlation-id текущего запроса (проставлен {@link CorrelationIdFilter}), либо {@code null}. */
    private String currentCorrelationId() {
        org.springframework.web.context.request.RequestAttributes attrs =
                org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
        if (attrs instanceof org.springframework.web.context.request.ServletRequestAttributes servletAttrs) {
            Object cid = servletAttrs.getRequest().getAttribute(CorrelationIdFilter.REQUEST_ATTRIBUTE);
            return cid == null ? null : cid.toString();
        }
        return null;
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
}
