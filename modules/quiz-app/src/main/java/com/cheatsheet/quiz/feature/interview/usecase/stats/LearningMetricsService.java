package com.cheatsheet.quiz.feature.interview.usecase.stats;

import com.cheatsheet.quiz.config.app.AppProperties;
import com.cheatsheet.quiz.domain.LearningMetrics;
import com.cheatsheet.quiz.persistence.AttemptRepository;
import com.cheatsheet.quiz.persistence.QuestionStatsRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.Clock;

/**
 * Собирает семантически разведённые {@link LearningMetrics} из двух источников:
 * лог попыток ({@code attempt}) и состояние повторений ({@code review_state}).
 * Проценты считаются здесь (каждый со своим знаменателем), а не в SQL — чтобы
 * логика «нет данных → {@link LearningMetrics#NO_DATA}» была в одном месте и
 * покрывалась unit-тестами.
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LearningMetricsService {

    private static final long SECONDS_PER_DAY = 86_400L;

    AttemptRepository attemptRepository;
    QuestionStatsRepository questionStatsRepository;
    AppProperties appProperties;
    Clock clock;

    /**
     * Вычисляет актуальные метрики обучения (глобально по всей БД вопросов).
     */
    public LearningMetrics compute() {
        long now = clock.instant().getEpochSecond();
        AttemptRepository.AttemptAggregate attempts = attemptRepository.aggregate(
                now - 7 * SECONDS_PER_DAY, now - 30 * SECONDS_PER_DAY);
        QuestionStatsRepository.ScheduleAggregate schedule = questionStatsRepository.getScheduleAggregate(
                now, now - SECONDS_PER_DAY, appProperties.getInterview().getLearnedRepetitions());

        double firstAttemptAccuracy = percentage(attempts.firstCorrect(), attempts.firstTotal());
        long nonSkip = attempts.attemptsCorrect() + attempts.attemptsIncorrect();
        double overallAccuracy = percentage(attempts.attemptsCorrect(), nonSkip);
        double retention7d = percentage(attempts.rev7Correct(), attempts.rev7Total());
        double retention30d = percentage(attempts.rev30Correct(), attempts.rev30Total());

        return new LearningMetrics(
                schedule.cardsTotal(),
                attempts.cardsSeen(),
                schedule.cardsMastered(),
                attempts.attemptsTotal(),
                attempts.attemptsCorrect(),
                attempts.attemptsIncorrect(),
                firstAttemptAccuracy,
                overallAccuracy,
                retention7d,
                retention30d,
                schedule.dueNow(),
                schedule.overdue(),
                attempts.averageResponseMs());
    }

    /**
     * Доля в процентах 0..100; {@link LearningMetrics#NO_DATA} при пустом знаменателе.
     */
    private static double percentage(long numerator, long denominator) {
        return denominator > 0 ? numerator * 100.0 / denominator : LearningMetrics.NO_DATA;
    }
}
