package com.cheatsheet.quiz.api.controller;

import com.cheatsheet.quiz.domain.InterviewMode;
import com.cheatsheet.quiz.domain.InterviewSession;
import com.cheatsheet.quiz.domain.SessionSummary;
import com.cheatsheet.quiz.feature.interview.controller.HttpSessionStateService;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HttpSessionStateServiceTest {

    private final HttpSessionStateService service = new HttpSessionStateService();

    @Test
    void storesAndClearsInterviewSession() {
        MockHttpSession session = new MockHttpSession();
        InterviewSession interviewSession = new InterviewSession(
                InterviewMode.TRAINING,
                List.of(1L),
                "java",
                "backend",
                false,
                false,
                false,
                true
        );

        service.setInterviewSession(session, interviewSession);
        assertThat(service.getInterviewSession(session)).isSameAs(interviewSession);

        service.clearInterviewSession(session);
        assertThat(service.getInterviewSession(session)).isNull();
    }

    @Test
    void storesAndClearsSessionSummary() {
        MockHttpSession session = new MockHttpSession();
        SessionSummary summary = SessionSummary.builder()
                .mode(InterviewMode.EXAM)
                .duration(Duration.ofMinutes(5))
                .totalQuestions(10)
                .correctCount(7)
                .wrongCount(3)
                .build();

        service.setLastSessionSummary(session, summary);
        assertThat(service.getLastSessionSummary(session)).contains(summary);

        service.clearLastSessionSummary(session);
        assertThat(service.getLastSessionSummary(session)).isEmpty();
    }
}
