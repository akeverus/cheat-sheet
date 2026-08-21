package com.cheatsheet.quiz.ui;

import com.cheatsheet.quiz.common.util.RankUtils;
import com.cheatsheet.quiz.domain.GuildRank;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Витрина пути: весь путь гильдии виден целиком, а не по одной ступени за раз.
 *
 * <h2>Найденный дефект, а не гипотеза</h2>
 * Карточка прогрессии отвечала на «где я сейчас» и «сколько до следующей ступени», но
 * сам путь был невидим: названия старших ступеней («Хранитель», «Магистр») человек
 * впервые узнавал, только дойдя до них. При этом {@link GuildRank#all()} — метод,
 * написанный в домене со словами «для витрины пути», — не вызывал НИКТО, кроме тестов:
 * поиск по production-коду давал ноль вызовов.
 *
 * <h2>Замер после</h2>
 * Раскрытие добавляет карточке 386px на десктопе и 613px на телефоне; закрытое — 33px
 * (одна строка-заголовок). Семь ступеней, диапазоны выводятся из порогов: 1–2, 3–5,
 * 6–9, 10–14, 15–20, 21–29, «уровень 30 и выше». Chromium и WebKit, 390/768/1512/2560,
 * обе темы, со скриптами и без.
 *
 * <h2>Что закреплено</h2>
 * <ol>
 *   <li>витрина показывает ВСЕ ступени в порядке лестницы;</li>
 *   <li>диапазоны уровней выводятся из порогов соседних ступеней, а не пишутся руками;</li>
 *   <li>текущая ступень ровно одна и совпадает с той, что в карточке;</li>
 *   <li>раскрытие нативное (`<details>`) — работает без скриптов;</li>
 *   <li>текущая ступень помечена СЛОВОМ и `aria-current`, а не только начертанием.</li>
 * </ol>
 */
class RankLadderContractTest {

    private final RankUtils ranks = new RankUtils();

    private String fragment() throws IOException {
        return new String(new ClassPathResource("templates/fragments/progression.html").getInputStream().readAllBytes(),
                StandardCharsets.UTF_8);
    }

    @Test
    void theLadderShowsEveryRankInOrder() {
        for (GuildRank rank : GuildRank.values()) {
            List<RankUtils.RankStep> ladder = ranks.ladder(rank);
            assertThat(ladder)
                    .as("витрина пути показывает не все ступени: путь снова виден по одной")
                    .hasSize(GuildRank.values().length);
            assertThat(ladder.stream().map(RankUtils.RankStep::title).toList())
                    .as("порядок ступеней в витрине разошёлся с лестницей гильдии")
                    .isEqualTo(GuildRank.all().stream().map(GuildRank::title).toList());
        }
    }

    @Test
    void theLevelRangesFollowFromTheThresholds() {
        List<GuildRank> all = GuildRank.all();
        List<RankUtils.RankStep> ladder = ranks.ladder(GuildRank.APPRENTICE);
        for (int i = 0; i < all.size(); i++) {
            RankUtils.RankStep step = ladder.get(i);
            assertThat(step.fromLevel())
                    .as("начало ступени «%s» в витрине не равно её порогу", step.title())
                    .isEqualTo(all.get(i).minLevel());
            if (i + 1 < all.size()) {
                assertThat(step.toLevel())
                        .as("между ступенями «%s» и «%s» появилась дыра или нахлёст: диапазон "
                                + "обязан выводиться из порога следующей ступени",
                                step.title(), all.get(i + 1).title())
                        .isEqualTo(all.get(i + 1).minLevel() - 1);
                assertThat(step.levels()).contains("уровни ");
            } else {
                assertThat(step.toLevel())
                        .as("у последней ступени появился верхний уровень — продолжения нет, "
                                + "и врать об этом нельзя")
                        .isNull();
                assertThat(step.levels()).contains("и выше");
            }
        }
    }

    @Test
    void exactlyOneStepIsCurrentAndItMatchesTheRank() {
        for (GuildRank rank : GuildRank.values()) {
            List<RankUtils.RankStep> ladder = ranks.ladder(rank);
            List<RankUtils.RankStep> current = ladder.stream().filter(RankUtils.RankStep::current).toList();
            assertThat(current)
                    .as("для ступени %s текущей отмечена не одна строка витрины", rank)
                    .hasSize(1);
            assertThat(current.get(0).title())
                    .as("витрина подсвечивает не ту ступень, что показывает карточка")
                    .isEqualTo(rank.title());
        }
    }

    /**
     * Ряд ступени отвечает на ширину СВОЕЙ колонки, а не окна.
     *
     * <p>Первая сборка витрины описала ряд четырьмя колонками и сузила его
     * медиазапросом `max-width: 600px`. Экран итогов сессии это поймал: там карточка
     * прогрессии стоит в узкой колонке — <b>240px при вьюпорте 1512</b>, то есть
     * медиазапрос не срабатывает, а `auto`-колонки не сжимаются ниже содержимого.
     * Замер скриншотом: название ступени и диапазон уровней НАЛЕЗАЛИ друг на друга,
     * а у текущей строки поверх обоих печаталось «СЕЙЧАС». После правки замер
     * перекрытий по обоим экранам, четырём ширинам и двум движкам — ноль.</p>
     */
    @Test
    void theStepRowAnswersToItsContainerAndNotToTheWindow() throws IOException {
        String css = CssRules.stripComments(CssRules.read("static/css/components-progression.css"));

        assertThat(css)
                .as("витрина перестала быть контейнером запросов — ряд снова будет слушать "
                        + "окно, хотя ширину ему задаёт колонка карточки")
                .containsPattern("\\.rank-ladder \\{[^}]*container-type:\\s*inline-size");

        Matcher container = Pattern.compile("@container[^{]*\\{(.*?\\.rank-step\\s*\\{[^}]*\\}.*?)\\n\\}",
                Pattern.DOTALL).matcher(css);
        assertThat(container.find())
                .as("многоколоночный ряд ступени объявлен не в `@container`: на узкой карточке "
                        + "итогов (240px при окне 1512) колонки налезут друг на друга")
                .isTrue();
        assertThat(container.group(1))
                .as("в контейнерном запросе нет полного ряда из четырёх колонок")
                .containsPattern("grid-template-columns:\\s*32px minmax\\(0, 1fr\\) auto auto");

        for (String block : mediaBlocks(css)) {
            assertThat(block)
                    .as("ширину ряда ступени снова меняет МЕДИАзапрос: он смотрит на окно, а "
                            + "решает колонка — ровно та ошибка, что дала наложение на итогах")
                    .doesNotContainPattern("\\.rank-step\\s*\\{[^}]*grid-template-columns");
        }
    }

    /** Тела всех `@media`-блоков файла. */
    private java.util.List<String> mediaBlocks(String css) {
        java.util.List<String> blocks = new java.util.ArrayList<>();
        Matcher m = Pattern.compile("@media[^{]*\\{").matcher(css);
        while (m.find()) {
            int depth = 0;
            for (int i = m.end() - 1; i < css.length(); i++) {
                char c = css.charAt(i);
                if (c == '{') depth++;
                if (c == '}') {
                    depth--;
                    if (depth == 0) {
                        blocks.add(css.substring(m.end(), i));
                        break;
                    }
                }
            }
        }
        return blocks;
    }

    @Test
    void theLadderWorksWithoutScriptsAndNamesTheCurrentStepInWords() throws IOException {
        String markup = fragment();
        assertThat(markup)
                .as("витрина перестала быть нативным раскрытием: без скриптов путь исчезнет")
                .contains("<details class=\"rank-ladder\">");
        assertThat(markup)
                .as("витрина строится не из ступеней, а из чего-то ещё")
                .contains("@rankUtils.ladder(p.rank())");
        assertThat(markup)
                .as("текущая ступень перестала называться словом: состояние осталось только "
                        + "на начертании")
                .contains("rank-step-now")
                .contains("сейчас");
        assertThat(markup)
                .as("у текущей ступени нет `aria-current`: читалка не узнает, где человек стоит")
                .contains("aria-current=${step.current()} ? 'step' : null");
        assertThat(markup)
                .as("герб в витрине обязан быть скрыт от читалки: рядом стоит название словом")
                .containsPattern("class=\"rank-step-emblem\" aria-hidden=\"true\"");
    }
}
