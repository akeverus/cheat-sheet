package com.cheatsheet.quiz.service.ai;

import com.cheatsheet.quiz.config.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Нормализатор quality-feedback для retry-цикла генерации вопросов.
 *
 * <p>Гарантирует детерминированный порядок и ограниченный размер списка нарушений,
 * передаваемого обратно в prompt следующей попытки.</p>
 */
@Component
@RequiredArgsConstructor
public class QuestionRetryFeedbackNormalizer {

    private final AppProperties appProperties;

    /**
     * Возвращает нормализованный список нарушений для prompt feedback.
     *
     * @param violations исходный список нарушений
     * @return детерминированный список: trim + distinct + sort + limit
     */
    public List<String> normalize(List<String> violations) {
        if (violations == null || violations.isEmpty()) {
            return List.of();
        }
        int maxItems = Math.max(1, appProperties.getInterview().getQuestionRetryFeedbackMaxItems());
        Map<String, String> uniqueByLowerValue = new LinkedHashMap<>();
        violations.stream()
                .filter(value -> value != null && !value.isBlank())
                .map(String::trim)
                .forEach(value -> uniqueByLowerValue.putIfAbsent(value.toLowerCase(Locale.ROOT), value));
        return uniqueByLowerValue.values().stream()
                .sorted(Comparator.comparing(value -> value.toLowerCase(Locale.ROOT)))
                .limit(maxItems)
                .toList();
    }
}
