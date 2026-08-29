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
 * Вкладка в приложении одна. Поведение уже было одним — весь трёхкратный разнобой жил в CSS.
 *
 * <h2>Дефект, а не гипотеза (iter-79, замеры в WebKit@2, 1440×900)</h2>
 * Три полосы вкладок — «Пример кода / Схема» на экране вопроса, «Сессия / Оформление / Данные»
 * в настройках и «Темы / Динамика / Ошибки» в аналитике — управляются ОДНИМ модулем
 * {@code ui/tablist.js}, который находит их по {@code [role="tab"]}. А выглядели они как три
 * разных компонента, потому что коробка была описана трижды: в {@code components-content-tabs.css},
 * в {@code pages-settings.css} и в {@code pages-stats.css}.
 *
 * <p>Замер расхождений:
 * <ul>
 *   <li>высота касания: 40px у вкладок материалов против 44px у двух остальных — ниже
 *       собственной планки приложения (WCAG 2.5.5);</li>
 *   <li>поля: {@code 8px 16px} / {@code 12px 24px} / {@code 12px 18px}, причём последние 18px
 *       зашиты числом в слое краски, мимо шкалы отступов;</li>
 *   <li>фокус в трёх видах: свечение через {@code box-shadow} у двух полос и обводка со
 *       сдвигом ВНУТРЬ у третьей — при том что идиома приложения одна (FRD-039);</li>
 *   <li>кегль на 390px: 16px у настроек против 18px у аналитики — один и тот же контрол
 *       уезжал по типографике только потому, что медиазапрос был написан в одном page-файле
 *       из трёх.</li>
 * </ul>
 *
 * <h2>Почему спрайт вкладки снят</h2>
 * Выбранная вкладка аналитики одевалась 3-slice спрайтом {@code tab-l/tab-m/tab-r.png} через
 * {@code !important}. Пиксельный замер: отрисованная вкладка в тёмной и светлой теме совпадает
 * ПОБАЙТНО (те же 30.1% teal, 17.3% почти-чёрного, 14.3% мяты) — растр фиксирован и на смену
 * темы не реагирует вовсе. В светлой теме это даёт чёрные бруски и тёмный блок на почти белой
 * странице. Плюс два системных нарушения: спрайт 16px растягивается до 45px, то есть в 2.8125
 * раза — дробный коэффициент для pixel art (§14), — а тёмный ink подписи ложится на те самые
 * 17.3% почти-чёрных пикселей с контрастом 1.13 при требуемых 4.5 (WCAG 1.4.3).
 *
 * <h2>Что держат тесты</h2>
 * Не пиксели — их проверяет браузер на матрице вьюпортов. Тесты держат структуру решения:
 * скелет объявлен один раз и по тому же селектору, по которому работает поведение; страницы
 * его не копируют; слой краски вкладку не переодевает; фокус — одной идиомой; тач-цель не
 * опускается ниже 44px.
 */
class TabComponentContractTest {

    private static final Path RESOURCES = Path.of("src/main/resources");
    private static final Path CSS_DIR = RESOURCES.resolve("static/css");
    private static final Path COMPONENT_CSS = CSS_DIR.resolve("components-tabs.css");
    private static final Path SHELL_CSS = CSS_DIR.resolve("shell.css");
    private static final Path UTILITIES_CSS = CSS_DIR.resolve("utilities-overrides.css");
    private static final Path MANIFEST = RESOURCES.resolve("css-bundle-manifest.txt");
    private static final Path BEHAVIOUR_JS = RESOURCES.resolve("static/js/ui/tablist.js");

    /** Классы вкладок, чью коробку страница переобъявлять не имеет права. */
    private static final List<String> TAB_CLASSES = List.of(
            "content-tab", "settings-tab", "section-tab");

    /** Свойства коробки и краски. Раскладка (margin, width, flex, position) сюда не входит. */
    private static final List<String> BOX_PROPERTIES = List.of(
            "min-height", "padding", "font-size", "font-family", "font-weight",
            "border-radius", "box-shadow", "background", "background-image", "color");

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

    /** Пары «селектор → тело» одного файла, уже без комментариев. */
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

    /** Класс в селекторе — строго по границе слова: {@code tab} не должен ловить {@code tablist}. */
    private static boolean mentions(String selector, String cssClass) {
        return selector.matches("(?s).*\\b" + cssClass + "\\b.*");
    }

    private static boolean aboutTab(String selector) {
        return TAB_CLASSES.stream().anyMatch(c -> mentions(selector, c));
    }

    private static List<Path> allCss() {
        try (Stream<Path> files = Files.list(CSS_DIR)) {
            return files.filter(p -> p.getFileName().toString().endsWith(".css")).sorted().toList();
        } catch (IOException e) {
            throw new UncheckedIOException("не читается " + CSS_DIR, e);
        }
    }

    /** Файлы, которым запрещено переобъявлять коробку вкладки: страничные + утилиты + оболочка. */
    private static List<Path> nonComponentCssFiles() {
        List<Path> all = new ArrayList<>(allCss().stream()
                .filter(p -> p.getFileName().toString().startsWith("pages-"))
                .toList());
        all.add(UTILITIES_CSS);
        all.add(SHELL_CSS);
        return all;
    }

    @Test
    void tabSkeletonIsDeclaredOnceInTheComponentAndShippedEverywhere() {
        assertThat(Files.exists(COMPONENT_CSS))
                .as("компонент вкладки удалён — коробка немедленно вернётся тремя копиями по страницам")
                .isTrue();

        String css = declarationsOnly(read(COMPONENT_CSS));
        assertThat(css)
                .as("скелет вкладки: одна высота касания, одни поля, один кегль и один маркер "
                        + "выбранного на все три полосы — иначе каждая страница снова начнёт "
                        + "сочинять свою")
                .contains("min-height: 44px")
                .contains("padding: var(--space-3) var(--space-5)")
                .contains("border-radius: var(--border-radius-");

        assertThat(read(MANIFEST))
                .as("компонент подключён гейтом `*`: полосы вкладок есть на трёх разных страницах, "
                        + "и постраничный гейт означал бы, что четвёртая приедет без вида")
                .contains("* /css/components-tabs.css");
    }

    @Test
    void appearanceKeysOffTheSameSelectorAsBehaviour() {
        assertThat(read(BEHAVIOUR_JS))
                .as("поведение вкладок находит их по роли — это уже одна система")
                .contains("[role=\"tab\"]");
        assertThat(declarationsOnly(read(COMPONENT_CSS)))
                .as("вид обязан находить вкладку тем же селектором, что и поведение. Пока вид "
                        + "цепляется за три классовых имени, а поведение за роль, любая новая "
                        + "полоса вкладок заработает, но приедет голой")
                .contains("[role=\"tab\"]")
                .contains("[role=\"tablist\"]");
    }

    @Test
    void pageFilesDoNotRedeclareTheTabBox() {
        List<String> offenders = new ArrayList<>();
        for (Path css : nonComponentCssFiles()) {
            for (String[] rule : rules(css)) {
                if (!aboutTab(rule[0])) {
                    continue;
                }
                for (String property : BOX_PROPERTIES) {
                    if (rule[1].contains(property + ":")) {
                        offenders.add(css.getFileName() + ": " + rule[0].strip().replaceAll("\\s+", " ")
                                + " → " + property);
                    }
                }
            }
        }
        assertThat(offenders)
                .as("страница задаёт полосе вкладок место в раскладке, а не вид самой вкладки. "
                        + "Каждая такая копия расходится с остальными молча: в iter-79 так "
                        + "разъехались высота (40 против 44px), поля (три разных пары) и кегль "
                        + "на узком экране (16 против 18px)")
                .isEmpty();
    }

    @Test
    void paintLayerDoesNotDressTabsInAThemeBlindSprite() {
        List<String> offenders = new ArrayList<>();
        for (Path css : allCss()) {
            for (String[] rule : rules(css)) {
                if (!aboutTab(rule[0])) {
                    continue;
                }
                if (rule[1].contains("!important")) {
                    offenders.add(css.getFileName() + ": !important → " + rule[0].strip().replaceAll("\\s+", " "));
                }
                if (rule[1].contains("/img/pixel/tab-")) {
                    offenders.add(css.getFileName() + ": спрайт → " + rule[0].strip().replaceAll("\\s+", " "));
                }
            }
        }
        assertThat(offenders)
                .as("растровый спрайт вкладки фиксирован и темы не знает: замер показал побайтно "
                        + "одинаковую отрисовку в тёмной и светлой, то есть в светлой теме на "
                        + "почти белую страницу ложился тёмный блок. Плюс дробное увеличение "
                        + "16→45px (2.8125×) для pixel art и контраст подписи 1.13 на тёмных "
                        + "пикселях растра. `!important` тут же делал все остальные рецепты мёртвыми")
                .isEmpty();
    }

    @Test
    void tabFocusIsTheOneIdiomOfTheApplication() {
        List<String> offenders = new ArrayList<>();
        for (Path css : allCss()) {
            for (String[] rule : rules(css)) {
                if (!rule[0].contains(":focus-visible")) {
                    continue;
                }
                boolean tab = aboutTab(rule[0]) || rule[0].contains("[role=\"tab\"]");
                boolean glow = rule[1].contains("outline: none") && rule[1].contains("box-shadow");
                if (tab && glow) {
                    offenders.add(css.getFileName() + ": " + rule[0].strip().replaceAll("\\s+", " "));
                }
            }
        }
        assertThat(offenders)
                .as("свечение через box-shadow — вторая идиома фокуса (FRD-039 закрыл её для "
                        + "кнопочного семейства). У вкладок она держалась дольше всех и жила "
                        + "рядом с третьей — обводкой со сдвигом ВНУТРЬ")
                .isEmpty();

        assertThat(declarationsOnly(read(COMPONENT_CSS)))
                .as("идиома одна и та же во всём приложении: обводка 2px тема-зависимым токеном "
                        + "со сдвигом наружу")
                .contains("outline: 2px solid var(--color-focus-ring)")
                .contains("outline-offset: 2px");
    }

    @Test
    void everyTabStaysReachableByFinger() {
        List<String> offenders = new ArrayList<>();
        for (Path css : allCss()) {
            for (String[] rule : rules(css)) {
                boolean tab = aboutTab(rule[0]) || rule[0].contains("[role=\"tab\"]");
                if (!tab) {
                    continue;
                }
                var matcher = java.util.regex.Pattern
                        .compile("min-height:\\s*(\\d+)px").matcher(rule[1]);
                while (matcher.find()) {
                    if (Integer.parseInt(matcher.group(1)) < 44) {
                        offenders.add(css.getFileName() + ": " + rule[0].strip().replaceAll("\\s+", " ")
                                + " → min-height " + matcher.group(1) + "px");
                    }
                }
            }
        }
        assertThat(offenders)
                .as("вкладка — цель под палец на всех вьюпортах: 44px без исключений (WCAG 2.5.5). "
                        + "До iter-79 вкладки материалов стояли 40px, и это не разжималось "
                        + "медиазапросом нигде")
                .isEmpty();
    }

}
