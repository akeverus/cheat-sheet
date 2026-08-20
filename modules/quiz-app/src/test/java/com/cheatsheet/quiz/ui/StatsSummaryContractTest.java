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
    void theSummaryIsOneRowAndNotTwoFloors() throws IOException {
        String markup = statsMarkup();
        int summary = markup.indexOf("<div class=\"stats-summary\">");
        int actions = markup.indexOf("class=\"card stats-next-actions\"");
        int standing = markup.indexOf("<div class=\"stats-standing\">");
        assertThat(summary).as("обёртки сводки нет — действия и стойка снова этажами").isNotNegative();
        assertThat(summary).as("действия обязаны лежать внутри сводки").isLessThan(actions);
        assertThat(actions).as("стойка обязана лежать в той же сводке").isLessThan(standing);

        String css = statsCss();
        assertThat(css)
                .as("сводка не становится рядом ни на какой ширине — тогда обёртка бессмысленна")
                .containsPattern("@media \\(min-width: 1280px\\) \\{[\\s\\S]{0,400}?\\.stats-summary \\{[\\s\\S]{0,200}?grid-template-columns");
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
    void aDoorIsACompactRowWhoseColumnCountFollowsTheWidth() throws IOException {
        String css = statsCss();
        assertThat(css)
                .as("число дверей в ряду снова задано медиазапросом/константой — ширина "
                        + "колонки сводки и планшета разная, и константа подходит одной из них")
                .containsPattern("\\.stats-next-actions-grid \\{[\\s\\S]{0,200}?grid-template-columns: repeat\\(auto-fit, minmax\\(\\d+rem, 1fr\\)\\)");
        assertThat(css)
                .as("дверь перестала быть строкой: число и расшифровка обязаны стоять на "
                        + "одной линии, иначе строка снова вырастает вчетверо")
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
