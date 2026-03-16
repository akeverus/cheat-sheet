package com.cheatsheet.quiz.feature.interview.dto.response.insight;

import lombok.Builder;

/**
 * Ответ API с пошаговым trace выполнения кода.
 */
@Builder(toBuilder = true)
public record CodeTraceResponse(String trace) {}
