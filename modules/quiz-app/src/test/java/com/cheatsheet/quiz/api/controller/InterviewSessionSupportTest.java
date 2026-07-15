package com.cheatsheet.quiz.api.controller;

import com.cheatsheet.quiz.domain.AnswerDisplayMode;
import com.cheatsheet.quiz.domain.AnswerOption;
import com.cheatsheet.quiz.domain.AnswerResult;
import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.domain.InterviewMode;
import com.cheatsheet.quiz.domain.InterviewSession;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.domain.ReviewResult;
import com.cheatsheet.quiz.domain.ReviewState;
import com.cheatsheet.quiz.feature.interview.controller.HttpSessionStateService;
import com.cheatsheet.quiz.feature.interview.controller.InterviewSessionSupport;
import com.cheatsheet.quiz.feature.interview.service.core.InterviewService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InterviewSessionSupportTest {

    @Mock
    private InterviewService interviewService;
    @Mock
    private HttpSessionStateService httpSessionStateService;
    @Mock
    private HttpSession session;
    @Mock
    private InterviewSession interviewSession;

    private InterviewSessionSupport support;

    @BeforeEach
    void setUp() {
        support = new InterviewSessionSupport(interviewService, httpSessionStateService);
    }

    @Test
    void processAnswerUsesSessionFilterAndAddsExamPenaltyOnWrongAnswer() {
        AnswerResult answerResult = sampleAnswerResult(false);
        when(httpSessionStateService.getInterviewSession(session)).thenReturn(interviewSession);
        when(interviewSession.getTopic()).thenReturn("java");
        when(interviewSession.getGroup()).thenReturn("core");
        when(interviewSession.getImportantOnly()).thenReturn(true);
        when(interviewSession.getOnlyWrong()).thenReturn(false);
        when(interviewSession.getShuffle()).thenReturn(true);
        when(interviewSession.getOrdered()).thenReturn(false);
        when(interviewSession.isFinished()).thenReturn(false);
        when(interviewSession.currentQuestionId()).thenReturn(10L);
        when(interviewSession.getMode()).thenReturn(InterviewMode.EXAM);
        when(interviewService.submitAnswer(eq(10L), eq(3L), any(InterviewFilter.class), eq(4))).thenReturn(answerResult);
        InterviewSessionSupport.AnswerSubmission submission = new InterviewSessionSupport.AnswerSubmission(
                10L, 3L, null, null, null, null, null, null, 4
        );

        InterviewSessionSupport.AnswerContext context = support.processAnswer(submission, session);

        assertThat(context.filter().topic()).isEqualTo("java");
        assertThat(context.filter().group()).isEqualTo("core");
        verify(interviewSession).registerAnswer(false, "java");
        verify(interviewService).addExamPenaltyQuestions(interviewSession);
        verify(httpSessionStateService).setInterviewSession(session, interviewSession);
    }

    @Test
    void processAnswerUsesRequestFilterWhenSessionMissing() {
        AnswerResult answerResult = sampleAnswerResult(true);
        when(httpSessionStateService.getInterviewSession(session)).thenReturn(null);
        when(interviewService.submitAnswer(eq(20L), eq(5L), any(InterviewFilter.class), eq(null))).thenReturn(answerResult);
        InterviewSessionSupport.AnswerSubmission submission = new InterviewSessionSupport.AnswerSubmission(
                20L,
                5L,
                "  java  ",
                "  backend ",
                true,
                false,
                true,
                true,
                null
        );

        support.processAnswer(submission, session);

        ArgumentCaptor<InterviewFilter> filterCaptor = ArgumentCaptor.forClass(InterviewFilter.class);
        verify(interviewService).submitAnswer(eq(20L), eq(5L), filterCaptor.capture(), eq(null));
        InterviewFilter filter = filterCaptor.getValue();
        assertThat(filter.topic()).isEqualTo("java");
        assertThat(filter.group()).isEqualTo("backend");
        verify(httpSessionStateService, never()).setInterviewSession(any(), any());
    }

    @Test
    void processAnswerDoesNotMutateOrPersistWhenSessionAlreadyFinished() {
        AnswerResult answerResult = sampleAnswerResult(true);
        when(httpSessionStateService.getInterviewSession(session)).thenReturn(interviewSession);
        when(interviewSession.getTopic()).thenReturn("java");
        when(interviewSession.getGroup()).thenReturn("core");
        when(interviewSession.getImportantOnly()).thenReturn(false);
        when(interviewSession.getOnlyWrong()).thenReturn(false);
        when(interviewSession.getShuffle()).thenReturn(true);
        when(interviewSession.getOrdered()).thenReturn(false);
        when(interviewSession.isFinished()).thenReturn(true);
        when(interviewService.submitAnswer(eq(11L), eq(2L), any(InterviewFilter.class), eq(3))).thenReturn(answerResult);
        InterviewSessionSupport.AnswerSubmission submission = new InterviewSessionSupport.AnswerSubmission(
                11L, 2L, null, null, null, null, null, null, 3
        );

        support.processAnswer(submission, session);

        verify(interviewSession, never()).registerAnswer(anyBoolean(), anyString());
        verify(interviewService, never()).addExamPenaltyQuestions(any());
        verify(httpSessionStateService, never()).setInterviewSession(any(), any());
    }

    @Test
    void processAnswerSwitchesStudySessionBackToLearnPhase() {
        AnswerResult answerResult = sampleAnswerResult(true);
        when(httpSessionStateService.getInterviewSession(session)).thenReturn(interviewSession);
        when(interviewSession.getTopic()).thenReturn("java");
        when(interviewSession.getGroup()).thenReturn("core");
        when(interviewSession.getImportantOnly()).thenReturn(false);
        when(interviewSession.getOnlyWrong()).thenReturn(false);
        when(interviewSession.getShuffle()).thenReturn(true);
        when(interviewSession.getOrdered()).thenReturn(false);
        when(interviewSession.isFinished()).thenReturn(false);
        when(interviewSession.currentQuestionId()).thenReturn(31L);
        when(interviewSession.getMode()).thenReturn(InterviewMode.STUDY);
        when(interviewService.submitAnswer(eq(31L), eq(9L), any(InterviewFilter.class), eq(5))).thenReturn(answerResult);
        InterviewSessionSupport.AnswerSubmission submission = new InterviewSessionSupport.AnswerSubmission(
                31L, 9L, null, null, null, null, null, null, 5
        );

        support.processAnswer(submission, session);

        verify(interviewSession).registerAnswer(true, "java");
        verify(interviewSession).switchToLearnPhase();
        verify(interviewService, never()).addExamPenaltyQuestions(any());
        verify(httpSessionStateService).setInterviewSession(session, interviewSession);
    }

    @Test
    void processAnswerReplaysWithoutSideEffectsOnStaleDuplicate() {
        // FLOW-02: устаревший дубль — сессия ушла вперёд (currentQuestionId != submission.questionId).
        // Ожидаем read-only повтор через evaluateAnswer, БЕЗ записи SM-2 и БЕЗ продвижения сессии.
        AnswerResult replay = sampleAnswerResult(true);
        when(httpSessionStateService.getInterviewSession(session)).thenReturn(interviewSession);
        when(interviewSession.isFinished()).thenReturn(false);
        when(interviewSession.currentQuestionId()).thenReturn(99L);
        when(interviewService.evaluateAnswer(10L, 3L)).thenReturn(replay);
        InterviewSessionSupport.AnswerSubmission submission = new InterviewSessionSupport.AnswerSubmission(
                10L, 3L, null, null, null, null, null, null, 4
        );

        InterviewSessionSupport.AnswerContext context = support.processAnswer(submission, session);

        assertThat(context.result()).isSameAs(replay);
        assertThat(context.interviewSession()).isSameAs(interviewSession);
        verify(interviewService).evaluateAnswer(10L, 3L);
        verify(interviewService, never()).submitAnswer(anyLong(), anyLong(), any(InterviewFilter.class), any());
        verify(interviewSession, never()).registerAnswer(anyBoolean(), anyString());
        verify(interviewService, never()).addExamPenaltyQuestions(any());
        verify(httpSessionStateService, never()).setInterviewSession(any(), any());
    }

    @Test
    void processSkipAppliesUnknownAndExamPenaltyAndPersists() {
        // FLOW-03: «не знаю» на текущем вопросе EXAM → SM-2-лапс + штрафные вопросы + persist.
        Question question = sampleQuestion("java");
        when(httpSessionStateService.getInterviewSession(session)).thenReturn(interviewSession);
        when(interviewSession.isFinished()).thenReturn(false);
        when(interviewSession.currentQuestionId()).thenReturn(10L);
        when(interviewSession.getMode()).thenReturn(InterviewMode.EXAM);
        when(interviewService.submitUnknown(10L)).thenReturn(question);

        InterviewSession result = support.processSkip(10L, session);

        assertThat(result).isSameAs(interviewSession);
        verify(interviewService).submitUnknown(10L);
        verify(interviewSession).registerUnknown("java");
        verify(interviewService).addExamPenaltyQuestions(interviewSession);
        verify(httpSessionStateService).setInterviewSession(session, interviewSession);
    }

    @Test
    void processSkipSwitchesStudySessionBackToLearnPhase() {
        Question question = sampleQuestion("java");
        when(httpSessionStateService.getInterviewSession(session)).thenReturn(interviewSession);
        when(interviewSession.isFinished()).thenReturn(false);
        when(interviewSession.currentQuestionId()).thenReturn(31L);
        when(interviewSession.getMode()).thenReturn(InterviewMode.STUDY);
        when(interviewService.submitUnknown(31L)).thenReturn(question);

        support.processSkip(31L, session);

        verify(interviewSession).registerUnknown("java");
        verify(interviewSession).switchToLearnPhase();
        verify(interviewService, never()).addExamPenaltyQuestions(any());
        verify(httpSessionStateService).setInterviewSession(session, interviewSession);
    }

    @Test
    void processSkipIsNoOpOnStaleDuplicate() {
        // Текущий вопрос сессии уже не тот, что пришёл в skip → без побочных эффектов.
        when(httpSessionStateService.getInterviewSession(session)).thenReturn(interviewSession);
        when(interviewSession.isFinished()).thenReturn(false);
        when(interviewSession.currentQuestionId()).thenReturn(99L);

        InterviewSession result = support.processSkip(10L, session);

        assertThat(result).isSameAs(interviewSession);
        verify(interviewService, never()).submitUnknown(anyLong());
        verify(interviewSession, never()).registerUnknown(anyString());
        verify(interviewService, never()).addExamPenaltyQuestions(any());
        verify(httpSessionStateService, never()).setInterviewSession(any(), any());
    }

    @Test
    void processSkipIsNoOpWhenSessionFinishedOrMissing() {
        when(httpSessionStateService.getInterviewSession(session)).thenReturn(interviewSession);
        when(interviewSession.isFinished()).thenReturn(true);

        support.processSkip(10L, session);

        verify(interviewService, never()).submitUnknown(anyLong());
        verify(interviewSession, never()).registerUnknown(anyString());
        verify(httpSessionStateService, never()).setInterviewSession(any(), any());
    }

    private Question sampleQuestion(String topic) {
        return new Question(
                1L,
                "slug",
                "slug",
                "file.md",
                topic,
                "Q?",
                "A",
                false,
                "hash",
                QuestionType.TEXT,
                null,
                null,
                0,
                null
        );
    }

    private AnswerResult sampleAnswerResult(boolean correct) {
        Question question = sampleQuestion("java");
        AnswerOption selected = new AnswerOption(10L, 1L, "selected", correct, 0, "OPENAI", null);
        AnswerOption correctOption = new AnswerOption(11L, 1L, "correct", true, 1, "OPENAI", null);
        ReviewState state = new ReviewState(1L, 1, 1, 2.5, 1_700_000_000L, ReviewResult.CORRECT, 1, 0);
        return new AnswerResult(
                question,
                List.of(selected, correctOption),
                selected,
                correctOption,
                correct,
                state,
                AnswerDisplayMode.MINIMAL
        );
    }
}
