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
 * Репозиторий {@code daily_activity}, scoped by {@link OwnerContext}.
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
     */
    public void incrementToday(boolean correct, int defaultGoal) {
        String today = LocalDate.now().format(DATE_FMT);
        String owner = OwnerContext.current();
        int correctInc = correct ? 1 : 0;

        // Атомарный upsert (ср. BookmarkRepository): прежняя схема UPDATE→(если 0)→INSERT
        // проигрывала гонку на ПЕРВОМ ответе дня — два параллельных сабмита оба видели
        // updated=0 и второй INSERT падал на daily_activity_pkey (owner_key, activity_date),
        // роняя сам сабмит ответа в 500. ON CONFLICT инкрементирует существующую строку.
        jdbcTemplate.update(
                "INSERT INTO daily_activity (owner_key, activity_date, questions_answered, correct_count, daily_goal) " +
                        "VALUES (?, ?, 1, ?, ?) " +
                        "ON CONFLICT (owner_key, activity_date) DO UPDATE SET " +
                        "questions_answered = daily_activity.questions_answered + 1, " +
                        "correct_count = daily_activity.correct_count + EXCLUDED.correct_count",
                owner, today, correctInc, defaultGoal
        );
    }

    /**
     * Возвращает данные за сегодня или null если записи нет.
     */
    public DailyProgress findToday() {
        String today = LocalDate.now().format(DATE_FMT);
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT activity_date, questions_answered, correct_count, daily_goal " +
                        "FROM daily_activity WHERE activity_date = ? AND owner_key = ?",
                today, OwnerContext.current());
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
     * Подсчитывает текущую серию дней (streak) для текущего владельца.
     */
    public StreakInfo findStreak() {
        LocalDate date = LocalDate.now();
        int streak = 0;

        DailyProgress today = findToday();
        if (today != null && today.questionsAnswered() > 0) {
            streak = 1;
        } else {
            return new StreakInfo(0);
        }

        String owner = OwnerContext.current();
        for (int i = 1; i <= 365; i++) {
            String dateStr = date.minusDays(i).format(DATE_FMT);
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                    "SELECT questions_answered FROM daily_activity WHERE activity_date = ? AND owner_key = ?",
                    dateStr, owner);
            if (rows.isEmpty() || ((Number) rows.get(0).get("questions_answered")).intValue() == 0) {
                break;
            }
            streak++;
        }
        return new StreakInfo(streak);
    }

    /**
     * Активность за последние {@code days} дней (включая сегодня) для тренда точности.
     * Дни без записей не включаются — UI заполняет пропуски нулями при необходимости.
     *
     * @param days горизонт (обычно 14)
     */
    public List<DayAccuracy> findRecentDays(int days) {
        int safeDays = Math.max(1, Math.min(days, 90));
        LocalDate from = LocalDate.now().minusDays(safeDays - 1L);
        return jdbcTemplate.query(
                "SELECT activity_date, questions_answered, correct_count " +
                        "FROM daily_activity " +
                        "WHERE owner_key = ? AND activity_date >= ? " +
                        "ORDER BY activity_date ASC",
                (rs, rowNum) -> new DayAccuracy(
                        rs.getString("activity_date"),
                        rs.getInt("questions_answered"),
                        rs.getInt("correct_count")),
                OwnerContext.current(),
                from.format(DATE_FMT));
    }

    /**
     * День активности: дата ISO, ответов всего, верных.
     */
    public record DayAccuracy(String day, int answered, int correct) {}

    /**
     * Обновляет цель на сегодня.
     */
    public void updateDailyGoal(int goal) {
        String today = LocalDate.now().format(DATE_FMT);
        String owner = OwnerContext.current();
        // Атомарный upsert (ср. BookmarkRepository): UPDATE→(если 0)→INSERT проигрывал
        // гонку с daily_activity_pkey; ON CONFLICT делает всё одной операцией, не трогая
        // накопленные счётчики (обновляется только daily_goal).
        jdbcTemplate.update(
                "INSERT INTO daily_activity (owner_key, activity_date, questions_answered, correct_count, daily_goal) " +
                        "VALUES (?, ?, 0, 0, ?) " +
                        "ON CONFLICT (owner_key, activity_date) DO UPDATE SET daily_goal = EXCLUDED.daily_goal",
                owner, today, goal);
    }
}
