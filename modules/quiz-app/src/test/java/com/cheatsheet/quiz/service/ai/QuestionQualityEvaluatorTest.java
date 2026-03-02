package com.cheatsheet.quiz.service.ai;

import com.cheatsheet.quiz.domain.Question;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
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
    private QuestionQualitySnapshotFactory questionQualitySnapshotFactory;
    @Mock
    private QuestionRequestFailureFeedbackSupplier questionRequestFailureFeedbackSupplier;
    @Mock
    private QuestionQualityMessageNormalizer questionQualityMessageNormalizer;
    @Mock
    private Question candidate;

    @Test
    void evaluateCandidateBuildsAcceptedSnapshot() {
        QuestionQualityEvaluator evaluator = new QuestionQualityEvaluator(
                validationService,
                questionQualityScorer,
                questionGenerationPolicy,
                questionRetryFeedbackNormalizer,
                questionQualitySnapshotFactory,
                questionRequestFailureFeedbackSupplier,
                questionQualityMessageNormalizer
        );
        Set<String> seenFingerprints = Set.of("q1::a|b|c|d");
        List<String> baseViolations = List.of("base");
        List<String> enrichedViolations = List.of("enriched");
        List<String> retryFeedback = List.of("retry");
        when(validationService.validate(candidate)).thenReturn(baseViolations);
        when(questionQualityMessageNormalizer.normalize(baseViolations)).thenReturn(baseViolations);
        when(questionGenerationPolicy.enrichViolations(candidate, baseViolations, seenFingerprints)).thenReturn(enrichedViolations);
        when(questionQualityMessageNormalizer.normalize(enrichedViolations)).thenReturn(enrichedViolations);
        when(questionRetryFeedbackNormalizer.normalize(enrichedViolations)).thenReturn(retryFeedback);
        when(questionQualityMessageNormalizer.normalize(retryFeedback)).thenReturn(retryFeedback);
        when(questionQualityScorer.score(enrichedViolations)).thenReturn(96);
        when(questionGenerationPolicy.isAccepted(96, enrichedViolations)).thenReturn(true);
        QuestionQualitySnapshot expected = QuestionQualitySnapshot.builder()
                .violations(enrichedViolations)
                .retryFeedback(retryFeedback)
                .score(96)
                .accepted(true)
                .build();
        when(questionQualitySnapshotFactory.create(enrichedViolations, retryFeedback, 96, true)).thenReturn(expected);

        QuestionQualitySnapshot snapshot = evaluator.evaluateCandidate(candidate, seenFingerprints);

        assertThat(snapshot).isEqualTo(expected);
        verify(validationService).validate(candidate);
        verify(questionQualityMessageNormalizer).normalize(baseViolations);
        verify(questionGenerationPolicy).enrichViolations(candidate, baseViolations, seenFingerprints);
        verify(questionQualityMessageNormalizer).normalize(enrichedViolations);
        verify(questionRetryFeedbackNormalizer).normalize(enrichedViolations);
        verify(questionQualityMessageNormalizer).normalize(retryFeedback);
        verify(questionQualityScorer).score(enrichedViolations);
        verify(questionGenerationPolicy).isAccepted(96, enrichedViolations);
        verify(questionQualitySnapshotFactory).create(enrichedViolations, retryFeedback, 96, true);
    }

    @Test
    void evaluateCandidateBuildsRejectedSnapshot() {
        QuestionQualityEvaluator evaluator = new QuestionQualityEvaluator(
                validationService,
                questionQualityScorer,
                questionGenerationPolicy,
                questionRetryFeedbackNormalizer,
                questionQualitySnapshotFactory,
                questionRequestFailureFeedbackSupplier,
                questionQualityMessageNormalizer
        );
        Set<String> seenFingerprints = Set.of();
        List<String> baseViolations = List.of("shortExplanation must be at least 30 characters");
        List<String> enrichedViolations = List.of(
                "shortExplanation must be at least 30 characters",
                "Question cognitive load is too high: question text is too long"
        );
        List<String> retryFeedback = List.of("Question cognitive load is too high: question text is too long");
        when(validationService.validate(candidate)).thenReturn(baseViolations);
        when(questionQualityMessageNormalizer.normalize(baseViolations)).thenReturn(baseViolations);
        when(questionGenerationPolicy.enrichViolations(candidate, baseViolations, seenFingerprints)).thenReturn(enrichedViolations);
        when(questionQualityMessageNormalizer.normalize(enrichedViolations)).thenReturn(enrichedViolations);
        when(questionRetryFeedbackNormalizer.normalize(enrichedViolations)).thenReturn(retryFeedback);
        when(questionQualityMessageNormalizer.normalize(retryFeedback)).thenReturn(retryFeedback);
        when(questionQualityScorer.score(enrichedViolations)).thenReturn(54);
        when(questionGenerationPolicy.isAccepted(54, enrichedViolations)).thenReturn(false);
        QuestionQualitySnapshot expected = QuestionQualitySnapshot.builder()
                .violations(enrichedViolations)
                .retryFeedback(retryFeedback)
                .score(54)
                .accepted(false)
                .build();
        when(questionQualitySnapshotFactory.create(enrichedViolations, retryFeedback, 54, false)).thenReturn(expected);

        QuestionQualitySnapshot snapshot = evaluator.evaluateCandidate(candidate, seenFingerprints);

        assertThat(snapshot).isEqualTo(expected);
        verify(questionQualitySnapshotFactory).create(enrichedViolations, retryFeedback, 54, false);
    }

    @Test
    void retryFeedbackForRequestFailureUsesNormalizer() {
        QuestionQualityEvaluator evaluator = new QuestionQualityEvaluator(
                validationService,
                questionQualityScorer,
                questionGenerationPolicy,
                questionRetryFeedbackNormalizer,
                questionQualitySnapshotFactory,
                questionRequestFailureFeedbackSupplier,
                questionQualityMessageNormalizer
        );
        List<String> normalized = List.of("ai-request-failure");
        when(questionRequestFailureFeedbackSupplier.feedback()).thenReturn(normalized);
        when(questionQualityMessageNormalizer.normalize(normalized)).thenReturn(normalized);

        List<String> retryFeedback = evaluator.retryFeedbackForRequestFailure();

        assertThat(retryFeedback).isEqualTo(normalized);
        verify(questionRequestFailureFeedbackSupplier).feedback();
        verify(questionQualityMessageNormalizer).normalize(normalized);
    }

    @Test
    void evaluateCandidateNormalizesNullAndBlankMessagesBeforeScoring() {
        QuestionQualityEvaluator evaluator = new QuestionQualityEvaluator(
                validationService,
                questionQualityScorer,
                questionGenerationPolicy,
                questionRetryFeedbackNormalizer,
                questionQualitySnapshotFactory,
                questionRequestFailureFeedbackSupplier,
                questionQualityMessageNormalizer
        );
        List<String> baseViolations = List.of("  base violation  ", "BASE VIOLATION", "   ");
        List<String> enrichedViolations = Arrays.asList("  policy violation  ", "POLICY VIOLATION", null, "");
        List<String> retryFeedback = List.of("  retry hint  ", "RETRY HINT", " ");
        List<String> normalizedRetryFeedback = List.of("retry hint");
        List<String> normalizedBaseViolations = List.of("base violation");
        List<String> normalizedEnrichedViolations = List.of("policy violation");
        when(validationService.validate(candidate)).thenReturn(baseViolations);
        when(questionQualityMessageNormalizer.normalize(baseViolations)).thenReturn(normalizedBaseViolations);
        when(questionGenerationPolicy.enrichViolations(candidate, normalizedBaseViolations, Set.of()))
                .thenReturn(enrichedViolations);
        when(questionQualityMessageNormalizer.normalize(enrichedViolations)).thenReturn(normalizedEnrichedViolations);
        when(questionRetryFeedbackNormalizer.normalize(normalizedEnrichedViolations)).thenReturn(retryFeedback);
        when(questionQualityMessageNormalizer.normalize(retryFeedback)).thenReturn(normalizedRetryFeedback);
        when(questionQualityScorer.score(normalizedEnrichedViolations)).thenReturn(88);
        when(questionGenerationPolicy.isAccepted(88, normalizedEnrichedViolations)).thenReturn(false);
        QuestionQualitySnapshot expected = QuestionQualitySnapshot.builder()
                .violations(normalizedEnrichedViolations)
                .retryFeedback(normalizedRetryFeedback)
                .score(88)
                .accepted(false)
                .build();
        when(questionQualitySnapshotFactory.create(normalizedEnrichedViolations, normalizedRetryFeedback, 88, false))
                .thenReturn(expected);

        QuestionQualitySnapshot snapshot = evaluator.evaluateCandidate(candidate, Set.of());

        assertThat(snapshot).isEqualTo(expected);
        verify(questionGenerationPolicy).enrichViolations(candidate, normalizedBaseViolations, Set.of());
        verify(questionQualityScorer).score(normalizedEnrichedViolations);
        verify(questionQualitySnapshotFactory).create(normalizedEnrichedViolations, normalizedRetryFeedback, 88, false);
    }
}
