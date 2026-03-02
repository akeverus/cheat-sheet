package com.cheatsheet.quiz.service.ai;

import com.cheatsheet.quiz.domain.Question;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuestionQualityEvaluatorTest {

    @Mock
    private QuestionValidationService validationService;
    @Mock
    private QuestionQualityScorer questionQualityScorer;
    @Mock
    private QuestionGenerationPolicy questionGenerationPolicy;
    @Mock
    private QuestionRetryFeedbackNormalizer questionRetryFeedbackNormalizer;
    @Mock
    private Question candidate;

    @Test
    void evaluateCandidateBuildsAcceptedSnapshot() {
        QuestionQualityEvaluator evaluator = new QuestionQualityEvaluator(
                validationService,
                questionQualityScorer,
                questionGenerationPolicy,
                questionRetryFeedbackNormalizer
        );
        Set<String> seenFingerprints = Set.of("q1::a|b|c|d");
        List<String> baseViolations = List.of();
        List<String> enrichedViolations = List.of();
        List<String> retryFeedback = List.of();
        when(validationService.validate(candidate)).thenReturn(baseViolations);
        when(questionGenerationPolicy.enrichViolations(candidate, baseViolations, seenFingerprints)).thenReturn(enrichedViolations);
        when(questionRetryFeedbackNormalizer.normalize(enrichedViolations)).thenReturn(retryFeedback);
        when(questionQualityScorer.score(enrichedViolations)).thenReturn(96);
        when(questionGenerationPolicy.isAccepted(96, enrichedViolations)).thenReturn(true);

        QuestionQualityEvaluator.QualitySnapshot snapshot = evaluator.evaluateCandidate(candidate, seenFingerprints);

        assertThat(snapshot.score()).isEqualTo(96);
        assertThat(snapshot.violations()).isEqualTo(enrichedViolations);
        assertThat(snapshot.retryFeedback()).isEqualTo(retryFeedback);
        assertThat(snapshot.accepted()).isTrue();
        verify(validationService).validate(candidate);
        verify(questionGenerationPolicy).enrichViolations(candidate, baseViolations, seenFingerprints);
        verify(questionRetryFeedbackNormalizer).normalize(enrichedViolations);
        verify(questionQualityScorer).score(enrichedViolations);
        verify(questionGenerationPolicy).isAccepted(96, enrichedViolations);
    }

    @Test
    void evaluateCandidateBuildsRejectedSnapshot() {
        QuestionQualityEvaluator evaluator = new QuestionQualityEvaluator(
                validationService,
                questionQualityScorer,
                questionGenerationPolicy,
                questionRetryFeedbackNormalizer
        );
        Set<String> seenFingerprints = Set.of();
        List<String> baseViolations = List.of("shortExplanation must be at least 30 characters");
        List<String> enrichedViolations = List.of(
                "shortExplanation must be at least 30 characters",
                "Question cognitive load is too high: question text is too long"
        );
        List<String> retryFeedback = List.of("Question cognitive load is too high: question text is too long");
        when(validationService.validate(candidate)).thenReturn(baseViolations);
        when(questionGenerationPolicy.enrichViolations(candidate, baseViolations, seenFingerprints)).thenReturn(enrichedViolations);
        when(questionRetryFeedbackNormalizer.normalize(enrichedViolations)).thenReturn(retryFeedback);
        when(questionQualityScorer.score(enrichedViolations)).thenReturn(54);
        when(questionGenerationPolicy.isAccepted(54, enrichedViolations)).thenReturn(false);

        QuestionQualityEvaluator.QualitySnapshot snapshot = evaluator.evaluateCandidate(candidate, seenFingerprints);

        assertThat(snapshot.score()).isEqualTo(54);
        assertThat(snapshot.violations()).isEqualTo(enrichedViolations);
        assertThat(snapshot.retryFeedback()).isEqualTo(retryFeedback);
        assertThat(snapshot.accepted()).isFalse();
    }

    @Test
    void retryFeedbackForRequestFailureUsesNormalizer() {
        QuestionQualityEvaluator evaluator = new QuestionQualityEvaluator(
                validationService,
                questionQualityScorer,
                questionGenerationPolicy,
                questionRetryFeedbackNormalizer
        );
        List<String> normalized = List.of("AI did not return parsable question JSON payload");
        when(questionRetryFeedbackNormalizer.normalize(List.of("AI did not return parsable question JSON payload")))
                .thenReturn(normalized);

        List<String> retryFeedback = evaluator.retryFeedbackForRequestFailure();

        assertThat(retryFeedback).isEqualTo(normalized);
        verify(questionRetryFeedbackNormalizer).normalize(List.of("AI did not return parsable question JSON payload"));
    }
}
