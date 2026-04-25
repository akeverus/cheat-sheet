package com.cheatsheet.quiz.infrastructure.render;

import com.cheatsheet.quiz.config.app.AppProperties;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Service;

/**
 * Сервис конвертации Markdown в HTML и plain text.
 *
 * <p>Используется для отображения ответов без markdown-синтаксиса
 * и извлечения plain text для превью.</p>
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MarkdownRenderService {

    /** Regex: один и более пробельных символов. */
    private static final String REGEX_WHITESPACE = "\\s+";

    private static final MutableDataSet MARKDOWN_OPTIONS = new MutableDataSet();
    private static final Parser PARSER = Parser.builder(MARKDOWN_OPTIONS).build();
    private static final HtmlRenderer HTML_RENDERER = HtmlRenderer.builder(MARKDOWN_OPTIONS).build();

    private final AppProperties appProperties;

    /** Safelist для HTML после markdown: только безопасные теги, без script/iframe/form. Подходит для th:utext. */
    private static final Safelist HTML_SAFELIST = Safelist.relaxed()
            .addTags("pre", "code", "table", "thead", "tbody", "tr", "th", "td")
            .removeTags("script", "iframe", "object", "embed", "form")
            .addAttributes("div", "class")   // mermaid diagrams: <div class="mermaid">
            .addAttributes("code", "class"); // highlight.js language hints: <code class="language-java">

    /** Паттерн для mermaid code-блоков: ```mermaid ... ```. */
    private static final java.util.regex.Pattern MERMAID_BLOCK =
            java.util.regex.Pattern.compile("```mermaid\\s*\n([\\s\\S]*?)```", java.util.regex.Pattern.MULTILINE);

    /**
     * Конвертирует markdown в HTML, санитизированный для безопасного отображения (th:utext / innerHTML).
     * Удаляются script, iframe, event-атрибуты и опасные теги.
     * Mermaid-блоки (```mermaid) конвертируются в {@code <div class="mermaid">} для рендеринга mermaid.js.
     *
     * @param markdown исходный markdown
     * @return HTML, безопасный для вставки в страницу
     */
    public String toHtml(String markdown) {
        if (markdown == null || markdown.isBlank()) {
            return "";
        }
        String processed = preprocessMermaid(markdown);
        Node document = PARSER.parse(processed);
        String html = HTML_RENDERER.render(document);
        return Jsoup.clean(html, HTML_SAFELIST);
    }

    /** Заменяет ```mermaid ... ``` блоки на <div class="mermaid"> для рендеринга mermaid.js. */
    private String preprocessMermaid(String markdown) {
        return MERMAID_BLOCK.matcher(markdown).replaceAll(
                mr -> "\n<div class=\"mermaid\">\n" + mr.group(1).trim() + "\n</div>\n\n");
    }

    /**
     * Извлекает plain text из markdown (без разметки).
     *
     * @param markdown исходный markdown
     * @return чистый текст
     */
    public String toPlainText(String markdown) {
        if (markdown == null || markdown.isBlank()) {
            return "";
        }
        Node document = PARSER.parse(markdown);
        String html = HTML_RENDERER.render(document);
        return Jsoup.parse(html).text().replaceAll(REGEX_WHITESPACE, StringUtils.SPACE).trim();
    }

    /**
     * Plain text с обрезкой для превью (поиск, сниппеты).
     *
     * @param markdown   исходный markdown
     * @param maxLength  максимальная длина результата (символов)
     * @return обрезанный plain text с «…» в конце при обрезке
     */
    public String toPlainTextPreview(String markdown, int maxLength) {
        String text = toPlainText(markdown);
        if (text.length() <= maxLength) {
            return text;
        }
        int cut = text.lastIndexOf(' ', maxLength);
        if (cut < maxLength / 2) {
            cut = maxLength;
        }
        return text.substring(0, cut).trim() + "…";
    }

    /**
     * Plain text превью до {@code app.search.preview-max-length} символов.
     *
     * @param markdown исходный markdown
     * @return обрезанный plain text
     */
    public String toPlainTextPreview(String markdown) {
        return toPlainTextPreview(markdown, appProperties.getSearch().getPreviewMaxLength());
    }
}
