package com.cheatsheet.quiz.feature.interview.dto.response.answer;

import lombok.Builder;

/**
 * DTO связанного вопроса для API.
 */
@Builder(toBuilder = true)
public record RelatedQuestionDto(long id, String text, String topic) {}
