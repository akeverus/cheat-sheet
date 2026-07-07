package com.cheatsheet.quiz.api.controller;

import com.cheatsheet.quiz.feature.interview.controller.InterviewApiController;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cheatsheet.quiz.api.dto.request.interview.QuestionIdRequest;
import com.cheatsheet.quiz.api.dto.request.interview.SubmitAnswerRequest;
import com.cheatsheet.quiz.api.mapper.request.ApiRequestMapper;
import com.cheatsheet.quiz.feature.interview.usecase.ConfidenceApiService;
import com.cheatsheet.quiz.feature.interview.usecase.FavoriteApiService;
import com.cheatsheet.quiz.feature.interview.usecase.stats.StreakApiService;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.domain.exception.QuestionNotFoundException;
import com.cheatsheet.quiz.feature.interview.usecase.AnswerApiService;
import com.cheatsheet.quiz.feature.interview.usecase.NextQuestionApiService;
import com.cheatsheet.quiz.feature.interview.usecase.stats.StatsApiService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class InterviewApiControllerUnitTest {

    @Mock private AnswerApiService answerApiService;
    @Mock private ConfidenceApiService confidenceApiService;
    @Mock private NextQuestionApiService nextQuestionApiService;
    @Mock private FavoriteApiService favoriteApiService;
    @Mock private StreakApiService streakApiService;
    @Mock private StatsApiService statsApiService;

    private InterviewApiController controller;

    @BeforeEach
    void setUp() {
        controller = new InterviewApiController(
                answerApiService,
                confidenceApiService,
                nextQuestionApiService,
                favoriteApiService,
                streakApiService,
                statsApiService,
                new ApiRequestMapper()
        );
    }

    @Test
    void updateConfidenceReturnsSuccessAndDelegatesToService() {
        long questionId = 201L;
        int grade = 5;
        com.cheatsheet.quiz.feature.interview.dto.response.progress.ConfidenceResponse payload =
                new com.cheatsheet.quiz.feature.interview.dto.response.progress.ConfidenceResponse(true, questionId, grade);
        when(confidenceApiService.toHttpResponse(questionId, grade)).thenReturn(ResponseEntity.ok(payload));

        ResponseEntity<?> response = controller.updateConfidence(questionId, grade);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isInstanceOf(com.cheatsheet.quiz.feature.interview.dto.response.progress.ConfidenceResponse.class);
        com.cheatsheet.quiz.feature.interview.dto.response.progress.ConfidenceResponse body =
                (com.cheatsheet.quiz.feature.interview.dto.response.progress.ConfidenceResponse) response.getBody();
        assertThat(body.success()).isTrue();
        assertThat(body.questionId()).isEqualTo(questionId);
        assertThat(body.grade()).isEqualTo(grade);
        verify(confidenceApiService).toHttpResponse(questionId, grade);
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
        com.cheatsheet.quiz.feature.interview.dto.response.answer.AnswerResponse payload =
                new com.cheatsheet.quiz.feature.interview.dto.response.answer.AnswerResponse(
                        true,
                        2L,
                        2L,
                        "<p>Ответ</p>",
                        List.of(new com.cheatsheet.quiz.feature.interview.dto.response.answer.OptionExplanationDto(2L, "Верно", true)),
                        "MINIMAL",
                        3,
                        List.of(new com.cheatsheet.quiz.feature.interview.dto.response.answer.RelatedQuestionDto(900L, "Похожий вопрос", "java")),
                        new com.cheatsheet.quiz.feature.interview.dto.response.answer.SessionInfoDto(1, 2, 1, 0, false)
                );
        when(answerApiService.toHttpResponse(any(), org.mockito.ArgumentMatchers.eq(httpSession)))
                .thenReturn(ResponseEntity.ok(payload));

        ResponseEntity<?> response = controller.answerApi(request, httpSession);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isInstanceOf(com.cheatsheet.quiz.feature.interview.dto.response.answer.AnswerResponse.class);
        com.cheatsheet.quiz.feature.interview.dto.response.answer.AnswerResponse body =
                (com.cheatsheet.quiz.feature.interview.dto.response.answer.AnswerResponse) response.getBody();
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
        verify(answerApiService).toHttpResponse(any(), org.mockito.ArgumentMatchers.eq(httpSession));
    }

    @Test
    void nextReturnsNoContentWhenServiceHasNoQuestion() {
        when(nextQuestionApiService.toHttpResponse(any())).thenReturn(ResponseEntity.noContent().build());

        ResponseEntity<?> response = controller.getNextQuestion(
                null, null, null, null, null, null, null, null
        );

        assertThat(response.getStatusCode().value()).isEqualTo(204);
        assertThat(response.getBody()).isNull();
        verify(nextQuestionApiService).toHttpResponse(any());
    }

    @Test
    void nextReturnsQuestionPayloadWhenServiceReturnsQuestion() {
        long questionId = 303L;
        Question question = existingQuestion(questionId);
        com.cheatsheet.quiz.feature.interview.dto.response.NextQuestionResponse payload =
                new com.cheatsheet.quiz.feature.interview.dto.response.NextQuestionResponse(
                        questionId,
                        question.questionText(),
                        question.topic(),
                        "TEXT",
                        null,
                        null,
                        List.of(
                                new com.cheatsheet.quiz.feature.interview.dto.response.NextQuestionOptionDto(1L, "A"),
                                new com.cheatsheet.quiz.feature.interview.dto.response.NextQuestionOptionDto(2L, "B")
                        ),
                        2L,
                        4,
                        1
                );
        when(nextQuestionApiService.toHttpResponse(any())).thenReturn(ResponseEntity.ok(payload));

        ResponseEntity<?> response = controller.getNextQuestion(
                null, null, null, null, null, null, null, null
        );

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isInstanceOf(com.cheatsheet.quiz.feature.interview.dto.response.NextQuestionResponse.class);
        com.cheatsheet.quiz.feature.interview.dto.response.NextQuestionResponse body =
                (com.cheatsheet.quiz.feature.interview.dto.response.NextQuestionResponse) response.getBody();
        assertThat(body.questionId()).isEqualTo(questionId);
        assertThat(body.questionText()).isEqualTo(question.questionText());
        assertThat(body.topic()).isEqualTo(question.topic());
        assertThat(body.options()).hasSize(2);
        assertThat(body.repetitions()).isEqualTo(2L);
        assertThat(body.correctCount()).isEqualTo(4);
        assertThat(body.wrongCount()).isEqualTo(1);
        verify(nextQuestionApiService).toHttpResponse(any());
    }

    @Test
    void nextPassesCommandToService() {
        when(nextQuestionApiService.toHttpResponse(any())).thenReturn(ResponseEntity.noContent().build());

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
        verify(nextQuestionApiService).toHttpResponse(commandCaptor.capture());
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
    void statsDelegatesToServiceAndReturnsPayload() {
        com.cheatsheet.quiz.feature.interview.dto.response.progress.InterviewStatsResponse mapped =
                new com.cheatsheet.quiz.feature.interview.dto.response.progress.InterviewStatsResponse(20, 5, 9, 33, 7);
        when(statsApiService.toStatsHttpResponse(any())).thenReturn(ResponseEntity.ok(mapped));

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
        verify(statsApiService).toStatsHttpResponse(any());
    }

    @Test
    void topicStatsDelegatesToServiceAndReturnsPayload() {
        List<com.cheatsheet.quiz.feature.interview.dto.response.progress.TopicStatsResponse> mapped = List.of(
                new com.cheatsheet.quiz.feature.interview.dto.response.progress.TopicStatsResponse("java", 10, 3, 4, 20, 5, 1),
                new com.cheatsheet.quiz.feature.interview.dto.response.progress.TopicStatsResponse("spring", 8, 2, 3, 15, 4, 0)
        );
        when(statsApiService.toTopicStatsHttpResponse()).thenReturn(ResponseEntity.ok(mapped));

        ResponseEntity<?> response = controller.getTopicStats();

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isEqualTo(mapped);
        verify(statsApiService).toTopicStatsHttpResponse();
    }

    @Test
    void streakReturnsDailyProgressFromService() {
        com.cheatsheet.quiz.feature.interview.dto.response.progress.StreakResponse progress =
                new com.cheatsheet.quiz.feature.interview.dto.response.progress.StreakResponse(7, 10, 3, false, 5);
        when(streakApiService.toHttpResponse()).thenReturn(ResponseEntity.ok(progress));

        ResponseEntity<?> response = controller.getStreak();

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isEqualTo(progress);
        verify(streakApiService).toHttpResponse();
    }

    @Test
    void favoriteMapsServiceResultToApiDto() {
        QuestionIdRequest request = new QuestionIdRequest();
        request.setQuestionId(1101L);
        com.cheatsheet.quiz.feature.interview.dto.response.progress.FavoriteResponse result =
                new com.cheatsheet.quiz.feature.interview.dto.response.progress.FavoriteResponse(true, 1101L);
        when(favoriteApiService.toHttpResponse(1101L)).thenReturn(ResponseEntity.ok(result));

        ResponseEntity<?> response = controller.toggleFavorite(request);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isInstanceOf(com.cheatsheet.quiz.feature.interview.dto.response.progress.FavoriteResponse.class);
        com.cheatsheet.quiz.feature.interview.dto.response.progress.FavoriteResponse body =
                (com.cheatsheet.quiz.feature.interview.dto.response.progress.FavoriteResponse) response.getBody();
        assertThat(body.questionId()).isEqualTo(1101L);
        assertThat(body.favorite()).isTrue();
        verify(favoriteApiService).toHttpResponse(1101L);
    }

    @Test
    void favoritePropagatesQuestionNotFoundFromService() {
        QuestionIdRequest request = new QuestionIdRequest();
        request.setQuestionId(1102L);
        when(favoriteApiService.toHttpResponse(1102L))
                .thenThrow(new QuestionNotFoundException("Вопрос не найден: id=1102"));

        assertThatThrownBy(() -> controller.toggleFavorite(request))
                .isInstanceOf(QuestionNotFoundException.class)
                .hasMessageContaining("id=1102");
    }

    private Question existingQuestion(long id) {
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
                QuestionType.TEXT,
                null,
                null,
                0,
                null
        );
    }
}
