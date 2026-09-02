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
        // Эволюция пина (viewport-first, 2026-09-01): прежний гейт требовал, чтобы
        // сводка на ≥1280 становилась ДВУМЯ КОЛОННАМИ («что дальше» 27rem | «где я»).
        // Замер 1440×900 показал цену колонн: левая кончалась на 605, правая на 970,
        // под дверями пустовало 432×365 первого экрана, двери в колонке 210px ломали
        // подписи, а вкладки не попадали в viewport (сводка 824px). Сводка теперь
        // ЛЕНТАМИ: ряд дверей на всю ширину, под ним ряд «ступень | метрики»
        // (базовая раскладка .stats-standing ≥1200). Гейт держит порядок внутри
        // обёртки и НЕвозвращение колонной раскладки.
        String markup = statsMarkup();
        int summary = markup.indexOf("<div class=\"stats-summary\">");
        int actions = markup.indexOf("class=\"stats-next-actions\"");
        int standing = markup.indexOf("<div class=\"stats-standing\">");
        assertThat(summary).as("обёртки сводки нет — действия и стойка снова этажами").isNotNegative();
        assertThat(summary).as("действия обязаны лежать внутри сводки").isLessThan(actions);
        assertThat(actions).as("стойка обязана лежать в той же сводке").isLessThan(standing);

        String css = statsCss();
        assertThat(css)
                .as("колонная сводка (узкие двери слева от стойки) вернулась — с ней "
                        + "вернутся пустота под дверями и вкладки за сгибом")
                .doesNotContain("minmax(0, 27rem)");
        assertThat(css)
                .as("двери обязаны раскладываться рядом по доступной ширине")
                .containsPattern("\\.stats-next-actions-grid \\{[\\s\\S]{0,300}?repeat\\(auto-fit");
    }

    /**
     * Стойка «где я» на телефоне сворачивается, и свёрнутая шапка несёт факты.
     *
     * <p>Замер 390×844 (iter-157): между дверями «что дальше» и вкладками разделов
     * лежали 706px статуса (ранг 319 + четыре метрики 283 + легенда), и вкладки —
     * рабочий интерфейс страницы — вставали на 1505-й пиксель при сгибе 844. После
     * правки вкладки на 917 (документ 2493 против 3081), на 430 — в первом экране.
     * Тот же довод и тот же приём, что у витрины знаков (766px) и формы отбора
     * (345px): статус смотрят мельком, разделами работают.</p>
     */
    @Test
    void theStandingCollapsesOnPhonesAndItsSummaryCarriesTheFacts() throws IOException {
        String markup = statsMarkup();
        assertThat(markup)
                .as("стойка «где я» перестала быть раскрытием: на телефоне 706px статуса "
                        + "снова встанут между дверями и разделами")
                .containsPattern("<details class=\"stats-standing-disclosure\" open data-mobile-collapsed>");
        assertThat(markup)
                .as("шапка стойки не общий компонент раскрытия — второй рисунок одного элемента")
                .contains("class=\"disclosure-summary stats-standing-summary\"");
        assertThat(markup)
                .as("в свёрнутой шапке нет фактов: свёрнутый статус обязан отвечать «где я» "
                        + "одной строкой — ступень, уровень, точность")
                .containsPattern("progression\\.rank\\(\\)\\.title\\(\\).+уровень");
        assertThat(markup)
                .as("точность в шапке считается не тем же выражением, что плитка «Точность "
                        + "попыток», — при первой правке форматирования они разойдутся")
                .containsPattern("stats-standing-lead-tail[^>]*th:if=\"\\$\\{metrics != null and metrics\\.hasOverallAccuracy\\(\\)}\"");
        // Маркер-комментарий у закрывающего тега срезает фильтр комментариев этого же
        // гейта (тот же случай ловил SummaryTopContractTest в iter-142), поэтому
        // закрытие проверяется БАЛАНСОМ: до вкладок разделов все открытые <details>
        // обязаны закрыться — иначе стойка проглотила бы и вкладки, и панели.
        // Точка отсчёта — начало ТЕГА, а не имени класса: класс стоит внутри тега, и
        // сегмент от него терял первый `<details`, из-за чего баланс всегда бился.
        int details = markup.indexOf("<details class=\"stats-standing-disclosure\"");
        int standing = markup.indexOf("<div class=\"stats-standing\">");
        int tabs = markup.indexOf("id=\"stats-tablist\"");
        assertThat(details).isNotNegative();
        assertThat(standing).as("стойка ушла из-под раскрытия").isGreaterThan(details);
        assertThat(tabs).as("вкладок разделов нет").isGreaterThan(standing);
        String beforeTabs = markup.substring(details, tabs);
        long opened = Pattern.compile("<details").matcher(beforeTabs).results().count();
        long closed = Pattern.compile("</details>").matcher(beforeTabs).results().count();
        assertThat(closed)
                .as("раскрытие стойки не закрывается до вкладок: вкладки и панели окажутся "
                        + "ВНУТРИ свёрнутого статуса и исчезнут вместе с ним")
                .isEqualTo(opened);

        // Порог сворачивания — до 1199, а не заводской 767 (iter-158): планшетная щель
        // 768–1199 давала вкладкам 1326–1375-й пиксель при сгибах 768–1194. Замер после:
        // 626/608/645 — в первом экране. Планшетный ряд стойки вместо сворачивания
        // ОТОЗВАН замером: плитка метрики в колонке 340px оказалась ~165px высотой,
        // и стойка выросла с 684 до 714.
        String statsJs = CssRules.read("static/js/stats/index.js");
        assertThat(statsJs)
                .as("аналитика вернулась к заводскому порогу сворачивания (767): на "
                        + "планшетах 768–1199 стойка снова встанет столбом 585–700px "
                        + "между дверями и разделами")
                .contains("initMobileDisclosures('(max-width: 1199px)')");
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
                .as("шапка стойки печатает безымянную «точность»: рядом с «Первой попыткой» "
                        + "панели деталей это читается как противоречие, а не как вторая метрика")
                .containsPattern("stats-standing-lead-tail[\\s\\S]{0,200}?точность попыток");
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
