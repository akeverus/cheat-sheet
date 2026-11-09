package com.cheatsheet.quiz.feature.interview.service.command;

import com.cheatsheet.quiz.TestQuestionBuilder;
import com.cheatsheet.quiz.domain.AnswerDisplayMode;
import com.cheatsheet.quiz.domain.AnswerOption;
import com.cheatsheet.quiz.domain.AnswerResult;
import com.cheatsheet.quiz.domain.Attempt;
import com.cheatsheet.quiz.domain.AttemptOutcome;
import com.cheatsheet.quiz.domain.QuestionRevision;
import com.cheatsheet.quiz.domain.ReviewResult;
import com.cheatsheet.quiz.domain.ReviewState;
import com.cheatsheet.quiz.feature.interview.controller.InterviewSessionSupport.AnswerSubmission;
import com.cheatsheet.quiz.feature.interview.service.core.InterviewService;
import com.cheatsheet.quiz.feature.interview.service.review.AttemptRecorder;
import com.cheatsheet.quiz.feature.interview.service.review.RevisionMismatchException;
import com.cheatsheet.quiz.persistence.AttemptRepository;
import com.cheatsheet.quiz.persistence.QuestionRevisionRepository;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubmitAnswerCommandTest {
    @Mock InterviewService interviewService;
    @Mock AttemptRepository attemptRepository;
    @Mock QuestionRevisionRepository revisions;

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-11-08T12:00:00Z"), ZoneOffset.UTC);
    private SubmitAnswerCommand command;

    @BeforeEach
    void setUp() {
        AttemptRecorder recorder = new AttemptRecorder(attemptRepository, revisions, CLOCK);
        command = new SubmitAnswerCommand(interviewService, recorder, new SimpleMeterRegistry());
    }

    @ParameterizedTest
    @ValueSource(strings = {"stale", "missing-latest", "wrong-question", "unknown", "deleted"})
    void firstSubmitRejectsAnUnconfirmedRevisionBeforeGradingOrRecording(String revisionState) {
        long clientRevisionId = revisionState.equals("unknown") ? 999L : 55L;
        QuestionRevision shown = revision(55L, revisionState.equals("wrong-question") ? 8L : 7L);
        when(attemptRepository.tryClaimIdempotencyKey(eq("first-submit"), anyLong())).thenReturn(true);
        // Keep both repository views explicit: the old revision can still exist
        // and belong to the question even though latest has already changed.
        lenient().when(revisions.findById(clientRevisionId)).thenReturn(
                revisionState.equals("unknown") || revisionState.equals("deleted")
                        ? Optional.empty() : Optional.of(shown));
        lenient().when(revisions.findLatest(7L)).thenReturn(revisionState.equals("missing-latest")
                ? Optional.empty() : Optional.of(revision(56L, 7L)));
        lenient().when(interviewService.submitAnswer(7L, 42L, null)).thenReturn(currentResult(56L));

        for (int retry = 0; retry < 2; retry++) {
            assertThatThrownBy(() -> command.executeOrNullIfDuplicate(submission(clientRevisionId), "session-1", null))
                    .isInstanceOf(RevisionMismatchException.class);
        }

        verify(interviewService, never()).submitAnswer(anyLong(), anyLong(), any());
        verify(attemptRepository, never()).insert(any());
        verify(attemptRepository, never()).insertIgnoreDuplicate(any());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {55L})
    void currentOrOmittedRevisionRecordsOnceAndDuplicateDoesNotGradeAgain(Long clientRevisionId) {
        QuestionRevision current = revision(55L, 7L);
        when(attemptRepository.tryClaimIdempotencyKey(eq("first-submit"), anyLong())).thenReturn(true, false);
        lenient().when(revisions.findLatest(7L)).thenReturn(Optional.of(current));
        if (clientRevisionId != null) when(revisions.findById(clientRevisionId)).thenReturn(Optional.of(current));
        AnswerResult accepted = currentResult(55L);
        when(interviewService.submitAnswer(7L, 42L, null)).thenReturn(accepted);

        assertThat(command.executeOrNullIfDuplicate(submission(clientRevisionId), "session-1", null)).isSameAs(accepted);
        assertThat(command.executeOrNullIfDuplicate(submission(clientRevisionId), "session-1", null)).isNull();

        ArgumentCaptor<Attempt> row = ArgumentCaptor.forClass(Attempt.class);
        verify(attemptRepository).insertIgnoreDuplicate(row.capture());
        assertThat(row.getValue().questionRevisionId()).isEqualTo(55L);
        assertThat(row.getValue().selectedOptionId()).isEqualTo(42L);
        assertThat(row.getValue().outcome()).isEqualTo(AttemptOutcome.CORRECT);
        verify(interviewService).submitAnswer(7L, 42L, null);
    }

    @Test
    void rejectedStaleSubmitsRollBackAndARefreshedRevisionCommitsOneAttempt() {
        when(attemptRepository.tryClaimIdempotencyKey(eq("first-submit"), anyLong())).thenReturn(true);
        lenient().when(revisions.findById(55L)).thenReturn(Optional.of(revision(55L, 7L)));
        lenient().when(revisions.findLatest(7L)).thenReturn(Optional.of(revision(56L, 7L)));
        lenient().when(revisions.findById(56L)).thenReturn(Optional.of(revision(56L, 7L)));
        when(interviewService.submitAnswer(7L, 42L, null)).thenReturn(currentResult(56L));
        PlatformTransactionManager transactions = mock(PlatformTransactionManager.class);
        TransactionStatus first = mock(TransactionStatus.class);
        TransactionStatus second = mock(TransactionStatus.class);
        TransactionStatus refreshed = mock(TransactionStatus.class);
        when(transactions.getTransaction(any())).thenReturn(first, second, refreshed);
        TransactionInterceptor advice = new TransactionInterceptor();
        advice.setTransactionManager(transactions);
        advice.setTransactionAttributeSource(new AnnotationTransactionAttributeSource());
        ProxyFactory factory = new ProxyFactory(command);
        factory.setProxyTargetClass(true);
        factory.addAdvice(advice);
        SubmitAnswerCommand transactional = (SubmitAnswerCommand) factory.getProxy();

        assertThatThrownBy(() -> transactional.executeOrNullIfDuplicate(submission(55L), "session-1", null))
                .isInstanceOf(RevisionMismatchException.class);
        assertThatThrownBy(() -> transactional.executeOrNullIfDuplicate(submission(55L), "session-1", null))
                .isInstanceOf(RevisionMismatchException.class);
        assertThat(transactional.executeOrNullIfDuplicate(submission(56L), "session-1", null)).isNotNull();

        verify(transactions).rollback(first);
        verify(transactions).rollback(second);
        verify(transactions).commit(refreshed);
        verify(transactions, times(1)).commit(any());
        verify(interviewService).submitAnswer(7L, 42L, null);
        ArgumentCaptor<Attempt> row = ArgumentCaptor.forClass(Attempt.class);
        verify(attemptRepository).insertIgnoreDuplicate(row.capture());
        assertThat(row.getValue().questionRevisionId()).isEqualTo(56L);
    }

    private AnswerSubmission submission(Long revisionId) {
        return AnswerSubmission.builder().questionId(7L).optionId(42L)
                .questionRevisionId(revisionId).clientAttemptId("first-submit").build();
    }

    private QuestionRevision revision(long id, long questionId) {
        return new QuestionRevision(id, questionId, id == 55L ? 1 : 2, "hash-" + id, "PUBLISHED", 100L);
    }

    private AnswerResult currentResult(long revisionId) {
        AnswerOption selected = new AnswerOption(42L, 7L, "Current answer", true, 0, "OPENAI", null);
        ReviewState state = new ReviewState(7L, 1, 1, 2.5, 100L, ReviewResult.CORRECT, 1, 0);
        return new AnswerResult(TestQuestionBuilder.aQuestion().withId(7L).withSourceHash("hash-" + revisionId).build(),
                List.of(selected), selected, selected, true, state, AnswerDisplayMode.FULL);
    }
}
