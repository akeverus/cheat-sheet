package com.cheatsheet.quiz.persistence;

import lombok.Builder;
import java.util.List;

/**
 * Контракт полнотекстового поиска по вопросам.
 *
 * <p>Единственная реализация — {@link PostgresFullTextSearchRepository}
 * (tsvector + GIN, поддержка русской морфологии).</p>
 */
public interface FullTextSearchRepository {

    /**
     * Обновляет индекс полнотекстового поиска для вопроса.
     *
     * @param questionId     идентификатор вопроса
     * @param questionText   текст вопроса
     * @param answerMarkdown текст ответа
     */
    void upsert(long questionId, String questionText, String answerMarkdown);

    /**
     * Удаляет запись из индекса при удалении вопроса.
     * Для текущей PostgreSQL-реализации — no-op (search_vector в той же таблице).
     *
     * @param questionId идентификатор вопроса
     */
    void delete(long questionId);

    /**
     * Выполняет полнотекстовый поиск.
     *
     * @param query пользовательский запрос
     * @param limit максимальное количество результатов (рекомендуется 1–100, вызывающий код ограничивает)
     * @return список результатов с ID и сниппетом
     */
    List<SearchResult> search(String query, int limit);

    /**
     * Результат полнотекстового поиска.
     *
     * @param questionId ID вопроса
     * @param snippet    сниппет с подсвеченными совпадениями
     */
    @Builder(toBuilder = true)
    record SearchResult(long questionId, String snippet) {}
}
