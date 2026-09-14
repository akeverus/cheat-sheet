package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Материал вопроса не выдавливает варианты за нижний край экрана.
 *
 * <h2>Найденный дефект, а не гипотеза</h2>
 * У листинга кода окно было ограничено на узких ширинах, у схемы — нет: `.diagram-viewport`
 * держал `min(70vh, 640px)` на всех ширинах. Замер (Chromium и WebKit, экзамен, вопрос со
 * схемой):
 *
 * <pre>
 *   вьюпорт     окно схемы   верх вариантов   сгиб
 *   390×844        517           @936          844   ← ни одного варианта в первом экране
 *   1024×768       517           @986          768   ← то же самое
 * </pre>
 *
 * <h2>Замер после</h2>
 * Кап — одно число на оба вида материала, `min(38vh, 320px)`. По всей матрице (8 ширин × 2
 * движка) верх вариантов в первом экране; отдельный поиск самого высокого материала:
 * 390×844 схема 517→320 варианты @740, WebKit 390 листинг 827→320 @793, 768×1024 @819/@829,
 * WebKit 1024×768 листинг 827→292 @750. Остаток честно: Chromium 1024×768 на листинге 586→292
 * ставит верх вариантов на 770 при сгибе 768 — 2px.
 *
 * <h2>Что закреплено</h2>
 * <ol>
 *   <li>кап один на оба вида материала — иначе новый вид снова окажется без ограничения;</li>
 *   <li>кап стоит ПОСЛЕ обоих базовых правил: селекторы одного веса, медиазапрос веса не
 *       добавляет, и правило выше по файлу просто не действует (первая сборка этой итерации
 *       положила его на 500 строк выше базы схемы — вычисленный `max-height` остался 590.8px);</li>
 *   <li>кап действительно меньше базы, которую перекрывает;</li>
 *   <li>порог капа и порог двухколоночного рабочего места — одно число.</li>
 * </ol>
 */
class MaterialCapContractTest {

    private static final Pattern COMMENT = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL);
    /**
     * Доля окна и потолок в пикселях. Ищутся ВНУТРИ объявления, а не с его начала: с
     * iter-147 у базовых правил два слагаемых — прежний кап и «высота окна минус
     * преамбула», — и сравнивать узкий кап надо с первым из них.
     */
    private static final Pattern MIN_HEIGHT = Pattern.compile("min\\(([\\d.]+)vh,\\s*(\\d+)px\\)");
    private static final Pattern MAX_WIDTH = Pattern.compile("max-width:\\s*(\\d+)px");

    /** Правило таблицы стилей вместе с медиаусловием, в котором оно лежит. */
    private record Rule(int start, String media, String selectors, String body) {
    }

    private List<Rule> focusRules() throws IOException {
        String css = COMMENT.matcher(new String(
                new ClassPathResource("static/css/pages-focus.css").getInputStream().readAllBytes(),
                StandardCharsets.UTF_8)).replaceAll("");
        List<Rule> rules = new ArrayList<>();
        collect(css, 0, css.length(), null, rules);
        return rules;
    }

    private void collect(String css, int from, int to, String media, List<Rule> out) {
        int i = from;
        while (i < to) {
            int brace = css.indexOf('{', i);
            if (brace < 0 || brace >= to) return;
            int end = closing(css, brace);
            String prelude = css.substring(i, brace).trim();
            if (prelude.startsWith("@")) {
                // Внутрь `@layer`/`@supports` спускаемся с тем же медиаусловием: файл целиком
                // лежит в слое, и без спуска правил не находится вовсе.
                collect(css, brace + 1, end, prelude.startsWith("@media") ? prelude : media, out);
            } else {
                out.add(new Rule(brace, media, prelude, css.substring(brace + 1, end)));
            }
            i = end + 1;
        }
    }

    private int closing(String css, int open) {
        int depth = 0;
        for (int i = open; i < css.length(); i++) {
            if (css.charAt(i) == '{') depth++;
            if (css.charAt(i) == '}' && --depth == 0) return i;
        }
        throw new AssertionError("незакрытый блок в pages-focus.css");
    }

    /**
     * Правила, задающие высоту окна материала. Полноэкранный режим и составные контексты
     * (`:has(...)`) и no-JS — отдельные состояния со своей высотой.
     * Без JS листинг раскрыт целиком, поскольку toolbar и динамический tab-stop недоступны.
     */
    private List<Rule> heightRules(List<Rule> rules, String carrier) {
        return rules.stream()
                .filter(r -> r.body().contains("max-height"))
                .filter(r -> splitSelectors(r.selectors()).stream()
                        .anyMatch(s -> s.endsWith(carrier) && !s.contains(":fullscreen")
                                && !s.contains(":has(") && !s.contains(":not([data-js])")))
                .toList();
    }

    /**
     * Список селекторов делится запятыми ВЕРХНЕГО уровня: у `:not(:has(a, b, c))` запятые
     * внутри скобок, и наивный `split(",")` разрывал селектор на куски, один из которых
     * выглядел как отдельное простое правило без `:has(`.
     */
    private List<String> splitSelectors(String selectorList) {
        List<String> parts = new ArrayList<>();
        int depth = 0;
        StringBuilder current = new StringBuilder();
        for (char c : selectorList.toCharArray()) {
            if (c == '(') depth++;
            if (c == ')') depth--;
            if (c == ',' && depth == 0) {
                parts.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        parts.add(current.toString().trim());
        return parts;
    }

    private Rule last(List<Rule> rules, String carrier) {
        List<Rule> found = heightRules(rules, carrier);
        assertThat(found).as("правил высоты для %s нет вовсе", carrier).isNotEmpty();
        return found.get(found.size() - 1);
    }

    private Rule base(List<Rule> rules, String carrier) {
        List<Rule> found = heightRules(rules, carrier).stream().filter(r -> r.media() == null).toList();
        assertThat(found).as("базового правила высоты для %s нет", carrier).isNotEmpty();
        return found.get(found.size() - 1);
    }

    private double[] limits(Rule rule) {
        Matcher m = MIN_HEIGHT.matcher(rule.body());
        assertThat(m.find())
                .as("в высоте нет слагаемого `min(<доля окна>, <потолок px>)`: %s", rule.body())
                .isTrue();
        return new double[] { Double.parseDouble(m.group(1)), Double.parseDouble(m.group(2)) };
    }

    /**
     * Высокий экран: потолок материала обязан считаться от окна, а не константой.
     *
     * <p>Замер (Chromium, 2560×1440, 28 подряд вопросов): материал у 54%, из них 5 из 15
     * упирались в абсолютный потолок — срезано медиана 133px, максимум 411px, — а под
     * композицией в это же время пустовало 236px по медиане и 383px в худшем случае.
     * После правки на той же выборке в потолок не упёрся НИ ОДИН материал (0 из 16), а
     * синтетически высокая схема (содержимое 2647px) получила окно 560/640/720/960 на
     * 1280×800 / 1512×982 / 1920×1200 / 2560×1440 — против 520/640/640/640 до.</p>
     */
    @Test
    void theCeilingGrowsWithTheWindowOnTallScreens() throws IOException {
        List<Rule> rules = focusRules();
        for (String carrier : List.of(".code-viewport", ".diagram-viewport")) {
            String body = base(rules, carrier).body();
            assertThat(body)
                    .as("потолок %s снова константа: на 1440-пиксельном окне он режет материал "
                            + "там, где под композицией пустуют сотни пикселей", carrier)
                    .contains("max(")
                    .containsPattern("100dvh\\s*-");
            assertThat(body)
                    .as("прежний кап пропал из формулы: `max()` обязан оставлять низкому окну "
                            + "то число, с которым оно уже работало")
                    .containsPattern("min\\([\\d.]+vh,\\s*\\d+px\\)");
        }
    }

    @Test
    void oneCapCoversBothKindsOfMaterial() throws IOException {
        List<Rule> rules = focusRules();
        Rule code = last(rules, ".code-viewport");
        Rule diagram = last(rules, ".diagram-viewport");
        assertThat(code.start())
                .as("последнее слово о высоте листинга и о высоте схемы говорят РАЗНЫЕ правила: "
                        + "вид материала без капа выдавливает варианты за сгиб (схема жила так — "
                        + "варианты на 936 при сгибе 844)")
                .isEqualTo(diagram.start());
        assertThat(code.media())
                .as("кап перестал быть узкоэкранным — на широком экране материал незачем резать")
                .isNotNull()
                .contains("max-width");
    }

    @Test
    void theCapStandsAfterTheBaseRulesItOverrides() throws IOException {
        List<Rule> rules = focusRules();
        Rule cap = last(rules, ".diagram-viewport");
        for (String carrier : List.of(".code-viewport", ".diagram-viewport")) {
            assertThat(base(rules, carrier).start())
                    .as("базовое правило %s стоит ПОСЛЕ капа: вес селекторов одинаковый, "
                            + "медиазапрос веса не добавляет — кап просто не применяется", carrier)
                    .isLessThan(cap.start());
        }
    }

    @Test
    void theCapIsActuallySmallerThanTheBase() throws IOException {
        List<Rule> rules = focusRules();
        double[] cap = limits(last(rules, ".diagram-viewport"));
        for (String carrier : List.of(".code-viewport", ".diagram-viewport")) {
            double[] base = limits(base(rules, carrier));
            assertThat(cap[0]).as("доля экрана в капе не меньше базовой у %s — резать нечего", carrier)
                    .isLessThan(base[0]);
            assertThat(cap[1]).as("потолок в пикселях у капа не меньше базового у %s", carrier)
                    .isLessThan(base[1]);
        }
    }

    /** Порог капа и порог двухколоночного рабочего места — одно событие «есть ли ширина». */
    @Test
    void theCapThresholdMeetsTheWorkspaceThreshold() throws IOException {
        List<Rule> rules = focusRules();
        Rule cap = last(rules, ".diagram-viewport");
        assertThat(cap.media())
                .as("последнее правило высоты схемы вообще не под медиазапросом — порога нет")
                .isNotNull();
        Matcher m = MAX_WIDTH.matcher(cap.media());
        assertThat(m.find()).as("в условии капа нет `max-width`").isTrue();
        int capEdge = Integer.parseInt(m.group(1));

        List<Integer> workspaceEdges = rules.stream()
                .map(Rule::media)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .map(cond -> Pattern.compile("min-width:\\s*(\\d+)px").matcher(cond))
                .filter(Matcher::find)
                .map(found -> Integer.parseInt(found.group(1)))
                .distinct()
                .toList();
        assertThat(workspaceEdges)
                .as("между порогом капа (%d) и ближайшим порогом раскладки появилась полоса "
                        + "ширин, где нет ни капа, ни второй колонки", capEdge)
                .contains(capEdge + 1);
    }

    /**
     * В сфокусированной карточке материал и действие стоят в ОДНОЙ колонке, поэтому у
     * неё свой, более низкий потолок — и он обязан покрывать оба вида материала.
     *
     * <p>Замер до правки (синтетическая высокая схема, содержимое 2647px): 1512×982 —
     * окно 640, «Показать ответ» на 1209 при сгибе 982; 2560×1440 — окно 960, действие
     * на 1548 при сгибе 1440. После: окно 373 и 420, действие на 942 и 1027, то есть в
     * первом экране в обоих движках.</p>
     */
    @Test
    void theFocusedCardCapsBothKindsOfMaterial() throws IOException {
        List<Rule> rules = focusRules();
        List<Rule> cardCaps = rules.stream()
                .filter(r -> r.selectors().contains(".flashcard-phase"))
                .filter(r -> r.body().contains("max-height"))
                .toList();
        assertThat(cardCaps).as("у карточки нет собственного потолка материала").isNotEmpty();
        Rule cap = cardCaps.get(cardCaps.size() - 1);
        for (String carrier : List.of(".code-viewport", ".diagram-viewport")) {
            String wanted = carrier;
            assertThat(splitSelectors(cap.selectors()).stream().anyMatch(sel -> sel.endsWith(wanted)))
                    .as("потолок карточки снова не покрывает %s: там материал выдавливает "
                            + "«Показать ответ» за сгиб (замер: действие на 1548 при сгибе 1440)", carrier)
                    .isTrue();
        }
        assertThat(limits(cap)[1])
                .as("потолок карточки перестал быть НИЖЕ общего — тогда он ничего не ограничивает")
                .isLessThan(limits(base(rules, ".diagram-viewport"))[1]);
    }

    /**
     * Прокручиваемая область листинга — ОДНА, и это сам `.code-viewport` (обе оси).
     *
     * <p>До A11Y-01 скроллеров было два вложенных: окно (overflow:auto + max-height,
     * фокусируемое из code-toolbar.js) крутило вертикаль, а pre внутри со своим
     * overflow-x:auto съедал горизонтальное переполнение — окно его «не видело»
     * (замер 390: pre 566/324, окно scrollWidth==clientWidth). Клавиатурный фокус
     * стоял на окне, стрелки вправо двигали НИЧТО — скрытые 242px кода были
     * недостижимы (WCAG 2.1.1). Схема соседней диаграммы всегда была одноконной —
     * листинг приведён к ней же.</p>
     *
     * <p>width:max-content — фон и рамку несёт pre (components-code-highlight.css:
     * «фон принадлежит pre»), и панель обязана застилать всю прокручиваемую ширину;
     * min-width:100% возвращает полную полосу короткому сниппету. В режиме переноса
     * (.is-code-wrap) правило молчит: там строки ломаются и горизонтали нет,
     * а max-content при pre-wrap мерил бы неразорванную строку и ломал перенос.</p>
     */
    @Test
    void theViewportIsTheOnlyScrollerOfTheListing() throws IOException {
        List<Rule> rules = focusRules();
        Rule inner = rules.stream()
                .filter(r -> r.selectors().contains(":not(.is-code-wrap) .code-viewport pre"))
                .filter(r -> r.media() == null)
                .reduce((a, b) -> b)
                .orElseThrow(() -> new AssertionError(
                        "нет экранного правила «pre внутри окна листинга не скроллер» — "
                                + "горизонтальное переполнение снова съедает вложенный "
                                + "нефокусируемый pre"));
        assertThat(inner.body())
                .as("pre внутри окна обязан отдать горизонтальное переполнение окну")
                .contains("overflow-x: visible")
                .contains("width: max-content")
                .contains("min-width: 100%")
                .contains("max-width: none");

        Rule print = rules.stream()
                .filter(r -> r.selectors().contains(":not(.is-code-wrap) .code-viewport pre"))
                .filter(r -> r.media() != null && r.media().contains("print"))
                .reduce((a, b) -> b)
                .orElseThrow(() -> new AssertionError(
                        "нет печатного отката: у бумаги прокрутки нет, и width:max-content "
                                + "уводит панель листинга за поле листа"));
        assertThat(print.body())
                .as("на бумаге строка ценнее переносом, чем обрезком")
                .contains("white-space: pre-wrap")
                .contains("width: auto");

        String js = new String(new ClassPathResource("static/js/ui/code-toolbar.js")
                .getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        assertThat(js)
                .as("фокусируемость окна оценивает только вертикаль: горизонтальный скролл "
                        + "короткого по высоте листинга останется без tab-стопа")
                .contains("viewport.scrollWidth > viewport.clientWidth");
    }
}
