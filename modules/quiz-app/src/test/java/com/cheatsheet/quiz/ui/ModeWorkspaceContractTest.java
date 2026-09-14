package com.cheatsheet.quiz.ui;

import com.cheatsheet.quiz.common.util.ModeUtils;
import com.cheatsheet.quiz.domain.InterviewMode;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Рабочее место печатного режима: у интервью и код-челленджа — своя поверхность.
 *
 * <h2>Найденный дефект, а не гипотеза</h2>
 * Замер (Chromium и WebKit, 1512×982 и 2560×1440, один и тот же вопрос) — коробки
 * трёх режимов семейства {@code usesTypedInput()}:
 *
 * <pre>
 *   режим            колонки (2560)   поле ввода   план ответа
 *   TYPED_RECALL       768 / 640       640×1100    текстом слева
 *   INTERVIEW          768 / 640       640×1100    текстом слева
 *   CODE_CHALLENGE     768 / 640       640×1100    текстом слева
 * </pre>
 *
 * Три режима рисовали ОДИН экран: отличались подпись поля, четыре строки плана и чип
 * режима. §14 брифа называет это архитектурным дефектом UI и запрещает лечить его
 * размером поля.
 *
 * <h2>Что закреплено</h2>
 * <ol>
 *   <li>таблица режима (подпись, подсказка, план) живёт в одном месте — {@link ModeUtils};</li>
 *   <li>план ответа — настоящие флажки, а не нарисованная клетка в {@code ::before};</li>
 *   <li>всё, что без скрипта показывало бы неверное число, приходит скрытым;</li>
 *   <li>шапка раскрытия — один рецепт на приложение, а не копия на страницу.</li>
 * </ol>
 */
class ModeWorkspaceContractTest {

    private static final Pattern COMMENT = Pattern.compile("<!--.*?-->", Pattern.DOTALL);

    private String read(String classpathLocation) throws IOException {
        return new String(new ClassPathResource(classpathLocation).getInputStream().readAllBytes(),
                StandardCharsets.UTF_8);
    }

    private String markupWithoutComments() throws IOException {
        return COMMENT.matcher(read("templates/focus-training.html")).replaceAll("");
    }

    /**
     * Признак «ответ проговаривают вслух» — свойство режима, как и «пишут кодом».
     * Пока его не было, шаблон мог отличить интервью только сравнением с именем
     * константы, а такие сравнения в этом проекте уже расходились с продуктом молча.
     */
    @Test
    void spokenRehearsalIsAModeProperty() {
        assertThat(InterviewMode.INTERVIEW.usesSpokenRehearsal()).isTrue();
        List<InterviewMode> others = List.of(
                InterviewMode.TRAINING, InterviewMode.STUDY, InterviewMode.FLASHCARD,
                InterviewMode.TYPED_RECALL, InterviewMode.CODE_CHALLENGE,
                InterviewMode.EXAM, InterviewMode.MARATHON);
        assertThat(others)
                .as("репетиция вслух — ровно у интервью; иначе часы приедут туда, где ответ пишут")
                .noneMatch(InterviewMode::usesSpokenRehearsal);
    }

    /**
     * Подсказка поля принадлежит режиму и у трёх печатных режимов РАЗНАЯ. До iter-129
     * все три печатали «Напиши ответ перед раскрытием эталона…» — в код-челлендже это
     * стояло над моноширинным полем с подписью «Твой код».
     */
    @Test
    void everyTypedModeHasItsOwnPlaceholder() {
        ModeUtils modeUtils = new ModeUtils();
        String typed = modeUtils.typedAnswerPlaceholder(InterviewMode.TYPED_RECALL);
        String interview = modeUtils.typedAnswerPlaceholder(InterviewMode.INTERVIEW);
        String code = modeUtils.typedAnswerPlaceholder(InterviewMode.CODE_CHALLENGE);

        assertThat(List.of(typed, interview, code)).doesNotContain("");
        assertThat(List.of(typed, interview, code))
                .as("одна подсказка на три режима — это и есть «один экран с другим ярлыком»")
                .doesNotHaveDuplicates();
        assertThat(modeUtils.typedAnswerPlaceholder(InterviewMode.TRAINING))
                .as("у режима без ввода подсказки нет — блок поля там не рендерится")
                .isEmpty();
        assertThat(modeUtils.typedAnswerPlaceholder(null)).isEmpty();

        for (InterviewMode mode : InterviewMode.values()) {
            if (mode.usesTypedInput()) {
                assertThat(modeUtils.typedAnswerPlaceholder(mode))
                        .as("режим с вводом обязан сказать, что писать в поле: " + mode)
                        .isNotBlank();
            }
        }
    }

    /**
     * План ответа — интерактивный список, а не проза с нарисованной клеткой. Клетка в
     * {@code ::before} выглядела как то, что можно отметить, и не отмечалась: ложный
     * аффорданс. Флажок нативный — работает без JS и объявляется скринридером.
     */
    @Test
    void thePlanIsMadeOfRealCheckboxes() throws IOException {
        String markup = markupWithoutComments();

        assertThat(markup).contains("class=\"workspace-plan-box\"");
        assertThat(markup)
                .as("флажок должен быть настоящим input, а не рисунком")
                .contains("type=\"checkbox\" class=\"workspace-plan-box\"");
        assertThat(markup)
                .as("строка флажка берёт общий компонент .toggle-item, а не свой рисунок")
                .contains("class=\"toggle-item workspace-plan-item\"");

        String css = read("static/css/pages-focus.css");
        assertThat(css)
                .as("нарисованная клетка старого списка удалена вместе с ним")
                .doesNotContain("workspace-support-list li::before");
    }

    /**
     * Опора не должна остаться в колонке задания второй копией: план переехал в
     * рабочее место целиком, старые имена не живут ни в разметке, ни в стилях.
     */
    @Test
    void theOldSupportBlockIsGoneEverywhere() throws IOException {
        assertThat(markupWithoutComments())
                .as("две системы одного плана — ровно то, что §4 брифа запрещает")
                .doesNotContain("workspace-support");

        String css = read("static/css/pages-focus.css");
        String rulesOnly = css.replaceAll("(?s)/\\*.*?\\*/", "");
        assertThat(rulesOnly)
                .as("правила снятого блока удалены, а не оставлены висеть")
                .doesNotContain("workspace-support");
    }

    /**
     * Всё, что скрипт пересчитывает, приходит с сервера скрытым. Иначе без JS
     * счётчик застыл бы на «покрыто 0 из 5», номера строк остались бы на единице, а
     * секундомер показывал бы 0:00 и не шёл — три интерфейса, которые врут.
     */
    @Test
    void everyComputedSurfaceArrivesHidden() throws IOException {
        String markup = markupWithoutComments();

        assertThat(markup).containsPattern("class=\"workspace-plan-count\" hidden");
        assertThat(markup).containsPattern("class=\"rehearsal-timer\"[^>]*hidden");
        assertThat(markup).containsPattern("class=\"code-gutter\"[^>]*hidden");
        assertThat(markup).containsPattern("class=\"code-status\"[^>]*hidden");

        String js = read("static/js/quiz/workspace.js");
        assertThat(js)
                .as("скрытое обязано раскрываться тем же кодом, который его считает")
                .contains("count.hidden = false")
                .contains("gutter.hidden = false")
                .contains("timer.hidden = false");
    }

    /**
     * Циферблат не объявляется скринридеру каждую секунду. {@code <output>} по
     * умолчанию {@code aria-live="polite"}, поэтому «off» здесь — не украшение, а
     * единственное, что отделяет часы от шестидесяти объявлений в минуту.
     */
    @Test
    void theClockDoesNotFloodTheScreenReader() throws IOException {
        String markup = markupWithoutComments();
        assertThat(markup).containsPattern("class=\"rehearsal-timer-clock\" aria-live=\"off\"");
        assertThat(markup)
                .as("смену состояния объявлять надо — это две реплики за ответ, а не шестьдесят")
                .containsPattern("class=\"rehearsal-timer-state\"");
    }

    /**
     * Шапка раскрытия рисуется одним рецептом на приложение. Приём понадобился
     * третьему экрану, и копия под каждой страницей — это второй рисунок одного
     * элемента.
     */
    @Test
    void theDisclosureHeaderHasOneRecipe() throws IOException {
        String controls = read("static/css/components-controls.css");
        assertThat(controls).contains(".disclosure-summary");
        assertThat(controls).contains(".disclosure-summary::-webkit-details-marker");

        // Проверяем ИМЕННО три шапки раскрытия, а не любой <summary> в файле: на
        // экране вопроса есть ещё две сводки другой природы — кнопка раскрытия
        // эталона (`.flashcard-reveal-summary`) и меню сессии в рельсе
        // (`.focus-session-more-summary`). Они не строки-заголовки, у них свой вид,
        // и запрет «страница не трогает marker» отобрал бы у них право на него.
        for (String page : List.of("static/css/pages-stats.css", "static/css/pages-focus.css")) {
            String rulesOnly = read(page).replaceAll("(?s)/\\*.*?\\*/", "");
            for (String header : List.of("badges-summary", "filter-bar-summary", "workspace-plan-summary")) {
                assertThat(rulesOnly)
                        .as("шапка раскрытия не рисуется второй раз под страницей: "
                                + header + " в " + page)
                        .doesNotContain(header + "::-webkit-details-marker")
                        .doesNotContain(header + ":focus-visible");
            }
        }

        assertThat(markupWithoutComments()).contains("class=\"disclosure-summary workspace-plan-summary\"");
        String stats = COMMENT.matcher(read("templates/stats.html")).replaceAll("");
        assertThat(stats)
                .as("аналитика перешла на общий класс вместе с компонентом")
                .contains("class=\"disclosure-summary badges-summary\"")
                .contains("class=\"disclosure-summary filter-bar-summary\"");
    }

    /**
     * Поле ввода не растёт до дна экрана и считает преамбулу над собой.
     *
     * <p>Замер 2560×1440 до правки: поле 1100px при колонке задания 630px — рабочая
     * колонка вдвое длиннее той, ради которой она работает. Замер после переезда
     * плана: «Показать ответ» на 1030 при сгибе 982 (1512) и на 1091 при 1024 (768).
     * Отсюда два числа — вычет 34rem в две колонки и 42rem в стопке, где над полем
     * стоит ещё и сам вопрос.
     */
    @Test
    void theWritingSurfaceHasACeiling() throws IOException {
        String css = read("static/css/pages-focus.css");
        assertThat(css)
                .as("высота поля обязана иметь верхнюю границу, а не только пол")
                .contains("min-height: max(14rem, min(calc(100svh - 34rem), 30rem))");
        assertThat(css)
                .as("в стопке преамбула больше на высоту вопроса — вычет обязан это учитывать")
                .contains("min-height: max(8rem, min(calc(100svh - 42rem), 24rem))");
        assertThat(css)
                .as("у интервью поле — заметки к устному ответу, а не полотно письма")
                .contains(".typed-workspace:has(.rehearsal-timer) .typed-answer-input textarea");

        String markup = markupWithoutComments();
        assertThat(markup)
                .as("собственная высота поля не должна перебивать объявленные полы: "
                        + "шесть строк давали 189px и решали за стили")
                .contains("name=\"typedAnswer\" rows=\"4\"");

        String rulesOnly = css.replaceAll("(?s)/\\*.*?\\*/", "");
        assertThat(rulesOnly)
                .as("мёртвые мобильные полы 5.5rem удалены, а не оставлены висеть")
                .doesNotContain("min-height: 5.5rem");
    }

    /**
     * Колонка задания широка ради листинга — на любой ширине workspace-грида.
     *
     * <h2>Найденный замером дефект (iter-186)</h2>
     * Гейт «нет материалов → --workspace-brief: 30rem» жил только в блоке
     * ≥2240px, и на 1728 (typed recall, эталон раскрыт) вопрос БЕЗ кода держал
     * колонки 768 | 542: под заголовком пустовало ~60% высоты, а эталон
     * читался в 542px. После переноса гейта в базовый блок мер — 480 | 830;
     * вопрос С материалами держит прежние 768 | 542 (проверено живьём).
     */
    /**
     * В LEARN-фазе STUDY материалы вопроса не рендерятся (iter-187).
     *
     * <h2>Найденный живым прогоном дефект</h2>
     * Эталон LEARN в шпаргалках почти всегда содержит тот же листинг, что и
     * codeSnippet вопроса: замер (Rat in a Maze, 1728) показал ОДНУ И ТУ ЖЕ
     * программу дважды на одном экране. Своего кода для сверки в LEARN нет —
     * левая копия была чистым дублем; её снятие заодно включает гейт «нет
     * материалов → узкая колонка задания» (замер: 288 | 1022 — чтению эталона
     * отдана почти вся полоса). В QUIZ-под-фазе материалы возвращаются: там
     * эталона на экране нет, и код — единственная опора ответа (проверено
     * живьём: чип «Проверка», материалы и варианты на месте).
     */
    @Test
    void studyLearnPhaseDropsTheDuplicatedMaterials() throws IOException {
        String m = markupWithoutComments();
        assertThat(m)
                .as("гейт LEARN-фазы пропал: эталон и его копия-листинг снова видны одновременно")
                .contains("th:if=\"${(hasCode or hasDiagram) and !studyLearnPhase}\"");

        String css = read("static/css/pages-focus.css");
        assertThat(css)
                .as("зазор кнопки раскрытия пропал: «Показать ответ» снова слипается "
                        + "с поверхностью письма (textarea bottom == button top)")
                .contains(".typed-answer-input .flashcard-reveal-btn { margin-top: var(--space-3); }");
    }

    @Test
    void theBriefColumnShrinksWithoutMaterialsOnEveryWorkspaceWidth() throws IOException {
        String css = read("static/css/pages-focus.css");
        // Pending/failed Mermaid is not usable material and must not reserve a pane.
        String gate = ":has(.workspace-surface:not(.hidden)):not(:has(.question-materials.is-ready))";
        int gatePos = css.indexOf(gate);
        int ultraWide = css.indexOf("@media (min-width: 2240px)");
        assertThat(gatePos)
                .as("гейт «нет материалов → узкий brief» пропал: пустая колонка "
                        + "задания вернётся на 1280–2239")
                .isNotNegative();
        assertThat(gatePos)
                .as("гейт обязан жить в БАЗОВОМ блоке мер, до сверхширокого медиа — "
                        + "иначе он снова действует только с 2240px")
                .isLessThan(ultraWide);
        assertThat(css.indexOf(gate, ultraWide))
                .as("в ≥2240 не должно быть второй копии гейта: два источника "
                        + "разъезжаются молча")
                .isNegative();
    }
}
