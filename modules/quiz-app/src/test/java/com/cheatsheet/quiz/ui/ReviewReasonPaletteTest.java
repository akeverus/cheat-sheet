package com.cheatsheet.quiz.ui;

import com.cheatsheet.quiz.domain.ReviewReason;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Соответствие серверного перечисления {@link ReviewReason.Type} и палитры точек-маркеров
 * причины повторения в {@code pages-focus.css}.
 *
 * <p>Мотив — найденный дефект, а не гипотеза. Констант в перечислении пять, правил в CSS
 * было три: {@code NEW} и {@code BOOKMARK} падали в базовый серый {@code ::before}. Ничего
 * не ломалось и ничего не мигало — чип рендерился и выглядел рабочим, просто две причины
 * из пяти теряли категорию и становились неотличимы друг от друга. Причём ровно те две,
 * что встречаются чаще прочих: {@code NEW} — состояние по умолчанию для свежего корпуса,
 * {@code BOOKMARK} — собственное явное действие пользователя.
 *
 * <p>Класс дефекта тот же, что у мёртвых спрайт-листов
 * ({@link SpriteAnimationContractTest}), но с обратным знаком: там правило существовало без
 * активации, здесь активация существует без правила. Ни то, ни другое не ловится
 * {@link StaticAssetInventoryTest}: файлов тут нет вообще, расходятся перечисление и
 * таблица стилей.
 *
 * <h2>Почему контракт исчерпывающий именно здесь</h2>
 * Точка — единственный носитель категории в этом чипе (текст несёт формулировку причины,
 * а не её класс), поэтому «категория без своего цвета» — дефект по определению. Это
 * <b>не</b> общее правило «каждая константа любого перечисления обязана иметь правило».
 * Соседний {@code data-content-type} ({@code BlockType}) устроен ровно наоборот:
 * там CSS и JS адресуют отдельные типы намеренно — {@code MERMAID} получает вьюпорт,
 * {@code CODE} — тулбар, а {@code MARKDOWN}/{@code IMAGE}/{@code CALLOUT} правил не имеют
 * и иметь не должны. Обобщённый гейт покраснел бы там на решении, а не на дефекте.
 */
class ReviewReasonPaletteTest {

    private static final Path FOCUS_CSS = Path.of("src/main/resources/static/css/pages-focus.css");
    private static final Path TOKENS = Path.of("src/main/resources/static/css/tokens.css");

    /** {@code [data-reason-type="X"]::before { ... background: Y; }} */
    private static final Pattern RULE = Pattern.compile(
            "\\.focus-question-reason\\[data-reason-type=\"([A-Z_]+)\"]::before\\s*\\{([^}]*)}");

    private static final Pattern BACKGROUND = Pattern.compile("background:\\s*([^;]+);");

    private Map<String, String> reasonDotColours() {
        Matcher m = RULE.matcher(readStripped(FOCUS_CSS));
        Map<String, String> byType = new LinkedHashMap<>();
        while (m.find()) {
            Matcher bg = BACKGROUND.matcher(m.group(2));
            assertThat(bg.find())
                    .as("правило для %s без background — точка невидима", m.group(1))
                    .isTrue();
            byType.put(m.group(1), bg.group(1).trim());
        }
        return byType;
    }

    @Test
    void everyReviewReasonTypeHasItsOwnDotRule() {
        Set<String> declared = Arrays.stream(ReviewReason.Type.values())
                .map(Enum::name)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        assertThat(reasonDotColours().keySet())
                .as("константа ReviewReason.Type без правила в pages-focus.css молча падает "
                        + "в базовый серый ::before и теряет категорию — так было с NEW и BOOKMARK")
                .containsExactlyInAnyOrderElementsOf(declared);
    }

    @Test
    void noDotRuleTargetsAReasonThatNoLongerExists() {
        Set<String> declared = Arrays.stream(ReviewReason.Type.values()).map(Enum::name).collect(Collectors.toSet());

        assertThat(reasonDotColours().keySet())
                .as("правило под несуществующую константу — мёртвый вес: переименование "
                        + "в перечислении оставляет CSS-селектор, который больше ни с чем не совпадёт")
                .allMatch(declared::contains);
    }

    @Test
    void everyCategoryGetsAVisuallyDistinctDot() {
        Map<String, String> colours = reasonDotColours();

        Map<String, List<String>> byColour = colours.entrySet().stream()
                .collect(Collectors.groupingBy(Map.Entry::getValue,
                        LinkedHashMap::new,
                        Collectors.mapping(Map.Entry::getKey, Collectors.toList())));

        List<String> shared = byColour.entrySet().stream()
                .filter(e -> e.getValue().size() > 1)
                .map(e -> e.getValue() + " → " + e.getKey())
                .toList();

        assertThat(shared)
                .as("две категории с одним цветом точки обесценивают сам сигнал: "
                        + "маркер перестаёт различать причины, ради чего он и существует")
                .isEmpty();
    }

    @Test
    void dotColoursAreConsumedThroughTokensNotHexLiterals() {
        String tokens = readStripped(TOKENS);

        reasonDotColours().forEach((type, colour) -> {
            Matcher var = Pattern.compile("^var\\((--[\\w-]+)\\)$").matcher(colour);
            assertThat(var.matches())
                    .as("цвет точки %s задан как «%s»: точка потребления обязана идти через var(), "
                            + "иначе слой заводит вторую копию палитры", type, colour)
                    .isTrue();
            assertThat(tokens)
                    .as("токен %s (точка %s) не объявлен в tokens.css — var() резолвится в ничто, "
                            + "и точка молча остаётся базовой серой", var.group(1), type)
                    .contains(var.group(1) + ":");
        });
    }

    /** Комментарии документируют свои же значения прозой — снимать до любого замера. */
    private static String readStripped(Path path) {
        try {
            String css = Files.readString(path, StandardCharsets.UTF_8);
            return css.replaceAll("(?s)/\\*.*?\\*/", "");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
