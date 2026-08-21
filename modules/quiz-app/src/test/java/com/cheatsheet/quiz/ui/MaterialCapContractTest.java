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
    private static final Pattern MIN_HEIGHT = Pattern.compile("max-height:\\s*min\\(([\\d.]+)vh,\\s*(\\d+)px\\)");
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
     * (`:has(...)`) — это отдельные состояния со своей высотой, они здесь ни при чём.
     */
    private List<Rule> heightRules(List<Rule> rules, String carrier) {
        return rules.stream()
                .filter(r -> r.body().contains("max-height"))
                .filter(r -> splitSelectors(r.selectors()).stream()
                        .anyMatch(s -> s.endsWith(carrier) && !s.contains(":fullscreen") && !s.contains(":has(")))
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
        assertThat(m.find()).as("высота задана не через `min(<vh>, <px>)`: %s", rule.body()).isTrue();
        return new double[] { Double.parseDouble(m.group(1)), Double.parseDouble(m.group(2)) };
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
}
