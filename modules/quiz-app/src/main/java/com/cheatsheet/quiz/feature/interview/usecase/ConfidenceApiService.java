package com.cheatsheet.quiz.feature.interview.usecase;

import com.cheatsheet.quiz.feature.interview.dto.response.progress.ConfidenceResponse;
import com.cheatsheet.quiz.feature.interview.service.facade.InterviewFacade;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Use-case orchestration для API endpoint `/api/confidence`.
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConfidenceApiService {

    InterviewFacade facade;

    public ConfidenceResponse updateConfidence(long questionId, int grade) {
        facade.updateConfidence(questionId, grade);
        return new ConfidenceResponse(true, questionId, grade);
    }

    public ResponseEntity<ConfidenceResponse> toHttpResponse(long questionId, int grade) {
        return ResponseEntity.ok(updateConfidence(questionId, grade));
    }
}
