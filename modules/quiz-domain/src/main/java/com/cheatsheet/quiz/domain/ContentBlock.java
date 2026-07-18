package com.cheatsheet.quiz.domain;

import lombok.Builder;

/**
 * Типизированный блок контента вопроса (хендофф-3, Этап 8).
 *
 * <p>Единая модель для Markdown-объяснения, кода, Mermaid-схемы и (в перспективе)
 * картинок/врезок/таблиц. Собирается во view-слое ({@code ContentBlockMapper}) из
 * плоских полей {@link Question} ({@code answerMarkdown}, {@code codeSnippet},
 * {@code diagramMermaid}). Тип отдаётся фронту явно, поэтому клиент не угадывает
 * его по содержимому (см. {@code docs/07}).</p>
 *
 * @param id                       стабильный id блока в пределах вопроса (для {@code openedContentBlockIds})
 * @param type                     тип блока
 * @param title                    заголовок блока для UI (может быть {@code null})
 * @param body                     содержимое: Markdown/код/исходник Mermaid (может быть {@code null})
 * @param language                 язык для {@link BlockType#CODE} (например {@code "java"}); {@code null} иначе
 * @param source                   источник/подпись происхождения (может быть {@code null})
 * @param caption                  подпись под блоком (может быть {@code null})
 * @param accessibilityDescription текстовая альтернатива для схемы/картинки (a11y; может быть {@code null})
 */
@Builder(toBuilder = true)
public record ContentBlock(
        String id,
        BlockType type,
        String title,
        String body,
        String language,
        String source,
        String caption,
        String accessibilityDescription
) {}
