package com.cheatsheet.quiz.feature.interview.service.review;

import com.cheatsheet.quiz.domain.FsrsState;
import com.cheatsheet.quiz.domain.ReviewReason;
import com.cheatsheet.quiz.domain.ReviewResult;
import com.cheatsheet.quiz.domain.ReviewState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Классификатор explainable-причины повторения. Детерминированно (при фикс.
 * {@link Clock}) выводит {@link ReviewReason} из уже накопленных сигналов
 * состояния повторения. Чистая логика — без обращения к БД: данные передаются
 * вызывающим ({@code FocusTrainingPageService}).
 *
 * <p>Приоритет причин: недавняя ошибка → просрочка → слабый навык темы.</p>
 */
@Service
@RequiredArgsConstructor
public class ReviewReasonService {

    /** Порог «слабого навыка» по точности темы, в процентах (0..100). */
    static final double WEAK_SKILL_ACCURACY_THRESHOLD = 60.0;

    private static final long SECONDS_PER_DAY = 86_400L;
    private static final DateTimeFormatter DAY_MONTH = DateTimeFormatter.ofPattern("dd.MM");

    private final Clock clock;

    /**
     * Определяет причину, по которой вопрос стоит повторить сейчас.
     *
     * @param reviewState  состояние SM-2 (общие поля last_result/next_review_at)
     * @param fsrsState    FSRS-память (нужна ради {@code last_reviewed_at})
     * @param topicAccuracy точность по теме в процентах (0..100), или &lt;0 если данных нет
     * @return причина или {@link Optional#empty()}, если явного повода нет
     */
    public Optional<ReviewReason> classify(Optional<ReviewState> reviewState,
                                           Optional<FsrsState> fsrsState,
                                           double topicAccuracy) {
        long now = clock.instant().getEpochSecond();
        ReviewState state = reviewState.orElse(null);
        Long lastReviewedAt = fsrsState.map(FsrsState::lastReviewedAt).orElse(null);

        // 1) Недавний лапс: последний ответ был неверным / «не знаю».
        if (state != null && isLapse(state.lastResult())) {
            String message = lastReviewedAt != null
                    ? "Последняя ошибка: " + formatDay(lastReviewedAt)
                    : "Последняя попытка была неверной";
            return Optional.of(new ReviewReason(ReviewReason.Type.RECENT_LAPSE, message));
        }

        // 2) Просрочка: текущее время позже запланированного повторения ≥ 1 дня.
        if (state != null) {
            long overdueDays = (now - state.nextReviewAt()) / SECONDS_PER_DAY;
            if (overdueDays >= 1) {
                return Optional.of(new ReviewReason(ReviewReason.Type.OVERDUE,
                        "Повторение просрочено на " + overdueDays + " " + pluralizeDays(overdueDays)));
            }
        }

        // 3) Слабый навык: общая точность по теме ниже порога (и данные есть).
        if (topicAccuracy >= 0 && topicAccuracy < WEAK_SKILL_ACCURACY_THRESHOLD) {
            long percent = Math.round(topicAccuracy);
            return Optional.of(new ReviewReason(ReviewReason.Type.WEAK_SKILL,
                    "Слабый навык — точность по теме " + percent + "%"));
        }

        return Optional.empty();
    }

    private static boolean isLapse(ReviewResult lastResult) {
        return lastResult == ReviewResult.WRONG || lastResult == ReviewResult.UNKNOWN;
    }

    private String formatDay(long epochSeconds) {
        return DAY_MONTH.format(Instant.ofEpochSecond(epochSeconds).atZone(clock.getZone()));
    }

    /**
     * Русское склонение слова «день» по числу: 1 день, 2–4 дня, 5+ дней
     * (с учётом исключения 11–14 → «дней»).
     */
    private static String pluralizeDays(long days) {
        long mod100 = days % 100;
        if (mod100 >= 11 && mod100 <= 14) {
            return "дней";
        }
        long mod10 = days % 10;
        if (mod10 == 1) {
            return "день";
        }
        if (mod10 >= 2 && mod10 <= 4) {
            return "дня";
        }
        return "дней";
    }
}
