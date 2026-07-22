package com.cheatsheet.quiz.feature.interview.controller;

import com.cheatsheet.quiz.domain.InterviewSession;
import com.cheatsheet.quiz.feature.interview.service.flow.PauseService;
import com.cheatsheet.quiz.persistence.OwnerContext;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

/**
 * FLOW-04: автосейв активной сессии при истечении HTTP-session (таймаут/инвалидация).
 *
 * <p>Runs on the container reaper thread (no SecurityContext). Uses
 * {@link HttpSessionStateService#peekInterviewSession} + stored ownership tag so
 * multi-user pause rows are written under the correct {@code owner_key}.</p>
 */
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InterviewSessionExpiryListener implements HttpSessionListener {

    PauseService pauseService;
    HttpSessionStateService httpSessionStateService;

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession httpSession = se.getSession();
        try {
            InterviewSession session = httpSessionStateService.peekInterviewSession(httpSession);
            if (session == null || session.isFinished() || session.getIndex() <= 0) {
                return;
            }
            String owner = httpSessionStateService.peekOwnershipKey(httpSession);
            if (owner == null || owner.isBlank()) {
                owner = "local";
            }
            OwnerContext.set(owner);
            try {
                pauseService.pause(session);
                log.debug("Автосохранена истёкшая сессия для resume (отвечено {} из {})",
                        session.getIndex(), session.getTotal());
            } finally {
                OwnerContext.clear();
            }
        } catch (RuntimeException e) {
            // Автосейв — best-effort: сбой не должен мешать уничтожению сессии.
            log.warn("Не удалось автосохранить истёкшую сессию: {}", e.getMessage());
        }
    }
}
