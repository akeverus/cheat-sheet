package com.cheatsheet.quiz.service.ai;

import org.springframework.stereotype.Component;

/**
 * Поставщик служебных метаданных для AI-сгенерированных вопросов.
 */
@Component
public class QuestionGeneratedMetadataSupplier {

    /**
     * Возвращает значение sourceSlug для сгенерированного вопроса.
     */
    public String sourceSlug() {
        return "generated";
    }

    /**
     * Возвращает значение filePath для сгенерированного вопроса.
     */
    public String filePath() {
        return "generated";
    }

    /**
     * Возвращает значение sourceHash для сгенерированного вопроса.
     */
    public String sourceHash() {
        return "generated";
    }
}
