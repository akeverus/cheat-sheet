package com.cheatsheet.quiz.api.mapper.request;

import com.cheatsheet.quiz.feature.interview.controller.InterviewSessionSupport;
import com.cheatsheet.quiz.api.dto.request.interview.SubmitAnswerRequest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MvcAnswerRequestMapperTest {

    private final MvcAnswerRequestMapper mapper = new MvcAnswerRequestMapper();

    @Test
    void mapsSubmitAnswerRequestToSubmission() {
        SubmitAnswerRequest request = new SubmitAnswerRequest();
        request.setQuestionId(42L);
        request.setOptionId(7L);
        request.setTopic("java");
        request.setGroup("core");
        request.setImportant(true);
        request.setOnlyWrong(false);
        request.setShuffle(true);
        request.setOrdered(false);
        request.setConfidence(4);

        InterviewSessionSupport.AnswerSubmission submission = mapper.toSubmission(request);

        assertThat(submission.questionId()).isEqualTo(42L);
        assertThat(submission.optionId()).isEqualTo(7L);
        assertThat(submission.topic()).isEqualTo("java");
        assertThat(submission.group()).isEqualTo("core");
        assertThat(submission.important()).isTrue();
        assertThat(submission.onlyWrong()).isFalse();
        assertThat(submission.shuffle()).isTrue();
        assertThat(submission.ordered()).isFalse();
        assertThat(submission.confidence()).isEqualTo(4);
    }
}
