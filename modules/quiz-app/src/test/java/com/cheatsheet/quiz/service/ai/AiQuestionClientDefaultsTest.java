package com.cheatsheet.quiz.service.ai;

import com.cheatsheet.quiz.domain.OptionSource;
import com.cheatsheet.quiz.service.ai.dto.GeneratedOptions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Default-методы интерфейса {@link AiQuestionClient} возвращают {@code Optional.empty()}.
 * Реальные клиенты их переопределяют, но default-реализация — публичная часть контракта
 * и должна быть покрыта на случай минимального no-op клиента.
 */
class AiQuestionClientDefaultsTest {

    private final AiQuestionClient client = new MinimalClient();

    @Test
    void allOptionalGeneratorsReturnEmpty() {
        assertThat(client.sourceId()).isEqualTo(OptionSource.OPENAI);
        assertThat(client.generateOptions("q", null)).isEmpty();
        assertThat(client.generateAlternativeQuestions("q", "a", 3)).isEmpty();

        assertThat(client.generateHints("q", "a", List.of())).isEmpty();
        assertThat(client.generateDiagram("q", "a", "topic")).isEmpty();
        assertThat(client.cleanCodeSnippet("code", "q")).isEmpty();
        assertThat(client.generateWrongAnswerFeedback("q", "wrong", "right", "a")).isEmpty();
        assertThat(client.generateTakeaway("q", "a")).isEmpty();
        assertThat(client.generateComparison("q", "wrong", "right")).isEmpty();
        assertThat(client.generateCodeTrace("q", "code")).isEmpty();
        assertThat(client.canonicalizeQuestion("title", "md", "path")).isEmpty();
        assertThat(client.generateStructuredJson("prompt")).isEmpty();
    }

    @Test
    void canonicalQuestionRecordRoundtripsViaBuilder() {
        AiQuestionClient.CanonicalQuestion q = AiQuestionClient.CanonicalQuestion.builder()
                .questionText("Что такое DI?")
                .answerMarkdown("Внедрение зависимостей.")
                .questionType("TEXT")
                .codeSnippet(null)
                .build();

        assertThat(q.questionText()).isEqualTo("Что такое DI?");
        assertThat(q.answerMarkdown()).isEqualTo("Внедрение зависимостей.");
        assertThat(q.questionType()).isEqualTo("TEXT");
        assertThat(q.codeSnippet()).isNull();

        AiQuestionClient.CanonicalQuestion patched = q.toBuilder().codeSnippet("int x = 0;").build();
        assertThat(patched.codeSnippet()).isEqualTo("int x = 0;");
        assertThat(patched.questionText()).isEqualTo(q.questionText());
    }

    /** Минимальная реализация, использующая все default-методы интерфейса. */
    private static final class MinimalClient implements AiQuestionClient {
        @Override
        public OptionSource sourceId() {
            return OptionSource.OPENAI;
        }

        @Override
        public Optional<GeneratedOptions> generateOptions(String questionText, String codeSnippet) {
            return Optional.empty();
        }

        @Override
        public Optional<List<String>> generateAlternativeQuestions(String questionText, String answerMarkdown, int count) {
            return Optional.empty();
        }
    }
}
