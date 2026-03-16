package com.cheatsheet.quiz;

import static org.assertj.core.api.Assertions.assertThat;

import com.cheatsheet.quiz.domain.InterviewMode;
import com.cheatsheet.quiz.domain.InterviewSession;
import java.util.List;
import org.junit.jupiter.api.Test;

class InterviewSessionTest {

    @Test
    void tracksProgressAndFinish() {
        InterviewSession session = new InterviewSession(InterviewMode.EXAM, List.of(1L, 2L), null, null, null, null);

        assertThat(session.currentQuestionId()).isEqualTo(1L);
        assertThat(session.isFinished()).isFalse();

        session.registerAnswer(true);
        assertThat(session.getCorrect()).isEqualTo(1);
        assertThat(session.getIndex()).isEqualTo(1);
        assertThat(session.currentQuestionId()).isEqualTo(2L);

        session.registerAnswer(false);
        assertThat(session.getWrong()).isEqualTo(1);
        assertThat(session.isFinished()).isTrue();
        assertThat(session.currentQuestionId()).isNull();
    }
}
