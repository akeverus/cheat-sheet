package com.cheatsheet.quiz.feature.interview.dto.response.progress;

import lombok.Builder;

/**
 * Ответ API с данными о дневной цели и стрике (GET /api/streak).
 *
 * <p>{@code streakLabel} — готовый русский лейбл серии («5 дней», «21 день»),
 * просклонённый на сервере ({@link com.cheatsheet.quiz.domain.RussianPlural}),
 * чтобы клиент не дублировал плюрализацию в JS.</p>
 */
@Builder(toBuilder = true)
public record StreakResponse(
        int today,
        int goal,
        int streak,
        boolean goalReached,
        int correct,
        String streakLabel
) {}
