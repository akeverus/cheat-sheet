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
 * Кнопка в приложении одна: коробку задаёт компонент, краску — слой pixel, страница
 * отвечает только за место в раскладке.
 *
 * <h2>Дефект, а не гипотеза (iter-78, замеры в WebKit@2)</h2>
 * Кнопка была описана 15 независимыми рецептами в 9 файлах, и почти все они не доезжали
 * до экрана: одно правило в {@code pixel-buttons.css} перечисляло 25 селекторов и стирало
 * им геометрию через {@code !important}. Порядок слоёв в приложении обратный для
 * {@code !important} — важное из РАННЕГО слоя бьёт важное из позднего, — а файла
 * {@code styles.css}, ради которого стиратель заводился, в проекте нет вовсе. Замер: у
 * каждой стёртой кнопки {@code border-radius: 0px} и {@code border-width: 0px}, то есть ни
 * один объявленный радиус не дошёл до пользователя ни разу.
 *
 * <p>Тем же проходом нашлись ещё четыре расхождения, каждое подтверждено в живом движке:
 * <ul>
 *   <li>кнопки тулбаров кода и схемы стояли 32px на ВСЕХ 15 вьюпортах, включая 360–430px,
 *       то есть ниже собственной планки касания приложения (WCAG 2.5.5);</li>
 *   <li>фокус существовал в двух идиомах: обводка у части кнопок и мягкое свечение через
 *       {@code box-shadow} у иконочных — а в {@code box-shadow} лежит pixel-контур, поэтому
 *       по Tab у иконки пропадала рамка;</li>
 *   <li>у вкладок статистики фокус-индикатора не было вовсе: {@code box-shadow} в покое и в
 *       фокусе совпадал побайтно при {@code outline: 0px} (WCAG 2.4.7);</li>
 *   <li>разрушительный ранг красился фиксированным {@code --px-red} (цвет тёмной палитры) в
 *       обеих темах: в светлой контраст 2.53 в покое и 2.26 на наведении при требуемых 4.5
 *       (WCAG 1.4.3).</li>
 * </ul>
 *
 * <h2>Что держат тесты</h2>
 * Не пиксели — их проверяет браузер (15 вьюпортов × 4 страницы). Тесты держат структуру
 * решения: скелет объявлен один раз, страницы его не копируют, стирателя на кнопках нет,
 * фокус — одной идиомой, тач-цель не опускается ниже 44px без явного разжатия под палец.
 */
class ButtonRankComponentContractTest {

    private static final Path RESOURCES = Path.of("src/main/resources");
    private static final Path CSS_DIR = RESOURCES.resolve("static/css");
    private static final Path COMPONENT_CSS = CSS_DIR.resolve("components-controls.css");
    private static final Path PAINT_CSS = CSS_DIR.resolve("pixel-buttons.css");
    private static final Path SHELL_CSS = CSS_DIR.resolve("shell.css");
    private static final Path UTILITIES_CSS = CSS_DIR.resolve("utilities-overrides.css");
    private static final Path MANIFEST = RESOURCES.resolve("css-bundle-manifest.txt");

    /** Классы кнопок, чью коробку страница переобъявлять не имеет права. */
    private static final List<String> BUTTON_CLASSES = List.of(
            "btn", "secondary-btn", "next-btn", "primary-btn", "danger-btn",
            "icon-button", "btn-favorite", "confidence-btn", "flashcard-grade-btn",
            "skip-btn", "report-trigger", "flashcard-reveal-btn", "btn-finish-small",
            "filters-apply-btn", "rail-finish-btn", "rail-pause-btn", "summary-more-summary",
            "code-action", "diagram-action");

    /** Свойства коробки и краски. Раскладка (width, margin, flex, position) сюда не входит. */
    private static final List<String> BOX_PROPERTIES = List.of(
            "border-radius", "min-height", "padding", "font-size", "font-family",
            "font-weight", "box-shadow", "background", "border-color");

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

    /**
     * Класс в селекторе — строго по границе слова. Наивное вхождение подстроки ловит
     * {@code .bookmark-remove-btn} на запросе {@code btn} и наоборот.
     */
    private static boolean mentions(String selector, String cssClass) {
        return selector.matches("(?s).*\\b" + cssClass + "\\b.*");
    }

    /** Файлы, которым запрещено переобъявлять коробку кнопки: страничные + утилиты + оболочка. */
    private static List<Path> nonComponentCssFiles() {
        try (Stream<Path> files = Files.list(CSS_DIR)) {
            List<Path> all = new ArrayList<>(files
                    .filter(p -> p.getFileName().toString().startsWith("pages-"))
                    .filter(p -> p.getFileName().toString().endsWith(".css"))
                    .sorted()
                    .toList());
            all.add(UTILITIES_CSS);
            all.add(SHELL_CSS);
            return all;
        } catch (IOException e) {
            throw new UncheckedIOException("не читается " + CSS_DIR, e);
        }
    }

    @Test
    void buttonSkeletonIsDeclaredOnceInTheComponentAndShippedEverywhere() {
        assertThat(Files.exists(COMPONENT_CSS))
                .as("компонент контролов удалён — коробка кнопки вернётся копиями по страницам")
                .isTrue();

        String css = declarationsOnly(read(COMPONENT_CSS));
        assertThat(css)
                .as("скелет кнопки: одна высота касания, один радиус, одна рамка и один контур "
                        + "на все ранги — иначе каждая страница снова начнёт сочинять свой")
                .contains("min-height: 44px")
                .contains("border: 0")
                .contains("border-radius: 0")
                .contains("box-shadow: var(--px-edge)");

        assertThat(read(MANIFEST))
                .as("компонент подключён гейтом `*`: кнопки есть на каждой странице приложения")
                .contains("* /css/components-controls.css");
    }

    @Test
    void pageFilesDoNotRedeclareTheButtonBox() {
        List<String> offenders = new ArrayList<>();
        for (Path css : nonComponentCssFiles()) {
            for (String[] rule : rules(css)) {
                String selector = rule[0];
                String body = rule[1];
                boolean aboutButton = BUTTON_CLASSES.stream().anyMatch(c -> mentions(selector, c));
                if (!aboutButton) {
                    continue;
                }
                for (String property : BOX_PROPERTIES) {
                    if (body.contains(property + ":")) {
                        offenders.add(css.getFileName() + ": " + selector.strip().replaceAll("\\s+", " ")
                                + " → " + property);
                    }
                }
            }
        }
        assertThat(offenders)
                .as("страница задаёт кнопке место в раскладке, а не её вид. Каждая такая копия "
                        + "или спорит с компонентом, или (чаще) молча проигрывает более позднему "
                        + "слою и не доезжает до экрана — в iter-78 таких мёртвых обещаний нашлось "
                        + "восемь, включая красную подсветку «Убрать», которой пользователь "
                        + "никогда не видел")
                .isEmpty();
    }

    @Test
    void paintLayerNeverErasesButtonsWithImportant() {
        List<String> offenders = new ArrayList<>();
        for (String[] rule : rules(PAINT_CSS)) {
            String selector = rule[0];
            boolean aboutButton = BUTTON_CLASSES.stream().anyMatch(c -> mentions(selector, c));
            if (aboutButton && rule[1].contains("!important")) {
                offenders.add(selector.strip().replaceAll("\\s+", " "));
            }
        }
        assertThat(offenders)
                .as("`!important` на кнопке — это стиратель: порядок слоёв для важных объявлений "
                        + "обратный, поэтому важное из раннего слоя бьёт даже страницу, и все "
                        + "остальные рецепты становятся мёртвыми. Файла styles.css, ради которого "
                        + "стиратель заводился, в проекте нет")
                .isEmpty();
    }

    @Test
    void selfAssessmentButtonsShareOneRecipe() {
        String css = declarationsOnly(read(COMPONENT_CSS));
        List<String[]> shared = rules(COMPONENT_CSS).stream()
                .filter(r -> mentions(r[0], "confidence-btn") && mentions(r[0], "flashcard-grade-btn"))
                .toList();
        assertThat(shared)
                .as("уверенность после MCQ и оценка флешкарты — один и тот же контрол самооценки. "
                        + "Пока у них два рецепта, они расходятся: до iter-78 выбранная кнопка "
                        + "тускнела у одной и не тускнела у другой")
                .isNotEmpty();
        assertThat(css)
                .as("ранг самооценки выше базового: у него собственная высота, объявленная явно")
                .contains("48px");
    }

    @Test
    void everyRankStaysReachableByFinger() {
        String css = declarationsOnly(read(COMPONENT_CSS));
        assertThat(css)
                .as("иконочная кнопка — квадрат под палец: 44×44 без исключений (WCAG 2.5.5)")
                .contains("width: 44px")
                .contains("height: 44px");

        List<String[]> compact = rules(COMPONENT_CSS).stream()
                .filter(r -> mentions(r[0], "code-action") || mentions(r[0], "diagram-action"))
                .toList();
        assertThat(compact)
                .as("компактный ранг тулбара объявлен в компоненте, а не ужат страницей")
                .isNotEmpty();
        assertThat(compact.stream().anyMatch(r -> r[1].contains("min-height: 44px")))
                .as("единственный ранг ниже 44px обязан разжиматься под грубый указатель: до "
                        + "iter-78 он стоял 32px на всех 15 вьюпортах, включая 360–430px")
                .isTrue();
        assertThat(css)
                .as("разжатие делает медиазапрос про указатель, а не про ширину окна: узкое окно "
                        + "на макбуке — всё ещё мышь, а широкий планшет — уже палец")
                .contains("(pointer: coarse)");
    }

    @Test
    void focusIsOneIdiomAndDoesNotEraseTheButtonContour() {
        List<String> offenders = new ArrayList<>();
        try (Stream<Path> files = Files.list(CSS_DIR)) {
            for (Path css : files.filter(p -> p.getFileName().toString().endsWith(".css")).sorted().toList()) {
                for (String[] rule : rules(css)) {
                    String selector = rule[0];
                    if (!selector.contains(":focus-visible")) {
                        continue;
                    }
                    boolean aboutButton = BUTTON_CLASSES.stream().anyMatch(c -> mentions(selector, c));
                    boolean glow = rule[1].contains("outline: none") && rule[1].contains("box-shadow");
                    if (aboutButton && glow) {
                        offenders.add(css.getFileName() + ": " + selector.strip().replaceAll("\\s+", " "));
                    }
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("не читается " + CSS_DIR, e);
        }
        assertThat(offenders)
                .as("свечение через box-shadow — вторая идиома фокуса, и она занимает ровно то "
                        + "свойство, в котором живёт pixel-контур кнопки: индикатор либо стирает "
                        + "рамку, либо стирается сам. Замер до iter-78: у вкладок статистики "
                        + "видимого фокуса не было вовсе (WCAG 2.4.7)")
                .isEmpty();

        assertThat(declarationsOnly(read(COMPONENT_CSS)))
                .as("обводка не трогает box-shadow и потому совместима с контуром — это и есть "
                        + "единственная идиома фокуса кнопки")
                .contains("outline: 2px solid var(--color-focus-ring)");
    }

    @Test
    void destructiveRankUsesThemeAwareRed() {
        String paint = declarationsOnly(read(PAINT_CSS));
        assertThat(paint)
                .as("разрушительный ранг красится тема-зависимым токеном: фиксированный --px-red "
                        + "— цвет тёмной палитры, и в светлой теме он давал контраст 2.53 при "
                        + "требуемых 4.5 (WCAG 1.4.3), то есть «необратимое действие» читалось "
                        + "бледной надписью")
                .contains("color: var(--color-status-error)");
        assertThat(paint)
                .as("заливка наведения — тот же семантический токен, а не зашитая rgba: пара "
                        + "текст/фон подобрана в каждой теме")
                .contains("background: var(--color-status-error-wash)");

        List<String> offenders = rules(PAINT_CSS).stream()
                .filter(r -> mentions(r[0], "danger-btn") || mentions(r[0], "btn-finish-small")
                        || mentions(r[0], "bookmark-remove-btn"))
                .filter(r -> r[1].contains("--px-red") || r[1].contains("255, 113, 95"))
                .map(r -> r[0].strip().replaceAll("\\s+", " "))
                .toList();
        assertThat(offenders)
                .as("ни одного фиксированного красного в разрушительном ранге не осталось")
                .isEmpty();
    }
}
