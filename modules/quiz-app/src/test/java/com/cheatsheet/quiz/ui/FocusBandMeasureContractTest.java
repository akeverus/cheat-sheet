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
 * У рабочей полосы экрана вопроса ОДНА мера и ОДНА кромка на все её состояния.
 *
 * <p>В шаблоне {@code focus-training.html} у контейнера полосы ровно три прямых потомка, и они
 * взаимоисключающие по смыслу, но не по вёрстке: resume-баннер приостановленной сессии
 * ({@code pausedInfo != null}), композиция вопроса ({@code current != null}) и пустое состояние
 * ({@code current == null}). Баннер соседствует с любым из двух остальных. Это один экран,
 * и его левая и правая кромки обязаны совпадать во всех трёх сочетаниях.
 *
 * <h2>Найденные дефекты, а не гипотезы (iter-67, замеры WebKit@2 по матрице §10)</h2>
 *
 * <ol>
 *   <li><b>Главный экран продукта был отвязан от общей шкалы полосы.</b>
 *       {@code .focus-page .ed-page} нёс {@code max-width: 100%}, перебивая базовый
 *       {@code max-width: var(--max-width-page)} = {@code min(100%, 112rem)}. Комментарий объяснял
 *       строку защитой от переполнения длинными {@code pre}, но защиту держат её соседи
 *       ({@code min-width: 0} снимает min-content-пол грид-элемента, {@code overflow-x: clip}
 *       обрезает вылезшее). Сама строка ниже 1792px тождественна {@code 100%} — no-op, а выше
 *       снимала кап: замер на 2560 дал полосу 2340px против 1792 на {@code /stats},
 *       {@code /bookmarks}, {@code /settings} и странице ошибки — всех, кто {@code AUDITED} по
 *       этой шкале (FRD-015). Классический случай §2: решение «основано на неверном
 *       предположении».</li>
 *   <li><b>Две кромки на одной странице.</b> Меру знала только композиция
 *       ({@code max-width: 1296px} из своих колонок, FRD-003), а баннер и пустое состояние шли по
 *       ширине полосы. Расхождение росло ровно с того вьюпорта, где композиция упиралась в кап:
 *       0px до 1512, затем 15 (1680), 37 (1728), 125 (1920) и 442px (2560) с КАЖДОГО борта.
 *       Кнопка «Продолжить» стояла на 442px правее правого края колонки ответов, к которой
 *       относится.</li>
 *   <li><b>Пустое состояние — рамка вокруг пустоты.</b> Правило {@code max-width: none} в блоке
 *       {@code >=1200px} растягивало карточку до 2180px при содержимом (маскот 72px, заголовок,
 *       строка текста, две кнопки) около 450px, причём с бордером и фоном, которые эту пустоту
 *       подчёркивают, и с 1110px мёртвого фона под ней. §6 дословно: «пустые панели» и «одна
 *       маленькая карточка на огромном canvas».</li>
 *   <li><b>Ниже 1200px карточка расходилась с баннером снова.</b> Базовым потолком было 560px, а
 *       снимало его только правило {@code >=1200px}. В зазоре 610–1199 работала база: 46px
 *       расхождения на 700, 80 на 768, 98 на 1024, 113 на 834 и до 161px на 1150. В этот
 *       диапазон попадают планшеты, окно на половину широкого экрана и зум 150–200%.</li>
 *   <li><b>Композиция с другой суммой колонок несла свою меру, о которой знала только она.</b>
 *       На 1920 такая композиция стояла 1229px, тогда как баннер шёл по 1296px общей меры —
 *       те же 34px с борта. Носителем этого дефекта была раскладка «Разделение»; она удалена
 *       (iter-102), но инвариант остаётся: переопределять можно СЛАГАЕМОЕ
 *       ({@code --focus-main-measure}), а не формулу суммы.</li>
 * </ol>
 *
 * <h2>Почему это гейт, а не разовая правка</h2>
 * Все пять — один класс дефекта: ширину знает КОМПОНЕНТ, а принадлежит она СТРАНИЦЕ. Каждое
 * правило по отдельности осмысленно, и ни одно не видно на скриншоте одной ширины — расхождение
 * начинается там, где композиция упирается в свой кап. Решение структурное: мера объявлена
 * токеном {@code --focus-measure} (сумма собственных колонок композиции), живёт на предке и
 * читается всеми тремя потомками; раскладка, у которой сумма колонок другая, переопределяет сам
 * токен, а не свой кап.
 *
 * <p>Ширины баннера и пустого состояния заданы через {@code width: min(100%, …)}, а не
 * {@code max-width}: в состоянии «вопросов нет» полоса становится flex-колонкой (вертикальное
 * центрирование), а в ней cross-axis stretch НЕ применяется к элементу с auto-полями по той же
 * оси. Замер с {@code max-width}: карточка схлопнулась до fit-content, 854px вместо меры полосы
 * на ВСЕХ вьюпортах. Тем же приёмом держится {@code .error-card} (iter-66) — гейт ниже
 * запрещает вернуться к {@code max-width} обратно.
 */
class FocusBandMeasureContractTest {

    private static final Path CSS_DIR = Path.of("src/main/resources/static/css");
    private static final Path FOCUS = CSS_DIR.resolve("pages-focus.css");
    private static final Path TOKENS = CSS_DIR.resolve("tokens.css");

    /**
     * Объявления без комментариев.
     *
     * <p>Каждый снятый дефект оставил после себя комментарий с причиной и замером — то есть с
     * дословными {@code max-width: 100%}, {@code max-width: none} и именами токенов. Без
     * вырезания комментариев гейт запрещал бы не возврат правила, а рассказ о том, почему его
     * убрали.
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

    private static int countOccurrences(String haystack, String needle) {
        int count = 0;
        for (int i = haystack.indexOf(needle); i >= 0; i = haystack.indexOf(needle, i + needle.length())) {
            count++;
        }
        return count;
    }

    /** Тело правила по точному селектору; падает внятно, если селектор переименовали. */
    private static String ruleBody(String css, String selector) {
        Matcher rule = Pattern.compile(Pattern.quote(selector) + "\\s*\\{([^}]*)}").matcher(css);
        assertThat(rule.find())
                .as("в pages-focus.css нет правила `%s` — селектор переименовали или удалили, "
                        + "контракт надо переписать под факт, а не подогнать факт под контракт",
                        selector)
                .isTrue();
        return rule.group(1);
    }

    @Test
    void focusBandDoesNotOverrideThePageWidthScale() {
        String body = ruleBody(declarationsOnly(read(FOCUS)), "html[data-design] .focus-page .ed-page");

        assertThat(body)
                .as("на полосу экрана вопроса вернулся собственный `max-width`. Пока там стоял "
                        + "`max-width: 100%`, главная страница продукта шла по 2340px на вьюпорте "
                        + "2560, тогда как /stats, /bookmarks, /settings и страница ошибки — по "
                        + "1792px общей шкалы (FRD-015, все AUDITED). Ниже 1792 строка была "
                        + "тождественным no-op, то есть чинила она ровно ничего. Переполнение "
                        + "длинными pre держат соседние `min-width: 0` и `overflow-x: clip` — "
                        + "им персональный кап не нужен (§24: не две шкалы ширины)")
                .doesNotContain("max-width");

        assertThat(body)
                .as("из полосы пропала защита от переполнения. Обе строки обязательны: "
                        + "`min-width: 0` снимает min-content-пол грид-элемента, `overflow-x: clip` "
                        + "обрезает то, что всё же вылезло. Без них длинный `pre` в вопросе даёт "
                        + "горизонтальный скролл всей страницы на мобиле")
                .contains("min-width: 0")
                .contains("overflow-x: clip");
    }

    /**
     * Мера полосы считается из колонок — и объявлена там, где её слагаемое можно переопределить.
     *
     * <p>iter-68 перенёс объявление из {@code :root} (tokens.css) на {@code .focus-page}
     * (pages-focus.css). Причина замерена: {@code var()} внутри значения кастомного свойства
     * подставляется на элементе ОБЪЯВЛЕНИЯ, а вниз идёт уже подставленная строка. Пока сумма
     * стояла на {@code :root}, она запекала базовые 60rem, и переопределение
     * {@code --focus-main-measure} у раскладки «Разделение» (оно на {@code .focus-page}) в неё
     * не попадало: WebKit@2, 2560×1440, split — кап полосы 1296px против собственной меры
     * рабочей колонки 1312px, колонку поджимало на 16px, рельс не вставал рядом ни на одной
     * ширине. Слагаемые остаются токенами; переезжает только сумма.
     */
    @Test
    void bandMeasureIsATokenComputedFromTheCompositionOwnColumns() {
        String tokens = declarationsOnly(read(TOKENS));
        String focus = declarationsOnly(read(FOCUS));

        assertThat(tokens)
                .as("сумма --focus-measure вернулась в tokens.css (на :root). Там var() внутри неё "
                        + "подставляется значением :root и запекается навсегда — переопределение "
                        + "--focus-main-measure у раскладки в сумму не попадёт, и кап полосы "
                        + "разойдётся с мерой её же рабочей колонки (замер split@2560: 1296 против "
                        + "1312, рельс не вставал рядом никогда)")
                .doesNotContain("--focus-measure:");

        Matcher declaration = Pattern.compile(
                        "html\\[data-design] \\.focus-page\\s*\\{[^}]*--focus-measure:\\s*([^;]+);")
                .matcher(focus);
        assertThat(declaration.find())
                .as("в pages-focus.css нет объявления --focus-measure на .focus-page. Это "
                        + "единственное место, где сказано, какой ширины рабочая полоса экрана "
                        + "вопроса, и оно обязано стоять на том же элементе, где раскладки "
                        + "переопределяют слагаемое; без него меру снова придётся дублировать "
                        + "литералом в каждом из трёх состояний — именно так и разошлись кромки "
                        + "до iter-67")
                .isTrue();

        String value = declaration.group(1).replaceAll("\\s+", " ").trim();
        String sum = ruleBody(focus, "html[data-design] .focus-page").replaceAll("\\s+", " ");

        assertThat(sum)
                .as("сумма колонок полосы стала литералом. Она обязана СЧИТАТЬСЯ: рабочая "
                        + "колонка + жёлоб + рельс. Численно совпадающая константа — тот же класс "
                        + "дефекта, что PROC-07 лечил в WIDE: правка любого трека молча открывает "
                        + "мёртвый центр, потому что кап о ней не узнаёт")
                .containsPattern("--focus-band-sum:\\s*calc\\(")
                .contains("var(--focus-main-measure)")
                .contains("var(--focus-rail-gap)")
                .contains("var(--focus-rail-width)");

        assertThat(value)
                .as("мера полосы перестала схлопываться к своей колонке: `%s`. Пока мера была "
                        + "просто суммой, коробка композиции держала сумму и тогда, когда рельс уже "
                        + "ушёл своей строкой, — замер WebKit@2 до починки: 1440×900 коробка 1104 "
                        + "против видимой колонки 960 (72px мёртвого поля на борт), 1512×982 — 106, "
                        + "1600×900 — 146. Мёртвое поле само по себе невидимо, но по этой же мере "
                        + "встают resume-баннер и пустое состояние — и расходятся с вопросом "
                        + "бортами ровно там, где рельс перенесён (FRD-018, §6 «пустые панели»). "
                        + "Обе ветви обязательны: сумма, пока она влезает, и мера колонки, когда "
                        + "нет", value)
                .contains("var(--focus-main-measure)")
                .contains("var(--focus-band-sum)")
                .containsPattern("max\\(")
                .containsPattern("min\\(")
                .contains("100%");

        // iter-68 — слагаемое «рабочая колонка» вынесено в собственный токен, потому что у
        // раскладки «Разделение» колонка сама двухпейновая и её мера другая. Формула полосы
        // при этом обязана остаться ОДНА на все раскладки, иначе раскладка, переопределяющая
        // формулу целиком, вычёркивает из неё рельс (ровно это split и делал до iter-68).
        // Разворачиваем индирекцию: базовое слагаемое обязано остаться мерой вариантов.
        Matcher mainMeasure = Pattern.compile("--focus-main-measure:\\s*([^;]+);").matcher(tokens);
        assertThat(mainMeasure.find())
                .as("в tokens.css нет базового объявления --focus-main-measure — первое слагаемое "
                        + "меры полосы исчезло, и формула считается из неопределённого токена")
                .isTrue();
        assertThat(mainMeasure.group(1).trim())
                .as("базовая мера рабочей колонки разошлась с капом вариантов. Пока они равны, "
                        + "`#interview-options` наполняет колонку целиком; как только колонка "
                        + "станет шире — справа от вариантов откроется мёртвое поле, а уже — "
                        + "варианты обрежутся своим же капом")
                .isEqualTo("var(--max-width-options)");

        assertThat(countOccurrences(focus, "--focus-measure:"))
                .as("--focus-measure объявлен больше одного раза — это и есть две меры одной "
                        + "полосы, которые запрещено держать одновременно (§24). Композиции с "
                        + "другой суммой колонок место в переопределении СЛАГАЕМОГО "
                        + "(--focus-main-measure), а не во второй формуле суммы")
                .isEqualTo(1);
    }

    @Test
    void allThreeBandChildrenReadTheSameMeasure() {
        String css = declarationsOnly(read(FOCUS));

        assertThat(ruleBody(css, "html[data-design] .focus-page .resume-banner"))
                .as("resume-баннер перестал брать общую меру. Он говорит о той самой сессии, чьи "
                        + "вопросы стоят под ним, и обязан стоять с ними на одной кромке: по "
                        + "ширине полосы он расходился с композицией на 15px (1680), 37 (1728), "
                        + "125 (1920) и 442px (2560) с борта, уводя «Продолжить» за пределы "
                        + "колонки, к которой кнопка относится")
                .contains("var(--focus-measure)");

        assertThat(ruleBody(css, "html[data-design] .focus-page .empty-state"))
                .as("пустое состояние перестало брать общую меру. Это третье состояние ОДНОЙ "
                        + "полосы, и кромка у всех троих одна")
                .contains("var(--focus-measure)");

        assertThat(css)
                .as("композиция вопроса перестала брать общую меру: если кап снова литерал, "
                        + "мера раздвоится на «ту, что у токена» и «ту, что у композиции», а "
                        + "разойдутся при этом баннер и вопрос — как до iter-67")
                .containsPattern("\\.focus-training[^{]*\\{[^}]*max-width:\\s*var\\(--focus-measure\\)");
    }

    /**
     * Фолбэк без {@code :has()} стоит по той же мере — второй меры у полосы нет ни в одном движке.
     *
     * <p>iter-68: база {@code .focus-training} несла собственный кап {@code var(--max-width-focus)}.
     * Он совпадал с мерой рабочей колонки значением (обе 60rem), но не был с ней связан — правка
     * одной не двигала другую. Комментарий оправдывал кап двумя случаями: движок без {@code :has()}
     * и «рельс пуст». Второй случай оказался неверным предположением (§2), и это проверено по коду:
     * {@code metrics} в модели приходит из {@code LearningMetricsService.compute()}, который всегда
     * возвращает запись, а {@code MvcModelAttributeMapper.applySurfaceState} кладёт её на КАЖДЫЙ
     * рендер {@code focus-training.html}. Гейт шаблона {@code interviewSession == null and
     * metrics != null} вырождается в {@code interviewSession == null} — рельс несёт либо прогресс
     * сессии, либо прогресс банка и пустым не бывает.
     *
     * <p>Остаётся первый случай, и в нём кап давал полосе ДВЕ меры сразу: композиция по 60rem,
     * баннер и пустое состояние по {@code --focus-measure}. В «Потоке» борта сходились случайно
     * (60rem с обеих сторон), в «Разделении» — нет: там {@code --focus-main-measure} переопределён
     * (на 2560 — 1312px), и композиция осталась бы на 960 при соседях по 1312.
     */
    @Test
    void railLessFallbackSharesTheBandMeasure() {
        String css = declarationsOnly(read(FOCUS));

        assertThat(ruleBody(css, "html[data-design] .focus-training"))
                .as("база композиции завела собственный кап вместо общей меры полосы. Это фолбэк "
                        + "для движка без `:has()` — и он обязан стоять по той же мере, что "
                        + "resume-баннер и пустое состояние, иначе полоса получает две меры сразу. "
                        + "Совпадение значений не считается связью: прежний --max-width-focus был "
                        + "равен мере колонки (60rem) только в «Потоке», а в «Разделении» мера "
                        + "переопределена (2560: 1312px) и борта разъезжались")
                .contains("max-width: var(--focus-measure)");

        for (Path file : new Path[]{FOCUS, TOKENS}) {
            assertThat(declarationsOnly(read(file)))
                    .as("токен --max-width-focus вернулся в %s. Он снят в iter-68 как ВТОРАЯ мера "
                            + "той же полосы: единственным его потребителем была база "
                            + ".focus-training, а значение дублировало меру рабочей колонки, не "
                            + "будучи из неё посчитанным. Если фолбэку снова нужна ширина — он "
                            + "читает --focus-measure, а не заводит свой кап (§24)", file)
                    .doesNotContain("--max-width-focus");
        }
    }

    @Test
    void bannerAndEmptyStateUseWidthNotMaxWidthBecauseOfTheFlexColumnTrap() {
        String css = declarationsOnly(read(FOCUS));

        for (String selector : new String[]{
                "html[data-design] .focus-page .resume-banner",
                "html[data-design] .focus-page .empty-state"}) {
            String body = ruleBody(css, selector);

            assertThat(body)
                    .as("`%s` задаёт ширину через `max-width`. В состоянии «вопросов нет» полоса — "
                            + "flex-колонка (вертикальное центрирование), а в ней cross-axis "
                            + "stretch НЕ применяется к элементу с auto-полями по той же оси: "
                            + "замер WebKit@2 дал 539px у баннера и 854px у карточки вместо меры "
                            + "полосы, на всех вьюпортах сразу. Нужна явная форма "
                            + "`width: min(100%%, …)` — та же, что держит .error-card", selector)
                    .doesNotContain("max-width")
                    .contains("width: min(100%, var(--focus-measure))");

            assertThat(body)
                    .as("`%s` потерял auto-поля: без `margin-inline: auto` (или `margin: … auto`) "
                            + "элемент уже мере полосы прижмётся к левому борту, а соседнее "
                            + "состояние останется по центру — те же две кромки, только "
                            + "по-другому", selector)
                    .containsPattern("margin(-inline)?:[^;]*auto");
        }
    }

    @Test
    void emptyStateHasNoSecondCeilingAnywhere() {
        String css = declarationsOnly(read(FOCUS));

        Matcher rule = Pattern.compile(
                "\\.empty-state\\s*\\{([^}]*)}").matcher(css);
        int seen = 0;
        while (rule.find()) {
            seen++;
            assertThat(rule.group(1))
                    .as("у `.empty-state` снова появился `max-width: none`. Именно он "
                            + "растягивал карточку до 2180px на вьюпорте 2560 при содержимом около "
                            + "450px — рамка с бордером и фоном вокруг пустоты (§6). Ширину "
                            + "пустого состояния на ВСЕХ ширинах задаёт одно правило через "
                            + "--focus-measure")
                    .doesNotContain("max-width: none");
        }

        assertThat(seen)
                .as("правил `.empty-state` стало %d вместо одного. Второе правило — это "
                        + "второй потолок: до iter-67 база держала 560px, а парное правило в "
                        + "блоке >=1200px его снимало, и в зазоре 610–1199 карточка расходилась "
                        + "с баннером на 46–161px с борта (планшеты, половина широкого экрана, "
                        + "зум 150–200%%). Одна мера — одно правило", seen)
                .isEqualTo(1);
    }

    @Test
    void verticalCenteringIsGatedToTheEmptyStateOnly() {
        String css = declarationsOnly(read(FOCUS));

        assertThat(css)
                .as("вертикальное центрирование полосы перестало быть привязано к пустому "
                        + "состоянию через `:has(.empty-state)`. Экран вопроса длинный "
                        + "(docH 1545px при вьюпорте 1440) — центрировать его нельзя, он просто "
                        + "уедет вверх за кромку. Гейт `:has()` включает центрирование ровно в "
                        + "том состоянии, где под карточкой было 1110px мёртвого фона")
                .contains(".focus-page .ed-page:has(.empty-state)");

        Matcher mobile = Pattern.compile(
                "@media \\(max-width: 600px\\) \\{\\s*html\\[data-design] \\.focus-page "
                        + "\\.ed-page:has\\(\\.empty-state\\)\\s*\\{([^}]*)}").matcher(css);
        assertThat(mobile.find())
                .as("пропал мобильный сброс центрирования. На телефоне центрировать нечего: "
                        + "карточка занимает почти весь экран, а фиксированный таббар снизу съел "
                        + "бы её нижнюю кромку")
                .isTrue();
        assertThat(mobile.group(1))
                .as("мобильный сброс перестал возвращать раскладку к верху полосы")
                .contains("justify-content: flex-start")
                .contains("min-height: 0");
    }

}
