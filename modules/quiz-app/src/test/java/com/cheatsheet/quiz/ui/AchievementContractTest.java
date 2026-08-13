package com.cheatsheet.quiz.ui;

import com.cheatsheet.quiz.domain.Achievement;
import com.cheatsheet.quiz.domain.LearningMetrics;
import com.cheatsheet.quiz.domain.Progression;
import com.cheatsheet.quiz.domain.UserExperience;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Знаки отличия — факты, а не награды.
 *
 * <h2>Почему это важно проверять</h2>
 * Награду надо где-то хранить, и с этого момента она живёт отдельно от того, что описывает:
 * таблица говорит «знак получен», данные говорят, что порог не пройден, и разойтись они могут
 * молча. Здесь знак — чистая функция от метрик и прогрессии, и гейт держит ровно это: правило
 * есть у каждого глифа, глиф есть у каждого правила, знак не выдаётся авансом.
 */
class AchievementContractTest {

    private static final Pattern COMMENT = Pattern.compile("<!--.*?-->", Pattern.DOTALL);

    private String read(String classpathLocation) throws IOException {
        return new String(new ClassPathResource(classpathLocation).getInputStream().readAllBytes(),
                StandardCharsets.UTF_8);
    }

    private static Progression progression(int streak, int best) {
        return new Progression(com.cheatsheet.quiz.domain.GuildRank.APPRENTICE, 1, 0, 100, 0, streak, best);
    }

    private static LearningMetrics metrics(long attempts, long mastered, double firstAttemptAccuracy) {
        return LearningMetrics.builder()
                .cardsTotal(9818)
                .cardsSeen(attempts)
                .cardsMastered(mastered)
                .attemptsTotal(attempts)
                .attemptsCorrect(attempts)
                .attemptsIncorrect(0)
                .firstAttemptAccuracy(firstAttemptAccuracy)
                .overallAccuracy(firstAttemptAccuracy)
                .retention7d(LearningMetrics.NO_DATA)
                .retention30d(LearningMetrics.NO_DATA)
                .averageResponseMs(LearningMetrics.NO_DATA)
                .build();
    }

    /** Каждому правилу — свой глиф, и наоборот: имя знака и есть имя символа. */
    @Test
    void everyBadgeHasItsGlyph() throws IOException {
        String sprite = COMMENT.matcher(read("templates/fragments/icons.html")).replaceAll("\n");

        for (Achievement badge : Achievement.CATALOG) {
            assertThat(sprite).as("у знака «%s» нет глифа в спрайте", badge.title())
                    .contains("<symbol id=\"" + badge.id() + "\"");
            assertThat(badge.title()).isNotBlank();
            assertThat(badge.goal()).as("знак «%s» не объясняет, что нужно сделать", badge.title())
                    .isNotBlank();
            assertThat(badge.target()).as("порог знака «%s»", badge.title()).isPositive();
        }

        // Обратная сторона: глиф badge-* без правила — обещание знака, которого нет.
        java.util.regex.Matcher glyphs = Pattern.compile("<symbol id=\"(badge-[a-z-]+)\"").matcher(sprite);
        while (glyphs.find()) {
            String id = glyphs.group(1);
            assertThat(Achievement.CATALOG.stream().map(Achievement::id))
                    .as("глиф %s не соответствует ни одному правилу", id)
                    .contains(id);
        }
    }

    /**
     * Знак не выдаётся авансом. «Твёрдая рука» — единственный с двумя условиями: без
     * дистанции один верный ответ давал бы 100% и знак «за меткость» доставался бы за первый
     * клик.
     */
    @Test
    void nothingIsAwardedInAdvance() {
        List<Achievement.Earned> fresh = Achievement.evaluate(LearningMetrics.EMPTY, progression(0, 0));
        assertThat(Achievement.earnedCount(fresh)).as("на чистой базе знаков быть не может").isZero();
        assertThat(fresh).allSatisfy(b -> assertThat(b.percent()).isZero());

        // Один верный ответ: «Первый шаг» — да, «Твёрдая рука» — нет, хотя точность 100%.
        List<Achievement.Earned> oneAnswer = Achievement.evaluate(metrics(1, 0, 100.0), progression(1, 1));
        assertThat(oneAnswer.get(0).earned()).as("«Первый шаг» после первого ответа").isTrue();
        Achievement.Earned steady = oneAnswer.stream()
                .filter(b -> b.achievement().id().equals("badge-steady-hand")).findFirst().orElseThrow();
        assertThat(steady.earned()).as("«Твёрдая рука» выдана за один верный ответ").isFalse();
        assertThat(steady.current()).as("до дистанции показывается сама дистанция").isEqualTo(1);

        // Дистанция набрана, точность держится — знак берётся.
        Achievement.Earned steadyFar = Achievement.evaluate(metrics(60, 0, 72.0), progression(0, 0)).stream()
                .filter(b -> b.achievement().id().equals("badge-steady-hand")).findFirst().orElseThrow();
        assertThat(steadyFar.earned()).isTrue();
        assertThat(steadyFar.current()).isEqualTo(72);

        // Дистанция набрана, точность ниже порога — знака нет.
        Achievement.Earned steadyWeak = Achievement.evaluate(metrics(60, 0, 61.0), progression(0, 0)).stream()
                .filter(b -> b.achievement().id().equals("badge-steady-hand")).findFirst().orElseThrow();
        assertThat(steadyWeak.earned()).isFalse();
        assertThat(steadyWeak.remaining()).isEqualTo(9);
    }

    /** Доля округляется ВНИЗ: «100%» обязано означать взятый порог. */
    @Test
    void progressNeverOverstatesItself() {
        Achievement.Earned almost = Achievement.evaluate(metrics(99, 0, 50.0), progression(0, 0)).stream()
                .filter(b -> b.achievement().id().equals("badge-hundred")).findFirst().orElseThrow();
        assertThat(almost.earned()).isFalse();
        assertThat(almost.percent()).isEqualTo(99);
        assertThat(almost.remaining()).isEqualTo(1);

        Achievement.Earned done = Achievement.evaluate(metrics(100, 0, 50.0), progression(0, 0)).stream()
                .filter(b -> b.achievement().id().equals("badge-hundred")).findFirst().orElseThrow();
        assertThat(done.earned()).isTrue();
        assertThat(done.percent()).isEqualTo(100);
        assertThat(done.remaining()).isZero();

        // Серия считается по лучшему из текущего и рекорда: знак, взятый месяц назад,
        // не отбирается из-за пропуска одного дня.
        Achievement.Earned week = Achievement.evaluate(metrics(10, 0, 50.0), progression(0, 9)).stream()
                .filter(b -> b.achievement().id().equals("badge-week")).findFirst().orElseThrow();
        assertThat(week.earned()).as("рекорд серии 9 дней — знак недели взят").isTrue();
    }

    /** Состояние знака читается словом и числом, а не только цветом (WCAG 1.4.1). */
    @Test
    void theShowcaseTellsStateInWords() throws IOException {
        String markup = COMMENT.matcher(read("templates/stats.html")).replaceAll("\n");

        assertThat(markup).as("витрина знаков исчезла со страницы").contains("id=\"badges-title\"");
        assertThat(markup).as("полученный знак не назван словом").contains("Получен");
        assertThat(markup).as("до неполученного знака не показано расстояние")
                .contains("${b.achievement().target()}");
        assertThat(markup).as("полоса знака не нативная — без JS она останется пустой")
                .contains("<progress class=\"badge-bar\"");
        assertThat(markup).as("витрина не говорит, сколько знаков всего взято")
                .contains("${achievementsEarned}");
    }

    /** Пустая база не роняет расчёт и не выдумывает данные. */
    @Test
    void emptyDatabaseIsAValidState() {
        List<Achievement.Earned> badges =
                Achievement.evaluate(null, Progression.of(UserExperience.EMPTY, 0));
        assertThat(badges).hasSize(Achievement.CATALOG.size());
        assertThat(badges).allSatisfy(b -> {
            assertThat(b.earned()).isFalse();
            assertThat(b.current()).isZero();
            assertThat(b.remaining()).isEqualTo(b.achievement().target());
        });
    }
}
