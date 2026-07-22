package com.cheatsheet.quiz.feature.interview.controller;

import com.cheatsheet.quiz.api.security.CurrentUserAccessor;
import com.cheatsheet.quiz.config.app.AppProperties;
import com.cheatsheet.quiz.domain.InterviewSession;
import com.cheatsheet.quiz.domain.SessionSummary;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Адаптер доступа к HTTP-session атрибутам MVC-интерфейса.
 *
 * <p>Централизует ключи и операции с состоянием UI-сессии,
 * чтобы контроллеры и сервисы не дублировали строковые ключи.</p>
 *
 * <p>In {@code app.security.mode=multi}, session payloads are tagged with
 * {@link CurrentUserAccessor#ownershipKey()}; cross-principal access is denied.</p>
 */
@Component
@RequiredArgsConstructor
public class HttpSessionStateService {

    private static final String INTERVIEW_SESSION_KEY = "interviewSession";
    private static final String LAST_SESSION_SUMMARY_KEY = "lastSessionSummary";
    private static final String OWNERSHIP_KEY = "sessionOwnershipKey";

    private final CurrentUserAccessor currentUserAccessor;
    private final AppProperties appProperties;

    /**
     * Читает активную interview-сессию без ownership-check (reaper / sessionDestroyed).
     */
    public InterviewSession peekInterviewSession(HttpSession session) {
        Object value = session.getAttribute(INTERVIEW_SESSION_KEY);
        return value instanceof InterviewSession interviewSession ? interviewSession : null;
    }

    /**
     * Ownership tag stored on the HTTP session, or {@code null} if unset.
     */
    public String peekOwnershipKey(HttpSession session) {
        Object stored = session.getAttribute(OWNERSHIP_KEY);
        return stored == null ? null : String.valueOf(stored);
    }

    /**
     * Читает активную interview-сессию из HttpSession.
     */
    public InterviewSession getInterviewSession(HttpSession session) {
        assertOwnership(session);
        Object value = session.getAttribute(INTERVIEW_SESSION_KEY);
        return value instanceof InterviewSession interviewSession ? interviewSession : null;
    }

    /**
     * Записывает активную interview-сессию в HttpSession.
     */
    public void setInterviewSession(HttpSession session, InterviewSession interviewSession) {
        session.setAttribute(OWNERSHIP_KEY, currentUserAccessor.ownershipKey());
        session.setAttribute(INTERVIEW_SESSION_KEY, interviewSession);
    }

    /**
     * Удаляет активную interview-сессию из HttpSession.
     * Ownership tag retained while {@link #LAST_SESSION_SUMMARY_KEY} is still present
     * so finish → summary remains scoped to the same principal in multi mode.
     */
    public void clearInterviewSession(HttpSession session) {
        session.removeAttribute(INTERVIEW_SESSION_KEY);
        if (session.getAttribute(LAST_SESSION_SUMMARY_KEY) == null) {
            session.removeAttribute(OWNERSHIP_KEY);
        }
    }

    /**
     * Читает summary последней завершенной сессии.
     */
    public Optional<SessionSummary> getLastSessionSummary(HttpSession session) {
        assertOwnership(session);
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
        session.setAttribute(OWNERSHIP_KEY, currentUserAccessor.ownershipKey());
        session.setAttribute(LAST_SESSION_SUMMARY_KEY, summary);
    }

    /**
     * Удаляет summary последней завершенной сессии.
     */
    public void clearLastSessionSummary(HttpSession session) {
        session.removeAttribute(LAST_SESSION_SUMMARY_KEY);
        if (session.getAttribute(INTERVIEW_SESSION_KEY) == null) {
            session.removeAttribute(OWNERSHIP_KEY);
        }
    }

    private void assertOwnership(HttpSession session) {
        Object stored = session.getAttribute(OWNERSHIP_KEY);
        boolean hasPayload = session.getAttribute(INTERVIEW_SESSION_KEY) != null
                || session.getAttribute(LAST_SESSION_SUMMARY_KEY) != null;
        if (stored == null) {
            // Multi: untagged payload is unsafe (e.g. after clear that dropped ownership early).
            if (isMultiMode() && hasPayload) {
                session.removeAttribute(INTERVIEW_SESSION_KEY);
                session.removeAttribute(LAST_SESSION_SUMMARY_KEY);
                throw new AccessDeniedException("HTTP session ownership missing");
            }
            return;
        }
        if (currentUserAccessor.ownershipKey().equals(String.valueOf(stored))) {
            return;
        }
        if (isMultiMode()) {
            throw new AccessDeniedException("HTTP session ownership mismatch");
        }
        // Local mode: treat as empty rather than hard-fail.
        session.removeAttribute(INTERVIEW_SESSION_KEY);
        session.removeAttribute(LAST_SESSION_SUMMARY_KEY);
        session.removeAttribute(OWNERSHIP_KEY);
    }

    private boolean isMultiMode() {
        return "multi".equalsIgnoreCase(appProperties.getSecurity().getMode());
    }
}
