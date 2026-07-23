package com.cheatsheet.quiz.common.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты русской плюрализации {@link PluralUtils#pick(long, String, String, String)}.
 * Фиксируют каверзные границы правил (регресс против критики round-01 C26, где формы
 * выбирались по абсолютному значению и ломались для чисел ≥11): исключение 11–14 → many,
 * цикличность по последней цифре (21→one, 22–24→few), сотни и отрицательные (знак игнор).
 */
class PluralUtilsTest {

    private final PluralUtils plural = new PluralUtils();

    private String pick(long n) {
        return plural.pick(n, "вопрос", "вопроса", "вопросов");
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 21, 31, 101, 1001, -1, -21})
    void picksOneFormWhenLastDigitIsOneExcept11(long n) {
        assertThat(pick(n)).isEqualTo("вопрос");
    }

    @ParameterizedTest
    @ValueSource(longs = {2, 3, 4, 22, 23, 24, 102, 1002, -2, -24})
    void picksFewFormWhenLastDigitIs2to4Except12to14(long n) {
        assertThat(pick(n)).isEqualTo("вопроса");
    }

    @ParameterizedTest
    @ValueSource(longs = {0, 5, 9, 10, 20, 25, 100, 1000, -5, -100})
    void picksManyFormForZeroFiveToTenAndRoundNumbers(long n) {
        assertThat(pick(n)).isEqualTo("вопросов");
    }

    /**
     * Ключевое исключение русского языка: 11–14 (и их аналоги 111–114, …) всегда many,
     * несмотря на последнюю цифру 1–4. Именно это ломалось до C26-фикса.
     */
    @ParameterizedTest
    @ValueSource(longs = {11, 12, 13, 14, 111, 112, 113, 114, 1011, -13})
    void picksManyFormFor11to14ExceptionZone(long n) {
        assertThat(pick(n)).isEqualTo("вопросов");
    }

    @Test
    void signIsIgnored() {
        assertThat(pick(-1)).isEqualTo(pick(1));
        assertThat(pick(-23)).isEqualTo(pick(23));
        assertThat(pick(-12)).isEqualTo(pick(12));
    }
}
