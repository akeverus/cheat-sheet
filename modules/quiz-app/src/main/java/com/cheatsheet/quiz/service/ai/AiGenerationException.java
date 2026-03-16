package com.cheatsheet.quiz.service.ai;

import lombok.experimental.StandardException;

/**
 * Ошибка генерации AI-контента, когда ответ пустой или невалидный.
 */
@StandardException
public class AiGenerationException extends RuntimeException {
}
