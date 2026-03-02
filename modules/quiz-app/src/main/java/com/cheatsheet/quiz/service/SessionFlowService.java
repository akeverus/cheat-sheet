package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.config.AppProperties;
import com.cheatsheet.quiz.domain.FlashcardGrade;
import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.domain.InterviewMode;
import com.cheatsheet.quiz.domain.InterviewSession;
import com.cheatsheet.quiz.domain.SessionSummary;
import com.cheatsheet.quiz.domain.StudyPhase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Сервис управления сценариями жизненного цикла учебной сессии.
 *
 * <p>Инкапсулирует flow-логику, чтобы MVC-контроллер выполнял только транспортные задачи.</p>
 */
@Service
@Slf4j
public class SessionFlowService {

    private final InterviewFacade facade;
    private final SessionSummaryService sessionSummaryService;
    private final AppProperties appProperties;

    public SessionFlowService(
            InterviewFacade facade,
            SessionSummaryService sessionSummaryService,
            AppProperties appProperties
    ) {
        this.facade = facade;
        this.sessionSummaryService = sessionSummaryService;
        this.appProperties = appProperties;
    }

    /**
     * Вычисляет результат старта сессии.
     */
    public StartFlowResult startSession(InterviewMode selected, Integer requestedCount, InterviewFilter filter) {
        if (selected == InterviewMode.TRAINING) {
            return new StartFlowResult(true, null);
        }
        int total = resolveSessionTotal(selected, requestedCount);
        InterviewSession session = facade.startSession(selected, total, filter);
        log.info("session_started mode={} total={} topic={} group={}",
                selected, total, filter.topic(), filter.group());
        return new StartFlowResult(false, session);
    }

    /**
     * Переключает STUDY-сессию из LEARN в QUIZ.
     */
    public boolean applyStudyConfirm(InterviewSession interviewSession) {
        if (!canSwitchStudyToQuiz(interviewSession)) {
            return false;
        }
        interviewSession.switchToQuizPhase();
        return true;
    }

    /**
     * Раскрывает ответ в FLASHCARD-сессии.
     */
    public boolean applyFlashcardReveal(InterviewSession interviewSession) {
        if (!canRevealFlashcard(interviewSession)) {
            return false;
        }
        interviewSession.revealFlashcard();
        return true;
    }

    /**
     * Применяет оценку flashcard-ответа и продвигает сессию.
     */
    public boolean applyFlashcardGrade(InterviewSession interviewSession, long questionId, int grade) {
        if (!canApplyFlashcardGrade(interviewSession)) {
            return false;
        }
        submitFlashcardGrade(questionId, grade);
        advanceFlashcardSession(interviewSession);
        return true;
    }

    /**
     * Собирает summary для завершения сессии, если это применимо.
     */
    public Optional<SessionSummary> buildSummary(InterviewSession interviewSession) {
        if (interviewSession == null || interviewSession.getMode() == InterviewMode.TRAINING) {
            return Optional.empty();
        }
        try {
            return Optional.of(sessionSummaryService.buildSummary(interviewSession));
        } catch (Exception e) {
            log.warn("session_summary_build_failed mode={} message={}",
                    interviewSession.getMode(), e.getMessage());
            return Optional.empty();
        }
    }

    public record StartFlowResult(boolean clearSession, InterviewSession interviewSession) {
    }

    private int resolveSessionTotal(InterviewMode selected, Integer requestedCount) {
        int defaultCount = selected == InterviewMode.EXAM
                ? appProperties.getInterview().getDefaultExamCount()
                : appProperties.getInterview().getDefaultMarathonCount();
        int requestedOrDefault = requestedCount != null ? requestedCount : defaultCount;
        return Math.min(Math.max(1, requestedOrDefault), appProperties.getInterview().getMaxSessionCount());
    }

    private boolean canSwitchStudyToQuiz(InterviewSession interviewSession) {
        return interviewSession != null
                && interviewSession.getMode() == InterviewMode.STUDY
                && interviewSession.getStudyPhase() == StudyPhase.LEARN;
    }

    private boolean canRevealFlashcard(InterviewSession interviewSession) {
        return interviewSession != null
                && interviewSession.isFlashcardMode()
                && interviewSession.getFlashcardPhase() == com.cheatsheet.quiz.domain.FlashcardPhase.QUESTION;
    }

    private boolean canApplyFlashcardGrade(InterviewSession interviewSession) {
        return interviewSession != null && interviewSession.isFlashcardMode();
    }

    private void submitFlashcardGrade(long questionId, int grade) {
        facade.submitFlashcardGrade(questionId, FlashcardGrade.isCorrect(grade), FlashcardGrade.toSm2Grade(grade));
    }

    private static void advanceFlashcardSession(InterviewSession interviewSession) {
        interviewSession.advanceFlashcard();
        interviewSession.resetFlashcardPhase();
    }
}
