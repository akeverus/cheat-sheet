package com.cheatsheet.quiz.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты {@link FlashcardGrade} — моста «само-оценка флешкарты (UI 1–4) → SM-2 grade».
 * Через SM-2 grade это состояние определяет, будет ли карточка засчитана как лапс
 * (сброс интервала) или как успех, поэтому маппинг критичен для планирования.
 *
 * <p>Фиксируем в т.ч. <b>намеренный пропуск</b> SM-2 grade 3: UI 1,2,3,4 → SM-2
 * 1,2,4,5 (наивная «правка» 3→3 сломала бы контракт), и кросс-методный инвариант
 * {@code isCorrect(g) ⇔ toSm2Grade(g) ≥ 3} на валидном домене 1..4.</p>
 */
class FlashcardGradeTest {

    @ParameterizedTest(name = "UI {0} → SM-2 {1}")
    @CsvSource({
            "1, 1", // «Не помню»
            "2, 2", // «Плохо»
            "3, 4", // «Хорошо» — SM-2 grade 3 пропущен намеренно
            "4, 5"  // «Отлично»
    })
    void mapsUiGradeToSm2Grade(int uiGrade, int expectedSm2) {
        assertThat(FlashcardGrade.toSm2Grade(uiGrade)).isEqualTo(expectedSm2);
    }

    @Test
    void sm2GradeThreeIsNeverProducedForValidUiGrades() {
        for (int ui = 1; ui <= 4; ui++) {
            assertThat(FlashcardGrade.toSm2Grade(ui)).isNotEqualTo(3);
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2})
    void lowGradesAreIncorrect(int uiGrade) {
        assertThat(FlashcardGrade.isCorrect(uiGrade)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(ints = {3, 4})
    void highGradesAreCorrect(int uiGrade) {
        assertThat(FlashcardGrade.isCorrect(uiGrade)).isTrue();
    }

    @Test
    void isCorrectAgreesWithSm2ThresholdAcrossValidDomain() {
        // Инвариант: «правильно» ⇔ SM-2 grade ≥ порога 3. Если методы разъедутся,
        // флешкарта будет засчитываться в лапс/успех не так, как в интервале.
        for (int ui = 1; ui <= 4; ui++) {
            assertThat(FlashcardGrade.isCorrect(ui))
                    .isEqualTo(FlashcardGrade.toSm2Grade(ui) >= 3);
        }
    }
}
