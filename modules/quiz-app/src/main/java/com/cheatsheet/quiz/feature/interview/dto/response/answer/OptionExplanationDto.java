package com.cheatsheet.quiz.feature.interview.dto.response.answer;

import lombok.Builder;

/**
 * DTO объяснения варианта ответа для API.
 *
 * <p>{@code explanationHtml} — уже отрендеренный из markdown и санитайзенный
 * HTML (см. {@code MarkdownRenderService}); клиент вставляет его как fragment,
 * аналогично {@code AnswerResponse.answerHtml}.</p>
 */
@Builder(toBuilder = true)
public record OptionExplanationDto(long id, String explanationHtml, boolean correct) {}
