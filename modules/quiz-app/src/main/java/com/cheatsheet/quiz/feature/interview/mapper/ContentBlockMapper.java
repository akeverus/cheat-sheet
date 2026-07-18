package com.cheatsheet.quiz.feature.interview.mapper;

import com.cheatsheet.quiz.domain.BlockType;
import com.cheatsheet.quiz.domain.ContentBlock;
import com.cheatsheet.quiz.domain.Question;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Собирает типизированные {@link ContentBlock} из плоских полей {@link Question}
 * (хендофф-3, Этап 8). Тип блока определяется здесь, на backend, и отдаётся фронту
 * явно ({@code data-block-type}) — клиент больше не угадывает Mermaid/код по
 * содержимому (см. {@code docs/07}, {@code METRICS_GLOSSARY.md}).
 *
 * <p>Сейчас в данных реально присутствуют {@link BlockType#CODE} ({@code codeSnippet})
 * и {@link BlockType#MERMAID} ({@code diagramMermaid}) — их рендерят вкладки
 * «Пример кода / Схема». Markdown-объяснение остаётся на существующем пути разбора.
 * {@link BlockType#IMAGE}/{@link BlockType#CALLOUT}/{@link BlockType#TABLE} появятся,
 * когда для них будут данные.</p>
 */
@Component
public class ContentBlockMapper {

    /** ID блока кода (стабилен для {@code openedContentBlockIds}). */
    public static final String CODE_BLOCK_ID = "code";

    /** ID блока схемы. */
    public static final String DIAGRAM_BLOCK_ID = "diagram";

    /**
     * Блоки контента, показываемые рядом с вопросом (вкладки Код/Схема). Порядок:
     * код, затем схема. Пустой список, если ни кода, ни схемы нет.
     */
    public List<ContentBlock> toContentBlocks(Question question) {
        List<ContentBlock> blocks = new ArrayList<>(2);
        if (question == null) {
            return blocks;
        }
        if (hasText(question.codeSnippet())) {
            blocks.add(ContentBlock.builder()
                    .id(CODE_BLOCK_ID)
                    .type(BlockType.CODE)
                    .title("Пример кода")
                    .body(question.codeSnippet())
                    // Язык не хранится в данных — highlight.js определит автоматически.
                    .language(null)
                    .build());
        }
        if (hasText(question.diagramMermaid())) {
            blocks.add(ContentBlock.builder()
                    .id(DIAGRAM_BLOCK_ID)
                    .type(BlockType.MERMAID)
                    .title("Схема")
                    .body(question.diagramMermaid())
                    .accessibilityDescription(
                            "Диаграмма к вопросу. Текст исходника доступен во вкладке «Исходник».")
                    .build());
        }
        return blocks;
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
