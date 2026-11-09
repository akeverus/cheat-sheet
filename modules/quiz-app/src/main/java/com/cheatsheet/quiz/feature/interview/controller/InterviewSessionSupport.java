package com.cheatsheet.quiz.feature.interview.controller;

import lombok.AccessLevel;
import com.cheatsheet.quiz.domain.AnswerResult;
import com.cheatsheet.quiz.domain.Attempt;
import com.cheatsheet.quiz.domain.AttemptOutcome;
import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.domain.InterviewMode;
import com.cheatsheet.quiz.domain.InterviewSession;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionRevision;
import com.cheatsheet.quiz.domain.exception.OptionNotFoundException;
import com.cheatsheet.quiz.domain.exception.QuestionNotFoundException;
import com.cheatsheet.quiz.feature.interview.service.command.SubmitAnswerCommand;
import com.cheatsheet.quiz.feature.interview.service.core.InterviewService;
import com.cheatsheet.quiz.feature.interview.service.review.AttemptRecorder;
import com.cheatsheet.quiz.feature.interview.service.review.RevisionMismatchException;
import com.cheatsheet.quiz.common.util.FilterUtils;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import lombok.Builder;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.time.Clock;
import java.util.Objects;

/**
 * Общая логика работы с HTTP-сессией интервью, используемая обоими контроллерами
 * (API и MVC) для устранения дублирования.
 */
@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InterviewSessionSupport {
    InterviewService interviewService;
    HttpSessionStateService httpSessionStateService;
    AttemptRecorder attemptRecorder;
    SubmitAnswerCommand submitAnswerCommand;
    Clock clock;
    Counter attemptJournalFailureCounter;
    Counter duplicateSubmitCounter;

    public InterviewSessionSupport(InterviewService interviewService,
                                   HttpSessionStateService httpSessionStateService,
                                   AttemptRecorder attemptRecorder,
                                   SubmitAnswerCommand submitAnswerCommand,
                                   Clock clock,
                                   MeterRegistry meterRegistry) {
        this.interviewService = interviewService;
        this.httpSessionStateService = httpSessionStateService;
        this.attemptRecorder = attemptRecorder;
        this.submitAnswerCommand = submitAnswerCommand;
        this.clock = clock;
        this.attemptJournalFailureCounter = Counter.builder("attempt.journal.write.failure")
                .description("Attempt journal write failures after SM-2/session commit (BE-007/OBS-001)")
                .register(meterRegistry);
        this.duplicateSubmitCounter = Counter.builder("attempt.submit.duplicate")
                .description("Duplicate answer submits: failed idempotency claim or stale session question (OBS-002)")
                .register(meterRegistry);
    }

    public InterviewSession getSession(HttpSession session) {
        return httpSessionStateService.getInterviewSession(session);
    }

    public String recallAnswer(HttpSession session, InterviewSession interviewSession) {
        return httpSessionStateService.getRecallAnswer(session, interviewSession);
    }

    /**
     * Фиксирует момент показа текущего вопроса активной сессии (для измерения
     * времени ответа, Фаза 4). Вызывается при рендере страницы вопроса. Без
     * сессии (TRAINING/browse) — no-op: время ответа там не измеряется.
     *
     * @param session          HTTP-сессия
     * @param interviewSession активная сессия интервью (может быть {@code null})
     */
    public void markQuestionServed(HttpSession session, InterviewSession interviewSession) {
        if (interviewSession == null) {
            return;
        }
        interviewSession.markQuestionServed(clock.millis());
        httpSessionStateService.setInterviewSession(session, interviewSession);
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
        // Устаревший дубль сессии: только подтверждённый replay без claim/SM-2.
        if (isStaleDuplicate(interviewSession, submission)) {
            duplicateSubmitCounter.increment();
            AnswerResult replay = replayAnswer(submission, session.getId());
            return new AnswerContext(replay, filter, interviewSession);
        }
        // Think-time до TX: consume server mark; HttpSession progress после commit.
        Integer responseTimeMs = resolveResponseTimeMs(submission.responseTimeMs(), interviewSession);
        // P0: claim + revision + SM-2 + journal в одной TX (SubmitAnswerCommand).
        AnswerResult result = submitAnswerCommand.executeOrNullIfDuplicate(
                submission, session.getId(), responseTimeMs);
        if (result == null) {
            duplicateSubmitCounter.increment();
            AnswerResult replay = replayAnswer(submission, session.getId());
            return new AnswerContext(replay, filter, interviewSession);
        }
        applySessionProgress(interviewSession, result, session);
        return new AnswerContext(result, filter, interviewSession);
    }

    private AnswerResult replayAnswer(AnswerSubmission submission, String sessionToken) {
        Attempt attempt = attemptRecorder.findByClientAttemptId(submission.clientAttemptId())
                .orElseThrow(() -> new IllegalStateException("Recorded MCQ attempt is unavailable for replay"));
        // Ключ уникален в пределах владельца, но сам по себе не подтверждает
        // принадлежность HTTP-сессии и вопросу. Fallback на новый выбор создал бы
        // видимость сохранённого ответа при уже занятом ключе.
        if (sessionToken == null || !sessionToken.equals(attempt.sessionToken())
                || attempt.questionId() != submission.questionId()
                || attempt.selectedOptionId() == null
                || (attempt.outcome() != AttemptOutcome.CORRECT && attempt.outcome() != AttemptOutcome.WRONG)) {
            throw new IllegalStateException("Recorded MCQ attempt does not match the session, question or answer");
        }
        QuestionRevision revision = attemptRecorder.requireCurrentRevision(
                attempt.questionId(), attempt.questionRevisionId());
        AnswerResult replay;
        try {
            replay = interviewService.evaluateAnswer(attempt.questionId(), attempt.selectedOptionId());
        } catch (QuestionNotFoundException | OptionNotFoundException ex) {
            throw new RevisionMismatchException("Recorded MCQ content is no longer available for replay", ex);
        }
        // Исторического снимка вариантов нет: при новой версии или изменившемся
        // вердикте нельзя выдавать текущую оценку за сохранённую попытку.
        if (!Objects.equals(revision.checksum(), replay.question().sourceHash())
                || replay.correctAnswer() != (attempt.outcome() == AttemptOutcome.CORRECT)) {
            throw new RevisionMismatchException("Recorded MCQ content changed before replay");
        }
        return replay;
    }

    /**
     * Клиентский think-time (0…30 мин) или серверный fallback; иначе {@code null}.
     * Серверный замер всё равно сбрасываем, чтобы следующий вопрос не унаследовал метку.
     */
    private Integer resolveResponseTimeMs(Integer clientMs, InterviewSession interviewSession) {
        Integer serverMs = interviewSession != null
                ? interviewSession.takeResponseTimeMs(clock.millis())
                : null;
        if (clientMs != null && clientMs >= 0 && clientMs <= 1_800_000) {
            return clientMs;
        }
        return serverMs;
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
        // Claim before SM-2 so double-skip / retry cannot lapse twice.
        String skipKey = skipIdempotencyKey(session.getId(), questionId);
        boolean claimed = attemptRecorder.tryClaimIdempotencyKey(skipKey);
        String topic = interviewSession.getTopic();
        if (claimed) {
            Question question = interviewService.submitUnknown(questionId);
            topic = question.topic();
        }
        interviewSession.registerUnknown(questionId, topic);
        if (interviewSession.getMode() == InterviewMode.EXAM) {
            interviewService.addExamPenaltyQuestions(interviewSession);
        }
        if (interviewSession.getMode() == InterviewMode.STUDY) {
            interviewSession.switchToLearnPhase();
        }
        Integer responseTimeMs = interviewSession.takeResponseTimeMs(clock.millis());
        httpSessionStateService.setInterviewSession(session, interviewSession);
        if (claimed) {
            recordAttempt(questionId, AttemptOutcome.UNKNOWN, 0, null, responseTimeMs, session,
                    skipKey, null, null);
        }
        return interviewSession;
    }

    private static String skipIdempotencyKey(String sessionId, long questionId) {
        String seed = (sessionId == null ? "" : sessionId) + ":" + questionId;
        String key = "skip-" + questionId + "-" + Integer.toUnsignedString(seed.hashCode());
        int max = com.cheatsheet.quiz.common.constants.QuizConstants.CLIENT_ATTEMPT_ID_MAX_LENGTH;
        return key.length() <= max ? key : key.substring(0, max);
    }

    /**
     * Пишет попытку в журнал, изолируя сбой записи от пользовательского флоу:
     * ответ уже применён (SM-2 + сессия), поэтому исключение журнала лишь логируем.
     */
    private void recordAttempt(long questionId, AttemptOutcome outcome, Integer memoryGrade,
                               Long selectedOptionId, Integer responseTimeMs, HttpSession session,
                               String clientAttemptId, String openedContentBlockIds,
                               Long questionRevisionId) {
        try {
            attemptRecorder.record(
                    questionId,
                    outcome,
                    memoryGrade,
                    selectedOptionId,
                    responseTimeMs,
                    session.getId(),
                    clientAttemptId,
                    openedContentBlockIds,
                    questionRevisionId);
        } catch (Exception e) {
            attemptJournalFailureCounter.increment();
            log.warn("Не удалось записать attempt для вопроса id={}: {}", questionId, e.getMessage());
        }
    }

    /**
     * Фильтр активной сессии — для рендера разбора вне submit-пути (UX-15:
     * страница результата после «не знаю» строится теми же атрибутами модели,
     * что и после ответа, а фильтр там приходит из сессии).
     */
    public InterviewFilter sessionFilter(InterviewSession interviewSession) {
        return resolveFilter(interviewSession, null, null, null, null, null, null);
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

    /**
     * Ответ пользователя вместе с контекстом выборки вопросов.
     *
     * <p>Собирается только билдером. Позиционные «обратно совместимые»
     * конструкторы убраны: внешних клиентов у внутреннего record нет, а
     * совместимость они обеспечивали лишь с собственными тестами — ценой
     * вызовов вида {@code (10L, 3L, null, null, null, null, null, null, 4, null)},
     * где перепутанный порядок восьми nullable-полей компилятор не поймает.</p>
     */
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
            Integer confidence,
            String clientAttemptId,
            String openedContentBlockIds,
            Integer responseTimeMs,
            Long questionRevisionId
    ) {
    }
}
