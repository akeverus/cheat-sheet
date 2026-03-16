package com.cheatsheet.quiz.api.dto.request.interview;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Параметры запроса подсказки.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Accessors(chain = true)
public class HintRequest {

    @Positive(message = "questionId must be greater than 0")
    private long questionId;

    @Min(value = 1, message = "level must be between 1 and 3")
    @Max(value = 3, message = "level must be between 1 and 3")
    @Builder.Default
    private Integer level = 1;

    public int levelOrDefault() {
        return level != null ? level : 1;
    }
}
