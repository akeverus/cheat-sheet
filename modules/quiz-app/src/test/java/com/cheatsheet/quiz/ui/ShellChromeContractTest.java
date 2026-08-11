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
 * У оболочки ОДНА полоса хрома на ширину, а не две (FRD-021).
 *
 * <h2>Найденный дефект, а не гипотеза (iter-70, замеры WebKit@2 по матрице §10)</h2>
 *
 * <ol>
 *   <li><b>Две полосы хрома на десктопе.</b> Слева стоял постоянный рельс, а сверху —
 *       {@code <header class="topbar">} высотой {@code --header-height: 64px} плюс 1px границы,
 *       во всю ширину колонки контента, на КАЖДОЙ из семи shell-страниц. Внутри полосы стоял
 *       ровно один интерактивный элемент — кнопка темы 38×38. На окне MacBook 1280×800 это
 *       8% высоты окна ради 2.2% собственной ширины; контент начинался с {@code y=65}
 *       вместо {@code y=0} на /, /stats, /bookmarks, /settings, /review. После удаления
 *       замер даёт {@code mainTop: 0} и первый значимый элемент на {@code y=32} — те же
 *       пять страниц.</li>
 *   <li><b>Ширина рельса была литералом.</b> {@code --sidebar-width: 232px} не выводилась ни из
 *       чего: подписи в рельсе набираются системным шрифтом, чьи метрики отличаются между
 *       WebKit и Chrome, и запас «на глаз» либо режет подпись, либо оставляет мёртвое поле.
 *       Замер самой длинной подписи («Светлая тема» 123.8px) + отступы (28 рельса + 24 строки
 *       + 16 иконка + 11 зазор = 202.8) дал вычисляемый пол 208px; потолок 236px — ширина, на
 *       которой рельс перестаёт расти, чтобы не отбирать полосу у контента (§12: плотность
 *       адаптивная, а не бесконечно растущие отступы).</li>
 *   <li><b>Три разные единицы высоты в трёх соседних правилах.</b> {@code body} держал
 *       {@code 100dvh}, а {@code .app} и {@code .sidebar} — {@code 100vh}. На мобильном Safari
 *       {@code 100vh} больше видимой области на высоту убирающейся панели (§11), то есть рельс
 *       и холст обещали высоту, которой на экране нет.</li>
 *   <li><b>Два класса на одну форму и три имени на одну кнопку.</b> {@code .sidebar-logout} и
 *       {@code .topbar-logout} имели ОДИНАКОВОЕ тело правила ({@code display: contents}), а
 *       контейнер действий назывался {@code .top-actions} при том, что верхней полосы не
 *       осталось. Это ровно та «вторая конкурирующая система», которую запрещает §24.</li>
 * </ol>
 *
 * <h2>Почему это гейт, а не разовая правка</h2>
 * Верхняя полоса возвращается не как решение, а как удобное место: у любого следующего
 * глобального действия (поиск, профиль, счётчик) первым кандидатом будет «положу наверх».
 * Гейт фиксирует не отсутствие класса, а структуру: место глобальных действий десктопа —
 * футер рельса, узких ширин — {@code mobile-header}; ширина рельса считается, а не пишется;
 * единица высоты одна.
 *
 * <p>Тогл темы отдельно считается ПОФРАГМЕНТНО. Прежний гейт запрещал
 * {@code data-theme-toggle} в рельсе — но запрещал он не место, а ДУБЛИРОВАНИЕ: пока полос
 * было две, две кнопки темы жили бы одновременно. Требование выжило в виде «ровно один тогл
 * в каждом фрагменте» (см. {@link TemplateFragmentContractTest}); здесь держим само наличие
 * одной десктопной формы.
 */
class ShellChromeContractTest {

    private static final Path CSS_DIR = Path.of("src/main/resources/static/css");
    private static final Path TEMPLATES = Path.of("src/main/resources/templates");
    private static final Path SHELL_CSS = CSS_DIR.resolve("shell.css");
    private static final Path TOKENS_CSS = CSS_DIR.resolve("tokens.css");

    /**
     * Имена, удалённые вместе с верхней полосой. Каждое оставило после себя комментарий с
     * причиной — то есть со своим дословным именем, поэтому гейт смотрит только на код.
     */
    private static final List<String> REMOVED_CHROME_NAMES = List.of(
            ".topbar", ".top-actions", ".topbar-logout", ".sidebar-logout",
            ".sidebar-spacer", "--header-height");

    private static String read(Path path) {
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("не прочитать " + path, e);
        }
    }

    /** Объявления без комментариев — иначе гейт запрещал бы рассказ о причине удаления. */
    private static String cssDeclarationsOnly(String css) {
        return css.replaceAll("(?s)/\\*.*?\\*/", "");
    }

    private static String markupOnly(String html) {
        return html.replaceAll("(?s)<!--.*?-->", "");
    }

    private static Stream<Path> filesUnder(Path dir, String suffix) {
        try (Stream<Path> files = Files.walk(dir)) {
            return files.filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith(suffix))
                    .toList().stream();
        } catch (IOException e) {
            throw new UncheckedIOException("не обойти " + dir, e);
        }
    }

    @Test
    void deskTopChromeBandDoesNotComeBack() {
        List<Path> css = filesUnder(CSS_DIR, ".css").toList();
        List<Path> html = filesUnder(TEMPLATES, ".html").toList();
        assertThat(css).as("в %s нет ни одного css — гейт смотрит не туда", CSS_DIR).isNotEmpty();
        assertThat(html).as("в %s нет ни одного шаблона — гейт смотрит не туда", TEMPLATES)
                .isNotEmpty();

        for (String name : REMOVED_CHROME_NAMES) {
            // `.topbar` как подстрока живёт внутри `.focus-topbar` — строки идентичности
            // вопроса. Это ДРУГОЙ элемент (страничный, внутри рабочей колонки), он остаётся.
            Pattern standalone = Pattern.compile("(?<![\\w.-])" + Pattern.quote(name) + "(?![\\w-])");

            for (Path file : css) {
                assertThat(standalone.matcher(cssDeclarationsOnly(read(file))).find())
                        .as("имя `%s` вернулось в %s. Оно снято вместе с верхней полосой "
                                + "(FRD-021): 64px sticky-высоты на семи страницах ради одной "
                                + "кнопки 38×38. Место глобальных действий десктопа — футер "
                                + "рельса (.sidebar-footer), узких ширин — .mobile-header",
                                name, file.getFileName())
                        .isFalse();
            }
            for (Path file : html) {
                assertThat(standalone.matcher(markupOnly(read(file))).find())
                        .as("имя `%s` вернулось в разметку %s (FRD-021). Вторая полоса хрома "
                                + "на ширину — это две конкурирующие системы навигации (§24)",
                                name, file.getFileName())
                        .isFalse();
            }
        }
    }

    /**
     * Мутационная проверка: каждый запрет выше обязан КУСАТЬСЯ.
     *
     * <p>Регулярка со словарными границами легко становится немой (например, если бы
     * {@code (?![\w-])} случайно съел точку). Гейт, который не срабатывает на возврате
     * дефекта, хуже отсутствующего: он создаёт ложную уверенность.
     */
    @Test
    void chromeBandGuardActuallyBites() {
        String returned = """
                html[data-design] .topbar { height: var(--header-height); }
                html[data-design] .top-actions { display: flex; }
                html[data-design] .topbar-logout { display: contents; }
                html[data-design] .sidebar-logout { display: contents; }
                html[data-design] .sidebar-spacer { flex: 1; }
                """;
        for (String name : REMOVED_CHROME_NAMES) {
            Pattern standalone = Pattern.compile("(?<![\\w.-])" + Pattern.quote(name) + "(?![\\w-])");
            assertThat(standalone.matcher(returned).find())
                    .as("запрет на `%s` немой: он не находит имя даже в тексте, который "
                            + "буквально возвращает удалённое правило", name)
                    .isTrue();
        }

        // …и НЕ кусает страничную строку идентичности вопроса, которая остаётся жить.
        Pattern topbar = Pattern.compile("(?<![\\w.-])" + Pattern.quote(".topbar") + "(?![\\w-])");
        assertThat(topbar.matcher("html[data-design] .focus-topbar .zone-chip { }").find())
                .as("запрет на `.topbar` задевает `.focus-topbar` — строку идентичности вопроса "
                        + "внутри рабочей колонки. Это другой элемент, он не удалялся")
                .isFalse();
    }

    @Test
    void railWidthIsDerivedFromContentNotAFrozenLiteral() {
        String tokens = cssDeclarationsOnly(read(TOKENS_CSS));

        Matcher declaration = Pattern.compile("--sidebar-width:\\s*([^;]+);").matcher(tokens);
        assertThat(declaration.find())
                .as("в tokens.css нет --sidebar-width — единственная ширина рельса исчезла или "
                        + "переименована, контракт надо переписать под факт")
                .isTrue();

        String value = declaration.group(1).trim();
        assertThat(declaration.find())
                .as("--sidebar-width объявлен дважды — две ширины одного рельса (§24)")
                .isFalse();

        assertThat(value)
                .as("ширина рельса снова заморожена литералом `%s`. Пол 208px посчитан из "
                        + "содержимого (28 отступа рельса + 24 отступа строки + 16 иконка + 11 "
                        + "зазор + 123.8 самая длинная подпись «Светлая тема» = 202.8), потолок "
                        + "236px не даёт рельсу отбирать полосу у контента. Литерал ломается на "
                        + "метриках системного шрифта: они разные в WebKit и Chrome", value)
                .startsWith("clamp(");
    }

    @Test
    void shellHeightUnitIsDvhEverywhere() {
        String shell = cssDeclarationsOnly(read(SHELL_CSS));

        assertThat(shell)
                .as("в shell.css вернулся 100vh. На мобильном Safari 100vh БОЛЬШЕ видимой "
                        + "области на высоту убирающейся панели браузера (§11) — рельс и холст "
                        + "обещают высоту, которой на экране нет. Единица у оболочки одна: dvh")
                .doesNotContain("100vh");
        assertThat(shell)
                .as("в shell.css не осталось ни одного 100dvh — высоту оболочки задаёт что-то "
                        + "другое, контракт надо переписать под факт")
                .contains("100dvh");
    }

    @Test
    void globalActionsLiveInRailFooterSeparatedFromNavigation() {
        String shell = cssDeclarationsOnly(read(SHELL_CSS));

        assertThat(shell)
                .as("правило .sidebar-footer исчезло из shell.css — глобальным действиям "
                        + "десктопа (тема, выход) больше негде жить, кроме как в новой полосе")
                .contains("html[data-design] .sidebar-footer {");

        String footer = shell.substring(shell.indexOf("html[data-design] .sidebar-footer {"));
        footer = footer.substring(0, footer.indexOf('}'));

        assertThat(footer)
                .as("футер рельса больше не отжимается вниз через margin-top:auto. Либо вернулась "
                        + "пустая распорка `.sidebar-spacer` (узел разметки ради одного свойства), "
                        + "либо действия всплыли под навигацию")
                .contains("margin-top: auto");
        assertThat(footer)
                .as("у футера рельса пропала линия сверху. Без неё «Тема»/«Выйти» в конце "
                        + "колонки читаются как продолжение списка разделов — именно это возражение "
                        + "когда-то и унесло тогл темы наверх")
                .contains("border-top");
    }

    @Test
    void exactlyOneDesktopThemeToggleAndItLivesOutsideNav() {
        String sidebar = markupOnly(read(TEMPLATES.resolve("fragments/sidebar.html")));

        assertThat(countOccurrences(sidebar, "data-theme-toggle"))
                .as("в рельсе не ровно один тогл темы. Прежний запрет на тогл в рельсе снимал "
                        + "не место, а ДУБЛИРОВАНИЕ (пока полос было две, кнопки темы жили бы "
                        + "одновременно) — требование выжило как счётчик, а не как запрет")
                .isEqualTo(1);

        int navEnd = sidebar.indexOf("</nav>");
        int toggle = sidebar.indexOf("data-theme-toggle");
        assertThat(navEnd).as("в fragments/sidebar.html нет </nav> — структура рельса другая, "
                + "контракт надо переписать под факт").isGreaterThan(0);
        assertThat(toggle)
                .as("тогл темы уехал ВНУТРЬ <nav>. Для скринридера он тогда становится ещё одним "
                        + "пунктом списка разделов — тем самым «ещё одним пунктом навигации», "
                        + "из-за которого его отсюда когда-то и унесли")
                .isGreaterThan(navEnd);
    }

    private static int countOccurrences(String haystack, String needle) {
        int count = 0;
        int at = haystack.indexOf(needle);
        while (at >= 0) {
            count++;
            at = haystack.indexOf(needle, at + needle.length());
        }
        return count;
    }
}
