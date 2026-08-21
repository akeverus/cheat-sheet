package com.cheatsheet.quiz.ui;

import com.cheatsheet.quiz.common.util.RankUtils;
import com.cheatsheet.quiz.domain.GuildRank;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * У каждой ступени гильдии свой герб.
 *
 * <h2>Найденный дефект, а не гипотеза</h2>
 * Ступеней семь ({@link GuildRank}: Ученик → Подмастерье → Ремесленник → Мастер →
 * Старший мастер → Хранитель → Магистр), а карточка прогрессии рисовала один и тот же
 * знак — щит раздела «Тренировка» из навигации (`<use href="#training">`). Замер живой
 * страницы: эмблема 48px на десктопе и 32px на телефоне, единственная картинка карточки,
 * и она не отличала «Ученика» от «Магистра». Вдобавок тот же щит стоит в меню, поэтому
 * карточка читалась как повтор пункта навигации.
 *
 * <h2>Что закреплено</h2>
 * <ol>
 *   <li>у каждой ступени есть герб, и все семь разные;</li>
 *   <li>каждый герб существует в спрайте (`fragments/icons.html`);</li>
 *   <li>гербы нарисованы на той же сетке и тем же языком, что остальные знаки:
 *       `viewBox="0 0 16 16"`, `currentColor`, `crispEdges`, только прогоны `<rect>`;</li>
 *   <li>карточка берёт герб из ступени, а не печатает знак навигации;</li>
 *   <li>герб не единственный носитель смысла: рядом стоит название ступени словом.</li>
 * </ol>
 */
class RankEmblemContractTest {

    private static final Pattern SYMBOL =
            Pattern.compile("<symbol id=\"([^\"]+)\" viewBox=\"([^\"]+)\">(.*?)</symbol>", Pattern.DOTALL);

    private final RankUtils ranks = new RankUtils();

    private String sprite() throws IOException {
        return new String(new ClassPathResource("templates/fragments/icons.html").getInputStream().readAllBytes(),
                StandardCharsets.UTF_8);
    }

    private String progression() throws IOException {
        return new String(new ClassPathResource("templates/fragments/progression.html").getInputStream().readAllBytes(),
                StandardCharsets.UTF_8);
    }

    @Test
    void everyRankHasItsOwnEmblem() {
        Set<String> seen = new HashSet<>();
        for (GuildRank rank : GuildRank.values()) {
            String glyph = ranks.glyph(rank);
            assertThat(glyph).as("у ступени %s нет герба", rank).isNotBlank();
            assertThat(seen.add(glyph))
                    .as("герб `%s` достался двум ступеням: лестница снова не читается — "
                            + "именно так «Ученик» и «Магистр» выглядели одинаково", glyph)
                    .isTrue();
        }
        assertThat(seen).as("гербов меньше, чем ступеней").hasSize(GuildRank.values().length);
    }

    @Test
    void everyEmblemExistsInTheSpriteAndSpeaksTheSameLanguage() throws IOException {
        String sprite = sprite();
        List<String> ids = new ArrayList<>();
        Matcher m = SYMBOL.matcher(sprite);
        while (m.find()) ids.add(m.group(1));

        for (GuildRank rank : GuildRank.values()) {
            String glyph = ranks.glyph(rank);
            assertThat(ids)
                    .as("герба `%s` нет в спрайте: `<use href=\"#%s\">` нарисует пустоту", glyph, glyph)
                    .contains(glyph);

            Matcher one = Pattern.compile(
                    "<symbol id=\"" + Pattern.quote(glyph) + "\" viewBox=\"([^\"]+)\">(.*?)</symbol>",
                    Pattern.DOTALL).matcher(sprite);
            assertThat(one.find()).as("символ %s не разобрался", glyph).isTrue();
            assertThat(one.group(1))
                    .as("герб %s нарисован не на сетке 16×16 — целый масштаб (32/48px) размоется",
                            glyph)
                    .isEqualTo("0 0 16 16");
            String body = one.group(2);
            assertThat(body)
                    .as("герб %s не наследует цвет темы", glyph)
                    .contains("currentColor")
                    .as("герб %s без crispEdges — пиксель поедет на дробном масштабе", glyph)
                    .contains("crispEdges");
            assertThat(body.replaceAll("<rect[^>]*/>", "").replaceAll("<g[^>]*>|</g>", "").trim())
                    .as("герб %s нарисован не прогонами `<rect>`: язык спрайта — пиксельная "
                            + "сетка, а не произвольные контуры", glyph)
                    .isEmpty();
        }
    }

    @Test
    void theCardTakesTheEmblemFromTheRankAndNamesItInWords() throws IOException {
        String fragment = progression();
        assertThat(fragment)
                .as("карточка прогрессии снова печатает знак навигации: у всех семи ступеней "
                        + "один и тот же герб")
                .doesNotContain("<use href=\"#training\">");
        assertThat(fragment)
                .as("герб берётся не из ступени — значит он снова ничего о ней не говорит")
                .contains("@rankUtils.glyph(p.rank())");
        assertThat(fragment)
                .as("название ступени пропало из карточки: тогда единственным носителем "
                        + "смысла остаётся картинка")
                .contains("p.rank().title()");
    }
}
