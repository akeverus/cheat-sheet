package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.api.dto.response.CodeTraceResponse;
import com.cheatsheet.quiz.api.dto.response.ComparisonResponse;
import com.cheatsheet.quiz.api.dto.response.TakeawayResponse;
import com.cheatsheet.quiz.api.dto.response.WrongFeedbackResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Supplier;

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
        Optional<String> feedback = resolveInsight(questionId, () -> facade.getWrongFeedback(questionId, optionId));
        if (feedback.isPresent()) {
            log.info("wrong_feedback_generated questionId={} optionId={}", questionId, optionId);
        } else {
            log.info("wrong_feedback_unavailable questionId={} optionId={}", questionId, optionId);
        }
        return new WrongFeedbackResponse(questionId, optionId, feedback.orElse(null), feedback.isPresent());
    }

    public TakeawayResponse buildTakeawayResponse(long questionId) {
        Optional<String> takeaway = resolveInsight(questionId, () -> facade.getTakeaway(questionId));
        return new TakeawayResponse(takeaway.orElse(null));
    }

    public ComparisonResponse buildComparisonResponse(long questionId, long selectedOptionId) {
        Optional<String> comparison = resolveInsight(
                questionId,
                () -> facade.generateComparison(questionId, selectedOptionId)
        );
        return new ComparisonResponse(comparison.orElse(null));
    }

    public CodeTraceResponse buildCodeTraceResponse(long questionId) {
        Optional<String> trace = resolveInsight(questionId, () -> facade.getCodeTrace(questionId));
        return new CodeTraceResponse(trace.orElse(null));
    }

    private Optional<String> resolveInsight(long questionId, Supplier<Optional<String>> loader) {
        facade.ensureQuestionExists(questionId);
        return loader.get();
    }
}
