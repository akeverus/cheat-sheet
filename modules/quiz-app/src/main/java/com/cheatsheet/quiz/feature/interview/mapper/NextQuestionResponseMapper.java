package com.cheatsheet.quiz.feature.interview.mapper;

import com.cheatsheet.quiz.domain.InterviewQuestion;
import com.cheatsheet.quiz.feature.interview.dto.response.NextQuestionOptionDto;
import com.cheatsheet.quiz.feature.interview.dto.response.NextQuestionResponse;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Маппер доменной модели вопроса в DTO ответа endpoint `/api/next`.
 */
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NextQuestionResponseMapper {

    private static final String DEFAULT_QUESTION_TYPE = "TEXT";

    public NextQuestionResponse toResponse(InterviewQuestion question) {
        List<NextQuestionOptionDto> options = question.options().stream()
                .map(option -> new NextQuestionOptionDto(option.id(), option.optionText()))
                .toList();
        String resolvedQuestionType = question.question().questionType() == null
                ? DEFAULT_QUESTION_TYPE
                : question.question().questionType().name();
        return new NextQuestionResponse(
                question.question().id(),
                question.question().questionText(),
                question.question().topic(),
                resolvedQuestionType,
                question.question().codeSnippet(),
                question.question().diagramMermaid(),
                options,
                question.reviewState().repetitions(),
                question.reviewState().correctCount(),
                question.reviewState().wrongCount()
        );
    }
}
