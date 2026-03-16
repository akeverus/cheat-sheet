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
 * Параметры отправки ответа на вопрос.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Accessors(chain = true)
public class SubmitAnswerRequest {

    @Positive(message = "questionId must be greater than 0")
    private long questionId;

    @Positive(message = "optionId must be greater than 0")
    private long optionId;

    private String topic;
    private String group;
    private Boolean important;
    private Boolean onlyWrong;
    private Boolean shuffle;
    private Boolean ordered;

    @Min(0) @Max(5)
    private Integer confidence;
}
