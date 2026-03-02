package com.cheatsheet.quiz.service.ai;

import com.cheatsheet.quiz.domain.Question;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Сервис оценки качества сгенерированного вопроса.
 *
 * <p>Инкапсулирует полный пайплайн quality-check:
 * базовая валидация, policy enrichment, quality scoring и итоговое решение о приёмке.</p>
 */
@Service
@RequiredArgsConstructor
public class QuestionQualityEvaluator {

    private final QuestionValidationService validationService;
    private final QuestionQualityScorer questionQualityScorer;
    private final QuestionGenerationPolicy questionGenerationPolicy;
    private final QuestionRetryFeedbackNormalizer questionRetryFeedbackNormalizer;
    private final QuestionQualitySnapshotFactory questionQualitySnapshotFactory;
    private final QuestionRequestFailureFeedbackSupplier questionRequestFailureFeedbackSupplier;
    private final QuestionQualityMessageNormalizer questionQualityMessageNormalizer;
    private final QuestionSeenFingerprintSanitizer questionSeenFingerprintSanitizer;

    /**
     * Выполняет полный quality-check для кандидата.
     *
     * @param candidate        кандидат на приёмку
     * @param seenFingerprints отпечатки предыдущих попыток генерации
     * @return снапшот качества с нарушениями, score и итогом приёмки
     */
    public QuestionQualitySnapshot evaluateCandidate(Question candidate, Set<String> seenFingerprints) {
        Set<String> safeSeenFingerprints = safeSanitizedFingerprints(seenFingerprints);
        List<String> baseViolations = safeNormalizedMessages(validationService.validate(candidate));
        List<String> violations = safeNormalizedMessages(
                questionGenerationPolicy.enrichViolations(candidate, baseViolations, safeSeenFingerprints)
        );
        List<String> retryFeedback =
                safeNormalizedMessages(questionRetryFeedbackNormalizer.normalize(violations));
        int score = questionQualityScorer.score(violations);
        boolean accepted = questionGenerationPolicy.isAccepted(score, violations);
        return questionQualitySnapshotFactory.create(violations, retryFeedback, score, accepted);
    }

    /**
     * Возвращает нормализованный retry-feedback для случая, когда AI не вернул парсируемый JSON.
     *
     * @return детерминированный список нарушений для следующей попытки генерации
     */
    public List<String> retryFeedbackForRequestFailure() {
        return safeNormalizedMessages(questionRequestFailureFeedbackSupplier.feedback());
    }

    private Set<String> safeSanitizedFingerprints(Set<String> seenFingerprints) {
        Set<String> sanitized = questionSeenFingerprintSanitizer.sanitize(seenFingerprints);
        return sanitized == null ? Set.of() : sanitized;
    }

    private List<String> safeNormalizedMessages(List<String> messages) {
        List<String> normalized = questionQualityMessageNormalizer.normalize(messages);
        return normalized == null ? List.of() : normalized;
    }
}
