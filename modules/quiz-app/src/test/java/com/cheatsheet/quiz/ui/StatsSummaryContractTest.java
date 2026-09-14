package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Аналитика начинается на первом экране, а не на втором.
 *
 * <h2>Найденный дефект, а не гипотеза</h2>
 * Замер положения переключателя разделов (Chromium и WebKit, живые данные):
 *
 * <pre>
 *   вьюпорт      сгиб   вкладки были   вкладки стали
 *   390×844       844      ~1600           1404
 *   768×1024     1024       1634           1215
 *   1024×768      768       1533           1098
 *   1280×800      800       1283            860
 *   1512×982      982       1261            821   ← в сгибе
 *   1920×1080    1080       1261            716   ← в сгибе
 *   2560×1440    1440       1261            716   ← в сгибе
 * </pre>
 *
 * До правки рабочий интерфейс страницы — сами разделы — не попадал в первый экран НИ
 * НА ОДНОЙ ширине матрицы: преамбула (карточка действий 297–588px, стойка 405–635px,
 * витрина знаков 137–426px) занимала его целиком.
 *
 * <h2>Что закреплено</h2>
 * <ol>
 *   <li>«что дальше» и «где я» — один ряд сводки, а не два этажа;</li>
 *   <li>витрина знаков стоит ПОСЛЕ разделов: она отвечает на «чего я достиг»;</li>
 *   <li>дверь — компактная строка: число и расшифровка на одной линии;</li>
 *   <li>число дверей в ряду выбирает ширина, а не медиазапрос;</li>
 *   <li>таблица-дубль графика не ездит вбок ни на какой ширине;</li>
 *   <li>стрелка двери центрирована по высоте и остаётся такой на hover.</li>
 * </ol>
 */
class StatsSummaryContractTest {

    private static final Pattern HTML_COMMENT = Pattern.compile("<!--.*?-->", Pattern.DOTALL);
    private static final Pattern CSS_COMMENT = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL);

    private String read(String classpathLocation) throws IOException {
        return new String(new ClassPathResource(classpathLocation).getInputStream().readAllBytes(),
                StandardCharsets.UTF_8);
    }

    private String statsMarkup() throws IOException {
        return HTML_COMMENT.matcher(read("templates/stats.html")).replaceAll("");
    }

    private String statsCss() throws IOException {
        return CSS_COMMENT.matcher(read("static/css/pages-stats.css")).replaceAll("");
    }

    @Test
    void theSummaryIsStandingOnlyWithoutNextActions() throws IOException {
        // IA Wave 3: блок «Что делать дальше» переехал на хаб «Сегодня».
        // На /stats остаётся сводка «где я» (стойка + метрики).
        String markup = statsMarkup();
        assertThat(markup)
                .as("двери «что дальше» снова на /stats — они должны жить на home")
                .doesNotContain("class=\"stats-next-actions\"");
        assertThat(markup)
                .as("обёртки сводки нет")
                .contains("<div class=\"stats-summary\">");
        assertThat(markup)
                .as("стойки «где я» нет")
                .contains("class=\"stats-standing\"");

        int summary = markup.indexOf("<div class=\"stats-summary\">");
        int standing = markup.indexOf("<div class=\"stats-standing\">");
        assertThat(summary).isNotNegative();
        assertThat(standing).as("стойка обязана лежать внутри сводки").isGreaterThan(summary);

        String css = statsCss();
        assertThat(css)
                .as("колонная сводка (узкие двери слева от стойки) вернулась")
                .doesNotContain("minmax(0, 27rem)");
        assertThat(css)
                .as("плитки метрик снова получили коробку")
                .containsPattern("\\.dashboard-grid \\.metric \\{[\\s\\S]{0,300}?background: transparent");
    }

    /**
     * Хаб «Сегодня» несёт primary CTA; /stats дверей больше не показывает.
     */
    @Test
    void homeCarriesPrimaryActionsAndStatsDoesNot() throws IOException {
        String home = HTML_COMMENT.matcher(read("templates/home.html")).replaceAll("");
        String stats = statsMarkup();
        assertThat(home)
                .as("на home нет primary CTA — «что делать дальше» потеряно")
                .contains("class=\"home-primary");
        assertThat(stats)
                .as("stats-next-actions вернулся на /stats")
                .doesNotContain("class=\"stats-next-actions\"");
    }

    /** Learning outcomes stay visible; optional XP must not precede them. */
    @Test
    void learningMetricsPrecedeOptionalExperience() throws IOException {
        String markup = statsMarkup();
        int metrics = markup.indexOf("<section class=\"dashboard-grid\"");
        int experience = markup.indexOf("<details class=\"stats-rank-disclosure\"");
        int tabs = markup.indexOf("id=\"stats-tablist\"");
        assertThat(metrics).as("учебные метрики доступны без раскрытия опыта").isNotNegative();
        assertThat(experience).as("опыт следует за учебными результатами").isGreaterThan(metrics);
        assertThat(tabs).isGreaterThan(experience);
        assertThat(markup).doesNotContain("<details class=\"stats-standing-disclosure\"");
        String beforeTabs = markup.substring(metrics, tabs);
        long opened = Pattern.compile("<details").matcher(beforeTabs).results().count();
        long closed = Pattern.compile("</details>").matcher(beforeTabs).results().count();
        assertThat(closed).as("вкладки не должны оказаться внутри раскрытия").isEqualTo(opened);
    }

    /**
     * Каждый процент точности на странице носит имя своего знаменателя (VIS-04).
     *
     * <p>На живых данных страница печатала рядом два «противоречащих» числа: шапка
     * стойки — «точность 8,1%», панель деталей — «Первая попытка 23,3%». Оба верны
     * (первые попытки 52/223, все попытки 225/2779 — дриллинг тянет общую вниз), но
     * голое слово «точность» в шапке не говорило, КОТОРАЯ это точность: читатель
     * сверял 8,1% с 23,3% и решал, что страница врёт. Шапка зеркалит плитку
     * «Точность попыток» — значит, и называться обязана её словами.</p>
     *
     * <p>Вторая ложь той же метрики — оговорка «(без пропусков)»: «пропуск» в
     * словаре пользователя — кнопка «Не знаю», а она пишет UNKNOWN, который СЧИТАЕТСЯ
     * ошибкой (знаменатель CORRECT+WRONG+UNKNOWN). Исключается только outcome SKIP —
     * мёртвая ветвь enum, её не пишет ни один обработчик. Копия обязана объяснять
     * живое правило («не знаю» — ошибка), а не мёртвое исключение.</p>
     */
    @Test
    void everyAccuracyPercentCarriesTheNameOfItsDenominator() throws IOException {
        String markup = statsMarkup();
        assertThat(markup)
                .as("общая точность должна явно отличаться от точности первых попыток")
                .contains("Точность всех попыток");
        assertThat(markup)
                .as("оговорка «без пропусков» описывает мёртвый outcome SKIP и отрицает "
                        + "живое правило: кнопка «Не знаю» пишет UNKNOWN, и он считается ошибкой")
                .doesNotContain("без пропусков");
        assertThat(markup)
                .as("подпись плитки «Точность попыток» обязана называть судьбу «не знаю»")
                .containsPattern("nonSkip[^<]*не знаю");
        assertThat(markup)
                .as("примечание панели «Точность и удержание» обязано называть судьбу «не знаю»")
                .containsPattern("Знаменатель общей[^<]*не знаю");
    }

    /**
     * Витрина знаков отвечает на «чего я достиг» — это вопрос после разбора, а не до
     * него. Стоя перед разделами, она удлиняла путь к аналитике на 137–426px.
     */
    @Test
    void theBadgesShowcaseComesAfterThePanels() throws IOException {
        String markup = statsMarkup();
        int lastPanel = markup.indexOf("id=\"stats-panel-errors\"");
        int badges = markup.indexOf("class=\"card badges\"");
        assertThat(lastPanel).as("панели ошибок нет").isNotNegative();
        assertThat(badges).as("витрины знаков нет").isNotNegative();
        assertThat(badges)
                .as("витрина знаков снова стоит перед разделами аналитики")
                .isGreaterThan(lastPanel);
    }

    @Test
    void nextActionDoorStylesRemainAvailableInCss() throws IOException {
        // Разметка дверей ушла с /stats на home, но CSS-рецепт компактной двери
        // остаётся в pages-stats (на случай переиспользования / регрессии формы).
        String css = statsCss();
        assertThat(css)
                .containsPattern("\\.stats-next-actions-grid \\{[\\s\\S]{0,400}?grid-template-columns: minmax\\(0, 1fr\\)");
        assertThat(css)
                .containsPattern("\\.stats-next-action:not\\(\\.stats-next-action-primary\\) \\{[\\s\\S]{0,400}?border-color: transparent");
        assertThat(css)
                .containsPattern("\\.stats-next-action \\{[\\s\\S]{0,400}?grid-template-areas:\\s*\"label label\"\\s*\"value detail\"");
    }

    /**
     * Стрелка центрируется по высоте строки. Классическая ловушка: hover задаёт свой
     * {@code transform}, и без повторного {@code translateY(-50%)} стрелка прыгает к
     * верхней кромке.
     */
    @Test
    void theArrowStaysCenteredOnHover() throws IOException {
        String css = statsCss();
        assertThat(css)
                .as("стрелка двери не центрирована по высоте — в низкой строке она висит "
                        + "над ярлыком")
                .containsPattern("\\.stats-next-action::after \\{[\\s\\S]{0,300}?transform: translateY\\(-50%\\)");
        assertThat(css)
                .as("hover сбрасывает центрирование: в объявлении обязаны быть ОБА сдвига")
                .containsPattern("focus-visible::after \\{[\\s\\S]{0,200}?transform: translate\\(3px, -50%\\)");
    }

    /**
     * Ограничение у таблицы-дубля — узкий КОНТЕЙНЕР, а не маленький экран: на 1024
     * карточка графика узкая из-за двух колонок в ряду, и правило под
     * {@code max-width: 767px} там не работало.
     */
    @Test
    void theChartTableNeverScrollsSideways() throws IOException {
        String css = statsCss();
        int at = css.indexOf(".chart-adjacent-table th,");
        assertThat(at).as("правил таблицы-дубля нет").isNotNegative();

        // Ищем ближайший открывающий @media перед правилом и убеждаемся, что правило
        // стоит вне его тела: гейт проверяет БЕЗУСЛОВНОСТЬ, а не текст селектора.
        int mediaBefore = css.lastIndexOf("@media", at);
        boolean insideMedia = false;
        if (mediaBefore >= 0) {
            int depth = 0;
            for (int i = css.indexOf('{', mediaBefore); i < at && i > 0; i++) {
                char c = css.charAt(i);
                if (c == '{') depth++;
                if (c == '}') depth--;
                if (depth == 0 && i > css.indexOf('{', mediaBefore)) break;
            }
            insideMedia = depth > 0;
        }
        assertThat(insideMedia)
                .as("правило таблицы-дубля снова под медиазапросом: на 1024 карточка "
                        + "графика узкая не из-за телефона, а из-за двух колонок в ряду")
                .isFalse();
        assertThat(css.substring(at, Math.min(css.length(), at + 400)))
                .as("поля и перенос имени темы — то, чем лечится жёлоб 310/279")
                .contains("padding-inline: var(--space-2)")
                .contains("overflow-wrap: anywhere");
    }
}
