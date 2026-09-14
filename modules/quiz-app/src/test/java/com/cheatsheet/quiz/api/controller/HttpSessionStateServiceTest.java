package com.cheatsheet.quiz.api.controller;

import com.cheatsheet.quiz.api.security.CurrentUserAccessor;
import com.cheatsheet.quiz.config.app.AppProperties;
import com.cheatsheet.quiz.domain.InterviewMode;
import com.cheatsheet.quiz.domain.InterviewSession;
import com.cheatsheet.quiz.domain.SessionSummary;
import com.cheatsheet.quiz.feature.interview.controller.HttpSessionStateService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpSessionStateServiceTest {

    private final AppProperties localProps = new AppProperties();
    private final HttpSessionStateService localService =
            new HttpSessionStateService(new CurrentUserAccessor(), localProps);

    @AfterEach
    void clearSecurity() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void storesAndClearsInterviewSession() {
        MockHttpSession session = new MockHttpSession();
        InterviewSession interviewSession = sampleSession();

        localService.setInterviewSession(session, interviewSession);
        assertThat(localService.getInterviewSession(session)).isSameAs(interviewSession);

        localService.clearInterviewSession(session);
        assertThat(localService.getInterviewSession(session)).isNull();
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

        localService.setLastSessionSummary(session, summary);
        assertThat(localService.getLastSessionSummary(session)).contains(summary);

        localService.clearLastSessionSummary(session);
        assertThat(localService.getLastSessionSummary(session)).isEmpty();
    }

    @Test
    void localModeClearsForeignOwnershipQuietly() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("alice", "n/a", List.of()));
        MockHttpSession session = new MockHttpSession();
        localService.setInterviewSession(session, sampleSession());

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("bob", "n/a", List.of()));
        assertThat(localService.getInterviewSession(session)).isNull();
    }

    @Test
    void multiModeDeniesForeignOwnership() {
        AppProperties multi = new AppProperties();
        multi.getSecurity().setMode("multi");
        HttpSessionStateService multiService =
                new HttpSessionStateService(new CurrentUserAccessor(), multi);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("alice", "n/a", List.of()));
        MockHttpSession session = new MockHttpSession();
        multiService.setInterviewSession(session, sampleSession());

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("bob", "n/a", List.of()));
        assertThatThrownBy(() -> multiService.getInterviewSession(session))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("ownership");
    }

    @Test
    void clearInterviewSessionKeepsOwnershipWhileSummaryPresent() {
        AppProperties multi = new AppProperties();
        multi.getSecurity().setMode("multi");
        HttpSessionStateService multiService =
                new HttpSessionStateService(new CurrentUserAccessor(), multi);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("alice", "n/a", List.of()));
        MockHttpSession session = new MockHttpSession();
        multiService.setInterviewSession(session, sampleSession());
        SessionSummary summary = SessionSummary.builder()
                .mode(InterviewMode.TRAINING)
                .duration(Duration.ofMinutes(1))
                .totalQuestions(1)
                .correctCount(1)
                .wrongCount(0)
                .build();
        multiService.setLastSessionSummary(session, summary);
        multiService.clearInterviewSession(session);

        assertThat(multiService.getLastSessionSummary(session)).contains(summary);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("bob", "n/a", List.of()));
        assertThatThrownBy(() -> multiService.getLastSessionSummary(session))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void multiModeRejectsUntaggedSummaryPayload() {
        AppProperties multi = new AppProperties();
        multi.getSecurity().setMode("multi");
        HttpSessionStateService multiService =
                new HttpSessionStateService(new CurrentUserAccessor(), multi);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("alice", "n/a", List.of()));
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("lastSessionSummary", SessionSummary.builder()
                .mode(InterviewMode.TRAINING)
                .duration(Duration.ofMinutes(1))
                .totalQuestions(1)
                .correctCount(0)
                .wrongCount(1)
                .build());

        assertThatThrownBy(() -> multiService.getLastSessionSummary(session))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("missing");
    }

    private static InterviewSession sampleSession() {
        return new InterviewSession(
                InterviewMode.TRAINING,
                List.of(1L),
                "java",
                "backend",
                false,
                false,
                false,
                true
        );
    }

    @Test
    void recallAnswerSurvivesPauseAndRemainsBoundToItsQuestion() {
        MockHttpSession session = new MockHttpSession();
        InterviewSession interview = new InterviewSession(InterviewMode.TYPED_RECALL,
                List.of(1L, 2L), "java", false, false, false);
        localService.setInterviewSession(session, interview);
        String text = "  Собственный ответ <code>\nС сохранёнными пробелами";
        localService.setRecallAnswer(session, interview, text);
        assertThat(localService.getRecallAnswer(session, interview)).isEqualTo(text);
        localService.clearInterviewSession(session);
        localService.setInterviewSession(session, interview);
        assertThat(localService.getRecallAnswer(session, interview)).isEqualTo(text);
        interview.registerAnswer(true, "java");
        assertThat(localService.getRecallAnswer(session, interview)).isEmpty();
        localService.clearRecallAnswer(session);
        assertThat(localService.getRecallAnswer(session, interview)).isEmpty();
    }

    @Test
    void recallAnswerDoesNotCrossPrincipalAfterPause() {
        AppProperties multi = new AppProperties();
        multi.getSecurity().setMode("multi");
        HttpSessionStateService service = new HttpSessionStateService(new CurrentUserAccessor(), multi);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("alice", "n/a", List.of()));
        MockHttpSession session = new MockHttpSession();
        InterviewSession interview = sampleSession();
        service.setInterviewSession(session, interview);
        service.setRecallAnswer(session, interview, "Alice's answer");
        service.clearInterviewSession(session);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("bob", "n/a", List.of()));
        assertThatThrownBy(() -> service.getRecallAnswer(session, interview))
                .isInstanceOf(AccessDeniedException.class);
    }
}
