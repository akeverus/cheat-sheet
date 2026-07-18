package com.cheatsheet.quiz.domain;

import lombok.Builder;

/**
 * Закладка на вопрос с опциональной текстовой заметкой (таблица {@code bookmark}).
 *
 * <p>UI-014: пользователь сохраняет вопрос и снабжает его заметкой. Одна закладка
 * на вопрос (UNIQUE {@code question_id}) — повторное сохранение обновляет заметку.</p>
 *
 * @param id         идентификатор записи (BIGSERIAL); {@code 0} у ещё не сохранённой
 * @param questionId вопрос (FK → questions.id)
 * @param note       заметка пользователя (может быть {@code null}/пустой)
 * @param createdAt  момент создания, epoch-секунды
 * @param updatedAt  момент последнего изменения заметки, epoch-секунды
 */
@Builder(toBuilder = true)
public record Bookmark(
        long id,
        long questionId,
        String note,
        long createdAt,
        long updatedAt
) {}
