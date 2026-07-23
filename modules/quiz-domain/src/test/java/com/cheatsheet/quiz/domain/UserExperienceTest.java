package com.cheatsheet.quiz.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

/**
 * Тесты {@link UserExperience} — XP↔level математики (единственный источник определения
 * уровня). Фиксируют формулы и границы: {@code levelForXp = floor(sqrt(xp/100)) + 1}
 * (уровень 1 при xp≤0, скачок ровно на пороге 100·(n−1)²), обратную согласованность
 * {@code levelForXp(xpForLevel(n)) == n}, прогресс внутри уровня и валидацию.
 */
class UserExperienceTest {

    @ParameterizedTest
    @CsvSource({
            "-10, 1",   // отрицательный XP → уровень 1
            "0,   1",
            "99,  1",   // ниже порога 2-го уровня
            "100, 2",   // ровно порог → скачок
            "101, 2",
            "399, 2",   // ниже порога 3-го уровня
            "400, 3",   // ровно порог
            "900, 4",
            "1600, 5"
    })
    void levelForXpFollowsSqrtFormulaWithThresholdJumps(long xp, int expectedLevel) {
        assertThat(UserExperience.levelForXp(xp)).isEqualTo(expectedLevel);
    }

    @ParameterizedTest
    @CsvSource({
            "0, 0",     // уровень ≤ 1 → порог 0
            "1, 0",
            "2, 100",
            "3, 400",
            "4, 900",
            "5, 1600"
    })
    void xpForLevelIsHundredTimesLevelMinusOneSquared(int level, long expectedXp) {
        assertThat(UserExperience.xpForLevel(level)).isEqualTo(expectedXp);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 10})
    void levelForXpIsInverseOfXpForLevelAtThresholds(int level) {
        assertThat(UserExperience.levelForXp(UserExperience.xpForLevel(level))).isEqualTo(level);
    }

    @Test
    void levelDelegatesToLevelForXp() {
        assertThat(new UserExperience(400, 0, 0).level()).isEqualTo(3);
        assertThat(new UserExperience(0, 0, 0).level()).isEqualTo(1);
    }

    @ParameterizedTest
    @CsvSource({
            "0,   0",     // начало уровня 1
            "100, 0",     // ровно начало уровня 2
            "150, 50",    // уровень 2 (порог 100) → 50 внутри
            "450, 50"     // уровень 3 (порог 400) → 50 внутри
    })
    void xpIntoLevelIsOffsetFromCurrentLevelThreshold(long xp, long expected) {
        assertThat(new UserExperience(xp, 0, 0).xpIntoLevel()).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
            "0,   100",   // уровень 1→2: 100−0
            "100, 300",   // уровень 2→3: 400−100
            "150, 300",   // тот же уровень 2
            "400, 500"    // уровень 3→4: 900−400
    })
    void xpToNextLevelIsGapBetweenCurrentAndNextThreshold(long xp, long expected) {
        assertThat(new UserExperience(xp, 0, 0).xpToNextLevel()).isEqualTo(expected);
    }

    @Test
    void emptyIsLevelOneWithZeroCounters() {
        assertThat(UserExperience.EMPTY.xp()).isZero();
        assertThat(UserExperience.EMPTY.bestStreak()).isZero();
        assertThat(UserExperience.EMPTY.level()).isEqualTo(1);
    }

    @Test
    void negativeXpOrStreakThrows() {
        assertThatIllegalArgumentException().isThrownBy(() -> new UserExperience(-1, 0, 0));
        assertThatIllegalArgumentException().isThrownBy(() -> new UserExperience(0, -1, 0));
    }
}
