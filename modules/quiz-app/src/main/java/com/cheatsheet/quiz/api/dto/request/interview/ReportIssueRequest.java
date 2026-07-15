package com.cheatsheet.quiz.api.dto.request.interview;

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
 * <p>{@code category} принимается как строка и разбирается лениво на бэкенде
 * ({@code QuestionIssueCategory.fromString}) — неизвестное значение падает в OTHER,
 * поэтому валидация набора категорий здесь не требуется. {@code comment} опционален.</p>
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

    private String category;

    @Size(max = 2000, message = "comment must be at most 2000 characters")
    private String comment;
}
