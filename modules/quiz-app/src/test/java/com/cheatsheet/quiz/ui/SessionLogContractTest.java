package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
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
     * Проверяем ПРАВИЛО, а не его написание. Первая редакция гейта требовала точной
     * строки с одним селектором, и добавление второго спутника (заметки к вопросу)
     * уронило её, хотя правило осталось прежним: спутники уходят за работу.
     */
    @Test
    void onThePhoneTheCompanionsFollowTheWork() throws IOException {
        String css = read("static/css/pages-focus.css");
        Matcher rule = Pattern
                .compile("([^{}]*\\.focus-main \\.session-log[^{}]*)\\{\\s*order:\\s*1;")
                .matcher(css);
        assertThat(rule.find())
                .as("журнал — спутник, а не преграда между вопросом и ответом")
                .isTrue();
        String selectors = rule.group(1);
        assertThat(selectors)
                .as("справка о теме уходит за работу тем же правилом")
                .contains(".focus-main .topic-brief");
        assertThat(selectors)
                .as("заметка к вопросу — такой же спутник, как журнал и справка")
                .contains(".focus-main .question-note");
    }
}
