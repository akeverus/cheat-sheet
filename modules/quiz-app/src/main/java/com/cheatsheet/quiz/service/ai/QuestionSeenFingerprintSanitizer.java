package com.cheatsheet.quiz.service.ai;

import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Санитайзер множества fingerprint предыдущих генераций.
 *
 * <p>Обеспечивает безопасный immutable-снимок входного множества:
 * удаляет null/blank значения и нормализует пробелы по краям.</p>
 */
@Component
public class QuestionSeenFingerprintSanitizer {

    /**
     * Нормализует множество fingerprint для policy-проверок.
     *
     * @param seenFingerprints исходный набор fingerprint
     * @return immutable-набор валидных fingerprint
     */
    public Set<String> sanitize(Set<String> seenFingerprints) {
        if (seenFingerprints == null || seenFingerprints.isEmpty()) {
            return Set.of();
        }
        return seenFingerprints.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .collect(Collectors.toUnmodifiableSet());
    }
}
