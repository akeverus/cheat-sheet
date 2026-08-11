package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Ширина двухпейновой композиции экрана вопроса считается из того же, чем капнуты её дети.
 *
 * <h2>Найденный дефект, а не гипотеза</h2>
 * Правило {@code .focus-training:has(.session-progress-track)} (PROC-07) стояло с
 * комментарием «НЕ уходим в edge-to-edge, иначе между капнутыми вариантами и рельсом зияет
 * мёртвый центр на &gt;1900px» — и делало ровно обратное: {@code max-width: none; margin: 0}.
 * Капа у {@code .ed-page} нет, поэтому грид растягивался на всю рабочую область, а
 * {@code #interview-options} оставался на своём {@code --max-width-options}. Замер в живом
 * приложении на 2560×1440 (сессия EXAM, раскладка flow): грид 2165px, главная колонка
 * 1829px, варианты 960px — <b>869px пустоты между вариантами и рельсом</b>. Ровно то, что
 * комментарий считал уже предотвращённым.
 *
 * <p>Класс дефекта — «комментарий разошёлся с правилом». Он не ловится ни скриншотом одной
 * ширины (на 1440 дыра всего ~90px и читается как воздух), ни
 * {@link VisualBaselineContractTest} (тот сверяет разметку, а дыра — чистая геометрия), ни
 * глазами по коду (правило выглядит осмысленно, пока не сопоставишь его с капом ребёнка,
 * который живёт в другом селекторе и другом файле).
 *
 * <h2>Что именно закреплено</h2>
 * Инвариант: <b>кап контейнера = кап вариантов + жёлоб + рельс</b>. Тогда {@code 1fr}
 * главной колонки никогда не перерастает {@code --max-width-options} и дыре взяться неоткуда
 * ни на одной ширине. Проверяем это структурно — по составу выражения, а не по
 * вычисленному числу: реализовывать в тесте вычислитель CSS calc значило бы проверять свою
 * же арифметику. Если хоть один из трёх токенов уедет (или кап снова «отпустят» в
 * {@code none}), тождество сломается и тест это назовёт.
 *
 * <p>Это <b>не</b> общее правило «каждый grid обязан быть капнут суммой детей» — но и не
 * единичный случай. Соседняя раскладка {@code [data-layout="split"]} пришла к тому же
 * тождеству своим путём и держится отдельным контрактом
 * ({@link FocusSplitCompositionContractTest}); до iter-58 её кап был литералом, численно
 * совпадавшим с суммой колонок по случайности. Гейт нужен там, где ширина контейнера и
 * ширина его детей заданы в разных местах: расхождение между ними и есть дыра.
 *
 * <h2>iter-68: тождество держалось, а колонка всё равно недобирала меру</h2>
 * Тождество «кап = колонка + жёлоб + рельс» ловит дыру ВНУТРИ композиции, но ничего не
 * говорит о том, что происходит, когда полоса УЖЕ этого капа. Композиция была гридом на
 * двух треках под литералом {@code @media (min-width: 1080px)}, а оплатить обе колонки их
 * мерами полоса может только с 1296px — на этой странице примерно с вьюпорта 1610. В
 * зазоре рельс забирал свои 288px из главной колонки, потому что <b>grid не переносит
 * трек</b>: если колонок две, они есть на любой ширине, и не хватает ровно первой.
 * Замер WebKit@2 (ширина главной колонки против её меры 960px): 492px на 1080, 631 на
 * 1280, 774 на 1440, ~830 на 1512 — весь диапазон MacBook держал главную колонку продукта
 * ниже проектной меры, тем сильнее, чем уже экран.
 *
 * <p>Починка — смена механики, а не сдвиг литерала: {@code flex-wrap} с {@code flex-basis}
 * из тех же токенов. Рельс стоит рядом, пока строка вмещает обе меры, и уезжает своей
 * строкой под колонку, когда не вмещает (§12). Порог не назван нигде — он и есть сумма
 * basis'ов, поэтому правка любой меры двигает его сама. Поэтому ниже проверяются
 * {@code flex}-величины, а не {@code grid-template-columns}, и отдельно — отсутствие
 * медиазапроса-гейта: вернуть литерал значит вернуть весь класс дефекта.
 *
 * <h2>iter-57: гейт композиции — про содержимое рельса, а не про сессию</h2>
 * Второй закреплённый здесь инвариант — <b>у кого композиция включается</b>. Гейт
 * {@code :has(...)} стоял только на сессионном треке, и вне сессии страница схлопывалась в
 * колонку 960px по центру: 363px пустоты с каждой стороны на 1920 и 683px (59% рабочей
 * области) на 2560. Причина была не в CSS: рельсу вне сессии нечего было показать, кроме
 * чипа очереди, и колонка выродилась бы в одинокую фишку в жёлобе.
 *
 * <p>Поэтому тесты ниже держат гейт и его содержимое <b>вместе</b>: колонка обязана
 * включаться в обоих состояниях рельса, но только потому, что в обоих состояниях в рельсе
 * есть чем её наполнить. Снять гейт «просто так», не дав рельсу контента, эти тесты не
 * позволят — а именно так и выглядел бы запрещённый мандатом «столбец ради заполнения места».
 */
class FocusWideCompositionContractTest {

    private static final Path FOCUS_CSS = Path.of("src/main/resources/static/css/pages-focus.css");
    private static final Path TOKENS = Path.of("src/main/resources/static/css/tokens.css");
    private static final Path FOCUS_TEMPLATE = Path.of("src/main/resources/templates/focus-training.html");

    /** Тело правила двухпейновой композиции (PROC-07), гейтнутого на непустой рельс. */
    private static final Pattern WIDE_RULE = Pattern.compile(
            "\\.focus-page \\.focus-training:has\\(([^)]*)\\)\\s*\\{([^}]*)}");

    /** Тело правила рабочей колонки композиции. */
    private static final Pattern MAIN_RULE = Pattern.compile(
            "\\.focus-page \\.focus-training:has\\([^)]*\\) > \\.focus-main\\s*\\{([^}]*)}");

    /** Тело правила вспомогательного рельса. */
    private static final Pattern ASIDE_RULE = Pattern.compile(
            "\\.focus-page \\.focus-training:has\\([^)]*\\) > \\.focus-aside\\s*\\{([^}]*)}");

    private static final Pattern DECLARATION_MAX_WIDTH = Pattern.compile("max-width:\\s*([^;]+);");
    private static final Pattern DECLARATION_FLEX = Pattern.compile("flex:\\s*([^;]+);");
    private static final Pattern DECLARATION_GAP = Pattern.compile("column-gap:\\s*([^;]+);");

    private Matcher wideRule() {
        Matcher matcher = WIDE_RULE.matcher(read(FOCUS_CSS));
        assertThat(matcher.find())
                .as("в pages-focus.css нет правила .focus-page .focus-training:has(...) — "
                        + "двухпейновую композицию переименовали или сняли, контракт надо переписать под факт")
                .isTrue();
        return matcher;
    }

    private String wideRuleBody() {
        return wideRule().group(2);
    }

    private static String ruleBody(String css, Pattern pattern, String what) {
        Matcher matcher = pattern.matcher(css);
        assertThat(matcher.find())
                .as("в pages-focus.css нет правила %s — композицию переписали, контракт надо "
                        + "переписать под факт", what)
                .isTrue();
        return matcher.group(1);
    }

    private static String declaration(String body, Pattern pattern, String property) {
        Matcher matcher = pattern.matcher(body);
        assertThat(matcher.find())
                .as("в правиле двухпейновой композиции нет объявления %s", property)
                .isTrue();
        return matcher.group(1).trim();
    }

    /**
     * Разворачивает {@code var(--focus-measure)} в объявление токена из tokens.css.
     *
     * <p>iter-67 вынес сумму колонок в токен: мерой полосы пользуется не только композиция, но и
     * resume-баннер с пустым состоянием — до этого меру знала ОДНА композиция, и кромки трёх
     * состояний одного экрана расходились до 442px с борта на вьюпорте 2560. Гарантия
     * контракта при этом не ослабла, а стала на одну индирекцию глубже: кап по-прежнему обязан
     * СЧИТАТЬСЯ из треков, просто теперь считается в токене. Поэтому здесь значение
     * разворачивается, а не проверяется буквально: подстановка литерала вместо суммы всё так же
     * валит тест — и в композиции, и в самом токене.
     *
     * <p>iter-68 добавил ВТОРУЮ индирекцию: слагаемое «рабочая колонка» вынесено в
     * {@code --focus-main-measure}, потому что у раскладки «Разделение» рабочая колонка сама
     * двухпейновая и её мера другая — а формула полосы обязана остаться одна на все раскладки
     * (иначе рельс снова выпадет из суммы у той раскладки, что переопределит меру целиком:
     * ровно это и делал split до iter-68). Разворачиваем обе ступени.
     *
     * <p>Ступени лежат в разных файлах, и это тоже часть контракта: сумма — на
     * {@code .focus-page} в pages-focus.css (там же, где раскладка переопределяет слагаемое,
     * иначе подстановка запекает базовое значение), слагаемое — токеном в tokens.css.
     *
     * <p>iter-68 добавил ТРЕТЬЮ ступень: мера полосы схлопывается к мере рабочей колонки,
     * когда сумма колонок в полосу не влезает, поэтому сама сумма живёт отдельным токеном
     * {@code --focus-band-sum} — её значение нужно дважды (как верхняя граница и как порог
     * сравнения). Разворачиваем все три: подстановка литерала на любой ступени валит тест.
     */
    private static String resolveMeasure(String value) {
        String resolved = resolveToken(value, "--focus-measure", read(FOCUS_CSS),
                "композиция берёт кап из --focus-measure, но в pages-focus.css такого объявления "
                        + "нет: мера полосы объявлена где-то ещё или исчезла — контракт надо "
                        + "переписать под факт");
        resolved = resolveToken(resolved, "--focus-band-sum", read(FOCUS_CSS),
                "мера полосы ссылается на --focus-band-sum, но сумма колонок в pages-focus.css "
                        + "не объявлена: у схлопывающейся ветви пропала величина, с которой она "
                        + "сравнивает ширину полосы");
        return resolveToken(resolved, "--focus-main-measure", read(TOKENS),
                "мера полосы считается из --focus-main-measure, но сам токен в tokens.css не "
                        + "объявлен — базовое слагаемое «рабочая колонка» исчезло из формулы");
    }

    private static String resolveToken(String value, String token, String source, String absenceMessage) {
        String reference = "var(" + token + ")";
        if (!value.contains(reference)) {
            return value;
        }
        Matcher declaration = Pattern.compile(Pattern.quote(token) + ":\\s*([^;]+);")
                .matcher(source.replaceAll("(?s)/\\*.*?\\*/", ""));
        assertThat(declaration.find()).as(absenceMessage).isTrue();
        return value.replace(reference, declaration.group(1).trim());
    }

    @Test
    void wideCompositionCapIsSumOfItsOwnColumns() {
        String maxWidth = resolveMeasure(declaration(wideRuleBody(), DECLARATION_MAX_WIDTH, "max-width"));

        assertThat(maxWidth)
                .as("кап композиции снова отпущен — главная колонка перерастёт --max-width-options "
                        + "вариантов, и между ними и рельсом откроется мёртвый центр (замер: 869px на 2560)")
                .isNotEqualTo("none");

        assertThat(maxWidth)
                .as("кап композиции обязан складываться из тех же трёх величин, что определяют "
                        + "ширину её колонок, иначе тождество «1fr == кап вариантов» держится случайно")
                .contains("var(--max-width-options)")
                .contains("var(--focus-rail-gap)")
                .contains("var(--focus-rail-width)");
    }

    @Test
    void wideCompositionColumnsUseTheSameTokensAsItsCap() {
        String css = read(FOCUS_CSS);
        String main = ruleBody(css, MAIN_RULE, "рабочей колонки композиции (> .focus-main)");
        String aside = ruleBody(css, ASIDE_RULE, "вспомогательного рельса композиции (> .focus-aside)");

        assertThat(declaration(main, DECLARATION_FLEX, "flex"))
                .as("basis рабочей колонки задан мимо --focus-main-measure — кап полосы считает "
                        + "одну её меру, а строка раскладывается по другой, и рельс либо не "
                        + "поместится там, где полоса за него уже заплатила, либо влезет туда, "
                        + "где платить нечем (замер до iter-68: колонка 631px при мере 960 на 1280)")
                .contains("var(--focus-main-measure)");
        assertThat(declaration(main, DECLARATION_MAX_WIDTH, "max-width"))
                .as("кап рабочей колонки задан мимо --focus-main-measure — при перенесённом "
                        + "рельсе строка целиком её, и колонка растянет текст за меру чтения")
                .isEqualTo("var(--focus-main-measure)");

        assertThat(declaration(aside, DECLARATION_FLEX, "flex"))
                .as("basis рельса задан мимо --focus-rail-width — кап полосы считает одну ширину "
                        + "рельса, а строка резервирует другую, и разница уходит в дыру")
                .contains("var(--focus-rail-width)");

        assertThat(declaration(wideRuleBody(), DECLARATION_GAP, "column-gap"))
                .as("жёлоб задан мимо --focus-rail-gap — кап контейнера считает один жёлоб, "
                        + "а строка рисует другой")
                .isEqualTo("var(--focus-rail-gap)");
    }

    /**
     * Композиция обязана переключаться суммой мер, а не медиазапросом-литералом.
     *
     * <p>Разница не косметическая. Порог, выраженный литералом, верен ровно до первой правки
     * любой из трёх мер и молча расходится с ними: {@code @media (min-width: 1080px)} включал
     * две колонки там, где полоса могла оплатить только одну (сумма мер = 1296px, то есть
     * примерно вьюпорт 1610), и рельс семь вьюпортов из десяти забирал недостающее из главной
     * колонки. Порог, выраженный суммой {@code flex-basis}, двигается сам вслед за токенами и
     * разойтись с ними не может.
     *
     * <p>Отдельно закреплено {@code flex-wrap}: без него строка не переносится, оба ребёнка
     * остаются рядом на любой ширине — и это ровно то же поведение, что у грида, то есть тот
     * же дефект под другим свойством.
     */
    @Test
    void wideCompositionThresholdComesFromMeasuresNotFromMediaLiteral() {
        String body = wideRuleBody();

        assertThat(body)
                .as("композиция снова раскладывается гридом: grid не умеет ПЕРЕНОСИТЬ трек, "
                        + "поэтому обе колонки существуют на любой ширине и рельс оплачивается "
                        + "из главной колонки везде, где полоса уже суммы мер")
                .doesNotContain("grid-template-columns");
        assertThat(body)
                .as("у композиции нет flex-wrap — рельс не сможет уехать под рабочую колонку, "
                        + "и на узкой полосе снова начнёт отъедать её меру")
                .contains("flex-wrap: wrap");

        String css = read(FOCUS_CSS).replaceAll("(?s)/\\*.*?\\*/", "");
        int at = css.indexOf(".focus-training:has(");
        assertThat(at)
                .as("в pages-focus.css больше нет селектора композиции .focus-training:has(...)")
                .isNotEqualTo(-1);
        assertThat(insideMediaQuery(css, at))
                .as("композиция снова закрыта медиазапросом: литерал совпадает с суммой мер лишь "
                        + "до первой правки любой из них, и в зазоре рельс отбирает у главной "
                        + "колонки её меру — именно этот дефект iter-68 и снял")
                .isFalse();
    }

    /**
     * Стоит ли позиция {@code index} внутри открытого блока {@code @media}.
     *
     * <p>Считаем скобками, а не регуляркой: наивное «@media … .focus-training» матчится через
     * закрытые блоки и врёт в обе стороны. Файл целиком завёрнут в {@code @layer pages} — этот
     * блок в стек попадает как «не медиазапрос», поэтому не мешает.
     */
    private static boolean insideMediaQuery(String css, int index) {
        Deque<Boolean> open = new ArrayDeque<>();
        StringBuilder selector = new StringBuilder();
        for (int i = 0; i < index; i++) {
            char c = css.charAt(i);
            if (c == '{') {
                open.push(selector.indexOf("@media") >= 0);
                selector.setLength(0);
            } else if (c == '}') {
                if (!open.isEmpty()) {
                    open.pop();
                }
                selector.setLength(0);
            } else {
                selector.append(c);
            }
        }
        return open.contains(Boolean.TRUE);
    }

    @Test
    void wideCompositionGateCoversBothRailStates() {
        String gate = wideRule().group(1);

        assertThat(gate)
                .as("гейт композиции не включает трек прогресса сессии — в сессии рельс есть, "
                        + "а правой колонки не будет")
                .contains(".session-progress-track");
        assertThat(gate)
                .as("гейт композиции стоит только на сессии — вне сессии страница схлопывается "
                        + "в колонку 960px по центру (замер: 683px пустоты с каждой стороны на 2560)")
                .contains(".focus-bank-progress");
    }

    @Test
    void railOutsideSessionActuallyCarriesContent() {
        String template = read(FOCUS_TEMPLATE);

        assertThat(template)
                .as("вне сессии рельсу нечего показать, а гейт композиции его уже впускает — "
                        + "это ровно «вторая колонка ради заполнения места», которую мандат запрещает")
                .contains("interviewSession == null and metrics != null")
                .contains("focus-bank-progress");
        assertThat(template)
                .as("блок прогресса по банку читает поля, которых нет в InterviewStats — если "
                        + "они пропадут из шаблона, рельс вне сессии снова выродится в один чип")
                .contains("metrics.cardsSeen()")
                .contains("metrics.cardsTotal()")
                .contains("metrics.cardsMastered()");
    }

    /**
     * Метрики обязаны доезжать до модели вида целиком, а не через {@code InterviewStats}.
     *
     * <p>{@code InterviewStats.from(LearningMetrics)} — view-adapter на 6 полей из 14:
     * {@code cardsSeen} и точность в него не проходят. Ровно поэтому рельсу вне сессии
     * «нечего было показать» — данные считались на каждом рендере и терялись в горлышке.
     * Если {@code metrics} снова уберут из {@code SurfaceState} или из модели, шаблонное
     * условие тихо станет ложным, блок исчезнет, а гейт композиции останется — то есть
     * вернётся дефект, но уже без видимой причины в CSS.
     */
    @Test
    void metricsReachTheViewModelUnreduced() {
        String surface = read(Path.of("src/main/java/com/cheatsheet/quiz/feature/interview/"
                + "service/page/FocusTrainingPageService.java"));
        String mapper = read(Path.of("src/main/java/com/cheatsheet/quiz/api/mapper/view/"
                + "MvcModelAttributeMapper.java"));

        assertThat(surface)
                .as("LearningMetrics снова не входит в SurfaceState — рельс вне сессии останется "
                        + "без данных, хотя метрики по-прежнему считаются на каждом рендере")
                .contains("LearningMetrics metrics");
        assertThat(mapper)
                .as("metrics не кладутся в модель экрана вопроса — шаблонное условие блока "
                        + "прогресса по банку станет ложным молча")
                .contains("model.addAttribute(\"metrics\", surfaceState.metrics())");
    }

    @Test
    void railTokensAreDefinedOnce() {
        String tokens = read(TOKENS);

        assertThat(countDefinitions(tokens, "--focus-rail-width"))
                .as("--focus-rail-width должен быть определён ровно один раз: второе определение "
                        + "рассинхронит кап композиции с шириной её же колонки")
                .isEqualTo(1);
        assertThat(countDefinitions(tokens, "--focus-rail-gap"))
                .as("--focus-rail-gap должен быть определён ровно один раз")
                .isEqualTo(1);
        assertThat(countDefinitions(tokens, "--focus-main-measure"))
                .as("--focus-main-measure должен иметь ровно одно БАЗОВОЕ определение в "
                        + "tokens.css: раскладки переопределяют его у себя (pages-focus.css), "
                        + "а второе определение здесь рассинхронит кап полосы с basis колонки")
                .isEqualTo(1);
    }

    /**
     * У композиции ровно два ребёнка — иначе перенос рельса невыразим.
     *
     * <p>{@code flex-wrap} переносит ЭЛЕМЕНТЫ строки. Пока прямых детей у
     * {@code .focus-training} было восемь (рельс, топбар, причина, вопрос, материалы, форма,
     * пост-ответная лента, зона репорта), перенести можно было только их все вперемешку —
     * поэтому раскладка и была гридом с явным {@code grid-column: 1} у семи из них. Обёртка
     * {@code .focus-main} — не косметика, а условие работоспособности правил выше: если её
     * снимут, рельс останется в одной строке с топбаром, а вопрос уедет на следующую.
     */
    @Test
    void compositionHasExactlyTwoChildrenInTemplate() {
        String template = read(FOCUS_TEMPLATE);

        assertThat(template)
                .as("в шаблоне нет обёртки рабочей колонки .focus-main — flex-переносу нечего "
                        + "переносить целиком, и содержимое колонки расползётся по строкам")
                .contains("class=\"focus-main\"");
        assertThat(template)
                .as("в шаблоне нет .focus-question-pane — колонке вопроса раскладки "
                        + "«Разделение» не за что зацепиться")
                .contains("class=\"focus-question-pane\"");
        assertThat(template)
                .as("в шаблоне остался старый класс focus-rail: имя сталкивалось с настоящим "
                        + "рельсом .focus-aside, а две конкурирующие сущности под одним словом — "
                        + "это то, что §24 запрещает оставлять. (focus-session-rail — другой "
                        + "класс, он на самом рельсе и остаётся.)")
                .doesNotContain("\"focus-rail\"")
                .doesNotContain("focus-rail ")
                .doesNotContain(" focus-rail");
    }

    /**
     * Перенесённый рельс обязан оставаться ПЕРВЫМ в строке — над рабочей колонкой.
     *
     * <p>Замер (WebKit@2, 1440×900): «над» ставит полосу прогресса на 215px, заголовок
     * вопроса на 397px; «под» отправило бы рельс на ~1460px, в самый низ страницы, и
     * первый экран потерял бы контекст сессии, выиграв 109px — первый вариант ответа
     * всё равно за сгибом, потому что 634px между заголовком и вариантами занимают сам
     * вопрос (h1 135px) и блок примера кода (423px), а не рельс. §8 требует контекст на
     * первом экране, поэтому порядок — разметочный, а {@code order} в композиции
     * запрещён: он переставляет визуальный порядок, не трогая разметку, и уронил бы
     * прогресс в подвал молча.
     */
    @Test
    void railStaysFirstInWrappedRow() {
        String template = read(FOCUS_TEMPLATE);
        int aside = template.indexOf("focus-aside");
        int main = template.indexOf("class=\"focus-main\"");

        assertThat(aside).as("в шаблоне нет .focus-aside").isNotNegative();
        assertThat(aside)
                .as("рельс .focus-aside съехал ПОСЛЕ рабочей колонки: при переносе он окажется "
                        + "второй строкой, то есть под колонкой высотой ~1234px — прогресс сессии "
                        + "уедет в подвал страницы и уйдёт с первого экрана (§8)")
                .isLessThan(main);

        String css = read(FOCUS_CSS);
        assertThat(ruleBody(css, MAIN_RULE, "правило рабочей колонки"))
                .as("в правиле рабочей колонки появился order — визуальный порядок разошёлся "
                        + "с разметочным, и перенос перестал быть предсказуемым")
                .doesNotContain("order:");
        assertThat(ruleBody(css, ASIDE_RULE, "правило рельса"))
                .as("в правиле рельса появился order — рельс уедет под рабочую колонку, "
                        + "а с ним и контекст сессии (§8)")
                .doesNotContain("order:");
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
