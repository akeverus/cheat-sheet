package com.cheatsheet.quiz.domain;

/**
 * Источник генерации варианта ответа на вопрос.
 *
 * <p>Позволяет отслеживать, откуда был получен каждый вариант —
 * сгенерирован OpenAI или DeepSeek.</p>
 *
 * <p>Хранится в БД как TEXT (через {@link #name()}).</p>
 */
public enum OptionSource {

    /** Вариант сгенерирован через OpenAI API. */
    OPENAI,

    /** Вариант сгенерирован через DeepSeek API. */
    DEEPSEEK,

    /** Вариант сгенерирован Claude в рамках офлайн-сида (без похода в runtime-AI). */
    CLAUDE,

    /** Вариант прочитан напрямую из markdown-файла шпаргалки (> [!mcq] блок). */
    MARKDOWN;

    /**
     * Парсит строку в источник варианта. Для null, пустой или неизвестной строки возвращает {@link #OPENAI}.
     *
     * @param value строка (например из БД: "OPENAI", "DEEPSEEK", "CLAUDE", "MARKDOWN")
     * @return соответствующий источник или OPENAI по умолчанию
     */
    public static OptionSource fromString(String value) {
        if (value == null || value.isBlank()) {
            return OPENAI;
        }
        String normalized = value.strip().toUpperCase().replace("-", "_");
        return switch (normalized) {
            case "DEEPSEEK" -> DEEPSEEK;
            case "CLAUDE" -> CLAUDE;
            case "MARKDOWN" -> MARKDOWN;
            // "OPENAI" и любое неизвестное значение → дефолтный OPENAI.
            default -> OPENAI;
        };
    }
}
