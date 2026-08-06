package com.cheatsheet.quiz.domain;

/**
 * Тип типизированного блока контента вопроса (хендофф-3, Этап 8).
 *
 * <p>Тип определяется на backend и приходит на фронт как {@code data-content-type} —
 * JS больше не угадывает Mermaid/код по содержимому. Реально присутствуют в данных
 * {@link #MARKDOWN}, {@link #CODE}, {@link #MERMAID}, {@link #ASCII_DIAGRAM};
 * {@link #IMAGE}/{@link #CALLOUT}/{@link #TABLE} — задел под будущий контент (модель
 * готова, рендерятся при появлении данных). См. {@code schemas/content-block.schema.json}
 * и {@code METRICS_GLOSSARY.md}.</p>
 */
public enum BlockType {
    MARKDOWN,
    CODE,
    MERMAID,
    /**
     * Схема, нарисованная символами псевдографики прямо в тексте шпаргалки.
     * Это тоже схема, а не код: рендерится моноширинным {@code <pre>} без подсветки
     * синтаксиса и без mermaid, но подписывается и ведёт себя как диаграмма.
     */
    ASCII_DIAGRAM,
    IMAGE,
    CALLOUT,
    TABLE;

    /**
     * Рендерится ли блок движком mermaid (иначе — моноширинным {@code <pre>}).
     *
     * <p>Предикат, а не сравнение с константой в шаблоне: {@code focus-training.html}
     * ветвился по {@code type().name() == 'MERMAID'} в четырёх местах, и все четыре
     * при переименовании константы одновременно уходили в ASCII-ветку — схема
     * оставалась на экране, просто переставала быть схемой.
     */
    public boolean rendersAsMermaid() {
        return this == MERMAID;
    }
}
