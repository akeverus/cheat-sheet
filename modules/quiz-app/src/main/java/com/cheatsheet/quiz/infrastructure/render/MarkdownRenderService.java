package com.cheatsheet.quiz.infrastructure.render;

import com.cheatsheet.quiz.config.app.AppProperties;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;

import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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

    // GFM-таблицы (| col | col |). Без TablesExtension flexmark схлопывал
    // строки таблицы в один <p> и пользователь видел сырой markdown-pipe-текст
    // в пояснениях/чек-листах. GFM-флаги выравнивают поведение с GitHub:
    // лишние колонки отбрасываются, недостающие добиваются, шапка матчится
    // по разделителю — это устойчивее к слегка кривым таблицам в контенте.
    private static final MutableDataSet MARKDOWN_OPTIONS = new MutableDataSet()
            .set(Parser.EXTENSIONS, List.of(TablesExtension.create()))
            .set(TablesExtension.COLUMN_SPANS, false)
            .set(TablesExtension.APPEND_MISSING_COLUMNS, true)
            .set(TablesExtension.DISCARD_EXTRA_COLUMNS, true)
            .set(TablesExtension.HEADER_SEPARATOR_COLUMN_MATCH, true);
    private static final Parser PARSER = Parser.builder(MARKDOWN_OPTIONS).build();
    private static final HtmlRenderer HTML_RENDERER = HtmlRenderer.builder(MARKDOWN_OPTIONS).build();

    /**
     * Obsidian-style wiki-link: {@code [[topic#anchor|label]]}.
     * group 1 — slug темы (без префикса), group 2 — отброшенный якорь #Qn (мы его не маршрутизируем),
     * group 3 — необязательный label.
     */
    private static final java.util.regex.Pattern WIKI_LINK =
            java.util.regex.Pattern.compile("\\[\\[([^\\]|#\\s]+)(#[^\\]|]+)?(?:\\|([^\\]]+))?\\]\\]");

    private final AppProperties appProperties;

    // prettyPrint=false: Jsoup НЕ переформатирует пробелы при выводе. Критично
    // для <div class="mermaid"> — иначе Jsoup схлопывал переводы строк в
    // mermaid-источнике в пробелы, и mermaid.js не мог распарсить диаграмму
    // (каждый statement должен быть на своей строке) → диаграмма не рисовалась.
    private static final Document.OutputSettings NO_PRETTY_PRINT =
            new Document.OutputSettings().prettyPrint(false);

    /** Safelist для HTML после markdown: только безопасные теги, без script/iframe/form. Подходит для th:utext. */
    private static final Safelist HTML_SAFELIST = Safelist.relaxed()
            .addTags("pre", "code", "table", "thead", "tbody", "tr", "th", "td")
            .removeTags("script", "iframe", "object", "embed", "form")
            .addAttributes("div", "class")   // mermaid diagrams: <div class="mermaid">
            .addAttributes("code", "class")  // highlight.js language hints: <code class="language-java">
            .addAttributes("a", "class")
            // wiki-link даёт relative href вида /?topic=foo — без preserveRelativeLinks
            // jsoup пытается «отабсолютить» его и убирает href; без removeProtocols
            // protocol-whitelist отбрасывает href, у которого нет схемы.
            .removeProtocols("a", "href", "ftp", "ftps", "http", "https", "mailto")
            .preserveRelativeLinks(true);

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
        processed = preprocessWikiLinks(processed);
        Node document = PARSER.parse(processed);
        String html = HTML_RENDERER.render(document);
        return Jsoup.clean(html, "", HTML_SAFELIST, NO_PRETTY_PRINT);
    }

    /** Заменяет ```mermaid ... ``` блоки на <div class="mermaid"> для рендеринга mermaid.js. */
    private String preprocessMermaid(String markdown) {
        return MERMAID_BLOCK.matcher(markdown).replaceAll(
                mr -> "\n<div class=\"mermaid\">\n" + mr.group(1).trim() + "\n</div>\n\n");
    }

    /**
     * Конвертирует Obsidian-style wiki-links в обычный markdown-link на роут темы.
     * {@code [[topic]]} → {@code [topic](/?topic=topic)},
     * {@code [[topic|label]]} → {@code [label](/?topic=topic)},
     * {@code [[topic#Qn]]} → {@code [topic#Qn](/?topic=topic)} (якорь оставляем в label, не в URL).
     */
    private String preprocessWikiLinks(String markdown) {
        return WIKI_LINK.matcher(markdown).replaceAll(mr -> {
            String slug = mr.group(1).trim();
            String anchor = mr.group(2);
            String label = mr.group(3);
            if (label == null || label.isBlank()) {
                label = anchor == null ? slug : slug + anchor;
            }
            // \\ для java-replaceAll: $ и \\ в label экранируем, чтобы не сломать regex backrefs.
            String safeLabel = java.util.regex.Matcher.quoteReplacement(label);
            return "[" + safeLabel + "](/?topic=" + slug + ")";
        });
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
