package com.cheatsheet.quiz.api.mapper.response;

import com.cheatsheet.quiz.domain.AnswerOption;
import com.cheatsheet.quiz.domain.InterviewQuestion;
import com.cheatsheet.quiz.domain.InterviewSession;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.domain.ReviewState;
import com.cheatsheet.quiz.domain.ReviewResult;
import com.cheatsheet.quiz.feature.interview.dto.response.session.QuestionResponseDTO;
import com.cheatsheet.quiz.feature.interview.dto.response.session.ReviewDTO;
import com.cheatsheet.quiz.feature.interview.dto.response.session.TrainingSessionDTO;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InterviewDtoMapperTest {

    private final InterviewDtoMapper mapper = new InterviewDtoMapper();

    @Test
    void mapsQuestionToResponseWithOptions() {
        Question question = stubQuestion(42L, QuestionType.TEXT, null, null);
        AnswerOption opt1 = new AnswerOption(101L, 42L, "First", false, 0,
                "MARKDOWN", null, 1, 1);
        AnswerOption opt2 = new AnswerOption(102L, 42L, "Second", true, 1,
                "MARKDOWN", null, 1, 1);
        ReviewState review = stubReview(42L);
        InterviewQuestion iq = new InterviewQuestion(question, List.of(opt1, opt2), review);

        QuestionResponseDTO dto = mapper.toQuestionResponse(iq);

        assertThat(dto.questionId()).isEqualTo(42L);
        assertThat(dto.questionText()).isEqualTo("Q text");
        assertThat(dto.topic()).isEqualTo("java");
        assertThat(dto.questionType()).isEqualTo("TEXT");
        assertThat(dto.options()).hasSize(2);
        assertThat(dto.options().get(0).id()).isEqualTo(101L);
        assertThat(dto.options().get(0).optionText()).isEqualTo("First");
    }

    @Test
    void mapsCodeQuestionWithSnippetAndDiagram() {
        Question question = stubQuestion(7L, QuestionType.CODE, "int x = 0;", "graph TD\nA-->B");
        ReviewState review = stubReview(7L);
        InterviewQuestion iq = new InterviewQuestion(question, List.of(), review);

        QuestionResponseDTO dto = mapper.toQuestionResponse(iq);

        assertThat(dto.questionType()).isEqualTo("CODE");
        assertThat(dto.codeSnippet()).isEqualTo("int x = 0;");
        assertThat(dto.diagramMermaid()).contains("graph TD");
        assertThat(dto.options()).isEmpty();
    }

    @Test
    void defaultsToTextWhenQuestionTypeIsNull() {
        Question question = stubQuestion(1L, null, null, null);
        ReviewState review = stubReview(1L);
        InterviewQuestion iq = new InterviewQuestion(question, List.of(), review);

        QuestionResponseDTO dto = mapper.toQuestionResponse(iq);

        assertThat(dto.questionType()).isEqualTo("TEXT");
    }

    @Test
    void mapsSessionToTrainingSessionDto() {
        InterviewSession session = Mockito.mock(InterviewSession.class);
        Mockito.when(session.getIndex()).thenReturn(3);
        Mockito.when(session.getTotal()).thenReturn(10);
        Mockito.when(session.getCorrect()).thenReturn(2);
        Mockito.when(session.getWrong()).thenReturn(1);
        Mockito.when(session.isFinished()).thenReturn(false);

        TrainingSessionDTO dto = mapper.toTrainingSessionDto(session);

        assertThat(dto.index()).isEqualTo(3);
        assertThat(dto.total()).isEqualTo(10);
        assertThat(dto.correct()).isEqualTo(2);
        assertThat(dto.wrong()).isEqualTo(1);
        assertThat(dto.finished()).isFalse();
    }

    @Test
    void mapsReviewStateToReviewDto() {
        ReviewState state = new ReviewState(99L, 5, 7, 2.5, 1700000000L,
                ReviewResult.CORRECT, 4, 1);

        ReviewDTO dto = mapper.toReviewDto(state);

        assertThat(dto.repetitions()).isEqualTo(5);
        assertThat(dto.intervalDays()).isEqualTo(7);
        assertThat(dto.easeFactor()).isEqualTo(2.5);
        assertThat(dto.nextReviewAt()).isEqualTo(1700000000L);
        assertThat(dto.correctCount()).isEqualTo(4);
        assertThat(dto.wrongCount()).isEqualTo(1);
    }

    private Question stubQuestion(long id, QuestionType type, String code, String diagram) {
        return new Question(
                id, "slug-" + id, "source-" + id, "java/q-" + id + ".md", "java",
                "Q text", "A markdown", false, "hash",
                type, code, diagram, 0, null);
    }

    private ReviewState stubReview(long id) {
        return new ReviewState(id, 0, 0, 2.5, 0L, null, 0, 0);
    }
}
