package com.cheatsheet.quiz.feature.interview.usecase.stats;

import com.cheatsheet.quiz.feature.interview.dto.response.progress.StreakResponse;
import com.cheatsheet.quiz.feature.interview.service.progress.DailyStreakService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Use-case orchestration для API endpoints `/api/streak` и `/api/streak/goal`.
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StreakApiService {

    DailyStreakService dailyStreakService;

    public StreakResponse buildStreakResponse() {
        return dailyStreakService.getTodayProgress();
    }

    public ResponseEntity<StreakResponse> toHttpResponse() {
        return ResponseEntity.ok(buildStreakResponse());
    }

    /**
     * Обновляет дневную цель и возвращает актуальный streak-снимок.
     */
    public ResponseEntity<StreakResponse> updateGoalToHttpResponse(int goal) {
        dailyStreakService.updateDailyGoal(goal);
        return ResponseEntity.ok(buildStreakResponse());
    }
}
