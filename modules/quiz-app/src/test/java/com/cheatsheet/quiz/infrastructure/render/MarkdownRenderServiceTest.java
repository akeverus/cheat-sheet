package com.cheatsheet.quiz.infrastructure.render;

import com.cheatsheet.quiz.config.app.AppProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MarkdownRenderServiceTest {

    private MarkdownRenderService service;

    @BeforeEach
    void setUp() {
        AppProperties props = new AppProperties();
        props.getSearch().setPreviewMaxLength(200);
        service = new MarkdownRenderService(props);
    }

    @Test
    void rendersPlainMarkdownToHtml() {
        String html = service.toHtml("**bold** and *italic*");

        assertThat(html).contains("<strong>bold</strong>");
        assertThat(html).contains("<em>italic</em>");
    }

    @Test
    void rendersWikiLinkWithExplicitLabel() {
        String html = service.toHtml("Подробнее в [[java-string-interview|вопросах по Java String]].");

        assertThat(html).contains("href=\"/?topic=java-string-interview\"");
        assertThat(html).contains(">вопросах по Java String</a>");
    }

    @Test
    void rendersBareWikiLinkWithSlugAsLabel() {
        String html = service.toHtml("См. [[java-collections-interview]].");

        assertThat(html).contains("href=\"/?topic=java-collections-interview\"");
        assertThat(html).contains(">java-collections-interview</a>");
    }

    @Test
    void stripsScriptTagsFromOutput() {
        String html = service.toHtml("Hello <script>alert(1)</script> world");

        assertThat(html).doesNotContain("<script");
        assertThat(html).doesNotContain("alert(1)");
    }

    @Test
    void preservesCodeFenceLanguageClass() {
        String html = service.toHtml("```java\nint x = 1;\n```");

        assertThat(html).contains("<code class=\"language-java\">");
    }

    @Test
    void convertsMermaidFenceToDiagramDiv() {
        String html = service.toHtml("```mermaid\ngraph TD; A-->B;\n```");

        assertThat(html).contains("<div class=\"mermaid\">");
        assertThat(html).contains("graph TD");
        assertThat(html).contains("A--&gt;B");
    }

    @Test
    void preservesNewlinesInMermaidSource() {
        // Регрессия: Jsoup prettyPrint схлопывал переводы строк в mermaid-источнике
        // в пробелы → mermaid.js не мог распарсить многострочную диаграмму.
        String md = "```mermaid\nflowchart LR\n    A --> B\n    B --> C\n```";
        String html = service.toHtml(md);

        assertThat(html).contains("<div class=\"mermaid\">");
        // statements должны остаться на разных строках (а не «flowchart LR A --> B B --> C»)
        assertThat(html).contains("flowchart LR\n");
        assertThat(html).doesNotContain("flowchart LR     A");
    }

    @Test
    void rendersGfmTableAsHtmlTable() {
        // Регрессия: без TablesExtension flexmark схлопывал строки таблицы в <p>,
        // и пользователь видел сырой pipe-текст в пояснениях/чек-листах.
        String md = "| Вопрос | Зачем |\n|--------|-------|\n| Размер входа? | Сложность |\n";
        String html = service.toHtml(md);

        assertThat(html).contains("<table>");
        assertThat(html).contains("<th>Вопрос</th>");
        assertThat(html).contains("<td>Размер входа?</td>");
        assertThat(html).doesNotContain("| Вопрос | Зачем |");
    }

    @Test
    void returnsEmptyOnNullOrBlankInput() {
        assertThat(service.toHtml(null)).isEmpty();
        assertThat(service.toHtml("")).isEmpty();
        assertThat(service.toHtml("   ")).isEmpty();
    }

    @Test
    void extractsPlainTextWithoutMarkdown() {
        String text = service.toPlainText("**bold** _it_ `code` [link](http://x)");

        assertThat(text).isEqualTo("bold it code link");
    }
}
