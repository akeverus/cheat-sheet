package com.cheatsheet.quiz.service.ai;

import lombok.Builder;
import lombok.Value;

/**
 * Immutable-метаданные для AI-сгенерированного вопроса.
 */
@Value
@Builder
public class GeneratedQuestionMetadata {

    /**
     * Значение sourceSlug для generated-вопроса.
     */
    String sourceSlug;

    /**
     * Значение filePath для generated-вопроса.
     */
    String filePath;

    /**
     * Значение sourceHash для generated-вопроса.
     */
    String sourceHash;
}
