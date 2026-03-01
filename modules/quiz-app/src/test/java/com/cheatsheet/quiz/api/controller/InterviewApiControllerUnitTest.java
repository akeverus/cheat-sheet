package com.cheatsheet.quiz.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cheatsheet.quiz.api.dto.ApiError;
import com.cheatsheet.quiz.api.exception.ApiErrorTypes;
import com.cheatsheet.quiz.api.dto.request.HintRequest;
import com.cheatsheet.quiz.api.dto.request.QuestionIdRequest;
import com.cheatsheet.quiz.api.dto.request.SubmitAnswerRequest;
import com.cheatsheet.quiz.api.mapper.ApiRequestMapper;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.domain.exception.QuestionNotFoundException;
import com.cheatsheet.quiz.service.AnswerApiService;
import com.cheatsheet.quiz.service.ConfidenceApiService;
import com.cheatsheet.quiz.service.FavoriteApiService;
import com.cheatsheet.quiz.service.HintApiService;
import com.cheatsheet.quiz.service.InterviewFacade;
import com.cheatsheet.quiz.service.NextQuestionApiService;
import com.cheatsheet.quiz.service.QuestionInsightsApiService;
import com.cheatsheet.quiz.service.RegenerateEndpointService;
import com.cheatsheet.quiz.service.StatsApiService;
import com.cheatsheet.quiz.service.StreakApiService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class InterviewApiControllerUnitTest {

    @Mock private AnswerApiService answerApiService;
    @Mock private HintApiService hintApiService;
    @Mock private ConfidenceApiService confidenceApiService;
    @Mock private InterviewFacade facade;
    @Mock private NextQuestionApiService nextQuestionApiService;
    @Mock private QuestionInsightsApiService questionInsightsApiService;
    @Mock private RegenerateEndpointService regenerateEndpointService;
    @Mock private FavoriteApiService favoriteApiService;
    @Mock private StreakApiService streakApiService;
    @Mock private StatsApiService statsApiService;

    private InterviewApiController controller;

    @BeforeEach
    void setUp() {
        controller = new InterviewApiController(
                answerApiService,
                hintApiService,
                confidenceApiService,
                nextQuestionApiService,
                questionInsightsApiService,
                regenerateEndpointService,
                favoriteApiService,
                streakApiService,
                statsApiService,
                new ApiRequestMapper()
        );
    }

    @Test
    void wrongFeedbackReturnsPayloadWhenServiceProvidesFeedback() {
        long questionId = 101L;
        long optionId = 7L;
        com.cheatsheet.quiz.api.dto.response.WrongFeedbackResponse payload =
                new com.cheatsheet.quiz.api.dto.response.WrongFeedbackResponse(questionId, optionId, "Причина ошибки", true);
        when(questionInsightsApiService.buildWrongFeedbackResponse(questionId, optionId)).thenReturn(payload);

        ResponseEntity<?> response = controller.getWrongAnswerFeedback(questionId, optionId);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isInstanceOf(com.cheatsheet.quiz.api.dto.response.WrongFeedbackResponse.class);
        com.cheatsheet.quiz.api.dto.response.WrongFeedbackResponse body =
                (com.cheatsheet.quiz.api.dto.response.WrongFeedbackResponse) response.getBody();
        assertThat(body.questionId()).isEqualTo(questionId);
        assertThat(body.optionId()).isEqualTo(optionId);
        assertThat(body.feedback()).isEqualTo("Причина ошибки");
        assertThat(body.available()).isTrue();
        verify(questionInsightsApiService).buildWrongFeedbackResponse(questionId, optionId);
    }

    @Test
    void wrongFeedbackReturnsUnavailablePayloadWhenServiceProvidesEmptyFeedback() {
        long questionId = 102L;
        long optionId = 999999L;
        com.cheatsheet.quiz.api.dto.response.WrongFeedbackResponse payload =
                new com.cheatsheet.quiz.api.dto.response.WrongFeedbackResponse(questionId, optionId, null, false);
        when(questionInsightsApiService.buildWrongFeedbackResponse(questionId, optionId)).thenReturn(payload);

        ResponseEntity<?> response = controller.getWrongAnswerFeedback(questionId, optionId);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isInstanceOf(com.cheatsheet.quiz.api.dto.response.WrongFeedbackResponse.class);
        com.cheatsheet.quiz.api.dto.response.WrongFeedbackResponse body =
                (com.cheatsheet.quiz.api.dto.response.WrongFeedbackResponse) response.getBody();
        assertThat(body.questionId()).isEqualTo(questionId);
        assertThat(body.optionId()).isEqualTo(optionId);
        assertThat(body.feedback()).isNull();
        assertThat(body.available()).isFalse();
        verify(questionInsightsApiService).buildWrongFeedbackResponse(questionId, optionId);
    }

    @Test
    void wrongFeedbackPropagatesQuestionNotFoundFromService() {
        long questionId = 103L;
        long optionId = 1L;
        doThrow(new QuestionNotFoundException("Вопрос не найден: id=" + questionId))
                .when(questionInsightsApiService).buildWrongFeedbackResponse(questionId, optionId);

        assertThatThrownBy(() -> controller.getWrongAnswerFeedback(questionId, optionId))
                .isInstanceOf(QuestionNotFoundException.class)
                .hasMessageContaining("Вопрос не найден: id=" + questionId);
        verify(questionInsightsApiService).buildWrongFeedbackResponse(questionId, optionId);
    }

    @Test
    void updateConfidenceReturnsSuccessAndDelegatesToService() {
        long questionId = 201L;
        int grade = 5;
        com.cheatsheet.quiz.api.dto.response.ConfidenceResponse payload =
                new com.cheatsheet.quiz.api.dto.response.ConfidenceResponse(true, questionId, grade);
        when(confidenceApiService.updateConfidence(questionId, grade)).thenReturn(payload);

        ResponseEntity<?> response = controller.updateConfidence(questionId, grade);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isInstanceOf(com.cheatsheet.quiz.api.dto.response.ConfidenceResponse.class);
        com.cheatsheet.quiz.api.dto.response.ConfidenceResponse body =
                (com.cheatsheet.quiz.api.dto.response.ConfidenceResponse) response.getBody();
        assertThat(body.success()).isTrue();
        assertThat(body.questionId()).isEqualTo(questionId);
        assertThat(body.grade()).isEqualTo(grade);
        verify(confidenceApiService).updateConfidence(questionId, grade);
    }

    @Test
    void regenerateReturnsForbiddenWhenTokenIsUnauthorized() {
        QuestionIdRequest request = new QuestionIdRequest();
        request.setQuestionId(55L);
        HttpServletRequest httpRequest = org.mockito.Mockito.mock(HttpServletRequest.class);
        String token = "bad-token";
        ApiError forbidden = new ApiError(403, ApiErrorTypes.FORBIDDEN, "Недостаточно прав", null);
        when(regenerateEndpointService.execute(request.getQuestionId(), token, httpRequest))
                .thenReturn(RegenerateEndpointService.RegenerateResult.forbidden(forbidden));

        ResponseEntity<?> response = controller.regenerateOptions(request, token, httpRequest);

        assertThat(response.getStatusCode().value()).isEqualTo(403);
        assertThat(response.getBody()).isEqualTo(forbidden);
        verify(regenerateEndpointService).execute(request.getQuestionId(), token, httpRequest);
    }

    @Test
    void hintReturnsHintPayloadWhenServiceProvidesHint() {
        long questionId = 601L;
        HintRequest request = new HintRequest();
        request.setQuestionId(questionId);
        request.setLevel(2);
        com.cheatsheet.quiz.api.dto.response.HintResponse payload =
                new com.cheatsheet.quiz.api.dto.response.HintResponse(
                        questionId, 3, "Смотри на порядок stream-операций", 2
                );
        when(hintApiService.buildHintResponse(any())).thenReturn(payload);

        ResponseEntity<?> response = controller.getHint(request);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isInstanceOf(com.cheatsheet.quiz.api.dto.response.HintResponse.class);
        com.cheatsheet.quiz.api.dto.response.HintResponse body =
                (com.cheatsheet.quiz.api.dto.response.HintResponse) response.getBody();
        assertThat(body.questionId()).isEqualTo(questionId);
        assertThat(body.maxLevel()).isEqualTo(3);
        assertThat(body.level()).isEqualTo(2);
        assertThat(body.hint()).contains("stream");
        verify(hintApiService).buildHintResponse(any());
    }

    @Test
    void hintReturnsEmptyPayloadWhenServiceReturnsEmptyPayload() {
        long questionId = 602L;
        HintRequest request = new HintRequest();
        request.setQuestionId(questionId);
        com.cheatsheet.quiz.api.dto.response.HintResponse payload =
                new com.cheatsheet.quiz.api.dto.response.HintResponse(questionId, 3, null, null);
        when(hintApiService.buildHintResponse(any())).thenReturn(payload);

        ResponseEntity<?> response = controller.getHint(request);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isInstanceOf(com.cheatsheet.quiz.api.dto.response.HintResponse.class);
        com.cheatsheet.quiz.api.dto.response.HintResponse body =
                (com.cheatsheet.quiz.api.dto.response.HintResponse) response.getBody();
        assertThat(body.questionId()).isEqualTo(questionId);
        assertThat(body.maxLevel()).isEqualTo(3);
        assertThat(body.level()).isNull();
        assertThat(body.hint()).isNull();
        verify(hintApiService).buildHintResponse(any());
    }

    @Test
    void hintPropagatesQuestionNotFoundFromService() {
        long questionId = 603L;
        HintRequest request = new HintRequest();
        request.setQuestionId(questionId);
        doThrow(new QuestionNotFoundException("Вопрос не найден: id=" + questionId)).when(hintApiService)
                .buildHintResponse(any());

        assertThatThrownBy(() -> controller.getHint(request))
                .isInstanceOf(QuestionNotFoundException.class)
                .hasMessageContaining("Вопрос не найден: id=" + questionId);
        verify(hintApiService).buildHintResponse(any());
    }

    @Test
    void answerApiDelegatesToAnswerApiServiceAndReturnsPayload() {
        long questionId = 701L;
        long selectedOptionId = 2L;
        SubmitAnswerRequest request = new SubmitAnswerRequest();
        request.setQuestionId(questionId);
        request.setOptionId(selectedOptionId);
        request.setTopic("java");
        request.setGroup("core");
        request.setImportant(true);
        request.setOnlyWrong(false);
        request.setShuffle(true);
        request.setOrdered(false);
        request.setConfidence(4);
        HttpSession httpSession = org.mockito.Mockito.mock(HttpSession.class);
        com.cheatsheet.quiz.api.dto.response.AnswerResponse payload =
                new com.cheatsheet.quiz.api.dto.response.AnswerResponse(
                        true,
                        2L,
                        2L,
                        "<p>Ответ</p>",
                        List.of(new com.cheatsheet.quiz.api.dto.response.OptionExplanationDto(2L, "Верно", true)),
                        "MINIMAL",
                        3,
                        List.of(new com.cheatsheet.quiz.api.dto.response.RelatedQuestionDto(900L, "Похожий вопрос", "java")),
                        new com.cheatsheet.quiz.api.dto.response.SessionInfoDto(1, 2, 1, 0, false)
                );
        when(answerApiService.buildAnswerResponse(any(), org.mockito.ArgumentMatchers.eq(httpSession))).thenReturn(payload);

        ResponseEntity<?> response = controller.answerApi(request, httpSession);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isInstanceOf(com.cheatsheet.quiz.api.dto.response.AnswerResponse.class);
        com.cheatsheet.quiz.api.dto.response.AnswerResponse body =
                (com.cheatsheet.quiz.api.dto.response.AnswerResponse) response.getBody();
        assertThat(body.correct()).isTrue();
        assertThat(body.correctOptionId()).isEqualTo(2L);
        assertThat(body.selectedOptionId()).isEqualTo(2L);
        assertThat(body.answerHtml()).isEqualTo("<p>Ответ</p>");
        assertThat(body.optionExplanations()).hasSize(1);
        assertThat(body.answerDisplayMode()).isEqualTo("MINIMAL");
        assertThat(body.repetitions()).isEqualTo(3);
        assertThat(body.relatedQuestions()).hasSize(1);
        assertThat(body.session()).isNotNull();
        assertThat(body.session().total()).isEqualTo(2);
        verify(answerApiService).buildAnswerResponse(any(), org.mockito.ArgumentMatchers.eq(httpSession));
    }

    @Test
    void regenerateReturnsRateLimitErrorWhenLimiterRejectsRequest() {
        QuestionIdRequest request = new QuestionIdRequest();
        request.setQuestionId(77L);
        HttpServletRequest httpRequest = org.mockito.Mockito.mock(HttpServletRequest.class);
        String token = "admin-token";
        int retryAfterSeconds = 60;
        ApiError error = new ApiError(429, ApiErrorTypes.RATE_LIMIT_EXCEEDED,
                "Слишком много запросов к /api/regenerate, повторите позже",
                java.util.Map.of("retryAfterSeconds", (long) retryAfterSeconds));

        when(regenerateEndpointService.execute(request.getQuestionId(), token, httpRequest))
                .thenReturn(RegenerateEndpointService.RegenerateResult.rateLimited(error, retryAfterSeconds));

        ResponseEntity<?> response = controller.regenerateOptions(request, token, httpRequest);

        assertThat(response.getStatusCode().value()).isEqualTo(429);
        assertThat(response.getHeaders().getFirst("Retry-After")).isEqualTo(String.valueOf(retryAfterSeconds));
        assertThat(response.getBody()).isInstanceOf(ApiError.class);
        ApiError body = (ApiError) response.getBody();
        assertThat(body.type()).isEqualTo(ApiErrorTypes.RATE_LIMIT_EXCEEDED);
        assertThat(body.status()).isEqualTo(429);
        assertThat(body.details()).isEqualTo(java.util.Map.of("retryAfterSeconds", (long) retryAfterSeconds));
        verify(regenerateEndpointService).execute(request.getQuestionId(), token, httpRequest);
    }

    @Test
    void regenerateReturnsSuccessWhenAuthorizedAndLimiterAllows() {
        QuestionIdRequest request = new QuestionIdRequest();
        request.setQuestionId(88L);
        HttpServletRequest httpRequest = org.mockito.Mockito.mock(HttpServletRequest.class);
        String token = "admin-token";
        com.cheatsheet.quiz.api.dto.response.RegenerateResponse payload =
                new com.cheatsheet.quiz.api.dto.response.RegenerateResponse(
                        true,
                        request.getQuestionId(),
                        "Варианты, подсказки и диаграмма удалены. При следующем показе будут сгенерированы заново."
                );

        when(regenerateEndpointService.execute(request.getQuestionId(), token, httpRequest))
                .thenReturn(RegenerateEndpointService.RegenerateResult.success(payload));

        ResponseEntity<?> response = controller.regenerateOptions(request, token, httpRequest);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isInstanceOf(com.cheatsheet.quiz.api.dto.response.RegenerateResponse.class);
        com.cheatsheet.quiz.api.dto.response.RegenerateResponse body =
                (com.cheatsheet.quiz.api.dto.response.RegenerateResponse) response.getBody();
        assertThat(body.success()).isTrue();
        assertThat(body.questionId()).isEqualTo(request.getQuestionId());
        assertThat(body.message()).isNotBlank();
        verify(regenerateEndpointService).execute(request.getQuestionId(), token, httpRequest);
    }

    @Test
    void nextReturnsNoContentWhenServiceHasNoQuestion() {
        when(nextQuestionApiService.getNextQuestion(any())).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.getNextQuestion(
                null, null, null, null, null, null, null, null
        );

        assertThat(response.getStatusCode().value()).isEqualTo(204);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void nextReturnsQuestionPayloadWhenServiceReturnsQuestion() {
        long questionId = 303L;
        Question question = existingQuestion(questionId);
        com.cheatsheet.quiz.api.dto.response.NextQuestionResponse payload =
                new com.cheatsheet.quiz.api.dto.response.NextQuestionResponse(
                        questionId,
                        question.questionText(),
                        question.topic(),
                        "TEXT",
                        null,
                        null,
                        List.of(
                                new com.cheatsheet.quiz.api.dto.response.NextQuestionOptionDto(1L, "A"),
                                new com.cheatsheet.quiz.api.dto.response.NextQuestionOptionDto(2L, "B")
                        ),
                        2L,
                        4,
                        1
                );
        when(nextQuestionApiService.getNextQuestion(any())).thenReturn(Optional.of(payload));

        ResponseEntity<?> response = controller.getNextQuestion(
                null, null, null, null, null, null, null, null
        );

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isInstanceOf(com.cheatsheet.quiz.api.dto.response.NextQuestionResponse.class);
        com.cheatsheet.quiz.api.dto.response.NextQuestionResponse body =
                (com.cheatsheet.quiz.api.dto.response.NextQuestionResponse) response.getBody();
        assertThat(body.questionId()).isEqualTo(questionId);
        assertThat(body.questionText()).isEqualTo(question.questionText());
        assertThat(body.topic()).isEqualTo(question.topic());
        assertThat(body.options()).hasSize(2);
        assertThat(body.repetitions()).isEqualTo(2L);
        assertThat(body.correctCount()).isEqualTo(4);
        assertThat(body.wrongCount()).isEqualTo(1);
        verify(nextQuestionApiService).getNextQuestion(any());
    }

    @Test
    void nextPassesCommandToService() {
        when(nextQuestionApiService.getNextQuestion(any())).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.getNextQuestion(
                "  java  ",
                "  core ",
                true,
                false,
                true,
                true,
                false,
                42L
        );

        ArgumentCaptor<NextQuestionApiService.NextQuestionCommand> commandCaptor =
                ArgumentCaptor.forClass(NextQuestionApiService.NextQuestionCommand.class);
        verify(nextQuestionApiService).getNextQuestion(commandCaptor.capture());
        NextQuestionApiService.NextQuestionCommand command = commandCaptor.getValue();
        assertThat(command.topic()).isEqualTo("  java  ");
        assertThat(command.group()).isEqualTo("  core ");
        assertThat(command.important()).isTrue();
        assertThat(command.onlyWrong()).isFalse();
        assertThat(command.shuffle()).isTrue();
        assertThat(command.weakTopics()).isTrue();
        assertThat(command.ordered()).isFalse();
        assertThat(command.excludeQuestionId()).isEqualTo(42L);
        assertThat(response.getStatusCode().value()).isEqualTo(204);
    }

    @Test
    void takeawayReturnsPayloadWhenServiceProvidesText() {
        long questionId = 801L;
        com.cheatsheet.quiz.api.dto.response.TakeawayResponse payload =
                new com.cheatsheet.quiz.api.dto.response.TakeawayResponse("Сфокусируйся на неизменяемости и потокобезопасности.");
        when(questionInsightsApiService.buildTakeawayResponse(questionId)).thenReturn(payload);

        ResponseEntity<?> response = controller.getTakeaway(questionId);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isInstanceOf(com.cheatsheet.quiz.api.dto.response.TakeawayResponse.class);
        com.cheatsheet.quiz.api.dto.response.TakeawayResponse body =
                (com.cheatsheet.quiz.api.dto.response.TakeawayResponse) response.getBody();
        assertThat(body.takeaway()).contains("потокобезопасности");
        verify(questionInsightsApiService).buildTakeawayResponse(questionId);
    }

    @Test
    void takeawayReturnsNullWhenServiceReturnsEmptyPayload() {
        long questionId = 802L;
        com.cheatsheet.quiz.api.dto.response.TakeawayResponse payload =
                new com.cheatsheet.quiz.api.dto.response.TakeawayResponse(null);
        when(questionInsightsApiService.buildTakeawayResponse(questionId)).thenReturn(payload);

        ResponseEntity<?> response = controller.getTakeaway(questionId);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isInstanceOf(com.cheatsheet.quiz.api.dto.response.TakeawayResponse.class);
        com.cheatsheet.quiz.api.dto.response.TakeawayResponse body =
                (com.cheatsheet.quiz.api.dto.response.TakeawayResponse) response.getBody();
        assertThat(body.takeaway()).isNull();
        verify(questionInsightsApiService).buildTakeawayResponse(questionId);
    }

    @Test
    void takeawayPropagatesQuestionNotFoundFromService() {
        long questionId = 803L;
        doThrow(new QuestionNotFoundException("Вопрос не найден: id=" + questionId))
                .when(questionInsightsApiService).buildTakeawayResponse(questionId);

        assertThatThrownBy(() -> controller.getTakeaway(questionId))
                .isInstanceOf(QuestionNotFoundException.class)
                .hasMessageContaining("Вопрос не найден: id=" + questionId);
        verify(questionInsightsApiService).buildTakeawayResponse(questionId);
    }

    @Test
    void comparisonReturnsPayloadWhenServiceProvidesText() {
        long questionId = 901L;
        long selectedOptionId = 11L;
        com.cheatsheet.quiz.api.dto.response.ComparisonResponse payload =
                new com.cheatsheet.quiz.api.dto.response.ComparisonResponse("Ваш ответ игнорирует edge-case с null.");
        when(questionInsightsApiService.buildComparisonResponse(questionId, selectedOptionId))
                .thenReturn(payload);

        ResponseEntity<?> response = controller.getComparison(questionId, selectedOptionId);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isInstanceOf(com.cheatsheet.quiz.api.dto.response.ComparisonResponse.class);
        com.cheatsheet.quiz.api.dto.response.ComparisonResponse body =
                (com.cheatsheet.quiz.api.dto.response.ComparisonResponse) response.getBody();
        assertThat(body.comparison()).contains("edge-case");
        verify(questionInsightsApiService).buildComparisonResponse(questionId, selectedOptionId);
    }

    @Test
    void comparisonReturnsNullWhenServiceReturnsEmptyPayload() {
        long questionId = 902L;
        long selectedOptionId = 12L;
        com.cheatsheet.quiz.api.dto.response.ComparisonResponse payload =
                new com.cheatsheet.quiz.api.dto.response.ComparisonResponse(null);
        when(questionInsightsApiService.buildComparisonResponse(questionId, selectedOptionId))
                .thenReturn(payload);

        ResponseEntity<?> response = controller.getComparison(questionId, selectedOptionId);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isInstanceOf(com.cheatsheet.quiz.api.dto.response.ComparisonResponse.class);
        com.cheatsheet.quiz.api.dto.response.ComparisonResponse body =
                (com.cheatsheet.quiz.api.dto.response.ComparisonResponse) response.getBody();
        assertThat(body.comparison()).isNull();
        verify(questionInsightsApiService).buildComparisonResponse(questionId, selectedOptionId);
    }

    @Test
    void comparisonPropagatesQuestionNotFoundFromService() {
        long questionId = 903L;
        long selectedOptionId = 13L;
        doThrow(new QuestionNotFoundException("Вопрос не найден: id=" + questionId))
                .when(questionInsightsApiService).buildComparisonResponse(questionId, selectedOptionId);

        assertThatThrownBy(() -> controller.getComparison(questionId, selectedOptionId))
                .isInstanceOf(QuestionNotFoundException.class)
                .hasMessageContaining("Вопрос не найден: id=" + questionId);
        verify(questionInsightsApiService).buildComparisonResponse(questionId, selectedOptionId);
    }

    @Test
    void codeTraceReturnsPayloadWhenServiceProvidesText() {
        long questionId = 1001L;
        com.cheatsheet.quiz.api.dto.response.CodeTraceResponse payload =
                new com.cheatsheet.quiz.api.dto.response.CodeTraceResponse("Трассировка: i=0 -> i=1 -> return 42");
        when(questionInsightsApiService.buildCodeTraceResponse(questionId)).thenReturn(payload);

        ResponseEntity<?> response = controller.getCodeTrace(questionId);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isInstanceOf(com.cheatsheet.quiz.api.dto.response.CodeTraceResponse.class);
        com.cheatsheet.quiz.api.dto.response.CodeTraceResponse body =
                (com.cheatsheet.quiz.api.dto.response.CodeTraceResponse) response.getBody();
        assertThat(body.trace()).contains("return 42");
        verify(questionInsightsApiService).buildCodeTraceResponse(questionId);
    }

    @Test
    void codeTraceReturnsNullWhenServiceReturnsEmptyPayload() {
        long questionId = 1002L;
        com.cheatsheet.quiz.api.dto.response.CodeTraceResponse payload =
                new com.cheatsheet.quiz.api.dto.response.CodeTraceResponse(null);
        when(questionInsightsApiService.buildCodeTraceResponse(questionId)).thenReturn(payload);

        ResponseEntity<?> response = controller.getCodeTrace(questionId);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isInstanceOf(com.cheatsheet.quiz.api.dto.response.CodeTraceResponse.class);
        com.cheatsheet.quiz.api.dto.response.CodeTraceResponse body =
                (com.cheatsheet.quiz.api.dto.response.CodeTraceResponse) response.getBody();
        assertThat(body.trace()).isNull();
        verify(questionInsightsApiService).buildCodeTraceResponse(questionId);
    }

    @Test
    void codeTracePropagatesQuestionNotFoundFromService() {
        long questionId = 1003L;
        doThrow(new QuestionNotFoundException("Вопрос не найден: id=" + questionId))
                .when(questionInsightsApiService).buildCodeTraceResponse(questionId);

        assertThatThrownBy(() -> controller.getCodeTrace(questionId))
                .isInstanceOf(QuestionNotFoundException.class)
                .hasMessageContaining("Вопрос не найден: id=" + questionId);
        verify(questionInsightsApiService).buildCodeTraceResponse(questionId);
    }

    @Test
    void statsDelegatesToServiceAndReturnsPayload() {
        com.cheatsheet.quiz.api.dto.response.InterviewStatsResponse mapped =
                new com.cheatsheet.quiz.api.dto.response.InterviewStatsResponse(20, 5, 9, 33, 7);
        when(statsApiService.buildStatsResponse(any())).thenReturn(mapped);

        ResponseEntity<?> response = controller.getStats(
                "  java  ",
                "  core ",
                true,
                false,
                true,
                false
        );

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isEqualTo(mapped);
        verify(statsApiService).buildStatsResponse(any());
    }

    @Test
    void topicStatsDelegatesToServiceAndReturnsPayload() {
        List<com.cheatsheet.quiz.api.dto.response.TopicStatsResponse> mapped = List.of(
                new com.cheatsheet.quiz.api.dto.response.TopicStatsResponse("java", 10, 3, 4, 20, 5, 1),
                new com.cheatsheet.quiz.api.dto.response.TopicStatsResponse("spring", 8, 2, 3, 15, 4, 0)
        );
        when(statsApiService.buildTopicStatsResponse()).thenReturn(mapped);

        ResponseEntity<?> response = controller.getTopicStats();

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isEqualTo(mapped);
        verify(statsApiService).buildTopicStatsResponse();
    }

    @Test
    void streakReturnsDailyProgressFromService() {
        com.cheatsheet.quiz.api.dto.response.StreakResponse progress =
                new com.cheatsheet.quiz.api.dto.response.StreakResponse(7, 10, 3, false, 5);
        when(streakApiService.buildStreakResponse()).thenReturn(progress);

        ResponseEntity<?> response = controller.getStreak();

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isEqualTo(progress);
        verify(streakApiService).buildStreakResponse();
    }

    @Test
    void favoriteMapsServiceResultToApiDto() {
        QuestionIdRequest request = new QuestionIdRequest();
        request.setQuestionId(1101L);
        com.cheatsheet.quiz.api.dto.response.FavoriteResponse result =
                new com.cheatsheet.quiz.api.dto.response.FavoriteResponse(true, true, 1101L);
        when(favoriteApiService.toggleFavorite(1101L)).thenReturn(result);

        ResponseEntity<?> response = controller.toggleFavorite(request);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isInstanceOf(com.cheatsheet.quiz.api.dto.response.FavoriteResponse.class);
        com.cheatsheet.quiz.api.dto.response.FavoriteResponse body =
                (com.cheatsheet.quiz.api.dto.response.FavoriteResponse) response.getBody();
        assertThat(body.questionId()).isEqualTo(1101L);
        assertThat(body.favorite()).isTrue();
        assertThat(body.synced()).isTrue();
        verify(favoriteApiService).toggleFavorite(1101L);
    }

    @Test
    void favoritePropagatesQuestionNotFoundFromService() {
        QuestionIdRequest request = new QuestionIdRequest();
        request.setQuestionId(1102L);
        when(favoriteApiService.toggleFavorite(1102L))
                .thenThrow(new QuestionNotFoundException("Вопрос не найден: id=1102"));

        assertThatThrownBy(() -> controller.toggleFavorite(request))
                .isInstanceOf(QuestionNotFoundException.class)
                .hasMessageContaining("id=1102");
    }

    private Question existingQuestion(long id) {
        return existingQuestionWithType(id, QuestionType.TEXT);
    }

    private Question existingQuestionWithType(long id, QuestionType questionType) {
        return new Question(
                id,
                "q-" + id,
                "q-" + id,
                "test.md",
                "java",
                "В чем ошибка?",
                "Ответ",
                false,
                "hash",
                questionType,
                null,
                null,
                0,
                null
        );
    }
}
