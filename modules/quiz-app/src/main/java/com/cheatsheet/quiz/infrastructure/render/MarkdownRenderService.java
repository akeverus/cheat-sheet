package com.cheatsheet.quiz.infrastructure.render;

import com.cheatsheet.quiz.config.app.AppProperties;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension;
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

    /** Класс скролл-обёртки таблиц (components-table.css, table-scroll.js). */
    private static final String TABLE_SCROLL_CLASS = "table-scroll";

    // GFM-расширения для тела ответа:
    //  • TablesExtension — GFM-таблицы (| col | col |); без него flexmark схлопывал
    //    строки в один <p> и показывал сырой pipe-текст.
    //  • StrikethroughExtension — ~~зачёркнуто~~ (напр. «~~SPA~~ (устарел)» в сравнениях);
    //    без него ~~ рендерился литеральными тильдами.
    //  • TaskListExtension — GFM-чек-листы (- [ ] / - [x]); без него пункты
    //    показывались литеральными «[ ]»/«[x]» вместо чекбоксов (напр. security-чек-листы
    //    TLS). Дефолтный рендер = disabled+readonly <input type=checkbox> (GFM-стандарт,
    //    доступно скринридеру, рисуется браузером). Safelist ниже пропускает такой input;
    //    sanitizeAnchorsAndSvg срезает любой НЕ-checkbox input (defense-in-depth).
    // GFM-флаги таблиц выравнивают поведение с GitHub (лишние колонки отбрасываются,
    // недостающие добиваются, шапка матчится по разделителю).
    private static final MutableDataSet MARKDOWN_OPTIONS = new MutableDataSet()
            .set(Parser.EXTENSIONS, List.of(
                    TablesExtension.create(),
                    StrikethroughExtension.create(),
                    TaskListExtension.create()))
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
                    "svg", "path", "g", "use", "title", "desc", "circle", "rect", "line", "polyline", "polygon",
                    "del", "s", "input")   // del/s: ~~strikethrough~~; input: GFM task-list checkbox
            .removeTags("script", "iframe", "object", "embed", "form", "foreignObject")
            .addAttributes("div", "class")   // mermaid diagrams: <div class="mermaid">
            .addAttributes("code", "class")  // highlight.js language hints: <code class="language-java">
            // GFM task-list: <li class="task-list-item"><input type=checkbox class=.. disabled readonly>.
            // sanitizeAnchorsAndSvg вырезает любой input с type!=checkbox (defense-in-depth).
            .addAttributes("input", "type", "checked", "disabled", "readonly", "class")
            .addAttributes("li", "class")
            .addAttributes("ul", "class")
            .addAttributes("ol", "class")
            .addAttributes("p", "class")
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
     * Стереотипы class-диаграмм mermaid: {@code <<interface>>}, {@code <<abstract>>},
     * {@code <<sealed>>}, {@code <<record>>}, {@code <<enumeration>>} и т.п. Как и {@code <br>},
     * их надо доставить в mermaid ЛИТЕРАЛОМ (см. preprocessMermaid), иначе jsoup распарсит
     * {@code <interface>} как неизвестный элемент, развернёт его и {@code <<interface>>}
     * схлопнется в {@code <>}. Ограничение длины — защита от захвата неродственных {@code <}.
     */
    private static final java.util.regex.Pattern MERMAID_STEREOTYPE =
            java.util.regex.Pattern.compile("<<[^<>\\n]{1,40}>>");

    /**
     * Инлайн-теги форматирования в mermaid-подписях: {@code <b>}/{@code <i>}/{@code <strong>}/
     * {@code <em>}/{@code <u>}/{@code <sub>}/{@code <sup>} (и закрывающие). mermaid (htmlLabels,
     * securityLevel=strict через DOMPurify) их рендерит жирным/курсивом — напр. STAR-мнемоника
     * {@code <b>S</b>ituation}. Без эскейпа (см. preprocessMermaid) jsoup оставляет их реальными
     * DOM-элементами, а клиентский {@code el.textContent} снимает разметку → в mermaid уходит
     * плоский текст без выделения. Эскейпим ТОЛЬКО эти теги — {@code <br>} обрабатывается отдельно
     * (нормализация), generic-типы вроде {@code List<items>} НЕ трогаем (не в списке).
     */
    private static final java.util.regex.Pattern MERMAID_INLINE_FMT =
            java.util.regex.Pattern.compile("</?(?:b|i|strong|em|u|sub|sup)\\s*>", java.util.regex.Pattern.CASE_INSENSITIVE);

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
        return postProcess(cleaned);
    }

    /**
     * Пост-обработка отрендеренного фрагмента: санитайзинг + доступность.
     * Один разбор Jsoup на все шаги — фрагмент ответа бывает большим, а
     * повторный parse/serialize на каждый шаг стоил бы столько же, сколько
     * сам рендер.
     */
    private String postProcess(String html) {
        if (html == null || html.isBlank()) {
            return html == null ? "" : html;
        }
        Document doc = Jsoup.parseBodyFragment(html);
        doc.outputSettings(NO_PRETTY_PRINT);
        sanitizeAnchorsAndSvg(doc);
        normalizeHeadingLevels(doc);
        labelTaskListCheckboxes(doc);
        wrapDataTables(doc);
        return doc.body().html();
    }

    /**
     * Оборачивает таблицы прозы в скролл-контейнер компонента и вешает на них
     * его классы.
     *
     * <p>Таблица — единственный блок markdown, ширина которого задаётся не нами,
     * а автором: пять колонок с кодом не сожмутся до ширины колонки чтения ни при
     * каком переносе. Раньше это лечилось прямо на самой таблице
     * ({@code display:block; overflow-x:auto} в CSS прозы), но {@code display:block}
     * создаёт внутри анонимный табличный бокс по содержимому — на широком экране
     * короткая таблица переставала растягиваться и оставляла справа мёртвую
     * полосу (замерено 377px в контейнере 600px).</p>
     *
     * <p>Поэтому скроллится обёртка, а таблица остаётся таблицей. Ставим её здесь,
     * а не в CSS, потому что обёртка — элемент разметки: сгенерировать её может
     * только тот, кто строит HTML. Классы {@code data-table is-ruled} подключают
     * общий скелет компонента и прозаический скин (рамки по ячейкам), чтобы
     * markdown-таблица и разметочная таблица {@code /stats} были одним компонентом,
     * а не двумя похожими.</p>
     *
     * <p>Идемпотентно: если таблица уже внутри {@code .table-scroll} (повторный
     * прогон, вложенная обёртка из исходника), второй раз не оборачиваем.</p>
     */
    private void wrapDataTables(Document doc) {
        for (Element table : doc.select("table")) {
            table.addClass("data-table").addClass("is-ruled");
            Element parent = table.parent();
            if (parent != null && parent.hasClass(TABLE_SCROLL_CLASS)) {
                continue;
            }
            table.wrap("<div class=\"" + TABLE_SCROLL_CLASS + "\"></div>");
        }
    }

    /**
     * Сдвигает заголовки фрагмента так, чтобы самый мелкий уровень стал {@code h2}.
     *
     * <p>Фрагмент встраивается в страницу, где {@code h1} — это сам вопрос. Автор
     * markdown про это не знает и начинает разбор с {@code ###}, поэтому в дереве
     * получался разрыв h1 → h3: скринридер, идущий по заголовкам, видит пропущенный
     * уровень и не может понять вложенность (WCAG 1.3.1). Правим не в 300+ исходных
     * файлах, а один раз на рендере.</p>
     *
     * <p>Сдвигается вся лестница целиком, поэтому относительная глубина разбора
     * сохраняется; уровни глубже {@code h6} схлопываются в {@code h6} — тега h7 нет.</p>
     */
    private void normalizeHeadingLevels(Document doc) {
        List<Element> headings = doc.select("h1, h2, h3, h4, h5, h6");
        if (headings.isEmpty()) {
            return;
        }
        int shallowest = headings.stream()
                .mapToInt(h -> h.tagName().charAt(1) - '0')
                .min()
                .orElse(2);
        int shift = 2 - shallowest;
        if (shift == 0) {
            return;
        }
        for (Element heading : headings) {
            int level = heading.tagName().charAt(1) - '0';
            heading.tagName("h" + Math.min(6, Math.max(2, level + shift)));
        }
    }

    /**
     * Даёт GFM-чекбоксам чек-листа доступное имя.
     *
     * <p>Flexmark рендерит {@code - [x] пункт} как безымянный
     * {@code <input type=checkbox disabled>}. Скринридер объявлял его как
     * «флажок» без состояния и без названия — то есть отмеченность пункта,
     * единственное, что этот элемент несёт, до пользователя не доходила.</p>
     *
     * <p>Имя — только состояние («выполнено» / «не выполнено»), не текст пункта:
     * текст лежит рядом в том же {@code <li>} и будет прочитан следом, дублировать
     * его в метке значит читать каждый пункт дважды.</p>
     */
    private void labelTaskListCheckboxes(Document doc) {
        for (Element input : doc.select("input[type=checkbox]")) {
            input.attr("aria-label", input.hasAttr("checked") ? "выполнено" : "не выполнено");
        }
    }

    /**
     * Санитайзинг ссылок и SVG: href allow-list, {@code <use href>} только на
     * фрагменты того же документа, из {@code <input>} допустим лишь disabled-checkbox.
     */
    private void sanitizeAnchorsAndSvg(Document doc) {
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
        // Defense-in-depth: safelist пропускает <input> ради GFM-чекбоксов task-list,
        // но допустим ТОЛЬКО disabled-checkbox. Любой иной input (text/hidden/…) — вон.
        for (Element input : doc.select("input")) {
            if (!"checkbox".equalsIgnoreCase(input.attr("type"))) {
                input.remove();
            } else {
                input.attr("disabled", "disabled");
                input.attr("readonly", "readonly");
            }
        }
        // Strip event-handler attributes that might survive if added outside safelist attrs.
        for (Element el : doc.getAllElements()) {
            el.attributes().asList().stream()
                    .filter(a -> a.getKey().startsWith("on"))
                    .toList()
                    .forEach(a -> el.removeAttr(a.getKey()));
        }
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
            // jsoup-санитайзер в toHtml, security-инвариант сохранён. Аналогично —
            // стереотипы class-диаграмм <<interface>>/<<abstract>>/…: без эскейпа jsoup
            // разворачивает мнимый <interface> и <<interface>> схлопывается в <>. Эскейпим
            // только угловые скобки самого стереотипа (внутренний текст цел).
            String source = MERMAID_BR.matcher(mr.group(1).trim()).replaceAll("&lt;br/&gt;");
            source = MERMAID_STEREOTYPE.matcher(source).replaceAll(
                    sm -> java.util.regex.Matcher.quoteReplacement(
                            sm.group().replace("<", "&lt;").replace(">", "&gt;")));
            source = MERMAID_INLINE_FMT.matcher(source).replaceAll(
                    fm -> java.util.regex.Matcher.quoteReplacement(
                            fm.group().replace("<", "&lt;").replace(">", "&gt;")));
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
