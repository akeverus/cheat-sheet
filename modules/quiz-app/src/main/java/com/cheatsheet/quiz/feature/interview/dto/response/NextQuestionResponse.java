package com.cheatsheet.quiz.feature.interview.dto.response;

import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public record NextQuestionResponse(
        long questionId,
        String questionText,
        String topic,
        String questionType,
        String codeSnippet,
        String diagramMermaid,
        List<NextQuestionOptionDto> options,
        long repetitions,
        int correctCount,
        int wrongCount
) {
}
