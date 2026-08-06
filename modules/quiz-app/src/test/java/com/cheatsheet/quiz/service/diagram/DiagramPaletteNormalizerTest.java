package com.cheatsheet.quiz.service.diagram;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class DiagramPaletteNormalizerTest {

    private final DiagramPaletteNormalizer normalizer = new DiagramPaletteNormalizer();

    @Test
    void keepsDiagramWithoutColorsUntouched() {
        String diagram = """
                graph TD
                  A[Клиент] --> B[Сервер]""";

        assertThat(normalizer.normalize(diagram)).isEqualTo(diagram);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void keepsBlankInputUntouched(String input) {
        assertThat(normalizer.normalize(input)).isEqualTo(input);
    }

    @Test
    void keepsNullUntouched() {
        assertThat(normalizer.normalize(null)).isNull();
    }

    @Test
    void replacesStyleStatementWithSemanticClass() {
        String diagram = """
                graph TD
                  A[Ошибка] --> B[Успех]
                  style A fill:#ff6b6b,color:#fff
                  style B fill:#4caf50,color:#fff""";

        assertThat(normalizer.normalize(diagram)).isEqualTo("""
                graph TD
                  A[Ошибка] --> B[Успех]
                  class A dgmDanger
                  class B dgmSuccess""");
    }

    /**
     * Оттенки берутся из реального корпуса: границы полос подбирались так, чтобы
     * material-пастель («светло-голубой» #e1f5fe) не уехала в бирюзу бренда,
     * а бирюза (#4ecdc4) — в синий.
     */
    @ParameterizedTest
    @CsvSource({
            "#ff6b6b, dgmDanger",
            "#e74c3c, dgmDanger",
            "#ffcdd2, dgmDanger",
            "#fdd,     dgmDanger",
            "#ffd93d, dgmWarning",
            "#ff9800, dgmWarning",
            "#fff3e0, dgmWarning",
            "#f96,     dgmWarning",
            "#4caf50, dgmSuccess",
            "#90ee90, dgmSuccess",
            "#e8f5e9, dgmSuccess",
            "#4ecdc4, dgmAccent",
            "#f9f,     dgmAccent",
            "#f3e5f5, dgmAccent",
            "#e1f5fe, dgmInfo",
            "#2196f3, dgmInfo",
            "#4a90d9, dgmInfo",
            "#333,     dgmNeutral",
            "#fff,     dgmNeutral",
            "#888,     dgmNeutral"
    })
    void classifiesCorpusColorsBySemantics(String color, String expectedClass) {
        String result = normalizer.normalize("graph TD\n  A[X]\n  style A fill:" + color);

        assertThat(result).endsWith("class A " + expectedClass);
    }

    @Test
    void fallsBackToStrokeWhenFillIsAbsent() {
        String result = normalizer.normalize("graph TD\n  A[X]\n  style A stroke:#c0392b,stroke-width:2px");

        assertThat(result).endsWith("class A dgmDanger");
    }

    @Test
    void keepsColorlessStyleStatement() {
        String diagram = "graph TD\n  A[X]\n  style A stroke-width:4px";

        assertThat(normalizer.normalize(diagram)).isEqualTo(diagram);
    }

    /**
     * {@code #} встречается в подписях рёбер (в корпусе — {@code -->|"#access_token"|}).
     * Нормализатор смотрит только на операторы style/classDef, поэтому подпись цела.
     */
    @Test
    void doesNotTouchHashInsideEdgeLabel() {
        String diagram = """
                graph LR
                  C2 -->|"#access_token"| A2
                  style A2 fill:#2196f3""";

        assertThat(normalizer.normalize(diagram)).isEqualTo("""
                graph LR
                  C2 -->|"#access_token"| A2
                  class A2 dgmInfo""");
    }

    @Test
    void retargetsClassReferencesWhenClassDefIsRemoved() {
        String diagram = """
                graph TD
                  A --> B
                  classDef cycle fill:#fdd,stroke:#c00;
                  class A,B cycle""";

        assertThat(normalizer.normalize(diagram)).isEqualTo("""
                graph TD
                  A --> B
                  class A,B dgmDanger""");
    }

    @Test
    void keepsClassReferenceToClassDefThatSurvived() {
        String diagram = """
                graph TD
                  A --> B
                  classDef fat stroke-width:4px
                  class A fat""";

        assertThat(normalizer.normalize(diagram)).isEqualTo(diagram);
    }

    @Test
    void toleratesTrailingSemicolonsAndIndentation() {
        String result = normalizer.normalize("graph TD\n    style A fill:#4caf50;");

        assertThat(result).isEqualTo("graph TD\n    class A dgmSuccess");
    }
}
