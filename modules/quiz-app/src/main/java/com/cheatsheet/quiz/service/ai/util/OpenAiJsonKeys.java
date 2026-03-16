package com.cheatsheet.quiz.service.ai.util;

import com.cheatsheet.quiz.service.ai.client.AbstractAiClient;
import lombok.experimental.UtilityClass;

/**
 * Ключи JSON-ответа OpenAI-совместимого Chat Completion API и структурированных ответов модели.
 *
 * <p>Используются при парсинге ответов в {@link AbstractAiClient}
 * и в модуле {@code ai-openai-client}.</p>
 */
@UtilityClass
public class OpenAiJsonKeys {

    /** Ключ массива вариантов ответа в ответе API. */
    public static final String CHOICES = "choices";

    /** Ключ объекта сообщения. */
    public static final String MESSAGE = "message";

    /** Ключ текста сообщения. */
    public static final String CONTENT = "content";

    /** Альтернативный ключ текста (старый формат API). */
    public static final String TEXT = "text";

    /** Ключ текста варианта внутри объекта wrong. */
    public static final String TEXT_FIELD = "text";

    /** Ключ списка альтернативных вопросов. */
    public static final String QUESTIONS = "questions";

    /** Ключ списка подсказок. */
    public static final String HINTS = "hints";

    /** Ключ флага «диаграмма нужна». */
    public static final String NEEDED = "needed";

    /** Ключ Mermaid-кода диаграммы. */
    public static final String MERMAID = "mermaid";

    /** Ключ ключевого вывода (takeaway). */
    public static final String TAKEAWAY = "takeaway";
}
