package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Главное действие конструктора сессии достижимо без прокрутки, и предпросмотр набора
 * виден тогда же, когда виден меняющий его контрол.
 *
 * <h2>Найденный дефект, а не гипотеза</h2>
 * Замер WebKit 18.2 (DPR 2, живое приложение, дефолтные настройки) на вкладке «Сессия»:
 *
 * <ul>
 *   <li>кнопка «Начать тренировку» стояла на 1382–1430px и не помещалась в окно на
 *       ДЕВЯТИ вьюпортах §10 из десяти — видел её только 2560×1440;</li>
 *   <li>чип фильтра «Ошибки» лежал на y≈758, а предпросмотр, который этот чип
 *       переписывает («все темы» → «ошибки · 19»), — на y≈1106. Разрыв 312px на
 *       1920×1200: человек менял набор и не видел ответа системы. Screen reader ответ
 *       слышал ({@code role="status"}), глаз — нет;</li>
 *   <li>высота документа держалась ровно 1573px и на 1366, и на 2560 — композиция вообще
 *       не справлялась о высоте окна.</li>
 * </ul>
 *
 * <p>Перекомпоновка конструктора в колонки проверена замером и отвергнута: разбивка
 * «Охват | Режим | сводка» уводит кнопку с 1430 на 1056 и всё равно оставляет её ниже
 * сгиба на 1280×800, 1366×768, 1440×900 и 1512×982 — ровно на ходовых MacBook.
 *
 * <h2>Что именно закреплено</h2>
 * Инвариант: <b>панель запуска закреплена у нижнего края, закреплена во всю ширину сетки
 * конструктора, а кнопка, вынесенная из формы, всё ещё отправляет эту форму</b>.
 * Числа сгиба тест не проверяет — это работа замера ({@code work/redesign}), а не файла на
 * диске. Закреплены три вещи, каждая из которых уже ломалась в этой итерации.
 *
 * <h2>iter-65: тот же инвариант, другой носитель</h2>
 * Обёртки {@code .builder-main} / {@code .builder-aside} сняты (FRD-016): они делали из
 * конструктора две независимо растущие колонки — 628px против 896px, то есть 268px пустого
 * прямоугольника под левой на каждой десктопной ширине. Сетка теперь именованная
 * ({@code grid-template-areas}), и «во всю ширину» выражается не {@code grid-column: 1 / -1},
 * а строкой областей, где {@code launch} занимает все колонки. Проверяем именно это, плюс то,
 * что панель осталась ПРЯМЫМ ребёнком сетки: у внука {@code grid-area} не действует молча.
 */
class SessionBuilderLaunchReachContractTest {

    private static final Path SHELL_CSS = Path.of("src/main/resources/static/css/shell.css");
    private static final Path SETTINGS_HTML = Path.of("src/main/resources/templates/settings.html");

    /** Ширина, ниже которой {@code .session-builder} схлопывается в одну колонку. */
    private static final int SINGLE_COLUMN_UPPER_BOUND_PX = 900;

    private static final Pattern PIN_RULE = Pattern.compile(
            "([^{}]*\\.builder-launch-bar[^{}]*)\\{([^{}]*position:\\s*sticky[^{}]*)}");

    /** CSS без комментариев: обоснования цитируют и селекторы, и свойства, которые тест ищет. */
    private final String css = read(SHELL_CSS).replaceAll("(?s)/\\*.*?\\*/", "");

    /** Разметка без комментариев: обоснования правок цитируют снятые классы. */
    private final String html = read(SETTINGS_HTML).replaceAll("(?s)<!--.*?-->", "");

    private String desktopPinBody() {
        Matcher media = Pattern.compile("@media \\(min-width:\\s*(\\d+)px\\)\\s*\\{").matcher(css);
        while (media.find()) {
            String body = braceBalancedBody(media.end() - 1);
            if (PIN_RULE.matcher(body).find()) {
                assertThat(Integer.parseInt(media.group(1)))
                        .as("диапазон закрепления не стыкуется с одноколоночным: конструктор "
                                + "схлопывается в стопку при max-width %dpx, а панель закреплена "
                                + "при min-width %s. Разрыв оставит полосу ширин с утонувшей "
                                + "кнопкой, нахлёст поставит вторую закреплённую полосу вплотную "
                                + "к фиксированной мобильной навигации",
                                SINGLE_COLUMN_UPPER_BOUND_PX, media.group(1))
                        .isEqualTo(SINGLE_COLUMN_UPPER_BOUND_PX + 1);
                return body;
            }
        }
        throw new AssertionError("в shell.css нет ни одного @media (min-width: …), где "
                + ".builder-launch-bar закреплена position: sticky. Без закрепления кнопка "
                + "«Начать тренировку» уходит под сгиб на девяти вьюпортах §10 из десяти "
                + "(замер до правки: низ кнопки 1430px при высоте окна 800–1200px), а "
                + "предпросмотр набора отстаёт от меняющих его чипов на 312px");
    }

    private String braceBalancedBody(int openBrace) {
        int depth = 0;
        for (int i = openBrace; i < css.length(); i++) {
            char c = css.charAt(i);
            if (c == '{') {
                depth++;
            } else if (c == '}' && --depth == 0) {
                return css.substring(openBrace + 1, i);
            }
        }
        throw new AssertionError("незакрытый блок в " + SHELL_CSS + " с позиции " + openBrace);
    }

    @Test
    void pinnedBarSpansTheWholeBuilderGrid() {
        List<String> rows = desktopGridAreaRows();
        int columns = rows.get(0).trim().split("\\s+").length;
        assertThat(rows)
                .as("в раскладке конструктора нет строки, где launch занимает ВСЕ %d колонки. "
                        + "В колонке 640px три метрики предпросмотра (274 + 342 + 139px) в ряд не "
                        + "встают, панель вырастает до 128–151px и в этой полосе прячет чипы "
                        + "фильтров и селекты: elementFromPoint над контролом возвращает саму "
                        + "панель. Замер той итерации — панель накрыла собой те самые фильтры, "
                        + "ради обратной связи от которых её и ставили. Строки: %s",
                        columns, rows)
                .anySatisfy(row -> {
                    String[] cells = row.trim().split("\\s+");
                    assertThat(cells).hasSize(columns).containsOnly("launch");
                });

        Matcher bar = Pattern.compile("([^{}]*\\.builder-launch-bar)\\s*\\{([^{}]*)}").matcher(css);
        assertThat(bar.find())
                .as("правила .builder-launch-bar в shell.css больше нет")
                .isTrue();
        assertThat(bar.group(2))
                .as("панель запуска не привязана к области launch: строка областей её описывает, "
                        + "а сама панель встанет автопотоком в первую свободную ячейку — то есть "
                        + "в колонку, а не во всю ширину")
                .containsPattern("grid-area:\\s*launch");
    }

    @Test
    void pinnedBarIsADirectChildOfTheBuilderGrid() {
        assertThat(html)
                .as("в settings.html вернулись обёртки-колонки .builder-main / .builder-aside. "
                        + "Они делали из конструктора две независимо растущие колонки: замер "
                        + "WebKit@2 на 1512 — 628px против 896px, 268px пустого прямоугольника "
                        + "под левой на КАЖДОЙ десктопной ширине (§6). Раскладку задаёт "
                        + "grid-template-areas, обёртки ей не нужны")
                .doesNotContain("builder-main")
                .doesNotContain("builder-aside");

        assertThat(depthInsideBuilder("builder-launch-bar"))
                .as("панель запуска снова лежит внутри обёртки, а не прямым ребёнком "
                        + ".session-builder. У внука сетки `grid-area: launch` не действует — и "
                        + "молча: правило останется в CSS, панель разъедется на три ряда и "
                        + "накроет фильтры. Носитель важнее правила")
                .isEqualTo(1);
    }

    /** Строки {@code grid-template-areas} десктопного правила {@code .session-builder}. */
    private List<String> desktopGridAreaRows() {
        Matcher rule = Pattern.compile(
                "([^{}]*\\.session-builder)\\s*\\{([^{}]*grid-template-areas[^{}]*)}").matcher(css);
        List<String> rows = new ArrayList<>();
        // Правил с grid-template-areas два: десктопное и мобильное (`none`). Берём первое
        // со строками областей — сравнивать колонки имеет смысл только у него.
        while (rows.isEmpty() && rule.find()) {
            Matcher row = Pattern.compile("\"([^\"]+)\"").matcher(rule.group(2));
            while (row.find()) {
                rows.add(row.group(1));
            }
        }
        assertThat(rows)
                .as("в shell.css нет правила .session-builder со строками grid-template-areas. "
                        + "Без именованных областей пять блоков конструктора встают автопотоком, "
                        + "и порядок разметки (он же порядок чтения и табуляции) снова становится "
                        + "заложником визуальной раскладки")
                .isNotEmpty();
        return rows;
    }

    /**
     * Глубина элемента с указанным классом относительно {@code .session-builder}: 1 — прямой
     * ребёнок. Считаем только контейнерные теги — только они могут оказаться обёрткой.
     */
    private int depthInsideBuilder(String marker) {
        int start = html.indexOf("class=\"session-builder\"");
        assertThat(start).as("в settings.html нет .session-builder").isNotNegative();

        Matcher tag = Pattern.compile("</?(div|section|aside|form|fieldset|main)\\b")
                .matcher(html);
        int depth = 0;
        for (int at = html.indexOf('>', start) + 1; tag.find(at); at = tag.end()) {
            if (html.startsWith("</", tag.start())) {
                if (--depth < 0) {
                    throw new AssertionError("элемент с классом " + marker
                            + " не найден внутри .session-builder");
                }
                continue;
            }
            int close = html.indexOf('>', tag.start());
            if (html.substring(tag.start(), close).contains(marker)) {
                return depth + 1;
            }
            depth++;
        }
        throw new AssertionError("элемент с классом " + marker + " не найден в " + SETTINGS_HTML);
    }

    @Test
    void startButtonStaysBoundToTheFormItLeft() {
        Matcher button = Pattern.compile("<button[^>]*id=\"session-start-btn\"[^>]*>", Pattern.DOTALL)
                .matcher(html);
        assertThat(button.find())
                .as("кнопки #session-start-btn нет в settings.html — её ищет по id "
                        + "settings/session-form.js (initSessionModeForm подставляет подпись режима)")
                .isTrue();
        String tag = button.group();

        assertThat(tag)
                .as("кнопка запуска вынесена из #session-form ради закрепления, но потеряла "
                        + "атрибут form. Без него submit-кнопка вне формы не отправляет ничего: "
                        + "сессия перестаёт стартовать, и в первую очередь — без JS, где нет "
                        + "обходного пути")
                .contains("form=\"session-form\"");
        assertThat(tag)
                .as("кнопка запуска перестала быть submit: type=button отправит форму только "
                        + "скриптом, а без JS кнопка станет мёртвой")
                .contains("type=\"submit\"");
    }

    @Test
    void pinnedBarIsOpaqueOverScrollingContent() {
        Matcher rule = PIN_RULE.matcher(desktopPinBody());
        assertThat(rule.find()).isTrue();
        String declarations = rule.group(2);

        assertThat(declarations)
                .as("у закреплённой панели нет непрозрачного фона: контролы конструктора будут "
                        + "проезжать СКВОЗЬ предпросмотр и кнопку запуска")
                .containsPattern("background:\\s*var\\(--color-bg-");
        assertThat(declarations)
                .as("у закреплённой панели нет верхней линии: край панели неотличим от контента, "
                        + "который под неё уезжает")
                .contains("border-top");
    }

    @Test
    void builderControlsKeepClearanceFromThePinnedBar() {
        Matcher clearance = Pattern.compile(
                "([^{}]*\\.session-builder[^{}]*)\\{([^{}]*scroll-margin-bottom[^{}]*)}")
                .matcher(desktopPinBody());

        assertThat(clearance.find())
                .as("у контролов конструктора нет запаса scroll-margin-bottom рядом с "
                        + "закреплённой панелью. Tab по чипам, селектам и счётчику скроллит "
                        + "контрол в вид — без запаса последние контролы приезжают ПОД панель "
                        + "и настраиваются вслепую")
                .isTrue();
        assertThat(clearance.group(1))
                .as("запас навешен на .builder-main, а панель с этой итерации — ребёнок "
                        + ".session-builder во всю ширину. Правило промахнётся мимо контролов, "
                        + "которые панель и накрывает")
                .contains(".session-builder");
    }

    @Test
    void previewStaysOneRowInsideThePinnedBar() {
        Matcher metric = Pattern.compile(
                "([^{}]*\\.builder-launch-bar[^{}]*\\.preview-metric[^{}]*)\\{([^{}]*)}").matcher(css);
        assertThat(metric.find())
                .as("в панели запуска нет правила для .preview-metric. Базовое правило "
                        + ".session-preview .preview-metric ставит flex-direction: column — "
                        + "подпись НАД значением, две строки на метрику. Замер: панель выросла "
                        + "до 277px и накрыла чипы фильтров")
                .isTrue();

        assertThat(metric.group(2))
                .as("метрика предпросмотра в закреплённой панели снова столбиком: каждая метрика "
                        + "займёт две строки, панель удвоится по высоте, и полоса, которую она "
                        + "съедает у конструктора, накроет контролы")
                .containsPattern("flex-direction:\\s*row");
    }

    private static String read(Path path) {
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("не прочитать " + path, e);
        }
    }
}
