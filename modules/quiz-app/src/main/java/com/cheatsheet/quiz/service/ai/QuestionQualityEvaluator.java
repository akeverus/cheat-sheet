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
        Set<String> safeSeenFingerprints = questionSeenFingerprintSanitizer.sanitize(seenFingerprints);
        List<String> baseViolations = questionQualityMessageNormalizer.normalize(validationService.validate(candidate));
        List<String> violations = questionQualityMessageNormalizer.normalize(
                questionGenerationPolicy.enrichViolations(candidate, baseViolations, safeSeenFingerprints)
        );
        List<String> retryFeedback =
                questionQualityMessageNormalizer.normalize(questionRetryFeedbackNormalizer.normalize(violations));
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
        return questionQualityMessageNormalizer.normalize(questionRequestFailureFeedbackSupplier.feedback());
    }
}
