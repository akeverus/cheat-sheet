package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.api.controller.HttpSessionStateService;
import com.cheatsheet.quiz.api.controller.InterviewSessionSupport;
import com.cheatsheet.quiz.api.controller.MvcNavigationService;
import com.cheatsheet.quiz.api.dto.request.StartSessionRequest;
import com.cheatsheet.quiz.api.dto.request.SubmitAnswerRequest;
import com.cheatsheet.quiz.api.mapper.MvcAnswerRequestMapper;
import com.cheatsheet.quiz.api.mapper.MvcModelAttributeMapper;
import com.cheatsheet.quiz.api.mapper.MvcRequestMapper;
import com.cheatsheet.quiz.domain.InterviewSession;
import com.cheatsheet.quiz.domain.SessionSummary;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.function.Function;

/**
 * Use-case orchestration для MVC flow endpoint-ов.
 */
@Service
public class InterviewFlowMvcService {

    private final InterviewSessionSupport sessionSupport;
    private final SessionFlowService sessionFlowService;
    private final HttpSessionStateService httpSessionStateService;
    private final MvcNavigationService navigationService;
    private final MvcAnswerRequestMapper answerRequestMapper;
    private final MvcModelAttributeMapper modelAttributeMapper;
    private final MvcRequestMapper requestMapper;
    private final AnswerPageService answerPageService;

    public InterviewFlowMvcService(
            InterviewSessionSupport sessionSupport,
            SessionFlowService sessionFlowService,
            HttpSessionStateService httpSessionStateService,
            MvcNavigationService navigationService,
            MvcAnswerRequestMapper answerRequestMapper,
            MvcModelAttributeMapper modelAttributeMapper,
            MvcRequestMapper requestMapper,
            AnswerPageService answerPageService
    ) {
        this.sessionSupport = sessionSupport;
        this.sessionFlowService = sessionFlowService;
        this.httpSessionStateService = httpSessionStateService;
        this.navigationService = navigationService;
        this.answerRequestMapper = answerRequestMapper;
        this.modelAttributeMapper = modelAttributeMapper;
        this.requestMapper = requestMapper;
        this.answerPageService = answerPageService;
    }

    public String start(StartSessionRequest request, HttpSession session) {
        var selected = requestMapper.resolveStartMode(request);
        var filter = requestMapper.resolveStartFilter(request);
        SessionFlowService.StartFlowResult flowResult =
                sessionFlowService.startSession(selected, request.getCount(), filter);
        applyStartSessionResult(session, flowResult);
        return navigationService.focusRedirect();
    }

    public String studyConfirm(HttpSession session) {
        return updateSessionAndFocusRedirect(session, sessionFlowService::applyStudyConfirm);
    }

    public String flashcardReveal(HttpSession session) {
        return updateSessionAndFocusRedirect(session, sessionFlowService::applyFlashcardReveal);
    }

    public String flashcardGrade(long questionId, int grade, HttpSession session) {
        return updateSessionAndFocusRedirect(
                session,
                interviewSession -> sessionFlowService.applyFlashcardGrade(interviewSession, questionId, grade)
        );
    }

    public String finish(HttpSession session) {
        InterviewSession interviewSession = sessionSupport.getSession(session);
        sessionFlowService.buildSummary(interviewSession)
                .ifPresent(summary -> httpSessionStateService.setLastSessionSummary(session, summary));
        httpSessionStateService.clearInterviewSession(session);
        return navigationService.sessionSummaryRedirect();
    }

    public String sessionSummary(HttpSession session, Model model) {
        SessionSummary summary = httpSessionStateService.getLastSessionSummary(session).orElse(null);
        if (summary == null) {
            return navigationService.focusRedirect();
        }
        modelAttributeMapper.applySessionSummary(model, summary);
        httpSessionStateService.clearLastSessionSummary(session);
        return navigationService.sessionSummaryView();
    }

    public String answer(SubmitAnswerRequest request, HttpSession session, Model model) {
        MvcAnswerRequestMapper.AnswerSubmission submission = answerRequestMapper.toSubmission(request);
        InterviewSessionSupport.AnswerSubmission answerSubmission = new InterviewSessionSupport.AnswerSubmission(
                submission.questionId(),
                submission.optionId(),
                submission.topic(),
                submission.group(),
                submission.important(),
                submission.onlyWrong(),
                submission.shuffle(),
                submission.ordered(),
                submission.confidence()
        );
        InterviewSessionSupport.AnswerContext ctx = sessionSupport.processAnswer(answerSubmission, session);
        AnswerPageService.AnswerPageState state = answerPageService.build(
                ctx.result(),
                ctx.filter(),
                ctx.interviewSession()
        );
        modelAttributeMapper.applyAnswerPageState(model, state);

        return navigationService.resultView();
    }

    private void applyStartSessionResult(HttpSession session, SessionFlowService.StartFlowResult flowResult) {
        if (flowResult.clearSession()) {
            httpSessionStateService.clearInterviewSession(session);
            return;
        }
        InterviewSession interviewSession = flowResult.interviewSession();
        if (interviewSession != null) {
            httpSessionStateService.setInterviewSession(session, interviewSession);
        }
    }

    private String updateSessionAndFocusRedirect(HttpSession session, Function<InterviewSession, Boolean> sessionUpdater) {
        InterviewSession interviewSession = sessionSupport.getSession(session);
        boolean sessionChanged = sessionUpdater.apply(interviewSession);
        if (sessionChanged) {
            httpSessionStateService.setInterviewSession(session, interviewSession);
        }
        return navigationService.focusRedirect();
    }
}
