package com.cheatsheet.quiz.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты {@link QuestionDifficulty#fromStats(int, int)} — классификатора сложности по
 * статистике ответов. Фиксируют пороги и их границы: {@code total==0 → NEW};
 * {@code total < MIN_ATTEMPTS_FOR_LEVEL(3) → MEDIUM} (независимо от точности);
 * при {@code total>=3}: {@code >=80% → EASY}, {@code 50..80% → MEDIUM}, {@code <50% → HARD}.
 */
class QuestionDifficultyTest {

    @Test
    void noAttemptsYieldsNew() {
        assertThat(QuestionDifficulty.fromStats(0, 0)).isEqualTo(QuestionDifficulty.NEW);
    }

    @ParameterizedTest
    @CsvSource({
            // менее 3 попыток → всегда MEDIUM, даже при 100% или 0% точности
            "1, 0, MEDIUM",
            "2, 0, MEDIUM",
            "0, 1, MEDIUM",
            "0, 2, MEDIUM",
            "1, 1, MEDIUM",
            // ровно 3 попытки — порог MIN_ATTEMPTS_FOR_LEVEL достигнут
            "3, 0, EASY",     // 100%
            "0, 3, HARD",     // 0%
            "1, 2, HARD",     // 33.3% < 50
            "2, 1, MEDIUM",   // 66.7% в [50,80)
            // граница EASY = ровно 80%
            "4, 1, EASY",     // 80.0% → EASY (>=)
            "8, 2, EASY",     // 80.0%
            "3, 1, MEDIUM",   // 75% < 80 → MEDIUM
            // граница MEDIUM/HARD = ровно 50%
            "2, 2, MEDIUM",   // 50.0% → MEDIUM (>=)
            "5, 5, MEDIUM",   // 50.0%
            "4, 5, HARD"      // 44.4% < 50 → HARD
    })
    void classifiesByAccuracyAndAttemptThresholds(int correct, int wrong, QuestionDifficulty expected) {
        assertThat(QuestionDifficulty.fromStats(correct, wrong)).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
            "NEW,    Новый,   new",
            "EASY,   Лёгкий,  easy",
            "MEDIUM, Средний, medium",
            "HARD,   Сложный, hard"
    })
    void labelAndCssClassAreStable(QuestionDifficulty level, String label, String cssClass) {
        assertThat(level.getLabel()).isEqualTo(label);
        assertThat(level.getCssClass()).isEqualTo(cssClass);
    }
}
