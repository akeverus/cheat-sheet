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
    public static final String SYSTEM_PROMPT =
            "Ты помощник для подготовки к собеседованию. Отвечай строго на русском языке, если не указано иное. " +
            "Отвечай строго по формату из запроса. " +
            "Если требуется JSON, верни только валидный JSON без markdown и комментариев. " +
            "Пиши кратко и по делу, без лишних вступлений.";

    /**
     * Шаблон генерации вопроса через единый JSON-контракт.
     * Загружается из classpath (prompts/general.txt), чтобы все правила жили в LLM prompt.
     */
    public static final String GENERAL_PROMPT_TEMPLATE = PromptLoader.load("general");

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
