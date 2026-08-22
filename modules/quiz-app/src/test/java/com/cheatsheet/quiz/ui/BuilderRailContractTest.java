package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * У конструктора сессии нет ряда, который пустует в состоянии по умолчанию.
 *
 * <h2>Найденный дефект, а не гипотеза</h2>
 * Сетка конструктора описывала четыре ряда:
 *
 * <pre>
 *   "scope  outcome"
 *   "mode   mode"
 *   "volume today"     ← карточка «Сегодня» рядом с шагом «Сколько»
 *   "launch launch"
 * </pre>
 *
 * Шаг «Сколько» скрыт у режимов без ограничения объёма — тренировка, изучение,
 * флешкарты, — а тренировка стоит режимом ПО УМОЛЧАНИЮ. Замер (Chromium и WebKit,
 * 1512×982): ряды 306 / 358 / 229 / 69, и третий ряд нёс 728×229 пустоты слева от
 * карточки. Документ 1397 при сгибе 982. Сама карточка при этом стояла ПОСЛЕ полосы
 * запуска, то есть после главного действия страницы.
 *
 * <h2>Замер после</h2>
 * <pre>
 *   вьюпорт     документ было → стало
 *   390×844        2415 → 2219
 *   768×1024       1997 → 1826
 *   1024×768       1751 → 1522
 *   1280×720       1544 → 1342
 *   1512×982       1397 → 1239
 * </pre>
 *
 * Третий ряд в состоянии по умолчанию — 0px на всех десктопных ширинах.
 */
class BuilderRailContractTest {

    private static final Pattern HTML_COMMENT = Pattern.compile("<!--.*?-->", Pattern.DOTALL);
    private static final Pattern CSS_COMMENT = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL);

    private String read(String classpathLocation) throws IOException {
        return new String(new ClassPathResource(classpathLocation).getInputStream().readAllBytes(),
                StandardCharsets.UTF_8);
    }

    private String settingsMarkup() throws IOException {
        // UXD-001: поверхность конструктора = train.html (лид с хвостом) + фрагмент
        // session-builder.html (рельс «Что попадёт» со стриком). Контракты класса
        // описывают обе части одной страницы — читаем их вместе.
        return HTML_COMMENT.matcher(read("templates/train.html")
                + read("templates/fragments/session-builder.html")).replaceAll("");
    }

    private String css(String name) throws IOException {
        return CSS_COMMENT.matcher(read("static/css/" + name)).replaceAll("");
    }

    /**
     * Стрик — факт о том, что сейчас будет, и живёт он в рельсе набора. Отдельной
     * карточки под него нет: карточка требовала своей области сетки и своего правила
     * «спрятаться, когда данных нет».
     */
    @Test
    void theStreakLivesInTheOutcomeRailAndNotInItsOwnCard() throws IOException {
        String markup = settingsMarkup();
        assertThat(markup)
                .as("карточка «Сегодня» вернулась — вместе с ней вернётся и пустой ряд сетки")
                .doesNotContain("builder-stats-card")
                .doesNotContain("builder-stats");

        int rail = markup.indexOf("class=\"builder-outcome\"");
        int streak = markup.indexOf("class=\"streak-bar outcome-today");
        int railEnd = markup.indexOf("</aside>", rail);
        assertThat(streak).as("строки «сегодня» нет вовсе").isNotNegative();
        assertThat(streak).as("строка «сегодня» обязана лежать ВНУТРИ рельса набора")
                .isGreaterThan(rail).isLessThan(railEnd);

        int bars = markup.split("data-streak-bar", -1).length - 1;
        assertThat(bars)
                .as("на странице снова два носителя стрика: скрипт заполняет оба, и одна и "
                        + "та же серия печатается дважды")
                .isEqualTo(1);
    }

    @Test
    void theThirdRowBelongsToTheVolumeStepAlone() throws IOException {
        String shell = css("shell.css");
        assertThat(shell)
                .as("сетка конструктора снова делит третий ряд с карточкой: у режимов без "
                        + "ограничения объёма шаг «Сколько» скрыт, и половина ряда пустует")
                .containsPattern("grid-template-areas:\\s*\"scope\\s+outcome\"\\s*\"mode\\s+mode\"\\s*\"volume\\s+volume\"\\s*\"launch\\s+launch\"");
        assertThat(shell)
                .as("привязка к удалённой области осталась — правило без носителя")
                .doesNotContain("grid-area: today");
        assertThat(css("pages-settings.css"))
                .as("правило скрытия удалённой карточки осталось в CSS")
                .doesNotContain("#builder-stats-card");
    }

    /**
     * Хвост лида описывает двухколоночную раскладку. Она включается на 901px — тем же
     * числом, по которому хвост и клипается: правило и реальность берут одно число.
     */
    @Test
    void theLeadTailIsClippedExactlyWhereTheTwoColumnLayoutEnds() throws IOException {
        String markup = settingsMarkup();
        assertThat(markup)
                .as("хвост лида не отделён — на телефоне он описывает колонку, которой там нет")
                .contains("<span class=\"settings-lead-tail\">");

        String settings = css("pages-settings.css");
        assertThat(settings)
                .as("хвост лида клипается не по порогу раскладки: правило и реальность "
                        + "разойдутся при первой правке одной из них")
                .containsPattern("@media \\(max-width: 900px\\) \\{[\\s\\S]{0,400}?\\.settings-lead-tail \\{[\\s\\S]{0,200}?clip-path");
        assertThat(css("shell.css"))
                .as("порог двухколоночной раскладки сдвинулся — клип хвоста надо пересчитать")
                .contains("@media (min-width: 901px)");
    }
}
