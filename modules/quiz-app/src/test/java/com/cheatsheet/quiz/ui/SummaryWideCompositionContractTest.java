package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Экран итогов использует ту ширину, которую ему даёт оболочка, — и отдаёт её содержимому.
 *
 * <h2>Найденные дефекты, а не гипотезы (iter-63, замеры в WebKit@2)</h2>
 * На {@code /session-summary} сошлись сразу четыре независимых сужения, и каждое по
 * отдельности выглядело осмысленным.
 *
 * <ol>
 *   <li><b>Мера чтения на странице данных.</b> {@code .summary-page .ed-page} капалась
 *       {@code --max-width-reading} (896px), пока {@code /stats} и {@code /settings} брали
 *       {@code --max-width-page}. Полоса контента росла 1060 → 2340px от 1280 до 2560, а
 *       страница стояла на 896: мимо уходило 384px на 1280 и 1444px на 2560. Две ширины для
 *       двух экранов данных — тот самый дубль систем, который запрещено оставлять.</li>
 *   <li><b>Двухстолбцовая раскладка из разнородных сущностей.</b> {@code 7fr/5fr} ставила
 *       таблицу тем слева, ошибки справа. Левый столбец растёт по числу ТЕМ (обычно 1–3
 *       строки), правый — по числу ОШИБОК (до сотен): на 1280/1440/1920/2560 левый
 *       заканчивался на 1067/1067/1037/1067px выше правого. Дыра постоянная, а не краевой
 *       случай редких данных. Таблице при этом доставалось 476…593px из 1280.</li>
 *   <li><b>Скролл-обёртка, применённая к самой таблице.</b> {@code display: block} превращает
 *       {@code table} в блок с анонимным табличным боксом внутри, и этот бокс —
 *       shrink-to-fit: бокс 1041px, сумма колонок 406px, 635px пустоты внутри собственной
 *       рамки. В 7fr-колонке (~600px) расхождение пряталось.</li>
 *   <li><b>Прозаическая мера на сетке карточек.</b> Базовый reset давал любому {@code ul}
 *       {@code max-width: var(--measure)} (72ch ≈ 818px). Для абзацного списка это верно, для
 *       сетки ошибок — нет: на 1440 сетка упиралась в 818px и давала две колонки вместо
 *       четырёх. <em>Снято системно в iter-64:</em> мера переехала с тега на прозо-контейнер
 *       {@code .markdown-content}, и локальное {@code max-width: none} отсюда удалено вместе с
 *       причиной. Гейт на само правило живёт в {@code PageWidthScaleContractTest}; здесь
 *       остались только свойства сетки, принадлежащие странице итогов.</li>
 * </ol>
 *
 * <h2>Почему это гейт, а не разовая правка</h2>
 * Общее у всех четырёх — ширину задаёт одно место, а расходуется она в другом, и расхождение
 * не видно ни в коде (каждое правило по отдельности осмысленно), ни на скриншоте одной
 * ширины (на 1280 дыры читаются как воздух). Ловится только сопоставлением. Тесты ниже
 * проверяют структуру решения, а не пиксели: страница не заводит собственного капа, полоса не
 * делится на столбцы с независимым ростом, таблица остаётся таблицей на десктопе, сетка не
 * подчиняется прозаической мере.
 *
 * <p>Мобильная раскладка карточками (≤760px) намеренно выведена из-под гейта: там
 * {@code display: block} — приём, а не дефект, и ARIA-роли в шаблоне существуют именно ради
 * неё.
 */
class SummaryWideCompositionContractTest {

    private static final Path SUMMARY_CSS = Path.of("src/main/resources/static/css/pages-summary.css");
    private static final Path SUMMARY_TEMPLATE = Path.of("src/main/resources/templates/session-summary.html");

    /** Граница, ниже которой таблица намеренно становится стопкой карточек. */
    private static final String CARD_VIEW_BREAKPOINT = "@media (max-width: 760px)";

    /**
     * Объявления без комментариев.
     *
     * <p>Каждое снятое здесь сужение оставило после себя комментарий с причиной и замером —
     * то есть с дословным упоминанием {@code --max-width-reading}, {@code 7fr} и
     * {@code display: block}. Без вырезания комментариев гейт запрещал бы не возврат правила,
     * а рассказ о том, почему его сняли: документация в коде стала бы нарушением контракта.
     */
    private static String declarationsOnly(String css) {
        return css.replaceAll("(?s)/\\*.*?\\*/", "");
    }

    /** Всё, что действует на десктопе: файл до мобильного блока раскладки карточками. */
    private String desktopCss() {
        String css = declarationsOnly(read(SUMMARY_CSS));
        int cardView = css.indexOf(CARD_VIEW_BREAKPOINT);
        assertThat(cardView)
                .as("в pages-summary.css нет блока %s — раскладку карточками переименовали или "
                        + "сняли, и контракт больше не знает, где кончается десктоп", CARD_VIEW_BREAKPOINT)
                .isPositive();
        return css.substring(0, cardView);
    }

    private String ruleBody(String css, String selector) {
        int start = css.indexOf(selector + " {");
        assertThat(start)
                .as("в pages-summary.css нет правила `%s` — селектор переименовали, контракт надо "
                        + "переписать под факт", selector)
                .isNotNegative();
        int open = css.indexOf('{', start);
        int close = css.indexOf('}', open);
        assertThat(close).as("правило `%s` не закрыто", selector).isPositive();
        return css.substring(open + 1, close);
    }

    @Test
    void summaryPageDoesNotDeclareItsOwnWidth() {
        assertThat(declarationsOnly(read(SUMMARY_CSS)))
                .as("на страницу итогов вернули меру чтения: она для сплошного текста, а итог — "
                        + "счёт, полоса KPI, пятиколоночная таблица и сетка ошибок. Замер: полоса "
                        + "контента 1060→2340px от 1280 до 2560, страница стояла на 896")
                .doesNotContain("--max-width-reading");
    }

    @Test
    void contentBandIsNotSplitIntoIndependentlyGrowingColumns() {
        String css = declarationsOnly(read(SUMMARY_CSS));

        assertThat(css)
                .as("двухстолбцовая раскладка вернулась: столбцы наполняются разными сущностями "
                        + "(левый — темы, правый — ошибки), и левый кончается на ~1067px выше правого "
                        + "на любой ширине")
                .doesNotContain("7fr");
        assertThat(css)
                .as("правый столбец прежней раскладки вернулся — см. выше про постоянную дыру")
                .doesNotContain("5fr");
    }

    @Test
    void topicTableStaysATableOnDesktop() {
        String base = ruleBody(desktopCss(), "html[data-design] .summary-page .summary-table");

        assertThat(base)
                .as("таблице тем вернули display:block — она станет блоком с shrink-to-fit "
                        + "табличным боксом внутри (замер: бокс 1041px, колонки 406px, 635px пустоты "
                        + "внутри собственной рамки)")
                .doesNotContain("display: block");
        assertThat(base)
                .as("скролл вернулся на саму таблицу: переполнение лечит внешняя обёртка "
                        + ".table-scroll, иначе у продукта снова два ответа на один вопрос")
                .doesNotContain("overflow-x: auto");
        assertThat(base)
                .as("без table-layout:fixed авто-раскладка раздаст остаток ширины всем пяти "
                        + "колонкам пропорционально, а забирать его должна «Тема»")
                .contains("table-layout: fixed");
    }

    /**
     * Скелет таблицы принадлежит компоненту, а страница держит только свою дельту.
     *
     * <p>До iter-76 {@code width/border-collapse/паддинги/шапка} были переобъявлены на
     * странице целиком — и ровно те же строки лежали в {@code pages-stats.css} (дважды) и в
     * прозе. Четыре копии одного скелета: правка в одной не доезжала до остальных, и
     * {@code table-layout:fixed} здесь молча подразумевал ширину, объявленную рядом.
     */
    @Test
    void topicTableTakesItsSkeletonFromTheSharedComponent() {
        String base = ruleBody(desktopCss(), "html[data-design] .summary-page .summary-table");

        assertThat(base)
                .as("ширина таблицы — свойство компонента .data-table; переобъявление здесь "
                        + "возвращает вторую копию скелета")
                .doesNotContain("width: 100%");
        assertThat(base)
                .as("border-collapse принадлежит компоненту")
                .doesNotContain("border-collapse");

        String template = read(SUMMARY_TEMPLATE);
        assertThat(template)
                .as("таблица итогов должна носить класс общего компонента, иначе скелет ей "
                        + "неоткуда взять")
                .contains("class=\"data-table summary-table\"");
        assertThat(template)
                .as("единственный ответ на «таблица шире полосы» — обёртка .table-scroll вокруг "
                        + "таблицы (замер: при root=25px пять колонок дают 687px и без обёртки "
                        + "толкают страницу вбок)")
                .contains("<div class=\"table-scroll\"");
    }

    @Test
    void mistakesGridIgnoresProseMeasureAndPicksItsOwnColumnCount() {
        String rule = ruleBody(declarationsOnly(read(SUMMARY_CSS)),
                "html[data-design] .summary-page .summary-mistakes");

        assertThat(rule)
                .as("список ошибок должен быть сеткой, а не колонкой: колонка была следствием "
                        + "удалённой двухстолбцовой раскладки страницы")
                .contains("display: grid");
        assertThat(rule)
                .as("число колонок выбирает контент (auto-fill), а не набор медиазапросов — "
                        + "4 на 1280, 2 на 768, 1 на 390 без единого breakpoint")
                .contains("auto-fill");
        assertThat(rule)
                .as("minmax без min(100%, …) даёт горизонтальный скролл там, где контейнер уже "
                        + "минимальной карточки (замер: 360px)")
                .contains("min(100%, 300px)");
    }

    @Test
    void mistakesGridAppliesToTheListThatActuallyCarriesTheCards() {
        assertThat(read(SUMMARY_TEMPLATE))
                .as("сетка ошибок висит на .summary-mistakes — если разметка переедет на другой "
                        + "элемент, правило останется, а колонок не будет")
                .contains("<ul class=\"summary-mistakes\">");
    }

    private static String read(Path path) {
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("не прочитать " + path, e);
        }
    }
}
