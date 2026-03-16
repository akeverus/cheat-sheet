package com.cheatsheet.quiz.feature.question.engine.metadata;

import com.cheatsheet.quiz.config.app.AppProperties;
import com.cheatsheet.quiz.feature.question.engine.model.GeneratedQuestionMetadata;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

/**
 * Поставщик служебных метаданных для AI-сгенерированных вопросов.
 */
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
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
