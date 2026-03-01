package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.api.dto.response.StreakResponse;
import org.springframework.stereotype.Service;

/**
 * Use-case orchestration для API endpoint `/api/streak`.
 */
@Service
public class StreakApiService {

    private final DailyStreakService dailyStreakService;

    public StreakApiService(DailyStreakService dailyStreakService) {
        this.dailyStreakService = dailyStreakService;
    }

    public StreakResponse buildStreakResponse() {
        return dailyStreakService.getTodayProgress();
    }
}
