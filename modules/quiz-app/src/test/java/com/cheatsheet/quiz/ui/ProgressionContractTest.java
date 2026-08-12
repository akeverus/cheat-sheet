package com.cheatsheet.quiz.ui;

import com.cheatsheet.quiz.domain.GuildRank;
import com.cheatsheet.quiz.domain.Progression;
import com.cheatsheet.quiz.domain.UserExperience;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Место в пути показано честно и в одном виде.
 *
 * <h2>Найденный дефект, а не гипотеза</h2>
 * Прогрессия была честной (XP суммируется из реальных попыток, уровень выводится формулой),
 * но видел её человек ровно в одном месте: подпись 11px серым под числом опыта на экране
 * итогов — «Уровень 7 · до следующего 902 XP». Ни аналитика, ни экран тренировки уровня не
 * показывали, а до итогов доезжают не все режимы: {@code TRAINING} идёт без сессии вовсе.
 * И само число ни на что не отвечало: цена седьмого уровня — 1300 XP, то есть пройдено 398,
 * почти треть, но узнать это было неоткуда.
 *
 * <h2>Что закреплено</h2>
 * <ol>
 *   <li>лестница рангов покрывает уровни без дыр и повторов;</li>
 *   <li>шкала не врёт: заполненность считается вниз, 100% означает взятый уровень;</li>
 *   <li>компонент один на оба экрана, и коробку ему даёт хозяин;</li>
 *   <li>полоса — нативный {@code <progress>} (работает без JS и без inline-стилей, которые
 *       запрещены CSP), а её значение продублировано прозой.</li>
 * </ol>
 */
class ProgressionContractTest {

    private static final Pattern COMMENT = Pattern.compile("<!--.*?-->", Pattern.DOTALL);

    private String read(String classpathLocation) throws IOException {
        return new String(new ClassPathResource(classpathLocation).getInputStream().readAllBytes(),
                StandardCharsets.UTF_8);
    }

    /** У каждого уровня есть ровно одна ступень, и названия не повторяются. */
    @Test
    void rankLadderCoversEveryLevelWithoutGapsOrDuplicates() {
        Set<String> titles = new LinkedHashSet<>();
        for (GuildRank rank : GuildRank.all()) {
            assertThat(titles.add(rank.title())).as("ступень «%s» объявлена дважды", rank.title()).isTrue();
        }

        List<Integer> starts = GuildRank.all().stream().map(GuildRank::minLevel).toList();
        assertThat(starts).as("лестница обязана начинаться с первого уровня").startsWith(1);
        assertThat(starts).as("ступени идут по возрастанию, иначе forLevel вернёт не ту")
                .isSorted();

        // 1..60 — с запасом: до 30-го уровня нужно 84 100 XP, дальше ступеней нет by design.
        for (int level = 1; level <= 60; level++) {
            GuildRank rank = GuildRank.forLevel(level);
            assertThat(rank).as("уровень %s остался без ступени", level).isNotNull();
            assertThat(rank.minLevel()).as("ступень «%s» не могла начаться позже уровня %s",
                    rank.title(), level).isLessThanOrEqualTo(level);
        }
        assertThat(GuildRank.all().get(GuildRank.all().size() - 1).next())
                .as("у последней ступени не бывает следующей — врать об этом нельзя").isEmpty();
    }

    /**
     * Округление вниз — не мелочь: «100%» обязано означать взятый уровень. При округлении
     * вверх полоса показывала бы полную заливку на 99.6%, то есть врала бы ровно в той точке,
     * ради которой на неё смотрят.
     */
    @Test
    void levelScaleNeverOverstatesProgress() {
        Progression fresh = Progression.of(UserExperience.EMPTY, 0);
        assertThat(fresh.levelPercent()).isZero();
        assertThat(fresh.rank()).isEqualTo(GuildRank.APPRENTICE);
        assertThat(fresh.level()).isEqualTo(1);

        // 99.6% пройденного уровня — это ещё не уровень.
        Progression almost = new Progression(GuildRank.CRAFTSMAN, 7, 1295, 1300, 5000, 3, 9);
        assertThat(almost.levelPercent()).isEqualTo(99);
        assertThat(almost.xpLeftToNextLevel()).isEqualTo(5);

        Progression full = new Progression(GuildRank.CRAFTSMAN, 7, 1300, 1300, 5000, 3, 9);
        assertThat(full.levelPercent()).isEqualTo(100);
        assertThat(full.xpLeftToNextLevel()).isZero();

        // Расстояние до ступени меряется в уровнях; у последней ступени его нет.
        assertThat(new Progression(GuildRank.CRAFTSMAN, 7, 0, 1300, 0, 0, 0).levelsToNextRank())
                .isEqualTo(3);
        assertThat(new Progression(GuildRank.MAGISTER, 42, 0, 1300, 0, 0, 0).levelsToNextRank())
                .isZero();
        assertThat(new Progression(GuildRank.MAGISTER, 42, 0, 1300, 0, 0, 0).nextRankTitle()).isNull();
    }

    /**
     * Компонент один, коробку даёт хозяин. Первая сборка ставила {@code card} на саму секцию,
     * и на итогах — где блок стоит ВНУТРИ карточки результата — выходила карточка в карточке.
     */
    @Test
    void oneComponentOnBothScreensAndTheHostOwnsTheBox() throws IOException {
        String fragment = read("templates/fragments/progression.html");
        String markup = COMMENT.matcher(fragment).replaceAll("\n");

        assertThat(markup).as("секция компонента забрала себе коробку")
                .doesNotContain("class=\"rank card\"");

        List<String> hosts = new ArrayList<>();
        for (String template : List.of("templates/session-summary.html", "templates/stats.html")) {
            String page = COMMENT.matcher(read(template)).replaceAll("\n");
            if (page.contains("fragments/progression :: rank")) {
                hosts.add(template);
            }
            assertThat(page).as("%s: место в пути описано своей разметкой вместо компонента", template)
                    .doesNotContain("class=\"rank-title\"");
        }
        assertThat(hosts).as("компонент обязан стоять на обоих экранах, где спрашивают «где я»")
                .containsExactlyInAnyOrder("templates/session-summary.html", "templates/stats.html");

        // Прежний тайл «Опыт» на итогах говорил то же самое мелким кеглем — он снят,
        // иначе на одном экране два места про один и тот же уровень.
        String summary = COMMENT.matcher(read("templates/session-summary.html")).replaceAll("\n");
        assertThat(summary).as("тайл «Опыт» вернулся — уровень снова назван дважды на одном экране")
                .doesNotContain("до следующего ");
    }

    /**
     * Полоса — нативный элемент, а её значение продублировано прозой: доля не может быть
     * единственным носителем смысла (WCAG 1.4.1), а без JS собственная полоса осталась бы
     * пустой — заполнять её нечем, inline-стили запрещены CSP.
     */
    @Test
    void theBarIsNativeAndItsValueIsAlsoWrittenOut() throws IOException {
        String markup = COMMENT.matcher(read("templates/fragments/progression.html")).replaceAll("\n");

        assertThat(markup).as("полоса перестала быть нативной — без JS она будет пустой")
                .contains("<progress");
        assertThat(markup).as("полоса заполняется скриптом или inline-стилем")
                .doesNotContain("style=").doesNotContain("data-progress");
        assertThat(markup).as("значение полосы не продублировано прозой")
                .contains("class=\"rank-scale\"");

        String css = read("static/css/components-progression.css").replaceAll("(?s)/\\*.*?\\*/", "\n");
        assertThat(css).as("компонент описывает коробку вместо хозяина")
                .doesNotContain("border-radius");
        assertThat(css).as("оформление нативной полосы требует обеих веток псевдоэлементов")
                .contains("::-webkit-progress-value").contains("::-moz-progress-bar");
    }
}
