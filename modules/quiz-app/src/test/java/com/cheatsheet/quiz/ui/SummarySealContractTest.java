package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Печать заверяет ВСЮ страницу итогов, а не левый столбец разбора.
 *
 * <h2>Найденный дефект, а не гипотеза</h2>
 * Сцена «запечатанная хроника» (320×200) — единственный декоративный объект итогов, и
 * поставлена она была осознанно: закрыть пустоту, которая на 1920 и 2560 остаётся после
 * разбора. Но `.summary-seal` — ПРЯМОЙ ребёнок сетки разбора, а та с 1200px становится
 * двухколоночной. Значит печать вставала грид-элементом в первую колонку и центрировалась
 * в ней:
 *
 * <pre>
 *   вьюпорт     центр сцены   центр полосы   смещение
 *   1920×1200      683           1069          −386
 *   2560×1440      977           1391          −414
 * </pre>
 *
 * То есть на широком экране печать читалась как случайный значок под левой карточкой.
 * На 1512 и уже сетка одноколоночная, и дефекта не видно — он живёт ровно там, где разбор
 * встаёт в два столбца. После правки смещение 0 на 390/1512/1920/2560 в обоих движках.
 *
 * <h2>Что закреплено</h2>
 * <ol>
 *   <li>печать растянута на все колонки сетки разбора;</li>
 *   <li>она остаётся ВНУТРИ этой сетки (иначе правило выше становится мёртвым, и это
 *       надо заметить, а не унаследовать молча);</li>
 *   <li>печати нет, когда заверять нечего — после сессии без ответов;</li>
 *   <li>сцена декоративна: `aria-hidden`, смысл итогов держится на числах.</li>
 * </ol>
 */
class SummarySealContractTest {

    private List<CssRules.Rule> summaryCss() throws IOException {
        return CssRules.parse("static/css/pages-summary.css");
    }

    private String markup() throws IOException {
        return CssRules.stripComments(CssRules.read("templates/session-summary.html"))
                .replaceAll("(?s)<!--.*?-->", "");
    }

    @Test
    void theSealSpansTheWholeBand() throws IOException {
        List<CssRules.Rule> rules = summaryCss().stream()
                .filter(r -> r.hasSelector("html[data-design] .summary-page .summary-seal"))
                .toList();
        assertThat(rules).as("правила печати нет вовсе").isNotEmpty();
        String span = rules.get(rules.size() - 1).value("grid-column");
        assertThat(span)
                .as("печать снова грид-элемент одной колонки: на 1920 и 2560 она уезжает "
                        + "на 386–414px от центра полосы и читается как значок под левой карточкой")
                .isNotNull();
        assertThat(span.replace(" ", ""))
                .as("печать растянута не на все колонки: %s", span)
                .isEqualTo("1/-1");
    }

    @Test
    void theSealStaysInsideTheBreakdownGrid() throws IOException {
        String html = markup();
        int grid = html.indexOf("class=\"summary-breakdown\"");
        int seal = html.indexOf("class=\"summary-seal\"");
        assertThat(grid).as("сетки разбора нет").isNotNegative();
        assertThat(seal).as("печати нет в разметке").isGreaterThan(grid);
        // печать обязана закрываться ДО конца сетки: считаем баланс <div> между ними
        String between = html.substring(grid, seal);
        long open = Pattern.compile("<div").matcher(between).results().count();
        long close = Pattern.compile("</div>").matcher(between).results().count();
        assertThat(open)
                .as("печать уехала за пределы сетки разбора — правило `grid-column` стало "
                        + "мёртвым, и центрирование теперь держится ни на чём")
                .isGreaterThan(close);
    }

    @Test
    void thereIsNoSealWhenThereIsNothingToSeal() throws IOException {
        String html = markup();
        assertThat(html)
                .as("печать печатается даже после сессии без ответов: заверять нечего, и "
                        + "картинка читается насмешкой")
                .containsPattern("class=\"summary-seal\"[^>]*th:unless=\"\\$\\{noAnswers}\"");
    }

    @Test
    void theSceneIsDecorativeOnly() throws IOException {
        String html = markup();
        assertThat(html)
                .as("сцена перестала быть декоративной: итог обязан держаться на числах, "
                        + "а не на картинке")
                .containsPattern("class=\"summary-scene\" aria-hidden=\"true\"");
    }
}
