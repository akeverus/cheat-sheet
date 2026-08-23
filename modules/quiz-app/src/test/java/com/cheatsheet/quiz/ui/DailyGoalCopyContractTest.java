package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Копирайт дневной цели честен при перевыполнении (§144, REF-025).
 *
 * <p>Живой скрин /train: «Дневная цель — 60 из 10». Формула
 * {@code today + ' из ' + goal} не имела языка для перевыполнения — счётчик
 * продолжает расти после цели, и «из 10» превращается в абсурд. Выполненная
 * цель называется словами: «Дневная цель выполнена — N ответов».</p>
 */
class DailyGoalCopyContractTest {

    @Test
    void overshootDayGoalIsNamedAsReachedNotAsBrokenFraction() throws IOException {
        String js = new String(new ClassPathResource("static/js/quiz/streak.js")
                .getInputStream().readAllBytes(), StandardCharsets.UTF_8)
                .replaceAll("(?s)/\\*.*?\\*/", "")
                .replaceAll("(?m)^\\s*//.*$", "");
        assertThat(js)
                .as("выполненная цель обязана называться словами, а не дробью «60 из 10»")
                .contains("Дневная цель выполнена");
        assertThat(js)
                .as("ветка выполнения выбирается сравнением с целью")
                .containsPattern("today\\s*>=\\s*d?\\.?goal");
        assertThat(js)
                .as("недостигнутая цель остаётся прежней дробью «N из M»")
                .contains("' из '");
    }
}
