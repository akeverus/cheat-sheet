package com.cheatsheet.quiz.service.ai;

import com.cheatsheet.quiz.config.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Поставщик нормализованного retry-feedback для сценария ошибки AI-запроса.
 */
@Component
@RequiredArgsConstructor
public class QuestionRequestFailureFeedbackSupplier {

    private static final String DEFAULT_REQUEST_FAILURE_MESSAGE = "AI did not return parsable question JSON payload";

    private final AppProperties appProperties;
    private final QuestionRetryFeedbackNormalizer questionRetryFeedbackNormalizer;

    /**
     * Возвращает нормализованный retry-feedback для случая,
     * когда AI не вернул парсируемый JSON.
     */
    public List<String> feedback() {
        String message = appProperties.getInterview().getQuestionRequestFailureViolationMessage();
        String safeMessage = normalizeMessage(message);
        List<String> normalized = questionRetryFeedbackNormalizer.normalize(List.of(safeMessage));
        if (!normalized.isEmpty()) {
            return normalized;
        }
        return List.of(safeMessage);
    }

    private static String normalizeMessage(String message) {
        if (message == null) {
            return DEFAULT_REQUEST_FAILURE_MESSAGE;
        }
        String normalized = message.trim();
        return normalized.isBlank() ? DEFAULT_REQUEST_FAILURE_MESSAGE : normalized;
    }
}
