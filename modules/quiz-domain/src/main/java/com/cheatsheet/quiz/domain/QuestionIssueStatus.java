package com.cheatsheet.quiz.domain;

/**
 * Статус жалобы на вопрос (FLOW-REPORT). При создании всегда {@link #OPEN};
 * дальнейшая смена статуса — ручная (single-user инструмент без модерации).
 */
public enum QuestionIssueStatus {

    /** Открыта, ждёт разбора. */
    OPEN,

    /** Разобрана и исправлена. */
    RESOLVED,

    /** Отклонена (не проблема). */
    DISMISSED
}
