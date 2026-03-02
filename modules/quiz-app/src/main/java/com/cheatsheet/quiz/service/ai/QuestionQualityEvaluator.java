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

    private static final String REQUEST_FAILURE_VIOLATION = "AI did not return parsable question JSON payload";

    private final QuestionValidationService validationService;
    private final QuestionGenerationPolicy questionGenerationPolicy;
    private final QuestionRetryFeedbackNormalizer questionRetryFeedbackNormalizer;

    /**
     * Выполняет полный quality-check для кандидата.
     *
     * @param candidate        кандидат на приёмку
     * @param seenFingerprints отпечатки предыдущих попыток генерации
     * @return снапшот качества с нарушениями, score и итогом приёмки
     */
    public QualitySnapshot evaluateCandidate(Question candidate, Set<String> seenFingerprints) {
        List<String> baseViolations = validationService.validate(candidate);
        List<String> violations = questionGenerationPolicy.enrichViolations(candidate, baseViolations, seenFingerprints);
        List<String> retryFeedback = questionRetryFeedbackNormalizer.normalize(violations);
        int score = validationService.qualityScore(violations);
        boolean accepted = questionGenerationPolicy.isAccepted(score, violations);
        return new QualitySnapshot(violations, retryFeedback, score, accepted);
    }

    /**
     * Возвращает нормализованный retry-feedback для случая, когда AI не вернул парсируемый JSON.
     *
     * @return детерминированный список нарушений для следующей попытки генерации
     */
    public List<String> retryFeedbackForRequestFailure() {
        return questionRetryFeedbackNormalizer.normalize(List.of(REQUEST_FAILURE_VIOLATION));
    }

    /**
     * Immutable-снапшот результата quality-check.
     *
     * @param violations список нарушений
     * @param retryFeedback нормализованный список нарушений для передачи в retry-prompt
     * @param score         quality score (0..100)
     * @param accepted      признак приёмки кандидата
     */
    public record QualitySnapshot(List<String> violations, List<String> retryFeedback, int score, boolean accepted) {
    }
}
