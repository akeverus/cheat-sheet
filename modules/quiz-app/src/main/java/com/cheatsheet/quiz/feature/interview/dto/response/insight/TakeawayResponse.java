package com.cheatsheet.quiz.feature.interview.dto.response.insight;

import lombok.Builder;

/**
 * Ответ API с Key Takeaway для вопроса.
 */
@Builder(toBuilder = true)
public record TakeawayResponse(String takeaway) {}
