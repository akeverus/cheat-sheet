package com.cheatsheet.quiz.api.dto.request.interview;

import com.cheatsheet.quiz.api.validation.ValidIssueCategory;
import com.cheatsheet.quiz.common.constants.QuizConstants;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Запрос «Сообщить о проблеме» с вопросом (POST /api/report, FLOW-REPORT).
 *
 * <p>{@code category}: {@code null}/пусто → OTHER на сервере; неизвестное значение
 * отклоняется валидацией (BE-005). {@code comment} опционален.</p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Accessors(chain = true)
public class ReportIssueRequest {

    @Positive(message = "questionId must be greater than 0")
    private long questionId;

    @ValidIssueCategory
    private String category;

    @Size(max = 2000, message = "comment must be at most 2000 characters")
    private String comment;
}
