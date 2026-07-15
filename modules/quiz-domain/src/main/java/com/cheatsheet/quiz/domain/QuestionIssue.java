package com.cheatsheet.quiz.domain;

import java.time.Instant;

/**
 * Жалоба пользователя на вопрос (FLOW-REPORT / UX-21).
 *
 * @param id         идентификатор записи (BIGSERIAL)
 * @param questionId вопрос, на который жалуются
 * @param category   категория проблемы
 * @param comment    опциональный комментарий пользователя (может быть {@code null})
 * @param status     статус разбора
 * @param createdAt  момент создания
 */
public record QuestionIssue(
        long id,
        long questionId,
        QuestionIssueCategory category,
        String comment,
        QuestionIssueStatus status,
        Instant createdAt
) {}
