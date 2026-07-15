package com.cheatsheet.quiz.domain;

/**
 * Категория жалобы на вопрос (FLOW-REPORT / UX-21).
 *
 * <p>Хранится в БД как TEXT. Значение из формы разбирается лениво
 * ({@link #fromString}) — неизвестная/пустая категория падает в {@link #OTHER},
 * чтобы репорт не терялся из-за расхождения фронт/бэкенд.</p>
 */
public enum QuestionIssueCategory {

    /** Неверный «правильный» ответ / разбор. */
    INCORRECT_ANSWER,

    /** Опечатка или ошибка форматирования. */
    TYPO,

    /** Формулировка неясна или неоднозначна. */
    UNCLEAR,

    /** Устаревшая информация. */
    OUTDATED,

    /** Прочее / не подошло под категории. */
    OTHER;

    /**
     * Ленивый парсинг категории из формы: {@code null}/пусто/неизвестное → {@link #OTHER}.
     *
     * @param value строковое значение из запроса
     * @return соответствующая категория или {@link #OTHER}
     */
    public static QuestionIssueCategory fromString(String value) {
        if (value == null || value.isBlank()) {
            return OTHER;
        }
        try {
            return valueOf(value.strip().toUpperCase());
        } catch (IllegalArgumentException e) {
            return OTHER;
        }
    }
}
