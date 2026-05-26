package com.cheatsheet.quiz.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HintTest {

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void acceptsValidLevels(int level) {
        Hint h = new Hint(1L, 10L, level, "text", 1000L);
        assertThat(h.level()).isEqualTo(level);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 4, 5, 100})
    void rejectsLevelOutsideOneToThree(int level) {
        assertThatThrownBy(() -> new Hint(1L, 10L, level, "text", 1000L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("level");
    }

    @Test
    void nullHintTextRejected() {
        assertThatThrownBy(() -> new Hint(1L, 10L, 1, null, 1000L))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("hintText");
    }

    @Test
    void emptyHintTextAccepted() {
        // Контракт: текст не может быть null, но может быть пустой строкой.
        // Если впоследствии добавится requireNonBlank, этот тест нужно
        // обновить — сейчас он фиксирует текущее поведение.
        Hint h = new Hint(1L, 10L, 1, "", 1000L);
        assertThat(h.hintText()).isEmpty();
    }
}
