package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.api.controller.HttpSessionStateService;
import com.cheatsheet.quiz.api.controller.InterviewSessionSupport;
import com.cheatsheet.quiz.api.controller.MvcNavigationService;
import com.cheatsheet.quiz.api.dto.request.StartSessionRequest;
import com.cheatsheet.quiz.api.mapper.MvcAnswerRequestMapper;
import com.cheatsheet.quiz.api.mapper.MvcModelAttributeMapper;
import com.cheatsheet.quiz.api.mapper.MvcRequestMapper;
import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.domain.InterviewMode;
import com.cheatsheet.quiz.domain.InterviewSession;
import com.cheatsheet.quiz.domain.SessionSummary;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.time.Duration;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InterviewFlowMvcServiceTest {

    @Mock
    private InterviewSessionSupport sessionSupport;
    @Mock
    private SessionFlowService sessionFlowService;
    @Mock
    private HttpSessionStateService httpSessionStateService;
    @Mock
    private MvcNavigationService navigationService;
    @Mock
    private MvcAnswerRequestMapper answerRequestMapper;
    @Mock
    private MvcModelAttributeMapper modelAttributeMapper;
    @Mock
    private MvcRequestMapper requestMapper;
    @Mock
    private AnswerPageService answerPageService;
    @Mock
    private HttpSession session;
    @Mock
    private Model model;

    private InterviewFlowMvcService service;

    @BeforeEach
    void setUp() {
        service = new InterviewFlowMvcService(
                sessionSupport,
                sessionFlowService,
                httpSessionStateService,
                navigationService,
                answerRequestMapper,
                modelAttributeMapper,
                requestMapper,
                answerPageService
        );
    }

    @Test
    void startClearsSessionWhenFlowRequiresClear() {
        StartSessionRequest request = new StartSessionRequest();
        request.setMode("TRAINING");
        request.setCount(10);
        InterviewFilter filter = new InterviewFilter(null, null, null, null, null, null);
        when(requestMapper.resolveStartMode(request)).thenReturn(InterviewMode.TRAINING);
        when(requestMapper.resolveStartFilter(request)).thenReturn(filter);
        when(sessionFlowService.startSession(InterviewMode.TRAINING, 10, filter))
                .thenReturn(new SessionFlowService.StartFlowResult(true, null));
        when(navigationService.focusRedirect()).thenReturn("redirect:/");

        String view = service.start(request, session);

        assertThat(view).isEqualTo("redirect:/");
        verify(httpSessionStateService).clearInterviewSession(session);
        verify(httpSessionStateService, never()).setInterviewSession(any(), any());
    }

    @Test
    void flashcardGradeDoesNotPersistSessionWhenFlowRejectsUpdate() {
        InterviewSession interviewSession = org.mockito.Mockito.mock(InterviewSession.class);
        when(sessionSupport.getSession(session)).thenReturn(interviewSession);
        when(sessionFlowService.applyFlashcardGrade(interviewSession, 55L, 2)).thenReturn(false);
        when(navigationService.focusRedirect()).thenReturn("redirect:/");

        String view = service.flashcardGrade(55L, 2, session);

        assertThat(view).isEqualTo("redirect:/");
        verify(httpSessionStateService, never()).setInterviewSession(any(), any());
    }

    @Test
    void startPersistsSessionWhenFlowReturnsInterviewSession() {
        StartSessionRequest request = new StartSessionRequest();
        request.setMode("EXAM");
        request.setCount(5);
        InterviewFilter filter = new InterviewFilter("java", null, false, false, false, true);
        InterviewSession interviewSession = org.mockito.Mockito.mock(InterviewSession.class);
        when(requestMapper.resolveStartMode(request)).thenReturn(InterviewMode.EXAM);
        when(requestMapper.resolveStartFilter(request)).thenReturn(filter);
        when(sessionFlowService.startSession(InterviewMode.EXAM, 5, filter))
                .thenReturn(new SessionFlowService.StartFlowResult(false, interviewSession));
        when(navigationService.focusRedirect()).thenReturn("redirect:/focus");

        String view = service.start(request, session);

        assertThat(view).isEqualTo("redirect:/focus");
        verify(httpSessionStateService).setInterviewSession(session, interviewSession);
        verify(httpSessionStateService, never()).clearInterviewSession(session);
    }

    @Test
    void studyConfirmPersistsSessionWhenFlowChangesState() {
        InterviewSession interviewSession = org.mockito.Mockito.mock(InterviewSession.class);
        when(sessionSupport.getSession(session)).thenReturn(interviewSession);
        when(sessionFlowService.applyStudyConfirm(interviewSession)).thenReturn(true);
        when(navigationService.focusRedirect()).thenReturn("redirect:/focus");

        String view = service.studyConfirm(session);

        assertThat(view).isEqualTo("redirect:/focus");
        verify(httpSessionStateService).setInterviewSession(session, interviewSession);
    }

    @Test
    void flashcardRevealPersistsSessionWhenFlowChangesState() {
        InterviewSession interviewSession = org.mockito.Mockito.mock(InterviewSession.class);
        when(sessionSupport.getSession(session)).thenReturn(interviewSession);
        when(sessionFlowService.applyFlashcardReveal(interviewSession)).thenReturn(true);
        when(navigationService.focusRedirect()).thenReturn("redirect:/focus");

        String view = service.flashcardReveal(session);

        assertThat(view).isEqualTo("redirect:/focus");
        verify(httpSessionStateService).setInterviewSession(session, interviewSession);
    }

    @Test
    void sessionSummaryAppliesModelAndClearsSummaryWhenExists() {
        SessionSummary summary = SessionSummary.builder()
                .mode(InterviewMode.EXAM)
                .duration(Duration.ofMinutes(10))
                .totalQuestions(10)
                .correctCount(7)
                .wrongCount(3)
                .build();
        when(httpSessionStateService.getLastSessionSummary(session)).thenReturn(Optional.of(summary));
        when(navigationService.sessionSummaryView()).thenReturn("session-summary");

        String view = service.sessionSummary(session, model);

        assertThat(view).isEqualTo("session-summary");
        verify(modelAttributeMapper).applySessionSummary(model, summary);
        verify(httpSessionStateService).clearLastSessionSummary(session);
    }
}
