package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Обе колонки раскладки «Разделение» ограничены своей мерой чтения, а ширина композиции
 * считается из них.
 *
 * <h2>Найденный дефект, а не гипотеза</h2>
 * Правило {@code [data-layout="split"] .focus-training} стояло как
 * {@code grid-template-columns: minmax(0, 20rem) minmax(0, 1fr)} под капом
 * {@code max-width: var(--max-width-result)}. Обе половины врали, причём в разные стороны.
 * Замер WebKit 18.2 (DPR 2, вьюпорты 1280…2560, ширина в СИМВОЛАХ через
 * {@code measureText('0')} по фактическому шрифту элемента):
 *
 * <ul>
 *   <li>колонка вопроса стояла ровно 320px на <b>всех</b> вьюпортах, а кегль героя растёт
 *       с {@code vw} (31.5 → 34.4px). Мера главного текста страницы поэтому <b>падала</b>
 *       по мере расширения экрана: 16ch (1280) → 15ch (1440, 1512) → 14ch (1728, 1920,
 *       2560). Кривая инвертирована: чем шире монитор, тем хуже читается вопрос. Проба
 *       108-символьным вопросом давала 7 строк на любой ширине;</li>
 *   <li>колонка ответов, наоборот, росла свободно ({@code 1fr}) и начиная с 1512 давала
 *       73ch внутри варианта — <b>сверх</b> проектной {@code --measure: 72ch};</li>
 *   <li>кап {@code 72rem} численно совпадал с суммой колонок (20 + 4 + 48rem), но это было
 *       совпадение: ни одна строка не связывала кап с треками. Правка любого литерала молча
 *       открыла бы мёртвый центр — тот же класс дефекта, что {@link
 *       FocusWideCompositionContractTest} закрывает в WIDE-композиции.</li>
 * </ul>
 *
 * <p>Ни один из трёх пунктов не виден на скриншоте одной ширины: инвертированная кривая
 * проявляется только при сравнении вьюпортов, а 73ch против 72ch — это одна лишняя буква
 * в строке. Все три ловятся замером и только им.
 *
 * <h2>Что именно закреплено</h2>
 * Инвариант: <b>обе колонки заданы токенами-мерами, кап контейнера сложен из них же</b>.
 * Свободное место сверх суммы уходит в симметричные поля ({@code justify-content: center}),
 * а не в растягивание текста за меру чтения. Проверяем структурно — по составу выражения,
 * а не по вычисленному числу: считать CSS {@code calc} в тесте значило бы проверять свою же
 * арифметику.
 *
 * <p>Отдельно закреплено отсутствие {@code 1fr}: именно свободный трек и уводил ответы за
 * меру. Вернуть его — значит вернуть дефект, оставив тождество капа формально целым.
 */
class FocusSplitCompositionContractTest {

    private static final Path FOCUS_CSS = Path.of("src/main/resources/static/css/pages-focus.css");
    private static final Path TOKENS = Path.of("src/main/resources/static/css/tokens.css");

    /**
     * Тело правила раскладки «Разделение».
     *
     * <p>iter-68: грид переехал с самой композиции ({@code .focus-training}) внутрь рабочей
     * колонки ({@code .focus-main}). Причина не в вкусе: у композиции теперь ровно два
     * flex-ребёнка — колонка и вспомогательный рельс, — и рельс обязан уметь уехать под
     * колонку, чего grid не умеет. Пейны «Разделения» делят только колонку.
     */
    private static final Pattern SPLIT_RULE = Pattern.compile(
            "\\[data-layout=\"split\"] \\.focus-page \\.focus-main\\s*\\{([^}]*)}");

    private static final Pattern DECLARATION_MAX_WIDTH = Pattern.compile("max-width:\\s*([^;]+);");
    private static final Pattern DECLARATION_COLUMNS = Pattern.compile("grid-template-columns:\\s*([^;]+);");
    private static final Pattern DECLARATION_GAP = Pattern.compile("column-gap:\\s*([^;]+);");

    private String splitRuleBody() {
        Matcher matcher = SPLIT_RULE.matcher(read(FOCUS_CSS));
        assertThat(matcher.find())
                .as("в pages-focus.css нет правила [data-layout=\"split\"] .focus-page .focus-main — "
                        + "раскладку переименовали или сняли, контракт надо переписать под факт")
                .isTrue();
        return matcher.group(1);
    }

    private static String declaration(String body, Pattern pattern, String property) {
        Matcher matcher = pattern.matcher(body);
        assertThat(matcher.find())
                .as("в правиле раскладки «Разделение» нет объявления %s", property)
                .isTrue();
        return matcher.group(1).trim();
    }

    @Test
    void splitColumnsAreMeasuresNotLiterals() {
        String columns = declaration(splitRuleBody(), DECLARATION_COLUMNS, "grid-template-columns");

        assertThat(columns)
                .as("колонка вопроса задана мимо --focus-split-question — она снова застынет "
                        + "константой, пока кегль героя растёт с vw, и мера вопроса будет ПАДАТЬ "
                        + "при расширении экрана (замер: 16ch на 1280 → 14ch на 1728+)")
                .contains("var(--focus-split-question)");
        assertThat(columns)
                .as("колонка ответов задана мимо --focus-split-answers — вариант перерастёт "
                        + "--measure: 72ch (замер: 73ch с 1512 и выше)")
                .contains("var(--focus-split-answers)");
    }

    @Test
    void splitAnswersColumnIsBoundedNotFree() {
        assertThat(declaration(splitRuleBody(), DECLARATION_COLUMNS, "grid-template-columns"))
                .as("в колонках раскладки снова свободный трек: 1fr съедает всё место сверх "
                        + "колонки вопроса и уводит вариант за меру чтения — кап контейнера при "
                        + "этом остаётся формально целым, дефект вернётся молча")
                .doesNotContain("1fr");
    }

    /**
     * Разворачивает {@code var(--focus-main-measure)} в переопределение токена внутри раскладки.
     *
     * <p>iter-67: мера полосы принадлежит СТРАНИЦЕ, а не одной композиции — её читают ещё
     * resume-баннер и пустое состояние. Пока сумму колонок знала только композиция split, на
     * вьюпорте 1920 замер давал композицию 1229[456..1684] против баннера 1296[422..1718] —
     * 34px с каждого борта. Поэтому раскладка переопределяет токен на предке
     * {@code .focus-page}, а кап читает его.
     *
     * <p>iter-68 сузил, ЧТО именно переопределяется: не {@code --focus-measure} (мера всей
     * полосы), а {@code --focus-main-measure} (мера рабочей колонки). Прежнее переопределение
     * было не просто широким, а неверным по смыслу: подменяя всю формулу, раскладка вычёркивала
     * из неё рельс — хотя рельс в «Разделении» на месте и ширину занимает. Теперь формула полосы
     * одна на все раскладки (колонка + жёлоб + рельс), а раскладки различаются только первым
     * слагаемым. Гарантия та же: сумма обязана остаться суммой.
     */
    private static String resolveSplitMeasure(String value) {
        if (!value.contains("var(--focus-main-measure)")) {
            return value;
        }
        Matcher override = Pattern.compile(
                "\\[data-layout=\"split\"] \\.focus-page\\s*\\{\\s*--focus-main-measure:\\s*([^;]+);")
                .matcher(read(FOCUS_CSS));
        assertThat(override.find())
                .as("композиция split берёт кап из --focus-main-measure, но раскладка этот токен НЕ "
                        + "переопределяет — значит, её рабочая колонка встала на меру основной "
                        + "раскладки (960px против своих ~1130), пейны в неё не поместятся, и "
                        + "композиция разойдётся с resume-баннером над ней")
                .isTrue();
        return value.replace("var(--focus-main-measure)", override.group(1).replaceAll("\\s+", " ").trim());
    }

    @Test
    void splitCapIsSumOfItsOwnColumns() {
        String body = splitRuleBody();
        String maxWidth = resolveSplitMeasure(declaration(body, DECLARATION_MAX_WIDTH, "max-width"));

        assertThat(maxWidth)
                .as("кап композиции отпущен — обе колонки капнуты своей мерой, поэтому "
                        + "растягиваться будет не текст, а пустой грид")
                .isNotEqualTo("none");
        assertThat(maxWidth)
                .as("кап композиции обязан складываться из тех же величин, что задают её колонки "
                        + "и жёлоб; литерал совпадает с суммой только до первой правки любого из них")
                .contains("var(--focus-split-question)")
                .contains("var(--focus-split-gap)")
                .contains("var(--focus-split-answers)");

        assertThat(declaration(body, DECLARATION_GAP, "column-gap"))
                .as("жёлоб задан мимо --focus-split-gap — кап контейнера считает один жёлоб, "
                        + "а грид рисует другой, и разница уходит в дыру")
                .isEqualTo("var(--focus-split-gap)");
    }

    /**
     * Раскладка меняет меру рабочей колонки, но не формулу полосы.
     *
     * <p>До iter-68 «Разделение» переопределяло {@code --focus-measure} целиком — и тем самым
     * вычёркивало из меры полосы рельс, который в этой раскладке никуда не девался. Кап полосы
     * оказывался меньше её содержимого; починка любой другой раскладки этого бы не заметила,
     * потому что формулы у них были РАЗНЫЕ. Одна формула на все раскладки — это и есть
     * запрет на «две конкурирующие системы» из §24 применительно к мере.
     *
     * <p>Формула объявлена ровно один раз — на {@code .focus-page}, рядом с переопределением
     * слагаемого. Именно это соседство и делает переопределение действующим: {@code var()}
     * внутри кастомного свойства подставляется на элементе объявления, поэтому сумма, стоявшая
     * на {@code :root}, запекала базовое слагаемое (замер split@2560: кап полосы 1296px против
     * собственной меры её рабочей колонки 1312px).
     */
    @Test
    void splitOverridesColumnMeasureNotTheBandFormula() {
        String css = read(FOCUS_CSS).replaceAll("(?s)/\\*.*?\\*/", "");

        assertThat(css)
                .as("раскладка «Разделение» снова переопределяет --focus-measure целиком: она "
                        + "подменяет ФОРМУЛУ полосы вместо одного слагаемого и вычёркивает из "
                        + "неё рельс, который в этой раскладке на месте и место занимает")
                .doesNotContainPattern("\\[data-layout=\"split\"][^{}]*\\{[^}]*--focus-measure:");

        assertThat(css)
                .as("формула полосы съехала с .focus-page. Она обязана стоять на том же элементе, "
                        + "где раскладка переопределяет слагаемое: иначе подстановка запечёт "
                        + "базовое значение и переопределение в сумму не попадёт")
                .containsPattern("html\\[data-design] \\.focus-page\\s*\\{[^}]*--focus-measure:\\s*max\\(");
    }

    @Test
    void splitLeftoverGoesToMarginsNotToText() {
        assertThat(splitRuleBody())
                .as("без justify-content:center остаток свободного места внутри капнутого "
                        + "контейнера копится у правого края одной несимметричной дырой")
                .contains("justify-content: center");
    }

    @Test
    void splitTokensAreDefinedOnce() {
        String tokens = read(TOKENS);

        assertThat(countDefinitions(tokens, "--focus-split-question"))
                .as("--focus-split-question должен быть определён ровно один раз: второе "
                        + "определение рассинхронит кап композиции с шириной её же колонки")
                .isEqualTo(1);
        assertThat(countDefinitions(tokens, "--focus-split-answers"))
                .as("--focus-split-answers должен быть определён ровно один раз")
                .isEqualTo(1);
        assertThat(countDefinitions(tokens, "--focus-split-gap"))
                .as("--focus-split-gap должен быть определён ровно один раз")
                .isEqualTo(1);
    }

    private static int countDefinitions(String css, String token) {
        Matcher matcher = Pattern.compile(Pattern.quote(token) + "\\s*:").matcher(css);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    private static String read(Path path) {
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("не прочитать " + path, e);
        }
    }
}
