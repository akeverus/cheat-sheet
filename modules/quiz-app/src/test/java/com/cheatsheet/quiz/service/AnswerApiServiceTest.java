package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.api.controller.InterviewSessionSupport;
import com.cheatsheet.quiz.api.dto.response.AnswerResponse;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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
                session
        )).thenReturn(context);
        when(facade.findRelated(questionId, "java"))
                .thenReturn(List.of(new RelatedQuestion(900L, "Похожий вопрос", "java", 75.0)));
        when(facade.renderMarkdown(question.answerMarkdown())).thenReturn("<p>Ответ</p>");

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
                questionId,
                selectedOptionId,
                "java",
                "core",
                true,
                false,
                true,
                false,
                4,
                session
        );
        verify(facade).findRelated(questionId, "java");
        verify(facade).renderMarkdown(eq(question.answerMarkdown()));
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
