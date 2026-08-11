package com.cheatsheet.quiz.ui;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
 * Коробка поля ввода в приложении одна, и живёт она в компоненте.
 *
 * <h2>Дефект, а не гипотеза (iter-77, замеры в WebKit@2, 1440×900)</h2>
 * Один и тот же {@code <select>} внутри одного и того же {@code <label class="field">}
 * рисовался двумя разными контролами: на {@code /stats} — 195×44, радиус 8px, паддинг
 * 8/48/8/12, стрелка приложения; на {@code /bookmarks} — 192×25, радиус 5px, паддинг 0,
 * нативная двойная стрелка. Соседний {@code input[type=search]} в той же полосе отбора
 * слушался и стоял 44px, поэтому строка фильтров шла ступенькой.
 *
 * <p>Правило {@code .field select} матчилось на обеих страницах (проверено
 * {@code el.matches()} в живом движке), но НЕ применялось: WebKit рисует {@code select}
 * нативным {@code menulist} и при {@code appearance: auto} игнорирует авторские
 * {@code height}/{@code padding}/{@code border-radius}. Разницу давал {@code appearance: none},
 * который жил в {@code utilities-overrides.css} под списком из двух страниц
 * ({@code .settings-page select, .stats-page select}). То есть коробка поля работала не там,
 * где стоит {@code .field}, а там, где страница попала в список — ровно две системы форм,
 * что запрещает §24, и нарушение FRD-022.
 *
 * <p>Тем же замером нашлись три несогласованные коробки {@code textarea} (компонент,
 * {@code .bookmark-note-form}, {@code .typed-answer-input}: 16px/18px, радиусы sm/md) и три
 * рецепта выключенного контрола (opacity 0.5/0.5/0.65).
 *
 * <h2>Что держат тесты</h2>
 * Не пиксели — их проверяет браузер (80 сочетаний вьюпорт×страница, 780 замеров). Тесты
 * держат структуру решения: скелет объявлен один раз, страницы его не копируют, каждый
 * контрол разметки стоит в {@code .field}, а граница поля берётся из собственного токена —
 * потому что у поля она единственный признак существования и обязана давать 3:1 (WCAG 1.4.11).
 */
class FormControlComponentContractTest {

    private static final Path RESOURCES = Path.of("src/main/resources");
    private static final Path COMPONENT_CSS = RESOURCES.resolve("static/css/components-controls.css");
    private static final Path TOKENS_CSS = RESOURCES.resolve("static/css/tokens.css");
    private static final Path UTILITIES_CSS = RESOURCES.resolve("static/css/utilities-overrides.css");
    private static final Path MANIFEST = RESOURCES.resolve("css-bundle-manifest.txt");
    private static final Path TEMPLATES = RESOURCES.resolve("templates");
    private static final Path CSS_DIR = RESOURCES.resolve("static/css");

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

    /** Файлы, которым запрещено переобъявлять коробку: все страничные + глобальные утилиты. */
    private static List<Path> nonComponentCssFiles() {
        try (Stream<Path> files = Files.list(CSS_DIR)) {
            List<Path> all = new ArrayList<>(files
                    .filter(p -> p.getFileName().toString().startsWith("pages-"))
                    .filter(p -> p.getFileName().toString().endsWith(".css"))
                    .sorted()
                    .toList());
            all.add(UTILITIES_CSS);
            return all;
        } catch (IOException e) {
            throw new UncheckedIOException("не читается " + CSS_DIR, e);
        }
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
     * Тег контрола в селекторе — строго по границе слова. Наивное вхождение подстроки даёт
     * ложные срабатывания на обычных классах: {@code :not(.selected)} содержит «select»,
     * и правило про кнопку уверенности прилетало в отчёт как «второй рецепт поля».
     */
    private static boolean mentions(String selector, String tag) {
        return selector.matches("(?s).*\\b" + tag + "\\b.*");
    }

    private static boolean mentionsAnyControl(String selector) {
        return mentions(selector, "select") || mentions(selector, "textarea")
                || mentions(selector, "input");
    }

    private static List<Path> templateFiles() {
        try (Stream<Path> files = Files.walk(TEMPLATES)) {
            return files.filter(p -> p.getFileName().toString().endsWith(".html")).sorted().toList();
        } catch (IOException e) {
            throw new UncheckedIOException("не читается " + TEMPLATES, e);
        }
    }

    @Test
    void controlBoxIsDeclaredOnceInTheComponentAndShippedEverywhere() {
        assertThat(Files.exists(COMPONENT_CSS))
                .as("компонент контролов удалён — коробка вернётся копиями по страницам")
                .isTrue();

        String css = declarationsOnly(read(COMPONENT_CSS));
        assertThat(css)
                .as("скелет коробки: одна высота касания, один кегль, один радиус на все три "
                        + "типа контрола")
                .contains(".field input")
                .contains(".field select")
                .contains(".field textarea")
                .contains("min-height: 44px");
        assertThat(css)
                .as("`appearance: none` — часть коробки, а не привилегия двух страниц: без него "
                        + "WebKit рисует нативный menulist и молча игнорирует height/padding/radius")
                .contains("appearance: none");
        assertThat(css)
                .as("свобода от нативной отрисовки обязана идти в паре со своей стрелкой, иначе "
                        + "select останется без единственного признака раскрываемости")
                .contains("background-image:");
        assertThat(css)
                .as("поверхность-носитель двигает ТОЛЬКО подложку и только через ручку — иначе "
                        + "страница снова начнёт переобъявлять коробку целиком ради одного цвета")
                .contains("--control-surface");

        assertThat(read(MANIFEST))
                .as("компонент подключён гейтом `*`: поля есть на /settings, /stats, /bookmarks, "
                        + "/focus и /login — список страниц здесь и породил две системы")
                .contains("* /css/components-controls.css");
    }

    @Test
    void nobodyOutsideTheComponentFreesTheNativeSelect() {
        List<String> offenders = new ArrayList<>();
        for (Path css : nonComponentCssFiles()) {
            for (String[] rule : rules(css)) {
                if (mentions(rule[0], "select") && rule[1].contains("appearance")) {
                    offenders.add(css.getFileName() + ": " + rule[0].trim());
                }
            }
        }
        assertThat(offenders)
                .as("`appearance` на select вне компонента — это возврат к списку страниц вместо "
                        + "класса .field: страница в списке получает коробку приложения, "
                        + "страница вне списка — контрол системы (замер: 44px против 25px в одной "
                        + "полосе отбора)")
                .isEmpty();
    }

    @Test
    void pagesDoNotRedeclareTheControlBox() {
        List<String> forbidden = List.of("appearance", "border-radius", "font-size", "font:");
        List<String> offenders = new ArrayList<>();
        for (Path css : nonComponentCssFiles()) {
            for (String[] rule : rules(css)) {
                if (!mentions(rule[0], "select") && !mentions(rule[0], "textarea")) {
                    continue;
                }
                for (String prop : forbidden) {
                    if (rule[1].contains(prop)) {
                        offenders.add(css.getFileName() + ": " + rule[0].trim() + " → " + prop);
                    }
                }
            }
        }
        assertThat(offenders)
                .as("страница снова описывает коробку контрола. Так и разъехались три textarea: "
                        + "`font: inherit` в закладках давал кегль ПРОЗЫ (18px) вместо контрола "
                        + "(16px), а /focus держал радиус md против sm компонента. Странице "
                        + "остаются её собственные величины (высота, моно-шрифт кода, "
                        + "scroll-margin), но не скелет")
                .isEmpty();
    }

    @Test
    void disabledControlHasASingleRecipe() {
        List<String> offenders = new ArrayList<>();
        for (Path css : nonComponentCssFiles()) {
            for (String[] rule : rules(css)) {
                if (mentionsAnyControl(rule[0]) && rule[0].contains(":disabled")
                        && rule[1].contains("opacity")) {
                    offenders.add(css.getFileName() + ": " + rule[0].trim());
                }
            }
        }
        assertThat(offenders)
                .as("второй ответ на вопрос «как выглядит выключенное поле». До iter-77 их было "
                        + "три: 0.5 у нативного :disabled, 0.5 у JS-класса .is-disabled-control и "
                        + "0.65 персонально у настроек — одна и та же форма гасла по-разному")
                .isEmpty();
    }

    @Test
    void everySelectAndTextareaSitsInsideAField() {
        List<String> naked = new ArrayList<>();
        for (Path template : templateFiles()) {
            Document doc = Jsoup.parse(read(template));
            for (Element control : doc.select("select, textarea")) {
                boolean inField = control.parents().stream().anyMatch(p -> p.hasClass("field"));
                if (!inField) {
                    naked.add(template.getFileName() + ": <" + control.tagName() + " name=\""
                            + control.attr("name") + "\">");
                }
            }
        }
        assertThat(naked)
                .as("контрол вне .field остаётся нативным: без 44px касания, без подписи-эйбрау и "
                        + "с системной стрелкой — то есть ровно в том виде, из которого iter-77 "
                        + "выводил /bookmarks")
                .isEmpty();
    }

    @Test
    void controlBorderIsItsOwnTokenInBothThemes() {
        String component = declarationsOnly(read(COMPONENT_CSS));
        assertThat(component)
                .as("граница поля берётся из собственного токена: заливка отличается от подложки "
                        + "на 1.00–1.09:1, то есть контрол рисует ровно граница. На общем "
                        + "`--color-border-primary` она шла 1.42:1 к карточке при требуемых 3:1 "
                        + "(WCAG 1.4.11); после токена — 3.36–3.74:1 в обеих темах")
                .contains("--color-control-border");

        String tokens = declarationsOnly(read(TOKENS_CSS));
        assertThat(tokens.split("--color-control-border:", -1).length - 1)
                .as("токен обязан быть определён в обеих темах: на светлой подложка белая, и "
                        + "тёмный тон границы там был бы не решением, а обратной ошибкой")
                .isEqualTo(2);
    }
}
