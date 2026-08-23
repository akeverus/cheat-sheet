package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Печать перекрашивается на уровне ТОКЕНОВ (§147, REF-027).
 *
 * <p>Живой print-замер: форс color на body не спасал токено-зависимые правила —
 * h1 фокуса печатался слоновой костью тёмной темы на белой бумаге
 * (rgb(242,236,224), контраст ~1.1:1), а варианты MCQ — светлым текстом на
 * тёмной плашке, которую браузер по умолчанию не печатает. Один блок
 * переопределения токенов чинит всё, что на них сидит; точечная нейтрализация
 * остаётся для фонов вариантов.</p>
 */
class PrintPaletteContractTest {

    @Test
    void printOverridesInkTokensNotJustBodyColor() throws IOException {
        String css = new String(new ClassPathResource("static/css/print.css")
                .getInputStream().readAllBytes(), StandardCharsets.UTF_8)
                .replaceAll("(?s)/\\*.*?\\*/", "");
        assertThat(css)
                .as("печать обязана переопределять токен основного текста — иначе " +
                        "токено-зависимые заголовки печатаются палитрой тёмной темы")
                .containsPattern("--color-text-primary:\\s*#141413");
        assertThat(css)
                .as("фон вариантов MCQ нейтрализуется: тёмная плашка не печатается, " +
                        "и без листа под текстом рамка карточки теряется")
                .containsPattern("#interview-options label\\s*\\{[^}]*background:\\s*#fff");
    }
}
