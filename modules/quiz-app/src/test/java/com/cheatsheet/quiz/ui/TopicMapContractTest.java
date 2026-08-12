package com.cheatsheet.quiz.ui;

import com.cheatsheet.quiz.domain.TopicMap;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Карта темы и вертикальная балансировка рабочей области.
 *
 * <h2>Найденный дефект, а не гипотеза</h2>
 * Замер нижней пустоты первого экрана вопроса (Chromium и WebKit, короткий вопрос без
 * кода): 68px (8%) на 1440×900, 150px (15%) на 1512×982, 434px (36%) на 1920×1200 и
 * 674px (47%) на 2560×1440. На больших экранах почти половина окна — фон под композицией.
 *
 * <h2>Отозванная гипотеза</h2>
 * Сначала предполагалось закрыть пустоту содержимым — картой темы. Карта сделана и
 * полезна сама по себе, но замер после неё дал ТЕ ЖЕ 674px и 434px: сетка из 27 клеток
 * занимает 18px высоты. «Материал закроет пустоту» — не подтвердилось; пустота
 * распределяется, а не заполняется.
 *
 * <h2>Что закреплено</h2>
 * <ol>
 *   <li>состояние клетки считается в домене, а не в шаблоне;</li>
 *   <li>цвет не единственный носитель: числа названы прозой, сетка скрыта от скринридера;</li>
 *   <li>балансировка включается по ВЫСОТЕ окна и не режет длинный вопрос.</li>
 * </ol>
 */
class TopicMapContractTest {

    private static final Pattern COMMENT = Pattern.compile("<!--.*?-->", Pattern.DOTALL);

    private String read(String classpathLocation) throws IOException {
        return new String(new ClassPathResource(classpathLocation).getInputStream().readAllBytes(),
                StandardCharsets.UTF_8);
    }

    /** Счёт по состояниям — в домене: подсчёт в разметке не проверить тестом. */
    @Test
    void theMapCountsStatesItself() {
        TopicMap map = new TopicMap(List.of(
                TopicMap.QuestionProgress.LEARNED,
                TopicMap.QuestionProgress.LEARNED,
                TopicMap.QuestionProgress.DUE,
                TopicMap.QuestionProgress.IN_PROGRESS,
                TopicMap.QuestionProgress.FRESH,
                TopicMap.QuestionProgress.FRESH,
                TopicMap.QuestionProgress.FRESH));

        assertThat(map.total()).isEqualTo(7);
        assertThat(map.learned()).isEqualTo(2);
        assertThat(map.due()).isEqualTo(1);
        assertThat(map.inProgress()).isEqualTo(1);
        assertThat(map.fresh()).isEqualTo(3);
        assertThat(map.learned() + map.due() + map.inProgress() + map.fresh())
                .as("сумма состояний обязана сходиться с числом клеток — иначе подпись врёт")
                .isEqualTo(map.total());

        assertThat(TopicMap.EMPTY.total()).isZero();
        assertThat(new TopicMap(null).total()).as("карта без клеток — пустая, а не падение").isZero();
    }

    /**
     * Цвет не может быть единственным носителем смысла (WCAG 1.4.1). Сетка помечена
     * aria-hidden намеренно: сто одинаковых «элементов списка» не сообщают скринридеру
     * ничего, чего нет в подписи, — а подпись обязана называть все состояния числами.
     */
    @Test
    void colourIsNotTheOnlyCarrier() throws IOException {
        String markup = COMMENT.matcher(read("templates/focus-training.html")).replaceAll("\n");

        assertThat(markup).as("карта темы исчезла с экрана вопроса").contains("class=\"topic-map\"");
        assertThat(markup).as("сетка карты не скрыта от скринридера — он прочтёт десятки пустых пунктов")
                .contains("class=\"topic-map-grid\" aria-hidden=\"true\"");
        for (String word : List.of("освоено", "к повторению", "не начато", "всего")) {
            assertThat(markup).as("подпись карты не называет состояние «%s» словами", word)
                    .contains(word);
        }

        String css = read("static/css/pages-focus.css").replaceAll("(?s)/\\*.*?\\*/", "\n");
        assertThat(css).as("состояние «ждёт повторения» отличается от освоенного только цветом — "
                        + "нужна другая форма (контур вместо заливки)")
                .contains(".topic-map-cell--due");
        Matcher due = Pattern.compile("\\.topic-map-cell--due\\s*\\{([^}]*)}").matcher(css);
        assertThat(due.find()).isTrue();
        assertThat(due.group(1)).contains("inset");
    }

    /**
     * Балансировка включается по высоте окна и делает это через {@code margin-block: auto},
     * а не {@code justify-content: center}: центрирование через justify-content режет верх
     * композиции, когда контент перерастает контейнер (проверено на 1512×1000 — вопрос
     * выше окна, верх на месте, страница прокручивается).
     */
    @Test
    void theWorkAreaIsBalancedByViewportHeightWithoutClipping() throws IOException {
        String css = read("static/css/pages-focus.css").replaceAll("(?s)/\\*.*?\\*/", "\n");

        Matcher rule = Pattern.compile("@media \\(min-height: (\\d+)px\\)\\s*\\{(.*?)\\n\\}", Pattern.DOTALL)
                .matcher(css);
        boolean found = false;
        while (rule.find()) {
            if (rule.group(2).contains(".focus-page .ed-page")) {
                found = true;
                assertThat(Integer.parseInt(rule.group(1)))
                        .as("порог балансировки опустился туда, где делить нечего: на 1440×900 "
                                + "пустоты 68px")
                        .isGreaterThanOrEqualTo(1000);
                assertThat(rule.group(2))
                        .as("центрирование через justify-content режет верх длинного вопроса")
                        .contains("margin-block: auto")
                        .doesNotContain("justify-content: center");
            }
        }
        assertThat(found).as("правило вертикальной балансировки рабочей области исчезло").isTrue();
    }
}
