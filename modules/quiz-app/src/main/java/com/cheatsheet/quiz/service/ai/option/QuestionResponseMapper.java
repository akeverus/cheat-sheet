package com.cheatsheet.quiz.service.ai.option;

import com.cheatsheet.quiz.domain.OptionSource;
import com.cheatsheet.quiz.persistence.AnswerOptionRepository.AnswerOptionCreate;
import com.cheatsheet.quiz.service.ai.dto.GeneratedOptions;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

/**
 * Маппит LLM JSON payload в persistence model.
 */
@UtilityClass
public class QuestionResponseMapper {
    private static final int PERSISTED_PROMPT_METADATA_VERSION = 0;

    public static List<AnswerOptionCreate> mapToCreates(
            GeneratedOptions generated,
            OptionSource source
    ) {
        List<GeneratedOptions.GeneratedOption> generatedOptions =
                generated.options() == null ? List.of() : generated.options();
        List<AnswerOptionCreate> results = new ArrayList<>(generatedOptions.size());
        for (int displayOrder = 0; displayOrder < generatedOptions.size(); displayOrder++) {
            GeneratedOptions.GeneratedOption option = generatedOptions.get(displayOrder);
            results.add(new AnswerOptionCreate(
                    option.text(),
                    option.correct(),
                    displayOrder,
                    source.name(),
                    null,
                    PERSISTED_PROMPT_METADATA_VERSION,
                    PERSISTED_PROMPT_METADATA_VERSION
            ));
        }
        return results;
    }
}
