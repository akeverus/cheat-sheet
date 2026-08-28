package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * После сессии видно, ЧТО получилось и ЧТО делать дальше — в первом же экране.
 *
 * <h2>Найденный дефект, а не гипотеза</h2>
 * Карточка итогов держала четыре блока сразу — кольцо, счёт, ступень, плитки — и на
 * телефоне вырастала выше экрана. Замер (Chromium и WebKit, экзамен):
 *
 * <pre>
 *   вьюпорт      карточка   главное действие   сгиб
 *   390×844        903         @1155            844   ← за экраном
 *   768×1024       508          @720           1024
 *   1280×720       336          @514            720
 *   1512×982       336          @514            982
 * </pre>
 *
 * Разложение 390: кольцо 144 + счёт 173 + ступень 186 + плитки 204 плюс зазоры.
 * То есть закончив сессию на телефоне, продолжить было нельзя, не пролистав экран.
 *
 * <h2>Замер после</h2>
 * <pre>
 *   390×844   карточка счёта 275, действие @519 (в сгибе), документ 2828 → 2352
 *   430×932   238, @482
 *   1512×982  208, @484, документ 1592 → 1562
 * </pre>
 *
 * <h2>Что закреплено</h2>
 * <ol>
 *   <li>верх итогов — две карточки: «сколько» и «что это дало»;</li>
 *   <li>на узком экране порядок задаёт страница: счёт → действие → прогресс;</li>
 *   <li>порог ряда и порог перестановки — одно число;</li>
 *   <li>в карточке прогресса нет интерактивных элементов: иначе визуальный порядок
 *       разошёлся бы с порядком обхода с клавиатуры;</li>
 *   <li>на телефоне кольцо отдаёт ширину счёту.</li>
 * </ol>
 */
class SummaryTopContractTest {

    private static final Pattern HTML_COMMENT = Pattern.compile("<!--.*?-->", Pattern.DOTALL);
    private static final Pattern CSS_COMMENT = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL);

    private String read(String classpathLocation) throws IOException {
        return new String(new ClassPathResource(classpathLocation).getInputStream().readAllBytes(),
                StandardCharsets.UTF_8);
    }

    private String markup() throws IOException {
        return HTML_COMMENT.matcher(read("templates/session-summary.html")).replaceAll("");
    }

    private String css() throws IOException {
        return CSS_COMMENT.matcher(read("static/css/pages-summary.css")).replaceAll("");
    }

    /**
     * Ряд «ступень | плитки» существует только вместе с плитками (iter-182).
     *
     * <h2>Найденный живым прогоном дефект</h2>
     * У пустой сессии KPI-блока нет (th:if по noAnswers/gamification), а второй
     * трек `1fr` был безусловным: правая половина карточки прогресса стояла
     * ПУСТОЙ, ступень зажималась до 289px (rank-body 226px), и XP-строка
     * ломалась с висячим «·». Ряд включает `:has(.summary-kpis)`; сирота-точка
     * убита отдельно — сепаратора больше нет (роздых даёт column-gap), а
     * стрелка раскрытия живёт на последнем ребёнке summary, не на контейнере
     * (::after flex-контейнера переносился один на вторую строку).
     */
    @Test
    void theProgressRowExistsOnlyTogetherWithKpiTiles() throws IOException {
        String c = css();
        assertThat(c)
                .as("ряд снова безусловный: у пустой сессии вернётся пустая половина карточки")
                .contains(".summary-progress:has(.summary-kpis)");

        String progression = HTML_COMMENT.matcher(read("templates/fragments/progression.html")).replaceAll("");
        assertThat(progression)
                .as("сепаратор-точка вернулась: в узкой колонке она виснет в конце строки")
                .doesNotContain("rank-scale-sep");

        String controls = CSS_COMMENT.matcher(read("static/css/components-controls.css")).replaceAll("");
        assertThat(controls)
                .as("стрелка раскрытия обязана жить на последнем ребёнке — контейнерный "
                        + "::after переносится один на вторую строку (сирота-«▾»)")
                .contains(".disclosure-summary > :last-child::after");
        assertThat(controls)
                .doesNotContainPattern("\\.disclosure-summary::after\\s*\\{");
    }

    @Test
    void theTopOfTheSummaryIsTwoCardsAndNotOneColumn() throws IOException {
        String m = markup();
        int top = m.indexOf("<div class=\"summary-top\">");
        int hero = m.indexOf("class=\"card summary-hero\"");
        int progress = m.indexOf("class=\"card summary-progress\"");
        assertThat(top).as("обёртки верха нет — раскладке нечем управлять").isNotNegative();
        assertThat(hero).as("карточки счёта нет").isGreaterThan(top);
        assertThat(progress).as("карточки прогресса нет — ступень и плитки снова в счёте")
                .isGreaterThan(hero);

        String heroBlock = m.substring(hero, progress);
        assertThat(heroBlock)
                .as("ступень вернулась в карточку счёта: на телефоне это +186px до "
                        + "главного действия")
                .doesNotContain("progression :: rank");
        assertThat(heroBlock)
                .as("плитки показателей вернулись в карточку счёта: ещё +204px")
                .doesNotContain("summary-kpis");
    }

    /**
     * Визуальная перестановка допустима ровно потому, что переставляемый блок не
     * содержит ничего фокусируемого: обход с клавиатуры идёт по разметке.
     */
    @Test
    void theProgressCardHasNothingFocusable() throws IOException {
        String m = markup();
        // Границы блока — по балансу `</section>`. Первая редакция искала маркер
        // `</div><!--/* /.summary-top */-->`, но комментарии снимаются фильтром выше:
        // маркера в тексте не оставалось, блок тянулся до конца документа и втягивал
        // ссылки из «Что дальше» — гейт падал на верном коде.
        int start = m.indexOf("class=\"card summary-progress\"");
        assertThat(start).isNotNegative();
        String block = sectionBody(m, start);
        assertThat(block)
                .as("в карточке прогресса появился интерактивный элемент — визуальный "
                        + "порядок разойдётся с порядком обхода (WCAG 2.4.3)")
                .doesNotContain("<a ").doesNotContain("<button").doesNotContain("<input")
                .doesNotContain("tabindex");
    }

    @Test
    void onNarrowScreensThePageDecidesTheOrder() throws IOException {
        String c = css();
        assertThat(c)
                .as("обёртка верха не растворяется на узком экране — порядок задать нечем")
                .containsPattern("\\.summary-top \\{ display: contents; }");
        int actions = c.indexOf(".summary-actions { order:");
        int progress = c.indexOf(".summary-progress { order:");
        assertThat(actions).as("порядок действий не задан").isNotNegative();
        assertThat(progress).as("порядок прогресса не задан").isNotNegative();

        int actionOrder = orderValue(c, ".summary-actions { order:");
        int progressOrder = orderValue(c, ".summary-progress { order:");
        assertThat(actionOrder)
                .as("прогресс снова стоит выше действия: на телефоне это возвращает "
                        + "главное действие за сгиб")
                .isLessThan(progressOrder);
    }

    /**
     * Карточки ряда выровнены по ОБЕИМ кромкам. Карточка счёта фиксированные 208px, а
     * карточка прогресса 278–363 в зависимости от ширины: `align-items: start` оставлял
     * под счётом рваный край — 70px пустоты на 2560 и 155px на 1512, читавшиеся как
     * незаполненный layout. Стретч выравнивает ряд (замер после: разница высот 0 на
     * 1100/1280/1512/2560 в обоих движках), а кольцо остаётся вертикально
     * центрированным собственным флексом карточки (замер: зазоры 67/67…167/167).
     */
    @Test
    void theRowCardsShareBothEdges() throws IOException {
        String c = css();
        java.util.regex.Matcher row = Pattern.compile(
                "@media \\(min-width: 1100px\\) \\{[\\s\\S]{0,400}?\\.summary-top \\{([^}]*)\\}").matcher(c);
        assertThat(row.find()).as("блока ряда нет").isTrue();
        assertThat(row.group(1))
                .as("ряд снова `align-items: start`: под короткой карточкой счёта остаётся "
                        + "рваный край 70–155px, который читается как сломанный layout")
                .contains("align-items: stretch");
    }

    /** Порог ряда и порог перестановки — одно событие «есть ли ширина на две колонки». */
    @Test
    void bothThresholdsAreTheSameNumber() throws IOException {
        String c = css();
        assertThat(c)
                .as("ряд верха включается не на 1100 — тогда порог перестановки надо пересчитать")
                .containsPattern("@media \\(min-width: 1100px\\) \\{[\\s\\S]{0,400}?\\.summary-top \\{");
        assertThat(c)
                .as("перестановка действует не до 1099 — между порогами появится полоса, "
                        + "где нет ни ряда, ни правильного порядка")
                .containsPattern("@media \\(max-width: 1099px\\) \\{[\\s\\S]{0,600}?\\.summary-top \\{ display: contents; }");
    }

    @Test
    void onPhonesTheRingGivesItsWidthToTheScore() throws IOException {
        String c = css();
        assertThat(c)
                .as("кольцо не уменьшается на телефоне: при полосе карточки 279 счёту "
                        + "остаётся 103px и «0 из 1 верных ответов» ломается на три строки")
                .containsPattern("@media \\(max-width: 600px\\) \\{[\\s\\S]{0,300}?--score-ring-size: \\d+px");
    }

    /**
     * Число плиток KPI в ряду выбирает ширина, а не константа (та же доктрина, что у
     * дверей аналитики). Сетка `repeat(4, 1fr)` и `flex: 1 1 560px` остались от эпохи,
     * когда полоса жила в карточке счёта во всю её ширину; с iter-142 плитки стоят в
     * колонке карточки прогресса (125–294px), и константа давала плитку 38–81px при
     * бюджете значения 110px — замер показал переполнение ВСЕХ плиток на 1280–1512 и
     * «Серии» даже на 2560, а четвёртая колонка пустовала (плиток три). После правки
     * плитки 141–279px, переполнений 0 на 360…2560 в обоих движках.
     */
    @Test
    void theKpiColumnCountFollowsTheWidth() throws IOException {
        String shell = CssRules.stripComments(CssRules.read("static/css/shell.css"));
        java.util.regex.Matcher kpis = Pattern.compile(
                "\\.summary-kpis \\{([^}]*)\\}").matcher(shell);
        assertThat(kpis.find()).as("правила полосы KPI нет").isTrue();
        String body = kpis.group(1);
        assertThat(body)
                .as("число колонок KPI снова константа: в узкой колонке карточки прогресса "
                        + "плитки сожмутся ниже своего бюджета (замер: 38–81px при бюджете 136)")
                .containsPattern("repeat\\(auto-fit, minmax\\(min\\(8\\.5rem, 100%\\), 1fr\\)\\)");
        assertThat(body)
                .as("`flex` вернулся на полосу KPI: в обоих её домах (грид карточки и блочная "
                        + "стопка) он мёртв и лишь маскирует настоящую раскладку")
                .doesNotContain("flex:");
        // Разбором правил, а не регуляркой по сырому тексту: первая редакция искала
        // `@media…[^@]*.summary-kpis` и ловила ЛОЖНОЕ срабатывание — `[^@]*`
        // перепрыгивал закрывающую скобку медиаблока и дотягивался до правила вне его.
        java.util.List<CssRules.Rule> insideMedia = CssRules.parse("static/css/shell.css").stream()
                .filter(r -> r.hasSelector("html[data-design] .summary-kpis"))
                .filter(r -> r.media() != null)
                .filter(r -> r.value("grid-template-columns") != null)
                .toList();
        assertThat(insideMedia)
                .as("медиазапрос снова решает число колонок KPI: auto-fit делает это из ширины")
                .isEmpty();
    }

    /** Тело секции от её открывающего тега до парного `</section>`. */
    private String sectionBody(String markup, int from) {
        int depth = 0;
        int i = from;
        while (i < markup.length()) {
            int open = markup.indexOf("<section", i + 1);
            int close = markup.indexOf("</section>", i + 1);
            if (close < 0) break;
            if (open >= 0 && open < close) {
                depth++;
                i = open;
            } else {
                if (depth == 0) return markup.substring(from, close);
                depth--;
                i = close;
            }
        }
        throw new AssertionError("секция прогресса не закрыта");
    }

    /** Значение `order` у правила: берём первую цифру после двоеточия. */
    private int orderValue(String css, String selector) {
        java.util.regex.Matcher m = Pattern.compile(Pattern.quote(selector) + "\\s*(\\d+)").matcher(css);
        assertThat(m.find()).as("правила `order` для %s нет", selector).isTrue();
        return Integer.parseInt(m.group(1));
    }
}
