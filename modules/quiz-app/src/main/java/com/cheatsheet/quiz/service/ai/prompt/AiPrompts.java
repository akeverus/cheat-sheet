package com.cheatsheet.quiz.service.ai.prompt;

import lombok.experimental.UtilityClass;

/**
 * Общие промпты для всех AI-клиентов (OpenAI, DeepSeek, Spring AI).
 *
 * <p>Централизованное хранение промптов предотвращает рассинхронизацию
 * при изменении формулировок. Шаблоны загружаются из classpath (prompts/*.txt).</p>
 */
@UtilityClass
public class AiPrompts {

    /** Системный промпт: контекст для AI-модели. */
    public static final String CODE_ONLY_SYSTEM_PROMPT =
            "Верни только код без markdown и пояснений.";

    /** Системный промпт: контекст для AI-модели. */
    public static final String SYSTEM_PROMPT =
            "Ты помощник для подготовки к собеседованию. Отвечай строго на русском языке, если не указано иное. " +
            "Отвечай строго по формату из запроса. " +
            "Если требуется JSON, верни только валидный JSON без markdown и комментариев. " +
            "Пиши кратко и по делу, без лишних вступлений.";

    /** Шаблон пользовательского промпта для генерации вариантов ответа. */
    public static final String USER_PROMPT_TEMPLATE = PromptLoader.load("option");

    /** Шаблон промпта для генерации вариантов ответа на вопрос с кодом. */
    public static final String CODE_OPTIONS_PROMPT_TEMPLATE = PromptLoader.load("code-options");

    /** Шаблон промпта для генерации альтернативных формулировок вопроса. */
    public static final String ALTERNATIVE_QUESTIONS_PROMPT_TEMPLATE = PromptLoader.load("alternative-questions");

    /** Шаблон промпта для очистки фрагмента кода от комментариев-подсказок. */
    public static final String CLEAN_CODE_PROMPT_TEMPLATE = PromptLoader.load("clean-code");

    /** Шаблон промпта для генерации 3 прогрессивных подсказок к вопросу. */
    public static final String HINTS_PROMPT_TEMPLATE = PromptLoader.load("hints");

    /** Шаблон промпта для генерации Mermaid-диаграммы к вопросу. */
    public static final String DIAGRAM_PROMPT_TEMPLATE = PromptLoader.load("diagram");

    /** Шаблон промпта для персонализированного фидбэка при неправильном ответе. */
    public static final String WRONG_ANSWER_FEEDBACK_TEMPLATE = PromptLoader.load("wrong-answer-feedback");

    /** Шаблон промпта для генерации Key Takeaway -- главный вывод для запоминания. */
    public static final String TAKEAWAY_PROMPT_TEMPLATE = PromptLoader.load("takeaway");

    /** Шаблон промпта для сравнительной таблицы при неправильном ответе. */
    public static final String COMPARISON_PROMPT_TEMPLATE = PromptLoader.load("comparison");

    /** Шаблон промпта для пошагового trace кода. */
    public static final String CODE_TRACE_PROMPT_TEMPLATE = PromptLoader.load("code-trace");

    /** Шаблон промпта для канонизации вопроса из markdown-источника. */
    public static final String CANONICALIZE_QUESTION_PROMPT_TEMPLATE = """
            Преобразуй сырой вопрос и сырой markdown-ответ в финальную карточку для тренировки.
            Источник: %s

            Сырой вопрос: %s
            Сырой markdown-ответ:
            %s

            Правила:
            - Итог должен быть полезным для тестирования, но без выдумывания фактов вне исходного контекста.
            - Язык результата — русский (кроме общепринятых технических терминов и токенов кода).
            - questionText: чёткий проверяемый вопрос.
            - answerMarkdown: структурированный и понятный ответ в markdown.
            - questionType: TEXT или CODE.
            - codeSnippet: обязателен только для questionType=CODE, иначе null.
            - Для questionType=TEXT запрещено помещать в answerMarkdown посторонние размышления о найме/коммуникации, если их нет в источнике.
            - Для questionType=CODE codeSnippet должен быть исполняемым и без спойлер-комментариев в стиле "правильный ответ: ...".
            - Верни только валидный JSON без markdown-обёрток.

            Формат:
            {"questionText":"...","answerMarkdown":"...","questionType":"TEXT|CODE","codeSnippet":"...|null"}
            """;

    /** Шаблон генерации вопроса через единый JSON-контракт. */
    public static final String QUESTION_V2_PROMPT_TEMPLATE = """
            You are generating a %s level %s technical interview question about %s.

            Return strict JSON only. Do not return markdown, comments, or any extra text.
            The response must be a single valid JSON object with this exact schema:

            {
              "question": "string",
              "options": [
                {"text":"string","correct":true},
                {"text":"string","correct":false},
                {"text":"string","correct":false},
                {"text":"string","correct":false}
              ],
              "explanation": "string"
            }

            Hard requirements:
            - options array must have exactly 4 items.
            - options must contain exactly one entry with "correct": true.
            - each option item must contain only "text" (string) and "correct" (boolean).
            - question must require technical reasoning, not pure memorization.
            - avoid trivial factual recall questions that can be answered without reasoning.
            - avoid yes/no question forms.
            - difficulty must match requested level (%s).
            - all options must answer this exact question context; no generic advice or topic drift.
            - keep distractors realistic, unique, and plausible; avoid duplicates.
            - do not make the correct option obviously stand out by tone, formatting, detail level, or explicit hints.
            - use one consistent wording style and formality level across all options.
            - keep options concise and token-efficient; avoid filler and generic prefaces.
            - do not use "all of the above" or "none of the above".
            - all options must contain roughly the same amount of information; no option should be noticeably longer, denser, or structurally different than others.
            - explanation must be 1-3 sentences, strictly technical and question-local, and justify why the correct option is correct and alternatives are wrong.
            - forbid interview meta-advice in options and explanation.
            - output language: Russian.
            """;

    /**
     * Оборачивает пользовательский ввод в разделители для защиты от prompt injection.
     * AI-модель видит чёткие границы между инструкцией и данными.
     *
     * @param input пользовательский текст (из markdown-файлов)
     * @return экранированный текст с разделителями
     */
    public static String wrapUserInput(String input) {
        if (input == null || input.isBlank()) return "";
        return "---BEGIN_USER_DATA---\n" + input + "\n---END_USER_DATA---";
    }

    /**
     * Добавляет модификатор адаптивной сложности к промпту.
     *
     * @param basePrompt исходный промпт
     * @param topicAccuracy точность пользователя по теме (0-100, -1 если нет данных)
     * @return обогащённый промпт
     */
    public static String withAdaptiveDifficulty(String basePrompt, double topicAccuracy) {
        if (topicAccuracy < 0) {
            return basePrompt;
        }
        if (topicAccuracy > 85.0) {
            return basePrompt + "\n\n" +
                    "АДАПТИВНАЯ СЛОЖНОСТЬ: пользователь отвечает на эту тему отлично (accuracy " +
                    String.format("%.0f%%", topicAccuracy) + ").\n" +
                    "Требования повышенной сложности:\n" +
                    "- Используй тонкие различия, edge cases и исключения\n" +
                    "- Включай нюансы реализации и граничные условия\n" +
                    "- Убирай очевидные distractor-варианты\n";
        }
        if (topicAccuracy < 40.0) {
            return basePrompt + "\n\n" +
                    "АДАПТИВНАЯ СЛОЖНОСТЬ: пользователь пока учится по этой теме (accuracy " +
                    String.format("%.0f%%", topicAccuracy) + ").\n" +
                    "Требования базовой сложности:\n" +
                    "- Объяснения делай простыми и короткими, но точными\n" +
                    "- Подсвечивай ключевое различие между похожими концептами\n" +
                    "- Добавляй конкретный ориентир: API, свойство или правило\n";
        }
        return basePrompt;
    }
}
