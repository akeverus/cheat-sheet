package com.cheatsheet.quiz.service.ai.dto;

import java.util.List;
import lombok.Builder;

/**
 * Результат генерации вариантов ответа от AI-провайдера.
 *
 * @param options список опций, как их вернула модель (text + correct)
 */
@Builder(toBuilder = true)
public record GeneratedOptions(
        List<GeneratedOption> options
) {
    @Builder(toBuilder = true)
    public record GeneratedOption(
            String text,
            boolean correct
    ) {
    }
}
