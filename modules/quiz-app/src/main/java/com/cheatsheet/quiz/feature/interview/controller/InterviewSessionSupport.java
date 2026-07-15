package com.cheatsheet.quiz.feature.interview.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import com.cheatsheet.quiz.domain.AnswerResult;
import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.domain.InterviewMode;
import com.cheatsheet.quiz.domain.InterviewSession;
import com.cheatsheet.quiz.feature.interview.service.core.InterviewService;
import com.cheatsheet.quiz.common.util.FilterUtils;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import lombok.Builder;
import lombok.experimental.FieldDefaults;

/**
 * Общая логика работы с HTTP-сессией интервью, используемая обоими контроллерами
 * (API и MVC) для устранения дублирования.
 */
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InterviewSessionSupport {
    InterviewService interviewService;
    HttpSessionStateService httpSessionStateService;

    public InterviewSession getSession(HttpSession session) {
        return httpSessionStateService.getInterviewSession(session);
    }

    /**
     * Обрабатывает ответ пользователя: определяет фильтр из сессии или параметров,
     * отправляет ответ в сервис, обновляет сессию.
     */
    public AnswerContext processAnswer(AnswerSubmission submission, HttpSession session) {
        InterviewSession interviewSession = getSession(session);
        InterviewFilter filter = resolveFilter(
                interviewSession,
                submission.topic(),
                submission.group(),
                submission.important(),
                submission.onlyWrong(),
                submission.shuffle(),
                submission.ordered()
        );
        // FLOW-02 — серверная идемпотентность. Если у активной сессии текущий вопрос
        // уже сменился (двойной/устаревший POST того же questionId: retry, двойной
        // клик в обход клиентского guard, resubmit из истории), НЕ пишем SM-2 повторно
        // и НЕ двигаем сессию: возвращаем read-only оценку (тот же вердикт) без
        // побочных эффектов. Покрывает сессионные режимы (EXAM/MARATHON/STUDY/FLASHCARD);
        // TRAINING без сессии (interviewSession == null) и finished-сессия — прежнее
        // поведение (submitAnswer вызывается: последнее сохраняется контрактом теста
        // processAnswerDoesNotMutateOrPersistWhenSessionAlreadyFinished).
        if (isStaleDuplicate(interviewSession, submission)) {
            AnswerResult replay = interviewService.evaluateAnswer(
                    submission.questionId(),
                    submission.optionId()
            );
            return new AnswerContext(replay, filter, interviewSession);
        }
        AnswerResult result = interviewService.submitAnswer(
                submission.questionId(),
                submission.optionId(),
                filter,
                submission.confidence()
        );
        applySessionProgress(interviewSession, result, session);

        return new AnswerContext(result, filter, interviewSession);
    }

    /**
     * Устаревший дубль: у активной незавершённой сессии текущий вопрос уже не тот,
     * что пришёл в submission (сессия ушла вперёд) → повторная запись SM-2 и второй
     * index++ недопустимы. Сверяем именно с вопросом на текущем индексе
     * ({@link InterviewSession#currentQuestionId()}), а не «был ли когда-либо
     * отвечен» — иначе legit-повтор questionId в EXAM (penalty-requeue) заблокировался бы.
     */
    private boolean isStaleDuplicate(InterviewSession interviewSession, AnswerSubmission submission) {
        if (interviewSession == null || interviewSession.isFinished()) {
            return false;
        }
        Long current = interviewSession.currentQuestionId();
        return current != null && current != submission.questionId();
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

    @Builder(toBuilder = true)
    public record AnswerContext(AnswerResult result, InterviewFilter filter, InterviewSession interviewSession) {
    }

    @Builder(toBuilder = true)
    public record AnswerSubmission(
            long questionId,
            long optionId,
            String topic,
            String group,
            Boolean important,
            Boolean onlyWrong,
            Boolean shuffle,
            Boolean ordered,
            Integer confidence
    ) {
    }
}
