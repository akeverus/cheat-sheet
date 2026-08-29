package com.cheatsheet.quiz.ui;

import com.cheatsheet.quiz.domain.Difficulty;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Бейдж сложности в шапке экрана вопроса: подпись, условие показа и палитра.
 *
 * <p>Мотив — найденный дефект. Подпись собиралась тернарником прямо в разметке
 * ({@code diff == 'EASY' ? 'Лёгкий' : (diff == 'HARD' ? 'Сложный' : 'Средний')}), и
 * четвёртая константа не сломала бы сборку — она молча печаталась бы как «Средний».
 * Тот же класс, что у {@code ReviewReason.Type} ({@link ReviewReasonPaletteTest}): не
 * падение, а тихая подмена смысла. Лечится переносом подписи в перечисление, а гейт
 * держит её там.
 *
 * <p>Второй дефект был виден на экране: {@code MEDIUM} — значение по умолчанию (в него
 * сводятся {@code null}, пустая строка и любой нераспознанный уровень), и замер по базе
 * дал 9818 вопросов из 9818 в нём. То есть бейдж стоял на каждом вопросе с одной и той
 * же подписью, ничего не различая. Аналитика этот же случай уже трактовала честно —
 * {@code StatsPageService} прячет донат сложности, когда уровень в банке ровно один, —
 * а экран вопроса утверждал обратное.
 *
 * <h2>Почему контракт палитры исчерпывающий именно здесь</h2>
 * Модификатор {@code badge--difficulty-*} разметка вешает на каждый показанный бейдж
 * безусловно. Бейдж без правила падает в тихую серую базу {@code .question-zone-head
 * .badge} — ту, что задумана для ярлыков, стоящих всегда. Но показанный бейдж по новому
 * условию стоит не всегда, а только когда уровень что-то различает: «сигнал, выглядящий
 * как фон» — дефект по определению. Это <b>не</b> общее правило «каждая константа обязана
 * иметь CSS»: у {@code MEDIUM} правила нет и быть не должно, что гейт и требует.
 */
class DifficultyBadgeContractTest {

    private static final Path FOCUS_CSS = Path.of("src/main/resources/static/css/pages-focus.css");
    private static final Path TOKENS = Path.of("src/main/resources/static/css/tokens.css");
    private static final Path TEMPLATES = Path.of("src/main/resources/templates");

    /** {@code .badge--difficulty-easy { … }} в шапке вопроса. */
    private static final Pattern RULE = Pattern.compile(
            "\\.question-zone-head\\s+\\.badge--difficulty-([a-z]+)\\s*\\{([^}]*)}");

    private static final Pattern COLOR = Pattern.compile("(?<![-\\w])color:\\s*([^;]+);");

    /** Значение любого {@code th:*}-атрибута — там, где жил тернарник. */
    private static final Pattern TH_ATTRIBUTE = Pattern.compile("th:[a-zA-Z-]+=\"([^\"]*)\"");

    @Test
    void everyLevelCarriesItsOwnLabel() {
        Map<String, String> labels = Arrays.stream(Difficulty.values())
                .collect(Collectors.toMap(Enum::name, Difficulty::label, (a, b) -> a, LinkedHashMap::new));

        assertThat(labels.values())
                .as("пустая подпись уровня — бейдж-призрак: рамка есть, смысла нет")
                .allMatch(label -> label != null && !label.isBlank());
        assertThat(labels.values())
                .as("две константы с одной подписью неразличимы на экране — ровно то, "
                        + "что делал тернарник, печатавший «Средний» для всего неизвестного")
                .doesNotHaveDuplicates();
    }

    @Test
    void theFallbackLevelIsTheUninformativeOne() {
        Difficulty fallback = Difficulty.fromString(null);

        assertThat(Stream.of("", "   ", "нет такого уровня", "EPIC").map(Difficulty::fromString))
                .as("fromString сводит к одному значению всё, о чём ничего не известно — "
                        + "на этом и держится вывод, что показывать его нельзя")
                .allMatch(value -> value == fallback);
        assertThat(fallback.informative())
                .as("значение по умолчанию неотличимо от «уровень неизвестен»: показывать "
                        + "его — значит держать в шапке вопроса строку, одинаковую для всего банка")
                .isFalse();
        assertThat(Arrays.stream(Difficulty.values()).filter(Difficulty::informative))
                .as("если информативных уровней не осталось, бейджа нет вообще — "
                        + "и вся ветка разметки мертва")
                .isNotEmpty();
    }

    @Test
    void noTemplateRebuildsDifficultyLabelsInMarkup() {
        Set<String> labels = Arrays.stream(Difficulty.values()).map(Difficulty::label).collect(Collectors.toSet());

        List<String> offenders = templateFiles()
                .flatMap(file -> {
                    Matcher m = TH_ATTRIBUTE.matcher(read(file));
                    return m.results()
                            .map(r -> r.group(1))
                            .filter(expression -> labels.stream().anyMatch(expression::contains))
                            .map(expression -> file.getFileName() + ": " + expression);
                })
                .toList();

        assertThat(offenders)
                .as("подпись уровня, собранная выражением в разметке, не ломается на новой "
                        + "константе — она молча печатает одну из старых; подписи живут в Difficulty")
                .isEmpty();
    }

    @Test
    void everyInformativeLevelHasItsOwnBadgeRule() {
        Set<String> informative = Arrays.stream(Difficulty.values())
                .filter(Difficulty::informative)
                .map(level -> level.name().toLowerCase(Locale.ROOT))
                .collect(Collectors.toSet());

        assertThat(badgeColours().keySet())
                .as("показанный бейдж без своего правила падает в тихую серую базу шапки — "
                        + "и уровень, ради которого бейдж и появился, читается как дежурный ярлык; "
                        + "правило под неинформативный уровень столь же вредно: его не рисуют")
                .containsExactlyInAnyOrderElementsOf(informative);
    }

    @Test
    void informativeLevelsAreVisuallyDistinct() {
        Map<String, List<String>> byColour = badgeColours().entrySet().stream()
                .collect(Collectors.groupingBy(Map.Entry::getValue,
                        LinkedHashMap::new,
                        Collectors.mapping(Map.Entry::getKey, Collectors.toList())));

        assertThat(byColour.entrySet().stream().filter(e -> e.getValue().size() > 1).toList())
                .as("два уровня одним цветом обесценивают сам бейдж: он перестаёт "
                        + "различать то единственное, что сообщает")
                .isEmpty();
    }

    /**
     * Пиксельного слоя, который безусловно гасил рамку бейджа
     * ({@code border: 0 !important} в снятом pixel-nav-badges.css), больше нет:
     * {@code border-color} на бейдже — снова живая ручка, и правило вправе её
     * объявлять. Требование к ней то же, что и к {@code color}: значение идёт
     * через {@code var()}, иначе экран вопроса заводит вторую копию палитры
     * сложности мимо tokens.css.
     */
    @Test
    void badgeBorderColourIfDeclaredGoesThroughTokens() {
        Matcher m = RULE.matcher(stripComments(read(FOCUS_CSS)));
        while (m.find()) {
            Matcher border = Pattern.compile("border-color:\\s*([^;]+);").matcher(m.group(2));
            while (border.find()) {
                assertThat(border.group(1).trim())
                        .as("бейдж %s объявляет border-color сырым значением «%s»: рамка обязана "
                                + "идти через var(), как и color", m.group(1), border.group(1).trim())
                        .matches("var\\(--[\\w-]+\\)");
            }
        }
    }

    @Test
    void badgeColoursAreConsumedThroughTokens() {
        String tokens = read(TOKENS);

        badgeColours().forEach((level, colour) -> {
            Matcher var = Pattern.compile("^var\\((--[\\w-]+)\\)$").matcher(colour);
            assertThat(var.matches())
                    .as("цвет бейджа %s задан как «%s»: точка потребления обязана идти через "
                            + "var(), иначе экран вопроса заводит вторую копию палитры сложности", level, colour)
                    .isTrue();
            assertThat(tokens)
                    .as("токен %s (бейдж %s) не объявлен в tokens.css — var() резолвится в ничто, "
                            + "и бейдж молча остаётся серым", var.group(1), level)
                    .contains(var.group(1) + ":");
        });
    }

    private Map<String, String> badgeColours() {
        Matcher m = RULE.matcher(stripComments(read(FOCUS_CSS)));
        Map<String, String> byLevel = new LinkedHashMap<>();
        while (m.find()) {
            Matcher color = COLOR.matcher(m.group(2));
            assertThat(color.find())
                    .as("правило для %s без color — бейдж неотличим от базового", m.group(1))
                    .isTrue();
            byLevel.put(m.group(1), color.group(1).trim());
        }
        return byLevel;
    }

    private static Stream<Path> templateFiles() {
        try (Stream<Path> walk = Files.walk(TEMPLATES)) {
            return walk.filter(path -> path.toString().endsWith(".html")).toList().stream();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Комментарии документируют свои же значения прозой — снимать до любого замера. */
    private static String stripComments(String css) {
        return css.replaceAll("(?s)/\\*.*?\\*/", "");
    }

    private static String read(Path path) {
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
