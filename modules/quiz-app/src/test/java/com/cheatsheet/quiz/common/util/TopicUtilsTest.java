package com.cheatsheet.quiz.common.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class TopicUtilsTest {

    private final TopicUtils utils = new TopicUtils();

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void displayNameReturnsEmptyForBlank(String input) {
        assertThat(utils.displayName(input)).isEmpty();
        assertThat(utils.slug(input)).isEmpty();
    }

    @ParameterizedTest
    @CsvSource({
            "java/collections-interview.md, Коллекции, collections",
            "java/collections-interview, Коллекции, collections",
            "java/collections.md, Коллекции, collections",
            "collections-interview, Коллекции, collections",
            "collections, Коллекции, collections",
            "ai-ml/rag-interview.md, RAG, rag",
            "deep/nested/path/topic-interview.md, Topic, topic",
            "database-transactions, Транзакции БД, database-transactions",
            "api-gateway, API gateway, api-gateway",
            "cqrs-event-sourcing, CQRS event sourcing, cqrs-event-sourcing",
            "jwt, JWT, jwt",
            "grpc, gRPC, grpc",
            "oauth2, OAuth2, oauth2",
            "mtls, mTLS, mtls",
            "tls-ssl, TLS/SSL, tls-ssl",
            "owasp-top10, OWASP Top 10, owasp-top10",
            "spring-data-jpa, Spring data JPA, spring-data-jpa",
            "interview-preparation, Подготовка к собеседованию, interview-preparation",
            "soft-skills/interview-preparation-interview.md, Подготовка к собеседованию, interview-preparation"
    })
    void displayNameIsHumanReadableAndSlugIsRaw(String input, String display, String slug) {
        assertThat(utils.displayName(input)).isEqualTo(display);
        assertThat(utils.slug(input)).isEqualTo(slug);
    }

    @Test
    void displayNameKeepsTrailingSlashLiterally() {
        // Документирует текущее поведение: slug с trailing slash не «зачёркивается»
        // (для реальных тем такой кейс не встречается, но тест фиксирует контракт).
        assertThat(utils.slug("category/")).isEqualTo("category/");
        assertThat(utils.displayName("category/")).isEqualTo("Category/");
    }
}
