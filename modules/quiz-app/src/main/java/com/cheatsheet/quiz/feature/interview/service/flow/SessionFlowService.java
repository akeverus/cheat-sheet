package com.cheatsheet.quiz.feature.interview.service.flow;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import com.cheatsheet.quiz.config.app.AppProperties;
import com.cheatsheet.quiz.domain.FlashcardGrade;
import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.domain.InterviewMode;
import com.cheatsheet.quiz.domain.InterviewSession;
import com.cheatsheet.quiz.domain.SessionSummary;
import com.cheatsheet.quiz.domain.StudyPhase;
import com.cheatsheet.quiz.feature.interview.service.facade.InterviewFacade;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import lombok.Builder;

import java.util.Optional;

/**
 * Сервис управления сценариями жизненного цикла учебной сессии.
 *
 * <p>Инкапсулирует flow-логику, чтобы MVC-контроллер выполнял только транспортные задачи.</p>
 */
@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SessionFlowService {
    InterviewFacade facade;
    SessionSummaryService sessionSummaryService;
    AppProperties appProperties;

    /**
     * Вычисляет результат старта сессии.
     */
    public StartFlowResult startSession(InterviewMode selected, Integer requestedCount, InterviewFilter filter) {
        if (selected != null && selected.isUnlimited()) {
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
     *
     * <p>Самооценка ЗАПИСЫВАЕТСЯ В СЕССИЮ, а не только уходит в планировщик. До
     * iter-130 здесь стоял {@code advanceFlashcard()} — он двигал индекс и всё:
     * ни {@code correct}/{@code wrong}, ни истории ответов. Замер на живом
     * приложении (код-челлендж, три отвеченных вопроса, оценки «отлично» и «не
     * помню»): рельс показывал «3/20» и ноль ошибок, а итог сессии — «Сессия
     * завершена без ответов. Засчитывать нечего: ни опыта, ни ошибок для разбора,
     * ни данных по темам». То есть у ЧЕТЫРЁХ режимов из восьми (флешкарта,
     * печатный пересказ, интервью, код-челлендж) экран итогов был пуст всегда,
     * сколько бы вопросов человек ни прошёл.
     *
     * <p>Верность берётся из той же таблицы, что и SM-2-оценка
     * ({@link FlashcardGrade#isCorrect(int)}), — второй трактовки оценки в
     * приложении не заводим. Тема нужна разбору по темам в итогах и журналу хода
     * сессии; вопрос уже загружен планировщиком, поэтому запрос здесь один и
     * только на действие пользователя.
     */
    public boolean applyFlashcardGrade(InterviewSession interviewSession, long questionId, int grade) {
        if (interviewSession == null) return false;
        // Два одновременно пришедших POST должны проверить фазу и изменить индекс атомарно.
        synchronized (interviewSession) {
            if (!canApplyFlashcardGrade(interviewSession)
                    || !Long.valueOf(questionId).equals(interviewSession.currentQuestionId())) {
                return false;
            }
            submitFlashcardGrade(questionId, grade);
            interviewSession.registerAnswer(FlashcardGrade.isCorrect(grade), topicOf(questionId));
            interviewSession.resetFlashcardPhase();
            return true;
        }
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

    @Builder(toBuilder = true)
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
        return interviewSession.isFlashcardMode()
                && !interviewSession.isFinished()
                && interviewSession.getFlashcardPhase() == com.cheatsheet.quiz.domain.FlashcardPhase.REVEALED;
    }

    private void submitFlashcardGrade(long questionId, int grade) {
        facade.submitFlashcardGrade(questionId, FlashcardGrade.isCorrect(grade), FlashcardGrade.toSm2Grade(grade));
    }

    /**
     * Тема вопроса для записи в сессию. Не нашли вопрос — пишем {@code null}: запись
     * об ответе всё равно нужна счётчикам и журналу, а разбор по темам просто не
     * увидит эту строку. Терять ответ из-за пропавшей темы нельзя.
     */
    private String topicOf(long questionId) {
        return facade.questionById(questionId)
                .map(question -> question.question().topic())
                .orElse(null);
    }
}
