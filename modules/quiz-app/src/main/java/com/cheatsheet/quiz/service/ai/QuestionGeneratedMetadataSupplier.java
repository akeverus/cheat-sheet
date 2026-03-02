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
     * Возвращает immutable-набор метаданных для сгенерированного вопроса.
     */
    public GeneratedQuestionMetadata metadata() {
        String value = appProperties.getInterview().getQuestionGeneratedMetadataValue();
        return GeneratedQuestionMetadata.builder()
                .sourceSlug(value)
                .filePath(value)
                .sourceHash(value)
                .build();
    }
}
