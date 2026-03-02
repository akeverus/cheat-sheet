package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.api.dto.response.CodeTraceResponse;
import com.cheatsheet.quiz.api.dto.response.ComparisonResponse;
import com.cheatsheet.quiz.api.dto.response.TakeawayResponse;
import com.cheatsheet.quiz.api.dto.response.WrongFeedbackResponse;
import com.cheatsheet.quiz.domain.exception.QuestionNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuestionInsightsApiServiceTest {

    @Mock
    private InterviewFacade facade;

    private QuestionInsightsApiService service;

    @BeforeEach
    void setUp() {
        service = new QuestionInsightsApiService(facade);
    }

    @Test
    void wrongFeedbackBuildsAvailableResponse() {
        long questionId = 101L;
        long optionId = 7L;
        when(facade.getWrongFeedback(questionId, optionId)).thenReturn(Optional.of("Причина ошибки"));

        WrongFeedbackResponse response = service.buildWrongFeedbackResponse(questionId, optionId);

        assertThat(response.questionId()).isEqualTo(questionId);
        assertThat(response.optionId()).isEqualTo(optionId);
        assertThat(response.feedback()).isEqualTo("Причина ошибки");
        assertThat(response.available()).isTrue();
        verify(facade).ensureQuestionExists(questionId);
    }

    @Test
    void wrongFeedbackPropagatesQuestionNotFound() {
        long questionId = 102L;
        long optionId = 8L;
        doThrow(new QuestionNotFoundException("Вопрос не найден: id=" + questionId))
                .when(facade).ensureQuestionExists(questionId);

        assertThatThrownBy(() -> service.buildWrongFeedbackResponse(questionId, optionId))
                .isInstanceOf(QuestionNotFoundException.class)
                .hasMessageContaining("id=" + questionId);
        verify(facade, never()).getWrongFeedback(questionId, optionId);
    }

    @Test
    void takeawayBuildsResponseWithNullWhenMissing() {
        long questionId = 801L;
        when(facade.getTakeaway(questionId)).thenReturn(Optional.empty());

        TakeawayResponse response = service.buildTakeawayResponse(questionId);

        assertThat(response.takeaway()).isNull();
        verify(facade).ensureQuestionExists(questionId);
    }

    @Test
    void comparisonBuildsResponse() {
        long questionId = 901L;
        long selectedOptionId = 11L;
        when(facade.generateComparison(questionId, selectedOptionId))
                .thenReturn(Optional.of("Ваш ответ игнорирует edge-case с null."));

        ComparisonResponse response = service.buildComparisonResponse(questionId, selectedOptionId);

        assertThat(response.comparison()).contains("edge-case");
        verify(facade).ensureQuestionExists(questionId);
    }

    @Test
    void codeTraceBuildsResponse() {
        long questionId = 1001L;
        when(facade.getCodeTrace(questionId)).thenReturn(Optional.of("Трассировка: i=0 -> i=1 -> return 42"));

        CodeTraceResponse response = service.buildCodeTraceResponse(questionId);

        assertThat(response.trace()).contains("return 42");
        verify(facade).ensureQuestionExists(questionId);
    }

    @Test
    void comparisonAndCodeTraceReturnNullPayloadWhenUnavailable() {
        long questionId = 1201L;
        long optionId = 15L;
        when(facade.generateComparison(questionId, optionId)).thenReturn(Optional.empty());
        when(facade.getCodeTrace(questionId)).thenReturn(Optional.empty());

        ComparisonResponse comparison = service.buildComparisonResponse(questionId, optionId);
        CodeTraceResponse trace = service.buildCodeTraceResponse(questionId);

        assertThat(comparison.comparison()).isNull();
        assertThat(trace.trace()).isNull();
        verify(facade, times(2)).ensureQuestionExists(questionId);
    }

    @Test
    void toHttpResponseMethodsWrapPayloadWithOkStatus() {
        long questionId = 1301L;
        long optionId = 21L;
        when(facade.getWrongFeedback(questionId, optionId)).thenReturn(Optional.of("reason"));
        when(facade.getTakeaway(questionId)).thenReturn(Optional.of("takeaway"));
        when(facade.generateComparison(questionId, optionId)).thenReturn(Optional.of("comparison"));
        when(facade.getCodeTrace(questionId)).thenReturn(Optional.of("trace"));

        ResponseEntity<WrongFeedbackResponse> wrong = service.toWrongFeedbackHttpResponse(questionId, optionId);
        ResponseEntity<TakeawayResponse> takeaway = service.toTakeawayHttpResponse(questionId);
        ResponseEntity<ComparisonResponse> comparison = service.toComparisonHttpResponse(questionId, optionId);
        ResponseEntity<CodeTraceResponse> trace = service.toCodeTraceHttpResponse(questionId);

        assertThat(wrong.getStatusCode().value()).isEqualTo(200);
        assertThat(takeaway.getStatusCode().value()).isEqualTo(200);
        assertThat(comparison.getStatusCode().value()).isEqualTo(200);
        assertThat(trace.getStatusCode().value()).isEqualTo(200);
    }
}
