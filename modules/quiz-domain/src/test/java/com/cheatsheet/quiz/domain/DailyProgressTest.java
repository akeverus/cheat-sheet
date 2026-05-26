package com.cheatsheet.quiz.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DailyProgressTest {

    @Test
    void constructsWithValidValues() {
        DailyProgress p = new DailyProgress(5, 3, 10);

        assertThat(p.questionsAnswered()).isEqualTo(5);
        assertThat(p.correctCount()).isEqualTo(3);
        assertThat(p.goal()).isEqualTo(10);
    }

    @Test
    void goalReachedFalseWhenBelow() {
        assertThat(new DailyProgress(9, 5, 10).goalReached()).isFalse();
    }

    @Test
    void goalReachedTrueWhenEqual() {
        assertThat(new DailyProgress(10, 5, 10).goalReached()).isTrue();
    }

    @Test
    void goalReachedTrueWhenAbove() {
        assertThat(new DailyProgress(15, 5, 10).goalReached()).isTrue();
    }

    @Test
    void negativeQuestionsAnsweredRejected() {
        assertThatThrownBy(() -> new DailyProgress(-1, 0, 10))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void negativeCorrectCountRejected() {
        assertThatThrownBy(() -> new DailyProgress(5, -1, 10))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void negativeGoalRejected() {
        assertThatThrownBy(() -> new DailyProgress(5, 3, -1))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
