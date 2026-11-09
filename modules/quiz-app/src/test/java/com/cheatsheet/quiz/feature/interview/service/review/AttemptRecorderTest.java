package com.cheatsheet.quiz.feature.interview.service.review;

import com.cheatsheet.quiz.domain.Attempt;
import com.cheatsheet.quiz.domain.AttemptOutcome;
import com.cheatsheet.quiz.domain.QuestionRevision;
import com.cheatsheet.quiz.persistence.AttemptRepository;
import com.cheatsheet.quiz.persistence.QuestionRevisionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AttemptRecorderTest {

    @Mock
    AttemptRepository attemptRepository;
    @Mock
    QuestionRevisionRepository questionRevisionRepository;

    private AttemptRecorder newRecorder(Clock clock) {
        return new AttemptRecorder(attemptRepository, questionRevisionRepository, clock);
    }

    @Test
    void recordMarksFirstAttemptAndLinksLatestRevision() {
        when(attemptRepository.findByIdempotencyKey("attempt-1")).thenReturn(Optional.empty());
        when(questionRevisionRepository.findLatest(7L)).thenReturn(Optional.of(
                new QuestionRevision(55L, 7L, 3, "hash", "PUBLISHED", 100L)));
        Clock clock = Clock.fixed(Instant.parse("2026-07-15T12:00:00Z"), ZoneOffset.UTC);

        newRecorder(clock).record(7L, AttemptOutcome.CORRECT, 5, 42L, 3200, "sess-1", "attempt-1");

        ArgumentCaptor<Attempt> captor = ArgumentCaptor.forClass(Attempt.class);
        verify(attemptRepository).insertIgnoreDuplicate(captor.capture());
        Attempt attempt = captor.getValue();
        assertThat(attempt.questionId()).isEqualTo(7L);
        // is_first_attempt вычисляется атомарно в AttemptRepository (BE-006).
        assertThat(attempt.questionRevisionId()).isEqualTo(55L);
        assertThat(attempt.outcome()).isEqualTo(AttemptOutcome.CORRECT);
        assertThat(attempt.memoryGrade()).isEqualTo(5);
        assertThat(attempt.selectedOptionId()).isEqualTo(42L);
        assertThat(attempt.responseTimeMs()).isEqualTo(3200);
        assertThat(attempt.sessionToken()).isEqualTo("sess-1");
        assertThat(attempt.idempotencyKey()).isEqualTo("attempt-1");
        assertThat(attempt.createdAt()).isEqualTo(Instant.parse("2026-07-15T12:00:00Z").getEpochSecond());
    }

    @Test
    void recordMarksNonFirstAttemptAndNullRevisionWhenAbsent() {
        when(questionRevisionRepository.findLatest(7L)).thenReturn(Optional.empty());
        Clock clock = Clock.fixed(Instant.parse("2026-07-15T12:00:00Z"), ZoneOffset.UTC);

        newRecorder(clock).record(7L, AttemptOutcome.UNKNOWN, 0, null, null, "sess-2", null);

        ArgumentCaptor<Attempt> captor = ArgumentCaptor.forClass(Attempt.class);
        verify(attemptRepository).insert(captor.capture());
        Attempt attempt = captor.getValue();
        assertThat(attempt.questionRevisionId()).isNull();
        assertThat(attempt.selectedOptionId()).isNull();
        assertThat(attempt.responseTimeMs()).isNull();
        assertThat(attempt.outcome()).isEqualTo(AttemptOutcome.UNKNOWN);
        assertThat(attempt.memoryGrade()).isZero();
        assertThat(attempt.idempotencyKey()).isNull();
    }

    @Test
    void recordSkipsInsertWhenClientAttemptIdAlreadyRecorded() {
        // BE-003: retry с тем же clientAttemptId (первый ответ уже записан, но клиент
        // не получил HTTP-ответ из-за сетевой ошибки) → повторная запись не создаётся.
        when(attemptRepository.findByIdempotencyKey("attempt-dup")).thenReturn(Optional.of(
                Attempt.builder().questionId(7L).idempotencyKey("attempt-dup").build()));
        Clock clock = Clock.fixed(Instant.parse("2026-07-15T12:00:00Z"), ZoneOffset.UTC);

        newRecorder(clock).record(7L, AttemptOutcome.CORRECT, 5, 42L, 3200, "sess-1", "attempt-dup");

        verify(attemptRepository, never()).insert(any());
        verify(attemptRepository, never()).insertIgnoreDuplicate(any());
    }

    @Test
    void recordUsesClientRevisionWhenBelongsToQuestion() {
        when(questionRevisionRepository.findById(99L)).thenReturn(Optional.of(
                new QuestionRevision(99L, 7L, 3, "hash", "PUBLISHED", 100L)));
        Clock clock = Clock.fixed(Instant.parse("2026-07-15T12:00:00Z"), ZoneOffset.UTC);

        newRecorder(clock).record(7L, AttemptOutcome.CORRECT, 5, 42L, 3200, "sess-1", "attempt-1", null, 99L);

        ArgumentCaptor<Attempt> captor = ArgumentCaptor.forClass(Attempt.class);
        verify(attemptRepository).insertIgnoreDuplicate(captor.capture());
        assertThat(captor.getValue().questionRevisionId()).isEqualTo(99L);
        verify(questionRevisionRepository, never()).findLatest(7L);
    }

    @Test
    void recordRejectsWhenClientRevisionBelongsToOtherQuestion() {
        when(questionRevisionRepository.findById(88L)).thenReturn(Optional.of(
                new QuestionRevision(88L, 99L, 3, "hash", "PUBLISHED", 100L)));
        Clock clock = Clock.fixed(Instant.parse("2026-07-15T12:00:00Z"), ZoneOffset.UTC);

        org.junit.jupiter.api.Assertions.assertThrows(
                RevisionMismatchException.class,
                () -> newRecorder(clock).record(7L, AttemptOutcome.CORRECT, 5, 42L, 3200, "sess-1", "attempt-1", null, 88L));

        verify(attemptRepository, never()).insertIgnoreDuplicate(any());
    }

    @Test
    void requireValidRevisionThrowsOnUnknownId() {
        when(questionRevisionRepository.findLatest(7L)).thenReturn(Optional.of(
                new QuestionRevision(55L, 7L, 3, "hash", "PUBLISHED", 100L)));
        Clock clock = Clock.fixed(Instant.parse("2026-07-15T12:00:00Z"), ZoneOffset.UTC);

        org.junit.jupiter.api.Assertions.assertThrows(
                RevisionMismatchException.class,
                () -> newRecorder(clock).requireValidRevision(7L, 1L));
    }

    @Test
    void recordBlankClientAttemptIdSkipsLookupAndStoresNullKey() {
        // Пустой ключ (blank) не должен ни триггерить lookup, ни попадать в БД —
        // partial unique index покрывает только idempotency_key IS NOT NULL.
        when(questionRevisionRepository.findLatest(7L)).thenReturn(Optional.empty());
        Clock clock = Clock.fixed(Instant.parse("2026-07-15T12:00:00Z"), ZoneOffset.UTC);

        newRecorder(clock).record(7L, AttemptOutcome.CORRECT, 5, 42L, 3200, "sess-1", "   ");

        ArgumentCaptor<Attempt> captor = ArgumentCaptor.forClass(Attempt.class);
        verify(attemptRepository).insert(captor.capture());
        assertThat(captor.getValue().idempotencyKey()).isNull();
        verify(attemptRepository, never()).findByIdempotencyKey(any());
    }
}
