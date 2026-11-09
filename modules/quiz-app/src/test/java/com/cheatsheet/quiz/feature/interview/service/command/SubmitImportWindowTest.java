package com.cheatsheet.quiz.feature.interview.service.command;

import com.cheatsheet.quiz.TestQuestionBuilder;
import com.cheatsheet.quiz.config.app.AppProperties;
import com.cheatsheet.quiz.domain.AnswerOption;
import com.cheatsheet.quiz.domain.AnswerResult;
import com.cheatsheet.quiz.domain.Attempt;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionRevision;
import com.cheatsheet.quiz.domain.ReviewResult;
import com.cheatsheet.quiz.domain.ReviewState;
import com.cheatsheet.quiz.domain.exception.OptionNotFoundException;
import com.cheatsheet.quiz.feature.interview.controller.InterviewSessionSupport.AnswerSubmission;
import com.cheatsheet.quiz.feature.interview.service.core.InterviewService;
import com.cheatsheet.quiz.feature.interview.service.core.OptionLookupService;
import com.cheatsheet.quiz.feature.interview.service.core.TrainingSessionService;
import com.cheatsheet.quiz.feature.interview.service.review.AttemptRecorder;
import com.cheatsheet.quiz.feature.interview.service.review.ReviewService;
import com.cheatsheet.quiz.feature.interview.service.review.RevisionMismatchException;
import com.cheatsheet.quiz.feature.interview.service.topic.TopicCatalogService;
import com.cheatsheet.quiz.persistence.AnswerOptionRepository;
import com.cheatsheet.quiz.persistence.AttemptRepository;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.persistence.QuestionRevisionRepository;
import com.cheatsheet.quiz.persistence.QuestionStatsRepository;
import com.cheatsheet.quiz.persistence.ReviewStateRepository;
import com.cheatsheet.quiz.service.strategy.DefaultSelectionStrategy;
import com.cheatsheet.quiz.service.strategy.ShuffleSelectionStrategy;
import com.cheatsheet.quiz.service.strategy.WeakTopicsSelectionStrategy;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubmitImportWindowTest {
    @Mock QuestionRepository questions;
    @Mock AnswerOptionRepository options;
    @Mock QuestionRevisionRepository revisions;
    @Mock AttemptRepository attempts;
    @Mock ReviewService reviews;
    @Mock ApplicationEventPublisher events;

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-11-08T12:00:00Z"), ZoneOffset.UTC);
    private SubmitAnswerCommand command;
    private final List<String> reads = new ArrayList<>();

    @BeforeEach
    void setUp() {
        InterviewService grading = new InterviewService(questions, mock(QuestionStatsRepository.class), options,
                mock(ReviewStateRepository.class), mock(OptionLookupService.class), events,
                mock(DefaultSelectionStrategy.class), mock(ShuffleSelectionStrategy.class),
                mock(WeakTopicsSelectionStrategy.class), mock(TopicCatalogService.class),
                mock(TrainingSessionService.class), reviews, CLOCK, new AppProperties());
        command = new SubmitAnswerCommand(grading, new AttemptRecorder(attempts, revisions, CLOCK),
                new SimpleMeterRegistry());
    }

    @ParameterizedTest
    @ValueSource(strings = {"updated-awaiting-options", "updated-with-options", "deleted-question"})
    void committedImportAfterValidationProducesRevisionConflictBeforeGrading(String phase) {
        Content before = content(55L, 42L);
        Content after = switch (phase) {
            case "updated-awaiting-options" -> new Content(content(56L, 142L).question(),
                    content(56L, 142L).revision(), List.of());
            case "updated-with-options" -> content(56L, 142L);
            default -> new Content(null, null, List.of());
        };
        modelCommittedReads(before, after, true);

        assertThatThrownBy(() -> command.executeOrNullIfDuplicate(submission(42L), "session-1", null))
                .as("repository snapshots: %s", reads)
                .isInstanceOf(RevisionMismatchException.class);

        verifyNoInteractions(reviews, events);
        verify(attempts, never()).insert(any());
        verify(attempts, never()).insertIgnoreDuplicate(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid-client-option", "mcq-only-replacement"})
    void missingOptionWithoutAConfirmedRevisionChangeRetainsItsOriginalError(String scenario) {
        Content before = content(55L, 42L);
        boolean replacement = scenario.equals("mcq-only-replacement");
        modelCommittedReads(before, replacement ? content(55L, 142L) : before, replacement);

        assertThatThrownBy(() -> command.executeOrNullIfDuplicate(
                submission(replacement ? 42L : 999L), "session-1", null))
                .isInstanceOf(OptionNotFoundException.class);

        verifyNoInteractions(reviews, events);
        verify(attempts, never()).insertIgnoreDuplicate(any());
    }

    @Test
    void unchangedContentGradesAndRecordsExactlyOneOriginalRevision() {
        Content current = content(55L, 42L);
        modelCommittedReads(current, current, false);
        ReviewState state = new ReviewState(7L, 1, 1, 2.5, 100L, ReviewResult.CORRECT, 1, 0);
        when(reviews.applyAnswer(current.question(), true, null)).thenReturn(state);

        AnswerResult result = command.executeOrNullIfDuplicate(submission(42L), "session-1", null);

        assertThat(result.correctAnswer()).isTrue();
        assertThat(result.updatedState()).isSameAs(state);
        verify(reviews).applyAnswer(current.question(), true, null);
        ArgumentCaptor<Attempt> row = ArgumentCaptor.forClass(Attempt.class);
        verify(attempts).insertIgnoreDuplicate(row.capture());
        assertThat(row.getValue().questionRevisionId()).isEqualTo(55L);
        assertThat(row.getValue().selectedOptionId()).isEqualTo(42L);
    }

    @Test
    void omittedRevisionKeepsMissingOptionErrorWithoutMetadataQueries() {
        Content current = content(55L, 42L);
        when(attempts.tryClaimIdempotencyKey(eq("import-window"), anyLong())).thenReturn(true);
        when(questions.findById(7L)).thenReturn(Optional.of(current.question()));
        when(options.findByQuestionId(7L)).thenReturn(current.options());

        assertThatThrownBy(() -> command.executeOrNullIfDuplicate(
                submission(999L).toBuilder().questionRevisionId(null).build(), "session-1", null))
                .isInstanceOf(OptionNotFoundException.class);

        verifyNoInteractions(revisions, reviews, events);
        verify(attempts, never()).insertIgnoreDuplicate(any());
    }

    private void modelCommittedReads(Content before, Content after, boolean importAfterValidation) {
        AtomicReference<Content> committed = new AtomicReference<>(before);
        AtomicBoolean pendingCommit = new AtomicBoolean(importAfterValidation);
        when(attempts.tryClaimIdempotencyKey(eq("import-window"), anyLong())).thenReturn(true);
        when(revisions.findLatest(7L)).thenAnswer(invocation -> {
            Content read = committed.get();
            reads.add("latest=" + (read.revision() == null ? "missing" : read.revision().id()));
            // Model the next committed snapshot after the validation SELECT.
            // No actual importer, database transaction or content file is run.
            if (pendingCommit.getAndSet(false)) {
                committed.set(after);
                reads.add("import-commit=" + (after.revision() == null ? "deleted" : after.revision().id()));
            }
            return Optional.ofNullable(read.revision());
        });
        when(questions.findById(7L)).thenAnswer(invocation -> {
            Question read = committed.get().question();
            reads.add("question=" + (read == null ? "missing" : read.sourceHash()));
            return Optional.ofNullable(read);
        });
        lenient().when(options.findByQuestionId(7L)).thenAnswer(invocation -> {
            List<AnswerOption> read = committed.get().options();
            reads.add("options=" + read.stream().map(AnswerOption::id).toList());
            return read;
        });
        lenient().when(revisions.findById(55L)).thenReturn(Optional.of(before.revision()));
    }

    private AnswerSubmission submission(long optionId) {
        return AnswerSubmission.builder().questionId(7L).optionId(optionId)
                .questionRevisionId(55L).clientAttemptId("import-window").build();
    }

    private Content content(long revisionId, long selectedOptionId) {
        Question question = TestQuestionBuilder.aQuestion().withId(7L).withSourceHash("hash-" + revisionId).build();
        QuestionRevision revision = new QuestionRevision(revisionId, 7L, revisionId == 55 ? 1 : 2,
                question.sourceHash(), "PUBLISHED", 100L);
        return new Content(question, revision, List.of(
                new AnswerOption(selectedOptionId, 7L, "Selected", true, 0, "MARKDOWN", null),
                new AnswerOption(selectedOptionId + 1, 7L, "Other", false, 1, "MARKDOWN", null)));
    }

    private record Content(Question question, QuestionRevision revision, List<AnswerOption> options) {}
}
