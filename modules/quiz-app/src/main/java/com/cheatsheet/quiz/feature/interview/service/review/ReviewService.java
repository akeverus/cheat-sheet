package com.cheatsheet.quiz.feature.interview.service.review;

import com.cheatsheet.quiz.config.app.AppProperties;
import com.cheatsheet.quiz.domain.FsrsRating;
import com.cheatsheet.quiz.domain.FsrsState;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.ReviewDefaults;
import com.cheatsheet.quiz.domain.ReviewResult;
import com.cheatsheet.quiz.domain.ReviewState;
import com.cheatsheet.quiz.domain.exception.QuestionNotFoundException;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.persistence.ReviewStateRepository;
import com.cheatsheet.quiz.persistence.UserTopicStatsRepository;
import com.cheatsheet.quiz.service.event.AnswerEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

/**
 * Сервис обновления состояния повторений и сигналов мастерства.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewService {

    private final ReviewStateRepository reviewStateRepository;
    private final SpacedRepetitionService spacedRepetitionService;
    private final FsrsService fsrsService;
    private final UserTopicStatsRepository userTopicStatsRepository;
    private final QuestionRepository questionRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final AppProperties appProperties;
    private final Clock clock;

    /**
     * Применяет результат ответа к состоянию повторений и статистике темы.
     *
     * @param question вопрос
     * @param correct правильность ответа
     * @param confidence опциональная уверенность 3..5
     * @return обновлённое состояние повторений
     */
    @Transactional
    public ReviewState applyAnswer(Question question, boolean correct, Integer confidence) {
        if (appProperties.getScheduling().isFsrs()) {
            ReviewResult lastResult = correct ? ReviewResult.CORRECT : ReviewResult.WRONG;
            return applyWithFsrs(question, mapRating(correct, confidence), correct, lastResult);
        }
        long questionId = question.id();
        ReviewState current = reviewStateRepository.findByQuestionId(questionId)
                .orElse(ReviewDefaults.initialState(questionId, clock.instant().getEpochSecond()));
        ReviewState updated = confidence != null
                ? spacedRepetitionService.applyAnswer(current, correct, confidence)
                : spacedRepetitionService.applyAnswer(current, correct);
        reviewStateRepository.update(updated);
        userTopicStatsRepository.recordAnswer(question.topic(), correct);
        return updated;
    }

    /**
     * Применяет исход «не знаю» (UNKNOWN, FLOW-03): SM-2-лапс с грейдом 0 и
     * пометкой {@code last_result = UNKNOWN}. Для мастерства темы засчитывается
     * как незнание (incorrect).
     *
     * @param question вопрос
     * @return обновлённое состояние повторений
     */
    @Transactional
    public ReviewState applyUnknown(Question question) {
        if (appProperties.getScheduling().isFsrs()) {
            return applyWithFsrs(question, FsrsRating.AGAIN, false, ReviewResult.UNKNOWN);
        }
        long questionId = question.id();
        ReviewState current = reviewStateRepository.findByQuestionId(questionId)
                .orElse(ReviewDefaults.initialState(questionId, clock.instant().getEpochSecond()));
        ReviewState updated = spacedRepetitionService.applyUnknown(current);
        reviewStateRepository.update(updated);
        userTopicStatsRepository.recordAnswer(question.topic(), false);
        return updated;
    }

    /**
     * Общий FSRS-путь для {@link #applyAnswer}/{@link #applyUnknown}. Читает SM-2
     * и FSRS-состояние из одной строки; при первом FSRS-апдележе, если есть
     * накопленный SM-2-прогресс, сидирует память из него (не теряем историю).
     * Пишет FSRS-память + общие поля расписания через
     * {@link ReviewStateRepository#updateFsrs}; SM-2-колонки не трогает.
     *
     * @return display-состояние (общие поля обновлены, интервал — из FSRS)
     */
    private ReviewState applyWithFsrs(Question question, FsrsRating rating, boolean correct, ReviewResult lastResult) {
        long questionId = question.id();
        long now = clock.instant().getEpochSecond();
        ReviewState sm2 = reviewStateRepository.findByQuestionId(questionId)
                .orElse(ReviewDefaults.initialState(questionId, now));
        FsrsState fsrs = reviewStateRepository.findFsrsState(questionId)
                .orElse(new FsrsState(questionId, null, null, 0, null, null));
        if (fsrs.isNew() && sm2.repetitions() > 0) {
            FsrsService.Seed seed = fsrsService.seedFromSm2(sm2.intervalDays(), sm2.easeFactor());
            fsrs = fsrs.toBuilder().stability(seed.stability()).difficulty(seed.difficulty()).build();
        }
        FsrsService.Result result = fsrsService.schedule(fsrs, rating, now);
        int correctCount = sm2.correctCount() + (correct ? 1 : 0);
        int wrongCount = sm2.wrongCount() + (correct ? 0 : 1);
        FsrsState updated = new FsrsState(questionId, result.stability(), result.difficulty(),
                result.lapses(), FsrsService.ALGO_VERSION, result.lastReviewedAt());
        reviewStateRepository.updateFsrs(updated, result.nextReviewAt(), lastResult, correctCount, wrongCount);
        userTopicStatsRepository.recordAnswer(question.topic(), correct);
        return sm2.toBuilder()
                .nextReviewAt(result.nextReviewAt())
                .intervalDays(result.intervalDays())
                .lastResult(lastResult)
                .correctCount(correctCount)
                .wrongCount(wrongCount)
                .build();
    }

    /**
     * Маппинг сигналов тренажёра в оценку FSRS: неверно → AGAIN; верно без
     * уверенности → GOOD; уверенность 3 «угадал» → HARD, 4 → GOOD, 5 «легко» → EASY.
     */
    private static FsrsRating mapRating(boolean correct, Integer confidence) {
        if (!correct) {
            return FsrsRating.AGAIN;
        }
        if (confidence == null) {
            return FsrsRating.GOOD;
        }
        return switch (Math.max(3, Math.min(5, confidence))) {
            case 3 -> FsrsRating.HARD;
            case 5 -> FsrsRating.EASY;
            default -> FsrsRating.GOOD;
        };
    }

    /**
     * Текущее (персистентное) состояние повторений вопроса — БЕЗ изменений.
     * Используется для идемпотентного повтора ответа (FLOW-02): при устаревшем
     * дублирующем POST нужно вернуть уже сохранённое состояние, не применяя SM-2
     * повторно. Если состояния ещё нет — дефолтное начальное (как в applyAnswer).
     *
     * @param questionId id вопроса
     * @return сохранённое или начальное состояние повторений (не записывается)
     */
    @Transactional(readOnly = true)
    public ReviewState currentState(long questionId) {
        return reviewStateRepository.findByQuestionId(questionId)
                .orElse(ReviewDefaults.initialState(questionId, clock.instant().getEpochSecond()));
    }

    /**
     * Корректирует уверенность после первичного ответа.
     *
     * @param questionId id вопроса
     * @param grade оценка уверенности 3..5
     */
    @Transactional
    public void updateConfidence(long questionId, int grade) {
        questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException("Вопрос не найден: id=" + questionId));
        if (grade == 5) {
            return;
        }
        ReviewState current = reviewStateRepository.findByQuestionId(questionId).orElse(null);
        if (current == null) {
            throw new IllegalStateException("Нельзя обновить уверенность до первого ответа: questionId=" + questionId);
        }
        int boundedGrade = Math.max(3, Math.min(5, grade));
        double easeDelta = SpacedRepetitionService.computeEaseDelta(boundedGrade)
                - SpacedRepetitionService.computeEaseDelta(5);
        double newEase = Math.max(ReviewDefaults.MIN_EASE_FACTOR, current.easeFactor() + easeDelta);
        reviewStateRepository.update(current.withEaseFactor(newEase));
    }

    /**
     * Применяет оценку flashcard-режима и публикует событие ответа.
     *
     * @param questionId id вопроса
     * @param correct корректность ответа
     * @param sm2Grade оценка для SM-2 (1..5)
     */
    @Transactional
    public void submitFlashcardGrade(long questionId, boolean correct, int sm2Grade) {
        ReviewState current = reviewStateRepository.findByQuestionId(questionId)
                .orElse(ReviewDefaults.initialState(questionId, clock.instant().getEpochSecond()));
        ReviewState updated = spacedRepetitionService.applyAnswer(current, correct, sm2Grade);
        reviewStateRepository.update(updated);

        try {
            Question question = questionRepository.findById(questionId).orElse(null);
            if (question != null) {
                eventPublisher.publishEvent(new AnswerEvent(
                        this, questionId, 0, 0, correct,
                        question.questionText(), "", "",
                        question.answerMarkdown(), question.topic()
                ));
            }
        } catch (Exception e) {
            log.warn("Не удалось опубликовать AnswerEvent для flashcard вопроса id={}: {}", questionId, e.getMessage());
        }
    }
}
