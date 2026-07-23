package com.cheatsheet.quiz.feature.interview.service.progress;

import com.cheatsheet.quiz.feature.interview.dto.response.progress.StreakResponse;
import com.cheatsheet.quiz.domain.DailyProgress;
import com.cheatsheet.quiz.domain.RussianPlural;
import com.cheatsheet.quiz.persistence.DailyActivityRepository;
import com.cheatsheet.quiz.service.event.AnswerEvent;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Сервис ежедневных целей и серий (Observer pattern).
 *
 * <p>Слушает {@link AnswerEvent} и обновляет {@code daily_activity}.
 * Предоставляет данные для streak-бара в UI.</p>
 */
@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DailyStreakService {

    DailyActivityRepository dailyActivityRepository;
    int defaultDailyGoal;

    public DailyStreakService(
            DailyActivityRepository dailyActivityRepository,
            @Value("${app.interview.default-daily-goal:10}") int defaultDailyGoal
    ) {
        this.dailyActivityRepository = dailyActivityRepository;
        this.defaultDailyGoal = defaultDailyGoal;
    }

    // AFTER_COMMIT, а не @EventListener: слушатель издаётся внутри @Transactional
    // submitAnswer/submitUnknown/submitFlashcardGrade. Синхронный @EventListener
    // выполнялся бы в ТОЙ ЖЕ TX, и SQL-сбой здесь переводил бы её в aborted (25P02) —
    // try/catch глотает Java-исключение, но финальный COMMIT ответа падал бы и весь
    // ответ (включая review_state) откатывался. Пост-коммит-фаза начисляет streak
    // только за уже закоммиченный ответ и физически не может отравить его TX.
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onAnswer(AnswerEvent event) {
        try {
            dailyActivityRepository.incrementToday(event.isCorrect(), defaultDailyGoal);
        } catch (Exception e) {
            // Логируем полный exception (class + message + cause), иначе SQL-ошибки
            // вроде «bad grammar» теряются за обобщённым WARN и streak молча не
            // апдейтится — пользователь видит «не выучил ничего сегодня» при
            // успешных ответах.
            log.warn("Не удалось обновить daily_activity [{}]: {}",
                    e.getClass().getSimpleName(), e.getMessage(), e);
        }
    }

    /**
     * Возвращает прогресс за сегодня и streak.
     */
    public StreakResponse getTodayProgress() {
        DailyProgress today = dailyActivityRepository.findToday();

        if (today == null) {
            return new StreakResponse(0, defaultDailyGoal, 0, false, 0, RussianPlural.days(0));
        }
        int streak = dailyActivityRepository.findStreak().currentStreak();
        return new StreakResponse(
                today.questionsAnswered(),
                today.goal(),
                streak,
                today.goalReached(),
                today.correctCount(),
                RussianPlural.days(streak)
        );
    }

    /**
     * Обновляет цель.
     */
    public void updateDailyGoal(int goal) {
        dailyActivityRepository.updateDailyGoal(Math.max(1, goal));
    }
}
