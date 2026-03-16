package com.cheatsheet.quiz.feature.interview.dto.response;

import lombok.Builder;

@Builder(toBuilder = true)
public record NextQuestionOptionDto(
        long id,
        String optionText
) {
}
