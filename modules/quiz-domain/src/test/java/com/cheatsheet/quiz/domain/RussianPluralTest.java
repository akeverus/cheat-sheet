package com.cheatsheet.quiz.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class RussianPluralTest {

    @ParameterizedTest(name = "{0} → {1}")
    @CsvSource({
            // one
            "1, 1 день",
            "21, 21 день",
            "31, 31 день",
            "101, 101 день",
            // few
            "2, 2 дня",
            "3, 3 дня",
            "4, 4 дня",
            "22, 22 дня",
            "23, 23 дня",
            "24, 24 дня",
            "102, 102 дня",
            // many
            "0, 0 дней",
            "5, 5 дней",
            "10, 10 дней",
            "20, 20 дней",
            "25, 25 дней",
            "100, 100 дней",
            // special-case 11–14 (по mod 100 → всегда many, несмотря на mod 10 = 1..4)
            "11, 11 дней",
            "12, 12 дней",
            "13, 13 дней",
            "14, 14 дней",
            "111, 111 дней",
            "112, 112 дней",
            "114, 114 дней"
    })
    void daysProducesCorrectRussianForm(long n, String expected) {
        assertThat(RussianPlural.days(n)).isEqualTo(expected);
    }

    @Test
    void pluralizeSelectsForm() {
        assertThat(RussianPlural.pluralize(1, "штука", "штуки", "штук")).isEqualTo("штука");
        assertThat(RussianPlural.pluralize(3, "штука", "штуки", "штук")).isEqualTo("штуки");
        assertThat(RussianPlural.pluralize(5, "штука", "штуки", "штук")).isEqualTo("штук");
        assertThat(RussianPlural.pluralize(13, "штука", "штуки", "штук")).isEqualTo("штук");
    }

    @Test
    void negativeNumbersUseAbsoluteValue() {
        // Знак игнорируется — форма определяется величиной.
        assertThat(RussianPlural.pluralize(-1, "день", "дня", "дней")).isEqualTo("день");
        assertThat(RussianPlural.pluralize(-3, "день", "дня", "дней")).isEqualTo("дня");
    }
}
