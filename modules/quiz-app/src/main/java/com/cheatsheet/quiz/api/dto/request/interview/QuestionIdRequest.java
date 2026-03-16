package com.cheatsheet.quiz.api.dto.request.interview;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Параметры запросов, где требуется только questionId.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Accessors(chain = true)
public class QuestionIdRequest {

    @Positive(message = "questionId must be greater than 0")
    private long questionId;
}
