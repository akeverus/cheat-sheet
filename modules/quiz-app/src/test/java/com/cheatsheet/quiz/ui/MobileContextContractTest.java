package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Экран вопроса в руке: контекст — строка состояния, работа — на первом экране.
 *
 * <h2>Найденный дефект, а не гипотеза</h2>
 * Замер (Chromium и WebKit, DPR2, экзамен с примером кода, ОДИН и тот же вопрос) —
 * высота вспомогательного рельса и положение первого варианта ответа:
 *
 * <pre>
 *   вьюпорт      360×780   390×844   430×932
 *   рельс до        168       147       147
 *   1-й вариант     1286      1216      1163      ← сгиб 780 / 844 / 932
 * </pre>
 *
 * На телефоне ни одного варианта ответа не было видно без прокрутки. Причины две, и
 * обе — следствие правил, верных для БОЛЬШОГО экрана:
 * <ol>
 *   <li>дети рельса объявлены {@code flex: 1 1 14rem} — это верно для колонки 288px
 *       справа, где каждый показатель берёт свою строку, и неверно для полосы во всю
 *       ширину телефона: шесть показателей выстраивались лестницей над вопросом;</li>
 *   <li>справка «О теме» (305px) стоит в колонке задания — на большом экране рядом с
 *       работой, а в схлопнутой стопке поперёк пути от вопроса к ответу.</li>
 * </ol>
 *
 * <h2>Что закреплено</h2>
 * <ol>
 *   <li>на телефоне рельс отвечает на один вопрос — «где я», а аналитика читается
 *       там, где собрана целиком;</li>
 *   <li>то, что ушло с телефонной полосы, доступно на странице аналитики — иначе это
 *       не перенос, а потеря;</li>
 *   <li>пока рабочее место одноколоночное, справочный материал идёт ПОСЛЕ работы, и
 *       порог этого правила смыкается с порогом самого рабочего места;</li>
 *   <li>зазоры принадлежат блокам, а не протекают из их нутра.</li>
 * </ol>
 */
class MobileContextContractTest {

    private static final Pattern CSS_COMMENT = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL);

    /** Порог телефона: рельс перестаёт быть колонкой показателей. */
    private static final String PHONE = "@media (max-width: 767px)";
    /** Порог одноколоночного рабочего места. */
    private static final String ONE_COLUMN = "@media (max-width: 1279px)";
    /** Порог, на котором рабочее место становится двухколоночным. */
    private static final String TWO_COLUMN = "@media (min-width: 1280px)";

    private String read(String classpathLocation) throws IOException {
        return new String(new ClassPathResource(classpathLocation).getInputStream().readAllBytes(),
                StandardCharsets.UTF_8);
    }

    private String focusCss() throws IOException {
        return CSS_COMMENT.matcher(read("static/css/pages-focus.css")).replaceAll("\n");
    }

    /**
     * Тела ВСЕХ медиазапросов с этим условием — по балансу скобок. Две тонкости, и
     * обе пойманы при написании гейта: одно и то же условие в файле встречается не
     * раз (порог телефона и порог одноколоночного места набраны по нескольку блоков),
     * поэтому брать первое вхождение нельзя; а внутри блока лежат вложенные правила,
     * поэтому «до первой закрывающей скобки» отрезало бы его на первом же селекторе.
     */
    private static String mediaBlocks(String css, String query) {
        StringBuilder collected = new StringBuilder();
        int from = 0;
        while (true) {
            int start = css.indexOf(query, from);
            if (start < 0) {
                break;
            }
            int open = css.indexOf('{', start);
            int depth = 0;
            for (int i = open; i < css.length(); i++) {
                char c = css.charAt(i);
                if (c == '{') {
                    depth++;
                } else if (c == '}') {
                    depth--;
                    if (depth == 0) {
                        collected.append(css, open + 1, i).append('\n');
                        from = i;
                        break;
                    }
                }
            }
            if (depth != 0) {
                throw new AssertionError("медиазапрос " + query + " не закрыт");
            }
        }
        assertThat(collected.length()).as("медиазапрос %s исчез из pages-focus.css", query).isPositive();
        return collected.toString();
    }

    /** На телефоне полоса контекста говорит «где я», а не показывает шесть чисел лестницей. */
    @Test
    void thePhoneRailIsAStatusLineAndNotAColumnOfMetrics() throws IOException {
        String phone = mediaBlocks(focusCss(), PHONE);

        assertThat(phone).as("дети рельса снова берут меру колонки (14rem) — на телефоне "
                        + "это лестница из шести показателей над вопросом, замер до правки: "
                        + "рельс 147–168px, первый вариант ответа за сгибом")
                .contains("flex: 0 1 auto");
        assertThat(phone).as("аналитика вернулась в телефонную полосу контекста")
                .contains(".focus-bank-facts")
                .contains(".today-chip")
                .contains("display: none");
        assertThat(phone).as("действия рельса перестали быть тач-целью во всю ширину")
                .contains("flex: 1 1 100%");
    }

    /**
     * Перенос, а не потеря: всё, что ушло с телефонной полосы, читается на странице
     * аналитики. Без этой проверки правило выше — просто «спрятали, чтобы влезло».
     */
    @Test
    void whatThePhoneDropsIsReadableInAnalytics() throws IOException {
        String stats = read("templates/stats.html");

        assertThat(stats).as("освоенные карточки не читаются в аналитике").contains("Освоено");
        assertThat(stats).as("точность первых попыток не читается в аналитике")
                .contains("Точность");
        assertThat(stats).as("серия занятий не читается в аналитике")
                .containsPattern("подряд");
        assertThat(stats).as("очередь к повторению не читается в аналитике")
                .contains("к повтору");
    }

    /**
     * Справочный материал не стоит между вопросом и ответом, пока место одноколоночное.
     * Замер на 390×844 (экзамен с кодом): первый вариант 1216 → 809, вопрос 282 → 211.
     */
    @Test
    void referenceMaterialFollowsTheWorkWhileTheDeskIsOneColumn() throws IOException {
        String oneColumn = mediaBlocks(focusCss(), ONE_COLUMN);

        assertThat(oneColumn).as("рабочая колонка перестала быть flex-контейнером — "
                        + "без него порядок блоков не переставить")
                .contains("display: flex")
                .contains("flex-direction: column");
        assertThat(oneColumn).as("справка о теме снова стоит между вопросом и вариантами: "
                        + "305px поперёк пути к ответу")
                .containsPattern("\\.topic-brief[^{]*\\{[^}]*order: 1");
        assertThat(oneColumn).as("хвост страницы («сообщить о проблеме») обогнал справку")
                .containsPattern("\\.report-zone \\{ order: 2");
    }

    /**
     * Порог перестановки — тот же, что у рабочего места. Разъедутся — либо справка
     * окажется в колонке задания И под работой одновременно, либо между 1279 и 1280
     * появится ширина, где она снова разрезает путь к ответу.
     */
    @Test
    void thePhoneRuleMeetsTheDeskRuleWithoutAGap() throws IOException {
        String css = focusCss();

        assertThat(css).as("порог одноколоночного места исчез").contains(ONE_COLUMN);
        assertThat(css).as("порог двухколоночного рабочего места исчез").contains(TWO_COLUMN);

        Matcher max = Pattern.compile("@media \\(max-width: (\\d+)px\\)").matcher(css);
        Matcher min = Pattern.compile("@media \\(min-width: 1280px\\)").matcher(css);
        assertThat(min.find()).isTrue();
        boolean meets = false;
        while (max.find()) {
            if (Integer.parseInt(max.group(1)) == 1279) {
                meets = true;
            }
        }
        assertThat(meets).as("правило перестановки и правило рабочего места разъехались: "
                + "между ними появилась ширина без хозяина").isTrue();
    }

    /**
     * Зазор принадлежит блоку. Замер цены flex-контекста: шапка вопроса 26→34px,
     * материалы 389→437px, документ +48px — потому что внутренние поля перестали
     * схлопываться сквозь границу родителя.
     */
    @Test
    void gapsBelongToBlocksAndDoNotLeakFromInside() throws IOException {
        String oneColumn = mediaBlocks(focusCss(), ONE_COLUMN);

        assertThat(oneColumn).as("отступ снова задаёт строка ВНУТРИ шапки вопроса — "
                        + "конструкция ломается от смены контекста форматирования")
                .containsPattern("\\.question-zone-head \\{ margin-bottom: 0");
        assertThat(oneColumn).as("поля материалов вопроса снова работают снаружи блока")
                .containsPattern("\\.question-materials > \\* \\{ margin-block: 0");
        assertThat(oneColumn).as("между несколькими материалами (код и схема) пропал ритм")
                .containsPattern("\\.question-materials > \\* \\+ \\*");
    }
}
