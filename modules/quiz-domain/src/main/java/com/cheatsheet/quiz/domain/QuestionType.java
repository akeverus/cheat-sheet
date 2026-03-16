package com.cheatsheet.quiz.domain;

/**
 * Тип вопроса квиза.
 *
 * <ul>
 *   <li>{@link #TEXT} — текстовый вопрос и варианты ответа.</li>
 *   <li>{@link #CODE} — вопрос с кодом («что выведет?», «какой фрагмент верен?»).</li>
 * </ul>
 */
public enum QuestionType {
    CONCEPT,
    CODE_ANALYSIS,
    DEBUGGING,
    ARCHITECTURE,
    TEXT,
    CODE;

    /**
     * Парсит строку в тип вопроса. Для null, пустой или неизвестной строки возвращает {@link #TEXT}.
     *
     * @param value строка (например из БД: "TEXT", "CODE")
     * @return соответствующий тип или TEXT по умолчанию
     */
    public static QuestionType fromString(String value) {
        if (value == null || value.isBlank()) {
            return TEXT;
        }
        String normalized = value.strip().toUpperCase();
        if ("CONCEPT".equals(normalized)) return CONCEPT;
        if ("CODE_ANALYSIS".equals(normalized)) return CODE_ANALYSIS;
        if ("DEBUGGING".equals(normalized)) return DEBUGGING;
        if ("ARCHITECTURE".equals(normalized)) return ARCHITECTURE;
        if ("TEXT".equals(normalized)) return TEXT;
        if ("CODE".equals(normalized)) return CODE;
        return TEXT;
    }
}
