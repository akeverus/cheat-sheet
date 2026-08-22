package com.cheatsheet.quiz.ui;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * У продукта ровно один ответ на вопрос «таблица шире отведённой полосы».
 *
 * <h2>Дефект, а не гипотеза (iter-76, замеры в WebKit@2)</h2>
 * На {@code /stats} страницу толкали вбок восемь элементов — и не на экзотике, а на
 * обычном MacBook при масштабе браузера 125% ({@code 1210×786}, root=25px). Причина:
 * табличным дублям графиков ({@code .chart-adjacent-table}) было объявлено только
 * {@code width: 100%}. При {@code table-layout: auto} это ЖЕЛАЕМАЯ ширина, а не предел:
 * таблица всё равно раздувается до min-content (замер: 441px внутри карточки 315px), и,
 * поскольку {@code section.card} не режет overflow, распор доходит до документа.
 *
 * <h2>Почему гейт на компонент, а не правка одного селектора</h2>
 * Дефект был не в одном правиле, а в том, что ответов на один вопрос было пять и жили
 * они в разных файлах:
 * <ol>
 *   <li>{@code .summary-table} — {@code table-layout: fixed} + ширины колонок;</li>
 *   <li>{@code .topic-table} — обёртка {@code overflow-x: auto} + карточки на мобиле;</li>
 *   <li>{@code .data-table} — ничего;</li>
 *   <li>{@code .chart-adjacent-table} — ничего (тот самый распор);</li>
 *   <li>{@code .markdown-content table} — {@code display: block; overflow-x: auto}.</li>
 * </ol>
 * Четыре из пяти вдобавок независимо переобъявляли один и тот же скелет
 * ({@code width}, {@code border-collapse}, паддинги ячеек, оформление шапки). Пятый приём
 * ({@code display: block}) проверен контрольным замером и отвергнут: он создаёт внутри
 * таблицы анонимный табличный бокс, а тот shrink-to-fit — на коротком содержимом справа
 * оставалась мёртвая полоса 377px из 600px (§6), тогда как обёртка недобирала 1px.
 *
 * <p>Тесты ниже держат следствие этого выбора: скелет объявлен один раз в компоненте,
 * страницы его не копируют, а каждая таблица разметки лежит в скролл-обёртке. Проверяется
 * структура решения, а не пиксели — пиксели проверены браузером, но браузер не запрещает
 * шестому месту завести шестой ответ.
 */
class DataTableComponentContractTest {

    private static final Path RESOURCES = Path.of("src/main/resources");
    private static final Path COMPONENT_CSS = RESOURCES.resolve("static/css/components-table.css");
    private static final Path MANIFEST = RESOURCES.resolve("css-bundle-manifest.txt");
    private static final Path TEMPLATES = RESOURCES.resolve("templates");
    private static final Path CSS_DIR = RESOURCES.resolve("static/css");

    /** Обёртка, которая ограничивает ширину, не превращая таблицу в блок. */
    private static final String SCROLL_CLASS = "table-scroll";

    private static String read(Path path) {
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("не читается " + path, e);
        }
    }

    /** Объявления без комментариев: рассказ о снятом приёме не должен считаться приёмом. */
    private static String declarationsOnly(String css) {
        return css.replaceAll("(?s)/\\*.*?\\*/", "");
    }

    private static List<Path> pageCssFiles() {
        try (Stream<Path> files = Files.list(CSS_DIR)) {
            return files.filter(p -> p.getFileName().toString().startsWith("pages-"))
                    .filter(p -> p.getFileName().toString().endsWith(".css"))
                    .sorted()
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException("не читается " + CSS_DIR, e);
        }
    }

    private static List<Path> templateFiles() {
        try (Stream<Path> files = Files.walk(TEMPLATES)) {
            return files.filter(p -> p.getFileName().toString().endsWith(".html")).sorted().toList();
        } catch (IOException e) {
            throw new UncheckedIOException("не читается " + TEMPLATES, e);
        }
    }

    @Test
    void tableComponentIsDeclaredOnceAndShippedEverywhere() {
        assertThat(Files.exists(COMPONENT_CSS))
                .as("компонент таблицы удалён — скелет вернётся копиями по страницам")
                .isTrue();

        String css = declarationsOnly(read(COMPONENT_CSS));
        assertThat(css)
                .as("скелет: таблица занимает всю полосу секции")
                .contains(".data-table")
                .contains("width: 100%")
                .contains("border-collapse: collapse");
        assertThat(css)
                .as("обёртка — единственный носитель горизонтального скролла")
                .contains(".table-scroll")
                .contains("overflow-x: auto");
        assertThat(css)
                .as("обёртка обязана уметь сжиматься ниже min-content содержимого, иначе внутри "
                        + "grid/flex она сама раздуется вместе с таблицей и скролл не включится")
                .contains("min-width: 0");
        assertThat(css)
                .as("скролл-контейнер получает фокус с клавиатуры (WCAG 2.1.1) — значит обязан "
                        + "нести видимое кольцо")
                .contains(":focus-visible");

        String manifest = read(MANIFEST);
        assertThat(manifest)
                .as("компонент подключён гейтом `*`: его таблицы есть и в разметке (/stats, "
                        + "/session-summary), и в отрендеренном markdown (/focus, /result)")
                .contains("* /css/components-table.css");
    }

    @Test
    void pagesDoNotRedeclareTheTableSkeleton() {
        List<String> offenders = new ArrayList<>();
        for (Path css : pageCssFiles()) {
            String body = declarationsOnly(read(css));
            for (String line : body.split("\n")) {
                if (!line.contains("table")) {
                    continue;
                }
                if (line.contains("border-collapse") || line.contains("width: 100%")) {
                    offenders.add(css.getFileName() + ": " + line.trim());
                }
            }
        }
        assertThat(offenders)
                .as("страница снова объявляет скелет таблицы вместо того, чтобы взять его из "
                        + ".data-table — это возврат к четырём независимым копиям, которые "
                        + "расходились при любой правке")
                .isEmpty();
    }

    /**
     * Липкая шапка и скролл-обёртка несовместимы — выбрано второе.
     *
     * <p>{@code position: sticky} привязывается к ближайшему скролл-порту. Обёртка объявляет
     * {@code overflow-x: auto}, а спецификация заставляет при этом {@code overflow-y}
     * вычислиться в {@code auto} — обёртка становится скролл-портом в обеих осях, и шапка
     * липнет к ней, а не к странице. Вертикально обёртка не прокручивается никогда
     * ({@code scrollHeight == clientHeight}), поэтому шапка неподвижна относительно таблицы.
     * Замер (WebKit@2, 1440×900): {@code theadTop == tableTop == -78}; контрольный опыт со
     * снятым overflow в том же прогоне: {@code theadTop == 0} при {@code tableTop == -78}.
     *
     * <p>Гейт не даёт вернуть объявление: визуально оно ничего не делает, но выглядит как
     * работающая функция и тянет за собой {@code background}/{@code z-index}-подпорки.
     */
    @Test
    void tableHeadersDoNotClaimStickinessTheWrapperCannotDeliver() {
        List<String> offenders = new ArrayList<>();
        List<Path> files = new ArrayList<>(pageCssFiles());
        files.add(COMPONENT_CSS);
        for (Path css : files) {
            for (String block : declarationsOnly(read(css)).split("}")) {
                int brace = block.lastIndexOf('{');
                if (brace < 0) {
                    continue;
                }
                String selector = block.substring(0, brace);
                String body = block.substring(brace);
                if (selector.contains("table") && selector.contains("th")
                        && body.contains("position: sticky")) {
                    offenders.add(css.getFileName() + ": " + selector.trim());
                }
            }
        }
        assertThat(offenders)
                .as("липкая шапка внутри .table-scroll не работает: обёртка сама становится "
                        + "скролл-портом и вертикально не прокручивается — объявление осталось "
                        + "бы неисполнимым обещанием")
                .isEmpty();
    }

    @Test
    void everyMarkupTableSitsInsideTheScrollWrapper() {
        List<String> naked = new ArrayList<>();
        for (Path template : templateFiles()) {
            Document doc = Jsoup.parse(read(template));
            for (Element table : doc.select("table")) {
                Element parent = table.parent();
                if (parent == null || !parent.hasClass(SCROLL_CLASS)) {
                    naked.add(template.getFileName() + ": <table class=\"" + table.className() + "\">");
                }
            }
        }
        assertThat(naked)
                .as("таблица без обёртки .table-scroll: `width: 100%` при table-layout:auto — "
                        + "ЖЕЛАЕМАЯ ширина, а не предел. Таблица раздуется до min-content и "
                        + "толкнёт страницу вбок (замер: 441px в карточке 315px на 1210×786 "
                        + "при root=25px, 8 распоров на /stats)")
                .isEmpty();
    }

    @Test
    void markupTablesCarryTheComponentClass() {
        List<String> offenders = new ArrayList<>();
        for (Path template : templateFiles()) {
            Document doc = Jsoup.parse(read(template));
            for (Element table : doc.select("table")) {
                if (!table.hasClass("data-table")) {
                    offenders.add(template.getFileName() + ": <table class=\"" + table.className() + "\">");
                }
            }
        }
        assertThat(offenders)
                .as("таблица без класса .data-table останется без скелета: ни линеек, ни шапки, "
                        + "ни паддингов — либо страница заведёт себе шестую копию")
                .isEmpty();
    }

    @Test
    void proseTablesAreWrappedByTheRenderer() {
        String service = read(Path.of(
                "src/main/java/com/cheatsheet/quiz/infrastructure/render/MarkdownRenderService.java"));
        assertThat(service)
                .as("markdown-таблицу оборачивает рендерер: обёртка — элемент разметки, CSS её "
                        + "создать не может, а автор шпаргалки про неё не знает")
                .contains("wrapDataTables")
                .contains(SCROLL_CLASS);
    }

    /**
     * Рецепт «переполнен → фокусируем и подсвечен» обслуживает не только таблицы.
     * Код — вторая полоса контента с горизонтальной прокруткой: база
     * `html[data-design] pre` (reset-layout.css) несёт overflow-x:auto на КАЖДОМ
     * pre приложения, и без этого контракта любой длинный листинг вне окна
     * `.code-viewport` (markdown-разбор, result) — прокрутка, до которой нельзя
     * дойти с клавиатуры (A11Y-01, WCAG 2.1.1).
     */
    @Test
    void codeBlocksJoinTheSameScrollAccessRecipe() {
        String js = read(RESOURCES.resolve("static/js/content/table-scroll.js"));
        assertThat(js)
                .as("модуль оценивает только .table-scroll: переполненный pre остаётся "
                        + "недостижимым с клавиатуры")
                .contains("'.table-scroll, pre'");
        assertThat(js)
                .as("остановка табуляции на коде обязана иметь имя — иначе скринридер "
                        + "объявит безымянную «группу»")
                .contains("'Код'");

        String css = read(RESOURCES.resolve("static/css/components-table.css"));
        assertThat(css)
                .as("класс .is-scrollable на pre без визуального рецепта: фокус есть, а "
                        + "стойкого скроллбара-подсказки нет")
                .containsPattern("pre\\.is-scrollable");
        assertThat(css)
                .as("кольцо фокуса на pre не объявлено — фокус получен, но невидим")
                .containsPattern("pre[^,{]*:focus-visible");
    }

    @Test
    void scrollHintIsAttachedOnlyToOverflowingContainers() {
        String js = read(RESOURCES.resolve("static/js/content/table-scroll.js"));
        assertThat(js)
                .as("подсказка навешивается по факту переполнения, а не всем подряд: на широком "
                        + "экране лишний tab-стоп и лишний landmark только удлиняют путь")
                .contains("scrollWidth - box.clientWidth");
        assertThat(js)
                .as("переполненный контейнер должен быть достижим с клавиатуры (WCAG 2.1.1)")
                .contains("setAttribute('tabindex', '0')");
        assertThat(js)
                .as("остановка табуляции обязана иметь имя, иначе скринридер объявит безымянную "
                        + "«группу»")
                .contains("aria-label");
        assertThat(js)
                .as("состояние переоценивается в обе стороны — иначе tab-стоп, появившийся на "
                        + "узком окне, останется после расширения")
                .contains("removeAttribute('tabindex')");
    }
}
