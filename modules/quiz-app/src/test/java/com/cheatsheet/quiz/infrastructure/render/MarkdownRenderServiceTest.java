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
    void stripsMermaidFenceEntirely() {
        // Проект отказался от mermaid: блок ```mermaid``` вырезается целиком,
        // в выводе нет ни диаграммы, ни сырого исходника.
        String html = service.toHtml("Текст до.\n\n```mermaid\ngraph TD; A-->B;\n```\n\nТекст после.");

        assertThat(html).doesNotContain("mermaid");
        assertThat(html).doesNotContain("graph TD");
        assertThat(html).contains("Текст до");
        assertThat(html).contains("Текст после");
    }

    @Test
    void stripsMultilineMermaidWithoutLeakingSource() {
        // Многострочная диаграмма тоже вырезается полностью — окружающая проза цела.
        String md = "Описание.\n\n```mermaid\nflowchart LR\n    A --> B\n    B --> C\n```\n\nИтог.";
        String html = service.toHtml(md);

        assertThat(html).doesNotContain("flowchart");
        assertThat(html).doesNotContain("mermaid");
        assertThat(html).contains("Описание");
        assertThat(html).contains("Итог");
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

    @Test
    void inlineHtmlRendersInlineCodeWithoutParagraphWrapper() {
        // Регрессия: текст варианта рендерился th:text → пользователь видел литералы
        // `HashMap` с бэктиками. toInlineHtml даёт <code>, и БЕЗ блочного <p>, чтобы
        // не ломать инлайн-раскладку label (бейдж + текст в одну строку).
        String html = service.toInlineHtml("Использовать `HashMap` для O(1)");

        assertThat(html).contains("<code>HashMap</code>");
        assertThat(html).doesNotContain("`");
        assertThat(html).doesNotStartWith("<p>");
        assertThat(html).doesNotContain("</p>");
    }

    @Test
    void inlineHtmlRendersBoldAndItalicInline() {
        String html = service.toInlineHtml("**важно** и *тонко*");

        assertThat(html).contains("<strong>важно</strong>");
        assertThat(html).contains("<em>тонко</em>");
        assertThat(html).doesNotContain("<p>");
    }

    @Test
    void inlineHtmlLeavesPlainTextUntouched() {
        String html = service.toInlineHtml("Просто текст без разметки");

        assertThat(html).isEqualTo("Просто текст без разметки");
    }

    @Test
    void inlineHtmlStripsDangerousTags() {
        String html = service.toInlineHtml("ok <script>alert(1)</script> `code`");

        assertThat(html).doesNotContain("<script");
        assertThat(html).doesNotContain("alert(1)");
        assertThat(html).contains("<code>code</code>");
    }

    @Test
    void inlineHtmlReturnsEmptyOnNullOrBlank() {
        assertThat(service.toInlineHtml(null)).isEmpty();
        assertThat(service.toInlineHtml("")).isEmpty();
        assertThat(service.toInlineHtml("   ")).isEmpty();
    }

    @Test
    void inlineHtmlStripsJavascriptLinks() {
        // Узкий INLINE_SAFELIST не разрешает <a> вовсе → javascript:-href невозможен,
        // остаётся только текст метки.
        String html = service.toInlineHtml("жми [сюда](javascript:alert(document.cookie))");

        assertThat(html).doesNotContain("javascript");
        assertThat(html).doesNotContain("<a");
        assertThat(html).contains("сюда");
    }

    @Test
    void inlineHtmlStripsImageWithEventHandler() {
        // raw inline HTML с on*-обработчиком должен быть вычищен (атрибуты не разрешены,
        // тег <img> не в allow-list).
        String html = service.toInlineHtml("текст <img src=x onerror=alert(1)> ещё");

        assertThat(html).doesNotContain("<img");
        assertThat(html).doesNotContain("onerror");
        assertThat(html).doesNotContain("alert(1)");
    }

    @Test
    void inlineHtmlStripsLanguageClassButKeepsCode() {
        // INLINE_SAFELIST не разрешает атрибуты — class на <code> отбрасывается,
        // но сам инлайн-код сохраняется.
        String html = service.toInlineHtml("`x`");

        assertThat(html).isEqualTo("<code>x</code>");
    }
}
