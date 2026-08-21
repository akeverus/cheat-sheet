package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Заголовки едут по одной текучей шкале, и на любой ширине страница громче секции.
 *
 * <h2>Найденный дефект, а не гипотеза</h2>
 * Заголовок шапки с iter-62 — clamp(22→36px к ~1000px). Секционные заголовки
 * остались статичными ступенями: замер 390px дал h1 страницы 22px против h2 секций
 * 28px (13 шт.) и 36px (5 шт.) на /stats, 10×28px на /topics, 36px на /settings,
 * 48px у заголовка страницы ошибки — на любом телефоне секции КРИЧАТ громче
 * страницы. Тот же класс дефекта iter-62 закрыл для h1 и не закрыл для остальных.
 *
 * <h2>Что закреплено</h2>
 * <ol>
 *   <li>пять токенов текучей шкалы существуют, их полы выстроены лестницей
 *       (page 22 > section 20 > card 19 > minor 18 = телу), потолки — прежние
 *       статичные ступени: десктоп ≥1280 после правки пиксельно тот же
 *       (замер 9 ширин × 2 движка);</li>
 *   <li>reset h1–h4 сидят на шкале, а не на статичных ступенях;</li>
 *   <li>компонентные заголовки тоже — статичный кегль в заголовочном правиле
 *       вернул бы инверсию точечно;</li>
 *   <li>числа (KPI, счёт, таймер) на шкалу НЕ переводятся — крупное число на
 *       телефоне намеренное.</li>
 * </ol>
 */
class HeadingScaleContractTest {

    /** Полы лестницы в rem: сверху вниз убывают, minor равен кеглю тела (1.125rem). */
    private static final double PAGE_FLOOR = 1.375;
    private static final double SECTION_FLOOR = 1.25;
    private static final double CARD_FLOOR = 1.1875;
    private static final double MINOR_FLOOR = 1.125;

    private String headingToken(String name) throws IOException {
        String css = CssRules.stripComments(CssRules.read("static/css/tokens.css"));
        Matcher m = Pattern.compile("--font-size-heading-" + name + ":\\s*([^;]+);").matcher(css);
        assertThat(m.find())
                .as("токен --font-size-heading-%s пропал из tokens.css", name)
                .isTrue();
        return m.group(1).trim();
    }

    /** Пол клампа в rem; токен-полы разворачиваются по статичной шкале. */
    private double floorRem(String clampValue) {
        Matcher m = Pattern.compile("clamp\\(\\s*([^,]+),").matcher(clampValue);
        assertThat(m.find()).as("не clamp: %s — шкала перестала быть текучей", clampValue).isTrue();
        String floor = m.group(1).trim();
        Map<String, Double> statics = Map.of(
                "var(--font-size-md)", 1.125, "var(--font-size-lg)", 1.375,
                "var(--font-size-xl)", 1.75, "var(--font-size-2xl)", 2.25);
        if (statics.containsKey(floor)) return statics.get(floor);
        Matcher rem = Pattern.compile("([\\d.]+)rem").matcher(floor);
        assertThat(rem.find()).as("пол не в rem: %s", floor).isTrue();
        return Double.parseDouble(rem.group(1));
    }

    @Test
    void theFloorsDescendFromPageToMinor() throws IOException {
        assertThat(floorRem(headingToken("page"))).isEqualTo(PAGE_FLOOR);
        assertThat(floorRem(headingToken("section")))
                .as("пол секции обязан быть НИЖЕ пола страницы — иначе на телефоне секция "
                        + "снова громче страницы")
                .isEqualTo(SECTION_FLOOR).isLessThan(PAGE_FLOOR);
        assertThat(floorRem(headingToken("card")))
                .isEqualTo(CARD_FLOOR).isLessThan(SECTION_FLOOR);
        assertThat(floorRem(headingToken("minor")))
                .isEqualTo(MINOR_FLOOR).isLessThan(CARD_FLOOR);
        assertThat(floorRem(headingToken("display")))
                .as("display-пол (заголовок вне шапки: ошибка, сырой h1) стоит над прочими")
                .isEqualTo(1.75);
    }

    @Test
    void theCeilingsKeepTheDesktopExactlyAsItWas() throws IOException {
        // Потолки = прежние статичные ступени: десктоп ≥1280 пиксельно тот же.
        assertThat(headingToken("page")).endsWith("var(--font-size-2xl))");
        assertThat(headingToken("section")).endsWith("var(--font-size-2xl))");
        assertThat(headingToken("card")).endsWith("var(--font-size-xl))");
        assertThat(headingToken("display")).endsWith("var(--font-size-3xl))");
        assertThat(headingToken("minor")).endsWith("var(--font-size-lg))");
    }

    @Test
    void theResetHeadingsSitOnTheFluidScale() throws IOException {
        List<CssRules.Rule> rules = CssRules.parse("static/css/reset.css");
        Map<String, String> expected = Map.of(
                "html[data-design] h1", "display",
                "html[data-design] h2", "section",
                "html[data-design] h3", "card",
                "html[data-design] h4", "minor");
        expected.forEach((selector, token) -> {
            String size = rules.stream()
                    .filter(r -> r.hasSelector(selector))
                    .map(r -> r.value("font-size"))
                    .filter(v -> v != null)
                    .reduce((a, b) -> b).orElse(null);
            assertThat(size)
                    .as("%s: статичная ступень здесь возвращает инверсию на телефоне", selector)
                    .isEqualTo("var(--font-size-heading-" + token + ")");
        });
    }

    /**
     * Компонентные заголовки — на шкале. Список — итог инвентаризации всех
     * font-size с xl/2xl/3xl (iter-161): это ЗАГОЛОВКИ; числа (metric-value,
     * score, таймер, счётчики) в список не входят и остаются статичными.
     */
    @Test
    void theComponentHeadingsSitOnTheFluidScaleToo() throws IOException {
        record Site(String file, String selectorPart, String token) {}
        List<Site> sites = List.of(
                new Site("pages-stats.css", ".stats-page .card h2", "card"),
                new Site("pages-stats.css", ".stats-panel-title", "section"),
                new Site("pages-settings.css", ".settings-panel-title", "section"),
                new Site("pages-topics.css", ".catalog-group-title", "card"),
                new Site("pages-result.css", ".question-header h1", "section"),
                new Site("components-empty-state.css", ".empty-state-title", "card"),
                new Site("components-modals.css", ".kbd-help-modal h2", "card"),
                new Site("components-modals.css", ".prompt-modal-title", "card"),
                new Site("components-progression.css", ".rank-title", "card"),
                new Site("pages-error.css", ".error-title", "display"));
        for (Site site : sites) {
            List<CssRules.Rule> rules = CssRules.parse("static/css/" + site.file());
            // Базовое правило (вне @media): медиа-ветки с ДОП. сжатием легальны —
            // например, низкий мобильный экран (max-height:700px) у страницы ошибки
            // прижимает заголовок ещё сильнее, и это осознанное правило, а не пара
            // «база+медиа», которую шкала заменяет.
            String size = rules.stream()
                    .filter(r -> r.media() == null)
                    .filter(r -> r.selectorList().stream().anyMatch(s -> s.contains(site.selectorPart())))
                    .map(r -> r.value("font-size"))
                    .filter(v -> v != null)
                    .reduce((a, b) -> b).orElse(null);
            assertThat(size)
                    .as("%s %s: заголовок сполз со шкалы — точечная инверсия на телефоне",
                            site.file(), site.selectorPart())
                    .isEqualTo("var(--font-size-heading-" + site.token() + ")");
        }
    }

    /** Кламп шапки живёт в токене, от которого выстроены полы остальных. */
    @Test
    void thePageHeadTitleUsesTheSharedToken() throws IOException {
        List<CssRules.Rule> rules = CssRules.parse("static/css/shell.css");
        String size = rules.stream()
                .filter(r -> r.hasSelector("html[data-design] .page-head-title h1"))
                .map(r -> r.value("font-size"))
                .filter(v -> v != null)
                .reduce((a, b) -> b).orElse(null);
        assertThat(size)
                .as("кегль шапки обязан жить в --font-size-heading-page: второй источник "
                        + "правды разъезжается с лестницей полов молча")
                .isEqualTo("var(--font-size-heading-page)");
    }
}
