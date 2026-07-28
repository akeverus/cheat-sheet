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
        // Mermaid возвращён (2026-06-17): блок ```mermaid``` оборачивается в
        // <div class="mermaid"> для рендера mermaid.js; окружающая проза цела.
        String html = service.toHtml("Текст до.\n\n```mermaid\ngraph TD; A-->B;\n```\n\nТекст после.");

        assertThat(html).contains("<div class=\"mermaid\">");
        assertThat(html).contains("graph TD");
        assertThat(html).contains("A--&gt;B");
        assertThat(html).contains("Текст до");
        assertThat(html).contains("Текст после");
    }

    @Test
    void preservesNewlinesInMermaidSource() {
        // Регрессия: Jsoup prettyPrint схлопывал переводы строк в mermaid-источнике
        // в пробелы → mermaid.js не мог распарсить многострочную диаграмму.
        String md = "Описание.\n\n```mermaid\nflowchart LR\n    A --> B\n    B --> C\n```\n\nИтог.";
        String html = service.toHtml(md);

        assertThat(html).contains("<div class=\"mermaid\">");
        // statements должны остаться на разных строках (а не «flowchart LR A --> B B --> C»)
        assertThat(html).contains("flowchart LR\n");
        assertThat(html).doesNotContain("flowchart LR     A");
        assertThat(html).contains("Описание");
        assertThat(html).contains("Итог");
    }

    @Test
    void escapesBrTagInsideMermaidSoLineBreaksSurviveTextContent() {
        // <br/> в подписях узлов mermaid обязан доехать до клиента ЛИТЕРАЛЬНЫМ текстом:
        // mermaid-renderer.js берёт источник через el.textContent, а реальный DOM-<br>
        // даёт пустую строку → «Atomicity<br/>Всё» схлопывалось в «AtomicityВсё».
        // Эскейпим в сущность &lt;br/&gt; — mermaid сам рисует перенос.
        String html = service.toHtml("```mermaid\nflowchart TD\n  T --> A[Atomicity<br/>Всё или ничего]\n```");

        assertThat(html).contains("<div class=\"mermaid\">");
        assertThat(html).contains("Atomicity&lt;br/&gt;Всё или ничего");
        // Реального <br>-элемента в источнике диаграммы быть не должно — иначе
        // textContent снова схлопнет подпись.
        assertThat(html).doesNotContain("<br>");
        assertThat(html).doesNotContain("<br/>");
    }

    @Test
    void escapesClassDiagramStereotypeSoItSurvivesTextContent() {
        // <<interface>> в class-диаграммах mermaid обязан доехать до клиента литералом:
        // без эскейпа jsoup разворачивает мнимый элемент <interface> и <<interface>>
        // схлопывается в «<>», ломая стереотип узла.
        String md = "```mermaid\nclassDiagram\n    class Builder {\n        <<interface>>\n        buildPartA()\n    }\n```";
        String html = service.toHtml(md);

        assertThat(html).contains("<div class=\"mermaid\">");
        assertThat(html).contains("&lt;&lt;interface&gt;&gt;");
        assertThat(html).doesNotContain("&lt;&gt;");
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
    void stripsUnsafeAnchorHrefs() {
        String html = service.toHtml(
                "click [x](javascript:alert(1)) and [y](/path) and [z](https://example.com) and [w](data:text/html,hi)");

        assertThat(html).contains("<a");
        assertThat(html).doesNotContain("javascript:");
        assertThat(html).doesNotContain("data:");
        assertThat(html).contains("href=\"/path\"");
        assertThat(html).contains("href=\"https://example.com\"");
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
    void stripsScriptTagsEmbeddedInMermaidFence() {
        String html = service.toHtml("```mermaid\ngraph TD;\nA-->B;\n<script>alert(1)</script>\n```");

        assertThat(html).contains("<div class=\"mermaid\">");
        assertThat(html).doesNotContain("<script");
        assertThat(html).doesNotContain("alert(1)");
    }

    @Test
    void stripsJavascriptProtocolInMarkdownLinkNearMermaid() {
        String md = "Before [x](javascript:alert(1)).\n\n```mermaid\nflowchart LR\n  A --> B\n```\n\nAfter.";
        String html = service.toHtml(md);

        assertThat(html).doesNotContain("javascript:");
        assertThat(html).contains("<div class=\"mermaid\">");
        assertThat(html).contains("Before");
        assertThat(html).contains("After");
    }

    @Test
    void stripsDataUriInMarkdownLinkNearMermaid() {
        String md = "See [payload](data:text/html,<script>alert(1)</script>).\n\n```mermaid\npie title T\n  \"A\" : 1\n```";
        String html = service.toHtml(md);

        assertThat(html).doesNotContain("data:");
        assertThat(html).contains("<div class=\"mermaid\">");
    }

    @Test
    void stripsProtocolRelativeMarkdownLinks() {
        String html = service.toHtml("Go [elsewhere](//evil.example/phish).");

        assertThat(html).doesNotContain("//evil.example");
        assertThat(html).doesNotContain("href=\"//");
        assertThat(html).contains("elsewhere");
    }

    @Test
    void inlineHtmlStripsLanguageClassButKeepsCode() {
        // INLINE_SAFELIST не разрешает атрибуты — class на <code> отбрасывается,
        // но сам инлайн-код сохраняется.
        String html = service.toInlineHtml("`x`");

        assertThat(html).isEqualTo("<code>x</code>");
    }

    @Test
    void allowsSafeSvgAndStripsScriptInsideSvg() {
        String html = service.toHtml(
                "Icon <svg viewBox=\"0 0 10 10\"><path d=\"M0 0h10v10H0z\"/><script>alert(1)</script></svg>");

        assertThat(html).contains("<svg");
        assertThat(html).contains("<path");
        assertThat(html).doesNotContain("<script");
        assertThat(html).doesNotContain("alert(1)");
    }

    @Test
    void stripsExternalUseHrefOnSvg() {
        String html = service.toHtml(
                "<svg><use href=\"https://evil.example/x.svg#i\"></use></svg>");

        assertThat(html).doesNotContain("https://evil.example");
    }

    @Test
    void stripsUrlEncodedJavascriptHref() {
        // %73 = 's'; isSafeHref декодирует href перед проверкой, поэтому
        // обфускация процент-кодированием не обходит запрет javascript:.
        String html = service.toHtml("жми [x](java%73cript:alert(1))");

        assertThat(html).doesNotContain("javascript");
        assertThat(html).doesNotContain("%73cript");
        assertThat(html).contains("<a"); // сам якорь остаётся, но без href
    }

    @Test
    void stripsHrefWithMalformedPercentEncoding() {
        // URLDecoder бросает IllegalArgumentException на битом %-encoding →
        // isSafeHref возвращает false (fail-closed), href удаляется.
        String html = service.toHtml("ссылка [x](%zz)");

        assertThat(html).doesNotContain("href=\"%zz\"");
    }

    @Test
    void stripsVbscriptHref() {
        String html = service.toHtml("[x](vbscript:msgbox(1))");

        assertThat(html).doesNotContain("vbscript");
    }

    @Test
    void preservesMailtoHref() {
        String html = service.toHtml("напиши [нам](mailto:team@example.com)");

        assertThat(html).contains("href=\"mailto:team@example.com\"");
    }

    @Test
    void preservesSameDocumentFragmentHref() {
        String html = service.toHtml("к [разделу](#section-2)");

        assertThat(html).contains("href=\"#section-2\"");
    }

    @Test
    void rendersWikiLinkWithAnchorKeptInLabelNotUrl() {
        // [[topic#Qn]] без явного label: якорь #Qn уходит в ВИДИМЫЙ label,
        // но НЕ в URL (мы не маршрутизируем якоря вопросов).
        String html = service.toHtml("см. [[java-generics-interview#Q5]]");

        assertThat(html).contains("href=\"/?topic=java-generics-interview\"");
        assertThat(html).contains(">java-generics-interview#Q5</a>");
    }

    @Test
    void plainTextPreviewReturnsShortTextUnchanged() {
        // Длина <= maxLength — без обрезки и без «…». Overload без maxLength
        // использует app.search.preview-max-length (=200 в setUp).
        assertThat(service.toPlainTextPreview("короткий текст", 200)).isEqualTo("короткий текст");
        assertThat(service.toPlainTextPreview("короткий текст")).isEqualTo("короткий текст");
    }

    @Test
    void plainTextPreviewTruncatesAtWordBoundaryWithEllipsis() {
        // maxLength=10: последний пробел на позиции 7 (>= maxLength/2) → режем по слову.
        String preview = service.toPlainTextPreview("one two three four five six", 10);

        assertThat(preview).isEqualTo("one two…");
    }

    @Test
    void plainTextPreviewHardCutsWhenNoEarlyWordBoundary() {
        // Первое слово длиннее maxLength: пробел (поз. 16) слишком поздно
        // (cut < maxLength/2) → жёсткая обрезка ровно по maxLength=10.
        String preview = service.toPlainTextPreview("abcdefghijklmnop qrst", 10);

        assertThat(preview).isEqualTo("abcdefghij…");
    }
}
