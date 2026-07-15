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
}
