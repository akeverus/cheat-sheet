package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.config.app.AppProperties;
import com.cheatsheet.quiz.domain.FlashcardPhase;
import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.domain.InterviewMode;
import com.cheatsheet.quiz.domain.InterviewSession;
import com.cheatsheet.quiz.domain.SessionSummary;
import com.cheatsheet.quiz.domain.StudyPhase;
import com.cheatsheet.quiz.feature.interview.service.facade.InterviewFacade;
import com.cheatsheet.quiz.feature.interview.service.flow.SessionFlowService;
import com.cheatsheet.quiz.feature.interview.service.flow.SessionSummaryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionFlowServiceTest {

    @Mock
    InterviewFacade facade;
    @Mock
    SessionSummaryService sessionSummaryService;
    @Mock
    SessionSummary sessionSummary;

    SessionFlowService service;

    @BeforeEach
    void setUp() {
        AppProperties appProperties = new AppProperties();
        appProperties.getInterview().setDefaultExamCount(20);
        appProperties.getInterview().setDefaultMarathonCount(50);
        appProperties.getInterview().setMaxSessionCount(100);
        service = new SessionFlowService(facade, sessionSummaryService, appProperties);
    }

    @Test
    void startSessionClearsForTrainingMode() {
        SessionFlowService.StartFlowResult result = service.startSession(
                InterviewMode.TRAINING,
                30,
                new InterviewFilter("java", "backend", false, false, false, true)
        );

        assertThat(result.clearSession()).isTrue();
        assertThat(result.interviewSession()).isNull();
    }

    @Test
    void startSessionUsesDefaultAndUpperBound() {
        InterviewFilter filter = new InterviewFilter("java", "backend", false, false, false, true);
        InterviewSession session = new InterviewSession(InterviewMode.EXAM, List.of(1L), "java", "backend", false, false, false, true);
        when(facade.startSession(InterviewMode.EXAM, 20, filter)).thenReturn(session);

        SessionFlowService.StartFlowResult result = service.startSession(InterviewMode.EXAM, null, filter);

        assertThat(result.clearSession()).isFalse();
        assertThat(result.interviewSession()).isSameAs(session);
        verify(facade).startSession(InterviewMode.EXAM, 20, filter);
    }

    @Test
    void startSessionClampsRequestedCountToAllowedBounds() {
        InterviewFilter filter = new InterviewFilter("java", "backend", false, false, false, true);
        InterviewSession lowerBoundSession = new InterviewSession(
                InterviewMode.EXAM, List.of(1L), "java", "backend", false, false, false, true
        );
        InterviewSession upperBoundSession = new InterviewSession(
                InterviewMode.MARATHON, List.of(1L), "java", "backend", false, false, false, true
        );
        when(facade.startSession(InterviewMode.EXAM, 1, filter)).thenReturn(lowerBoundSession);
        when(facade.startSession(InterviewMode.MARATHON, 100, filter)).thenReturn(upperBoundSession);

        SessionFlowService.StartFlowResult minResult = service.startSession(InterviewMode.EXAM, 0, filter);
        SessionFlowService.StartFlowResult maxResult = service.startSession(InterviewMode.MARATHON, 1000, filter);

        assertThat(minResult.interviewSession()).isSameAs(lowerBoundSession);
        assertThat(maxResult.interviewSession()).isSameAs(upperBoundSession);
        verify(facade).startSession(InterviewMode.EXAM, 1, filter);
        verify(facade).startSession(InterviewMode.MARATHON, 100, filter);
    }

    @Test
    void applyStudyConfirmSwitchesLearnToQuiz() {
        InterviewSession session = new InterviewSession(InterviewMode.STUDY, List.of(1L), "java", "backend", false, false, false, true);
        assertThat(session.getStudyPhase()).isEqualTo(StudyPhase.LEARN);

        boolean changed = service.applyStudyConfirm(session);

        assertThat(changed).isTrue();
        assertThat(session.getStudyPhase()).isEqualTo(StudyPhase.QUIZ);
    }

    @Test
    void applyFlashcardGradeReturnsFalseForNonFlashcardSession() {
        InterviewSession session = new InterviewSession(InterviewMode.STUDY, List.of(1L), "java", "backend", false, false, false, true);

        boolean updated = service.applyFlashcardGrade(session, 1L, 5);

        assertThat(updated).isFalse();
    }

    @Test
    void applyFlashcardGradeUpdatesFlashcardSession() {
        InterviewSession session = new InterviewSession(InterviewMode.FLASHCARD, List.of(1L), "java", "backend", false, false, false, true);

        boolean updated = service.applyFlashcardGrade(session, 1L, 5);

        assertThat(updated).isTrue();
        verify(facade).submitFlashcardGrade(eq(1L), anyBoolean(), anyInt());
        assertThat(session.getIndex()).isEqualTo(1);
    }

    @Test
    void applyFlashcardRevealReturnsFalseWhenPhaseAlreadyRevealed() {
        InterviewSession session = new InterviewSession(InterviewMode.FLASHCARD, List.of(1L), "java", "backend", false, false, false, true);
        assertThat(session.getFlashcardPhase()).isEqualTo(FlashcardPhase.QUESTION);

        boolean firstReveal = service.applyFlashcardReveal(session);
        boolean secondReveal = service.applyFlashcardReveal(session);

        assertThat(firstReveal).isTrue();
        assertThat(secondReveal).isFalse();
    }

    @Test
    void buildSummaryReturnsOptionalWhenAvailable() {
        InterviewSession session = new InterviewSession(InterviewMode.EXAM, List.of(1L), "java", "backend", false, false, false, true);
        when(sessionSummaryService.buildSummary(session)).thenReturn(sessionSummary);

        Optional<SessionSummary> summary = service.buildSummary(session);

        assertThat(summary).contains(sessionSummary);
    }
}
