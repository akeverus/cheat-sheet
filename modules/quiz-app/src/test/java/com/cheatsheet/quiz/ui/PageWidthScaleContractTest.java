package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Ширина рабочей полосы задаётся ОДНОЙ шкалой, и прозаическая мера принадлежит прозе.
 *
 * <h2>Найденные дефекты, а не гипотезы (iter-64, замеры в WebKit@2 по матрице §10)</h2>
 *
 * <ol>
 *   <li><b>Пять токенов ширины не задавали ничего.</b> {@code --max-width-content} и
 *       {@code --max-width-wide} не использовал ни один селектор. {@code --content-max}
 *       кормил правило {@code .content}, а класса {@code content} нет ни в одном шаблоне и
 *       ни в одном JS-построителе разметки (в разметке только составные {@code content-box},
 *       {@code content-tab}, {@code content-toolbar}, {@code markdown-content}) — правило не
 *       применялось никогда. Правил у мёртвого класса оказалось ДВА: десктопное и второе,
 *       {@code padding: 14px} внутри медиазапроса ≤768px; ручное чтение файла нашло первое и
 *       пропустило второе — его вытащил уже этот гейт.
 *       {@code --max-width-reading} после iter-63 остался только в комментарии.
 *       {@code --max-width-data} был литеральным {@code none} — индирекция вокруг «ничего».
 *       Комментарий над блоком при этом утверждал, что «новый шелл использует
 *       {@code --content-max}»: девять токенов и неверная легенда к ним.</li>
 *   <li><b>Кап страницы был заморожен, а поле — нет.</b> {@code --max-width-page: 80rem}
 *       (1280px) против {@code --page-gutter: clamp(2rem, 4vw, 5rem)}, растущего с {@code vw}.
 *       Полоса контента не расширялась вместе с экраном, а СУЖАЛАСЬ: на {@code /stats} 1159px
 *       при вьюпорте 1512 → 1146 (1680) → 1142 (1728) → 1126 (1920) → 1120 (2560). Мёртвая
 *       ширина по каждому борту при этом росла 6 → 90 → 114 → 210 → 530px (§6).</li>
 *   <li><b>Прозаическая мера висела на теге списка.</b> Базовый reset давал любому
 *       {@code ul}/{@code ol} {@code max-width: var(--measure)} (72ch ≈ 818px). Аудит разметки:
 *       все восемь рукописных {@code <ul>} в шаблонах — структура (сетка разделов, список
 *       закладок, карточки ошибок, легенда, прогноз), прозаические списки приходят только из
 *       markdown внутрь {@code .markdown-content}. То есть правило промахивалось мимо 100%
 *       случаев, ради которых написано, и било ровно по тем, по которым не должно: замер @2560
 *       давал {@code .topic-catalog-grid} 818px при родителе 1120px и {@code .bookmark-list}
 *       818px. Страницы чинили это персональными {@code max-width: none} — к iter-63 таких
 *       исключений накопилось три.</li>
 * </ol>
 *
 * <h2>Почему это гейт, а не разовая правка</h2>
 * Все три дефекта — один класс: ширину объявляет одно место, а расходуется она в другом, и
 * расхождение не видно ни в коде (каждое правило по отдельности осмысленно), ни на скриншоте
 * одной ширины. Регрессия сюда возвращается тихо — новым «временным» токеном, новым
 * персональным капом страницы или новым {@code max-width: none} вместо системного решения.
 * Тесты ниже проверяют структуру решения: мёртвые токены не возвращаются, страница имеет ровно
 * один кап и он не заморожен, мера чтения стоит на прозо-контейнере, а не на теге.
 *
 * <p>Меры КОМПОЗИЦИЙ под гейт не попадают намеренно: это ширины компонентов, считающиеся из их
 * собственных колонок (FRD-003), а не второй кап страницы. Отличать одно от другого приходится
 * замером, и к iter-69 из четырёх токенов вида {@code --max-width-<страница>} этой проверки не
 * прошёл ни один:
 * <ul>
 *   <li>{@code --max-width-settings} — снят в iter-65: держал полосу {@code /settings} ровно на
 *       1024px при ширинах окна 1440–2560;</li>
 *   <li>{@code --max-width-focus} — снят в iter-68: не был ни капом страницы, ни считаемой мерой,
 *       а ДУБЛИРОВАЛ меру экрана вопроса значением (обе 60rem), не будучи с ней связан, и при
 *       выключенном {@code :has()} давал полосе две меры сразу;</li>
 *   <li>{@code --max-width-result} — снят в iter-69: стоял на {@code .result-page .ed-page}, то
 *       есть на ПОЛОСЕ, и морозил её на 1152px вплоть до 2560 (594px мёртвой ширины по борту),
 *       причём рабочая колонка при этом сужалась с ростом окна — растущий жёлоб вычитался
 *       из неё.</li>
 * </ul>
 * Настоящей мерой композиции остаётся {@code --max-width-options}: он ограничивает список
 * вариантов, и меры рабочих колонок обоих экранов сценария считаются ИЗ него. Сами меры держат
 * {@link FocusBandMeasureContractTest} и {@link ResultBandMeasureContractTest}.
 */
class PageWidthScaleContractTest {

    private static final Path CSS_DIR = Path.of("src/main/resources/static/css");
    private static final Path TOKENS = CSS_DIR.resolve("tokens.css");
    private static final Path RESET = CSS_DIR.resolve("reset.css");
    private static final Path PROSE = CSS_DIR.resolve("components-prose.css");
    private static final Path LAYOUT = CSS_DIR.resolve("reset-layout.css");

    /** Токены, каждый из которых при удалении не изменил ни одного computed-стиля. */
    private static final List<String> REMOVED_WIDTH_TOKENS = List.of(
            "--max-width-content", "--max-width-wide", "--max-width-reading",
            "--max-width-data", "--content-max");

    /**
     * Объявления без комментариев.
     *
     * <p>Каждый снятый здесь токен оставил после себя комментарий с причиной и замером — то есть
     * с дословным своим именем. Без вырезания комментариев гейт запрещал бы не возврат токена, а
     * рассказ о том, почему его убрали.
     */
    private static String declarationsOnly(String css) {
        return css.replaceAll("(?s)/\\*.*?\\*/", "");
    }

    private static String read(Path path) {
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("не прочитать " + path, e);
        }
    }

    private static Stream<Path> allCss() {
        try (Stream<Path> files = Files.list(CSS_DIR)) {
            return files.filter(p -> p.getFileName().toString().endsWith(".css")).toList().stream();
        } catch (IOException e) {
            throw new UncheckedIOException("не прочитать " + CSS_DIR, e);
        }
    }

    @Test
    void deadWidthTokensDoNotComeBack() {
        List<Path> css = allCss().toList();
        assertThat(css).as("в %s не нашлось ни одного css-файла — гейт смотрит не туда", CSS_DIR)
                .isNotEmpty();

        for (String token : REMOVED_WIDTH_TOKENS) {
            for (Path file : css) {
                assertThat(declarationsOnly(read(file)))
                        .as("токен `%s` вернулся в %s. Он был удалён как не задающий ничего: "
                                + "у него не было ни одного потребителя (либо потребителем было "
                                + "мёртвое правило `.content`, либо значением — литеральный `none`). "
                                + "Если ширина понадобилась снова — она берётся из единой шкалы "
                                + "--max-width-page, а не из нового персонального токена (§24)",
                                token, file.getFileName())
                        .doesNotContain(token);
            }
        }
    }

    @Test
    void deadContentColumnRuleDoesNotComeBack() {
        assertThat(declarationsOnly(read(CSS_DIR.resolve("shell.css"))))
                .as("правило `.content` вернулось в shell.css. Класса `content` нет ни в одном "
                        + "шаблоне и ни в одном JS-построителе разметки — правило не применялось "
                        + "ни разу. Колонку контента ведёт `.ed-page` (reset-layout.css)")
                .doesNotContain("html[data-design] .content {");
    }

    @Test
    void pageBandHasExactlyOneCapAndItIsNotFrozen() {
        String tokens = declarationsOnly(read(TOKENS));

        Matcher declaration = Pattern.compile("--max-width-page:\\s*([^;]+);").matcher(tokens);
        assertThat(declaration.find())
                .as("в tokens.css нет объявления --max-width-page — единственная шкала ширины "
                        + "страницы исчезла или переименована, контракт надо переписать под факт")
                .isTrue();

        String value = declaration.group(1).trim();
        assertThat(declaration.find())
                .as("--max-width-page объявлен дважды (второе значение: см. tokens.css) — это и "
                        + "есть две шкалы ширины, которые запрещено держать одновременно (§24)")
                .isFalse();

        assertThat(value)
                .as("кап страницы снова заморожен литералом `%s`. Пока он был 80rem, а "
                        + "--page-gutter рос с vw, полоса контента СУЖАЛАСЬ с ростом окна: "
                        + "1159px@1512 → 1120px@2560, мёртвая ширина по борту 6 → 530px. "
                        + "Значение обязано начинаться с min(100%%, …), чтобы до потолка полоса "
                        + "шла вровень с оболочкой", value)
                .startsWith("min(100%,");
    }

    @Test
    void pageContainerTakesWidthFromThatSingleScale() {
        assertThat(declarationsOnly(read(LAYOUT)))
                .as("`.ed-page` перестала брать --max-width-page. Это единственный контейнер "
                        + "рабочей полосы; если он заведёт собственный литерал, шкала снова "
                        + "раздвоится")
                .contains("max-width: var(--max-width-page)");
    }

    @Test
    void proseMeasureLivesOnTheProseContainerNotOnTheListTag() {
        String reset = declarationsOnly(read(RESET));

        Matcher listRule = Pattern.compile(
                "html\\[data-design]\\s+ul,\\s*html\\[data-design]\\s+ol\\s*\\{([^}]*)}").matcher(reset);
        assertThat(listRule.find())
                .as("в reset.css нет базового правила для `ul, ol` — селектор переименовали, "
                        + "контракт надо переписать под факт")
                .isTrue();
        assertThat(listRule.group(1))
                .as("прозаическая мера вернулась на тег списка. В разметке приложения все восемь "
                        + "рукописных <ul> — структура (сетки, карточки, легенды), проза приходит "
                        + "только из markdown. Замер @2560 с этим правилом: .topic-catalog-grid "
                        + "818px при родителе 1120px, .bookmark-list 818px одной колонкой")
                .doesNotContain("--measure");

        assertThat(declarationsOnly(read(PROSE)))
                .as("мера чтения пропала с прозаических списков: длинный пункт markdown-списка "
                        + "поедет на всю ширину карточки и разойдётся со строкой соседнего абзаца "
                        + "(`p` меру сохранил)")
                .contains("html[data-design] .markdown-content ul")
                .contains("max-width: var(--measure)");
    }

    @Test
    void paragraphKeepsItsMeasure() {
        assertThat(declarationsOnly(read(RESET)))
                .as("мера снята и с `p`. Снимался ТОЛЬКО тег списка: `p` прозой быть не "
                        + "перестаёт, и без меры строка абзаца на 2560 растянулась бы на всю "
                        + "полосу — это уже нарушение §12, а не его исправление")
                .containsPattern("html\\[data-design]\\s+p\\s*\\{[^}]*max-width:\\s*var\\(--measure\\)");
    }

    @Test
    void structuralListsDoNotNeedPerPageExceptions() {
        for (Path file : allCss().toList()) {
            String name = file.getFileName().toString();
            if (!name.startsWith("pages-")) {
                continue;
            }
            Matcher exception = Pattern.compile(
                    "\\.(summary-mistakes|bookmark-list|topic-catalog-grid|search-results|"
                            + "difficulty-legend|forecast-list|accuracy-trend-bars|wrong-question-list)"
                            + "\\s*\\{([^}]*)}").matcher(declarationsOnly(read(file)));
            while (exception.find()) {
                assertThat(exception.group(2))
                        .as("в %s структурный список `%s` снова снимает прозаическую меру через "
                                + "`max-width: none`. Снимать больше нечего: reset её не ставит "
                                + "(iter-64). Если мера появилась — она вернулась на тег, и чинить "
                                + "надо там, а не персональным исключением на каждой странице (§24)",
                                name, exception.group(1))
                        .doesNotContain("max-width: none");
            }
        }
    }
}
