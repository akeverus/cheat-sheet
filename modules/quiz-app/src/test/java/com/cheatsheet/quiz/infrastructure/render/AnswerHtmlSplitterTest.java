package com.cheatsheet.quiz.infrastructure.render;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты {@link AnswerHtmlSplitter} — деривации «краткого лида» (первый {@code <p>})
 * и «остатка» из отрендеренного HTML разбора. Пути чисто строковые, поэтому
 * характеризуются без Spring/БД. Покрывают все ветки {@code split} и защиту
 * {@code firstParagraphStart} от ложных срабатываний на {@code <pre>}/{@code <picture>}.
 */
class AnswerHtmlSplitterTest {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t\n"})
    void nullOrBlankYieldsEmptySplit(String html) {
        AnswerHtmlSplitter.Split split = AnswerHtmlSplitter.split(html);
        assertThat(split.leadHtml()).isEmpty();
        assertThat(split.restHtml()).isEmpty();
    }

    @Test
    void leadIsFirstParagraphRestIsRemainder() {
        AnswerHtmlSplitter.Split split = AnswerHtmlSplitter.split("<p>Lead.</p><ul><li>a</li></ul>");
        assertThat(split.leadHtml()).isEqualTo("<p>Lead.</p>");
        assertThat(split.restHtml()).isEqualTo("<ul><li>a</li></ul>");
    }

    @Test
    void singleParagraphLeavesEmptyRest() {
        AnswerHtmlSplitter.Split split = AnswerHtmlSplitter.split("<p>Only paragraph.</p>");
        assertThat(split.leadHtml()).isEqualTo("<p>Only paragraph.</p>");
        assertThat(split.restHtml()).isEmpty();
    }

    @Test
    void multipleParagraphsSplitAtFirst() {
        AnswerHtmlSplitter.Split split = AnswerHtmlSplitter.split("<p>First</p><p>Second</p>");
        assertThat(split.leadHtml()).isEqualTo("<p>First</p>");
        assertThat(split.restHtml()).isEqualTo("<p>Second</p>");
    }

    @Test
    void leadingWhitespaceBeforeParagraphIsTolerated() {
        AnswerHtmlSplitter.Split split = AnswerHtmlSplitter.split("  <p>Lead.</p><ul>x</ul>");
        assertThat(split.leadHtml()).isEqualTo("<p>Lead.</p>");
        assertThat(split.restHtml()).isEqualTo("<ul>x</ul>");
    }

    @Test
    void paragraphWithAttributesIsRecognized() {
        AnswerHtmlSplitter.Split split = AnswerHtmlSplitter.split("<p class=\"explain\">Lead</p><p>rest</p>");
        assertThat(split.leadHtml()).isEqualTo("<p class=\"explain\">Lead</p>");
        assertThat(split.restHtml()).isEqualTo("<p>rest</p>");
    }

    @Test
    void caseInsensitiveTagsAreRecognized() {
        AnswerHtmlSplitter.Split split = AnswerHtmlSplitter.split("<P>Lead</P><div>d</div>");
        assertThat(split.leadHtml()).isEqualTo("<P>Lead</P>");
        assertThat(split.restHtml()).isEqualTo("<div>d</div>");
    }

    @Test
    void contentNotStartingWithParagraphGoesEntirelyToRest() {
        // Начинается с <ul> — лид выделять неоткуда, весь HTML в остаток (фолбэк покажет всё).
        AnswerHtmlSplitter.Split split = AnswerHtmlSplitter.split("<ul><li>a</li></ul><p>mid</p>");
        assertThat(split.leadHtml()).isEmpty();
        assertThat(split.restHtml()).isEqualTo("<ul><li>a</li></ul><p>mid</p>");
    }

    @Test
    void paragraphInMiddleIsNotPulledOut() {
        // <p> есть, но не в начале (перед ним <h3>) — выдёргивать нельзя (переставит контент).
        AnswerHtmlSplitter.Split split = AnswerHtmlSplitter.split("<h3>Title</h3><p>Body</p>");
        assertThat(split.leadHtml()).isEmpty();
        assertThat(split.restHtml()).isEqualTo("<h3>Title</h3><p>Body</p>");
    }

    @Test
    void preTagIsNotMistakenForParagraph() {
        // <pre> начинается с "<p" но за ним 'r' — не абзац. Реального <p> нет → всё в остаток.
        AnswerHtmlSplitter.Split split = AnswerHtmlSplitter.split("<pre>code</pre>");
        assertThat(split.leadHtml()).isEmpty();
        assertThat(split.restHtml()).isEqualTo("<pre>code</pre>");
    }

    @Test
    void pictureTagIsNotMistakenForParagraph() {
        AnswerHtmlSplitter.Split split = AnswerHtmlSplitter.split("<picture><img></picture>");
        assertThat(split.leadHtml()).isEmpty();
        assertThat(split.restHtml()).isEqualTo("<picture><img></picture>");
    }

    @Test
    void unclosedParagraphFallsBackToRest() {
        // Нет </p> → лид не выделяется, весь HTML в остаток.
        AnswerHtmlSplitter.Split split = AnswerHtmlSplitter.split("<p>unclosed text");
        assertThat(split.leadHtml()).isEmpty();
        assertThat(split.restHtml()).isEqualTo("<p>unclosed text");
    }
}
