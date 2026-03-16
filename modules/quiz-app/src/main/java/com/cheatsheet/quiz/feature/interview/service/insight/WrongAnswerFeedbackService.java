package com.cheatsheet.quiz.feature.interview.service.insight;

import lombok.RequiredArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import com.cheatsheet.quiz.domain.AnswerOption;
import com.cheatsheet.quiz.persistence.AnswerOptionRepository;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.service.ai.AiQuestionClient;
import com.cheatsheet.quiz.service.event.AnswerEvent;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Персонализированный AI-фидбэк при неправильном ответе (Observer pattern).
 *
 * <p>Слушает {@link AnswerEvent}: при ошибке асинхронно запускает AI-генерацию
 * объяснения и кеширует результат. API-эндпоинт запрашивает фидбэк через
 * {@link #getFeedback(long, long)}.</p>
 */
@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WrongAnswerFeedbackService {

    private static final int FEEDBACK_TIMEOUT_SECONDS = 15;

    private final AiQuestionClient aiQuestionClient;
    private final QuestionRepository questionRepository;
    private final AnswerOptionRepository answerOptionRepository;

    private static final int CACHE_MAX_SIZE = 500;
    private static final int CACHE_EXPIRE_MINUTES = 30;

    /**
     * Кеш асинхронных фидбэков: ключ = "questionId:optionId".
     * Значение — CompletableFuture, которое заполняется AI-ответом.
     * Ограничен 500 записями, срок жизни записи — 30 минут после записи.
     */
    private final Cache<String, CompletableFuture<String>> feedbackCache = Caffeine.newBuilder()
            .maximumSize(CACHE_MAX_SIZE)
            .expireAfterWrite(CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES)
            .build();

    /**
     * Обработчик события ответа. При неправильном ответе запускает
     * асинхронную генерацию фидбэка.
     */
    @EventListener
    @Async
    public void onAnswer(AnswerEvent event) {
        if (event.isCorrect()) {
            return;
        }
        String cacheKey = cacheKey(event.getQuestionId(), event.getSelectedOptionId());
        feedbackCache.asMap().computeIfAbsent(cacheKey, k -> {
            log.info("Запуск генерации wrong-answer feedback для вопроса {} (выбран вариант {})",
                    event.getQuestionId(), event.getSelectedOptionId());
            CompletableFuture<String> future = new CompletableFuture<>();
            try {
                Optional<String> feedback = aiQuestionClient.generateWrongAnswerFeedback(
                        event.getQuestionText(),
                        event.getSelectedOptionText(),
                        event.getCorrectOptionText(),
                        event.getAnswerMarkdown()
                );
                future.complete(feedback.orElse(null));
            } catch (Exception e) {
                log.warn("Ошибка генерации wrong-answer feedback: {}", e.getMessage());
                future.complete(null);
            }
            return future;
        });
    }

    /**
     * Возвращает фидбэк, если он уже готов. Если AI ещё не ответил — ожидает до 15 секунд.
     * Если фидбэк не был запрошен — запрашивает синхронно.
     *
     * @param questionId ID вопроса
     * @param optionId   ID выбранного (неверного) варианта
     * @return текст фидбэка или пустой Optional
     */
    public Optional<String> getFeedback(long questionId, long optionId) {
        String cacheKey = cacheKey(questionId, optionId);
        CompletableFuture<String> future = feedbackCache.getIfPresent(cacheKey);
        if (future == null) {
            return Optional.empty();
        }
        try {
            String result = future.get(FEEDBACK_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            return Optional.ofNullable(result);
        } catch (Exception e) {
            log.warn("Не удалось получить wrong-answer feedback для {}: {}", cacheKey, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Генерирует сравнительную таблицу между выбранным и правильным вариантом.
     *
     * @param questionId      ID вопроса
     * @param selectedOptionId ID выбранного (неверного) варианта
     * @return JSON-строка с массивом criteria или пустой Optional
     */
    public Optional<String> generateComparison(long questionId, long selectedOptionId) {
        var question = questionRepository.findById(questionId);
        if (question.isEmpty()) {
            return Optional.empty();
        }
        List<AnswerOption> options = answerOptionRepository.findByQuestionId(questionId);
        String selectedText = null;
        String correctText = null;
        for (AnswerOption opt : options) {
            if (opt.id() == selectedOptionId) {
                selectedText = opt.optionText();
            }
            if (opt.correct()) {
                correctText = opt.optionText();
            }
        }
        if (selectedText == null || correctText == null) {
            return Optional.empty();
        }
        try {
            return aiQuestionClient.generateComparison(question.get().questionText(), selectedText, correctText)
                    .map(String::trim)
                    .filter(text -> !text.isBlank());
        } catch (Exception e) {
            log.warn("Ошибка генерации comparison для вопроса {}: {}", questionId, e.getMessage());
            return Optional.empty();
        }
    }

    private static String cacheKey(long questionId, long optionId) {
        return questionId + ":" + optionId;
    }
}
