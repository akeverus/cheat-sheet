package com.cheatsheet.quiz.service.diagram;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MermaidSanitizerTest {

    private final MermaidSanitizer sanitizer = new MermaidSanitizer();

    @Test
    void returnsNullOrBlankUnchanged() {
        assertThat(sanitizer.sanitizeMermaid(null)).isNull();
        assertThat(sanitizer.sanitizeMermaid("")).isEmpty();
        assertThat(sanitizer.sanitizeMermaid("   ")).isEqualTo("   ");
    }

    @Test
    void stripsLeadingAndTrailingCodeFences() {
        String input = "```mermaid\ngraph TD\nA-->B\n```";

        String cleaned = sanitizer.sanitizeMermaid(input);

        assertThat(cleaned).doesNotContain("```");
        assertThat(cleaned).startsWith("graph TD");
    }

    @Test
    void splitsSingleLineDiagramBySemicolons() {
        String oneLiner = "graph TD; A-->B; B-->C";

        String cleaned = sanitizer.sanitizeMermaid(oneLiner);

        assertThat(cleaned.lines().count()).isGreaterThanOrEqualTo(3);
        assertThat(cleaned).contains("graph TD");
        assertThat(cleaned).contains("A-->B");
        assertThat(cleaned).contains("B-->C");
    }

    @Test
    void removesEmptyEdgeLabels() {
        String input = "graph TD\nA-->||B";

        String cleaned = sanitizer.sanitizeMermaid(input);

        assertThat(cleaned).doesNotContain("-->||");
        assertThat(cleaned).contains("A-->B");
    }

    @Test
    void quotesNodeLabelsContainingSpecialChars() {
        // O(n) внутри [...] должно превратиться в ["O(n)"], иначе mermaid падает.
        String input = "graph TD\nA[O(n) lookup]";

        String cleaned = sanitizer.sanitizeMermaid(input);

        assertThat(cleaned).contains("A[\"O(n) lookup\"]");
    }

    @Test
    void leavesPlainNodeLabelsAlone() {
        String input = "graph TD\nA[hello world]";

        String cleaned = sanitizer.sanitizeMermaid(input);

        assertThat(cleaned).contains("A[hello world]");
        assertThat(cleaned).doesNotContain("A[\"hello world\"]");
    }

    @Test
    void leavesAlreadyQuotedLabelAlone() {
        // Если AI уже корректно обернул label в кавычки, sanitizer не должен
        // его трогать (regex NODE_LABEL по дизайну не матчит ["..."]).
        String input = "graph TD\nA[\"O(n)\"]";

        String cleaned = sanitizer.sanitizeMermaid(input);

        assertThat(cleaned).contains("A[\"O(n)\"]");
        // ни двойной кавычки, ни дублей
        assertThat(cleaned).doesNotContain("A[\"\"");
    }

    @Test
    void quoteSpecialLabelsIsIndependentlyCallable() {
        // Метод используется напрямую для line-level дочистки.
        String line = "X[count: 5]";

        assertThat(sanitizer.quoteSpecialLabels(line)).isEqualTo("X[\"count: 5\"]");
    }
}
