package com.cheatsheet.quiz.feature.interview.usecase;

import com.cheatsheet.quiz.feature.interview.dto.response.insight.CodeTraceResponse;
import com.cheatsheet.quiz.feature.interview.dto.response.insight.ComparisonResponse;
import com.cheatsheet.quiz.feature.interview.dto.response.insight.TakeawayResponse;
import com.cheatsheet.quiz.feature.interview.dto.response.insight.WrongFeedbackResponse;
import com.cheatsheet.quiz.feature.interview.service.facade.InterviewFacade;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Use-case orchestration для question-insights API endpoint-ов.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuestionInsightsApiService {

    InterviewFacade facade;

    public WrongFeedbackResponse buildWrongFeedbackResponse(long questionId, long optionId) {
        Optional<String> feedback = resolveInsight(questionId, () -> facade.getWrongFeedback(questionId, optionId));
        if (feedback.isPresent()) {
            log.info("wrong_feedback_generated questionId={} optionId={}", questionId, optionId);
        } else {
            log.info("wrong_feedback_unavailable questionId={} optionId={}", questionId, optionId);
        }
        return new WrongFeedbackResponse(questionId, optionId, feedback.orElse(null), feedback.isPresent());
    }

    public ResponseEntity<WrongFeedbackResponse> toWrongFeedbackHttpResponse(long questionId, long optionId) {
        return ResponseEntity.ok(buildWrongFeedbackResponse(questionId, optionId));
    }

    public TakeawayResponse buildTakeawayResponse(long questionId) {
        Optional<String> takeaway = resolveInsight(questionId, () -> facade.getTakeaway(questionId));
        return new TakeawayResponse(takeaway.orElse(null));
    }

    public ResponseEntity<TakeawayResponse> toTakeawayHttpResponse(long questionId) {
        return ResponseEntity.ok(buildTakeawayResponse(questionId));
    }

    public ComparisonResponse buildComparisonResponse(long questionId, long selectedOptionId) {
        Optional<String> comparison = resolveInsight(
                questionId,
                () -> facade.generateComparison(questionId, selectedOptionId)
        );
        return new ComparisonResponse(comparison.orElse(null));
    }

    public ResponseEntity<ComparisonResponse> toComparisonHttpResponse(long questionId, long selectedOptionId) {
        return ResponseEntity.ok(buildComparisonResponse(questionId, selectedOptionId));
    }

    public CodeTraceResponse buildCodeTraceResponse(long questionId) {
        Optional<String> trace = resolveInsight(questionId, () -> facade.getCodeTrace(questionId));
        return new CodeTraceResponse(trace.orElse(null));
    }

    public ResponseEntity<CodeTraceResponse> toCodeTraceHttpResponse(long questionId) {
        return ResponseEntity.ok(buildCodeTraceResponse(questionId));
    }

    private Optional<String> resolveInsight(long questionId, Supplier<Optional<String>> loader) {
        facade.ensureQuestionExists(questionId);
        return loader.get();
    }
}
