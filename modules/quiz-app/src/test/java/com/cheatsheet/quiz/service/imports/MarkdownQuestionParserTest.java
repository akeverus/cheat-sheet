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

        AppProperties properties = new AppProperties();
        AppProperties.Import importSettings = new AppProperties.Import();
        importSettings.setMinCodeBlockLength(20);
        properties.setImportSettings(importSettings);
        MarkdownQuestionParser parser = new MarkdownQuestionParser(properties);

        List<MarkdownQuestionParser.ParsedQuestion> questions = parser.parse(file);

        assertThat(questions).hasSize(2);
        assertThat(questions.get(0).important()).isTrue();
        assertThat(questions.get(0).questionText()).doesNotContain("(!)");
        assertThat(questions.get(1).questionType()).isEqualTo(QuestionType.CODE);
        assertThat(questions.get(1).codeSnippet()).contains("for (int i = 0; i < 10; i++)");
    }
}
