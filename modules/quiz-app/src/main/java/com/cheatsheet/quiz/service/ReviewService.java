package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.ReviewDefaults;
import com.cheatsheet.quiz.domain.ReviewState;
import com.cheatsheet.quiz.domain.exception.QuestionNotFoundException;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.persistence.ReviewStateRepository;
import com.cheatsheet.quiz.persistence.UserTopicStatsRepository;
import com.cheatsheet.quiz.service.event.AnswerEvent;
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
public class ReviewService {

    private final ReviewStateRepository reviewStateRepository;
    private final SpacedRepetitionService spacedRepetitionService;
    private final UserTopicStatsRepository userTopicStatsRepository;
    private final QuestionRepository questionRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final Clock clock;

    public ReviewService(
            ReviewStateRepository reviewStateRepository,
            SpacedRepetitionService spacedRepetitionService,
            UserTopicStatsRepository userTopicStatsRepository,
            QuestionRepository questionRepository,
            ApplicationEventPublisher eventPublisher,
            Clock clock
    ) {
        this.reviewStateRepository = reviewStateRepository;
        this.spacedRepetitionService = spacedRepetitionService;
        this.userTopicStatsRepository = userTopicStatsRepository;
        this.questionRepository = questionRepository;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

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
