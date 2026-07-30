package com.cheatsheet.quiz.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Инварианты {@link AttemptOutcome}.
 *
 * <p>Enum хранится в БД как TEXT (см. javadoc типа), поэтому НАБОР значений и их
 * ИМЕНА — контракт персистентности: переименование/удаление/добавление значения
 * молча ломает чтение старых строк {@code attempt}. Тесты фиксируют и фабрику
 * {@link AttemptOutcome#ofCorrect(boolean)}, и стабильность имён.</p>
 */
class AttemptOutcomeTest {

    @Test
    void ofCorrectTrueIsCorrect() {
        assertThat(AttemptOutcome.ofCorrect(true)).isEqualTo(AttemptOutcome.CORRECT);
    }

    @Test
    void ofCorrectFalseIsWrong() {
        assertThat(AttemptOutcome.ofCorrect(false)).isEqualTo(AttemptOutcome.WRONG);
    }

    @Test
    void ofCorrectNeverProducesUnknownOrSkip() {
        // ofCorrect — только для обычного ответа: UNKNOWN (reveal/«не знаю») и SKIP
        // (пропуск, FLOW-03) выставляются отдельными путями, не через этот флаг.
        assertThat(AttemptOutcome.ofCorrect(true)).isNotIn(AttemptOutcome.UNKNOWN, AttemptOutcome.SKIP);
        assertThat(AttemptOutcome.ofCorrect(false)).isNotIn(AttemptOutcome.UNKNOWN, AttemptOutcome.SKIP);
    }

    @Test
    void enumHasExactlyTheFourPersistedValues() {
        // Набор — контракт БД (TEXT). Изменение состава требует миграции старых строк.
        assertThat(AttemptOutcome.values())
                .containsExactly(
                        AttemptOutcome.CORRECT,
                        AttemptOutcome.WRONG,
                        AttemptOutcome.UNKNOWN,
                        AttemptOutcome.SKIP);
    }

    @Test
    void storedNamesRoundTripViaValueOf() {
        // Round-trip чтения из БД: имя в TEXT → valueOf → та же константа.
        for (AttemptOutcome outcome : AttemptOutcome.values()) {
            assertThat(AttemptOutcome.valueOf(outcome.name())).isSameAs(outcome);
        }
        // Точные имена, на которые опираются существующие строки attempt:
        assertThat(AttemptOutcome.valueOf("CORRECT")).isSameAs(AttemptOutcome.CORRECT);
        assertThat(AttemptOutcome.valueOf("WRONG")).isSameAs(AttemptOutcome.WRONG);
        assertThat(AttemptOutcome.valueOf("UNKNOWN")).isSameAs(AttemptOutcome.UNKNOWN);
        assertThat(AttemptOutcome.valueOf("SKIP")).isSameAs(AttemptOutcome.SKIP);
    }

    @Test
    void valueOfUnknownNameThrows() {
        assertThatThrownBy(() -> AttemptOutcome.valueOf("SKIPPED"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
