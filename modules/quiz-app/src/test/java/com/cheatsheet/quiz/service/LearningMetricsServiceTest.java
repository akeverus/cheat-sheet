package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.config.app.AppProperties;
import com.cheatsheet.quiz.domain.LearningMetrics;
import com.cheatsheet.quiz.feature.interview.usecase.stats.LearningMetricsService;
import com.cheatsheet.quiz.persistence.AttemptRepository;
import com.cheatsheet.quiz.persistence.QuestionStatsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LearningMetricsServiceTest {

    private static final long NOW = Instant.parse("2026-03-10T00:00:00Z").getEpochSecond();
    private static final long DAY = 86_400L;

    @Mock
    AttemptRepository attemptRepository;
    @Mock
    QuestionStatsRepository questionStatsRepository;

    private LearningMetricsService service() {
        Clock clock = Clock.fixed(Instant.ofEpochSecond(NOW), ZoneOffset.UTC);
        // Реальный AppProperties: learnedRepetitions по умолчанию = 3.
        return new LearningMetricsService(attemptRepository, questionStatsRepository, new AppProperties(), clock);
    }

    @Test
    void computesEachRatioWithItsOwnDenominatorAndPassesWindowCutoffs() {
        when(attemptRepository.aggregate(NOW - 7 * DAY, NOW - 30 * DAY)).thenReturn(
                new AttemptRepository.AttemptAggregate(
                        /*cardsSeen*/ 7, /*attemptsTotal*/ 12,
                        /*attemptsCorrect*/ 5, /*attemptsIncorrect*/ 5,
                        /*firstTotal*/ 4, /*firstCorrect*/ 3,
                        /*rev7Total*/ 8, /*rev7Correct*/ 4,
                        /*rev30Total*/ 12, /*rev30Correct*/ 9,
                        /*averageResponseMs*/ 250.0));
        when(questionStatsRepository.getScheduleAggregate(NOW, NOW - DAY, 3)).thenReturn(
                new QuestionStatsRepository.ScheduleAggregate(10, 4, 3, 1));

        LearningMetrics m = service().compute();

        assertThat(m.cardsTotal()).isEqualTo(10);
        assertThat(m.cardsSeen()).isEqualTo(7);
        assertThat(m.cardsMastered()).isEqualTo(4);
        assertThat(m.attemptsTotal()).isEqualTo(12);
        assertThat(m.attemptsCorrect()).isEqualTo(5);
        assertThat(m.attemptsIncorrect()).isEqualTo(5);
        assertThat(m.firstAttemptAccuracy()).isEqualTo(75.0);  // 3/4 — честная память
        assertThat(m.overallAccuracy()).isEqualTo(50.0);       // 5/(5+5) — все non-SKIP
        assertThat(m.retention7d()).isEqualTo(50.0);           // 4/8
        assertThat(m.retention30d()).isEqualTo(75.0);          // 9/12
        assertThat(m.dueNow()).isEqualTo(3);
        assertThat(m.overdue()).isEqualTo(1);
        assertThat(m.averageResponseMs()).isEqualTo(250.0);

        verify(attemptRepository).aggregate(eq(NOW - 7 * DAY), eq(NOW - 30 * DAY));
        verify(questionStatsRepository).getScheduleAggregate(eq(NOW), eq(NOW - DAY), eq(3));
    }

    @Test
    void emptyDenominatorsYieldNoDataSentinels() {
        when(attemptRepository.aggregate(NOW - 7 * DAY, NOW - 30 * DAY))
                .thenReturn(AttemptRepository.AttemptAggregate.EMPTY);
        when(questionStatsRepository.getScheduleAggregate(NOW, NOW - DAY, 3))
                .thenReturn(QuestionStatsRepository.ScheduleAggregate.EMPTY);

        LearningMetrics m = service().compute();

        assertThat(m.hasFirstAttemptAccuracy()).isFalse();
        assertThat(m.hasOverallAccuracy()).isFalse();
        assertThat(m.hasRetention7d()).isFalse();
        assertThat(m.hasRetention30d()).isFalse();
        assertThat(m.hasAverageResponse()).isFalse();
        assertThat(m.firstAttemptAccuracy()).isEqualTo(LearningMetrics.NO_DATA);
        assertThat(m.cardsTotal()).isZero();
    }
}
