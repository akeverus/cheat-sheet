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
    private QuestionGenerationPolicy questionGenerationPolicy;
    @Mock
    private Question candidate;

    @Test
    void evaluateCandidateBuildsAcceptedSnapshot() {
        QuestionQualityEvaluator evaluator = new QuestionQualityEvaluator(validationService, questionGenerationPolicy);
        Set<String> seenFingerprints = Set.of("q1::a|b|c|d");
        List<String> baseViolations = List.of();
        List<String> enrichedViolations = List.of();
        when(validationService.validate(candidate)).thenReturn(baseViolations);
        when(questionGenerationPolicy.enrichViolations(candidate, baseViolations, seenFingerprints)).thenReturn(enrichedViolations);
        when(validationService.qualityScore(enrichedViolations)).thenReturn(96);
        when(questionGenerationPolicy.isAccepted(96, enrichedViolations)).thenReturn(true);

        QuestionQualityEvaluator.QualitySnapshot snapshot = evaluator.evaluateCandidate(candidate, seenFingerprints);

        assertThat(snapshot.score()).isEqualTo(96);
        assertThat(snapshot.violations()).isEqualTo(enrichedViolations);
        assertThat(snapshot.accepted()).isTrue();
        verify(validationService).validate(candidate);
        verify(questionGenerationPolicy).enrichViolations(candidate, baseViolations, seenFingerprints);
        verify(validationService).qualityScore(enrichedViolations);
        verify(questionGenerationPolicy).isAccepted(96, enrichedViolations);
    }

    @Test
    void evaluateCandidateBuildsRejectedSnapshot() {
        QuestionQualityEvaluator evaluator = new QuestionQualityEvaluator(validationService, questionGenerationPolicy);
        Set<String> seenFingerprints = Set.of();
        List<String> baseViolations = List.of("shortExplanation must be at least 30 characters");
        List<String> enrichedViolations = List.of(
                "shortExplanation must be at least 30 characters",
                "Question cognitive load is too high: question text is too long"
        );
        when(validationService.validate(candidate)).thenReturn(baseViolations);
        when(questionGenerationPolicy.enrichViolations(candidate, baseViolations, seenFingerprints)).thenReturn(enrichedViolations);
        when(validationService.qualityScore(enrichedViolations)).thenReturn(54);
        when(questionGenerationPolicy.isAccepted(54, enrichedViolations)).thenReturn(false);

        QuestionQualityEvaluator.QualitySnapshot snapshot = evaluator.evaluateCandidate(candidate, seenFingerprints);

        assertThat(snapshot.score()).isEqualTo(54);
        assertThat(snapshot.violations()).isEqualTo(enrichedViolations);
        assertThat(snapshot.accepted()).isFalse();
    }
}
