package com.cheatsheet.quiz.feature.interview.service.review;

import com.cheatsheet.quiz.domain.Attempt;
import com.cheatsheet.quiz.domain.AttemptOutcome;
import com.cheatsheet.quiz.domain.QuestionRevision;
import com.cheatsheet.quiz.persistence.AttemptRepository;
import com.cheatsheet.quiz.persistence.QuestionRevisionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.Optional;

/**
 * Пишет построчный лог попыток ответа ({@code attempt}).
 *
 * <p>Отдельно от {@link ReviewService} (агрегаты SM-2): {@link ReviewService}
 * держит текущее состояние повторений, а {@link AttemptRecorder} — неизменяемый
 * журнал каждого ответа для честной аналитики. Запись идёт в собственной транзакции
 * и НЕ должна ронять пользовательский флоу — вызывающий оборачивает её в
 * try/catch (журнал — не критичный путь ответа).</p>
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AttemptRecorder {

    AttemptRepository attemptRepository;
    QuestionRevisionRepository questionRevisionRepository;
    Clock clock;

    @Transactional(readOnly = true)
    public Optional<Attempt> findByClientAttemptId(String clientAttemptId) {
        String key = normalizeKey(clientAttemptId);
        return key == null ? Optional.empty() : attemptRepository.findByIdempotencyKey(key);
    }

    @Transactional(readOnly = true)
    public QuestionRevision requireCurrentRevision(long questionId, Long expectedRevisionId) {
        return questionRevisionRepository.findLatest(questionId)
                .filter(revision -> expectedRevisionId != null && revision.id() == expectedRevisionId)
                .orElseThrow(() -> new RevisionMismatchException("Question revision is no longer current"));
    }

    @Transactional
    public void record(long questionId,
                       AttemptOutcome outcome,
                       Integer memoryGrade,
                       Long selectedOptionId,
                       Integer responseTimeMs,
                       String sessionToken,
                       String clientAttemptId) {
        record(questionId, outcome, memoryGrade, selectedOptionId, responseTimeMs,
                sessionToken, clientAttemptId, null, null);
    }

    @Transactional
    public void record(long questionId,
                       AttemptOutcome outcome,
                       Integer memoryGrade,
                       Long selectedOptionId,
                       Integer responseTimeMs,
                       String sessionToken,
                       String clientAttemptId,
                       String openedContentBlockIds) {
        record(questionId, outcome, memoryGrade, selectedOptionId, responseTimeMs,
                sessionToken, clientAttemptId, openedContentBlockIds, null);
    }

    /**
     * @param questionRevisionId ревизия, которую видел пользователь; {@code null} → latest fallback
     */
    @Transactional
    public void record(long questionId,
                       AttemptOutcome outcome,
                       Integer memoryGrade,
                       Long selectedOptionId,
                       Integer responseTimeMs,
                       String sessionToken,
                       String clientAttemptId,
                       String openedContentBlockIds,
                       Long questionRevisionId) {
        String key = normalizeKey(clientAttemptId);
        if (key != null && attemptRepository.findByIdempotencyKey(key).isPresent()) {
            return;
        }
        Long revisionId = resolveRevisionId(questionId, questionRevisionId);
        Attempt attempt = Attempt.builder()
                .questionId(questionId)
                .questionRevisionId(revisionId)
                .selectedOptionId(selectedOptionId)
                .outcome(outcome)
                .firstAttempt(false)
                .responseTimeMs(responseTimeMs)
                .memoryGrade(memoryGrade)
                .sessionToken(sessionToken)
                .idempotencyKey(key)
                .openedContentBlockIds(normalizeKey(openedContentBlockIds))
                .createdAt(clock.instant().getEpochSecond())
                .build();
        if (key != null) {
            attemptRepository.insertIgnoreDuplicate(attempt);
        } else {
            attemptRepository.insert(attempt);
        }
    }

    /**
     * Резервирует ключ до SM-2/session (BE-001). Без ключа всегда {@code true}.
     */
    @Transactional
    public boolean tryClaimIdempotencyKey(String clientAttemptId) {
        String key = normalizeKey(clientAttemptId);
        if (key == null) {
            return true;
        }
        return attemptRepository.tryClaimIdempotencyKey(key, clock.instant().getEpochSecond());
    }

    /**
     * Перед оценкой ответа клиентский revisionId должен совпадать с последней
     * ревизией вопроса. Существующая старая ревизия тоже даёт конфликт (409).
     * {@code null}/неположительный id допускается (no-JS → latest при record).
     */
    public void requireValidRevision(long questionId, Long clientRevisionId) {
        if (clientRevisionId == null || clientRevisionId <= 0) {
            return;
        }
        requireCurrentRevision(questionId, clientRevisionId);
    }

    private Long resolveRevisionId(long questionId, Long clientRevisionId) {
        if (clientRevisionId != null && clientRevisionId > 0) {
            return questionRevisionRepository.findById(clientRevisionId)
                    .filter(rev -> rev.questionId() == questionId)
                    .map(QuestionRevision::id)
                    .orElseThrow(() -> new RevisionMismatchException(
                            "Question revision does not match the question shown; refresh and retry"));
        }
        return questionRevisionRepository.findLatest(questionId)
                .map(QuestionRevision::id)
                .orElse(null);
    }

    private String normalizeKey(String clientAttemptId) {
        return clientAttemptId == null || clientAttemptId.isBlank() ? null : clientAttemptId;
    }
}
