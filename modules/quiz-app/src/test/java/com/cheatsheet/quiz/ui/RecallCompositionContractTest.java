package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Композиция экрана вопроса включается по ОДНОМУ признаку — «на странице есть
 * рабочая поверхность», — а у фазы вспоминания флешкарты есть своя.
 *
 * <h2>Найденный дефект, а не гипотеза</h2>
 * Гейт композиции был перечнем из пяти признаков ({@code #interview-options},
 * {@code .flashcard-answer}, {@code .typed-workspace}, {@code .study-learn-phase},
 * {@code .result-feedback}), повторённым в тринадцати блоках — 62 строки селекторов.
 * Перечень оказался неполон ровно так же, как до него был неполон перечень режимов:
 * фаза вспоминания флешкарты (значок + «Показать ответ») не подходит ни под один из
 * пяти, и композиции у неё не было. Замер (Chromium и WebKit, FLASHCARD в сессии,
 * до раскрытия):
 *
 * <pre>
 *   вьюпорт     сетка   карточка        пусто справа  пусто снизу
 *   1280×800    нет     955×855          66             8
 *   1512×982    нет     960×855         175             0
 *   1920×1080   нет     1296×757        203           120
 *   2560×1440   нет     1296×709        522           324
 * </pre>
 *
 * Спутники вопроса (о теме, карта темы, ход сессии, заметка) при этом на странице
 * были — они уезжали лентой ПОД карточку (2560: 960×317 на отметке 643).
 *
 * <h2>Что закреплено</h2>
 * <ol>
 *   <li>каждая рабочая поверхность носит общий класс {@code workspace-surface};</li>
 *   <li>композиция гейтится этим классом, а не перечнем поимённых признаков;</li>
 *   <li>у фазы вспоминания своя композиция, и включает её СОДЕРЖИМОЕ спутников,
 *       а не имя режима: пустой рельс — это пустая правая колонка, которую §37
 *       запрещает;</li>
 *   <li>ширина рельса спутников объявлена один раз;</li>
 *   <li>высота плиты карточки считается от окна, а не пикселями.</li>
 * </ol>
 */
class RecallCompositionContractTest {

    private static final Pattern COMMENT_HTML = Pattern.compile("<!--.*?-->", Pattern.DOTALL);
    private static final Pattern COMMENT_CSS = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL);

    private String read(String classpathLocation) throws IOException {
        return new String(new ClassPathResource(classpathLocation).getInputStream().readAllBytes(),
                StandardCharsets.UTF_8);
    }

    private String focusMarkup() throws IOException {
        return COMMENT_HTML.matcher(read("templates/focus-training.html")).replaceAll("");
    }

    private String focusCss() throws IOException {
        return COMMENT_CSS.matcher(read("static/css/pages-focus.css")).replaceAll("");
    }

    /**
     * Носителей шесть: варианты, три ответа флешкарты (browse, сессия, изучение),
     * рабочее место письма и разбор. Пропущенный носитель означает фазу без
     * композиции — ровно тот дефект, который эта итерация чинила.
     */
    @Test
    void everyWorkingSurfaceCarriesTheCommonClass() throws IOException {
        String markup = focusMarkup();
        for (String carrier : new String[]{
                "options workspace-surface\" id=\"interview-options\"",
                "class=\"typed-workspace workspace-surface\"",
                "class=\"flashcard-phase study-learn-phase workspace-surface\""}) {
            assertThat(markup).as("носитель рабочей поверхности потерял общий класс: " + carrier)
                    .contains(carrier);
        }
        int answers = markup.split("class=\"flashcard-answer workspace-surface\"", -1).length - 1;
        assertThat(answers)
                .as("ответов флешкарты в шаблоне три (browse, сессия, изучение) — и все три "
                        + "являются рабочей поверхностью")
                .isEqualTo(3);
        assertThat(COMMENT_HTML.matcher(read("templates/fragments/post-answer-controls.html")).replaceAll(""))
                .as("приёмник разбора — тоже рабочая поверхность; класс `hidden` рядом с ним "
                        + "значим: до заполнения скриптом композиции быть не должно")
                .contains("class=\"result-feedback workspace-surface hidden\"");
    }

    /**
     * Перечень признаков в гейте — та же ошибка, что перечень режимов до него.
     * Разрешены только ФАЗОВЫЕ исключения (у фазы изучения своя мера колонки, у
     * ответившего состояния — своя липкость), но не сам гейт.
     */
    @Test
    void theCompositionIsGatedByOneCarrierNotByAList() throws IOException {
        String css = focusCss();
        Matcher named = Pattern.compile(
                ":has\\(#interview-options\\)|:has\\(\\.flashcard-answer\\)|:has\\(\\.typed-workspace\\)")
                .matcher(css);
        int hits = 0;
        while (named.find()) hits++;
        assertThat(hits)
                .as("гейт композиции снова перечисляет носителей поимённо — перечень уже "
                        + "оказывался неполон дважды (сначала режимы, потом признаки)")
                .isZero();
        assertThat(css)
                .as("общий признак рабочей поверхности исчез из гейта")
                .contains(":has(.workspace-surface:not(.hidden))");
        assertThat(css)
                .as("`hidden` в гейте обязателен: приёмник разбора стоит в разметке всегда "
                        + "и до заполнения скриптом композицию включать нельзя")
                .doesNotContain(":has(.workspace-surface)");
    }

    @Test
    void theRecallPhaseHasItsOwnCompositionGatedByCompanionContent() throws IOException {
        String css = focusCss();
        assertThat(css)
                .as("композиции фазы вспоминания нет — карточка снова одна на широком экране")
                .contains(":has(.flashcard-phase):not(:has(.workspace-surface:not(.hidden))):has(.focus-companions > *)");
        assertThat(css)
                .as("вторая колонка обязана включаться СОДЕРЖИМЫМ спутников: на первом вопросе "
                        + "сессии, без описания темы и без заметки рельс пуст, и пустая правая "
                        + "колонка — это §37")
                .contains(":has(.focus-companions > *)");
        assertThat(css)
                .as("мера состава считается формулой (карточка + жёлоб + рельс), иначе рельс "
                        + "отнимает ширину у карточки вместо того, чтобы её добавить")
                .contains("--focus-main-measure: calc(60rem + var(--focus-rail-gap) + var(--workspace-companions))");
    }

    @Test
    void theCompanionRailWidthIsDeclaredOnce() throws IOException {
        String css = focusCss();
        int declarations = css.split("--workspace-companions:", -1).length - 1;
        assertThat(declarations)
                .as("ширину одной и той же колонки объявили дважды — это две точки правды")
                .isEqualTo(1);
    }

    /**
     * Плита карточки — поверхность из общих токенов, а её высота идёт от окна:
     * пиксельное число здесь означало бы карточку, которая на одном экране
     * прижата, а на другом растянута.
     */
    @Test
    void theCardPlateTakesItsToneFromTokensWithoutReservingEmptyViewportSpace() throws IOException {
        String css = focusCss();
        int at = css.indexOf(":has(.focus-companions > *) > .focus-task > .focus-question-pane {");
        assertThat(at).as("правила плиты карточки нет").isNotNegative();
        String rule = css.substring(at, css.indexOf('}', at));
        assertThat(rule)
                .as("тон плиты обязан браться из токена поверхности, а не быть своим цветом")
                .contains("background: var(--color-bg-secondary)");
        assertThat(rule)
                .as("действие следует за содержимым, короткий вопрос не резервирует пустой экран")
                .doesNotContainPattern("min-height: min\\(\\d+svh, \\d+rem\\)");
    }

    /** Перечень режимов в правилах рельса — исторический дубль содержательного гейта. */
    @Test
    void theSessionRailRuleNoLongerListsModes() throws IOException {
        String css = focusCss();
        assertThat(css)
                .as("правило рельса снова перечисляет режимы: TYPED_RECALL / INTERVIEW / "
                        + "CODE_CHALLENGE рендерят ту же рабочую поверхность")
                .doesNotContain("[data-session-mode=\"TYPED_RECALL\"] .focus-aside")
                .doesNotContain("[data-session-mode=\"INTERVIEW\"] .focus-aside")
                .doesNotContain("[data-session-mode=\"CODE_CHALLENGE\"] .focus-aside");
    }
}
