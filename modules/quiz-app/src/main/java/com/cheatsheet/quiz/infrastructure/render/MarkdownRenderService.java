package com.cheatsheet.quiz.infrastructure.render;

import com.cheatsheet.quiz.config.app.AppProperties;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
            .addTags("pre", "code", "table", "thead", "tbody", "tr", "th", "td",
                    "svg", "path", "g", "use", "title", "desc", "circle", "rect", "line", "polyline", "polygon")
            .removeTags("script", "iframe", "object", "embed", "form", "foreignObject")
            .addAttributes("div", "class")   // mermaid diagrams: <div class="mermaid">
            .addAttributes("code", "class")  // highlight.js language hints: <code class="language-java">
            .addAttributes("a", "class")
            .addAttributes("svg", "viewBox", "width", "height", "xmlns", "aria-hidden", "focusable", "role", "class", "fill", "stroke")
            .addAttributes("path", "d", "fill", "stroke", "stroke-width", "transform", "class")
            .addAttributes("g", "transform", "fill", "stroke", "class")
            .addAttributes("use", "href")
            .addAttributes("circle", "cx", "cy", "r", "fill", "stroke", "class")
            .addAttributes("rect", "x", "y", "width", "height", "rx", "ry", "fill", "stroke", "class")
            .addAttributes("line", "x1", "y1", "x2", "y2", "stroke", "class")
            .addAttributes("polyline", "points", "fill", "stroke", "class")
            .addAttributes("polygon", "points", "fill", "stroke", "class")
            // wiki-link даёт relative href вида /?topic=foo — без preserveRelativeLinks
            // jsoup пытается «отабсолютить» его и убирает href; без removeProtocols
            // protocol-whitelist отбрасывает href, у которого нет схемы.
            .removeProtocols("a", "href", "ftp", "ftps", "http", "https", "mailto")
            .preserveRelativeLinks(true);

    /**
     * Узкий Safelist для ИНЛАЙН-текста (варианты/вопросы): только инлайн-форматирование.
     * Намеренно НЕ разрешает ни {@code <a>}, ни блоки/таблицы/картинки — у текста варианта
     * и вопроса их быть не должно. Отсутствие {@code <a>} снимает даже теоретический
     * {@code javascript:}-href вектор, который в {@link #HTML_SAFELIST} открыт ради
     * относительных wiki-ссылок в {@code toHtml}. Атрибуты не разрешены вовсе → никаких
     * on*-обработчиков. Базис {@code new Safelist()} = «только текст», добавляем строго
     * инлайн-теги.
     */
    private static final Safelist INLINE_SAFELIST = new Safelist()
            .addTags("code", "strong", "em", "b", "i", "sub", "sup", "del", "ins", "mark", "br");

    /** Паттерн для mermaid code-блоков: ```mermaid ... ```. */
    private static final java.util.regex.Pattern MERMAID_BLOCK =
            java.util.regex.Pattern.compile("```mermaid\\s*\n([\\s\\S]*?)```", java.util.regex.Pattern.MULTILINE);

    /** {@code <br>} / {@code <br/>} / {@code <br />} внутри mermaid-подписей (любой регистр). */
    private static final java.util.regex.Pattern MERMAID_BR =
            java.util.regex.Pattern.compile("<\\s*br\\s*/?\\s*>", java.util.regex.Pattern.CASE_INSENSITIVE);

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
        String cleaned = Jsoup.clean(html, "", HTML_SAFELIST, NO_PRETTY_PRINT);
        return sanitizeAnchorsAndSvg(cleaned);
    }

    /**
     * Post-clean: href allow-list + SVG use/href only same-document fragments.
     */
    private String sanitizeAnchorsAndSvg(String html) {
        if (html == null || html.isBlank()) {
            return html == null ? "" : html;
        }
        Document doc = Jsoup.parseBodyFragment(html);
        doc.outputSettings(NO_PRETTY_PRINT);
        for (Element anchor : doc.select("a[href]")) {
            if (!isSafeHref(anchor.attr("href"))) {
                anchor.removeAttr("href");
            }
        }
        for (Element use : doc.select("use[href], use[xlink:href]")) {
            String href = use.hasAttr("href") ? use.attr("href") : use.attr("xlink:href");
            if (href == null || !href.startsWith("#")) {
                use.removeAttr("href");
                use.removeAttr("xlink:href");
            }
        }
        // Strip event-handler attributes that might survive if added outside safelist attrs.
        for (Element el : doc.getAllElements()) {
            el.attributes().asList().stream()
                    .filter(a -> a.getKey().startsWith("on"))
                    .toList()
                    .forEach(a -> el.removeAttr(a.getKey()));
        }
        return doc.body().html();
    }

    private boolean isSafeHref(String href) {
        if (href == null || href.isBlank()) {
            return false;
        }
        String trimmed = href.trim();
        String decoded;
        try {
            decoded = URLDecoder.decode(trimmed, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException ex) {
            return false;
        }
        String normalized = decoded.trim().replaceAll("\\s+", "").toLowerCase(Locale.ROOT);
        if (normalized.startsWith("javascript:")
                || normalized.startsWith("data:")
                || normalized.startsWith("vbscript:")
                || normalized.startsWith("//")) {
            return false;
        }
        if (trimmed.startsWith("#")) {
            return true;
        }
        // Same-origin paths only — reject protocol-relative "//evil".
        if (trimmed.startsWith("/") && !trimmed.startsWith("//")) {
            return true;
        }
        String lower = trimmed.toLowerCase(Locale.ROOT);
        return lower.startsWith("http:") || lower.startsWith("https:") || lower.startsWith("mailto:");
    }

    /**
     * Конвертирует короткий markdown (текст варианта ответа, подпись) в ИНЛАЙН-HTML
     * без блочной обёртки {@code <p>}. Нужно, чтобы {@code `code`}, {@code **bold**},
     * {@code *em*} в тексте варианта рендерились как настоящие
     * {@code <code>/<strong>/<em>}, а не как литералы с бэктиками/звёздочками — при
     * этом не ломая инлайн-раскладку label (буква-бейдж + текст в одну строку).
     *
     * <p>Flexmark всегда оборачивает одиночный абзац в {@code <p>…</p>}; для вставки
     * внутрь {@code <span>}/{@code <label>} этот блок лишний (даёт перенос и
     * вертикальный отступ), поэтому единственный верхнеуровневый {@code <p>}
     * разворачиваем. Несколько блоков (редкость для одной строки) оставляем как есть —
     * лучше лишний отступ, чем потеря структуры.</p>
     *
     * @param markdown исходный markdown (обычно одна строка)
     * @return инлайн-HTML, безопасный для th:utext / innerHTML
     */
    public String toInlineHtml(String markdown) {
        if (markdown == null || markdown.isBlank()) {
            return "";
        }
        Node document = PARSER.parse(markdown);
        String html = HTML_RENDERER.render(document);
        Document doc = Jsoup.parseBodyFragment(html);
        doc.outputSettings(NO_PRETTY_PRINT);
        if (doc.body().childrenSize() == 1 && "p".equals(doc.body().child(0).tagName())) {
            doc.body().child(0).unwrap();
        }
        // trim: flexmark добавляет хвостовой \n после блока, а после unwrap он остаётся
        // в body. Для инлайн-вставки в <span> краевой пробел не нужен (у текста варианта
        // нет значимых ведущих/хвостовых пробелов).
        // INLINE_SAFELIST (без <a>/блоков/атрибутов) — строже, чем HTML_SAFELIST: для
        // короткого инлайн-текста ссылки/таблицы не нужны, а узкий allow-list закрывает
        // и теоретический javascript:-href вектор.
        return Jsoup.clean(doc.body().html(), "", INLINE_SAFELIST, NO_PRETTY_PRINT).trim();
    }

    /** Заменяет ```mermaid ... ``` блоки на &lt;div class="mermaid"&gt; для рендеринга mermaid.js. */
    private String preprocessMermaid(String markdown) {
        return MERMAID_BLOCK.matcher(markdown).replaceAll(mr -> {
            // Источник mermaid кладётся в <div class="mermaid"> как ТЕКСТ, но <br/> в
            // подписях узлов иначе распарсится flexmark/jsoup как реальный DOM-элемент
            // <br>. Клиент (mermaid-renderer.js) читает источник через el.textContent,
            // а <br>-элемент даёт пустую строку → «Atomicity<br/>Всё или ничего»
            // рендерилось как «AtomicityВсё или ничего» (перенос строки терялся во всех
            // диаграммах тела ответа: 2513 узлов в 181 файле). Эскейпим ТОЛЬКО <br> в
            // сущность — он доезжает до mermaid литеральным текстом, и mermaid сам рисует
            // перенос. Прочие теги (в т.ч. <script>) идут прежним путём: их вырезает
            // jsoup-санитайзер в toHtml, security-инвариант сохранён.
            String source = MERMAID_BR.matcher(mr.group(1).trim()).replaceAll("&lt;br/&gt;");
            return "\n<div class=\"mermaid\">\n" + java.util.regex.Matcher.quoteReplacement(source) + "\n</div>\n\n";
        });
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
