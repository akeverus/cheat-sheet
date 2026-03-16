package com.cheatsheet.quiz.api.mapper.request;

import com.cheatsheet.quiz.feature.interview.controller.InterviewSessionSupport;
import com.cheatsheet.quiz.api.dto.request.interview.SubmitAnswerRequest;
import org.springframework.stereotype.Component;

/**
 * Маппинг request DTO ответа в плоскую структуру для service-вызовов.
 */
@Component
public class MvcAnswerRequestMapper {

    /**
     * Преобразует {@link SubmitAnswerRequest} в объект передачи параметров ответа.
     */
    public InterviewSessionSupport.AnswerSubmission toSubmission(SubmitAnswerRequest request) {
        return new InterviewSessionSupport.AnswerSubmission(
                request.getQuestionId(),
                request.getOptionId(),
                request.getTopic(),
                request.getGroup(),
                request.getImportant(),
                request.getOnlyWrong(),
                request.getShuffle(),
                request.getOrdered(),
                request.getConfidence()
        );
    }
}
