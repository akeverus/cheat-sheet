package com.cheatsheet.quiz.domain;

/**
 * Категория жалобы на вопрос (FLOW-REPORT / UX-21).
 *
 * <p>Хранится в БД как TEXT. Значение из формы разбирается лениво
 * ({@link #fromString}) — неизвестная/пустая категория падает в {@link #OTHER},
 * чтобы репорт не терялся из-за расхождения фронт/бэкенд.</p>
 *
 * <p>Подпись — свойство самой категории, а не копирайт экрана: «Опечатка»
 * называется так везде, где её показывают. Поэтому {@link #label()} живёт здесь,
 * а не в слое приложения (критерий — {@code docs/backend-architecture.md} §3b).
 * Список для формы жалобы собирает сервер: держать его второй копией в JS опасно
 * вдвойне — {@link #fromString} намеренно снисходителен, и расхождение имён не
 * уронит запрос, а тихо переклассифицирует жалобу в {@link #OTHER}.</p>
 */
public enum QuestionIssueCategory {

    /** Неверный «правильный» ответ / разбор. */
    INCORRECT_ANSWER("Неверный ответ"),

    /** Опечатка или ошибка форматирования. */
    TYPO("Опечатка"),

    /** Формулировка неясна или неоднозначна. */
    UNCLEAR("Непонятная формулировка"),

    /** Устаревшая информация. */
    OUTDATED("Устаревшая информация"),

    /** Прочее / не подошло под категории. */
    OTHER("Другое");

    private final String label;

    QuestionIssueCategory(String label) {
        this.label = label;
    }

    /**
     * Русская подпись категории для формы жалобы.
     *
     * @return подпись, пригодная для показа пользователю
     */
    public String label() {
        return label;
    }

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
