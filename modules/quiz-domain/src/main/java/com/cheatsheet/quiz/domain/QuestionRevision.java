package com.cheatsheet.quiz.domain;

import lombok.Builder;

/**
 * Ревизия контента вопроса (таблица {@code question_revision}, append-only).
 *
 * <p>Новая ревизия создаётся при импорте вопроса и при смене {@code source_hash}
 * (контент отредактирован). Позволяет привязать попытку к конкретной версии
 * контента ({@link Attempt#questionRevisionId()}).</p>
 *
 * @param id                идентификатор записи (BIGSERIAL); {@code 0} у ещё не сохранённой
 * @param questionId        вопрос (FK → questions.id)
 * @param revisionNumber    порядковый номер ревизии (1, 2, 3, …) в рамках вопроса
 * @param checksum          SHA-256 контента (= questions.source_hash на момент ревизии)
 * @param publicationStatus статус публикации (пока всегда {@code PUBLISHED})
 * @param createdAt         момент создания ревизии, epoch-секунды
 */
@Builder(toBuilder = true)
public record QuestionRevision(
        long id,
        long questionId,
        int revisionNumber,
        String checksum,
        String publicationStatus,
        long createdAt
) {
    /** Статус по умолчанию для новой ревизии. */
    public static final String STATUS_PUBLISHED = "PUBLISHED";
}
