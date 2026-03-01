package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.domain.ReviewDefaults;
import com.cheatsheet.quiz.domain.ReviewResult;
import com.cheatsheet.quiz.domain.ReviewState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Реализация алгоритма интервального повторения SM-2 (SuperMemo 2).
 *
 * <p>На основании правильности ответа пересчитывает:</p>
 * <ul>
 *   <li>количество последовательных правильных ответов ({@code repetitions});</li>
 *   <li>интервал до следующего повторения ({@code intervalDays}): 1 день после первого правильного, 6 дней после второго, далее по формуле {@code interval * easeFactor};</li>
 *   <li>ease factor — коэффициент лёгкости (обновляется по формуле SM-2, не ниже {@link ReviewDefaults#MIN_EASE_FACTOR});</li>
 *   <li>дату следующего повторения ({@code nextReviewAt});</li>
 *   <li>результат последнего ответа ({@link ReviewResult#CORRECT} или {@link ReviewResult#WRONG}).</li>
 * </ul>
 *
 * <p>При неправильном ответе (оценка ниже порога) прогресс сбрасывается: {@code repetitions = 0}, интервал возвращается к 1 дню.</p>
 *
 * @see <a href="https://en.wikipedia.org/wiki/SuperMemo#SM-2">Алгоритм SM-2 (Wikipedia)</a>
 */
@Service
@Slf4j
public class SpacedRepetitionService {

    /** Оценка SM-2 для правильного ответа (максимальная уверенность). */
    private static final int GRADE_CORRECT = 5;

    /** Оценка SM-2 для неправильного ответа (минимальный порог). */
    private static final int GRADE_WRONG = 2;

    /** Порог SM-2: при оценке ниже этого значения прогресс сбрасывается. */
    private static final int GRADE_THRESHOLD = 3;

    /** Начальный интервал при первом правильном ответе (дни). */
    private static final int INITIAL_INTERVAL_DAYS = 1;

    /** Интервал после второго правильного ответа (дни). */
    private static final int SECOND_INTERVAL_DAYS = 6;

    /** SM-2 ease factor increment constants. */
    private static final double EASE_BASE = 0.1;
    private static final double EASE_LINEAR = 0.08;
    private static final double EASE_QUADRATIC = 0.02;

    private final Clock clock;

    public SpacedRepetitionService(Clock clock) {
        this.clock = clock;
    }

    /**
     * Вычисляет прирост ease factor по формуле SM-2 для данной оценки (1–5).
     *
     * <p>Формула: 0.1 - (5 - grade) * (0.08 + (5 - grade) * 0.02).</p>
     *
     * @param grade оценка SM-2 (1–5)
     * @return прирост ease factor
     */
    public static double computeEaseDelta(int grade) {
        double q = 5.0 - grade;
        return EASE_BASE - q * (EASE_LINEAR + q * EASE_QUADRATIC);
    }

    /**
     * Применяет результат ответа к текущему состоянию повторения.
     *
     * @param current текущее состояние (из БД)
     * @param correct {@code true} если ответ правильный
     * @return новое состояние (для сохранения в БД)
     */
    public ReviewState applyAnswer(ReviewState current, boolean correct) {
        int grade = correct ? GRADE_CORRECT : GRADE_WRONG;
        return applyAnswer(current, correct, grade);
    }

    /**
     * Применяет результат ответа с заданной оценкой уверенности (SM-2 grade 1-5).
     *
     * <p>Используется для режима уверенности:</p>
     * <ul>
     *   <li>grade 3 — «Угадал» (минимальный прирост интервала);</li>
     *   <li>grade 4 — «Вспомнил с трудом» (средний прирост);</li>
     *   <li>grade 5 — «Знал точно» (максимальный прирост).</li>
     * </ul>
     *
     * @param current текущее состояние (из БД)
     * @param correct {@code true} если ответ правильный
     * @param grade   оценка SM-2 (1-5), при неправильном ответе игнорируется (используется 2)
     * @return новое состояние (для сохранения в БД)
     */
    public ReviewState applyAnswer(ReviewState current, boolean correct, int grade) {
        if (!correct) {
            grade = GRADE_WRONG;
        } else {
            grade = Math.max(GRADE_THRESHOLD, Math.min(5, grade));
        }

        int repetitions = current.repetitions();
        int interval = current.intervalDays();
        double ease = current.easeFactor();

        if (grade < GRADE_THRESHOLD) {
            repetitions = 0;
            interval = INITIAL_INTERVAL_DAYS;
        } else {
            if (repetitions == 0) {
                interval = INITIAL_INTERVAL_DAYS;
            } else if (repetitions == 1) {
                interval = SECOND_INTERVAL_DAYS;
            } else {
                interval = Math.max(1, (int) Math.round(interval * ease));
            }
            repetitions += 1;
        }

        // Пересчёт ease factor по формуле SM-2
        double oldEase = ease;
        int oldInterval = interval;
        ease = ease + computeEaseDelta(grade);
        if (ease < ReviewDefaults.MIN_EASE_FACTOR) {
            ease = ReviewDefaults.MIN_EASE_FACTOR;
        }
        log.debug("SM-2 state: questionId={} ease {} -> {}, interval {} -> {} days",
                current.questionId(), oldEase, ease, oldInterval, interval);

        // Вычисляем дату следующего повторения
        Instant next = Instant.now(clock).plus(interval, ChronoUnit.DAYS);
        long nextEpoch = next.getEpochSecond();

        ReviewResult lastResult = correct ? ReviewResult.CORRECT : ReviewResult.WRONG;

        int correctCount = current.correctCount() + (correct ? 1 : 0);
        int wrongCount = current.wrongCount() + (correct ? 0 : 1);

        log.debug("Review recorded: questionId={}, grade={}, newInterval={} days, nextReviewAt={}",
                current.questionId(), grade, interval, next);

        return new ReviewState(
                current.questionId(),
                repetitions,
                interval,
                ease,
                nextEpoch,
                lastResult,
                correctCount,
                wrongCount
        );
    }
}
