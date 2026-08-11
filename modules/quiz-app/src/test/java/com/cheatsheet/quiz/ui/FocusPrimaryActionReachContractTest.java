package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Главное действие экрана вопроса достижимо без прокрутки на любом вьюпорте.
 *
 * <h2>Найденный дефект, а не гипотеза</h2>
 * Проект уже признал этот дефект дефектом — но только на мобиле. В блоке
 * {@code @media (max-width: 600px)} та же панель закреплена {@code position: fixed} с
 * обоснованием «главное действие MCQ-ответа — закреплённая нижняя панель, а не кнопка,
 * тонущая под длинным списком вариантов». Причина в этой формулировке не мобильная:
 * длина списка вариантов от ширины окна не зависит.
 *
 * <p>Замер WebKit 18.2 (DPR 2, дефолтная раскладка, до правки) показал ровно то, что
 * обоснование и описывает — на самых ходовых MacBook-вьюпортах кнопка стояла НИЖЕ сгиба:
 *
 * <ul>
 *   <li>1280×800 — низ кнопки 1190px при высоте окна 800px, видно 59% списка вариантов;</li>
 *   <li>1440×900 — 1098px против 900px, видно 82%;</li>
 *   <li>1512×982 — 1098px против 982px, видно 95% (кнопка всё равно за кадром);</li>
 *   <li>1920×1200 и 2560×1440 — те же 1098px, но окно выше, и дефект исчезает сам собой.</li>
 * </ul>
 *
 * <p>Высота документа держалась 1252px и на 1440, и на 1512: композиция вообще не
 * справлялась о высоте окна. Уплотнением отступов до 1440-го сгиба честно не доехать —
 * весь стек над вариантами весит 232px, а срезать надо было 198px, то есть сам вопрос.
 *
 * <h2>Что именно закреплено</h2>
 * Инвариант: <b>на desktop панель главного действия закреплена, закреплена на
 * обёртке-фрагменте, диапазон стыкуется с мобильным ровно встык, и вариант ответа
 * не приезжает под всплывшую панель</b>. Числа сгиба тест не проверяет: они зависят от
 * длины конкретного вопроса, это работа замера ({@code work/redesign}), а не файла на диске.
 *
 * <p>Отдельно закреплён <b>носитель</b> правила. {@code position: sticky} упирается в
 * рамку <i>родителя</i>: на {@code .action-footer} (родитель — {@code training-actions}
 * той же высоты) sticky молча выродился бы в static, и дефект вернулся бы при формально
 * присутствующем правиле. Пробег даёт только сама обёртка — её родитель
 * {@code #interview-form} высотой ~770px.
 */
class FocusPrimaryActionReachContractTest {

    private static final Path FOCUS_CSS = Path.of("src/main/resources/static/css/pages-focus.css");

    /** Верхняя граница мобильного блока, где та же панель закреплена {@code position: fixed}. */
    private static final int MOBILE_PINNED_UPPER_BOUND_PX = 600;

    /**
     * Правило ищется по намерению («панель вопроса закреплена»), а не по эталонному
     * селектору: иначе переезд закрепления на неверный носитель читался бы как «правила
     * нет вообще», и красный тест сообщал бы не ту причину.
     */
    private static final Pattern DESKTOP_PIN_SELECTOR = Pattern.compile(
            "([^{}]*\\[data-ui-fragment=\"training-actions\"][^{}]*)\\{([^{}]*position:\\s*sticky[^{}]*)}");

    /**
     * CSS без комментариев: селекторы здесь ловятся «от предыдущей скобки», а обоснования
     * в комментариях цитируют и {@code .action-footer}, и {@code position: fixed} — на
     * неочищенном тексте тест ловил бы собственную документацию.
     */
    private final String css = read(FOCUS_CSS).replaceAll("(?s)/\\*.*?\\*/", "");

    /** Тело {@code @media}, внутри которого панель закреплена на desktop, + его нижняя граница. */
    private record DesktopPinBlock(int minWidthPx, String body) {
    }

    private DesktopPinBlock desktopPinBlock() {
        Matcher media = Pattern.compile("@media \\(min-width:\\s*(\\d+)px\\)\\s*\\{").matcher(css);
        while (media.find()) {
            String body = braceBalancedBody(media.end() - 1);
            if (DESKTOP_PIN_SELECTOR.matcher(body).find()) {
                return new DesktopPinBlock(Integer.parseInt(media.group(1)), body);
            }
        }
        throw new AssertionError("в pages-focus.css нет ни одного @media (min-width: …), где панель "
                + "#interview-form > [data-ui-fragment=\"training-actions\"] закреплена position: sticky. "
                + "Без закрепления главное действие уходит под сгиб на 1280×800, 1440×900 и 1512×982 — "
                + "замер до правки: низ кнопки 1190/1098/1098px при высоте окна 800/900/982px");
    }

    /** Тело блока от открывающей скобки по адресу {@code openBrace} до парной ей. */
    private String braceBalancedBody(int openBrace) {
        int depth = 0;
        for (int i = openBrace; i < css.length(); i++) {
            char c = css.charAt(i);
            if (c == '{') {
                depth++;
            } else if (c == '}' && --depth == 0) {
                return css.substring(openBrace + 1, i);
            }
        }
        throw new AssertionError("незакрытый блок в " + FOCUS_CSS + " с позиции " + openBrace);
    }

    @Test
    void desktopPinSitsOnTheFragmentWrapperNotOnTheInnerFooter() {
        Matcher rule = DESKTOP_PIN_SELECTOR.matcher(desktopPinBlock().body());
        assertThat(rule.find()).isTrue();
        String selector = rule.group(1).trim();

        assertThat(selector)
                .as("правило закрепления переехало на .action-footer. sticky упирается в рамку "
                        + "РОДИТЕЛЯ, а родитель .action-footer — та же training-actions той же "
                        + "высоты: пробега нет, правило молча выродится в static и кнопка снова "
                        + "утонет под списком вариантов")
                .doesNotContain(".action-footer");
        assertThat(selector)
                .as("связь с #interview-form стала не прямым потомком: без `>` правило поймает "
                        + "вложенные обёртки, а без #interview-form — флешкарту, typed и study, "
                        + "у которых своя нижняя панель и свои состояния")
                .contains("#interview-form >");
    }

    @Test
    void desktopPinIsStickyNotFixedSoTheColumnKeepsItsMeasure() {
        Matcher rule = DESKTOP_PIN_SELECTOR.matcher(desktopPinBlock().body());
        assertThat(rule.find()).isTrue();

        assertThat(rule.group(2))
                .as("панель закреплена fixed, а не sticky: fixed вынут из потока и растянется "
                        + "во всё окно поверх правой колонки — на 2560px это полоса на 2560px "
                        + "под колонкой чтения в 72ch. sticky держится своей колонки")
                .doesNotContain("position: fixed");
    }

    @Test
    void desktopPinIsOpaqueOverScrollingContent() {
        Matcher rule = DESKTOP_PIN_SELECTOR.matcher(desktopPinBlock().body());
        assertThat(rule.find()).isTrue();
        String declarations = rule.group(2);

        assertThat(declarations)
                .as("у закреплённой панели нет непрозрачного фона: базовое правило "
                        + ".focus-page .action-footer ставит background: transparent, и варианты "
                        + "ответа будут проезжать СКВОЗЬ кнопку")
                .containsPattern("background:\\s*var\\(--color-bg-");
        assertThat(declarations)
                .as("у закреплённой панели нет верхней линии: без неё край панели неотличим от "
                        + "контента, который под неё уезжает")
                .contains("border-top");
    }

    @Test
    void desktopRangeButtsAgainstTheMobilePinnedRange() {
        int minWidth = desktopPinBlock().minWidthPx();

        assertThat(minWidth)
                .as("диапазон desktop-закрепления не стыкуется с мобильным: панель закреплена "
                        + "fixed при max-width %dpx и sticky при min-width %dpx. Разрыв оставит "
                        + "полосу ширин с утонувшей кнопкой, нахлёст — две закреплённые панели",
                        MOBILE_PINNED_UPPER_BOUND_PX, minWidth)
                .isEqualTo(MOBILE_PINNED_UPPER_BOUND_PX + 1);
        assertThat(css)
                .as("мобильное закрепление панели (@media (max-width: %dpx)) исчезло — граница "
                        + "стыка, на которую опирается desktop-правило, больше не существует",
                        MOBILE_PINNED_UPPER_BOUND_PX)
                .contains("@media (max-width: " + MOBILE_PINNED_UPPER_BOUND_PX + "px)");
    }

    @Test
    void optionsKeepClearanceFromThePinnedBar() {
        String body = desktopPinBlock().body();

        Matcher clearance = Pattern.compile(
                "([^{}]*#interview-options\\s*>\\s*label[^{}]*)\\{([^{}]*scroll-margin-bottom[^{}]*)}")
                .matcher(body);
        assertThat(clearance.find())
                .as("у вариантов ответа нет запаса scroll-margin-bottom рядом с закреплённой "
                        + "панелью. Клавиатурная навигация (↑/↓, 1–9) скроллит вариант в вид — "
                        + "без запаса последний вариант приезжает ПОД панель и выбирается вслепую")
                .isTrue();
        assertThat(clearance.group(1))
                .as("запас под вариантами не снят после ответа: панель помечается .is-answered и "
                        + "прячется, а 6rem пустоты под последним вариантом остаются висеть")
                .contains(":not(.is-answered)");
    }

    private static String read(Path path) {
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("не прочитать " + path, e);
        }
    }
}
