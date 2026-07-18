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
 * Тело запроса REST-сабмита попытки {@code POST /api/sessions/{sessionId}/attempts}
 * (хендофф-3, Этап 9). Каноничная REST-форма поверх той же бизнес-логики, что и
 * {@code POST /api/answer} — контроллер делегирует в {@code AnswerApiService},
 * поэтому идемпотентность ({@code clientAttemptId}), начисление XP и расчёт
 * следующего интервала работают одинаково.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Accessors(chain = true)
public class SubmitAttemptRequest {

    @Positive(message = "questionId must be greater than 0")
    private long questionId;

    /** Ревизия контента, на которой отвечали (опционально, для полноты контракта). */
    private Long questionRevisionId;

    @Positive(message = "selectedOptionId must be greater than 0")
    private long selectedOptionId;

    /** Время ответа, измеренное клиентом, мс (опционально). */
    private Integer responseTimeMs;

    @Min(0) @Max(5)
    private Integer confidence;

    /** Клиентский ключ идемпотентности (см. {@link SubmitAnswerRequest#getClientAttemptId()}). */
    private String clientAttemptId;

    /** Comma-separated id раскрытых перед ответом блоков контента (телеметрия). */
    private String openedContentBlockIds;
}
