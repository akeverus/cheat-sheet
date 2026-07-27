package com.cheatsheet.quiz.common.util;

import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

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
            Map.entry("data-structures", "Структуры данных"),
            Map.entry("tls-ssl", "TLS/SSL")
    );

    /**
     * Токены-аббревиатуры, которые в fallback-имени пишутся целиком заглавными
     * ("jwt" → "JWT", "cqrs" → "CQRS"). Иначе наивная капитализация первой буквы
     * даёт "Jwt", "Cqrs", "Api" — визуально неряшливо в селекте тем и на аналитике.
     */
    private static final Set<String> UPPER_TOKENS = Set.of(
            "jwt", "api", "sql", "http", "https", "cdn", "dns", "ddd", "cqrs",
            "tls", "ssl", "aws", "gcp", "mcp", "rag", "llm", "ai", "jvm", "jpa",
            "orm", "jdbc", "rest", "bff", "owasp", "io", "nio", "jms", "dto",
            "dao", "acid", "cap", "wal", "mvcc", "dsl", "sns", "sqs", "ml", "url",
            "uri"
    );

    /** Аббревиатуры со смешанным регистром — задаются явным написанием. */
    private static final Map<String, String> MIXED_TOKENS = Map.ofEntries(
            Map.entry("grpc", "gRPC"),
            Map.entry("oauth2", "OAuth2"),
            Map.entry("oauth", "OAuth"),
            Map.entry("mtls", "mTLS"),
            Map.entry("top10", "Top 10")
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
        // Пословный fallback: аббревиатуры → верхний/смешанный регистр,
        // первое слово — с заглавной, остальные обычные слова остаются строчными.
        // "api-gateway" → "API gateway", "cqrs-event-sourcing" → "CQRS event sourcing",
        // "grpc" → "gRPC", "database-transactions" → "Database transactions".
        String spaced = key.replace('-', ' ').replace('_', ' ').trim();
        if (spaced.isEmpty()) {
            return "";
        }
        String[] words = spaced.split("\\s+");
        StringBuilder result = new StringBuilder(spaced.length());
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            String lower = word.toLowerCase(Locale.ROOT);
            String rendered;
            if (MIXED_TOKENS.containsKey(lower)) {
                rendered = MIXED_TOKENS.get(lower);
            } else if (UPPER_TOKENS.contains(lower)) {
                rendered = lower.toUpperCase(Locale.ROOT);
            } else if (i == 0) {
                rendered = Character.toUpperCase(word.charAt(0)) + word.substring(1);
            } else {
                rendered = word;
            }
            if (i > 0) {
                result.append(' ');
            }
            result.append(rendered);
        }
        return result.toString();
    }
}
