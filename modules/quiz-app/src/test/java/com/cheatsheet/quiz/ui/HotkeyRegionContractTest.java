package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Хоткеи MCQ не угоняют клавиатуру у скролл-регионов (A11Y-02, REF-021).
 *
 * <p>Живой репро: фокус на скроллируемом код-регионе (pre.is-scrollable /
 * .code-viewport / .diagram-viewport — рецепт REF-007 «переполнен →
 * фокусируем») → ArrowDown ВЫБИРАЛ вариант ответа и угонял фокус с региона.
 * Пользователь, читающий код с клавиатуры, менял свой ответ на экзамене
 * попыткой проскроллить. Стрелки с фокуса внутри такого региона обязаны
 * оставаться браузерным скроллом.</p>
 */
class HotkeyRegionContractTest {

    private String readStrippedJs() throws IOException {
        // Комментарии стрипаются: упоминание селектора в прозе — не код гейта.
        return new String(new ClassPathResource("static/js/quiz/interview.js")
                .getInputStream().readAllBytes(), StandardCharsets.UTF_8)
                .replaceAll("(?s)/\\*.*?\\*/", "")
                .replaceAll("(?m)^\\s*//.*$", "");
    }

    @Test
    void arrowKeysYieldToFocusableScrollRegions() throws IOException {
        String js = readStrippedJs();
        // Guard — единый блок: условие по стрелкам + closest по регионам + return.
        // (Первая редакция сравнивала indexOf двух подстрок и сломалась о то,
        // что guard сам содержит упоминание стрелок.)
        assertThat(js)
                .as("гейт хоткеев обязан уступать стрелки скролл-регионам единым guard-блоком")
                .containsPattern("(?s)ArrowDown[^\\n]*ArrowUp[^{]*closest\\([^)]*code-viewport[^)]*\\)\\)\\s*\\{\\s*return;");
        assertThat(js)
                .as("guard покрывает и диаграмму — второй фокусируемый регион рецепта REF-007")
                .containsPattern("closest\\([^)]*diagram-viewport");
    }
}
