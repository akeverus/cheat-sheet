package com.cheatsheet.quiz.domain;

/**
 * Тип типизированного блока контента вопроса (хендофф-3, Этап 8).
 *
 * <p>Тип определяется на backend и приходит на фронт как {@code data-block-type} —
 * JS больше не угадывает Mermaid/код по содержимому. Реально присутствуют в данных
 * {@link #MARKDOWN}, {@link #CODE}, {@link #MERMAID}; {@link #IMAGE}/{@link #CALLOUT}/
 * {@link #TABLE} — задел под будущий контент (модель готова, рендерятся при появлении
 * данных). См. {@code schemas/content-block.schema.json} и {@code METRICS_GLOSSARY.md}.</p>
 */
public enum BlockType {
    MARKDOWN,
    CODE,
    MERMAID,
    IMAGE,
    CALLOUT,
    TABLE
}
