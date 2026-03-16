package com.cheatsheet.quiz.feature.interview.usecase.mvc;

import com.cheatsheet.quiz.feature.interview.controller.HttpSessionStateService;
import com.cheatsheet.quiz.feature.interview.controller.InterviewSessionSupport;
import com.cheatsheet.quiz.feature.interview.controller.MvcNavigationService;
import com.cheatsheet.quiz.api.dto.request.interview.StartSessionRequest;
import com.cheatsheet.quiz.api.dto.request.interview.SubmitAnswerRequest;
import com.cheatsheet.quiz.api.mapper.request.MvcAnswerRequestMapper;
import com.cheatsheet.quiz.api.mapper.request.MvcRequestMapper;
import com.cheatsheet.quiz.api.mapper.view.MvcModelAttributeMapper;
import com.cheatsheet.quiz.domain.InterviewSession;
import com.cheatsheet.quiz.domain.SessionSummary;
import com.cheatsheet.quiz.feature.interview.service.page.AnswerPageService;
import com.cheatsheet.quiz.feature.interview.service.flow.SessionFlowService;
import jakarta.servlet.http.HttpSession;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.function.Function;

/**
 * Use-case orchestration для MVC flow endpoint-ов.
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InterviewFlowMvcService {
    InterviewSessionSupport sessionSupport;
    SessionFlowService sessionFlowService;
    HttpSessionStateService httpSessionStateService;
    MvcNavigationService navigationService;
    MvcAnswerRequestMapper answerRequestMapper;
    MvcModelAttributeMapper modelAttributeMapper;
    MvcRequestMapper requestMapper;
    AnswerPageService answerPageService;

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
        persistSessionSummary(session, interviewSession);
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
        InterviewSessionSupport.AnswerContext ctx = processAnswer(request, session);
        AnswerPageService.AnswerPageState state = toAnswerPageState(ctx);
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
            return;
        }
        // Defensive guard: never keep stale interview session on ambiguous start result.
        httpSessionStateService.clearInterviewSession(session);
    }

    private String updateSessionAndFocusRedirect(HttpSession session, Function<InterviewSession, Boolean> sessionUpdater) {
        InterviewSession interviewSession = sessionSupport.getSession(session);
        boolean sessionChanged = sessionUpdater.apply(interviewSession);
        persistSessionIfChanged(session, interviewSession, sessionChanged);
        return navigationService.focusRedirect();
    }

    private void persistSessionSummary(HttpSession session, InterviewSession interviewSession) {
        sessionFlowService.buildSummary(interviewSession)
                .ifPresent(summary -> httpSessionStateService.setLastSessionSummary(session, summary));
    }

    private InterviewSessionSupport.AnswerContext processAnswer(SubmitAnswerRequest request, HttpSession session) {
        InterviewSessionSupport.AnswerSubmission submission = answerRequestMapper.toSubmission(request);
        return sessionSupport.processAnswer(submission, session);
    }

    private AnswerPageService.AnswerPageState toAnswerPageState(InterviewSessionSupport.AnswerContext ctx) {
        return answerPageService.build(
                ctx.result(),
                ctx.filter(),
                ctx.interviewSession()
        );
    }

    private void persistSessionIfChanged(HttpSession session, InterviewSession interviewSession, boolean sessionChanged) {
        if (!sessionChanged) {
            return;
        }
        if (interviewSession == null) {
            httpSessionStateService.clearInterviewSession(session);
            return;
        }
        httpSessionStateService.setInterviewSession(session, interviewSession);
    }
}
