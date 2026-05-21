package com.cheatsheet.quiz.service.imports;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.cheatsheet.quiz.config.app.AppProperties;
import com.cheatsheet.quiz.domain.QuestionType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.LoggerFactory;

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
    void noWarningEmittedForMultipleMcqBlocksAfterV15Migration() throws IOException {
        // После V15 миграции (uq на (question_id, mcq_block_idx)) несколько MCQ блоков
        // на один Q — допустимо, парсер сохраняет все с инкрементом mcqBlockIdx,
        // никаких WARN не должно быть.
        Logger parserLogger = (Logger) LoggerFactory.getLogger(MarkdownQuestionParser.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        parserLogger.addAppender(appender);
        try {
            Path file = tempDir.resolve("transactions.md");
            Files.writeString(file, """
                    ## Q7. (!) dirty/non-repeatable/phantom?

                    Текст ответа.

                    > [!mcq]
                    > - [x] правильный | объяснение
                    > - [ ] неправильный 1 | объяснение
                    > - [ ] неправильный 2 | объяснение
                    > - [ ] неправильный 3 | объяснение

                    > [!mcq]
                    > - [ ] вариант A | объяснение
                    > - [x] вариант B | объяснение
                    > - [ ] вариант C | объяснение
                    > - [ ] вариант D | объяснение
                    """);

            List<MarkdownQuestionParser.ParsedQuestion> questions = parser().parse(file);

            // Оба блока попали в опции
            assertThat(questions).hasSize(1);
            assertThat(questions.get(0).options()).hasSize(8);
            // Нет WARN-сообщений о пропуске
            assertThat(appender.list)
                    .filteredOn(e -> e.getLevel() == Level.WARN)
                    .filteredOn(e -> e.getFormattedMessage().contains("пропущено"))
                    .as("no skip-warnings after multi-block support")
                    .isEmpty();
        } finally {
            parserLogger.detachAppender(appender);
        }
    }

    @Test
    void capturesAllMcqBlocksWithBlockIndex() throws IOException {
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
        // Multi-block MCQ: оба блока сохранены, идентифицируются mcqBlockIdx 0 и 1
        assertThat(q.options()).hasSize(8);

        long block0Count = q.options().stream().filter(o -> o.mcqBlockIdx() == 0).count();
        long block1Count = q.options().stream().filter(o -> o.mcqBlockIdx() == 1).count();
        assertThat(block0Count).as("4 опции в блоке 0").isEqualTo(4);
        assertThat(block1Count).as("4 опции в блоке 1").isEqualTo(4);

        // В каждом блоке ровно один correct
        long correctBlock0 = q.options().stream()
                .filter(o -> o.mcqBlockIdx() == 0 && o.correct())
                .count();
        long correctBlock1 = q.options().stream()
                .filter(o -> o.mcqBlockIdx() == 1 && o.correct())
                .count();
        assertThat(correctBlock0).isEqualTo(1);
        assertThat(correctBlock1).isEqualTo(1);

        assertThat(q.options().get(0).text()).contains("Dirty read");
        assertThat(q.options().get(4).text()).contains("Phantom");
    }

    @Test
    void capturesMultiLineExplanationFromIndentedSections() throws IOException {
        Path file = tempDir.resolve("multiline-mcq.md");
        Files.writeString(file, """
                ## Q1. (!) `@Transactional` rollback behavior?

                Spring AOP-proxy перехватывает вызов и решает rollback по типу исключения.

                > [!mcq] Что произойдёт при checked exception?
                >
                > - [ ] A. Транзакция автоматически откатится — Spring трактует любое исключение как rollback.
                >
                >     **Что на самом деле.** Spring откатывает только на RuntimeException и Error.
                >
                >     **Откуда путаница.** Аналогия с try-catch-finally.
                >
                >     **Если бы это было правдой.** Не нужен был бы rollbackFor параметр.
                >
                >     **Как было бы правильно.** Бросать unchecked exception или указать rollbackFor.
                >
                > - [x] B. Транзакция закоммитится, изменение попадёт в БД, исключение пробросится наверх.
                >
                >     **Развёрнутое объяснение.** Default-логика в DefaultTransactionAttribute.rollbackOn() возвращает true только для RuntimeException и Error.
                >
                >     **Пример.** Сервис списывает деньги и шлёт email — checked exception оставит деньги списанными.
                >
                >     **Когда применять.** Знать всегда — это default Spring behavior.
                >
                >     **Подводные камни.** rollbackFor не наследуется через REQUIRES_NEW.
                >
                >     **Связанные вопросы.** [[Q5]] propagation; [[Q12]] self-invocation.
                """);

        List<MarkdownQuestionParser.ParsedQuestion> questions = parser().parse(file);

        assertThat(questions).hasSize(1);
        MarkdownQuestionParser.ParsedQuestion q = questions.get(0);
        assertThat(q.options()).hasSize(2);

        // Wrong option: 4 секции аккумулируются как explanation
        MarkdownQuestionParser.ParsedOption wrong = q.options().get(0);
        assertThat(wrong.correct()).isFalse();
        assertThat(wrong.text()).startsWith("A.");
        assertThat(wrong.explanation())
                .as("multi-line explanation accumulates all named sections")
                .contains("**Что на самом деле.** Spring откатывает только на RuntimeException")
                .contains("**Откуда путаница.** Аналогия с try-catch-finally")
                .contains("**Если бы это было правдой.** Не нужен был бы rollbackFor")
                .contains("**Как было бы правильно.** Бросать unchecked");

        // Correct option: 5 секций
        MarkdownQuestionParser.ParsedOption correct = q.options().get(1);
        assertThat(correct.correct()).isTrue();
        assertThat(correct.text()).startsWith("B.");
        assertThat(correct.explanation())
                .contains("**Развёрнутое объяснение.** Default-логика")
                .contains("**Пример.** Сервис списывает деньги")
                .contains("**Когда применять.** Знать всегда")
                .contains("**Подводные камни.** rollbackFor не наследуется")
                .contains("**Связанные вопросы.** [[Q5]] propagation");
    }

    @Test
    void inlinePipeExplanationStillWorksForLegacyFormat() throws IOException {
        Path file = tempDir.resolve("legacy-mcq.md");
        Files.writeString(file, """
                ## Q1. Default GC?

                > [!mcq]
                > - [x] G1 | G1 — default с Java 9
                > - [ ] CMS | Удалён в Java 14
                """);

        List<MarkdownQuestionParser.ParsedQuestion> questions = parser().parse(file);
        assertThat(questions.get(0).options()).hasSize(2);
        assertThat(questions.get(0).options().get(0).explanation()).isEqualTo("G1 — default с Java 9");
        assertThat(questions.get(0).options().get(1).explanation()).isEqualTo("Удалён в Java 14");
    }

    @Test
    void mcqCalloutWithInlineQuestionHeaderStillRecognized() throws IOException {
        Path file = tempDir.resolve("inline-q.md");
        Files.writeString(file, """
                ## Q1. Topic

                > [!mcq] What is X?
                > - [x] Correct answer here
                > - [ ] Wrong A
                > - [ ] Wrong B
                > - [ ] Wrong C
                """);

        List<MarkdownQuestionParser.ParsedQuestion> questions = parser().parse(file);
        assertThat(questions.get(0).options()).hasSize(4);
        assertThat(questions.get(0).options().get(0).correct()).isTrue();
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
