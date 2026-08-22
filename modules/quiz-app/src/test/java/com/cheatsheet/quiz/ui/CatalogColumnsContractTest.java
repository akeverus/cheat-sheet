package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Каталог тем сравнивает разделы между собой — значит, его числа стоят колонками.
 *
 * <h2>Найденный дефект, а не гипотеза</h2>
 * Шапка раздела печатала числа бегущей строкой «20 тем · 626 вопросов · 10 освоено ·
 * 107 к повторению». Замер (Chromium и WebKit, 1512×982, десять разделов):
 *
 * <pre>
 *   правые кромки чисел раздела     разброс по десяти разделам
 *   «тем»        одинаков            0px
 *   «вопросов»   плавает            12px
 *   «освоено»    плавает           179px
 *   «к повторению» плавает         118px
 * </pre>
 *
 * То есть сравнить «сколько ждёт повторения» между разделами можно было только
 * чтением каждой строки целиком. В середине строки при этом стояла пустота: от
 * конца описания раздела (590) до первого числа (1065) — 475px.
 *
 * <h2>Что закреплено</h2>
 * <ol>
 *   <li>имена колонок печатаются ОДИН раз над списком, а не в каждой из десяти строк;</li>
 *   <li>у каждого числа своя подпись в разметке — на узком экране колонок нет,
 *       и подпись остаётся единственным носителем смысла цифры;</li>
 *   <li>шапка колонок и шапка раздела делят одно объявление сетки: две копии
 *       разъедутся на первой же правке одной из них;</li>
 *   <li>шапка колонок — не {@code <p>}: у абзаца в reset.css стоит мера прозы,
 *       и она обрезала строку на 818-м пикселе, уведя колонки на 615px левее чисел;</li>
 *   <li>доля освоенного показана нативным {@code <progress>} с защитой {@code max}
 *       от нуля — свой div потребовал бы инлайнового style (запрещён CSP) или JS;</li>
 *   <li>действие раздела ведёт на серверный конструктор и живёт ВНЕ {@code <summary>}.</li>
 * </ol>
 */
class CatalogColumnsContractTest {

    private static final Pattern COMMENT = Pattern.compile("<!--.*?-->", Pattern.DOTALL);

    private String read(String classpathLocation) throws IOException {
        return new String(new ClassPathResource(classpathLocation).getInputStream().readAllBytes(),
                StandardCharsets.UTF_8);
    }

    private String topicsMarkup() throws IOException {
        return COMMENT.matcher(read("templates/topics.html")).replaceAll("");
    }

    private String topicsCss() throws IOException {
        return read("static/css/pages-topics.css");
    }

    @Test
    void theColumnNamesArePrintedOnceAboveTheList() throws IOException {
        String markup = topicsMarkup();
        assertThat(markup)
                .as("шапка колонок исчезла — числа снова стоят без имён")
                .contains("class=\"catalog-legend\"");
        assertThat(markup.indexOf("class=\"catalog-legend\""))
                .as("шапка колонок обязана стоять ДО списка разделов, иначе она "
                        + "подписывает то, что уже прочитано")
                .isLessThan(markup.indexOf("class=\"card catalog-group\""));
        assertThat(markup)
                .as("шапка колонок печатается над пустым каталогом: подписи без строк")
                .containsPattern("class=\"catalog-legend\"[^>]*th:if=\"\\$\\{!#lists.isEmpty\\(catalogSections\\)}\"");
    }

    /**
     * Подпись числа не может быть {@code .visually-hidden}: та утилита объявлена с
     * {@code !important} и означает «скрыто всегда», а здесь подпись обязана
     * проявиться, как только колонки схлопнутся.
     */
    @Test
    void everyNumberCarriesItsOwnLabel() throws IOException {
        String markup = topicsMarkup();
        String header = markup.substring(markup.indexOf("catalog-group-kpi"),
                markup.indexOf("</summary>"));

        Matcher cells = Pattern.compile("class=\"catalog-num(?: [^\"]*)?\"").matcher(header);
        int cellCount = 0;
        while (cells.find()) cellCount++;
        int labelCount = header.split("catalog-num-label", -1).length - 1;

        assertThat(cellCount).as("числа раздела исчезли из шапки").isEqualTo(4);
        assertThat(labelCount)
                .as("у каждого числа обязана быть своя подпись: на узком экране "
                        + "колонок нет, и цифра остаётся без имени")
                .isEqualTo(cellCount);
        assertThat(header)
                .as("подпись числа не должна быть .visually-hidden — эта утилита "
                        + "объявлена с !important и означает «скрыто всегда»")
                .doesNotContain("visually-hidden");
    }

    @Test
    void theHeaderAndTheRowsShareOneGridDeclaration() throws IOException {
        String css = topicsCss();
        // Одно объявление на два селектора: разные копии сетки разъедутся, и это
        // ровно тот дефект, ради которого гейт написан.
        assertThat(css)
                .as("шапка колонок и шапка раздела обязаны делить одно объявление сетки")
                .containsPattern("\\.catalog-legend,\\s*\\n[^\\n]*\\.catalog-group-summary \\{");
        // Считаем ПОЛНУЮ сетку (с числовыми колонками): узкий блок ниже 1100px
        // законно переобъявляет первые две колонки, и запрещать это нельзя —
        // первая редакция гейта именно на этом и упала.
        int fullGrids = css.split("repeat\\(3, var\\(--catalog-num-col\\)\\)", -1).length - 1;
        assertThat(fullGrids)
                .as("полная сетка каталога объявлена дважды — вторая копия и есть будущий разъезд")
                .isEqualTo(1);
    }

    /**
     * {@code <p>} несёт меру прозы из reset.css. Шапка колонок — не проза.
     */
    @Test
    void theColumnHeaderIsNotAParagraph() throws IOException {
        assertThat(read("static/css/reset.css"))
                .as("мера у абзаца исчезла — причину запрета надо пересчитать")
                .containsPattern("html\\[data-design] p \\{[^}]*max-width: var\\(--measure\\)");
        assertThat(topicsMarkup())
                .as("шапка колонок снова абзац: мера прозы обрежет её раньше чисел")
                .doesNotContain("<p class=\"catalog-legend\"");
    }

    @Test
    void theMasteryShareIsANativeProgressWithANonZeroMax() throws IOException {
        String markup = topicsMarkup();
        assertThat(markup)
                .as("полоса освоения исчезла — середина строки снова пуста")
                .contains("<progress class=\"catalog-meter\"");
        assertThat(markup)
                .as("<progress> с max=0 становится неопределённым и анимируется без конца — "
                        + "декоративное движение, которое нечем выключить")
                .containsPattern("th:max=\"\\$\\{section.questionCount\\(\\) > 0 \\?");
        assertThat(markup)
                .as("ширина заливки своего div потребовала бы инлайнового style, "
                        + "запрещённого CSP (style-src-attr 'none')")
                .doesNotContain("th:style");
    }

    @Test
    void theSectionActionLivesOutsideTheSummaryAndWorksWithoutScripts() throws IOException {
        String markup = topicsMarkup();
        int summaryEnd = markup.indexOf("</summary>");
        int action = markup.indexOf("class=\"catalog-group-action\"");
        assertThat(action).as("действия раздела нет: раскрытие ведёт только к списку тем").isNotNegative();
        assertThat(action)
                .as("ссылка внутри <summary> крала бы у раскрытия часть площади и "
                        + "давала бы две цели в одном элементе")
                .isGreaterThan(summaryEnd);
        assertThat(markup)
                .as("действие обязано быть обычной ссылкой на серверный конструктор")
                .containsPattern("catalog-group-action\"[^>]*>\\s*<a class=\"btn secondary-btn\" th:href=\"@\\{/train\\(group=");
    }

    /**
     * Ниже порога колонок подпись числа становится единственным его именем: шапки
     * колонок над списком там больше нет.
     */
    @Test
    void whenTheColumnsCollapseTheLabelsAppear() throws IOException {
        String css = topicsCss();
        String narrow = mediaBlock(css, "@media (max-width: 1099px)");
        assertThat(narrow)
                .as("на узком экране шапка колонок обязана уйти: её колонок больше нет")
                .containsPattern("\\.catalog-legend \\{\\s*display: none");
        assertThat(narrow)
                .as("вместе с колонками исчезают имена — подпись у числа обязана проявиться")
                .containsPattern("\\.catalog-num-label \\{[^}]*position: static");
    }

    /**
     * Класс {@code .num} носили ячейки в двух шаблонах, а правила под него в CSS не
     * было ни одного: разметка объявляла «здесь число», таблица печатала его влево.
     */
    @Test
    void theNumericColumnClassActuallyDoesSomething() throws IOException {
        String tableCss = read("static/css/components-table.css");
        assertThat(tableCss)
                .as(".num снова мёртвый класс: разметка помечает числовые колонки, "
                        + "а таблица печатает числа как имена")
                .containsPattern("\\.data-table \\.num \\{[^}]*text-align: right");
        assertThat(tableCss)
                .as("числа в колонке сравнивают по разряду — цифры обязаны быть табличными")
                .containsPattern("\\.data-table \\.num \\{[^}]*font-variant-numeric: tabular-nums");
    }

    /**
     * Строка раздела — это строка итогов своей таблицы. Значит, три её числа стоят
     * над теми же тремя колонками, и порядок колонок в таблице обязан это держать.
     */
    @Test
    void theTableRepeatsTheOrderOfTheSectionTotals() throws IOException {
        String markup = topicsMarkup();
        String head = markup.substring(markup.indexOf("<thead>"), markup.indexOf("</thead>"));
        assertThat(head.indexOf("Состояние"))
                .as("«Состояние» вернулось в конец таблицы — числа больше не стоят "
                        + "под своими итогами у правой кромки")
                .isLessThan(head.indexOf("Вопросов"));
        assertThat(List.of(head.indexOf("Вопросов"), head.indexOf("Освоено"), head.indexOf("К повторению")))
                .as("порядок числовых колонок таблицы разошёлся с порядком итогов раздела")
                .isSorted();

        String rows = markup.substring(markup.indexOf("<tbody>"), markup.indexOf("</tbody>"));
        assertThat(rows.indexOf("row.untouched()"))
                .as("порядок ячеек в строке обязан повторять порядок заголовков")
                .isLessThan(rows.indexOf("row.total"));
    }

    /**
     * Колонки таблицы считаются теми же величинами, что сетка шапки. Числа в CSS
     * вместо них означают, что первая же правка ширины разведёт два слоя.
     */
    @Test
    void theTableColumnsAreDerivedFromTheSameTokens() throws IOException {
        // Скобка в запросе обязательна: выше в файле стоит `@media (min-width: 1100px)
        // and (max-width: 1439px)`, и поиск по префиксу возвращал ЕГО тело — гейт
        // проверял не тот блок и падал на верном коде.
        String block = mediaBlock(topicsCss(), "@media (min-width: 1100px) {");
        assertThat(block)
                .as("таблица раздела перестала считать колонки от шапки: без fixed "
                        + "ширины колонок определяет содержимое ячеек")
                .contains("table-layout: fixed");
        assertThat(block)
                .as("промежуток колонки обязан уходить в левое поле ячейки, иначе "
                        + "правая кромка числа не совпадёт с кромкой итога")
                .containsPattern("padding-right: 0;\\s*\\n\\s*padding-left: var\\(--catalog-col-gap\\)");
        assertThat(block)
                .as("ширины колонок таблицы заданы мимо величин каталога — они разъедутся")
                .contains("width: calc(var(--catalog-num-col) + var(--catalog-col-gap))")
                .contains("width: calc(var(--catalog-num-col-last) + var(--catalog-col-gap))");
    }

    /** Тело медиазапроса по балансу скобок. */
    private String mediaBlock(String css, String query) {
        int at = css.indexOf(query);
        assertThat(at).as("медиазапроса нет: " + query).isNotNegative();
        int open = css.indexOf('{', at);
        int depth = 0;
        for (int i = open; i < css.length(); i++) {
            char c = css.charAt(i);
            if (c == '{') depth++;
            if (c == '}') {
                depth--;
                if (depth == 0) return css.substring(open, i);
            }
        }
        throw new AssertionError("медиазапрос не закрыт: " + query);
    }
}
