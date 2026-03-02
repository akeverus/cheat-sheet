package com.cheatsheet.quiz.service.ai;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Нормализатор quality-сообщений Question Engine.
 *
 * <p>Обеспечивает единый контракт обработки нарушений/feedback:
 * trim, удаление пустых/null, case-insensitive dedup с сохранением
 * первого вхождения и детерминированного порядка.</p>
 */
@Component
public class QuestionQualityMessageNormalizer {

    /**
     * Нормализует сообщения качества в детерминированный immutable-список.
     *
     * @param messages исходные сообщения
     * @return нормализованный список сообщений
     */
    public List<String> normalize(List<String> messages) {
        if (messages == null || messages.isEmpty()) {
            return List.of();
        }
        Map<String, String> uniqueByLowerValue = new LinkedHashMap<>();
        messages.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .forEach(value -> uniqueByLowerValue.putIfAbsent(value.toLowerCase(Locale.ROOT), value));
        return List.copyOf(uniqueByLowerValue.values());
    }
}
