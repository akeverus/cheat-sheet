package com.cheatsheet.quiz.feature.interview.controller;

import com.cheatsheet.quiz.domain.InterviewSession;
import com.cheatsheet.quiz.feature.interview.service.flow.PauseService;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InterviewSessionExpiryListenerTest {

    @Mock
    private PauseService pauseService;
    @Mock
    private HttpSessionStateService httpSessionStateService;
    @Mock
    private HttpSession httpSession;

    private InterviewSessionExpiryListener listener;
    private HttpSessionEvent event;

    @BeforeEach
    void setUp() {
        listener = new InterviewSessionExpiryListener(pauseService, httpSessionStateService);
        event = new HttpSessionEvent(httpSession);
    }

    @Test
    void autosavesActiveSessionWithProgress() {
        InterviewSession session = mock(InterviewSession.class);
        when(session.isFinished()).thenReturn(false);
        when(session.getIndex()).thenReturn(3);
        lenient().when(session.getTotal()).thenReturn(20);
        when(httpSessionStateService.getInterviewSession(httpSession)).thenReturn(session);

        listener.sessionDestroyed(event);

        verify(pauseService).pause(session);
    }

    @Test
    void ignoresSessionWithoutProgress() {
        InterviewSession session = mock(InterviewSession.class);
        when(session.isFinished()).thenReturn(false);
        when(session.getIndex()).thenReturn(0);
        when(httpSessionStateService.getInterviewSession(httpSession)).thenReturn(session);

        listener.sessionDestroyed(event);

        verify(pauseService, never()).pause(session);
    }

    @Test
    void ignoresFinishedSession() {
        InterviewSession session = mock(InterviewSession.class);
        when(session.isFinished()).thenReturn(true);
        when(httpSessionStateService.getInterviewSession(httpSession)).thenReturn(session);

        listener.sessionDestroyed(event);

        verify(pauseService, never()).pause(session);
    }

    @Test
    void ignoresMissingSession() {
        when(httpSessionStateService.getInterviewSession(httpSession)).thenReturn(null);

        listener.sessionDestroyed(event);

        verify(pauseService, never()).pause(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void swallowsAutosaveFailure() {
        InterviewSession session = mock(InterviewSession.class);
        when(session.isFinished()).thenReturn(false);
        when(session.getIndex()).thenReturn(5);
        when(httpSessionStateService.getInterviewSession(httpSession)).thenReturn(session);
        doThrow(new RuntimeException("db down")).when(pauseService).pause(session);

        // Не должно пробрасывать — best-effort автосейв не ломает teardown сессии.
        listener.sessionDestroyed(event);

        verify(pauseService).pause(session);
    }
}
