package com.cheatsheet.quiz.domain;

/**
 * Explainable-причина, по которой вопрос попал в текущее повторение
 * (планировщик FSRS/SM-2). Это value-объект: {@link #type} — машинная категория
 * для стилизации/иконки, {@link #message} — готовая человекочитаемая строка
 * на русском, которую UI показывает как есть.
 *
 * <p>Причина вычисляется при отборе вопроса на основе сигналов, которые уже
 * есть в данных: {@code next_review_at} (просрочка), {@code last_result} +
 * {@code last_reviewed_at} (недавняя ошибка), точность по теме (слабый навык).</p>
 *
 * @param type    категория причины
 * @param message готовая строка для показа пользователю
 */
public record ReviewReason(Type type, String message) {

    public ReviewReason {
        if (type == null) {
            throw new IllegalArgumentException("ReviewReason.type не может быть null");
        }
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("ReviewReason.message не может быть пустым");
        }
    }

    /**
     * Категория причины. Порядок объявления отражает приоритет при отборе:
     * недавняя ошибка важнее просрочки, просрочка важнее общего слабого навыка.
     */
    public enum Type {
        /** Последний ответ был неверным/«не знаю» — свежий лапс. */
        RECENT_LAPSE,
        /** Повторение просрочено (текущее время позже {@code next_review_at}). */
        OVERDUE,
        /** Низкая точность по теме в целом. */
        WEAK_SKILL
    }
}
