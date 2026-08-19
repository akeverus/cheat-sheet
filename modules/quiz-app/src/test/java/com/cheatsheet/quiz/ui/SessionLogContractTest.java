package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Журнал хода сессии в колонке задания.
 *
 * <h2>Найденный дефект, а не гипотеза</h2>
 * Замер первого экрана (Chromium и WebKit, 2560×1440, код-челлендж): низ колонки
 * задания 624, низ рабочей колонки 1116 — 768×492 пустоты рядом с работой. На
 * 1920×1080 то же самое даёт 492px, на 1512×982 — 437px. §38 брифа велит сначала
 * спросить, какую пользу человек получит на свободной площади, и только потом класть
 * туда что-либо.
 *
 * <h2>Что закреплено</h2>
 * <ol>
 *   <li>журнала нет, пока нечего показывать — пустая рамка сообщала бы о себе;</li>
 *   <li>вердикт несёт форма И слово, а не только цвет (WCAG 1.4.1);</li>
 *   <li>тема показана человеческим именем, а не slug-ом;</li>
 *   <li>на телефоне журнал уходит за работу вместе со справкой о теме.</li>
 * </ol>
 */
class SessionLogContractTest {

    private static final Pattern COMMENT = Pattern.compile("<!--.*?-->", Pattern.DOTALL);

    private String read(String classpathLocation) throws IOException {
        return new String(new ClassPathResource(classpathLocation).getInputStream().readAllBytes(),
                StandardCharsets.UTF_8);
    }

    private String markup() throws IOException {
        return COMMENT.matcher(read("templates/focus-training.html")).replaceAll("");
    }

    @Test
    void theLogIsAbsentWhenThereIsNothingToShow() throws IOException {
        assertThat(markup())
                .as("пустой журнал — это рамка, которая сообщает только о себе")
                .contains("th:if=\"${sessionLog != null and sessionLog.present()}\"");
    }

    @Test
    void theVerdictIsCarriedByShapeAndWordNotOnlyColour() throws IOException {
        String markup = markup();
        assertThat(markup).contains("#circle-check");
        assertThat(markup).contains("#circle-x");
        assertThat(markup)
                .as("скринридер обязан услышать исход, а не только увидеть кольцо")
                .containsPattern("visually-hidden[^>]*— верно");

        String css = read("static/css/pages-focus.css");
        assertThat(css).contains(".session-log-entry.is-correct .session-log-mark");
        assertThat(css).contains(".session-log-entry.is-wrong .session-log-mark");
    }

    @Test
    void theTopicIsShownByItsHumanName() throws IOException {
        assertThat(markup())
                .as("slug темы человеку не показываем — на это есть TopicUtils")
                .contains("@topicUtils.displayName(entry.topic())");
    }

    /**
     * Проверяем ПРАВИЛО, а не его написание. Первая редакция требовала точной строки
     * с одним селектором и упала от второго спутника; вторая перечисляла три имени и
     * упала, когда все три уехали в общую обёртку. Смысл всё это время был один:
     * спутники идут ЗА работой, а не между вопросом и ответом.
     */
    @Test
    void onThePhoneTheCompanionsFollowTheWork() throws IOException {
        String css = read("static/css/pages-focus.css");
        assertThat(css)
                .as("обёртка спутников не переставляется — они снова окажутся на пути к ответу")
                .containsPattern("\\.focus-main \\.focus-companions \\{ order: 1");

        // Границы обёртки берём из самой разметки, а не окном в N символов: окно —
        // это допущение о длине блока, и первая редакция гейта на нём и споткнулась
        // (заметка оказалась дальше 6000 знаков от начала обёртки).
        String markup = read("templates/focus-training.html");
        int open = markup.indexOf("class=\"focus-companions\"");
        int close = markup.indexOf("/.focus-companions");
        assertThat(open).as("обёртки спутников нет в разметке").isNotNegative();
        assertThat(close).as("обёртка не закрыта — границы не определить").isGreaterThan(open);
        String inside = markup.substring(open, close);
        for (String companion : List.of("session-log", "topic-brief", "question-note")) {
            assertThat(inside)
                    .as("спутник вне обёртки — правило перестановки его не достанет: " + companion)
                    .contains("class=\"" + companion + "\"");
        }
    }
}
