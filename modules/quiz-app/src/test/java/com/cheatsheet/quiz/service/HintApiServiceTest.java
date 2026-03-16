package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.feature.interview.dto.response.insight.HintResponse;
import com.cheatsheet.quiz.feature.interview.service.facade.InterviewFacade;
import com.cheatsheet.quiz.feature.interview.usecase.HintApiService;
import com.cheatsheet.quiz.domain.Hint;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HintApiServiceTest {

    @Mock
    private InterviewFacade facade;

    private HintApiService service;

    @BeforeEach
    void setUp() {
        service = new HintApiService(facade);
    }

    @Test
    void buildHintResponseReturnsHintPayloadWhenFacadeProvidesHint() {
        long questionId = 601L;
        HintApiService.HintCommand command = new HintApiService.HintCommand(questionId, 2);
        when(facade.getHint(questionId, 2))
                .thenReturn(Optional.of(new Hint(1L, questionId, 2, "Смотри на порядок stream-операций", 1_700_000_000L)));

        HintResponse body = service.buildHintResponse(command);

        assertThat(body.questionId()).isEqualTo(questionId);
        assertThat(body.maxLevel()).isEqualTo(3);
        assertThat(body.level()).isEqualTo(2);
        assertThat(body.hint()).contains("stream");
        verify(facade).ensureQuestionExists(questionId);
        verify(facade).getHint(questionId, 2);
    }

    @Test
    void buildHintResponseReturnsEmptyPayloadWhenFacadeReturnsEmpty() {
        long questionId = 602L;
        HintApiService.HintCommand command = new HintApiService.HintCommand(questionId, 1);
        when(facade.getHint(questionId, 1)).thenReturn(Optional.empty());

        HintResponse body = service.buildHintResponse(command);

        assertThat(body.questionId()).isEqualTo(questionId);
        assertThat(body.maxLevel()).isEqualTo(3);
        assertThat(body.level()).isNull();
        assertThat(body.hint()).isNull();
        verify(facade).getHint(questionId, 1);
    }

    @Test
    void buildHintResponsePropagatesQuestionNotFound() {
        long questionId = 603L;
        HintApiService.HintCommand command = new HintApiService.HintCommand(questionId, 1);
        doThrow(new QuestionNotFoundException("Вопрос не найден: id=" + questionId))
                .when(facade).ensureQuestionExists(questionId);

        assertThatThrownBy(() -> service.buildHintResponse(command))
                .isInstanceOf(QuestionNotFoundException.class)
                .hasMessageContaining("Вопрос не найден: id=" + questionId);
        verify(facade, never()).getHint(questionId, 1);
    }

    @Test
    void buildHintResponseClampsRequestedLevelToAllowedRange() {
        long questionId = 604L;
        HintApiService.HintCommand command = new HintApiService.HintCommand(questionId, 99);
        when(facade.getHint(questionId, 3))
                .thenReturn(Optional.of(new Hint(2L, questionId, 3, "Почти ответ", 1_700_000_001L)));

        HintResponse body = service.buildHintResponse(command);

        assertThat(body.level()).isEqualTo(3);
        assertThat(body.hint()).contains("Почти ответ");
        verify(facade).getHint(questionId, 3);
    }

    @Test
    void toHttpResponseWrapsHintPayloadWithOkStatus() {
        long questionId = 605L;
        HintApiService.HintCommand command = new HintApiService.HintCommand(questionId, 2);
        when(facade.getHint(questionId, 2))
                .thenReturn(Optional.of(new Hint(3L, questionId, 2, "Подсказка", 1_700_000_002L)));

        ResponseEntity<HintResponse> response = service.toHttpResponse(command);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().questionId()).isEqualTo(questionId);
        assertThat(response.getBody().hint()).isEqualTo("Подсказка");
    }
}
