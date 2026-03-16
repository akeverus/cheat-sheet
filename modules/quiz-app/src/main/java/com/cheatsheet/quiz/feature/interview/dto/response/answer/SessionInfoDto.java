package com.cheatsheet.quiz.feature.interview.dto.response.answer;

import lombok.Builder;

/**
 * DTO информации о сессии интервью для API.
 */
@Builder(toBuilder = true)
public record SessionInfoDto(int index, int total, int correct, int wrong, boolean finished) {}
