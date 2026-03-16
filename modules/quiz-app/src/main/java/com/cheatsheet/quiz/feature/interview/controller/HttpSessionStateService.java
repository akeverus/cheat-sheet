package com.cheatsheet.quiz.feature.interview.controller;

import com.cheatsheet.quiz.domain.InterviewSession;
import com.cheatsheet.quiz.domain.SessionSummary;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Адаптер доступа к HTTP-session атрибутам MVC-интерфейса.
 *
 * <p>Централизует ключи и операции с состоянием UI-сессии,
 * чтобы контроллеры и сервисы не дублировали строковые ключи.</p>
 */
@Component
public class HttpSessionStateService {

    private static final String INTERVIEW_SESSION_KEY = "interviewSession";
    private static final String LAST_SESSION_SUMMARY_KEY = "lastSessionSummary";

    /**
     * Читает активную interview-сессию из HttpSession.
     */
    public InterviewSession getInterviewSession(HttpSession session) {
        Object value = session.getAttribute(INTERVIEW_SESSION_KEY);
        return value instanceof InterviewSession interviewSession ? interviewSession : null;
    }

    /**
     * Записывает активную interview-сессию в HttpSession.
     */
    public void setInterviewSession(HttpSession session, InterviewSession interviewSession) {
        session.setAttribute(INTERVIEW_SESSION_KEY, interviewSession);
    }

    /**
     * Удаляет активную interview-сессию из HttpSession.
     */
    public void clearInterviewSession(HttpSession session) {
        session.removeAttribute(INTERVIEW_SESSION_KEY);
    }

    /**
     * Читает summary последней завершенной сессии.
     */
    public Optional<SessionSummary> getLastSessionSummary(HttpSession session) {
        Object value = session.getAttribute(LAST_SESSION_SUMMARY_KEY);
        if (value instanceof SessionSummary summary) {
            return Optional.of(summary);
        }
        return Optional.empty();
    }

    /**
     * Сохраняет summary последней завершенной сессии.
     */
    public void setLastSessionSummary(HttpSession session, SessionSummary summary) {
        session.setAttribute(LAST_SESSION_SUMMARY_KEY, summary);
    }

    /**
     * Удаляет summary последней завершенной сессии.
     */
    public void clearLastSessionSummary(HttpSession session) {
        session.removeAttribute(LAST_SESSION_SUMMARY_KEY);
    }
}
