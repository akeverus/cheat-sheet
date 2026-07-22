package com.cheatsheet.quiz.feature.interview.service.command;

import com.cheatsheet.quiz.domain.AnswerResult;
import com.cheatsheet.quiz.domain.AttemptOutcome;
import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.feature.interview.controller.InterviewSessionSupport.AnswerSubmission;
import com.cheatsheet.quiz.feature.interview.service.core.InterviewService;
import com.cheatsheet.quiz.feature.interview.service.review.AttemptRecorder;
import com.cheatsheet.quiz.feature.interview.service.review.RevisionMismatchException;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Атомарный submit: claim → validate revision → SM-2 → journal в одной TX.
 * HttpSession progress остаётся снаружи (см. DECISIONS D-003).
 */
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubmitAnswerCommand {

    InterviewService interviewService;
    AttemptRecorder attemptRecorder;
    Timer submitTimer;
    Counter duplicateClaimCounter;
    Counter revisionMismatchCounter;
    Counter submitSuccessCounter;

    public SubmitAnswerCommand(InterviewService interviewService,
                               AttemptRecorder attemptRecorder,
                               MeterRegistry meterRegistry) {
        this.interviewService = interviewService;
        this.attemptRecorder = attemptRecorder;
        this.submitTimer = Timer.builder("attempt.submit.duration")
                .description("Wall time of atomic submit command (claim+SM-2+journal)")
                .register(meterRegistry);
        this.duplicateClaimCounter = Counter.builder("attempt.idempotency.claim.conflict")
                .description("Idempotency key already claimed (duplicate submit)")
                .register(meterRegistry);
        this.revisionMismatchCounter = Counter.builder("attempt.revision.mismatch")
                .description("Submit rejected due to questionRevisionId mismatch")
                .register(meterRegistry);
        this.submitSuccessCounter = Counter.builder("attempt.submit.success")
                .description("Successful atomic answer submits")
                .register(meterRegistry);
    }

    /**
     * @return результат SM-2, или {@code null} если ключ уже занят (replay без side effects)
     */
    @Transactional
    public AnswerResult executeOrNullIfDuplicate(
            AnswerSubmission submission,
            InterviewFilter filter,
            String sessionToken,
            Integer responseTimeMs
    ) {
        return submitTimer.record(() -> doExecute(submission, filter, sessionToken, responseTimeMs));
    }

    private AnswerResult doExecute(
            AnswerSubmission submission,
            InterviewFilter filter,
            String sessionToken,
            Integer responseTimeMs
    ) {
        if (!attemptRecorder.tryClaimIdempotencyKey(submission.clientAttemptId())) {
            duplicateClaimCounter.increment();
            return null;
        }
        try {
            attemptRecorder.requireValidRevision(submission.questionId(), submission.questionRevisionId());
        } catch (RevisionMismatchException ex) {
            revisionMismatchCounter.increment();
            throw ex;
        }

        AnswerResult result = interviewService.submitAnswer(
                submission.questionId(),
                submission.optionId(),
                filter,
                submission.confidence()
        );

        attemptRecorder.record(
                submission.questionId(),
                AttemptOutcome.ofCorrect(result.correctAnswer()),
                submission.confidence(),
                submission.optionId(),
                responseTimeMs,
                sessionToken,
                submission.clientAttemptId(),
                submission.openedContentBlockIds(),
                submission.questionRevisionId()
        );
        submitSuccessCounter.increment();
        return result;
    }
}
