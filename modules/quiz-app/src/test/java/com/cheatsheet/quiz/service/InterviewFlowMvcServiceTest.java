package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.feature.interview.controller.HttpSessionStateService;
import com.cheatsheet.quiz.feature.interview.controller.InterviewSessionSupport;
import com.cheatsheet.quiz.feature.interview.controller.MvcNavigationService;
import com.cheatsheet.quiz.api.dto.request.interview.StartSessionRequest;
import com.cheatsheet.quiz.api.dto.request.interview.SubmitAnswerRequest;
import com.cheatsheet.quiz.api.mapper.request.MvcAnswerRequestMapper;
import com.cheatsheet.quiz.api.mapper.request.MvcRequestMapper;
import com.cheatsheet.quiz.api.mapper.view.MvcModelAttributeMapper;
import com.cheatsheet.quiz.domain.AnswerResult;
import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.domain.InterviewMode;
import com.cheatsheet.quiz.domain.InterviewSession;
import com.cheatsheet.quiz.domain.SessionSummary;
import com.cheatsheet.quiz.feature.interview.service.flow.PauseService;
import com.cheatsheet.quiz.feature.interview.service.flow.SessionFlowService;
import com.cheatsheet.quiz.feature.interview.service.page.AnswerPageService;
import com.cheatsheet.quiz.feature.interview.usecase.mvc.InterviewFlowMvcService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.time.Duration;
import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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
    private PauseService pauseService;
    @Mock
    private HttpSession session;
    @Mock
    private Model model;

    @Mock
    com.cheatsheet.quiz.feature.interview.service.flow.SessionHistoryService sessionHistoryService;

    @Mock
    private com.cheatsheet.quiz.feature.interview.service.core.InterviewService interviewService;

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
                answerPageService,
                pauseService,
                sessionHistoryService,
                interviewService
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
        // Итоги прошлой сессии обесценены стартом новой: без этого
        // /session-summary однажды показал бы результат позапрошлого захода.
        verify(httpSessionStateService).clearLastSessionSummary(session);
        verify(httpSessionStateService).setInterviewSession(session, interviewSession);
        verify(httpSessionStateService, never()).clearInterviewSession(session);
    }

    @Test
    void startClearsSessionWhenFlowReturnsNoSessionPayload() {
        StartSessionRequest request = new StartSessionRequest();
        request.setMode("EXAM");
        request.setCount(7);
        InterviewFilter filter = new InterviewFilter("java", null, false, false, true, false);
        when(requestMapper.resolveStartMode(request)).thenReturn(InterviewMode.EXAM);
        when(requestMapper.resolveStartFilter(request)).thenReturn(filter);
        when(sessionFlowService.startSession(InterviewMode.EXAM, 7, filter))
                .thenReturn(new SessionFlowService.StartFlowResult(false, null));
        when(navigationService.focusRedirect()).thenReturn("redirect:/focus");

        String view = service.start(request, session);

        assertThat(view).isEqualTo("redirect:/focus");
        verify(httpSessionStateService).clearInterviewSession(session);
        verify(httpSessionStateService, never()).setInterviewSession(any(), any());
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
    void typedRevealStoresAnswerOnlyForCurrentQuestionOnFirstReveal() {
        InterviewSession interview = new InterviewSession(InterviewMode.TYPED_RECALL,
                List.of(55L, 56L), "java", false, false, false);
        when(sessionSupport.getSession(session)).thenReturn(interview);
        when(sessionFlowService.applyFlashcardReveal(interview)).thenReturn(true, false);
        service.flashcardReveal(56L, "stale tab", session);
        verify(sessionFlowService, never()).applyFlashcardReveal(any());
        service.flashcardReveal(55L, "my answer", session);
        service.flashcardReveal(55L, "repeated request", session);
        verify(httpSessionStateService).setRecallAnswer(session, interview, "my answer");
        verify(httpSessionStateService, never()).setRecallAnswer(session, interview, "repeated request");
    }

    @Test
    void studyConfirmClearsSessionWhenUpdaterReportsChangeWithoutSession() {
        when(sessionSupport.getSession(session)).thenReturn(null);
        when(sessionFlowService.applyStudyConfirm(null)).thenReturn(true);
        when(navigationService.focusRedirect()).thenReturn("redirect:/focus");

        String view = service.studyConfirm(session);

        assertThat(view).isEqualTo("redirect:/focus");
        verify(httpSessionStateService).clearInterviewSession(session);
        verify(httpSessionStateService, never()).setInterviewSession(any(), any());
    }

    /**
     * Итоги переживают повторный заход на страницу. Раньше рендер стирал summary,
     * и перезагрузка или «назад» выбрасывали на фокус — результат завершённой
     * сессии терялся от действия, которое пользователь считает безобидным.
     */
    @Test
    void sessionSummaryKeepsSummaryAcrossRepeatedVisits() {
        SessionSummary summary = SessionSummary.builder()
                .mode(InterviewMode.EXAM)
                .duration(Duration.ofMinutes(10))
                .totalQuestions(10)
                .correctCount(7)
                .wrongCount(3)
                .build();
        when(httpSessionStateService.getLastSessionSummary(session)).thenReturn(Optional.of(summary));
        when(navigationService.sessionSummaryView()).thenReturn("session-summary");

        assertThat(service.sessionSummary(session, model)).isEqualTo("session-summary");
        assertThat(service.sessionSummary(session, model)).isEqualTo("session-summary");

        verify(modelAttributeMapper, times(2)).applySessionSummary(model, summary);
        verify(httpSessionStateService, never()).clearLastSessionSummary(session);
    }

    @Test
    void finishPersistsSummaryAndClearsSession() {
        InterviewSession interviewSession = org.mockito.Mockito.mock(InterviewSession.class);
        SessionSummary summary = SessionSummary.builder()
                .mode(InterviewMode.EXAM)
                .duration(Duration.ofMinutes(5))
                .totalQuestions(5)
                .correctCount(4)
                .wrongCount(1)
                .build();
        when(sessionSupport.getSession(session)).thenReturn(interviewSession);
        when(sessionFlowService.buildSummary(interviewSession)).thenReturn(Optional.of(summary));
        when(navigationService.sessionSummaryRedirect()).thenReturn("redirect:/session-summary");

        String view = service.finish(session);

        assertThat(view).isEqualTo("redirect:/session-summary");
        verify(httpSessionStateService).setLastSessionSummary(session, summary);
        verify(httpSessionStateService).clearInterviewSession(session);
    }

    @Test
    void answerBuildsPageStateAndAppliesModelAttributes() {
        SubmitAnswerRequest request = new SubmitAnswerRequest();
        InterviewSessionSupport.AnswerSubmission submission = InterviewSessionSupport.AnswerSubmission.builder()
                .questionId(10L)
                .optionId(2L)
                .topic("java")
                .group("core")
                .important(true)
                .onlyWrong(false)
                .shuffle(true)
                .ordered(false)
                .confidence(4)
                .build();
        AnswerResult answerResult = org.mockito.Mockito.mock(AnswerResult.class);
        InterviewFilter filter = new InterviewFilter("java", "core", true, false, true, false);
        InterviewSession interviewSession = org.mockito.Mockito.mock(InterviewSession.class);
        InterviewSessionSupport.AnswerContext context = new InterviewSessionSupport.AnswerContext(
                answerResult,
                filter,
                interviewSession
        );
        AnswerPageService.AnswerPageState state = org.mockito.Mockito.mock(AnswerPageService.AnswerPageState.class);
        when(answerRequestMapper.toSubmission(request)).thenReturn(submission);
        when(sessionSupport.processAnswer(submission, session)).thenReturn(context);
        when(answerPageService.build(answerResult, filter, interviewSession)).thenReturn(state);
        when(navigationService.resultView()).thenReturn("result");

        String view = service.answer(request, session, model);

        assertThat(view).isEqualTo("result");
        verify(modelAttributeMapper).applyAnswerPageState(model, state);
    }

    @Test
    void pausePersistsActiveSessionAndClearsHttpSession() {
        InterviewSession interviewSession = org.mockito.Mockito.mock(InterviewSession.class);
        when(sessionSupport.getSession(session)).thenReturn(interviewSession);
        when(interviewSession.isFinished()).thenReturn(false);
        when(navigationService.focusRedirect()).thenReturn("redirect:/");

        String view = service.pause(session);

        assertThat(view).isEqualTo("redirect:/");
        verify(pauseService).pause(interviewSession);
        verify(httpSessionStateService).clearInterviewSession(session);
    }

    @Test
    void pauseDoesNothingWhenNoActiveSession() {
        when(sessionSupport.getSession(session)).thenReturn(null);
        when(navigationService.focusRedirect()).thenReturn("redirect:/");

        service.pause(session);

        verify(pauseService, never()).pause(any());
        verify(httpSessionStateService, never()).clearInterviewSession(session);
    }

    @Test
    void resumeRestoresSessionIntoHttpSession() {
        InterviewSession interviewSession = org.mockito.Mockito.mock(InterviewSession.class);
        when(pauseService.resume()).thenReturn(java.util.Optional.of(interviewSession));
        when(navigationService.focusRedirect()).thenReturn("redirect:/");

        String view = service.resume(session);

        assertThat(view).isEqualTo("redirect:/");
        verify(httpSessionStateService).clearLastSessionSummary(session);
        verify(httpSessionStateService).setInterviewSession(session, interviewSession);
    }

    @Test
    void resumeDoesNotTouchHttpSessionWhenNoPause() {
        when(pauseService.resume()).thenReturn(java.util.Optional.empty());
        when(navigationService.focusRedirect()).thenReturn("redirect:/");

        service.resume(session);

        verify(httpSessionStateService, never()).setInterviewSession(any(), any());
        // Паузы не было — восстанавливать нечего, и стирать чужие итоги не за что.
        verify(httpSessionStateService, never()).clearLastSessionSummary(any());
    }

    /** Вне сессии processSkip — no-op (null): разбор не обещан, прежний redirect. */
    @Test
    void skipOutsideSessionRedirectsToFocus() {
        when(sessionSupport.processSkip(42L, session)).thenReturn(null);
        when(navigationService.focusRedirect()).thenReturn("redirect:/");

        String view = service.skip(42L, session, model);

        assertThat(view).isEqualTo("redirect:/");
        verify(sessionSupport).processSkip(42L, session);
        verify(interviewService, never()).evaluateUnknown(anyLong());
    }

    /**
     * UX-15: skip MCQ-вопроса внутри сессии рендерит разбор (result view), а не
     * уводит к следующему вопросу — «не знаю» и есть запрос на объяснение.
     */
    @Test
    void skipInsideSessionRendersTheBreakdown() {
        InterviewSession interviewSession = mock(InterviewSession.class);
        when(interviewSession.mcqBlockIdxForQuestion(42L)).thenReturn(null);
        com.cheatsheet.quiz.domain.AnswerResult unknownResult = mock(com.cheatsheet.quiz.domain.AnswerResult.class);
        InterviewFilter filter = new InterviewFilter(null, null, null, null, null, null);
        AnswerPageService.AnswerPageState state = mock(AnswerPageService.AnswerPageState.class);
        when(sessionSupport.processSkip(42L, session)).thenReturn(interviewSession);
        when(interviewService.evaluateUnknown(42L)).thenReturn(java.util.Optional.of(unknownResult));
        when(sessionSupport.sessionFilter(interviewSession)).thenReturn(filter);
        when(answerPageService.build(unknownResult, filter, interviewSession)).thenReturn(state);
        when(navigationService.resultView()).thenReturn("result");

        String view = service.skip(42L, session, model);

        assertThat(view).isEqualTo("result");
        verify(modelAttributeMapper).applyAnswerPageState(model, state);
    }

    @Test
    void skipBreakdownUsesTheBlockRememberedForTheSkippedQuestion() {
        InterviewSession interviewSession = mock(InterviewSession.class);
        com.cheatsheet.quiz.domain.AnswerResult unknownResult = mock(com.cheatsheet.quiz.domain.AnswerResult.class);
        InterviewFilter filter = new InterviewFilter(null, null, null, null, null, null);
        AnswerPageService.AnswerPageState state = mock(AnswerPageService.AnswerPageState.class);
        when(sessionSupport.processSkip(42L, session)).thenReturn(interviewSession);
        when(interviewSession.mcqBlockIdxForQuestion(42L)).thenReturn(1);
        when(interviewService.evaluateUnknown(42L, 1)).thenReturn(java.util.Optional.of(unknownResult));
        when(sessionSupport.sessionFilter(interviewSession)).thenReturn(filter);
        when(answerPageService.build(unknownResult, filter, interviewSession)).thenReturn(state);
        when(navigationService.resultView()).thenReturn("result");

        String view = service.skip(42L, session, model);

        assertThat(view).isEqualTo("result");
        verify(modelAttributeMapper).applyAnswerPageState(model, state);
    }


    /** Optionless-ветка (флешкарта, ответ уже на экране): разбора нет — redirect. */
    @Test
    void skipOfOptionlessQuestionKeepsTheRedirect() {
        InterviewSession interviewSession = mock(InterviewSession.class);
        when(interviewSession.mcqBlockIdxForQuestion(42L)).thenReturn(null);
        when(sessionSupport.processSkip(42L, session)).thenReturn(interviewSession);
        when(interviewService.evaluateUnknown(42L)).thenReturn(java.util.Optional.empty());
        when(navigationService.focusRedirect()).thenReturn("redirect:/");

        String view = service.skip(42L, session, model);

        assertThat(view).isEqualTo("redirect:/");
        verify(answerPageService, never()).build(any(), any(), any());
    }
}
