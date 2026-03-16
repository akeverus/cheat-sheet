package com.cheatsheet.quiz.feature.admin.service;

import lombok.RequiredArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import com.cheatsheet.quiz.domain.exception.QuestionNotFoundException;
import com.cheatsheet.quiz.persistence.AnswerOptionRepository;
import com.cheatsheet.quiz.persistence.HintRepository;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.service.cache.OptionCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Сервис перегенерации артефактов вопроса (варианты, подсказки, диаграмма).
 */
@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RegenerateService {

    private final AnswerOptionRepository answerOptionRepository;
    private final HintRepository hintRepository;
    private final OptionCache optionCache;
    private final QuestionRepository questionRepository;

    /**
     * Удаляет сгенерированные артефакты вопроса.
     *
     * @param questionId идентификатор вопроса
     */
    @Transactional
    public void regenerateQuestion(long questionId) {
        log.info("Начало перегенерации вопроса: questionId={}", questionId);
        if (questionRepository.findById(questionId).isEmpty()) {
            log.warn("Перегенерация отклонена: вопрос не найден, questionId={}", questionId);
            throw new QuestionNotFoundException("Вопрос не найден: id=" + questionId);
        }
        try {
            answerOptionRepository.deleteByQuestionId(questionId);
            hintRepository.deleteByQuestionId(questionId);
            optionCache.invalidate(questionId);
            questionRepository.incrementRegenCount(questionId);
            questionRepository.updateDiagram(questionId, null);
            log.info("Перегенерация завершена успешно: questionId={}", questionId);
        } catch (Exception e) {
            log.error("Ошибка перегенерации вопроса: questionId={}, error={}", questionId, e.getMessage(), e);
            throw e;
        }
    }
}
