package com.cheatsheet.quiz;

import static org.assertj.core.api.Assertions.assertThat;

import com.cheatsheet.quiz.config.app.AppProperties;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.service.imports.MarkdownQuestionParser;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class MarkdownQuestionParserTest {

    @TempDir
    Path tempDir;

    @Test
    void parsesQuestionsWithNewImportantMarker() throws Exception {
        String content = """
                # Заголовок

                ## Полезные ссылки

                ## Q1. (!) Что такое тест?

                Это ответ на первый вопрос.

                ## Q2. Второй вопрос?

                Второй ответ.
                """;
        Path temp = tempDir.resolve("interview.md");
        Files.writeString(temp, content);

        MarkdownQuestionParser parser = new MarkdownQuestionParser(new AppProperties());
        List<MarkdownQuestionParser.ParsedQuestion> questions = parser.parse(temp);

        assertThat(questions).hasSize(2);
        assertThat(questions.get(0).questionText()).isEqualTo("Что такое тест?");
        assertThat(questions.get(0).important()).isTrue();
        assertThat(questions.get(0).answerMarkdown()).contains("Это ответ");
        assertThat(questions.get(1).questionText()).isEqualTo("Второй вопрос?");
        assertThat(questions.get(1).important()).isFalse();
    }

    @Test
    void parsesQuestionsWithLegacyImportantMarker() throws Exception {
        String content = """
                ## Q1. (ВАЖНО) Что такое legacy?

                Ответ про legacy маркер.
                """;
        Path temp = tempDir.resolve("legacy.md");
        Files.writeString(temp, content);

        MarkdownQuestionParser parser = new MarkdownQuestionParser(new AppProperties());
        List<MarkdownQuestionParser.ParsedQuestion> questions = parser.parse(temp);

        assertThat(questions).hasSize(1);
        assertThat(questions.get(0).questionText()).isEqualTo("Что такое legacy?");
        assertThat(questions.get(0).important()).isTrue();
    }

    @Test
    void parsesQuestionsWithoutDotAfterNumber() throws Exception {
        String content = """
                ## Q1 Первый вопрос без точки?

                Ответ 1.

                ## Q2 Второй вопрос без точки?

                Ответ 2.
                """;
        Path temp = tempDir.resolve("no-dot.md");
        Files.writeString(temp, content);

        MarkdownQuestionParser parser = new MarkdownQuestionParser(new AppProperties());
        List<MarkdownQuestionParser.ParsedQuestion> questions = parser.parse(temp);

        assertThat(questions).hasSize(2);
        assertThat(questions.get(0).questionText()).isEqualTo("Первый вопрос без точки?");
        assertThat(questions.get(1).questionText()).isEqualTo("Второй вопрос без точки?");
    }

    @Test
    void detectsCodeTypeAndExtractsSnippet() throws Exception {
        String content = """
                ## Q1. Что выведет этот код?

                Код выведет 42.

                ```java
                public static void main(String[] args) {
                    int x = 21;
                    System.out.println(x * 2);
                }
                ```

                Результат: 42.
                """;
        Path temp = tempDir.resolve("code-question.md");
        Files.writeString(temp, content);

        MarkdownQuestionParser parser = new MarkdownQuestionParser(new AppProperties());
        List<MarkdownQuestionParser.ParsedQuestion> questions = parser.parse(temp);

        assertThat(questions).hasSize(1);
        assertThat(questions.get(0).questionType()).isEqualTo(QuestionType.CODE);
        assertThat(questions.get(0).codeSnippet()).contains("public static void main");
        assertThat(questions.get(0).codeSnippet()).contains("System.out.println(x * 2)");
    }

    @Test
    void normalizesQuestionWithoutQuestionMark() throws Exception {
        String content = """
                ## Q1. Объясни принцип Dependency Injection

                Ответ.
                """;
        Path temp = tempDir.resolve("normalize-question.md");
        Files.writeString(temp, content);

        MarkdownQuestionParser parser = new MarkdownQuestionParser(new AppProperties());
        List<MarkdownQuestionParser.ParsedQuestion> questions = parser.parse(temp);

        assertThat(questions).hasSize(1);
        assertThat(questions.get(0).questionText()).isEqualTo("Объясни принцип Dependency Injection");
    }

    @Test
    void normalizesAmbiguousQuestionMarkers() throws Exception {
        String content = """
                ## Q1. Какие есть типы изоляции транзакций

                Ответ.

                ## Q2. Что лучше использовать JOIN или подзапросы?

                Ответ 2.
                """;
        Path temp = tempDir.resolve("ambiguous-normalize.md");
        Files.writeString(temp, content);

        MarkdownQuestionParser parser = new MarkdownQuestionParser(new AppProperties());
        List<MarkdownQuestionParser.ParsedQuestion> questions = parser.parse(temp);

        assertThat(questions).hasSize(2);
        assertThat(questions.get(0).questionText()).isEqualTo("Какие есть типы изоляции транзакций");
        assertThat(questions.get(1).questionText()).isEqualTo("Что лучше использовать JOIN или подзапросы?");
    }
}
