package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.api.dto.response.ConfidenceResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Use-case orchestration для API endpoint `/api/confidence`.
 */
@Service
public class ConfidenceApiService {

    private final InterviewFacade facade;

    public ConfidenceApiService(InterviewFacade facade) {
        this.facade = facade;
    }

    public ConfidenceResponse updateConfidence(long questionId, int grade) {
        facade.updateConfidence(questionId, grade);
        return new ConfidenceResponse(true, questionId, grade);
    }

    public ResponseEntity<ConfidenceResponse> toHttpResponse(long questionId, int grade) {
        return ResponseEntity.ok(updateConfidence(questionId, grade));
    }
}
