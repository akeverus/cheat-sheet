package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * На очень широком экране лишняя ширина достаётся МАТЕРИАЛУ, а не полям страницы.
 *
 * <h2>Найденный дефект, а не гипотеза</h2>
 * Колонка задания мерялась по самому широкому жильцу — листингу (~750px), и это было
 * верно, пока схема вписывалась в ширину колонки. С iter-146 масштаб схемы задаёт
 * читаемость подписи, то есть у схемы появилась СВОЯ ширина, и она больше. Замер
 * (Chromium, вопрос со схемой собственной ширины 970px):
 *
 * <pre>
 *   вьюпорт   полоса   рабочее место   колонки            поле/сторона   прокрутка схемы
 *   1680      1442     1307            747 + 512          67             250
 *   1728      1488     1350            768 + 534          69             229
 *   1920      1672     1456            768 + 640          108            229
 *   2560      2309     1856            768 + 640 + 352    227            229
 * </pre>
 *
 * С 1728 колонка замерзала на 768, а прибавка ширины уходила в поля: на 2560 это 454px
 * полей при схеме, которую в тот же момент прокручивают вбок на 229px.
 *
 * <h2>Замер после</h2>
 * A/B на одной странице 2560×1440 (одни и те же 40 схем корпуса, разница только в
 * токене колонки): без боковой прокрутки 13 из 40 → 20 из 40, медиана ширины схемы
 * 750 → 942px, медиана самой мелкой подписи 19.6 → 22.9px, суммарная прокрутка
 * 2004 → 1051px. Колонки на 2560 — 960 + 640 + 352, поля 227 → 131.
 *
 * <h2>Что закреплено</h2>
 * <ol>
 *   <li>расширение включается ОТДЕЛЬНЫМ медиаблоком, а трёхколоночная сетка остаётся
 *       под своим порогом 2240 (первая сборка вложила правило внутрь блока 2240 и
 *       разорвала его: на 2240 и 2400 композиция теряла третью колонку, а работа
 *       раздувалась до 816px);</li>
 *   <li>расширение действует только там, где в колонке ЕСТЬ материал;</li>
 *   <li>порог включения не ниже суммы колонок — иначе расширение отнимает меру у
 *       работы (замер кромки: при пороге 2440 Chromium давал 960 + 621 + 352, то есть
 *       работа теряла 19px там, где правило обещало её не трогать).</li>
 * </ol>
 */
class UltrawideWorkspaceContractTest {

    /** Замеренные величины окружения, которых нет в CSS-числах. */
    private static final int SIDEBAR_PX = 236;      // --sidebar-width, верхняя граница clamp
    private static final int SCROLLBAR_PX = 15;     // полоса прокрутки Chromium на макбуке
    private static final double PAGE_GUTTER_PX = 76.8; // замер .ed-page padding-inline на 2560
    private static final int ROOT_FONT_PX = 16;

    private List<CssRules.Rule> focus() throws IOException {
        return CssRules.parse("static/css/pages-focus.css");
    }

    private CssRules.Rule ultrawideBrief(List<CssRules.Rule> rules) {
        List<CssRules.Rule> found = rules.stream()
                .filter(r -> r.value("--workspace-brief") != null)
                .filter(r -> r.selectors().contains(":has(.question-materials)"))
                .toList();
        assertThat(found)
                .as("правила «широкая колонка задания при наличии материала» больше нет: "
                        + "лишняя ширина снова уходит в поля страницы")
                .isNotEmpty();
        return found.get(found.size() - 1);
    }

    private double rem(String value) {
        Matcher m = Pattern.compile("([\\d.]+)rem").matcher(value);
        assertThat(m.find()).as("значение не в rem: %s", value).isTrue();
        return Double.parseDouble(m.group(1));
    }

    private int mediaMinWidth(String media) {
        assertThat(media).as("правило вне медиазапроса").isNotNull();
        Matcher m = Pattern.compile("min-width:\\s*(\\d+)px").matcher(media);
        assertThat(m.find()).as("в условии нет min-width: %s", media).isTrue();
        return Integer.parseInt(m.group(1));
    }

    @Test
    void theWiderBriefOnlyAppliesWhereTheColumnHasMaterial() throws IOException {
        List<CssRules.Rule> rules = focus();
        CssRules.Rule wide = ultrawideBrief(rules);
        assertThat(rem(wide.value("--workspace-brief")))
                .as("широкая колонка перестала быть шире базовой 48rem — правило ничего не делает")
                .isGreaterThan(48);

        assertThat(rules.stream()
                .anyMatch(r -> r.selectors().contains(":not(:has(.question-materials))")
                        && r.value("--workspace-brief") != null))
                .as("правило «без материала колонка узкая» пропало: под вопросом без "
                        + "листинга и схемы снова окажется 768×900 пустоты")
                .isTrue();
    }

    /**
     * Структурная проверка: расширение живёт в своём блоке, а трёхзонная сетка — в своём.
     */
    @Test
    void theThreeColumnGridKeepsItsOwnThreshold() throws IOException {
        List<CssRules.Rule> rules = focus();
        List<CssRules.Rule> threeColumn = rules.stream()
                .filter(r -> {
                    String value = r.value("grid-template-columns");
                    return value != null && value.contains("--workspace-companions")
                            && value.contains("--workspace-brief");
                })
                .toList();
        assertThat(threeColumn).as("трёхзонной сетки рабочего места больше нет").isNotEmpty();
        int gridEdge = mediaMinWidth(threeColumn.get(threeColumn.size() - 1).media());
        int briefEdge = mediaMinWidth(ultrawideBrief(rules).media());
        assertThat(gridEdge)
                .as("сетка и расширение колонки уехали под ОДИН порог: тогда на ширинах между "
                        + "ними композиция теряет третью колонку, а работа раздувается "
                        + "(замер той ошибки: 2240 и 2400 давали 768 + 816 без спутников)")
                .isLessThan(briefEdge);
    }

    /**
     * Порог включения обязан быть не ниже суммы того, что в него встаёт. Иначе правило
     * «не отнимаем меру у работы» нарушается самим правилом.
     */
    @Test
    void theThresholdIsNotLowerThanTheSumOfTheColumns() throws IOException {
        List<CssRules.Rule> rules = focus();
        CssRules.Rule wide = ultrawideBrief(rules);

        double brief = rem(wide.value("--workspace-brief"));
        double companions = rules.stream()
                .map(r -> r.value("--workspace-companions"))
                .filter(java.util.Objects::nonNull)
                .findFirst().map(this::rem)
                .orElseThrow(() -> new AssertionError("меры колонки спутников нет"));

        String measure = rules.stream()
                .map(r -> r.value("--focus-main-measure"))
                .filter(java.util.Objects::nonNull)
                .filter(v -> v.contains("--workspace-brief"))
                .findFirst().orElseThrow(() -> new AssertionError("формулы меры рабочего места нет"));
        Matcher work = Pattern.compile("\\+\\s*([\\d.]+)rem").matcher(measure);
        assertThat(work.find()).as("в формуле меры нет слагаемого работы: %s", measure).isTrue();
        double desk = Double.parseDouble(work.group(1));

        String gapToken = CssRules.stripComments(CssRules.read("static/css/tokens.css"));
        Matcher gapMatcher = Pattern.compile("--focus-rail-gap:[^;]*?([\\d.]+)rem\\s*\\)").matcher(gapToken);
        assertThat(gapMatcher.find()).as("значения жёлоба рельса нет в токенах").isTrue();
        double gap = Double.parseDouble(gapMatcher.group(1));

        double columnsPx = (brief + desk + companions + gap * 2) * ROOT_FONT_PX;
        double needed = columnsPx + PAGE_GUTTER_PX * 2 + SIDEBAR_PX + SCROLLBAR_PX;
        assertThat((double) mediaMinWidth(wide.media()))
                .as("порог %spx ниже суммы колонок (%.0f + поля + сайдбар = %.0f): расширенная "
                        + "колонка отнимет ширину у работы ровно там, где правило обещает обратное",
                        mediaMinWidth(wide.media()), columnsPx, needed)
                .isGreaterThanOrEqualTo(needed);
    }
}
