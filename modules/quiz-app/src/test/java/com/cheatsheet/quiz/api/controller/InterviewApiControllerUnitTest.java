package com.cheatsheet.quiz.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cheatsheet.quiz.api.dto.ApiError;
import com.cheatsheet.quiz.api.dto.request.HintRequest;
import com.cheatsheet.quiz.api.dto.request.QuestionIdRequest;
import com.cheatsheet.quiz.api.dto.request.SubmitAnswerRequest;
import com.cheatsheet.quiz.api.exception.ApiErrorTypes;
import com.cheatsheet.quiz.api.mapper.StatsApiMapper;
import com.cheatsheet.quiz.api.security.SensitiveEndpointAccessService;
import com.cheatsheet.quiz.domain.AnswerDisplayMode;
import com.cheatsheet.quiz.domain.AnswerOption;
import com.cheatsheet.quiz.domain.AnswerResult;
import com.cheatsheet.quiz.domain.Hint;
import com.cheatsheet.quiz.domain.InterviewQuestion;
import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.domain.InterviewMode;
import com.cheatsheet.quiz.domain.InterviewSession;
import com.cheatsheet.quiz.domain.InterviewStats;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.domain.RelatedQuestion;
import com.cheatsheet.quiz.domain.ReviewResult;
import com.cheatsheet.quiz.domain.ReviewState;
import com.cheatsheet.quiz.domain.TopicStats;
import com.cheatsheet.quiz.domain.exception.QuestionNotFoundException;
import com.cheatsheet.quiz.service.DailyStreakService;
import com.cheatsheet.quiz.service.FavoriteService;
import com.cheatsheet.quiz.service.InterviewFacade;
import com.cheatsheet.quiz.service.RegenerateService;
import java.util.List;
import java.util.Optional;
import java.util.Map;
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

    @Mock private InterviewSessionSupport sessionSupport;
    @Mock private InterviewFacade facade;
    @Mock private RegenerateService regenerateService;
    @Mock private FavoriteService favoriteService;
    @Mock private SensitiveEndpointAccessService accessService;
    @Mock private DailyStreakService dailyStreakService;
    @Mock private StatsApiMapper statsApiMapper;

    private InterviewApiController controller;

    @BeforeEach
    void setUp() {
        controller = new InterviewApiController(
                sessionSupport,
                facade,
                regenerateService,
                favoriteService,
                accessService,
                dailyStreakService,
                statsApiMapper
        );
    }

    @Test
    void wrongFeedbackMarksAvailableWhenFacadeReturnsFeedback() {
        long questionId = 101L;
        long optionId = 7L;
        when(facade.getWrongFeedback(questionId, optionId)).thenReturn(Optional.of("Причина ошибки"));

        ResponseEntity<?> response = controller.getWrongAnswerFeedback(questionId, optionId);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isInstanceOf(com.cheatsheet.quiz.api.dto.response.WrongFeedbackResponse.class);
        com.cheatsheet.quiz.api.dto.response.WrongFeedbackResponse body =
                (com.cheatsheet.quiz.api.dto.response.WrongFeedbackResponse) response.getBody();
        assertThat(body.questionId()).isEqualTo(questionId);
        assertThat(body.optionId()).isEqualTo(optionId);
        assertThat(body.feedback()).isEqualTo("Причина ошибки");
        assertThat(body.available()).isTrue();
        verify(facade).getWrongFeedback(questionId, optionId);
    }

    @Test
    void wrongFeedbackMarksUnavailableWhenFacadeReturnsEmpty() {
        long questionId = 102L;
        long optionId = 999999L;
        when(facade.getWrongFeedback(questionId, optionId)).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.getWrongAnswerFeedback(questionId, optionId);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isInstanceOf(com.cheatsheet.quiz.api.dto.response.WrongFeedbackResponse.class);
        com.cheatsheet.quiz.api.dto.response.WrongFeedbackResponse body =
                (com.cheatsheet.quiz.api.dto.response.WrongFeedbackResponse) response.getBody();
        assertThat(body.questionId()).isEqualTo(questionId);
        assertThat(body.optionId()).isEqualTo(optionId);
        assertThat(body.feedback()).isNull();
        assertThat(body.available()).isFalse();
        verify(facade).getWrongFeedback(questionId, optionId);
    }

    @Test
    void wrongFeedbackThrowsWhenQuestionIsMissing() {
        long questionId = 103L;
        long optionId = 1L;
        doThrow(new QuestionNotFoundException("Вопрос не найден: id=" + questionId))
                .when(facade).ensureQuestionExists(questionId);

        assertThatThrownBy(() -> controller.getWrongAnswerFeedback(questionId, optionId))
                .isInstanceOf(QuestionNotFoundException.class)
                .hasMessageContaining("Вопрос не найден: id=" + questionId);
        verify(facade, never()).getWrongFeedback(questionId, optionId);
    }

    @Test
    void updateConfidenceReturnsSuccessAndDelegatesToFacade() {
        long questionId = 201L;
        int grade = 5;

        ResponseEntity<?> response = controller.updateConfidence(questionId, grade);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isInstanceOf(com.cheatsheet.quiz.api.dto.response.ConfidenceResponse.class);
        com.cheatsheet.quiz.api.dto.response.ConfidenceResponse body =
                (com.cheatsheet.quiz.api.dto.response.ConfidenceResponse) response.getBody();
        assertThat(body.success()).isTrue();
        assertThat(body.questionId()).isEqualTo(questionId);
        assertThat(body.grade()).isEqualTo(grade);
        verify(facade).updateConfidence(questionId, grade);
    }

    @Test
    void regenerateReturnsForbiddenWhenTokenIsUnauthorized() {
        QuestionIdRequest request = new QuestionIdRequest();
        request.setQuestionId(55L);
        HttpServletRequest httpRequest = org.mockito.Mockito.mock(HttpServletRequest.class);
        String token = "bad-token";
        ApiError forbidden = new ApiError(403, ApiErrorTypes.FORBIDDEN, "Недостаточно прав", null);
        ResponseEntity<ApiError> forbiddenResponse = ResponseEntity.status(403).body(forbidden);

        when(accessService.isAuthorized(token)).thenReturn(false);
        when(accessService.buildForbiddenResponse(token)).thenReturn(forbiddenResponse);

        ResponseEntity<?> response = controller.regenerateOptions(request, token, httpRequest);

        assertThat(response.getStatusCode().value()).isEqualTo(403);
        assertThat(response.getBody()).isEqualTo(forbidden);
        verify(accessService).isAuthorized(token);
        verify(accessService).buildForbiddenResponse(token);
        verify(regenerateService, never()).regenerateQuestion(request.getQuestionId());
    }

    @Test
    void hintReturnsHintPayloadWhenFacadeProvidesHint() {
        long questionId = 601L;
        HintRequest request = new HintRequest();
        request.setQuestionId(questionId);
        request.setLevel(2);
        when(facade.getHint(questionId, 2)).thenReturn(Optional.of(new Hint(1L, questionId, 2, "Смотри на порядок stream-операций", 1_700_000_000L)));

        ResponseEntity<?> response = controller.getHint(request);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isInstanceOf(com.cheatsheet.quiz.api.dto.response.HintResponse.class);
        com.cheatsheet.quiz.api.dto.response.HintResponse body =
                (com.cheatsheet.quiz.api.dto.response.HintResponse) response.getBody();
        assertThat(body.questionId()).isEqualTo(questionId);
        assertThat(body.maxLevel()).isEqualTo(3);
        assertThat(body.level()).isEqualTo(2);
        assertThat(body.hint()).contains("stream");
    }

    @Test
    void hintReturnsEmptyPayloadWhenFacadeReturnsEmpty() {
        long questionId = 602L;
        HintRequest request = new HintRequest();
        request.setQuestionId(questionId);
        when(facade.getHint(questionId, 1)).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.getHint(request);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isInstanceOf(com.cheatsheet.quiz.api.dto.response.HintResponse.class);
        com.cheatsheet.quiz.api.dto.response.HintResponse body =
                (com.cheatsheet.quiz.api.dto.response.HintResponse) response.getBody();
        assertThat(body.questionId()).isEqualTo(questionId);
        assertThat(body.maxLevel()).isEqualTo(3);
        assertThat(body.level()).isNull();
        assertThat(body.hint()).isNull();
    }

    @Test
    void hintThrowsWhenQuestionIsMissing() {
        long questionId = 603L;
        HintRequest request = new HintRequest();
        request.setQuestionId(questionId);
        doThrow(new QuestionNotFoundException("Вопрос не найден: id=" + questionId))
                .when(facade).ensureQuestionExists(questionId);

        assertThatThrownBy(() -> controller.getHint(request))
                .isInstanceOf(QuestionNotFoundException.class)
                .hasMessageContaining("Вопрос не найден: id=" + questionId);
        verify(facade, never()).getHint(questionId, 1);
    }

    @Test
    void answerApiMapsResponseAndDelegatesToSupportAndFacade() {
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

        Question question = existingQuestion(questionId);
        AnswerOption optionA = new AnswerOption(1L, questionId, "A", false, 0, "OPENAI", "Неверно");
        AnswerOption optionB = new AnswerOption(2L, questionId, "B", true, 1, "OPENAI", "Верно");
        ReviewState reviewState = new ReviewState(questionId, 3, 3, 2.5, 1_700_000_000L, ReviewResult.CORRECT, 5, 2);
        AnswerResult answerResult = new AnswerResult(
                question,
                List.of(optionA, optionB),
                optionB,
                optionB,
                true,
                reviewState,
                AnswerDisplayMode.MINIMAL
        );
        InterviewSession interviewSession = new InterviewSession(
                InterviewMode.TRAINING,
                List.of(questionId, 702L),
                "java",
                "core",
                true,
                false,
                true,
                false
        );
        InterviewSessionSupport.AnswerContext context = new InterviewSessionSupport.AnswerContext(
                answerResult,
                new InterviewFilter("java", "core", true, false, true, false),
                interviewSession
        );
        when(sessionSupport.processAnswer(
                questionId,
                selectedOptionId,
                "java",
                "core",
                true,
                false,
                true,
                false,
                4,
                httpSession
        )).thenReturn(context);
        when(facade.findRelated(questionId, "java"))
                .thenReturn(List.of(new RelatedQuestion(900L, "Похожий вопрос", "java", 75.0)));
        when(facade.renderMarkdown(question.answerMarkdown())).thenReturn("<p>Ответ</p>");

        ResponseEntity<?> response = controller.answerApi(request, httpSession);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isInstanceOf(com.cheatsheet.quiz.api.dto.response.AnswerResponse.class);
        com.cheatsheet.quiz.api.dto.response.AnswerResponse body =
                (com.cheatsheet.quiz.api.dto.response.AnswerResponse) response.getBody();
        assertThat(body.correct()).isTrue();
        assertThat(body.correctOptionId()).isEqualTo(2L);
        assertThat(body.selectedOptionId()).isEqualTo(2L);
        assertThat(body.answerHtml()).isEqualTo("<p>Ответ</p>");
        assertThat(body.optionExplanations()).hasSize(2);
        assertThat(body.answerDisplayMode()).isEqualTo("MINIMAL");
        assertThat(body.repetitions()).isEqualTo(3);
        assertThat(body.relatedQuestions()).hasSize(1);
        assertThat(body.session()).isNotNull();
        assertThat(body.session().total()).isEqualTo(2);
        verify(sessionSupport).processAnswer(
                questionId,
                selectedOptionId,
                "java",
                "core",
                true,
                false,
                true,
                false,
                4,
                httpSession
        );
        verify(facade).findRelated(questionId, "java");
        verify(facade).renderMarkdown(question.answerMarkdown());
    }

    @Test
    void regenerateReturnsRateLimitErrorWhenLimiterRejectsRequest() {
        QuestionIdRequest request = new QuestionIdRequest();
        request.setQuestionId(77L);
        HttpServletRequest httpRequest = org.mockito.Mockito.mock(HttpServletRequest.class);
        String token = "admin-token";
        int retryAfterSeconds = 60;

        when(accessService.isAuthorized(token)).thenReturn(true);
        when(accessService.allowRegenerate(httpRequest)).thenReturn(false);
        when(accessService.regenerateRetryAfterSeconds()).thenReturn(retryAfterSeconds);

        ResponseEntity<?> response = controller.regenerateOptions(request, token, httpRequest);

        assertThat(response.getStatusCode().value()).isEqualTo(429);
        assertThat(response.getHeaders().getFirst("Retry-After")).isEqualTo(String.valueOf(retryAfterSeconds));
        assertThat(response.getBody()).isInstanceOf(ApiError.class);
        ApiError body = (ApiError) response.getBody();
        assertThat(body.type()).isEqualTo(ApiErrorTypes.RATE_LIMIT_EXCEEDED);
        assertThat(body.status()).isEqualTo(429);
        assertThat(body.details()).isEqualTo(Map.of("retryAfterSeconds", (long) retryAfterSeconds));
        verify(accessService).isAuthorized(token);
        verify(accessService).allowRegenerate(httpRequest);
        verify(accessService).regenerateRetryAfterSeconds();
        verify(regenerateService, never()).regenerateQuestion(request.getQuestionId());
    }

    @Test
    void regenerateReturnsSuccessWhenAuthorizedAndLimiterAllows() {
        QuestionIdRequest request = new QuestionIdRequest();
        request.setQuestionId(88L);
        HttpServletRequest httpRequest = org.mockito.Mockito.mock(HttpServletRequest.class);
        String token = "admin-token";

        when(accessService.isAuthorized(token)).thenReturn(true);
        when(accessService.allowRegenerate(httpRequest)).thenReturn(true);

        ResponseEntity<?> response = controller.regenerateOptions(request, token, httpRequest);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isInstanceOf(com.cheatsheet.quiz.api.dto.response.RegenerateResponse.class);
        com.cheatsheet.quiz.api.dto.response.RegenerateResponse body =
                (com.cheatsheet.quiz.api.dto.response.RegenerateResponse) response.getBody();
        assertThat(body.success()).isTrue();
        assertThat(body.questionId()).isEqualTo(request.getQuestionId());
        assertThat(body.message()).isNotBlank();
        verify(accessService).isAuthorized(token);
        verify(accessService).allowRegenerate(httpRequest);
        verify(regenerateService).regenerateQuestion(request.getQuestionId());
    }

    @Test
    void nextReturnsNoContentWhenFacadeHasNoQuestion() {
        when(facade.nextQuestion(
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.eq(false),
                org.mockito.ArgumentMatchers.isNull()
        )).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.getNextQuestion(
                null, null, null, null, null, null, null, null
        );

        assertThat(response.getStatusCode().value()).isEqualTo(204);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void nextReturnsQuestionPayloadWhenFacadeReturnsQuestion() {
        long questionId = 303L;
        Question question = existingQuestion(questionId);
        AnswerOption optionA = new AnswerOption(1L, questionId, "A", false, 0, "OPENAI", "Неверно");
        AnswerOption optionB = new AnswerOption(2L, questionId, "B", true, 1, "OPENAI", "Верно");
        ReviewState reviewState = new ReviewState(questionId, 2, 3, 2.5, 1_700_000_000L, ReviewResult.CORRECT, 4, 1);
        InterviewQuestion interviewQuestion = new InterviewQuestion(question, List.of(optionA, optionB), reviewState);
        when(facade.nextQuestion(
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.eq(false),
                org.mockito.ArgumentMatchers.isNull()
        )).thenReturn(Optional.of(interviewQuestion));

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
    }

    @Test
    void nextFallsBackToTextTypeWhenQuestionTypeIsNull() {
        long questionId = 404L;
        Question question = existingQuestionWithType(questionId, null);
        AnswerOption option = new AnswerOption(1L, questionId, "A", true, 0, "OPENAI", "Верно");
        ReviewState reviewState = new ReviewState(questionId, 0, 0, 2.5, 1_700_000_000L, ReviewResult.NEW, 0, 0);
        InterviewQuestion interviewQuestion = new InterviewQuestion(question, List.of(option), reviewState);
        when(facade.nextQuestion(
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.eq(false),
                org.mockito.ArgumentMatchers.isNull()
        )).thenReturn(Optional.of(interviewQuestion));

        ResponseEntity<?> response = controller.getNextQuestion(
                null, null, null, null, null, null, null, null
        );

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isInstanceOf(com.cheatsheet.quiz.api.dto.response.NextQuestionResponse.class);
        com.cheatsheet.quiz.api.dto.response.NextQuestionResponse body =
                (com.cheatsheet.quiz.api.dto.response.NextQuestionResponse) response.getBody();
        assertThat(body.questionType()).isEqualTo("TEXT");
    }

    @Test
    void nextMapsCodeSnippetAndNullableDiagramAsIs() {
        long questionId = 505L;
        Question question = new Question(
                questionId,
                "q-" + questionId,
                "q-" + questionId,
                "test.md",
                "java",
                "Что выведет код?",
                "Ответ",
                false,
                "hash",
                QuestionType.CODE,
                "int x = 1;",
                null,
                0,
                null
        );
        AnswerOption option = new AnswerOption(1L, questionId, "A", true, 0, "OPENAI", "Верно");
        ReviewState reviewState = new ReviewState(questionId, 1, 1, 2.5, 1_700_000_000L, ReviewResult.CORRECT, 1, 0);
        InterviewQuestion interviewQuestion = new InterviewQuestion(question, List.of(option), reviewState);
        when(facade.nextQuestion(
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.eq(false),
                org.mockito.ArgumentMatchers.isNull()
        )).thenReturn(Optional.of(interviewQuestion));

        ResponseEntity<?> response = controller.getNextQuestion(
                null, null, null, null, null, null, null, null
        );

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        com.cheatsheet.quiz.api.dto.response.NextQuestionResponse body =
                (com.cheatsheet.quiz.api.dto.response.NextQuestionResponse) response.getBody();
        assertThat(body.questionType()).isEqualTo("CODE");
        assertThat(body.codeSnippet()).isEqualTo("int x = 1;");
        assertThat(body.diagramMermaid()).isNull();
    }

    @Test
    void nextNormalizesFilterAndPassesFlagsToFacade() {
        when(facade.nextQuestion(
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.eq(true),
                org.mockito.ArgumentMatchers.eq(42L)
        )).thenReturn(Optional.empty());

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

        ArgumentCaptor<InterviewFilter> filterCaptor = ArgumentCaptor.forClass(InterviewFilter.class);
        verify(facade).nextQuestion(filterCaptor.capture(), org.mockito.ArgumentMatchers.eq(true), org.mockito.ArgumentMatchers.eq(42L));
        InterviewFilter filter = filterCaptor.getValue();
        assertThat(filter.topic()).isEqualTo("java");
        assertThat(filter.group()).isEqualTo("core");
        assertThat(filter.importantOnly()).isTrue();
        assertThat(filter.onlyWrong()).isFalse();
        assertThat(filter.shuffle()).isTrue();
        assertThat(filter.ordered()).isFalse();
        assertThat(response.getStatusCode().value()).isEqualTo(204);
    }

    @Test
    void takeawayReturnsPayloadWhenFacadeProvidesText() {
        long questionId = 801L;
        when(facade.getTakeaway(questionId)).thenReturn(Optional.of("Сфокусируйся на неизменяемости и потокобезопасности."));

        ResponseEntity<?> response = controller.getTakeaway(questionId);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isInstanceOf(com.cheatsheet.quiz.api.dto.response.TakeawayResponse.class);
        com.cheatsheet.quiz.api.dto.response.TakeawayResponse body =
                (com.cheatsheet.quiz.api.dto.response.TakeawayResponse) response.getBody();
        assertThat(body.takeaway()).contains("потокобезопасности");
        verify(facade).getTakeaway(questionId);
    }

    @Test
    void takeawayReturnsNullWhenFacadeReturnsEmpty() {
        long questionId = 802L;
        when(facade.getTakeaway(questionId)).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.getTakeaway(questionId);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isInstanceOf(com.cheatsheet.quiz.api.dto.response.TakeawayResponse.class);
        com.cheatsheet.quiz.api.dto.response.TakeawayResponse body =
                (com.cheatsheet.quiz.api.dto.response.TakeawayResponse) response.getBody();
        assertThat(body.takeaway()).isNull();
        verify(facade).getTakeaway(questionId);
    }

    @Test
    void takeawayThrowsWhenQuestionIsMissing() {
        long questionId = 803L;
        doThrow(new QuestionNotFoundException("Вопрос не найден: id=" + questionId))
                .when(facade).ensureQuestionExists(questionId);

        assertThatThrownBy(() -> controller.getTakeaway(questionId))
                .isInstanceOf(QuestionNotFoundException.class)
                .hasMessageContaining("Вопрос не найден: id=" + questionId);
        verify(facade, never()).getTakeaway(questionId);
    }

    @Test
    void comparisonReturnsPayloadWhenFacadeProvidesText() {
        long questionId = 901L;
        long selectedOptionId = 11L;
        when(facade.generateComparison(questionId, selectedOptionId))
                .thenReturn(Optional.of("Ваш ответ игнорирует edge-case с null."));

        ResponseEntity<?> response = controller.getComparison(questionId, selectedOptionId);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isInstanceOf(com.cheatsheet.quiz.api.dto.response.ComparisonResponse.class);
        com.cheatsheet.quiz.api.dto.response.ComparisonResponse body =
                (com.cheatsheet.quiz.api.dto.response.ComparisonResponse) response.getBody();
        assertThat(body.comparison()).contains("edge-case");
        verify(facade).generateComparison(questionId, selectedOptionId);
    }

    @Test
    void comparisonReturnsNullWhenFacadeReturnsEmpty() {
        long questionId = 902L;
        long selectedOptionId = 12L;
        when(facade.generateComparison(questionId, selectedOptionId)).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.getComparison(questionId, selectedOptionId);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isInstanceOf(com.cheatsheet.quiz.api.dto.response.ComparisonResponse.class);
        com.cheatsheet.quiz.api.dto.response.ComparisonResponse body =
                (com.cheatsheet.quiz.api.dto.response.ComparisonResponse) response.getBody();
        assertThat(body.comparison()).isNull();
        verify(facade).generateComparison(questionId, selectedOptionId);
    }

    @Test
    void comparisonThrowsWhenQuestionIsMissing() {
        long questionId = 903L;
        long selectedOptionId = 13L;
        doThrow(new QuestionNotFoundException("Вопрос не найден: id=" + questionId))
                .when(facade).ensureQuestionExists(questionId);

        assertThatThrownBy(() -> controller.getComparison(questionId, selectedOptionId))
                .isInstanceOf(QuestionNotFoundException.class)
                .hasMessageContaining("Вопрос не найден: id=" + questionId);
        verify(facade, never()).generateComparison(questionId, selectedOptionId);
    }

    @Test
    void codeTraceReturnsPayloadWhenFacadeProvidesText() {
        long questionId = 1001L;
        when(facade.getCodeTrace(questionId)).thenReturn(Optional.of("Трассировка: i=0 -> i=1 -> return 42"));

        ResponseEntity<?> response = controller.getCodeTrace(questionId);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isInstanceOf(com.cheatsheet.quiz.api.dto.response.CodeTraceResponse.class);
        com.cheatsheet.quiz.api.dto.response.CodeTraceResponse body =
                (com.cheatsheet.quiz.api.dto.response.CodeTraceResponse) response.getBody();
        assertThat(body.trace()).contains("return 42");
        verify(facade).getCodeTrace(questionId);
    }

    @Test
    void codeTraceReturnsNullWhenFacadeReturnsEmpty() {
        long questionId = 1002L;
        when(facade.getCodeTrace(questionId)).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.getCodeTrace(questionId);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isInstanceOf(com.cheatsheet.quiz.api.dto.response.CodeTraceResponse.class);
        com.cheatsheet.quiz.api.dto.response.CodeTraceResponse body =
                (com.cheatsheet.quiz.api.dto.response.CodeTraceResponse) response.getBody();
        assertThat(body.trace()).isNull();
        verify(facade).getCodeTrace(questionId);
    }

    @Test
    void codeTraceThrowsWhenQuestionIsMissing() {
        long questionId = 1003L;
        doThrow(new QuestionNotFoundException("Вопрос не найден: id=" + questionId))
                .when(facade).ensureQuestionExists(questionId);

        assertThatThrownBy(() -> controller.getCodeTrace(questionId))
                .isInstanceOf(QuestionNotFoundException.class)
                .hasMessageContaining("Вопрос не найден: id=" + questionId);
        verify(facade, never()).getCodeTrace(questionId);
    }

    @Test
    void statsNormalizesFilterAndUsesMapperResponse() {
        InterviewStats domainStats = new InterviewStats(20, 5, 9, 33, 7);
        com.cheatsheet.quiz.api.dto.response.InterviewStatsResponse mapped =
                new com.cheatsheet.quiz.api.dto.response.InterviewStatsResponse(20, 5, 9, 33, 7);
        when(facade.getStats(org.mockito.ArgumentMatchers.any())).thenReturn(domainStats);
        when(statsApiMapper.toResponse(domainStats)).thenReturn(mapped);

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
        ArgumentCaptor<InterviewFilter> captor = ArgumentCaptor.forClass(InterviewFilter.class);
        verify(facade).getStats(captor.capture());
        InterviewFilter filter = captor.getValue();
        assertThat(filter.topic()).isEqualTo("java");
        assertThat(filter.group()).isEqualTo("core");
        assertThat(filter.importantOnly()).isTrue();
        assertThat(filter.onlyWrong()).isFalse();
        assertThat(filter.shuffle()).isTrue();
        assertThat(filter.ordered()).isFalse();
        verify(statsApiMapper).toResponse(domainStats);
    }

    @Test
    void topicStatsReturnsMappedListFromMapper() {
        List<TopicStats> domain = List.of(
                new TopicStats("java", 10, 3, 4, 20, 5, 1),
                new TopicStats("spring", 8, 2, 3, 15, 4, 0)
        );
        List<com.cheatsheet.quiz.api.dto.response.TopicStatsResponse> mapped = List.of(
                new com.cheatsheet.quiz.api.dto.response.TopicStatsResponse("java", 10, 3, 4, 20, 5, 1),
                new com.cheatsheet.quiz.api.dto.response.TopicStatsResponse("spring", 8, 2, 3, 15, 4, 0)
        );
        when(facade.getTopicStats()).thenReturn(domain);
        when(statsApiMapper.toTopicResponses(domain)).thenReturn(mapped);

        ResponseEntity<?> response = controller.getTopicStats();

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isEqualTo(mapped);
        verify(facade).getTopicStats();
        verify(statsApiMapper).toTopicResponses(domain);
    }

    @Test
    void streakReturnsDailyProgressFromService() {
        com.cheatsheet.quiz.api.dto.response.StreakResponse progress =
                new com.cheatsheet.quiz.api.dto.response.StreakResponse(7, 10, 3, false, 5);
        when(dailyStreakService.getTodayProgress()).thenReturn(progress);

        ResponseEntity<?> response = controller.getStreak();

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isEqualTo(progress);
        verify(dailyStreakService).getTodayProgress();
    }

    @Test
    void favoriteMapsServiceResultToApiDto() {
        QuestionIdRequest request = new QuestionIdRequest();
        request.setQuestionId(1101L);
        FavoriteService.FavoriteResult result = new FavoriteService.FavoriteResult(1101L, true, true);
        when(favoriteService.toggleFavorite(1101L)).thenReturn(result);

        ResponseEntity<?> response = controller.toggleFavorite(request);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isInstanceOf(com.cheatsheet.quiz.api.dto.response.FavoriteResponse.class);
        com.cheatsheet.quiz.api.dto.response.FavoriteResponse body =
                (com.cheatsheet.quiz.api.dto.response.FavoriteResponse) response.getBody();
        assertThat(body.questionId()).isEqualTo(1101L);
        assertThat(body.favorite()).isTrue();
        assertThat(body.synced()).isTrue();
        verify(favoriteService).toggleFavorite(1101L);
    }

    @Test
    void favoritePropagatesQuestionNotFoundFromService() {
        QuestionIdRequest request = new QuestionIdRequest();
        request.setQuestionId(1102L);
        when(favoriteService.toggleFavorite(1102L))
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
