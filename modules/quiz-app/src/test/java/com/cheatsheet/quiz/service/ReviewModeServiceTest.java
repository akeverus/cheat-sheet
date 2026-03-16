package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.feature.interview.service.flow.ReviewModeService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReviewModeServiceTest {

    private final ReviewModeService service = new ReviewModeService();

    @Test
    void applyAlwaysEnablesOnlyWrong() {
        InterviewFilter source = new InterviewFilter("java", "backend", true, false, false, true);

        InterviewFilter review = service.apply(source);

        assertThat(review.topic()).isEqualTo("java");
        assertThat(review.group()).isEqualTo("backend");
        assertThat(review.importantOnly()).isTrue();
        assertThat(review.onlyWrong()).isTrue();
    }
}
