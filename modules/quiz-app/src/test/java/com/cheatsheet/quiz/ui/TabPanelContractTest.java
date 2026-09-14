package com.cheatsheet.quiz.ui;

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
 * Панель вкладки — вторая половина той же системы, что и полоса.
 *
 * <h2>Дефект, а не гипотеза (iter-80, замеры в WebKit@2, 1440×900, DPR 2)</h2>
 * После iter-79 полоса вкладок стала одним компонентом, а видимость панелей осталась
 * расписанной по семьям страниц. Гасили лишние панели три разных класса-маркера, и вешал
 * их JS РОУТОВОГО чанка — то есть уже после отрисовки страницы. Отсчёт от
 * first-contentful-paint до появления маркера:
 * <ul>
 *   <li>{@code /stats} — FCP 253 мс, маркер {@code body.js-tabs} 402 мс: 149 мс страница
 *       стояла со всеми тремя панелями в стопке. Высота документа за это окно прошла
 *       21734 → 9405 → 3939 px, то есть схлопнулась в 5.5 раза ПОСЛЕ первого кадра.
 *       Ползунок прокрутки прыгал с «страница на 24 экрана» на «страница на 4», а
 *       Chart.js всё это время строил графики трёх панелей в раскладке, которой не будет;</li>
 *   <li>{@code /settings} — 0 мс, но ценой личного механизма: pre-paint писал на {@code <html>}
 *       атрибут {@code data-settings-tab}, и четыре CSS-правила разбирали его по ID трёх
 *       конкретных панелей. Ни одна другая страница этим воспользоваться не могла;</li>
 *   <li>{@code /focus} — 0 мс случайно: модуль материалов успевал до первого кадра.</li>
 * </ul>
 *
 * <p>Плюс два механизма для одного и того же PE-гашения полосы: атрибут {@code hidden} у
 * материалов и утилита {@code .hidden} у настроек и аналитики, и три разные функции
 * {@code reveal()}, снимавшие их.
 *
 * <h2>Что держат тесты</h2>
 * Не пиксели — их проверяет браузер на матрице вьюпортов. Тесты держат структуру решения:
 * видимость панели объявлена ОДИН раз и по РОЛИ, гейт срабатывает до первого кадра
 * ({@code [data-js]}), сервер сам помечает открытую панель, страницы видимость не трогают,
 * а pre-paint скрипт не знает ID ни одной конкретной панели.
 */
class TabPanelContractTest {

    private static final Path RESOURCES = Path.of("src/main/resources");
    private static final Path CSS_DIR = RESOURCES.resolve("static/css");
    private static final Path COMPONENT_CSS = CSS_DIR.resolve("components-tabs.css");
    private static final Path TEMPLATES = RESOURCES.resolve("templates");
    private static final Path HEAD = TEMPLATES.resolve("fragments/head.html");
    private static final Path BEHAVIOUR_JS = RESOURCES.resolve("static/js/ui/tablist.js");

    /** Шаблоны с полосой вкладок. Все три семьи, чтобы правило не проверялось на одной. */
    private static final List<Path> TAB_TEMPLATES = List.of(
            TEMPLATES.resolve("focus-training.html"),
            TEMPLATES.resolve("settings.html"),
            TEMPLATES.resolve("stats.html"));

    /** Классы-маркеры «вкладки включены», которых больше нет ни в CSS, ни в JS, ни в разметке. */
    private static final List<String> DEAD_MARKERS = List.of("js-tabs", "js-content-tabs", "data-settings-tab");

    private static String read(Path path) {
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("не читается " + path, e);
        }
    }

    /** Объявления без комментариев: рассказ о снятом приёме не должен считаться приёмом. */
    private static String declarationsOnly(String source) {
        return source.replaceAll("(?s)/\\*.*?\\*/", "");
    }

    /** Разметка без комментариев Thymeleaf {@code <!--/* … *&#47;-->} и обычных HTML-комментариев. */
    private static String markupOnly(String html) {
        return html.replaceAll("(?s)<!--.*?-->", "");
    }

    private static List<String[]> rules(Path css) {
        List<String[]> out = new ArrayList<>();
        for (String block : declarationsOnly(read(css)).split("}")) {
            int brace = block.lastIndexOf('{');
            if (brace < 0) {
                continue;
            }
            out.add(new String[] {block.substring(0, brace), block.substring(brace + 1)});
        }
        return out;
    }

    private static List<Path> allCss() {
        try (Stream<Path> files = Files.list(CSS_DIR)) {
            return files.filter(p -> p.getFileName().toString().endsWith(".css")).sorted().toList();
        } catch (IOException e) {
            throw new UncheckedIOException("не читается " + CSS_DIR, e);
        }
    }

    @Test
    void panelVisibilityIsDeclaredOnceByRoleAndGatedBeforeFirstPaint() {
        String css = declarationsOnly(read(COMPONENT_CSS));
        assertThat(css)
                .as("видимость панели обязана ключеваться ролью — тем же селектором, которым "
                        + "панель находит поведение, — и гейтом [data-js], который ставит "
                        + "pre-paint скрипт head. Иначе вкладочное состояние снова приедет "
                        + "после отрисовки: на /stats это стоило 149 мс и схлопывания "
                        + "документа с 21734px до 3939px")
                .contains("html[data-design][data-js] [role=\"tabpanel\"]:not(.is-active) { display: none; }");
        assertThat(css)
                .as("полоса вкладок без JS не нужна и гасится тем же гейтом — одним способом "
                        + "на все три семьи вместо атрибута hidden у одной и утилиты .hidden у двух")
                .contains("html[data-design]:not([data-js]) [role=\"tablist\"] { display: none; }");
    }

    @Test
    void tabOverridesOfStackStylingLiveInTheLayerThatOutranksPages() {
        String css = declarationsOnly(read(COMPONENT_CSS));
        int utilities = css.indexOf("@layer utilities");
        assertThat(utilities)
                .as("поправки «панель показана вкладкой, а не стопкой» обязаны лежать в слое, "
                        + "который бьёт pages: порядок tokens→…→components→pages→utilities значит, "
                        + "что любое правило страницы перекрывает components независимо от "
                        + "специфичности")
                .isGreaterThan(0);

        assertThat(css.indexOf("[role=\"tabpanel\"] { margin: 0; }"))
                .as("ритм стопки (`.stats-panel { margin: 0 0 var(--space-8) }`) задаёт СТРАНИЦА — "
                        + "значит отменять его под вкладками можно только из utilities")
                .isGreaterThan(utilities);
        assertThat(css.indexOf(".tabpanel-title"))
                .as("вид заголовка панели тоже красит страница: замер iter-80 показал, что из pages "
                        + "уже приезжал `margin: 0 0 32px` вместо `-1px` из sr-only-рецепта. "
                        + "Пока правило в components, страница отыгрывает его по частям")
                .isGreaterThan(utilities);

        assertThat(css.indexOf("[role=\"tabpanel\"]:not(.is-active) { display: none; }"))
                .as("а вот сама видимость остаётся в components: её никто не перекрывает, потому что "
                        + "страницам запрещено трогать display панели — это держит "
                        + "pageFilesDoNotDecideWhichPanelIsVisible")
                .isLessThan(utilities);
    }

    @Test
    void serverMarksTheOpenPanelItself() {
        for (Path template : TAB_TEMPLATES) {
            String markup = markupOnly(read(template));
            assertThat(markup)
                    .as("%s: открытую панель помечает СЕРВЕР классом is-active. Без этой метки "
                            + "правило по роли скроет всё до загрузки модуля — вместо вспышки "
                            + "стопки получится вспышка пустоты", template.getFileName())
                    .contains("is-active");
            assertThat(markup)
                    .as("%s: панель без роли tabpanel правилом не гасится — роль обязана быть "
                            + "в разметке, а не навешиваться скриптом", template.getFileName())
                    .contains("tabpanel");
        }
    }

    @Test
    void tablistCarriesNoMarkupLevelHidingAnyMore() {
        for (Path template : TAB_TEMPLATES) {
            String markup = markupOnly(read(template));
            assertThat(markup)
                    .as("%s: PE-гашение полосы живёт в CSS. Утилита .hidden на полосе означала бы "
                            + "второй механизм для той же роли — а снимать её пришлось бы "
                            + "роутовому модулю, то есть после первого кадра", template.getFileName())
                    .doesNotContain("tablist\" hidden")
                    .doesNotContain("-tabs hidden")
                    .doesNotContain("tablist hidden");
        }
    }

    @Test
    void noMarkerClassSurvivesAnywhere() {
        List<String> offenders = new ArrayList<>();
        for (Path css : allCss()) {
            String declarations = declarationsOnly(read(css));
            DEAD_MARKERS.stream().filter(declarations::contains)
                    .forEach(m -> offenders.add(css.getFileName() + " → " + m));
        }
        for (Path template : TAB_TEMPLATES) {
            String markup = markupOnly(read(template));
            DEAD_MARKERS.stream().filter(markup::contains)
                    .forEach(m -> offenders.add(template.getFileName() + " → " + m));
        }
        String behaviour = read(BEHAVIOUR_JS);
        DEAD_MARKERS.stream().filter(behaviour::contains).forEach(m -> offenders.add("tablist.js → " + m));

        assertThat(offenders)
                .as("две конкурирующие системы гашения панелей не оставляем. Пока жив хоть один "
                        + "класс-маркер, страница может снова начать ждать роутовый модуль")
                .isEmpty();
    }

    @Test
    void pageFilesDoNotDecideWhichPanelIsVisible() {
        List<String> offenders = new ArrayList<>();
        for (Path css : allCss()) {
            if (!css.getFileName().toString().startsWith("pages-")) {
                continue;
            }
            for (String[] rule : rules(css)) {
                // Проверяем саму панель: .stats-panel-intro — обычная шапка внутри неё.
                boolean aboutPanel = rule[0].contains("[role=\"tabpanel\"]")
                        || java.util.regex.Pattern.compile(
                                "\\.(?:settings-panel|stats-panel|content-tabpanel)(?![\\w-])")
                                .matcher(rule[0]).find();
                if (aboutPanel && rule[1].contains("display:")) {
                    offenders.add(css.getFileName() + ": " + rule[0].strip().replaceAll("\\s+", " "));
                }
            }
        }
        assertThat(offenders)
                .as("страница задаёт панели вертикальный ритм стопки, а не то, какая из них "
                        + "видна. Ровно из-за трёх таких копий видимость и разъехалась по семьям")
                .isEmpty();
    }

    @Test
    void rememberedPanelIsRestoredGenericallyBeforeFirstPaint() {
        String head = markupOnly(read(HEAD));
        assertThat(head)
                .as("pre-paint восстанавливает запомненную панель ОБЩИМ правилом: :has(#id) делает "
                        + "его самоприцельным, поэтому механизм работает на любой полосе вкладок, "
                        + "а не только на настройках")
                .contains("tabPanel:")
                .contains(":has(#")
                .contains("[role=\"tabpanel\"]");
        assertThat(head)
                .as("скрипт не имеет права знать ID конкретных панелей — именно это делало "
                        + "прежний CLS-резерв непереносимым")
                .doesNotContain("panel-session")
                .doesNotContain("panel-appearance")
                .doesNotContain("stats-panel-topics");

        assertThat(read(BEHAVIOUR_JS))
                .as("в хранилище кладётся ID ПАНЕЛИ: ID вкладки pre-paint скрипту ничего не "
                        + "говорит — связь tab→panel живёт в aria-controls, до которого CSS "
                        + "не дотягивается")
                .contains("tabPanel:")
                .contains("localStorage.setItem(storeKey, panels[index].id)");
    }
}
