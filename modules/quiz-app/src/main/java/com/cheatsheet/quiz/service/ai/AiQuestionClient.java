package com.cheatsheet.quiz.service.ai;

import com.cheatsheet.quiz.domain.OptionSource;
import java.util.Optional;

/**
 * Контракт интеграции с LLM для генерации учебного контента.
 */
public interface AiQuestionClient {

    OptionSource sourceId();

    /** Выполняет один LLM-вызов по заранее собранному промпту и возвращает сырой JSON-ответ. */
    Optional<String> generateStructuredJson(String prompt);
}
