package com.cheatsheet.quiz.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.cheatsheet.quiz.service.diagram.MermaidSanitizer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Тесты для {@link MermaidSanitizer#sanitizeMermaid(String)}.
 */
class DiagramServiceTest {

    private final MermaidSanitizer mermaidSanitizer = new MermaidSanitizer();

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t\n"})
    void sanitizeMermaidReturnsInputForNullOrBlank(String input) {
        String result = mermaidSanitizer.sanitizeMermaid(input);
        if (input == null) {
            assertThat(result).isNull();
        } else {
            // blank strings are returned as-is
            assertThat(result).isEqualTo(input);
        }
    }

    @Test
    void sanitizeMermaidRemovesCodeFences() {
        String input = "```mermaid\ngraph TD\nA-->B\n```";
        String result = mermaidSanitizer.sanitizeMermaid(input);

        assertThat(result).doesNotContain("```");
        assertThat(result).contains("graph TD");
        assertThat(result).contains("A-->B");
    }

    @Test
    void sanitizeMermaidSplitsSingleLineOnSemicolon() {
        String input = "graph LR;A-->B;B-->C";
        String result = mermaidSanitizer.sanitizeMermaid(input);

        assertThat(result).contains("graph LR");
        assertThat(result).contains("A-->B");
        assertThat(result).contains("B-->C");
        assertThat(result).doesNotContain(";");
    }

    @Test
    void sanitizeMermaidRemovesEmptyEdgeLabels() {
        String input = "graph TD\nA-->||B\nC-->| |D";
        String result = mermaidSanitizer.sanitizeMermaid(input);

        assertThat(result).contains("A-->B");
        assertThat(result).contains("C-->D");
        assertThat(result).doesNotContain("-->||");
        assertThat(result).doesNotContain("-->| |");
    }

    @Test
    void sanitizeMermaidQuotesSpecialCharsInNodeLabels() {
        String input = "graph TD\nA[O(n)]";
        String result = mermaidSanitizer.sanitizeMermaid(input);

        assertThat(result).contains("A[\"O(n)\"]");
    }

    @Test
    void sanitizeMermaidDoesNotQuoteSimpleLabels() {
        String input = "graph TD\nA[Simple Label]";
        String result = mermaidSanitizer.sanitizeMermaid(input);

        assertThat(result).contains("A[Simple Label]");
        assertThat(result).doesNotContain("\"");
    }

    @Test
    void sanitizeMermaidHandlesMultipleSpecialNodes() {
        String input = "graph TD\nA[List<String>] --> B[Map(K,V)]";
        String result = mermaidSanitizer.sanitizeMermaid(input);

        assertThat(result).contains("A[\"List<String>\"]");
        assertThat(result).contains("B[\"Map(K,V)\"]");
    }

    @Test
    void sanitizeMermaidPreservesAlreadyCleanCode() {
        String input = "graph TD\n    A[Start] --> B[End]";
        String result = mermaidSanitizer.sanitizeMermaid(input);

        assertThat(result).isEqualTo(input);
    }

    @Test
    void sanitizeMermaidHandlesComplexRealWorldDiagram() {
        String input = """
                ```mermaid
                graph LR
                A[HashMap] -->|O(1)| B[get/put]
                C[TreeMap] -->|O(log n)| D[get/put]
                ```""";
        String result = mermaidSanitizer.sanitizeMermaid(input);

        assertThat(result).doesNotContain("```");
        assertThat(result).contains("graph LR");
        // Edge labels (-->|...|) are preserved as-is
        assertThat(result).contains("-->|O(1)|");
        assertThat(result).contains("-->|O(log n)|");
        // Node labels without special chars (HashMap, get/put) — get/put has / which is not a special char
        assertThat(result).contains("A[HashMap]");
    }

    @Test
    void sanitizeMermaidQuotesNodeLabelsWithParens() {
        // Node labels with special chars inside [...] should be quoted
        String input = "graph TD\nA[HashMap] --> B[O(1) complexity]";
        String result = mermaidSanitizer.sanitizeMermaid(input);

        assertThat(result).contains("B[\"O(1) complexity\"]");
        assertThat(result).contains("A[HashMap]");
    }
}
