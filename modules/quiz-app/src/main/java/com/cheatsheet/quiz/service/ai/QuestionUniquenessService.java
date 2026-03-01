package com.cheatsheet.quiz.service.ai;

import com.cheatsheet.quiz.config.AppProperties;
import com.cheatsheet.quiz.domain.QuestionType;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Хранит недавние fingerprint'ы сгенерированных вопросов для topic/type uniqueness.
 */
@Service
public class QuestionUniquenessService {

    private final int maxFingerprintsPerKey;
    private final Cache<String, ArrayDeque<String>> recentFingerprints;

    public QuestionUniquenessService(AppProperties appProperties) {
        int windowMinutes = appProperties.getInterview().getQuestionUniquenessWindowMinutes();
        int maxFingerprintsPerKey = appProperties.getInterview().getQuestionUniquenessMaxFingerprintsPerKey();
        this.maxFingerprintsPerKey = Math.max(10, maxFingerprintsPerKey);
        this.recentFingerprints = Caffeine.newBuilder()
                .maximumSize(5_000)
                .expireAfterWrite(Duration.ofMinutes(Math.max(5, windowMinutes)))
                .build();
    }

    public Set<String> loadRecentFingerprints(String topic, QuestionType type) {
        String key = key(topic, type);
        ArrayDeque<String> deque = recentFingerprints.getIfPresent(key);
        if (deque == null) {
            return new HashSet<>();
        }
        synchronized (deque) {
            return new HashSet<>(deque);
        }
    }

    public void rememberFingerprint(String topic, QuestionType type, String fingerprint) {
        if (fingerprint == null || fingerprint.isBlank()) {
            return;
        }
        String key = key(topic, type);
        ArrayDeque<String> deque = recentFingerprints.get(key, ignored -> new ArrayDeque<>());
        synchronized (deque) {
            if (deque.contains(fingerprint)) {
                return;
            }
            deque.addLast(fingerprint);
            while (deque.size() > maxFingerprintsPerKey) {
                deque.pollFirst();
            }
        }
    }

    private String key(String topic, QuestionType type) {
        String normalizedTopic = topic == null ? "general" : topic.trim().toLowerCase(Locale.ROOT);
        String normalizedType = type == null ? QuestionType.CONCEPT.name() : type.name();
        return normalizedTopic + "::" + normalizedType;
    }
}
