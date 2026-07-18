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

/**
 * Пишет построчный лог попыток ответа ({@code attempt}).
 *
 * <p>Отдельно от {@link ReviewService} (агрегаты SM-2): {@link ReviewService}
 * держит текущее состояние повторений, а {@link AttemptRecorder} — неизменяемый
 * журнал каждого ответа для честной аналитики (first-attempt accuracy, response
 * time, retention) и истории грейдов FSRS. Запись идёт в собственной транзакции
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

    /**
     * Записывает одну попытку ответа.
     *
     * <p>{@code is_first_attempt} определяется по отсутствию прежних попыток
     * ({@link AttemptRepository#existsByQuestionId}). Попытка привязывается к
     * последней ревизии контента вопроса, если она есть.</p>
     *
     * @param questionId       вопрос
     * @param outcome          исход попытки
     * @param memoryGrade      объявленный грейд (уверенность 3–5 / 0 для «не знаю»); {@code null} если нет
     * @param selectedOptionId выбранный вариант ({@code null} для флешкарт/«не знаю»)
     * @param responseTimeMs   время ответа в мс ({@code null} если не измерено)
     * @param sessionToken     токен HTTP-сессии тренажёра ({@code null} допустим)
     * @param clientAttemptId  клиентский ключ идемпотентности (BE-003): один на вопрос,
     *                         тот же при retry после сетевой ошибки; {@code null} допустим
     *                         (skip/флешкарта/no-JS) — тогда дедупликация не применяется
     */
    @Transactional
    public void record(long questionId,
                       AttemptOutcome outcome,
                       Integer memoryGrade,
                       Long selectedOptionId,
                       Integer responseTimeMs,
                       String sessionToken,
                       String clientAttemptId) {
        // BE-003: если попытка с этим клиентским ключом уже записана (retry после
        // сетевой ошибки, двойной submit в обход клиентского guard) — не дублируем.
        // Ключ null/blank (skip, no-JS-фоллбэк) дедупликацию отключает: journal тогда
        // ведёт себя как раньше. Дополняет сессионный FLOW-02 (InterviewSessionSupport)
        // на путях без активной сессии (TRAINING) и finished-сессии.
        if (clientAttemptId != null && !clientAttemptId.isBlank()
                && attemptRepository.findByIdempotencyKey(clientAttemptId).isPresent()) {
            return;
        }
        boolean firstAttempt = !attemptRepository.existsByQuestionId(questionId);
        Long revisionId = questionRevisionRepository.findLatest(questionId)
                .map(QuestionRevision::id)
                .orElse(null);
        Attempt attempt = Attempt.builder()
                .questionId(questionId)
                .questionRevisionId(revisionId)
                .selectedOptionId(selectedOptionId)
                .outcome(outcome)
                .firstAttempt(firstAttempt)
                .responseTimeMs(responseTimeMs)
                .memoryGrade(memoryGrade)
                .sessionToken(sessionToken)
                .idempotencyKey(normalizeKey(clientAttemptId))
                .createdAt(clock.instant().getEpochSecond())
                .build();
        attemptRepository.insert(attempt);
    }

    /**
     * Нормализует клиентский ключ: blank → {@code null}, чтобы в БД не попадали
     * пустые строки (partial unique index {@code uq_attempt_idempotency_key}
     * покрывает только {@code idempotency_key IS NOT NULL}).
     */
    private String normalizeKey(String clientAttemptId) {
        return clientAttemptId == null || clientAttemptId.isBlank() ? null : clientAttemptId;
    }
}
