package com.cheatsheet.quiz.feature.interview.dto.response.answer;

import lombok.Builder;

import java.util.List;

/**
 * Ответ API на отправку ответа (POST /api/answer).
 */
@Builder(toBuilder = true)
public record AnswerResponse(
        boolean correct,
        long correctOptionId,
        long selectedOptionId,
        String answerHtml,
        List<OptionExplanationDto> optionExplanations,
        String answerDisplayMode,
        int repetitions,
        List<RelatedQuestionDto> relatedQuestions,
        SessionInfoDto session
) {}
