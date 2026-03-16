package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.feature.interview.dto.response.progress.StreakResponse;
import com.cheatsheet.quiz.feature.interview.service.progress.DailyStreakService;
import com.cheatsheet.quiz.domain.DailyProgress;
import com.cheatsheet.quiz.domain.StreakInfo;
import com.cheatsheet.quiz.persistence.DailyActivityRepository;
import com.cheatsheet.quiz.service.event.AnswerEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DailyStreakServiceTest {

    @Mock
    DailyActivityRepository dailyActivityRepository;

    private DailyStreakService service;

    @BeforeEach
    void setUp() {
        service = new DailyStreakService(dailyActivityRepository, 10);
    }

    @Test
    void getTodayProgressReturnsDefaultsWhenNoActivity() {
        when(dailyActivityRepository.findToday()).thenReturn(null);

        StreakResponse result = service.getTodayProgress();

        assertThat(result.today()).isEqualTo(0);
        assertThat(result.goal()).isEqualTo(10);
        assertThat(result.streak()).isEqualTo(0);
        assertThat(result.goalReached()).isFalse();
        assertThat(result.correct()).isEqualTo(0);
    }

    @Test
    void getTodayProgressReturnsActivityData() {
        DailyProgress progress = new DailyProgress(5, 3, 10);
        when(dailyActivityRepository.findToday()).thenReturn(progress);
        when(dailyActivityRepository.findStreak()).thenReturn(new StreakInfo(7));

        StreakResponse result = service.getTodayProgress();

        assertThat(result.today()).isEqualTo(5);
        assertThat(result.goal()).isEqualTo(10);
        assertThat(result.streak()).isEqualTo(7);
        assertThat(result.goalReached()).isFalse();
        assertThat(result.correct()).isEqualTo(3);
    }

    @Test
    void getTodayProgressWhenGoalReached() {
        DailyProgress progress = new DailyProgress(10, 8, 10);
        when(dailyActivityRepository.findToday()).thenReturn(progress);
        when(dailyActivityRepository.findStreak()).thenReturn(new StreakInfo(3));

        StreakResponse result = service.getTodayProgress();

        assertThat(result.goalReached()).isTrue();
        assertThat(result.today()).isEqualTo(10);
    }

    @Test
    void onAnswerIncrementsToday() {
        AnswerEvent event = new AnswerEvent(this, 1L, 2L, 3L, true,
                "text", "sel", "cor", "md", "topic");

        service.onAnswer(event);

        verify(dailyActivityRepository).incrementToday(true, 10);
    }

    @Test
    void onAnswerHandlesExceptionGracefully() {
        AnswerEvent event = new AnswerEvent(this, 1L, 2L, 3L, false,
                "text", "sel", "cor", "md", "topic");
        doThrow(new RuntimeException("DB down")).when(dailyActivityRepository).incrementToday(false, 10);

        service.onAnswer(event);

        verify(dailyActivityRepository).incrementToday(false, 10);
    }

    @Test
    void updateDailyGoalEnforcesMinimumOne() {
        service.updateDailyGoal(0);
        verify(dailyActivityRepository).updateDailyGoal(1);

        service.updateDailyGoal(-5);
        verify(dailyActivityRepository, times(2)).updateDailyGoal(1);

        service.updateDailyGoal(15);
        verify(dailyActivityRepository).updateDailyGoal(15);
    }
}
