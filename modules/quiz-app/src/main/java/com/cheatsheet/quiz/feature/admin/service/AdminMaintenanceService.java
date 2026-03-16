package com.cheatsheet.quiz.feature.admin.service;

import com.cheatsheet.quiz.persistence.AnswerOptionRepository;
import com.cheatsheet.quiz.persistence.HintRepository;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import lombok.AccessLevel;
import com.cheatsheet.quiz.service.cache.OptionCache;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.Builder;

/**
 * Сервис административных операций обслуживания данных AI-артефактов.
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminMaintenanceService {
    AnswerOptionRepository answerOptionRepository;
    HintRepository hintRepository;
    QuestionRepository questionRepository;
    OptionCache optionCache;

    /**
     * Очищает все варианты ответов и инвалидирует кэш.
     */
    @Transactional
    public int clearOptions() {
        int deleted = answerOptionRepository.deleteAll();
        optionCache.invalidateAll();
        return deleted;
    }

    /**
     * Полный сброс AI-артефактов (options/hints/diagram/regen_count).
     */
    @Transactional
    public ResetAllResult resetAll() {
        int deletedOptions = answerOptionRepository.deleteAll();
        int deletedHints = hintRepository.deleteAll();
        int clearedDiagrams = questionRepository.clearAllDiagrams();
        int resetRegenCount = questionRepository.resetAllRegenCount();
        optionCache.invalidateAll();
        return new ResetAllResult(deletedOptions, deletedHints, clearedDiagrams, resetRegenCount);
    }

    @Builder(toBuilder = true)
    public record ResetAllResult(
            int deletedOptions,
            int deletedHints,
            int clearedDiagrams,
            int resetRegenCount
    ) {
    }
}
