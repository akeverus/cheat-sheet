package com.cheatsheet.quiz.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты инвариантов {@link InterviewSession}.
 */
class InterviewSessionTest {

    @Test
    void newSessionStartsAtIndexZero() {
        InterviewSession session = new InterviewSession(
                InterviewMode.TRAINING, List.of(1L, 2L, 3L),
                "Java", false, false, false, Instant.now());

        assertThat(session.getIndex()).isZero();
        assertThat(session.getCorrect()).isZero();
        assertThat(session.getWrong()).isZero();
        assertThat(session.isFinished()).isFalse();
        assertThat(session.getTotal()).isEqualTo(3);
    }

    @Test
    void registerCorrectAnswerIncrementsCorrectAndIndex() {
        InterviewSession session = new InterviewSession(
                InterviewMode.TRAINING, List.of(1L, 2L),
                null, null, null, null, Instant.now());

        session.registerAnswer(true);

        assertThat(session.getCorrect()).isEqualTo(1);
        assertThat(session.getWrong()).isZero();
        assertThat(session.getIndex()).isEqualTo(1);
    }

    @Test
    void registerWrongAnswerIncrementsWrongAndIndex() {
        InterviewSession session = new InterviewSession(
                InterviewMode.EXAM, List.of(1L, 2L),
                null, null, null, null, Instant.now());

        session.registerAnswer(false);

        assertThat(session.getWrong()).isEqualTo(1);
        assertThat(session.getCorrect()).isZero();
        assertThat(session.getIndex()).isEqualTo(1);
    }

    @Test
    void registerUnknownIncrementsUnknownAndIndexButNotCorrectWrong() {
        InterviewSession session = new InterviewSession(
                InterviewMode.EXAM, List.of(1L, 2L),
                null, null, null, null, Instant.now());

        session.registerUnknown("java");

        assertThat(session.getUnknown()).isEqualTo(1);
        assertThat(session.getCorrect()).isZero();
        assertThat(session.getWrong()).isZero();
        assertThat(session.getIndex()).isEqualTo(1);
        // «Не знаю» НЕ добавляется в историю ответов (форма AnswerRecord неизменна).
        assertThat(session.getAnswerHistory()).isEmpty();
    }

    @Test
    void sessionFinishesWhenAllQuestionsAnswered() {
        InterviewSession session = new InterviewSession(
                InterviewMode.TRAINING, List.of(1L),
                null, null, null, null, Instant.now());

        assertThat(session.isFinished()).isFalse();
        session.registerAnswer(true);
        assertThat(session.isFinished()).isTrue();
    }

    @Test
    void currentQuestionIdReturnsNullWhenFinished() {
        InterviewSession session = new InterviewSession(
                InterviewMode.TRAINING, List.of(1L),
                null, null, null, null, Instant.now());

        assertThat(session.currentQuestionId()).isEqualTo(1L);
        session.registerAnswer(true);
        assertThat(session.currentQuestionId()).isNull();
    }

    @Test
    void addPenaltyQuestionsExtendsQuestionList() {
        InterviewSession session = new InterviewSession(
                InterviewMode.EXAM, List.of(1L, 2L),
                null, null, null, null, Instant.now());

        assertThat(session.getTotal()).isEqualTo(2);
        session.addPenaltyQuestions(List.of(3L, 4L));
        assertThat(session.getTotal()).isEqualTo(4);
    }

    @Test
    void addPenaltyQuestionsWithNullOrEmptyIsNoOp() {
        InterviewSession session = new InterviewSession(
                InterviewMode.EXAM, List.of(1L),
                null, null, null, null, Instant.now());

        session.addPenaltyQuestions(null);
        assertThat(session.getTotal()).isEqualTo(1);

        session.addPenaltyQuestions(List.of());
        assertThat(session.getTotal()).isEqualTo(1);
    }

    @Test
    void emptySessionIsImmediatelyFinished() {
        InterviewSession session = new InterviewSession(
                InterviewMode.TRAINING, List.of(),
                null, null, null, null, Instant.now());

        assertThat(session.isFinished()).isTrue();
        assertThat(session.getTotal()).isZero();
    }

    @Test
    void takeResponseTimeMsReturnsElapsedAndClearsMark() {
        InterviewSession session = trainingSession();
        session.markQuestionServed(1_000_000L);

        assertThat(session.takeResponseTimeMs(1_004_200L)).isEqualTo(4200);
        // Метка потреблена — повторный вызов уже null.
        assertThat(session.takeResponseTimeMs(1_004_300L)).isNull();
    }

    @Test
    void takeResponseTimeMsReturnsNullWithoutMark() {
        assertThat(trainingSession().takeResponseTimeMs(1_000_000L)).isNull();
    }

    @Test
    void takeResponseTimeMsRejectsNegativeAndAbsurdIntervals() {
        InterviewSession negative = trainingSession();
        negative.markQuestionServed(2_000L);
        assertThat(negative.takeResponseTimeMs(1_000L)).isNull(); // часы уехали назад

        InterviewSession absurd = trainingSession();
        absurd.markQuestionServed(0L);
        // 31 минута > лимита 30 мин — вкладку забыли открытой.
        assertThat(absurd.takeResponseTimeMs(31L * 60L * 1000L)).isNull();
    }

    private static InterviewSession trainingSession() {
        return new InterviewSession(
                InterviewMode.TRAINING, List.of(1L, 2L),
                null, null, null, null, Instant.now());
    }
}
