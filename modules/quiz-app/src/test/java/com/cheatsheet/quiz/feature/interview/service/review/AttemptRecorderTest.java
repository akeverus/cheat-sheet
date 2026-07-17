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
        when(attemptRepository.existsByQuestionId(7L)).thenReturn(false);
        when(questionRevisionRepository.findLatest(7L)).thenReturn(Optional.of(
                new QuestionRevision(55L, 7L, 3, "hash", "PUBLISHED", 100L)));
        Clock clock = Clock.fixed(Instant.parse("2026-07-15T12:00:00Z"), ZoneOffset.UTC);

        newRecorder(clock).record(7L, AttemptOutcome.CORRECT, 5, 42L, 3200, "sess-1");

        ArgumentCaptor<Attempt> captor = ArgumentCaptor.forClass(Attempt.class);
        verify(attemptRepository).insert(captor.capture());
        Attempt attempt = captor.getValue();
        assertThat(attempt.questionId()).isEqualTo(7L);
        assertThat(attempt.firstAttempt()).isTrue();
        assertThat(attempt.questionRevisionId()).isEqualTo(55L);
        assertThat(attempt.outcome()).isEqualTo(AttemptOutcome.CORRECT);
        assertThat(attempt.memoryGrade()).isEqualTo(5);
        assertThat(attempt.selectedOptionId()).isEqualTo(42L);
        assertThat(attempt.responseTimeMs()).isEqualTo(3200);
        assertThat(attempt.sessionToken()).isEqualTo("sess-1");
        assertThat(attempt.createdAt()).isEqualTo(Instant.parse("2026-07-15T12:00:00Z").getEpochSecond());
    }

    @Test
    void recordMarksNonFirstAttemptAndNullRevisionWhenAbsent() {
        when(attemptRepository.existsByQuestionId(7L)).thenReturn(true);
        when(questionRevisionRepository.findLatest(7L)).thenReturn(Optional.empty());
        Clock clock = Clock.fixed(Instant.parse("2026-07-15T12:00:00Z"), ZoneOffset.UTC);

        newRecorder(clock).record(7L, AttemptOutcome.UNKNOWN, 0, null, null, "sess-2");

        ArgumentCaptor<Attempt> captor = ArgumentCaptor.forClass(Attempt.class);
        verify(attemptRepository).insert(captor.capture());
        Attempt attempt = captor.getValue();
        assertThat(attempt.firstAttempt()).isFalse();
        assertThat(attempt.questionRevisionId()).isNull();
        assertThat(attempt.selectedOptionId()).isNull();
        assertThat(attempt.responseTimeMs()).isNull();
        assertThat(attempt.outcome()).isEqualTo(AttemptOutcome.UNKNOWN);
        assertThat(attempt.memoryGrade()).isZero();
    }
}
