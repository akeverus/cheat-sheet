package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.feature.interview.dto.response.NextQuestionResponse;
import com.cheatsheet.quiz.feature.interview.mapper.NextQuestionResponseMapper;
import com.cheatsheet.quiz.feature.interview.service.facade.InterviewFacade;
import com.cheatsheet.quiz.feature.interview.usecase.NextQuestionApiService;
import com.cheatsheet.quiz.domain.AnswerOption;
import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.domain.InterviewQuestion;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.domain.ReviewResult;
import com.cheatsheet.quiz.domain.ReviewState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NextQuestionApiServiceTest {

    @Mock
    private InterviewFacade facade;

    private NextQuestionApiService service;

    @BeforeEach
    void setUp() {
        service = new NextQuestionApiService(facade, new NextQuestionResponseMapper());
    }

    @Test
    void returnsEmptyWhenFacadeHasNoQuestion() {
        NextQuestionApiService.NextQuestionCommand command = new NextQuestionApiService.NextQuestionCommand(
                null, null, null, null, null, null, null, null
        );
        when(facade.nextQuestion(any(), eq(false), eq(null))).thenReturn(Optional.empty());

        Optional<NextQuestionResponse> response = service.getNextQuestion(command);

        assertThat(response).isEmpty();
    }

    @Test
    void mapsQuestionPayloadWhenFacadeReturnsQuestion() {
        long questionId = 303L;
        Question question = existingQuestion(questionId);
        AnswerOption optionA = new AnswerOption(1L, questionId, "A", false, 0, "OPENAI", "Неверно");
        AnswerOption optionB = new AnswerOption(2L, questionId, "B", true, 1, "OPENAI", "Верно");
        ReviewState reviewState = new ReviewState(questionId, 2, 3, 2.5, 1_700_000_000L, ReviewResult.CORRECT, 4, 1);
        InterviewQuestion interviewQuestion = new InterviewQuestion(question, List.of(optionA, optionB), reviewState);
        NextQuestionApiService.NextQuestionCommand command = new NextQuestionApiService.NextQuestionCommand(
                null, null, null, null, null, null, null, null
        );
        when(facade.nextQuestion(any(), eq(false), eq(null))).thenReturn(Optional.of(interviewQuestion));

        Optional<NextQuestionResponse> response = service.getNextQuestion(command);

        assertThat(response).isPresent();
        NextQuestionResponse body = response.get();
        assertThat(body.questionId()).isEqualTo(questionId);
        assertThat(body.questionText()).isEqualTo(question.questionText());
        assertThat(body.topic()).isEqualTo(question.topic());
        assertThat(body.options()).hasSize(2);
        assertThat(body.repetitions()).isEqualTo(2L);
        assertThat(body.correctCount()).isEqualTo(4);
        assertThat(body.wrongCount()).isEqualTo(1);
    }

    @Test
    void fallsBackToTextTypeWhenQuestionTypeIsNull() {
        long questionId = 404L;
        Question question = existingQuestionWithType(questionId, null);
        AnswerOption option = new AnswerOption(1L, questionId, "A", true, 0, "OPENAI", "Верно");
        ReviewState reviewState = new ReviewState(questionId, 0, 0, 2.5, 1_700_000_000L, ReviewResult.NEW, 0, 0);
        InterviewQuestion interviewQuestion = new InterviewQuestion(question, List.of(option), reviewState);
        NextQuestionApiService.NextQuestionCommand command = new NextQuestionApiService.NextQuestionCommand(
                null, null, null, null, null, null, null, null
        );
        when(facade.nextQuestion(any(), eq(false), eq(null))).thenReturn(Optional.of(interviewQuestion));

        Optional<NextQuestionResponse> response = service.getNextQuestion(command);

        assertThat(response).isPresent();
        assertThat(response.get().questionType()).isEqualTo("TEXT");
    }

    @Test
    void normalizesFilterAndPassesFlagsToFacade() {
        NextQuestionApiService.NextQuestionCommand command = new NextQuestionApiService.NextQuestionCommand(
                "  java  ",
                "  core ",
                true,
                false,
                true,
                true,
                false,
                42L
        );
        when(facade.nextQuestion(any(), eq(true), eq(42L))).thenReturn(Optional.empty());

        service.getNextQuestion(command);

        ArgumentCaptor<InterviewFilter> filterCaptor = ArgumentCaptor.forClass(InterviewFilter.class);
        verify(facade).nextQuestion(filterCaptor.capture(), eq(true), eq(42L));
        InterviewFilter filter = filterCaptor.getValue();
        assertThat(filter.topic()).isEqualTo("java");
        assertThat(filter.group()).isEqualTo("core");
        assertThat(filter.importantOnly()).isTrue();
        assertThat(filter.onlyWrong()).isFalse();
        assertThat(filter.shuffle()).isTrue();
        assertThat(filter.ordered()).isFalse();
    }

    @Test
    void toHttpResponseReturnsNoContentWhenQuestionIsMissing() {
        NextQuestionApiService.NextQuestionCommand command = new NextQuestionApiService.NextQuestionCommand(
                null, null, null, null, null, null, null, null
        );
        when(facade.nextQuestion(any(), eq(false), eq(null))).thenReturn(Optional.empty());

        var response = service.toHttpResponse(command);

        assertThat(response.getStatusCode().value()).isEqualTo(204);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void toHttpResponseReturnsOkWhenQuestionExists() {
        long questionId = 505L;
        Question question = existingQuestion(questionId);
        AnswerOption option = new AnswerOption(1L, questionId, "A", true, 0, "OPENAI", "Верно");
        ReviewState reviewState = new ReviewState(questionId, 1, 2, 2.5, 1_700_000_000L, ReviewResult.CORRECT, 3, 1);
        InterviewQuestion interviewQuestion = new InterviewQuestion(question, List.of(option), reviewState);
        NextQuestionApiService.NextQuestionCommand command = new NextQuestionApiService.NextQuestionCommand(
                null, null, null, null, null, null, null, null
        );
        when(facade.nextQuestion(any(), eq(false), eq(null))).thenReturn(Optional.of(interviewQuestion));

        var response = service.toHttpResponse(command);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().questionId()).isEqualTo(questionId);
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
