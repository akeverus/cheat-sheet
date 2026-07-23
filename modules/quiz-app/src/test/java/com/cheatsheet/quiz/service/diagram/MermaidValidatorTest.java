package com.cheatsheet.quiz.service.diagram;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Характеризационные тесты {@link MermaidValidator} — фиксируют текущее поведение
 * структурной и контекстной валидации Mermaid-кода: границы количества строк
 * ({@code MIN_LINES}=3, {@code MAX_LINES}=30), распознавание типа диаграммы,
 * баланс кавычек/скобок и семантику {@link MermaidValidator#isRelevantMermaid}
 * (пересечение токенов диаграммы с контекстом вопроса, отсев generic-меток).
 */
class MermaidValidatorTest {

    private final MermaidValidator validator = new MermaidValidator();

    /** Минимально валидная диаграмма: объявление + 2 связи, без скобок/кавычек. */
    private static final String MINIMAL_VALID = """
            flowchart TD
            A --> B
            B --> C
            """;

    // ── isValidMermaid: null / blank ─────────────────────────────────────────

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\n\t \n"})
    void nullOrBlankCodeIsInvalid(String code) {
        assertThat(validator.isValidMermaid(code)).isFalse();
    }

    @Test
    void minimalWellFormedCodeIsValid() {
        assertThat(validator.isValidMermaid(MINIMAL_VALID)).isTrue();
    }

    @Test
    void invalidTypeMakesWholeCodeInvalid() {
        String code = """
                mindmap root
                A --> B
                B --> C
                """;
        assertThat(validator.isValidMermaid(code)).isFalse();
    }

    @Test
    void unbalancedBracketsMakeWholeCodeInvalid() {
        String code = """
                flowchart TD
                A[open --> B
                B --> C
                """;
        assertThat(validator.isValidMermaid(code)).isFalse();
    }

    // ── validateLineCount: границы MIN_LINES/MAX_LINES ───────────────────────

    @Test
    void tooFewNonEmptyLinesRejected() {
        // 2 непустые строки < MIN_LINES(3).
        assertThat(validator.validateLineCount("flowchart TD\nA --> B\n")).isFalse();
    }

    @Test
    void exactlyMinLinesAccepted() {
        assertThat(validator.validateLineCount("a\nb\nc")).isTrue();
    }

    @Test
    void blankLinesDoNotCountTowardMinimum() {
        // 6 физических строк, но только 2 непустые → отклонено.
        assertThat(validator.validateLineCount("a\n\n\nb\n\n")).isFalse();
    }

    @Test
    void exactlyMaxLinesAccepted() {
        assertThat(validator.validateLineCount(repeatLines(MermaidValidator.MAX_LINES))).isTrue();
    }

    @Test
    void oneOverMaxLinesRejected() {
        assertThat(validator.validateLineCount(repeatLines(MermaidValidator.MAX_LINES + 1))).isFalse();
    }

    private static String repeatLines(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append("node").append(i).append('\n');
        }
        return sb.toString();
    }

    // ── validateDiagramType ──────────────────────────────────────────────────

    @ParameterizedTest
    @ValueSource(strings = {
            "flowchart TD", "graph LR", "sequenceDiagram", "classDiagram",
            "stateDiagram", "stateDiagram-v2", "erDiagram", "pie title X", "gantt"
    })
    void recognizesEveryValidDiagramTypeCaseInsensitive(String firstLine) {
        assertThat(validator.validateDiagramType(firstLine + "\nA --> B")).isTrue();
    }

    @Test
    void leadingBlankLinesSkippedWhenDetectingType() {
        assertThat(validator.validateDiagramType("\n\n   \nflowchart TD\nA --> B")).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"mindmap", "journey", "erd something", "random text"})
    void unknownFirstLineTypeRejected(String firstLine) {
        assertThat(validator.validateDiagramType(firstLine + "\nA --> B")).isFalse();
    }

    @Test
    void allBlankCodeHasNoDetectableType() {
        assertThat(validator.validateDiagramType("\n   \n\t")).isFalse();
    }

    // ── validateBalance ──────────────────────────────────────────────────────

    @Test
    void balancedQuotesAndBracketsPass() {
        assertThat(validator.validateBalance("A[\"label\"] --> B[\"x\"]")).isTrue();
    }

    @Test
    void oddQuotesFail() {
        assertThat(validator.validateBalance("A[\"label] --> B")).isFalse();
    }

    @Test
    void unclosedBracketFails() {
        assertThat(validator.validateBalance("A[label --> B")).isFalse();
    }

    @Test
    void loneClosingBracketFails() {
        // Итоговый счётчик скобок = -1 → небаланс.
        assertThat(validator.validateBalance("A]label --> B")).isFalse();
    }

    @Test
    void reorderedBracketsNettingToZeroIncorrectlyPass() {
        // Характеризация: валидатор считает только ИТОГ, не порядок — '][' даёт 0 и проходит.
        assertThat(validator.validateBalance("A]label[ --> B")).isTrue();
    }

    // ── isRelevantMermaid: ветки контекста ───────────────────────────────────

    @Test
    void structurallyInvalidCodeIsNeverRelevant() {
        assertThat(validator.isRelevantMermaid("not a diagram", "вопрос про кэш", "ответ про кэш"))
                .isFalse();
    }

    @Test
    void emptyContextTokensShortCircuitToRelevant() {
        // Контекст из токенов <4 символов → пустой → диаграмма считается релевантной.
        assertThat(validator.isRelevantMermaid(MINIMAL_VALID, "как", "ttl")).isTrue();
    }

    @Test
    void validDiagramWithoutLabelsIsIrrelevantWhenContextPresent() {
        // Нет [меток]/|подписей| → diagramTokens пусты → не релевантна.
        assertThat(validator.isRelevantMermaid(
                MINIMAL_VALID,
                "Как работает механизм TTL в Redis cache?",
                "TTL определяет время жизни ключа."
        )).isFalse();
    }

    @Test
    void insufficientTokenOverlapRejected() {
        String code = """
                flowchart TD
                A[elephant] --> B[giraffe]
                B --> C[penguin]
                """;
        assertThat(validator.isRelevantMermaid(
                code,
                "Как работает механизм TTL в Redis cache?",
                "TTL определяет время жизни ключа."
        )).isFalse();
    }

    @Test
    void allGenericLabelsRejectedEvenWithOverlap() {
        // Метки полностью generic и пересекаются с контекстом → mostlyGeneric → отклонено.
        String code = """
                flowchart TD
                A[обработка] --> B[процесс]
                B --> C[данные]
                """;
        assertThat(validator.isRelevantMermaid(
                code,
                "Опиши обработка процесс данные подробно",
                "обработка процесс данные это ключевые шаги"
        )).isFalse();
    }

    @Test
    void acceptsRelevantMermaidWithDomainTerms() {
        String code = """
                flowchart TD
                    A[TTL cache key] --> B{ttl expired}
                    B -- yes --> C[invalidate cache]
                    B -- no --> D[return cached value]
                    C --> E[load from database]
                """;

        boolean relevant = validator.isRelevantMermaid(
                code,
                "Как работает механизм TTL в Redis cache?",
                "TTL определяет время жизни key, после истечения нужен invalidate и чтение из базы."
        );

        assertThat(relevant).isTrue();
    }

    @Test
    void rejectsGenericMermaidWithoutQuestionContextOverlap() {
        String code = """
                flowchart TD
                    A[Обработка] --> B{Этап}
                    B -- yes --> C[Логика]
                    B -- no --> D[Процесс]
                """;

        boolean relevant = validator.isRelevantMermaid(
                code,
                "Как работает механизм TTL в Redis cache?",
                "Нужно проверить ttl и удалить просроченный ключ."
        );

        assertThat(relevant).isFalse();
    }

    @Test
    void edgeLabelsContributeToRelevance() {
        // Токены берутся и из |подписей рёбер|, не только из [узлов].
        String code = """
                flowchart TD
                A --> |транзакция откат| B
                B --> |транзакция commit| C
                """;
        assertThat(validator.isRelevantMermaid(
                code,
                "Что происходит при откат транзакция в базе?",
                "При откат транзакция изменения отменяются, commit фиксирует."
        )).isTrue();
    }
}
