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
    void parsesMcqBlock() throws IOException {
        Path file = tempDir.resolve("mcq.md");
        Files.writeString(file, """
                ## Q1. (!) Что является default GC в Java 9+?

                Краткий ответ на вопрос.

                > [!mcq]
                > - [ ] Parallel GC — стал default с Java 9 | Неверно, Parallel был default до Java 8
                > - [x] G1 — стал default с Java 9, заменив Parallel GC | G1 оптимизирован для больших heap
                > - [ ] ZGC — стал default с Java 11 | ZGC достиг production в Java 15
                > - [ ] Shenandoah — стал default с Java 12 в OpenJDK | Shenandoah никогда не был default
                """);

        List<MarkdownQuestionParser.ParsedQuestion> questions = parser().parse(file);

        assertThat(questions).hasSize(1);
        MarkdownQuestionParser.ParsedQuestion q = questions.get(0);
        assertThat(q.hasMcqOptions()).isTrue();
        assertThat(q.options()).hasSize(4);
        assertThat(q.options().stream().filter(MarkdownQuestionParser.ParsedOption::correct).count()).isEqualTo(1);
        assertThat(q.options().stream().filter(MarkdownQuestionParser.ParsedOption::correct).findFirst())
                .map(MarkdownQuestionParser.ParsedOption::text)
                .hasValueSatisfying(t -> assertThat(t).contains("G1"));
        // MCQ block must NOT appear in stored answerMarkdown
        assertThat(q.answerMarkdown()).doesNotContain("[!mcq]");
        // rawAnswer must include the MCQ block (for hashing)
        assertThat(q.rawAnswer()).contains("[!mcq]");
        // explanations parsed correctly
        assertThat(q.options().get(0).explanation()).contains("Parallel был default до Java 8");
    }

    @Test
    void usesOnlyFirstMcqBlockWhenQuestionHasMultiple() throws IOException {
        Path file = tempDir.resolve("multi-mcq.md");
        Files.writeString(file, """
                ## Q1. (!) dirty/non-repeatable/phantom?

                Описание трёх аномалий.

                > [!mcq]
                > - [x] Dirty read — чтение незакоммиченных данных | Возможен на READ UNCOMMITTED
                > - [ ] Dirty read — повторное чтение даёт другое значение | Это non-repeatable
                > - [ ] Dirty read — появление новых строк | Это phantom
                > - [ ] Dirty read — чтение старой версии MVCC | Не аномалия

                > [!mcq]
                > - [ ] Phantom предотвращается на READ COMMITTED | Нужен SERIALIZABLE
                > - [x] Phantom — повторный SELECT с WHERE возвращает другой набор | Корректно
                > - [ ] Phantom — чтение удалённой строки | Это не phantom
                > - [ ] Phantom — чтение незакоммиченных данных | Это dirty
                """);

        List<MarkdownQuestionParser.ParsedQuestion> questions = parser().parse(file);

        assertThat(questions).hasSize(1);
        MarkdownQuestionParser.ParsedQuestion q = questions.get(0);
        // Только первый блок попал в опции
        assertThat(q.options()).hasSize(4);
        assertThat(q.options().stream().filter(MarkdownQuestionParser.ParsedOption::correct).count())
                .as("ровно один правильный ответ — без нарушения uq_answer_options_single_correct_per_question")
                .isEqualTo(1);
        assertThat(q.options().get(0).text()).contains("Dirty read");
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
}
