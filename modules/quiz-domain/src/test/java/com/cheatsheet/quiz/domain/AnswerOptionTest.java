package com.cheatsheet.quiz.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AnswerOptionTest {

    @Test
    void fullConstructorAcceptsValidValues() {
        AnswerOption opt = new AnswerOption(1L, 10L, "A. text", true, 0,
                "OPENAI", "explain", 2, 2, 0);

        assertThat(opt.id()).isEqualTo(1L);
        assertThat(opt.correct()).isTrue();
        assertThat(opt.promptVersion()).isEqualTo(2);
        assertThat(opt.qualityProfileVersion()).isEqualTo(2);
        assertThat(opt.mcqBlockIdx()).isZero();
    }

    @Test
    void sevenArgConvenienceConstructorDefaultsVersionsAndBlock() {
        AnswerOption opt = new AnswerOption(1L, 10L, "A. text", true, 0, "OPENAI", "x");

        assertThat(opt.promptVersion()).isEqualTo(1);
        assertThat(opt.qualityProfileVersion()).isEqualTo(1);
        assertThat(opt.mcqBlockIdx()).isZero();
    }

    @Test
    void nineArgConvenienceConstructorDefaultsBlockOnly() {
        AnswerOption opt = new AnswerOption(1L, 10L, "A", false, 1, "MARKDOWN", null, 3, 4);

        assertThat(opt.promptVersion()).isEqualTo(3);
        assertThat(opt.qualityProfileVersion()).isEqualTo(4);
        assertThat(opt.mcqBlockIdx()).isZero();
    }

    @Test
    void nullOptionTextRejected() {
        assertThatThrownBy(() -> new AnswerOption(1L, 10L, null, true, 0, "OPENAI", null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("optionText");
    }

    @Test
    void nullSourceRejected() {
        assertThatThrownBy(() -> new AnswerOption(1L, 10L, "A", true, 0, null, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("source");
    }

    @Test
    void negativeDisplayOrderRejected() {
        assertThatThrownBy(() -> new AnswerOption(1L, 10L, "A", true, -1, "OPENAI", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("displayOrder");
    }

    @Test
    void promptVersionLessThanOneRejected() {
        assertThatThrownBy(() -> new AnswerOption(1L, 10L, "A", true, 0,
                "OPENAI", null, 0, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("promptVersion");
    }

    @Test
    void qualityProfileVersionLessThanOneRejected() {
        assertThatThrownBy(() -> new AnswerOption(1L, 10L, "A", true, 0,
                "OPENAI", null, 1, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("qualityProfileVersion");
    }

    @Test
    void negativeMcqBlockIdxRejected() {
        assertThatThrownBy(() -> new AnswerOption(1L, 10L, "A", true, 0,
                "OPENAI", null, 1, 1, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("mcqBlockIdx");
    }
}
