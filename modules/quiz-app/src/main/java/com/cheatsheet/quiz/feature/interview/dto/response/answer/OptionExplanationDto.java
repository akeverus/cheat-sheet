package com.cheatsheet.quiz.feature.interview.dto.response.answer;

import lombok.Builder;

/**
 * DTO объяснения варианта ответа для API.
 */
@Builder(toBuilder = true)
public record OptionExplanationDto(long id, String explanation, boolean correct) {}
