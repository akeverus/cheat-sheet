package com.cheatsheet.quiz.feature.interview.service.review;

import com.cheatsheet.quiz.domain.FsrsRating;
import com.cheatsheet.quiz.domain.FsrsState;
import lombok.Builder;
import org.springframework.stereotype.Service;

/**
 * Планировщик FSRS-4.5 (Free Spaced Repetition Scheduler) с опубликованными
 * дефолтными весами.
 *
 * <p>Модель памяти — пара (Stability S, Difficulty D). Оптимизация весов на
 * истории пользователя вне scope: используются дефолты, поэтому результат
 * детерминирован и объясним. Чистая функция от состояния, оценки и времени
 * (никакого {@link java.time.Clock}) — {@code nowEpoch} передаёт вызывающий,
 * чтобы легко тестировать.</p>
 *
 * <p>Формулы (DECAY = −0.5, FACTOR = 19/81):</p>
 * <ul>
 *   <li>кривая забывания: R(t,S) = (1 + FACTOR·t/S)^DECAY;</li>
 *   <li>интервал: I = (S/FACTOR)·(retention^(1/DECAY) − 1) ≈ S при retention 0.9;</li>
 *   <li>S₀(G) = w[G−1]; D₀(G) = w[4] − e^(w[5]·(G−1)) + 1;</li>
 *   <li>стабильность после припоминания/забывания и mean-reversion сложности —
 *       по спецификации FSRS-4.5.</li>
 * </ul>
 *
 * @see <a href="https://github.com/open-spaced-repetition/awesome-fsrs/wiki/The-Algorithm">FSRS Algorithm</a>
 */
@Service
public class FsrsService {

    /** Версия алгоритма, записываемая в {@code review_state.algo_version}. */
    public static final String ALGO_VERSION = "fsrs-4.5-default";

    /** Опубликованные дефолтные веса FSRS-4.5 (17 параметров, w[0..16]). */
    static final double[] DEFAULT_W = {
            0.4872, 1.4003, 3.7145, 13.8206, 5.1618, 1.2298, 0.8975, 0.031,
            1.6474, 0.1367, 1.0461, 2.1072, 0.0793, 0.3246, 1.587, 0.2272, 2.8755
    };

    private static final double DECAY = -0.5;
    private static final double FACTOR = 19.0 / 81.0;
    private static final double REQUEST_RETENTION = 0.9;

    private static final double MIN_STABILITY = 0.1;
    private static final double MIN_DIFFICULTY = 1.0;
    private static final double MAX_DIFFICULTY = 10.0;
    private static final int MIN_INTERVAL_DAYS = 1;
    private static final int MAX_INTERVAL_DAYS = 36500;
    private static final long SECONDS_PER_DAY = 86_400L;

    /** Ease-границы SM-2 для аппроксимации сложности при миграции прогресса. */
    private static final double SM2_MIN_EASE = 1.3;
    private static final double SM2_DEFAULT_EASE = 2.5;

    private final double[] w;

    public FsrsService() {
        this(DEFAULT_W);
    }

    /** Для тестов/будущей оптимизации: планировщик с заданными весами. */
    FsrsService(double[] weights) {
        this.w = weights.clone();
    }

    /**
     * Планирует следующее повторение по FSRS.
     *
     * @param current  текущее состояние памяти (или {@link FsrsState#isNew()} — первое повторение)
     * @param rating   оценка припоминания
     * @param nowEpoch текущее время, epoch-секунды
     * @return новое состояние памяти + интервал/дата следующего повторения
     */
    public Result schedule(FsrsState current, FsrsRating rating, long nowEpoch) {
        int g = rating.value();
        double stability;
        double difficulty;
        int lapses = current.lapses();

        if (current.isNew()) {
            stability = initStability(g);
            difficulty = initDifficulty(g);
            if (rating == FsrsRating.AGAIN) {
                lapses += 1;
            }
        } else {
            double sOld = current.stability();
            double dOld = current.difficulty();
            double elapsedDays = elapsedDays(current.lastReviewedAt(), nowEpoch);
            double retrievability = forgettingCurve(elapsedDays, sOld);
            difficulty = nextDifficulty(dOld, g);
            if (rating == FsrsRating.AGAIN) {
                stability = nextForgetStability(dOld, sOld, retrievability);
                lapses += 1;
            } else {
                stability = nextRecallStability(dOld, sOld, retrievability, g);
            }
        }

        stability = clamp(stability, MIN_STABILITY, MAX_INTERVAL_DAYS);
        int intervalDays = nextInterval(stability);
        long nextReviewAt = nowEpoch + (long) intervalDays * SECONDS_PER_DAY;

        return Result.builder()
                .stability(stability)
                .difficulty(difficulty)
                .lapses(lapses)
                .intervalDays(intervalDays)
                .nextReviewAt(nextReviewAt)
                .lastReviewedAt(nowEpoch)
                .build();
    }

    /**
     * Аппроксимирует начальную память FSRS из накопленного SM-2-прогресса —
     * чтобы при переходе на FSRS не терять историю. Стабильность ≈ текущий
     * SM-2-интервал (интервал ≈ стабильность при retention 0.9); сложность
     * линейно из ease-фактора (низкий ease → высокая сложность).
     *
     * @param intervalDays SM-2 interval_days
     * @param easeFactor   SM-2 ease_factor
     * @return начальные (stability, difficulty)
     */
    public Seed seedFromSm2(int intervalDays, double easeFactor) {
        double stability = clamp(intervalDays, MIN_STABILITY, MAX_INTERVAL_DAYS);
        double easeSpan = SM2_DEFAULT_EASE - SM2_MIN_EASE;
        double normalized = (easeFactor - SM2_MIN_EASE) / easeSpan;
        double difficulty = clamp(MAX_DIFFICULTY - normalized * 9.0, MIN_DIFFICULTY, MAX_DIFFICULTY);
        return new Seed(stability, difficulty);
    }

    // ── Формулы FSRS-4.5 ────────────────────────────────────────────────────

    private double initStability(int g) {
        return Math.max(w[g - 1], MIN_STABILITY);
    }

    private double initDifficulty(int g) {
        return clampDifficulty(w[4] - Math.exp(w[5] * (g - 1)) + 1.0);
    }

    private double forgettingCurve(double elapsedDays, double stability) {
        return Math.pow(1.0 + FACTOR * elapsedDays / stability, DECAY);
    }

    private double nextDifficulty(double difficulty, int g) {
        double next = difficulty - w[6] * (g - 3);
        // Mean-reversion к сложности «лёгкого» первого ответа (D₀ при G=4).
        double reverted = w[7] * initDifficulty(4) + (1.0 - w[7]) * next;
        return clampDifficulty(reverted);
    }

    private double nextRecallStability(double difficulty, double stability, double retrievability, int g) {
        double hardPenalty = (g == FsrsRating.HARD.value()) ? w[15] : 1.0;
        double easyBonus = (g == FsrsRating.EASY.value()) ? w[16] : 1.0;
        return stability * (1.0
                + Math.exp(w[8])
                * (11.0 - difficulty)
                * Math.pow(stability, -w[9])
                * (Math.exp((1.0 - retrievability) * w[10]) - 1.0)
                * hardPenalty
                * easyBonus);
    }

    private double nextForgetStability(double difficulty, double stability, double retrievability) {
        return w[11]
                * Math.pow(difficulty, -w[12])
                * (Math.pow(stability + 1.0, w[13]) - 1.0)
                * Math.exp((1.0 - retrievability) * w[14]);
    }

    private int nextInterval(double stability) {
        double raw = (stability / FACTOR) * (Math.pow(REQUEST_RETENTION, 1.0 / DECAY) - 1.0);
        long rounded = Math.round(raw);
        return (int) clampLong(rounded, MIN_INTERVAL_DAYS, MAX_INTERVAL_DAYS);
    }

    private static double elapsedDays(Long lastReviewedAt, long nowEpoch) {
        if (lastReviewedAt == null) {
            return 0.0;
        }
        return Math.max(0.0, (nowEpoch - lastReviewedAt) / (double) SECONDS_PER_DAY);
    }

    private double clampDifficulty(double d) {
        return clamp(d, MIN_DIFFICULTY, MAX_DIFFICULTY);
    }

    private static double clamp(double value, double min, double max) {
        return Math.min(Math.max(value, min), max);
    }

    private static long clampLong(long value, long min, long max) {
        return Math.min(Math.max(value, min), max);
    }

    /** Результат планирования FSRS. */
    @Builder
    public record Result(
            double stability,
            double difficulty,
            int lapses,
            int intervalDays,
            long nextReviewAt,
            long lastReviewedAt
    ) {}

    /** Начальная память FSRS, аппроксимированная из SM-2-прогресса. */
    public record Seed(double stability, double difficulty) {}
}
