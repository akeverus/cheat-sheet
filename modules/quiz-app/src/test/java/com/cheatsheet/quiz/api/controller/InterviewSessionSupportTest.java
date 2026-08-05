package com.cheatsheet.quiz.api.controller;

import com.cheatsheet.quiz.domain.AnswerDisplayMode;
import com.cheatsheet.quiz.domain.AnswerOption;
import com.cheatsheet.quiz.domain.AnswerResult;
import com.cheatsheet.quiz.domain.AttemptOutcome;
import com.cheatsheet.quiz.domain.InterviewMode;
import com.cheatsheet.quiz.domain.InterviewSession;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.domain.ReviewResult;
import com.cheatsheet.quiz.domain.ReviewState;
import com.cheatsheet.quiz.feature.interview.controller.HttpSessionStateService;
import com.cheatsheet.quiz.feature.interview.controller.InterviewSessionSupport;
import com.cheatsheet.quiz.feature.interview.service.command.SubmitAnswerCommand;
import com.cheatsheet.quiz.feature.interview.service.core.InterviewService;
import com.cheatsheet.quiz.feature.interview.service.review.AttemptRecorder;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import jakarta.servlet.http.HttpSession;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.lenient;
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
    private AttemptRecorder attemptRecorder;
    @Mock
    private SubmitAnswerCommand submitAnswerCommand;
    @Mock
    private HttpSession session;
    @Mock
    private InterviewSession interviewSession;

    private InterviewSessionSupport support;

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-03-02T00:00:00Z"), ZoneOffset.UTC);

    @BeforeEach
    void setUp() {
        support = new InterviewSessionSupport(
                interviewService, httpSessionStateService, attemptRecorder, submitAnswerCommand,
                CLOCK, new SimpleMeterRegistry());
        lenient().when(interviewSession.takeResponseTimeMs(anyLong())).thenReturn(null);
        lenient().when(session.getId()).thenReturn("sess-test");
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
        when(submitAnswerCommand.executeOrNullIfDuplicate(any(), eq("sess-test"), isNull()))
                .thenReturn(answerResult);
        InterviewSessionSupport.AnswerSubmission submission = InterviewSessionSupport.AnswerSubmission.builder()
                .questionId(10L)
                .optionId(3L)
                .confidence(4)
                .clientAttemptId("ca-1")
                .build();

        InterviewSessionSupport.AnswerContext context = support.processAnswer(submission, session);

        assertThat(context.filter().topic()).isEqualTo("java");
        assertThat(context.filter().group()).isEqualTo("core");
        verify(interviewSession).registerAnswer(false, "java");
        verify(interviewService).addExamPenaltyQuestions(interviewSession);
        verify(httpSessionStateService).setInterviewSession(session, interviewSession);
        verify(submitAnswerCommand).executeOrNullIfDuplicate(any(), eq("sess-test"), isNull());
    }

    @Test
    void processAnswerRecordsMeasuredResponseTime() {
        AnswerResult answerResult = sampleAnswerResult(true);
        when(httpSessionStateService.getInterviewSession(session)).thenReturn(interviewSession);
        when(interviewSession.getTopic()).thenReturn("java");
        when(interviewSession.getGroup()).thenReturn("core");
        when(interviewSession.getImportantOnly()).thenReturn(false);
        when(interviewSession.getOnlyWrong()).thenReturn(false);
        when(interviewSession.getShuffle()).thenReturn(false);
        when(interviewSession.getOrdered()).thenReturn(true);
        when(interviewSession.isFinished()).thenReturn(false);
        when(interviewSession.currentQuestionId()).thenReturn(10L);
        when(interviewSession.getMode()).thenReturn(InterviewMode.MARATHON);
        when(interviewSession.takeResponseTimeMs(anyLong())).thenReturn(4200);
        when(submitAnswerCommand.executeOrNullIfDuplicate(any(), eq("sess-test"), eq(4200)))
                .thenReturn(answerResult);
        InterviewSessionSupport.AnswerSubmission submission = InterviewSessionSupport.AnswerSubmission.builder()
                .questionId(10L)
                .optionId(3L)
                .build();

        support.processAnswer(submission, session);

        verify(submitAnswerCommand).executeOrNullIfDuplicate(any(), eq("sess-test"), eq(4200));
    }

    @Test
    void markQuestionServedStampsClockAndPersists() {
        support.markQuestionServed(session, interviewSession);

        verify(interviewSession).markQuestionServed(CLOCK.millis());
        verify(httpSessionStateService).setInterviewSession(session, interviewSession);
    }

    @Test
    void markQuestionServedIsNoopWithoutSession() {
        support.markQuestionServed(session, null);

        verify(httpSessionStateService, never()).setInterviewSession(any(), any());
    }

    @Test
    void processAnswerUsesRequestFilterWhenSessionMissing() {
        AnswerResult answerResult = sampleAnswerResult(true);
        when(httpSessionStateService.getInterviewSession(session)).thenReturn(null);
        when(submitAnswerCommand.executeOrNullIfDuplicate(any(), eq("sess-test"), isNull()))
                .thenReturn(answerResult);
        InterviewSessionSupport.AnswerSubmission submission = InterviewSessionSupport.AnswerSubmission.builder()
                .questionId(20L)
                .optionId(5L)
                .topic("  java  ")
                .group("  backend ")
                .important(true)
                .onlyWrong(false)
                .shuffle(true)
                .ordered(true)
                .build();

        InterviewSessionSupport.AnswerContext context = support.processAnswer(submission, session);

        assertThat(context.filter().topic()).isEqualTo("java");
        assertThat(context.filter().group()).isEqualTo("backend");
        verify(submitAnswerCommand).executeOrNullIfDuplicate(any(), eq("sess-test"), isNull());
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
        when(submitAnswerCommand.executeOrNullIfDuplicate(any(), eq("sess-test"), isNull()))
                .thenReturn(answerResult);
        InterviewSessionSupport.AnswerSubmission submission = InterviewSessionSupport.AnswerSubmission.builder()
                .questionId(11L)
                .optionId(2L)
                .confidence(3)
                .build();

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
        when(submitAnswerCommand.executeOrNullIfDuplicate(any(), eq("sess-test"), isNull()))
                .thenReturn(answerResult);
        InterviewSessionSupport.AnswerSubmission submission = InterviewSessionSupport.AnswerSubmission.builder()
                .questionId(31L)
                .optionId(9L)
                .confidence(5)
                .build();

        support.processAnswer(submission, session);

        verify(interviewSession).registerAnswer(true, "java");
        verify(interviewSession).switchToLearnPhase();
        verify(interviewService, never()).addExamPenaltyQuestions(any());
        verify(httpSessionStateService).setInterviewSession(session, interviewSession);
    }

    @Test
    void processAnswerReplaysWhenIdempotencyClaimFails() {
        AnswerResult replay = sampleAnswerResult(true);
        when(httpSessionStateService.getInterviewSession(session)).thenReturn(interviewSession);
        when(interviewSession.isFinished()).thenReturn(false);
        when(interviewSession.currentQuestionId()).thenReturn(10L);
        when(submitAnswerCommand.executeOrNullIfDuplicate(any(), eq("sess-test"), isNull()))
                .thenReturn(null);
        when(interviewService.evaluateAnswer(10L, 3L)).thenReturn(replay);
        InterviewSessionSupport.AnswerSubmission submission = InterviewSessionSupport.AnswerSubmission.builder()
                .questionId(10L)
                .optionId(3L)
                .confidence(4)
                .clientAttemptId("dup-key")
                .build();

        InterviewSessionSupport.AnswerContext context = support.processAnswer(submission, session);

        assertThat(context.result()).isSameAs(replay);
        verify(interviewService).evaluateAnswer(10L, 3L);
        verify(interviewSession, never()).registerAnswer(anyBoolean(), anyString());
    }

    @Test
    void processAnswerReplaysWithoutSideEffectsOnStaleDuplicate() {
        AnswerResult replay = sampleAnswerResult(true);
        when(httpSessionStateService.getInterviewSession(session)).thenReturn(interviewSession);
        when(interviewSession.isFinished()).thenReturn(false);
        when(interviewSession.currentQuestionId()).thenReturn(99L);
        when(interviewService.evaluateAnswer(10L, 3L)).thenReturn(replay);
        InterviewSessionSupport.AnswerSubmission submission = InterviewSessionSupport.AnswerSubmission.builder()
                .questionId(10L)
                .optionId(3L)
                .confidence(4)
                .build();

        InterviewSessionSupport.AnswerContext context = support.processAnswer(submission, session);

        assertThat(context.result()).isSameAs(replay);
        assertThat(context.interviewSession()).isSameAs(interviewSession);
        verify(interviewService).evaluateAnswer(10L, 3L);
        verify(submitAnswerCommand, never()).executeOrNullIfDuplicate(any(), any(), any());
        verify(interviewSession, never()).registerAnswer(anyBoolean(), anyString());
        verify(interviewService, never()).addExamPenaltyQuestions(any());
        verify(httpSessionStateService, never()).setInterviewSession(any(), any());
    }

    @Test
    void processSkipAppliesUnknownAndExamPenaltyAndPersists() {
        Question question = sampleQuestion("java");
        when(httpSessionStateService.getInterviewSession(session)).thenReturn(interviewSession);
        when(interviewSession.isFinished()).thenReturn(false);
        when(interviewSession.currentQuestionId()).thenReturn(10L);
        when(interviewSession.getMode()).thenReturn(InterviewMode.EXAM);
        when(interviewService.submitUnknown(10L)).thenReturn(question);
        when(attemptRecorder.tryClaimIdempotencyKey(anyString())).thenReturn(true);
        when(session.getId()).thenReturn("sess-1");

        InterviewSession result = support.processSkip(10L, session);

        assertThat(result).isSameAs(interviewSession);
        verify(interviewService).submitUnknown(10L);
        verify(interviewSession).registerUnknown("java");
        verify(interviewService).addExamPenaltyQuestions(interviewSession);
        verify(httpSessionStateService).setInterviewSession(session, interviewSession);
        verify(attemptRecorder).record(eq(10L), eq(AttemptOutcome.UNKNOWN), eq(0), isNull(), isNull(), any(), anyString(), isNull(), isNull());
    }

    @Test
    void processSkipSwitchesStudySessionBackToLearnPhase() {
        Question question = sampleQuestion("java");
        when(httpSessionStateService.getInterviewSession(session)).thenReturn(interviewSession);
        when(interviewSession.isFinished()).thenReturn(false);
        when(interviewSession.currentQuestionId()).thenReturn(31L);
        when(interviewSession.getMode()).thenReturn(InterviewMode.STUDY);
        when(interviewService.submitUnknown(31L)).thenReturn(question);
        when(attemptRecorder.tryClaimIdempotencyKey(anyString())).thenReturn(true);
        when(session.getId()).thenReturn("sess-2");

        support.processSkip(31L, session);

        verify(interviewSession).registerUnknown("java");
        verify(interviewSession).switchToLearnPhase();
        verify(interviewService, never()).addExamPenaltyQuestions(any());
        verify(httpSessionStateService).setInterviewSession(session, interviewSession);
    }

    @Test
    void processSkipIsNoOpOnStaleDuplicate() {
        when(httpSessionStateService.getInterviewSession(session)).thenReturn(interviewSession);
        when(interviewSession.isFinished()).thenReturn(false);
        when(interviewSession.currentQuestionId()).thenReturn(99L);

        InterviewSession result = support.processSkip(10L, session);

        assertThat(result).isSameAs(interviewSession);
        verify(interviewService, never()).submitUnknown(anyLong());
        verify(interviewSession, never()).registerUnknown(anyString());
        verify(interviewService, never()).addExamPenaltyQuestions(any());
        verify(httpSessionStateService, never()).setInterviewSession(any(), any());
        verify(attemptRecorder, never()).record(anyLong(), any(), any(), any(), any(), any(), any(), any());
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
        return Question.builder()
                .id(1L)
                .slug("slug")
                .sourceSlug("slug")
                .filePath("file.md")
                .topic(topic)
                .questionText("Q?")
                .answerMarkdown("A")
                .important(false)
                .sourceHash("hash")
                .questionType(QuestionType.TEXT)
                .codeSnippet(null)
                .diagramMermaid(null)
                .takeaway(null)
                .build();
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
