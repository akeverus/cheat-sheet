package com.cheatsheet.quiz.feature.interview.dto.response.insight;

import lombok.Builder;

/**
 * Ответ API со сравнительной таблицей при неправильном ответе.
 */
@Builder(toBuilder = true)
public record ComparisonResponse(String comparison) {}
