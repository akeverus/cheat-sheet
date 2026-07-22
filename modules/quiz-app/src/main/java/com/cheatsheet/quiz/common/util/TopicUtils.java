package com.cheatsheet.quiz.common.util;

import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;

/**
 * Преобразование slug-а темы в человекочитаемое название для UI.
 * Slug в обычном UI не показывается — только {@link #displayName(String)}.
 */
@Component("topicUtils")
public class TopicUtils {

    /** Известные slug → красивые названия (поверх авто-нормализации). */
    private static final Map<String, String> DISPLAY_NAMES = Map.ofEntries(
            Map.entry("database-transactions", "Транзакции БД"),
            Map.entry("database transactions", "Транзакции БД"),
            Map.entry("acid", "ACID"),
            Map.entry("jvm", "JVM"),
            Map.entry("gc", "Garbage Collection"),
            Map.entry("http", "HTTP"),
            Map.entry("sql", "SQL"),
            Map.entry("jpa", "JPA"),
            Map.entry("spring-boot", "Spring Boot"),
            Map.entry("spring boot", "Spring Boot"),
            Map.entry("kafka", "Kafka"),
            Map.entry("redis", "Redis"),
            Map.entry("docker", "Docker"),
            Map.entry("kubernetes", "Kubernetes"),
            Map.entry("microservices", "Микросервисы"),
            Map.entry("concurrency", "Многопоточность"),
            Map.entry("collections", "Коллекции"),
            Map.entry("streams", "Streams API"),
            Map.entry("security", "Безопасность"),
            Map.entry("networking", "Сети"),
            Map.entry("algorithms", "Алгоритмы"),
            Map.entry("data-structures", "Структуры данных")
    );

    /**
     * Сырой slug темы (для data-атрибутов / API), без UI-форматирования.
     */
    public String slug(String topic) {
        if (topic == null || topic.isBlank()) {
            return "";
        }
        String name = topic.trim();
        int lastSlash = name.lastIndexOf('/');
        if (lastSlash >= 0 && lastSlash < name.length() - 1) {
            name = name.substring(lastSlash + 1);
        }
        return name.replace("-interview", "").replace(".md", "").trim();
    }

    /**
     * @param topic slug темы, например {@code "java/collections-interview.md"}
     * @return отображаемое имя для UI (не slug)
     */
    public String displayName(String topic) {
        String key = slug(topic);
        if (key.isEmpty()) {
            return "";
        }
        String lookup = key.toLowerCase(Locale.ROOT).replace('_', '-');
        if (DISPLAY_NAMES.containsKey(lookup)) {
            return DISPLAY_NAMES.get(lookup);
        }
        // "database-transactions" → "Database transactions"
        String spaced = key.replace('-', ' ').replace('_', ' ').trim();
        if (spaced.isEmpty()) {
            return "";
        }
        return Character.toUpperCase(spaced.charAt(0)) + spaced.substring(1);
    }
}
