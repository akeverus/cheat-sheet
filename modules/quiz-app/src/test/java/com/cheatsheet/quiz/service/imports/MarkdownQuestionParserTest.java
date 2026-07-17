package com.cheatsheet.quiz.service.imports;

import com.cheatsheet.quiz.config.app.AppProperties;
import com.cheatsheet.quiz.domain.QuestionType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MarkdownQuestionParserTest {

    @TempDir
    Path tempDir;

    private MarkdownQuestionParser parser() {
        AppProperties properties = new AppProperties();
        AppProperties.Import importSettings = new AppProperties.Import();
        importSettings.setMinCodeBlockLength(20);
        properties.setImportSettings(importSettings);
        return new MarkdownQuestionParser(properties);
    }

    @Test
    void parsesImportantQuestionAndDetectsCodeSnippet() throws IOException {
        Path file = tempDir.resolve("questions.md");
        Files.writeString(file, """
                ## Q1. Что такое DI? (!)
                DI — это внедрение зависимостей.

                ## Q2 Что выведет код?
                ```java
                public class Demo {
                    public static void main(String[] args) {
                        for (int i = 0; i < 10; i++) {
                            System.out.println(i);
                        }
                    }
                }
                ```
                """);

        List<MarkdownQuestionParser.ParsedQuestion> questions = parser().parse(file);

        assertThat(questions).hasSize(2);
        assertThat(questions.get(0).important()).isTrue();
        assertThat(questions.get(0).questionText()).doesNotContain("(!)");
        assertThat(questions.get(1).questionType()).isEqualTo(QuestionType.CODE);
        assertThat(questions.get(1).codeSnippet()).contains("for (int i = 0; i < 10; i++)");
    }

    @Test
    void routesMermaidFenceToDiagramNotCodeSnippet() throws IOException {
        Path file = tempDir.resolve("mermaid.md");
        Files.writeString(file, """
                ## Q1. Как устроены ACID-свойства транзакции?
                Транзакция обладает свойствами ACID.

                ```mermaid
                graph LR
                    A[Atomicity] --> C[Consistency]
                    C --> I[Isolation]
                    I --> D[Durability]
                ```
                """);

        List<MarkdownQuestionParser.ParsedQuestion> questions = parser().parse(file);

        assertThat(questions).hasSize(1);
        MarkdownQuestionParser.ParsedQuestion q = questions.get(0);
        // mermaid-блок — диаграмма, не код: тело идёт в diagramMermaid, а не в codeSnippet.
        assertThat(q.questionType()).isEqualTo(QuestionType.TEXT);
        assertThat(q.codeSnippet()).isNull();
        assertThat(q.diagramMermaid()).contains("graph LR").contains("Atomicity");
    }

    @Test
    void extractsBothMermaidDiagramAndCodeSnippet() throws IOException {
        Path file = tempDir.resolve("mixed.md");
        Files.writeString(file, """
                ## Q1. Что выведет код и как устроен поток?
                Разбор ниже.

                ```mermaid
                graph TD
                    Start --> Finish
                ```

                ```java
                for (int i = 0; i < 10; i++) {
                    System.out.println(i);
                }
                ```
                """);

        List<MarkdownQuestionParser.ParsedQuestion> questions = parser().parse(file);

        assertThat(questions).hasSize(1);
        MarkdownQuestionParser.ParsedQuestion q = questions.get(0);
        // Оба блока извлекаются независимо: mermaid → диаграмма, java → код.
        assertThat(q.diagramMermaid()).contains("graph TD");
        assertThat(q.questionType()).isEqualTo(QuestionType.CODE);
        assertThat(q.codeSnippet()).contains("for (int i = 0; i < 10; i++)").doesNotContain("graph TD");
    }

    @Test
    void parsesMarkdownWithoutMcqBlocks() throws IOException {
        Path file = tempDir.resolve("clean.md");
        Files.writeString(file, """
                ## Q1. (!) What is X?

                X is a thing that does Y.

                ## Q2. Why use Z?

                Z is useful because of W.
                """);

        List<MarkdownQuestionParser.ParsedQuestion> questions = parser().parse(file);

        assertThat(questions).hasSize(2);
        assertThat(questions.get(0).important()).isTrue();
        assertThat(questions.get(0).answerMarkdown()).contains("X is a thing");
        assertThat(questions.get(1).important()).isFalse();
    }
}
