package com.cheatsheet.quiz.persistence;

import com.cheatsheet.quiz.domain.DailyProgress;
import com.cheatsheet.quiz.domain.StreakInfo;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Репозиторий для таблицы {@code daily_activity}.
 *
 * <p>Хранит ежедневную статистику: количество ответов, правильных ответов,
 * цель на день. Используется для подсчёта streak и прогресса.</p>
 */
@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DailyActivityRepository {

    JdbcTemplate jdbcTemplate;

    static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    /**
     * Инкрементирует счётчик ответов на сегодня.
     * При первом вызове за день создаёт запись.
     * Использует атомарный upsert (SQLite и PostgreSQL совместимый).
     */
    public void incrementToday(boolean correct, int defaultGoal) {
        String today = LocalDate.now().format(DATE_FMT);
        int correctInc = correct ? 1 : 0;

        jdbcTemplate.update(
                "INSERT INTO daily_activity (activity_date, questions_answered, correct_count, daily_goal) " +
                        "VALUES (?, 1, ?, ?) " +
                        "ON CONFLICT(activity_date) DO UPDATE SET " +
                        "questions_answered = questions_answered + 1, " +
                        "correct_count = correct_count + excluded.correct_count",
                today, correctInc, defaultGoal
        );
    }

    /**
     * Возвращает данные за сегодня или null если записи нет.
     */
    public DailyProgress findToday() {
        String today = LocalDate.now().format(DATE_FMT);
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT activity_date, questions_answered, correct_count, daily_goal " +
                        "FROM daily_activity WHERE activity_date = ?", today);
        if (rows.isEmpty()) {
            return null;
        }
        Map<String, Object> row = rows.get(0);
        int questionsAnswered = ((Number) row.get("questions_answered")).intValue();
        int correctCount = ((Number) row.get("correct_count")).intValue();
        int goal = ((Number) row.get("daily_goal")).intValue();
        return new DailyProgress(questionsAnswered, correctCount, goal);
    }

    /**
     * Подсчитывает текущую серию дней (streak).
     * Считает подряд идущие дни назад от вчера, у которых questions_answered > 0.
     * Сегодняшний день считается, если есть ответы.
     */
    public StreakInfo findStreak() {
        LocalDate date = LocalDate.now();
        int streak = 0;

        // Проверяем сегодня
        DailyProgress today = findToday();
        if (today != null && today.questionsAnswered() > 0) {
            streak = 1;
        } else {
            return new StreakInfo(0);
        }

        // Считаем дни назад
        for (int i = 1; i <= 365; i++) {
            String dateStr = date.minusDays(i).format(DATE_FMT);
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                    "SELECT questions_answered FROM daily_activity WHERE activity_date = ?", dateStr);
            if (rows.isEmpty() || ((Number) rows.get(0).get("questions_answered")).intValue() == 0) {
                break;
            }
            streak++;
        }
        return new StreakInfo(streak);
    }

    /**
     * Обновляет цель на сегодня.
     */
    public void updateDailyGoal(int goal) {
        String today = LocalDate.now().format(DATE_FMT);
        jdbcTemplate.update(
                "INSERT INTO daily_activity (activity_date, questions_answered, correct_count, daily_goal) " +
                        "VALUES (?, 0, 0, ?) " +
                        "ON CONFLICT(activity_date) DO UPDATE SET daily_goal = excluded.daily_goal",
                today, goal);
    }
}
