package com.cheatsheet.quiz.api.controller;

import com.cheatsheet.quiz.domain.AnswerDisplayMode;
import com.cheatsheet.quiz.domain.AnswerOption;
import com.cheatsheet.quiz.domain.AnswerResult;
import com.cheatsheet.quiz.domain.Attempt;
import com.cheatsheet.quiz.domain.AttemptOutcome;
import com.cheatsheet.quiz.domain.InterviewMode;
import com.cheatsheet.quiz.domain.InterviewSession;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.domain.QuestionRevision;
import com.cheatsheet.quiz.domain.exception.OptionNotFoundException;
import com.cheatsheet.quiz.domain.exception.QuestionNotFoundException;
import com.cheatsheet.quiz.domain.ReviewResult;
import com.cheatsheet.quiz.domain.ReviewState;
import com.cheatsheet.quiz.feature.interview.controller.HttpSessionStateService;
import com.cheatsheet.quiz.feature.interview.controller.InterviewSessionSupport;
import com.cheatsheet.quiz.feature.interview.service.command.SubmitAnswerCommand;
import com.cheatsheet.quiz.feature.interview.service.core.InterviewService;
import com.cheatsheet.quiz.feature.interview.service.review.AttemptRecorder;
import com.cheatsheet.quiz.feature.interview.service.review.RevisionMismatchException;
import com.cheatsheet.quiz.persistence.AttemptRepository;
import com.cheatsheet.quiz.persistence.QuestionRevisionRepository;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import jakarta.servlet.http.HttpSession;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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
        when(attemptRecorder.findByClientAttemptId("dup-key")).thenReturn(Optional.of(
                Attempt.builder().questionId(10L).selectedOptionId(3L).outcome(AttemptOutcome.CORRECT)
                        .sessionToken("sess-test").questionRevisionId(55L).idempotencyKey("dup-key").build()));
        when(attemptRecorder.requireCurrentRevision(10L, 55L))
                .thenReturn(new QuestionRevision(55L, 10L, 1, "hash", "PUBLISHED", 100L));
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
        when(attemptRecorder.findByClientAttemptId("stale-key")).thenReturn(Optional.of(
                Attempt.builder().questionId(10L).selectedOptionId(3L).outcome(AttemptOutcome.CORRECT)
                        .sessionToken("sess-test").questionRevisionId(55L).idempotencyKey("stale-key").build()));
        when(attemptRecorder.requireCurrentRevision(10L, 55L))
                .thenReturn(new QuestionRevision(55L, 10L, 1, "hash", "PUBLISHED", 100L));
        InterviewSessionSupport.AnswerSubmission submission = InterviewSessionSupport.AnswerSubmission.builder()
                .questionId(10L)
                .optionId(3L)
                .confidence(4)
                .clientAttemptId("stale-key")
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

    @ParameterizedTest(name = "lost response: advanced session={0}, changed selection={1}")
    @CsvSource({"false, false", "false, true", "true, false", "true, true"})
    void lostResponseRetryKeepsTheRecordedOptionAndOutcome(boolean sessionAdvanced, boolean changeSelection) {
        String key = "lost-response";
        AnswerOption original = new AnswerOption(3L, 1L, "original wrong choice", false, 0, "OPENAI", null);
        AnswerOption correct = new AnswerOption(4L, 1L, "correct choice", true, 1, "OPENAI", null);
        AnswerResult wrongResult = sampleAnswerResult(false).toBuilder()
                .options(List.of(original, correct)).selected(original).correct(correct).build();
        AnswerResult correctResult = wrongResult.toBuilder().selected(correct).correctAnswer(true).build();

        // Keep the real command and recorder: only persistence and evaluation
        // are mocked. The captured journal row is the first accepted attempt.
        AttemptRepository repository = mock(AttemptRepository.class);
        AtomicReference<Attempt> recorded = new AtomicReference<>();
        when(repository.tryClaimIdempotencyKey(eq(key), anyLong())).thenReturn(true, false);
        when(repository.findByIdempotencyKey(key)).thenAnswer(invocation -> Optional.ofNullable(recorded.get()));
        when(repository.insertIgnoreDuplicate(any())).thenAnswer(invocation -> {
            recorded.set(invocation.getArgument(0));
            return Optional.of(501L);
        });
        QuestionRevisionRepository revisions = mock(QuestionRevisionRepository.class);
        when(revisions.findLatest(1L)).thenReturn(Optional.of(
                new QuestionRevision(55L, 1L, 1, "hash", "PUBLISHED", 100L)));
        AttemptRecorder recorder = new AttemptRecorder(repository, revisions, CLOCK);
        SimpleMeterRegistry metrics = new SimpleMeterRegistry();
        SubmitAnswerCommand command = new SubmitAnswerCommand(interviewService, recorder, metrics);
        InterviewSessionSupport realSupport = new InterviewSessionSupport(
                interviewService, httpSessionStateService, recorder, command, CLOCK, metrics);
        when(interviewService.submitAnswer(1L, 3L, 4)).thenReturn(wrongResult);
        when(interviewService.evaluateAnswer(eq(1L), anyLong())).thenAnswer(invocation ->
                invocation.<Long>getArgument(1) == 3L ? wrongResult : correctResult);
        if (sessionAdvanced) {
            when(httpSessionStateService.getInterviewSession(session)).thenReturn(interviewSession);
            when(interviewSession.currentQuestionId()).thenReturn(1L, 99L);
            when(interviewSession.getMode()).thenReturn(InterviewMode.MARATHON);
        }
        InterviewSessionSupport.AnswerSubmission first = InterviewSessionSupport.AnswerSubmission.builder()
                .questionId(1L).optionId(3L).confidence(4).clientAttemptId(key).build();

        InterviewSessionSupport.AnswerContext accepted = realSupport.processAnswer(first, session);
        Attempt committed = recorded.get();
        assertThat(accepted.result().correctAnswer()).isFalse();
        assertThat(committed.selectedOptionId()).isEqualTo(3L);
        assertThat(committed.outcome()).isEqualTo(AttemptOutcome.WRONG);
        assertThat(committed.idempotencyKey()).isEqualTo(key);
        assertThat(committed.sessionToken()).isEqualTo("sess-test");
        assertThat(committed.questionRevisionId()).isEqualTo(55L);

        // The client lost the first HTTP response and may have edited its radio.
        InterviewSessionSupport.AnswerContext replay = realSupport.processAnswer(
                first.toBuilder().optionId(changeSelection ? 4L : 3L).build(), session);

        assertThat(recorded.get()).isSameAs(committed);
        assertThat(replay.result().selected().id()).isEqualTo(committed.selectedOptionId());
        assertThat(replay.result().correctAnswer()).isFalse();
        verify(repository).insertIgnoreDuplicate(any());
        verify(interviewService).submitAnswer(1L, 3L, 4);
        verify(interviewService, never()).submitAnswer(1L, 4L, 4);
        verify(interviewService).evaluateAnswer(1L, 3L);
        verify(interviewService, never()).evaluateAnswer(1L, 4L);
        verify(interviewSession, times(sessionAdvanced ? 1 : 0)).registerAnswer(false, "java");
        verify(httpSessionStateService, times(sessionAdvanced ? 1 : 0)).setInterviewSession(session, interviewSession);
    }

    @ParameterizedTest(name = "replay ownership: advanced session={0}, different HTTP session={1}")
    @CsvSource({"false, true", "false, false", "true, true", "true, false"})
    void duplicateKeyFromAnotherSessionOrQuestionIsRejected(boolean sessionAdvanced, boolean differentSession) {
        Attempt recorded = Attempt.builder()
                .questionId(differentSession ? 10L : 20L)
                .sessionToken(differentSession ? "other-http-session" : "sess-test")
                .selectedOptionId(3L).outcome(AttemptOutcome.WRONG).idempotencyKey("shared-key").build();
        when(httpSessionStateService.getInterviewSession(session)).thenReturn(interviewSession);
        when(interviewSession.currentQuestionId()).thenReturn(sessionAdvanced ? 99L : 10L);
        when(attemptRecorder.findByClientAttemptId("shared-key")).thenReturn(Optional.of(recorded));
        // The previous implementation returned a result instead of a conflict.
        lenient().when(interviewService.evaluateAnswer(anyLong(), anyLong())).thenReturn(sampleAnswerResult(true));
        InterviewSessionSupport.AnswerSubmission submission = InterviewSessionSupport.AnswerSubmission.builder()
                .questionId(10L).optionId(4L).clientAttemptId("shared-key").build();

        assertThatThrownBy(() -> support.processAnswer(submission, session))
                .isInstanceOf(IllegalStateException.class);

        verify(interviewService, never()).evaluateAnswer(anyLong(), anyLong());
        verify(interviewService, never()).submitAnswer(anyLong(), anyLong(), any());
        verify(interviewSession, never()).registerAnswer(anyBoolean(), anyString());
        verify(httpSessionStateService, never()).setInterviewSession(any(), any());
        verify(submitAnswerCommand, times(sessionAdvanced ? 0 : 1)).executeOrNullIfDuplicate(any(), any(), any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"missing", "no-option", "unknown", "no-session"})
    void duplicateKeyWithoutARecordedMcqAnswerIsRejected(String missingProof) {
        Attempt recorded = Attempt.builder().questionId(10L).idempotencyKey("shared-key")
                .sessionToken(missingProof.equals("no-session") ? null : "sess-test")
                .selectedOptionId(missingProof.equals("no-option") ? null : 3L)
                .outcome(missingProof.equals("unknown") ? AttemptOutcome.UNKNOWN : AttemptOutcome.WRONG).build();
        when(attemptRecorder.findByClientAttemptId("shared-key"))
                .thenReturn(missingProof.equals("missing") ? Optional.empty() : Optional.of(recorded));
        lenient().when(interviewService.evaluateAnswer(anyLong(), anyLong())).thenReturn(sampleAnswerResult(true));
        InterviewSessionSupport.AnswerSubmission submission = InterviewSessionSupport.AnswerSubmission.builder()
                .questionId(10L).optionId(4L).clientAttemptId("shared-key").build();

        assertThatThrownBy(() -> support.processAnswer(submission, session))
                .isInstanceOf(IllegalStateException.class);

        verify(interviewService, never()).evaluateAnswer(anyLong(), anyLong());
        verify(interviewSession, never()).registerAnswer(anyBoolean(), anyString());
        verify(httpSessionStateService, never()).setInterviewSession(any(), any());
    }

    @Test
    void staleAnswerWithoutAClientAttemptIdIsRejected() {
        when(httpSessionStateService.getInterviewSession(session)).thenReturn(interviewSession);
        when(interviewSession.currentQuestionId()).thenReturn(99L);
        InterviewSessionSupport.AnswerSubmission submission = InterviewSessionSupport.AnswerSubmission.builder()
                .questionId(10L).optionId(4L).build();

        assertThatThrownBy(() -> support.processAnswer(submission, session))
                .isInstanceOf(IllegalStateException.class);

        verify(interviewService, never()).evaluateAnswer(anyLong(), anyLong());
        verify(submitAnswerCommand, never()).executeOrNullIfDuplicate(any(), any(), any());
        verify(interviewSession, never()).registerAnswer(anyBoolean(), anyString());
    }

    @ParameterizedTest(name = "content drift={0}, recorded correct={1}")
    @CsvSource({"changed-option, false", "changed-option, true", "deleted-option, false",
            "changed-revision, false", "deleted-revision, false", "missing-recorded-revision, false",
            "changed-question-hash, false", "deleted-question, false"})
    void replayRejectsContentDriftInsteadOfReevaluatingHistory(String drift, boolean recordedCorrect) {
        Attempt stored = Attempt.builder().questionId(10L).sessionToken("sess-test")
                .questionRevisionId(drift.equals("missing-recorded-revision") ? null : 55L)
                .selectedOptionId(3L).outcome(AttemptOutcome.ofCorrect(recordedCorrect))
                .idempotencyKey("content-drift").build();
        AttemptRepository repository = mock(AttemptRepository.class);
        when(repository.findByIdempotencyKey("content-drift")).thenReturn(Optional.of(stored));
        QuestionRevisionRepository revisions = mock(QuestionRevisionRepository.class);
        QuestionRevision current = new QuestionRevision(drift.equals("changed-revision") ? 56L : 55L,
                10L, 1, "hash", "PUBLISHED", 100L);
        lenient().when(revisions.findLatest(10L)).thenReturn(
                drift.equals("deleted-revision") ? Optional.empty() : Optional.of(current));
        AttemptRecorder recorder = new AttemptRecorder(repository, revisions, CLOCK);
        InterviewSessionSupport realSupport = new InterviewSessionSupport(
                interviewService, httpSessionStateService, recorder, submitAnswerCommand,
                CLOCK, new SimpleMeterRegistry());
        boolean currentCorrect = drift.equals("changed-option") ? !recordedCorrect : recordedCorrect;
        AnswerOption selected = new AnswerOption(3L, 10L, "current option", currentCorrect, 0, "OPENAI", null);
        AnswerOption alternative = new AnswerOption(4L, 10L, "alternative", !currentCorrect, 1, "OPENAI", null);
        AnswerResult evaluated = sampleAnswerResult(currentCorrect).toBuilder()
                .question(sampleQuestion("java").toBuilder().id(10L)
                        .sourceHash(drift.equals("changed-question-hash") ? "new-hash" : "hash").build())
                .options(List.of(selected, alternative)).selected(selected)
                .correct(currentCorrect ? selected : alternative).build();
        if (drift.equals("deleted-option")) {
            // The journal was read before the option disappeared during replay.
            lenient().when(interviewService.evaluateAnswer(10L, 3L))
                    .thenThrow(new OptionNotFoundException("Saved option was deleted"));
        } else if (drift.equals("deleted-question")) {
            lenient().when(interviewService.evaluateAnswer(10L, 3L))
                    .thenThrow(new QuestionNotFoundException("Saved question was deleted"));
        } else {
            lenient().when(interviewService.evaluateAnswer(10L, 3L)).thenReturn(evaluated);
        }
        InterviewSessionSupport.AnswerSubmission retry = InterviewSessionSupport.AnswerSubmission.builder()
                .questionId(10L).optionId(4L).clientAttemptId("content-drift").questionRevisionId(55L).build();

        assertThatThrownBy(() -> realSupport.processAnswer(retry, session))
                .isInstanceOf(RevisionMismatchException.class);

        verify(repository, never()).insert(any());
        verify(repository, never()).insertIgnoreDuplicate(any());
        verify(interviewService, never()).submitAnswer(anyLong(), anyLong(), any());
        verify(interviewSession, never()).registerAnswer(anyBoolean(), anyString());
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
        verify(interviewSession).registerUnknown(10L, "java");
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

        verify(interviewSession).registerUnknown(31L, "java");
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
        verify(interviewSession, never()).registerUnknown(anyLong(), anyString());
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
        verify(interviewSession, never()).registerUnknown(anyLong(), anyString());
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
