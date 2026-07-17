package com.cheatsheet.quiz.feature.interview.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import com.cheatsheet.quiz.domain.AnswerResult;
import com.cheatsheet.quiz.domain.AttemptOutcome;
import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.domain.InterviewMode;
import com.cheatsheet.quiz.domain.InterviewSession;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.feature.interview.service.core.InterviewService;
import com.cheatsheet.quiz.feature.interview.service.review.AttemptRecorder;
import com.cheatsheet.quiz.common.util.FilterUtils;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import lombok.Builder;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

/**
 * Общая логика работы с HTTP-сессией интервью, используемая обоими контроллерами
 * (API и MVC) для устранения дублирования.
 */
@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InterviewSessionSupport {
    InterviewService interviewService;
    HttpSessionStateService httpSessionStateService;
    AttemptRecorder attemptRecorder;

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
        // Построчный лог попытки (Фаза 2). Не критичный путь — журнал не должен
        // ронять ответ, поэтому обёрнут в try/catch. response_time пока не измеряется
        // (появится вместе с метрикой avg-response-time).
        recordAttempt(
                submission.questionId(),
                AttemptOutcome.ofCorrect(result.correctAnswer()),
                submission.confidence(),
                submission.optionId(),
                session);

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

    /**
     * Обрабатывает исход «не знаю» (UNKNOWN, FLOW-03) для активной сессии: применяет
     * SM-2-лапс (грейд 0) и продвигает сессию как незнание. Идемпотентен и безопасен:
     * при отсутствующей/завершённой сессии или несовпадении текущего вопроса
     * (устаревший/дублирующий skip) — БЕЗ побочных эффектов. Для EXAM незнание, как и
     * неверный ответ, добавляет штрафные вопросы; для STUDY возвращает фазу LEARN.
     *
     * @param questionId id пропускаемого вопроса
     * @param session HTTP-сессия
     * @return текущая {@link InterviewSession} (может быть {@code null} для TRAINING)
     */
    public InterviewSession processSkip(long questionId, HttpSession session) {
        InterviewSession interviewSession = getSession(session);
        if (interviewSession == null || interviewSession.isFinished()) {
            return interviewSession;
        }
        Long current = interviewSession.currentQuestionId();
        if (current == null || current != questionId) {
            return interviewSession;
        }
        Question question = interviewService.submitUnknown(questionId);
        interviewSession.registerUnknown(question.topic());
        if (interviewSession.getMode() == InterviewMode.EXAM) {
            interviewService.addExamPenaltyQuestions(interviewSession);
        }
        if (interviewSession.getMode() == InterviewMode.STUDY) {
            interviewSession.switchToLearnPhase();
        }
        httpSessionStateService.setInterviewSession(session, interviewSession);
        // Лог попытки «не знаю» (UNKNOWN, грейд 0, без выбранного варианта).
        recordAttempt(questionId, AttemptOutcome.UNKNOWN, 0, null, session);
        return interviewSession;
    }

    /**
     * Пишет попытку в журнал, изолируя сбой записи от пользовательского флоу:
     * ответ уже применён (SM-2 + сессия), поэтому исключение журнала лишь логируем.
     */
    private void recordAttempt(long questionId, AttemptOutcome outcome, Integer memoryGrade,
                               Long selectedOptionId, HttpSession session) {
        try {
            attemptRecorder.record(
                    questionId,
                    outcome,
                    memoryGrade,
                    selectedOptionId,
                    null,
                    session.getId());
        } catch (Exception e) {
            log.warn("Не удалось записать attempt для вопроса id={}: {}", questionId, e.getMessage());
        }
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
