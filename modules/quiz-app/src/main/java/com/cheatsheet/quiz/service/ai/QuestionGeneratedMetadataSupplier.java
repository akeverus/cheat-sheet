package com.cheatsheet.quiz.service.ai;

import com.cheatsheet.quiz.config.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Поставщик служебных метаданных для AI-сгенерированных вопросов.
 */
@Component
@RequiredArgsConstructor
public class QuestionGeneratedMetadataSupplier {

    private final AppProperties appProperties;

    /**
     * Возвращает значение sourceSlug для сгенерированного вопроса.
     */
    public String sourceSlug() {
        return appProperties.getInterview().getQuestionGeneratedMetadataValue();
    }

    /**
     * Возвращает значение filePath для сгенерированного вопроса.
     */
    public String filePath() {
        return appProperties.getInterview().getQuestionGeneratedMetadataValue();
    }

    /**
     * Возвращает значение sourceHash для сгенерированного вопроса.
     */
    public String sourceHash() {
        return appProperties.getInterview().getQuestionGeneratedMetadataValue();
    }
}
