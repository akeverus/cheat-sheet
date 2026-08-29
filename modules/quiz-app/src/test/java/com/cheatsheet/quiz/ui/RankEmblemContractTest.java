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
 *   <li>у каждой ступени есть герб `rank-*`, и все семь разные;</li>
 *   <li>каждый герб существует в спрайте (`fragments/icons.html`);</li>
 *   <li>карточка берёт герб из ступени, а не печатает знак навигации;</li>
 *   <li>герб не единственный носитель смысла: рядом стоит название ступени словом.</li>
 * </ol>
 *
 * <p>Язык самих символов (сетка 24×24, отсутствие цвета внутри `<symbol>`) —
 * контракт всего спрайта, его стережёт {@code IconLanguageContractTest};
 * здесь эти проверки не дублируются.</p>
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
            assertThat(glyph)
                    .as("герб ступени %s зовётся не по конвенции `rank-*` спрайта", rank)
                    .startsWith("rank-");
            assertThat(seen.add(glyph))
                    .as("герб `%s` достался двум ступеням: лестница снова не читается — "
                            + "именно так «Ученик» и «Магистр» выглядели одинаково", glyph)
                    .isTrue();
        }
        assertThat(seen).as("гербов меньше, чем ступеней").hasSize(GuildRank.values().length);
    }

    /**
     * Язык символа (24×24, без цвета внутри) здесь не перепроверяется:
     * его для всего спрайта держит {@code IconLanguageContractTest}.
     */
    @Test
    void everyEmblemExistsInTheSprite() throws IOException {
        List<String> ids = new ArrayList<>();
        Matcher m = SYMBOL.matcher(sprite());
        while (m.find()) ids.add(m.group(1));

        for (GuildRank rank : GuildRank.values()) {
            String glyph = ranks.glyph(rank);
            assertThat(ids)
                    .as("герба `%s` нет в спрайте: `<use href=\"#%s\">` нарисует пустоту", glyph, glyph)
                    .contains(glyph);
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
