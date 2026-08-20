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
