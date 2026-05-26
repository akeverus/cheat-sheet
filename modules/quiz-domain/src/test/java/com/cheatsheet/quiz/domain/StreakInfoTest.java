package com.cheatsheet.quiz.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StreakInfoTest {

    @Test
    void constructsWithZeroStreak() {
        StreakInfo info = new StreakInfo(0);
        assertThat(info.currentStreak()).isZero();
    }

    @Test
    void constructsWithPositiveStreak() {
        StreakInfo info = new StreakInfo(42);
        assertThat(info.currentStreak()).isEqualTo(42);
    }

    @Test
    void negativeStreakRejected() {
        assertThatThrownBy(() -> new StreakInfo(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
