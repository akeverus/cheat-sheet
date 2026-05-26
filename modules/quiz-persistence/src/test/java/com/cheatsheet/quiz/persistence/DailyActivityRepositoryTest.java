package com.cheatsheet.quiz.persistence;

import com.cheatsheet.quiz.domain.DailyProgress;
import com.cheatsheet.quiz.domain.StreakInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration-тесты {@link DailyActivityRepository} на реальном PostgreSQL.
 * Раньше использовался ON CONFLICT, который ломался в Testcontainers PG 16
 * («bad SQL grammar»). Переписано на UPDATE-then-INSERT — теперь работает
 * на любых RDBMS и покрыто этим тестом.
 */
class DailyActivityRepositoryTest extends AbstractPostgresRepositoryTest {

    private DailyActivityRepository repository;

    @BeforeEach
    void initRepository() {
        repository = new DailyActivityRepository(jdbcTemplate);
    }

    @Test
    void findTodayReturnsNullWhenNoActivityRecorded() {
        assertThat(repository.findToday()).isNull();
    }

    @Test
    void incrementTodayCreatesRowOnFirstCallAndUpdatesOnSecond() {
        repository.incrementToday(true, 10);
        DailyProgress after1 = repository.findToday();
        assertThat(after1).isNotNull();
        assertThat(after1.questionsAnswered()).isEqualTo(1);
        assertThat(after1.correctCount()).isEqualTo(1);
        assertThat(after1.goal()).isEqualTo(10);

        repository.incrementToday(false, 999); // goal на UPDATE не меняется
        DailyProgress after2 = repository.findToday();
        assertThat(after2.questionsAnswered()).isEqualTo(2);
        assertThat(after2.correctCount()).isEqualTo(1);
        assertThat(after2.goal()).isEqualTo(10);
    }

    @Test
    void updateDailyGoalCreatesEmptyRowWhenNoneAndOverwritesGoalOtherwise() {
        repository.updateDailyGoal(15);
        DailyProgress after1 = repository.findToday();
        assertThat(after1.questionsAnswered()).isZero();
        assertThat(after1.goal()).isEqualTo(15);

        repository.incrementToday(true, 999);
        repository.updateDailyGoal(20);
        DailyProgress after2 = repository.findToday();
        assertThat(after2.questionsAnswered()).isEqualTo(1);
        assertThat(after2.goal()).isEqualTo(20);
    }

    @Test
    void findStreakReturnsZeroWhenNoTodayActivity() {
        StreakInfo info = repository.findStreak();
        assertThat(info.currentStreak()).isZero();
    }

    @Test
    void findStreakCountsConsecutiveDaysIncludingToday() {
        DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;
        LocalDate today = LocalDate.now();
        repository.incrementToday(true, 10);
        for (int i = 1; i <= 3; i++) {
            jdbcTemplate.update(
                    "INSERT INTO daily_activity (activity_date, questions_answered, correct_count, daily_goal) " +
                            "VALUES (?, 1, 1, 10)",
                    today.minusDays(i).format(fmt));
        }
        jdbcTemplate.update(
                "INSERT INTO daily_activity (activity_date, questions_answered, correct_count, daily_goal) " +
                        "VALUES (?, 1, 1, 10)",
                today.minusDays(5).format(fmt));

        StreakInfo info = repository.findStreak();
        assertThat(info.currentStreak()).isEqualTo(4); // today + 3 предыдущих
    }
}
