package com.cheatsheet.quiz.common.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class TopicUtilsTest {

    private final TopicDisplayNames displayNames = new TopicDisplayNames();
    private final TopicUtils utils = new TopicUtils(displayNames);

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void displayNameReturnsEmptyForBlank(String input) {
        assertThat(utils.displayName(input)).isEmpty();
        assertThat(utils.slug(input)).isEmpty();
    }

    @ParameterizedTest
    @CsvSource({
            "java/collections-interview.md, collections",
            "java/collections-interview, collections",
            "java/collections.md, collections",
            "collections-interview, collections",
            "collections, collections",
            "deep/nested/path/topic-interview.md, topic",
            "databases/cockroachdb-interview.md, cockroachdb"
    })
    void slugStripsPathAndSuffixes(String input, String slug) {
        assertThat(utils.slug(input)).isEqualTo(slug);
    }

    @ParameterizedTest
    @CsvSource({
            // Название из реестра приходит как есть — включая регистр внутри слова,
            // который из slug-а восстановить невозможно.
            "databases/cockroachdb-interview.md, cockroachdb, CockroachDB",
            "testing/archunit-interview.md, archunit, ArchUnit",
            "databases/jooq-interview.md, jooq, jOOQ",
            "data-engineering/apache-airflow-interview.md, apache-airflow, Apache Airflow",
            "databases/database-transactions-interview.md, database-transactions, Транзакции и уровни изоляции"
    })
    void displayNameComesFromRegistry(String topic, String registeredSlug, String expected) {
        displayNames.register(registeredSlug, expected);

        assertThat(utils.displayName(topic)).isEqualTo(expected);
    }

    @Test
    void registryLookupIgnoresCaseAndUnderscores() {
        displayNames.register("spring-data-jpa", "Spring Data JPA");

        assertThat(utils.displayName("frameworks/spring/Spring_Data_JPA-interview.md"))
                .isEqualTo("Spring Data JPA");
    }

    @Test
    void unknownTopicFallsBackToReadableSlug() {
        // Тема без файла (например, файл удалён, а вопросы ещё в БД): показываем slug
        // с заглавной буквы. Реконструировать «настоящее» имя из slug-а не пытаемся —
        // любая такая догадка коверкает названия продуктов.
        assertThat(utils.displayName("legacy/some-removed-topic-interview.md"))
                .isEqualTo("Some removed topic");
    }

    @Test
    void blankRegistrationIsIgnored() {
        displayNames.register("kafka", "  ");
        displayNames.register("  ", "Kafka");

        assertThat(displayNames.lookup("kafka")).isNull();
        assertThat(utils.displayName("messaging/kafka-interview.md")).isEqualTo("Kafka");
    }

    @Test
    void displayNameKeepsTrailingSlashLiterally() {
        // Документирует текущее поведение: slug с trailing slash не «зачёркивается»
        // (для реальных тем такой кейс не встречается, но тест фиксирует контракт).
        assertThat(utils.slug("category/")).isEqualTo("category/");
        assertThat(utils.displayName("category/")).isEqualTo("Category/");
    }
}
