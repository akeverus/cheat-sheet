package com.cheatsheet.quiz.api.controller;

import com.cheatsheet.quiz.domain.AnswerResult;
import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.domain.InterviewMode;
import com.cheatsheet.quiz.domain.InterviewSession;
import com.cheatsheet.quiz.service.InterviewService;
import com.cheatsheet.quiz.util.FilterUtils;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Общая логика работы с HTTP-сессией интервью, используемая обоими контроллерами
 * (API и MVC) для устранения дублирования.
 */
@Component
@RequiredArgsConstructor
public class InterviewSessionSupport {

    private final InterviewService interviewService;
    private final HttpSessionStateService httpSessionStateService;

    public InterviewSession getSession(HttpSession session) {
        return httpSessionStateService.getInterviewSession(session);
    }

    /**
     * Обрабатывает ответ пользователя: определяет фильтр из сессии или параметров,
     * отправляет ответ в сервис, обновляет сессию.
     */
    public AnswerContext processAnswer(long questionId, long optionId,
                                       String topic, String group, Boolean important, Boolean onlyWrong,
                                       Boolean shuffle, Boolean ordered, Integer confidence, HttpSession session) {
        InterviewSession interviewSession = getSession(session);
        InterviewFilter filter = resolveFilter(
                interviewSession,
                topic,
                group,
                important,
                onlyWrong,
                shuffle,
                ordered
        );
        AnswerResult result = interviewService.submitAnswer(questionId, optionId, filter, confidence);
        applySessionProgress(interviewSession, result, session);

        return new AnswerContext(result, filter, interviewSession);
    }

    private InterviewFilter resolveFilter(
            InterviewSession interviewSession,
            String topic,
            String group,
            Boolean important,
            Boolean onlyWrong,
            Boolean shuffle,
            Boolean ordered
    ) {
        if (interviewSession != null) {
            return new InterviewFilter(
                    interviewSession.getTopic(),
                    interviewSession.getGroup(),
                    interviewSession.getImportantOnly(),
                    interviewSession.getOnlyWrong(),
                    interviewSession.getShuffle(),
                    interviewSession.getOrdered()
            );
        }
        return new InterviewFilter(
                FilterUtils.normalizeTopic(topic),
                FilterUtils.normalizeGroup(group),
                important,
                onlyWrong,
                shuffle,
                ordered
        );
    }

    private void applySessionProgress(InterviewSession interviewSession, AnswerResult result, HttpSession session) {
        if (interviewSession == null || interviewSession.isFinished()) {
            return;
        }
        interviewSession.registerAnswer(result.correctAnswer(), result.question().topic());
        if (interviewSession.getMode() == InterviewMode.EXAM && !result.correctAnswer()) {
            interviewService.addExamPenaltyQuestions(interviewSession);
        }
        if (interviewSession.getMode() == InterviewMode.STUDY) {
            interviewSession.switchToLearnPhase();
        }
        httpSessionStateService.setInterviewSession(session, interviewSession);
    }

    public record AnswerContext(AnswerResult result, InterviewFilter filter, InterviewSession interviewSession) {
    }
}
