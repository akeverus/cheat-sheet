package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.api.dto.response.ConfidenceResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ConfidenceApiServiceTest {

    @Mock
    private InterviewFacade facade;

    private ConfidenceApiService service;

    @BeforeEach
    void setUp() {
        service = new ConfidenceApiService(facade);
    }

    @Test
    void updateConfidenceDelegatesToFacadeAndBuildsResponse() {
        long questionId = 201L;
        int grade = 5;

        ConfidenceResponse response = service.updateConfidence(questionId, grade);

        assertThat(response.success()).isTrue();
        assertThat(response.questionId()).isEqualTo(questionId);
        assertThat(response.grade()).isEqualTo(grade);
        verify(facade).updateConfidence(questionId, grade);
    }

    @Test
    void toHttpResponseWrapsConfidencePayloadWithOkStatus() {
        long questionId = 202L;
        int grade = 4;

        ResponseEntity<ConfidenceResponse> response = service.toHttpResponse(questionId, grade);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isTrue();
        assertThat(response.getBody().questionId()).isEqualTo(questionId);
        assertThat(response.getBody().grade()).isEqualTo(grade);
        verify(facade).updateConfidence(questionId, grade);
    }
}
