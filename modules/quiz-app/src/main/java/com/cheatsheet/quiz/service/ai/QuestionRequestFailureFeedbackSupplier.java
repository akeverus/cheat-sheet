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

    private final AppProperties appProperties;
    private final QuestionRetryFeedbackNormalizer questionRetryFeedbackNormalizer;

    /**
     * Возвращает нормализованный retry-feedback для случая,
     * когда AI не вернул парсируемый JSON.
     */
    public List<String> feedback() {
        String message = appProperties.getInterview().getQuestionRequestFailureViolationMessage();
        return questionRetryFeedbackNormalizer.normalize(List.of(message));
    }
}
