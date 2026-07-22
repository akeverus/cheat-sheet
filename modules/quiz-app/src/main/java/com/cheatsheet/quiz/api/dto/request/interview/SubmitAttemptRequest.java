package com.cheatsheet.quiz.api.dto.request.interview;

import com.cheatsheet.quiz.common.constants.QuizConstants;
import com.cheatsheet.quiz.common.constants.ValidationMessages;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Тело запроса REST-сабмита попытки {@code POST /api/attempts} (BE-003, модель B).
 * Каноничная REST-форма поверх той же бизнес-логики, что и {@code POST /api/answer}.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Accessors(chain = true)
public class SubmitAttemptRequest {

    @Positive(message = ValidationMessages.QUESTION_ID_POSITIVE)
    private long questionId;

    /** Ревизия контента, на которой отвечали (опционально, для полноты контракта). */
    @Positive(message = ValidationMessages.QUESTION_REVISION_ID_POSITIVE)
    private Long questionRevisionId;

    @Positive(message = "selectedOptionId must be greater than 0")
    private long selectedOptionId;

    /** Время ответа, измеренное клиентом, мс (опционально). */
    @Min(0)
    @Max(QuizConstants.RESPONSE_TIME_MS_MAX)
    private Integer responseTimeMs;

    @Min(0) @Max(5)
    private Integer confidence;

    /** Клиентский ключ идемпотентности (см. {@link SubmitAnswerRequest#getClientAttemptId()}). */
    @Size(max = QuizConstants.CLIENT_ATTEMPT_ID_MAX_LENGTH,
            message = ValidationMessages.CLIENT_ATTEMPT_ID_MAX)
    private String clientAttemptId;

    /** Comma-separated id раскрытых перед ответом блоков контента (телеметрия). */
    @Size(max = QuizConstants.OPENED_CONTENT_BLOCK_IDS_MAX_LENGTH,
            message = ValidationMessages.OPENED_CONTENT_BLOCK_IDS_MAX)
    private String openedContentBlockIds;
}
