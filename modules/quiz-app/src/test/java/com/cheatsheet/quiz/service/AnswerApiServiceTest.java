package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.feature.interview.controller.InterviewSessionSupport;
import com.cheatsheet.quiz.feature.interview.usecase.AnswerApiService;
import com.cheatsheet.quiz.feature.interview.dto.response.answer.AnswerResponse;
import com.cheatsheet.quiz.feature.interview.service.facade.InterviewFacade;
import com.cheatsheet.quiz.domain.AnswerDisplayMode;
import com.cheatsheet.quiz.domain.AnswerOption;
import com.cheatsheet.quiz.domain.AnswerResult;
import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.domain.InterviewMode;
import com.cheatsheet.quiz.domain.InterviewSession;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.domain.RelatedQuestion;
import com.cheatsheet.quiz.domain.ReviewResult;
import com.cheatsheet.quiz.domain.ReviewState;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnswerApiServiceTest {

    @Mock
    private InterviewSessionSupport sessionSupport;
    @Mock
    private InterviewFacade facade;
    @Mock
    private HttpSession session;

    private AnswerApiService service;

    @BeforeEach
    void setUp() {
        service = new AnswerApiService(sessionSupport, facade);
    }

    @Test
    void buildAnswerResponseBuildsApiPayloadFromAnswerContext() {
        long questionId = 701L;
        long selectedOptionId = 2L;
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
        AnswerApiService.AnswerCommand command = new AnswerApiService.AnswerCommand(
                questionId, selectedOptionId, "java", "core", true, false, true, false, 4
        );

        when(sessionSupport.processAnswer(any(InterviewSessionSupport.AnswerSubmission.class), eq(session)))
                .thenReturn(context);
        when(facade.findRelated(questionId, "java"))
                .thenReturn(List.of(new RelatedQuestion(900L, "Похожий вопрос", "java", 75.0)));
        // Эхо-стаб: answerMarkdown ("Ответ") и explanation каждого варианта
        // ("Верно"/"Неверно") теперь рендерятся через facade.renderMarkdown.
        when(facade.renderMarkdown(any())).thenAnswer(inv -> {
            Object md = inv.getArgument(0);
            return md == null ? null : "<p>" + md + "</p>";
        });

        AnswerResponse body = service.buildAnswerResponse(command, session);

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
                new InterviewSessionSupport.AnswerSubmission(
                        questionId, selectedOptionId, "java", "core", true, false, true, false, 4
                ),
                session
        );
        verify(facade).findRelated(questionId, "java");
        verify(facade).renderMarkdown(eq(question.answerMarkdown()));
    }

    @Test
    void buildAnswerResponseOmitsSessionPayloadWhenSessionIsAbsent() {
        long questionId = 801L;
        long selectedOptionId = 9L;
        Question question = existingQuestion(questionId);
        AnswerOption selected = new AnswerOption(9L, questionId, "A", false, 0, "OPENAI", "Неверно");
        AnswerOption correct = new AnswerOption(10L, questionId, "B", true, 1, "OPENAI", "Верно");
        ReviewState reviewState = new ReviewState(questionId, 1, 1, 2.4, 1_700_000_001L, ReviewResult.WRONG, 2, 3);
        AnswerResult answerResult = new AnswerResult(
                question,
                List.of(selected, correct),
                selected,
                correct,
                false,
                reviewState,
                AnswerDisplayMode.FULL
        );
        InterviewSessionSupport.AnswerContext context = new InterviewSessionSupport.AnswerContext(
                answerResult,
                new InterviewFilter("java", "core", true, false, true, false),
                null
        );
        AnswerApiService.AnswerCommand command = new AnswerApiService.AnswerCommand(
                questionId, selectedOptionId, "java", "core", true, false, true, false, 3
        );

        when(sessionSupport.processAnswer(any(InterviewSessionSupport.AnswerSubmission.class), eq(session)))
                .thenReturn(context);
        when(facade.findRelated(questionId, "java")).thenReturn(List.of());
        // Эхо-стаб: answerMarkdown ("Ответ") и explanation каждого варианта
        // ("Верно"/"Неверно") теперь рендерятся через facade.renderMarkdown.
        when(facade.renderMarkdown(any())).thenAnswer(inv -> {
            Object md = inv.getArgument(0);
            return md == null ? null : "<p>" + md + "</p>";
        });

        AnswerResponse body = service.buildAnswerResponse(command, session);

        assertThat(body.correct()).isFalse();
        assertThat(body.session()).isNull();
        assertThat(body.relatedQuestions()).isEmpty();
    }

    @Test
    void toHttpResponseWrapsAnswerPayloadWithOkStatus() {
        long questionId = 901L;
        long selectedOptionId = 2L;
        Question question = existingQuestion(questionId);
        AnswerOption option = new AnswerOption(2L, questionId, "B", true, 1, "OPENAI", "Верно");
        ReviewState reviewState = new ReviewState(questionId, 1, 1, 2.4, 1_700_000_001L, ReviewResult.CORRECT, 2, 0);
        AnswerResult answerResult = new AnswerResult(
                question,
                List.of(option),
                option,
                option,
                true,
                reviewState,
                AnswerDisplayMode.MINIMAL
        );
        InterviewSessionSupport.AnswerContext context = new InterviewSessionSupport.AnswerContext(
                answerResult,
                new InterviewFilter("java", "core", true, false, true, false),
                null
        );
        AnswerApiService.AnswerCommand command = new AnswerApiService.AnswerCommand(
                questionId, selectedOptionId, "java", "core", true, false, true, false, 4
        );
        when(sessionSupport.processAnswer(any(InterviewSessionSupport.AnswerSubmission.class), eq(session)))
                .thenReturn(context);
        when(facade.findRelated(questionId, "java")).thenReturn(List.of());
        // Эхо-стаб: answerMarkdown ("Ответ") и explanation каждого варианта
        // ("Верно"/"Неверно") теперь рендерятся через facade.renderMarkdown.
        when(facade.renderMarkdown(any())).thenAnswer(inv -> {
            Object md = inv.getArgument(0);
            return md == null ? null : "<p>" + md + "</p>";
        });

        ResponseEntity<AnswerResponse> response = service.toHttpResponse(command, session);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().correct()).isTrue();
        assertThat(response.getBody().selectedOptionId()).isEqualTo(selectedOptionId);
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
