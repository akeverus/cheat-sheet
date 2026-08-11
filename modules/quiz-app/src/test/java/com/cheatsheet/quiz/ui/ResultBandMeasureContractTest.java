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
 * Страница разбора считает свою ширину из колонок — как и экран вопроса, которому она отвечает.
 *
 * <p>{@code /answer} — вторая половина основного сценария продукта: пользователь отвечает на
 * экране вопроса и попадает сюда. Оба экрана обязаны согласованно выбирать число колонок, иначе
 * на одном и том же вьюпорте один показывает рельс сбоку, а другой — нет.
 *
 * <h2>Найденные дефекты, а не гипотезы (iter-69, замеры WebKit@2 на живом {@code /answer})</h2>
 *
 * <ol>
 *   <li><b>Четвёртый замороженный кап полосы подряд.</b> {@code --max-width-result} (72rem) стоял
 *       на {@code .result-page .ed-page} — то есть на ПОЛОСЕ, а не на композиции. Полоса держала
 *       1152px на 1440, 1512, 1680, 1728, 1920 и 2560; мёртвая ширина по борту 34 → 70 → 154 →
 *       178 → 274 → 594px. Тот же класс, что {@code --max-width-settings} (iter-65) и
 *       {@code --max-width-focus} (iter-68).</li>
 *   <li><b>Рабочая колонка СУЖАЛАСЬ по мере расширения окна.</b> Полоса заморожена, а жёлоб
 *       ({@code --space-7}) и {@code --page-gutter} растут с {@code vw}, рельс же упёрт в свой
 *       потолок — значит каждый лишний пиксель жёлоба вычитался из разбора: карточка 637px@1366
 *       → 631 → 618 → 614 → 598 → 592px@2560. Варианты в ней — 573 → 528px при собственном капе
 *       {@code --max-width-options} = 960px, то есть кап был недостижим и ничего не ограничивал.</li>
 *   <li><b>Число колонок выбирал литерал ширины — и выбирал неверно.</b> Грид включался
 *       {@code @media (min-width: 1200px)} при капе полосы 1152px: порог в принципе не мог
 *       соответствовать реальной ширине полосы. На 1280 полоса в 1060px уже делилась на 558 + 352
 *       — рабочая колонка 558px против меры вариантов 960px. Рядом с flex-переносом экрана
 *       вопроса это была вторая, конкурирующая responsive-стратегия (§24).</li>
 * </ol>
 *
 * <h2>Почему гейт, а не разовая правка</h2>
 * Все три — один класс: ширину знает СТРАНИЦА (замороженный литерал), а принадлежит она
 * КОМПОЗИЦИИ (сумме её колонок). Ни один из трёх не виден на скриншоте одной ширины: замороженный
 * кап выглядит опрятно, а расхождение растёт только вправо по матрице §10. Решение структурное —
 * мера {@code --result-measure} по формуле FRD-019 и flex-перенос, порог которого и есть сумма
 * базисов. Гейт запрещает вернуть любую из трёх причин по отдельности.
 *
 * <p>Замер после переработки (WebKit@2, скрипты заблокированы): рабочая колонка ровно 1024px на
 * всех вьюпортах ≥1366 вместо 592–637, варианты достают до своих 960px, мёртвая ширина по борту
 * 1680 154→67, 1728 178→69, 1920 274→146, 2560 594→192. Рельс встаёт рядом с 1680 — на том же
 * вьюпорте, что и у экрана вопроса.
 */
class ResultBandMeasureContractTest {

    private static final Path CSS_DIR = Path.of("src/main/resources/static/css");
    private static final Path RESULT = CSS_DIR.resolve("pages-result.css");
    private static final Path TOKENS = CSS_DIR.resolve("tokens.css");
    private static final Path FOCUS = CSS_DIR.resolve("pages-focus.css");
    private static final Path TEMPLATE = Path.of("src/main/resources/templates/result.html");

    /**
     * Объявления без комментариев: каждый снятый дефект оставил после себя комментарий с
     * причиной и дословным именем убранного токена. Без вырезания комментариев гейт запрещал бы
     * не возврат правила, а рассказ о том, почему его убрали.
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

    private static String ruleBody(String css, String selector) {
        Matcher rule = Pattern.compile(Pattern.quote(selector) + "\\s*\\{([^}]*)}").matcher(css);
        assertThat(rule.find())
                .as("нет правила `%s` — селектор переименовали или удалили; контракт надо "
                        + "переписать под факт, а не подогнать факт под контракт", selector)
                .isTrue();
        return rule.group(1).replaceAll("\\s+", " ");
    }

    @Test
    void resultBandHasNoPersonalWidthCap() {
        assertThat(declarationsOnly(read(TOKENS)))
                .as("вернулся токен --max-width-result. Это был персональный замороженный кап "
                        + "ПОЛОСЫ страницы разбора: 1152px на 1440, 1512, 1680, 1728, 1920 и 2560, "
                        + "мёртвая ширина по борту доходила до 594px, а рабочая колонка при этом "
                        + "сужалась с ростом окна (637px@1366 → 592px@2560), потому что растущий "
                        + "жёлоб вычитался из неё. Ширину полосы задаёт общая шкала "
                        + "--max-width-page, свою меру страница считает из колонок (§24)")
                .doesNotContain("--max-width-result");

        assertThat(declarationsOnly(read(RESULT)))
                .as("на полосу страницы разбора вернулся собственный max-width или display. "
                        + "Полоса принадлежит шеллу и идёт по общей шкале; композиция — отдельный "
                        + "бокс .result-layout. Пока композицией была сама .ed-page, кап полосы и "
                        + "порог грида жили независимо и противоречили друг другу")
                .doesNotContainPattern("\\.result-page \\.ed-page\\s*\\{[^}]*(max-width|display)\\s*:");
    }

    @Test
    void columnCountIsChosenByContentNotByAMediaLiteral() {
        String css = read(RESULT);

        Matcher wide = Pattern.compile("@media\\s*\\([^)]*min-width[^)]*\\)").matcher(declarationsOnly(css));
        assertThat(wide.find())
                .as("в pages-result.css снова появился медиазапрос по min-width. Именно так "
                        + "выбиралось число колонок до iter-69: `@media (min-width: 1200px)` при "
                        + "капе полосы 1152px — порог, который не мог соответствовать реальной "
                        + "ширине полосы, и на 1280 делил её на 558 + 352 при мере вариантов 960. "
                        + "Порог переноса обязан оставаться СУММОЙ базисов, чтобы правка меры "
                        + "колонки двигала его сама")
                .isFalse();

        assertThat(declarationsOnly(css))
                .as("композиция страницы разбора снова стала гридом. Grid не умеет переносить "
                        + "трек, а ровно перенос и делает порог считаемым: с гридом число колонок "
                        + "опять придётся называть литералом в медиазапросе")
                .doesNotContainPattern("\\.result-layout[^{]*\\{[^}]*display\\s*:\\s*grid");

        assertThat(ruleBody(declarationsOnly(css),
                        "html[data-design] .result-page .result-layout:has(> #result-related-questions)"))
                .as("композиция перестала быть flex-переносом — механизм разошёлся с экраном "
                        + "вопроса, хотя это два экрана одного сценария (FRD-003/007/018/019)")
                .contains("display: flex")
                .contains("flex-wrap: wrap")
                .contains("max-width: var(--result-measure)");
    }

    @Test
    void measureIsComputedFromTheCompositionOwnColumns() {
        String tokens = declarationsOnly(read(TOKENS));
        String result = declarationsOnly(read(RESULT));

        assertThat(tokens)
                .as("сумма --result-measure переехала в tokens.css (на :root). var() внутри "
                        + "значения кастомного свойства подставляется на элементе ОБЪЯВЛЕНИЯ — на "
                        + ":root сумма запечёт базовые слагаемые и перестанет видеть их "
                        + "переопределения в поддереве страницы (тот же дефект чинил iter-68 у "
                        + "--focus-measure)")
                .doesNotContain("--result-measure:");

        String page = ruleBody(result, "html[data-design] .result-page");

        assertThat(page)
                .as("порог переноса и потолок композиции стали литералами. Оба обязаны "
                        + "СЧИТАТЬСЯ из колонок: численно совпадающая константа молча разъезжается "
                        + "с колонками при первой же правке любого слагаемого")
                .containsPattern("--result-band-min:\\s*calc\\(")
                .containsPattern("--result-band-max:\\s*calc\\(")
                .contains("var(--result-main-measure)")
                .contains("var(--result-rail-gap)")
                .contains("var(--result-rail-min)")
                .contains("var(--result-rail-max)");

        Matcher measure = Pattern.compile("--result-measure:\\s*([^;]+);").matcher(page);
        assertThat(measure.find())
                .as("на .result-page нет объявления --result-measure — единственного места, где "
                        + "сказано, какой ширины композиция разбора")
                .isTrue();

        assertThat(measure.group(1).replaceAll("\\s+", " ").trim())
                .as("мера композиции перестала схлопываться к своей рабочей колонке. Обе ветви "
                        + "обязательны: сумма, пока она влезает, и мера одной колонки, когда рельс "
                        + "уже перенесён вниз. Без max() коробка держит сумму и в перенесённом "
                        + "состоянии — мёртвое поле по борту при визуально одноколоночной странице")
                .contains("var(--result-main-measure)")
                .contains("var(--result-band-max)")
                .contains("var(--result-band-min)")
                .containsPattern("max\\(")
                .containsPattern("min\\(")
                .contains("100%")
                .contains("9999");
    }

    /**
     * Мера рабочей колонки — не литерал, а мера вариантов плюс поля карточки.
     *
     * <p>{@code #interview-options} внутри разбора несёт собственный
     * {@code max-width: var(--max-width-options)}. Пока колонка была уже — этот кап был мёртвым
     * объявлением (замер: варианты 528–573px при капе 960). Пока колонка шире — справа от
     * вариантов откроется мёртвое поле внутри карточки. Равенство «колонка = варианты + хром
     * карточки» единственное, при котором кап ровно достижим.
     */
    @Test
    void mainColumnMeasureIsDerivedFromTheOptionsMeasure() {
        String tokens = declarationsOnly(read(TOKENS));

        Matcher main = Pattern.compile("--result-main-measure:\\s*([^;]+);").matcher(tokens);
        assertThat(main.find())
                .as("в tokens.css нет --result-main-measure — мера рабочей колонки страницы "
                        + "разбора исчезла, и формула композиции считается из неопределённого токена")
                .isTrue();

        assertThat(main.group(1).replaceAll("\\s+", " ").trim())
                .as("мера рабочей колонки разошлась с мерой вариантов. Она обязана СЧИТАТЬСЯ из "
                        + "неё: варианты сидят внутри .card, которая съедает свои поля с обоих "
                        + "бортов, — иначе либо кап вариантов недостижим (был именно так), либо "
                        + "внутри карточки открывается мёртвое поле")
                .contains("var(--max-width-options)")
                .containsPattern("calc\\(");

        assertThat(tokens)
                .as("рельс снова описан одним токеном. Их обязано быть два: --result-rail-min — "
                        + "flex-базис, то есть ПОРОГ переноса, а --result-rail-max — потолок "
                        + "роста. Один токен на две роли даёт выбор из двух зол: с 22rem рельс "
                        + "встаёт рядом только с 1728 (мёртвая ширина на 1512/1680 хуже прежней), "
                        + "с 16rem он никогда не дорастает до полезной ширины")
                .contains("--result-rail-min:")
                .contains("--result-rail-max:");
    }

    /**
     * Оба экрана сценария «вопрос → разбор» переносят рельс на одном вьюпорте.
     *
     * <p>Пороги не обязаны совпадать до пикселя, но обязаны попадать в один интервал матрицы §10:
     * иначе пользователь на 1680 видит рельс на вопросе и не видит на разборе (или наоборот) —
     * страница «схлопывается» при переходе, хотя окно не менялось. Проверяем не числа, а то, что
     * суммы построены из одних и тех же по смыслу слагаемых и потому двигаются согласованно.
     */
    @Test
    void bothScreensOfTheFlowShareTheSameThresholdMechanism() {
        String focus = declarationsOnly(read(FOCUS));
        String result = declarationsOnly(read(RESULT));

        assertThat(focus)
                .as("экран вопроса перестал считать порог суммой базисов — механизмы двух экранов "
                        + "одного сценария разошлись")
                .containsPattern("--focus-band-sum:\\s*calc\\(");

        assertThat(result)
                .as("страница разбора перестала считать порог суммой базисов — см. выше")
                .containsPattern("--result-band-min:\\s*calc\\(");

        String focusMain = Pattern.compile("--focus-main-measure:\\s*([^;]+);")
                .matcher(declarationsOnly(read(TOKENS))).results().findFirst()
                .map(m -> m.group(1).trim()).orElse("<нет>");
        assertThat(focusMain)
                .as("рабочие колонки двух экранов сценария считаются от разных мер — пороги "
                        + "переноса разъедутся, и при переходе «вопрос → разбор» на одном и том же "
                        + "окне число колонок изменится само собой")
                .isEqualTo("var(--max-width-options)");
    }

    /**
     * Порядок чтения задаёт DOM, а не {@code order}.
     *
     * <p>Тот же инвариант, что гейтит {@code FocusWideCompositionContractTest}: рельс идёт вторым
     * и в ряду, и в переносе. Замер табуляции (WebKit, 1920): весь рельс после всей карточки,
     * положительных {@code tabindex} нет, {@code order} у обоих детей 0.
     */
    @Test
    void railIsSecondByDomOrderInBothStates() {
        assertThat(declarationsOnly(read(RESULT)))
                .as("в композиции разбора появился `order`. Визуальный порядок разъедется с "
                        + "порядком чтения: клавиатура и скринридер пойдут по DOM, а глаз — по "
                        + "перестановке")
                .doesNotContainPattern("\\.result-layout[^{]*\\{[^}]*[^-\\w]order\\s*:");

        String template = read(TEMPLATE);
        assertThat(template.indexOf("id=\"interview-card\""))
                .as("в result.html разбор перестал идти перед рельсом. Порядок чтения задаётся "
                        + "здесь и только здесь — CSS его не переставляет намеренно")
                .isPositive()
                .isLessThan(template.indexOf("id=\"result-related-questions\""));

        assertThat(template)
                .as("исчезла обёртка .result-layout. Без неё композицией снова становится сама "
                        + "полоса .ed-page — а полоса принадлежит шеллу и идёт по общей шкале; "
                        + "именно совмещение двух ролей в одном боксе и породило конфликт капа "
                        + "полосы с порогом грида")
                .contains("class=\"result-layout\"");
    }

    /**
     * Внутри композиции нет вложенного скролл-контейнера.
     *
     * <p>Рельс нёс {@code max-height: calc(100dvh - …)} и {@code overflow-y: auto} — вложенный
     * скроллер внутри страницы, отдельный источник дефектов в Safari (§11: nested scroll
     * containers, {@code 100dvh}). Список похожих вопросов короткий (замер: 3 ссылки, 270–352px
     * ширины, высота много меньше вьюпорта) — ограничение решало проблему, которой у него нет.
     */
    @Test
    void railIsNotANestedScrollContainer() {
        String rail = ruleBody(declarationsOnly(read(RESULT)),
                "html[data-design] .result-page .result-layout:has(> #result-related-questions) "
                        + "> #result-related-questions");

        assertThat(rail)
                .as("рельсу вернули вложенный скролл. В Safari это отдельный класс дефектов "
                        + "(§11), а список похожих вопросов короче вьюпорта — ограничивать нечего")
                .doesNotContain("overflow-y")
                .doesNotContain("max-height");

        assertThat(rail)
                .as("рельс перестал держать min-width: 0. Во flex это единственное, что снимает "
                        + "min-content-пол: длинный вопрос без пробелов начнёт распирать ряд и "
                        + "уводить композицию в горизонтальный скролл (§11)")
                .contains("min-width: 0");

        assertThat(rail)
                .as("рельс потерял собственный потолок. В ПЕРЕНЕСЁННОМ состоянии его ограничивает "
                        + "только этот max-width; без него панель растянется на всю меру "
                        + "композиции и разойдётся бортом с разбором, под которым стоит")
                .contains("max-width: var(--result-main-measure)");
    }
}
