package com.cheatsheet.quiz.service.ai;

import com.cheatsheet.quiz.config.AppProperties;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class QuestionRequestFailureFeedbackSupplierTest {

    @Test
    void feedbackUsesConfiguredViolationMessageAndNormalizer() {
        AppProperties appProperties = new AppProperties();
        appProperties.getInterview().setQuestionRequestFailureViolationMessage("AI response is not valid JSON");
        QuestionRetryFeedbackNormalizer normalizer = mock(QuestionRetryFeedbackNormalizer.class);
        when(normalizer.normalize(List.of("AI response is not valid JSON")))
                .thenReturn(List.of("AI response is not valid JSON"));

        QuestionRequestFailureFeedbackSupplier supplier =
                new QuestionRequestFailureFeedbackSupplier(appProperties, normalizer);

        List<String> feedback = supplier.feedback();

        assertThat(feedback).containsExactly("AI response is not valid JSON");
        verify(normalizer).normalize(List.of("AI response is not valid JSON"));
    }

    @Test
    void feedbackFallsBackToSafeMessageWhenNormalizerReturnsEmpty() {
        AppProperties appProperties = new AppProperties();
        appProperties.getInterview().setQuestionRequestFailureViolationMessage("   ");
        QuestionRetryFeedbackNormalizer normalizer = mock(QuestionRetryFeedbackNormalizer.class);
        when(normalizer.normalize(List.of("AI did not return parsable question JSON payload")))
                .thenReturn(List.of());

        QuestionRequestFailureFeedbackSupplier supplier =
                new QuestionRequestFailureFeedbackSupplier(appProperties, normalizer);

        List<String> feedback = supplier.feedback();

        assertThat(feedback).containsExactly("AI did not return parsable question JSON payload");
        verify(normalizer).normalize(List.of("AI did not return parsable question JSON payload"));
    }
}
