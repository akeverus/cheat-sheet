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
import com.cheatsheet.quiz.feature.interview.service.core.InterviewService;
import com.cheatsheet.quiz.feature.interview.service.page.AnswerPageService;
import com.cheatsheet.quiz.feature.interview.service.flow.PauseService;
import com.cheatsheet.quiz.feature.interview.service.flow.SessionFlowService;
import com.cheatsheet.quiz.feature.interview.service.flow.SessionHistoryService;
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
    PauseService pauseService;
    SessionHistoryService sessionHistoryService;
    InterviewService interviewService;

    public String start(StartSessionRequest request, HttpSession session) {
        archiveActiveSession(session);
        var selected = requestMapper.resolveStartMode(request);
        var filter = requestMapper.resolveStartFilter(request);
        SessionFlowService.StartFlowResult flowResult =
                sessionFlowService.startSession(selected, request.getCount(), filter);
        applyStartSessionResult(session, flowResult);
        return navigationService.focusRedirect();
    }

    /**
     * Старт поверх активной сессии — не молчаливое затирание (UX-03): прогресс
     * прежней сессии фиксируется в журнале, как если бы её завершили. Пустую
     * сессию {@link SessionHistoryService#record} отбрасывает сам (ноль ответов —
     * нечего фиксировать), завершённую уже записал {@link #finish}. В HTTP-сводку
     * summary НЕ кладётся: {@link #applyStartSessionResult} тут же стёр бы её,
     * а показывать итоги старой сессии при старте новой — шум, не ответ.
     */
    private void archiveActiveSession(HttpSession session) {
        InterviewSession current = sessionSupport.getSession(session);
        if (current == null || current.isFinished()) {
            return;
        }
        sessionFlowService.buildSummary(current)
                .ifPresent(sessionHistoryService::record);
    }

    /**
     * Отбрасывает приостановленную сессию (UX-02): второй выход из баннера
     * «Есть незавершённая сессия» — раньше единственным был «Продолжить», и
     * {@link PauseService#discard} не вызывался никем, пауза была вечной.
     */
    public String discardPaused() {
        pauseService.discard();
        return navigationService.focusRedirect();
    }

    public String studyConfirm(HttpSession session) {
        return updateSessionAndFocusRedirect(session, sessionFlowService::applyStudyConfirm);
    }

    public String flashcardReveal(HttpSession session) {
        return flashcardReveal(null, null, session);
    }

    public String flashcardReveal(Long questionId, String typedAnswer, HttpSession session) {
        InterviewSession interviewSession = sessionSupport.getSession(session);
        if (interviewSession == null) return navigationService.focusRedirect();
        synchronized (interviewSession) {
            if (questionId != null && !questionId.equals(interviewSession.currentQuestionId())) {
                return navigationService.focusRedirect();
            }
            boolean changed = sessionFlowService.applyFlashcardReveal(interviewSession);
            if (changed && typedAnswer != null && interviewSession.getMode().usesTypedInput()) {
                httpSessionStateService.setRecallAnswer(session, interviewSession, typedAnswer);
            }
            persistSessionIfChanged(session, interviewSession, changed);
        }
        return navigationService.focusRedirect();
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
        httpSessionStateService.clearRecallAnswer(session);
        httpSessionStateService.clearInterviewSession(session);
        return navigationService.sessionSummaryRedirect();
    }

    /**
     * Ставит активную сессию на паузу (FLOW-01): персистит её в БД и очищает
     * HTTP-сессию. Пустую/завершённую сессию {@link PauseService} игнорирует.
     */
    public String pause(HttpSession session) {
        return pause(session, null);
    }

    /**
     * Пауза с целью перехода (UXR-01, REF-024): развилка «отложить сессию и
     * тренировать фильтр» несёт параметры цели, и redirect ведёт на browse-URL
     * этой цели. Собирается из БЕЛОГО СПИСКА параметров — никаких raw-URL,
     * open-redirect невозможен. Без цели — прежний redirect на голый фокус.
     */
    public String pause(HttpSession session, BrowseTarget target) {
        InterviewSession interviewSession = sessionSupport.getSession(session);
        if (interviewSession != null && !interviewSession.isFinished()) {
            pauseService.pause(interviewSession);
            httpSessionStateService.clearInterviewSession(session);
        }
        if (target == null || target.isEmpty()) {
            return navigationService.focusRedirect();
        }
        org.springframework.web.util.UriComponentsBuilder uri =
                org.springframework.web.util.UriComponentsBuilder.fromPath("/practice");
        if (target.weakTopics() != null) {
            uri.queryParam("weakTopics", target.weakTopics());
        }
        if (target.important() != null) {
            uri.queryParam("important", target.important());
        }
        if (target.onlyWrong() != null) {
            uri.queryParam("onlyWrong", target.onlyWrong());
        }
        if (target.topic() != null) {
            uri.queryParam("topic", target.topic());
        }
        if (target.group() != null) {
            uri.queryParam("group", target.group());
        }
        if (target.questionId() != null) {
            uri.queryParam("questionId", target.questionId());
        }
        if (target.ordered() != null) {
            uri.queryParam("ordered", target.ordered());
        }
        return "redirect:" + uri.build().encode().toUriString();
    }

    /** Цель browse-перехода после паузы (белый список параметров отбора). */
    public record BrowseTarget(
            Boolean weakTopics,
            Boolean important,
            Boolean onlyWrong,
            String topic,
            String group,
            Boolean ordered,
            Long questionId
    ) {
        public boolean isEmpty() {
            return weakTopics == null && important == null && onlyWrong == null
                    && topic == null && group == null && ordered == null && questionId == null;
        }
    }

    /**
     * Возобновляет приостановленную сессию (FLOW-01): восстанавливает её в
     * HTTP-сессию и удаляет из БД. Если паузы нет — просто редирект на фокус.
     */
    public String resume(HttpSession session) {
        pauseService.resume()
                .ifPresent(interviewSession -> {
                    // Возобновление — тоже начало нового захода: итоги прошлой
                    // сессии больше не актуальны. Чистим только при фактическом
                    // восстановлении: если паузы не было, стирать нечего и незачем.
                    httpSessionStateService.clearLastSessionSummary(session);
                    httpSessionStateService.setInterviewSession(session, interviewSession);
                });
        return navigationService.focusRedirect();
    }

    /**
     * Отмечает текущий вопрос как «не знаю» (UNKNOWN, FLOW-03) и показывает
     * разбор (UX-15): «не знаю» — момент максимальной потребности в объяснении,
     * и уводить сразу к следующему вопросу значило терять его. Персистенцию и
     * идемпотентность обеспечивает {@link InterviewSessionSupport#processSkip};
     * повторный POST безопасен — SM-2 не дублируется, разбор просто рендерится
     * снова (тот же replay-принцип, что у FLOW-02 на /answer).
     *
     * <p>Redirect остаётся в двух честных случаях: вне сессии (processSkip —
     * no-op, разбор не обещан) и у optionless-вопроса (флешкарт-ветка: ответ
     * уже на экране, {@code evaluateUnknown} пуст).</p>
     */
    public String skip(long questionId, HttpSession session, Model model) {
        InterviewSession interviewSession = sessionSupport.processSkip(questionId, session);
        if (interviewSession == null) {
            return navigationService.focusRedirect();
        }
        Integer blockIdx = interviewSession.mcqBlockIdxForQuestion(questionId);
        var breakdown = blockIdx == null ? interviewService.evaluateUnknown(questionId)
                : interviewService.evaluateUnknown(questionId, blockIdx);
        return breakdown.map(result -> {
                    AnswerPageService.AnswerPageState state = answerPageService.build(
                            result,
                            sessionSupport.sessionFilter(interviewSession),
                            interviewSession
                    );
                    modelAttributeMapper.applyAnswerPageState(model, state);
                    return navigationService.resultView();
                })
                .orElseGet(navigationService::focusRedirect);
    }

    /**
     * Итоги последней завершённой сессии.
     *
     * <p>Страница намеренно <b>не</b> одноразовая. Раньше summary стиралась
     * сразу после первого рендера, и любая перезагрузка, «назад» из закладок
     * или случайный повторный переход выбрасывали на экран фокуса — результат
     * только что законченной сессии исчезал безвозвратно, хотя пользователь
     * ничего не сделал. Это тупиковое состояние на основном сценарии: разбор
     * итогов — как раз то, к чему возвращаются.</p>
     *
     * <p>Теперь summary живёт до следующего осмысленного события: старта новой
     * сессии ({@link #applyStartSessionResult}) или возобновления
     * приостановленной ({@link #resume}). Тогда прежние итоги действительно
     * устарели, и показывать их было бы враньём.</p>
     */
    public String sessionSummary(HttpSession session, Model model) {
        SessionSummary summary = httpSessionStateService.getLastSessionSummary(session).orElse(null);
        if (summary == null) {
            return navigationService.focusRedirect();
        }
        modelAttributeMapper.applySessionSummary(model, summary);
        return navigationService.sessionSummaryView();
    }

    public String answer(SubmitAnswerRequest request, HttpSession session, Model model) {
        InterviewSessionSupport.AnswerContext ctx = processAnswer(request, session);
        AnswerPageService.AnswerPageState state = toAnswerPageState(ctx);
        modelAttributeMapper.applyAnswerPageState(model, state);

        return navigationService.resultView();
    }

    private void applyStartSessionResult(HttpSession session, SessionFlowService.StartFlowResult flowResult) {
        httpSessionStateService.clearRecallAnswer(session);
        // Новая сессия обесценивает итоги прежней: держать их дальше — значит
        // однажды показать на /session-summary результат позапрошлого захода.
        // Именно здесь, а не после первого рендера (см. javadoc sessionSummary).
        httpSessionStateService.clearLastSessionSummary(session);
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
                .ifPresent(summary -> {
                    httpSessionStateService.setLastSessionSummary(session, summary);
                    // История переживает HttpSession: до неё сводка существовала ровно до
                    // следующего старта, перезагрузки или закрытия вкладки, и «сколько я
                    // занимался на прошлой неделе» продукт не отвечал нигде.
                    sessionHistoryService.record(summary);
                });
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
