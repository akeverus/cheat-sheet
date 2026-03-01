package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.api.dto.response.CodeTraceResponse;
import com.cheatsheet.quiz.api.dto.response.ComparisonResponse;
import com.cheatsheet.quiz.api.dto.response.TakeawayResponse;
import com.cheatsheet.quiz.api.dto.response.WrongFeedbackResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Use-case orchestration для question-insights API endpoint-ов.
 */
@Service
@Slf4j
public class QuestionInsightsApiService {

    private final InterviewFacade facade;

    public QuestionInsightsApiService(InterviewFacade facade) {
        this.facade = facade;
    }

    public WrongFeedbackResponse buildWrongFeedbackResponse(long questionId, long optionId) {
        facade.ensureQuestionExists(questionId);
        Optional<String> feedback = facade.getWrongFeedback(questionId, optionId);
        if (feedback.isPresent()) {
            log.info("wrong_feedback_generated questionId={} optionId={}", questionId, optionId);
        } else {
            log.info("wrong_feedback_unavailable questionId={} optionId={}", questionId, optionId);
        }
        return new WrongFeedbackResponse(questionId, optionId, feedback.orElse(null), feedback.isPresent());
    }

    public TakeawayResponse buildTakeawayResponse(long questionId) {
        facade.ensureQuestionExists(questionId);
        Optional<String> takeaway = facade.getTakeaway(questionId);
        return new TakeawayResponse(takeaway.orElse(null));
    }

    public ComparisonResponse buildComparisonResponse(long questionId, long selectedOptionId) {
        facade.ensureQuestionExists(questionId);
        Optional<String> comparison = facade.generateComparison(questionId, selectedOptionId);
        return new ComparisonResponse(comparison.orElse(null));
    }

    public CodeTraceResponse buildCodeTraceResponse(long questionId) {
        facade.ensureQuestionExists(questionId);
        Optional<String> trace = facade.getCodeTrace(questionId);
        return new CodeTraceResponse(trace.orElse(null));
    }
}
