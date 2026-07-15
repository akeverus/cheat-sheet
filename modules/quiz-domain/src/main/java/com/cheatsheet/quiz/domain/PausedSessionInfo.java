package com.cheatsheet.quiz.domain;

import java.time.Instant;

/**
 * Метаданные приостановленной сессии (FLOW-01) для баннера
 * «есть незавершённая сессия — продолжить?».
 *
 * <p>Содержит только то, что нужно показать пользователю для решения, стоит ли
 * возобновлять — без самого состояния сессии (сериализованного блоба). Это
 * позволяет отрисовать баннер без десериализации.</p>
 *
 * @param mode     режим приостановленной сессии
 * @param topic    фильтр по теме (или {@code null}, если сессия не по одной теме)
 * @param total    всего вопросов в сессии
 * @param answered сколько уже отвечено к моменту паузы
 * @param pausedAt момент постановки на паузу
 */
public record PausedSessionInfo(
        InterviewMode mode,
        String topic,
        int total,
        int answered,
        Instant pausedAt
) {
}
