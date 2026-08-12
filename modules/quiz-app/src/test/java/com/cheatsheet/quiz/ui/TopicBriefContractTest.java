package com.cheatsheet.quiz.ui;

import com.cheatsheet.quiz.common.util.TopicDisplayNames;
import com.cheatsheet.quiz.common.util.TopicUtils;
import com.cheatsheet.quiz.service.imports.TopicTitleReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Материал темы под вопросом: откуда он берётся и чего не делает.
 *
 * <h2>Найденный дефект, а не гипотеза</h2>
 * Замер пустоты на экране вопроса (Chromium и WebKit, вопрос без кода): под колонкой
 * вопроса до нижней кромки вариантов пустовало 336×602 px на 1512×982 и 253×768 на
 * 1920 и 2560 — около 200 тысяч пикселей рабочей области, не занятых ничем. Это §37
 * брифа: «одна карточка в центре большого экрана».
 *
 * <h2>Что закреплено</h2>
 * <ol>
 *   <li>описание темы читается из frontmatter самой шпаргалки — второго источника нет;</li>
 *   <li>у темы без описания блок не рендерится: выдумывать текст из slug-а нельзя;</li>
 *   <li>блок не повторяет название темы, которое уже стоит в мета-строке вопроса.</li>
 * </ol>
 */
class TopicBriefContractTest {

    private static final Pattern COMMENT = Pattern.compile("<!--.*?-->", Pattern.DOTALL);

    private String read(String classpathLocation) throws IOException {
        return new String(new ClassPathResource(classpathLocation).getInputStream().readAllBytes(),
                StandardCharsets.UTF_8);
    }

    /**
     * Читатель берёт описание из того же frontmatter, что и заголовок, одним правилом.
     * Заголовку префикс раздела снимается («Вопросы на собеседовании: Дженерики Go» →
     * «Дженерики Go»), описанию — нет: там двоеточие часть текста, а не разделитель.
     */
    @Test
    void descriptionComesFromTheCheatsheetFrontmatter(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("go-generics-interview.md");
        Files.writeString(file, """
                ---
                title: "Вопросы на собеседовании: Дженерики Go"
                description: "Generics в Go 1.18+: type parameters, constraints, type sets"
                tags:
                  - interview
                ---

                ## Q1
                Вопрос?
                """, StandardCharsets.UTF_8);

        TopicTitleReader reader = new TopicTitleReader();
        assertThat(reader.readTopicTitle(file)).isEqualTo("Дженерики Go");
        assertThat(reader.readTopicDescription(file))
                .isEqualTo("Generics в Go 1.18+: type parameters, constraints, type sets");

        Path withoutDescription = dir.resolve("plain.md");
        Files.writeString(withoutDescription, """
                ---
                title: "Раздел: Тема"
                ---
                """, StandardCharsets.UTF_8);
        assertThat(reader.readTopicDescription(withoutDescription))
                .as("описания нет — значит его нет, а не пустая строка").isNull();

        Path withoutFrontmatter = dir.resolve("bare.md");
        Files.writeString(withoutFrontmatter, "## Q1\nВопрос?\n", StandardCharsets.UTF_8);
        assertThat(reader.readTopicDescription(withoutFrontmatter)).isNull();
    }

    /** Тема без описания не получает выдуманный текст: блок просто не рендерится. */
    @Test
    void unknownTopicHasNoInventedDescription() {
        TopicDisplayNames registry = new TopicDisplayNames();
        TopicUtils utils = new TopicUtils(registry);

        assertThat(utils.description("java/collections-interview.md")).isNull();
        assertThat(utils.description(null)).isNull();
        assertThat(utils.description("")).isNull();

        // Регистрируем ровно так, как это делает импорт: ключ — slug, а не путь файла.
        registry.registerDescription(utils.slug("java/collections-interview.md"),
                "  Списки, множества, карты  ");
        assertThat(utils.description("java/collections-interview.md")).isEqualTo("Списки, множества, карты");
        // Регистр не влияет: в БД тема хранится путём файла, а в модель приходит как есть.
        assertThat(utils.description("Collections")).isEqualTo("Списки, множества, карты");
        // А вот подчёркивание вместо дефиса slug НЕ нормализует («collections_interview»
        // остаётся собой, потому что режется только суффикс «-interview»). Проверять
        // обратное значило бы утверждать в гейте больше, чем делает продукт: описания
        // ищутся по тому же ключу, что и названия, и этого достаточно — темы банка
        // приходят из имён файлов, где разделитель дефис.
        assertThat(utils.description("java/collections_interview.md")).isNull();

        registry.registerDescription(utils.slug("java/x.md"), "   ");
        assertThat(utils.description("java/x.md")).as("пустое описание не регистрируется").isNull();
    }

    /**
     * Блок называет свою роль, а не тему. Первая сборка печатала здесь название темы —
     * и оно читалось дважды на одном экране: в мета-строке вопроса и в 350 пикселях
     * ниже, в заголовке блока (замер WebKit 1512×982).
     */
    @Test
    void theBriefDoesNotRepeatTheTopicNameAndDegradesWithoutDescription() throws IOException {
        String markup = COMMENT.matcher(read("templates/focus-training.html")).replaceAll("\n");

        assertThat(markup).as("блок материала темы исчез с экрана вопроса")
                .contains("class=\"topic-brief\"");
        assertThat(markup).as("заголовок блока снова печатает название темы — оно уже стоит "
                        + "в мета-строке вопроса")
                .doesNotContain("class=\"topic-brief-title\" id=\"topic-brief-title\"\n"
                        + "            th:text=\"${@topicUtils.displayName");
        assertThat(markup).as("блок рендерится даже без описания — значит на экране появится "
                        + "пустая рамка вместо материала")
                .contains("@topicUtils.description(current.question.topic) != null");
        assertThat(markup).as("описание тянется не из бина TopicUtils, то есть завёлся второй "
                        + "источник материала темы")
                .contains("th:text=\"${@topicUtils.description(current.question.topic)}\"");
    }
}
