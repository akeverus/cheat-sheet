package com.cheatsheet.quiz.common.util;

import com.cheatsheet.quiz.domain.InterviewMode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты {@link ModeUtils#displayName(InterviewMode)} — единого источника русских
 * UI-лейблов режимов. Фиксируют точные строки (регресс против критики round-01 C22,
 * где на итогах вместо «Интенсив» показывался сырой {@code MARATHON}) и {@code null}-гард.
 */
class ModeUtilsTest {

    private final ModeUtils modeUtils = new ModeUtils();

    @ParameterizedTest
    @CsvSource({
            "TRAINING,Тренировка",
            "EXAM,Экзамен",
            "MARATHON,Интенсив",
            "STUDY,Изучение",
            "FLASHCARD,Флешкарты",
            "TYPED_RECALL,Печатный ответ",
            "INTERVIEW,Интервью",
            "CODE_CHALLENGE,Код-челлендж"
    })
    void displayNameReturnsRussianLabelForEachMode(InterviewMode mode, String expected) {
        assertThat(modeUtils.displayName(mode)).isEqualTo(expected);
    }

    @Test
    void displayNameReturnsEmptyStringForNull() {
        assertThat(modeUtils.displayName(null)).isEmpty();
    }

    /**
     * Гарантирует, что КАЖДЫЙ режим (в т.ч. добавленный в будущем) имеет непустой лейбл —
     * иначе UI покажет пустую строку вместо названия режима.
     */
    @ParameterizedTest
    @EnumSource(InterviewMode.class)
    void displayNameIsNeverBlankForAnyMode(InterviewMode mode) {
        assertThat(modeUtils.displayName(mode)).isNotBlank();
    }
}
