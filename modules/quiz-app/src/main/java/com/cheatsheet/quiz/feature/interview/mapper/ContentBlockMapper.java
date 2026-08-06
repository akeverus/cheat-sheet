package com.cheatsheet.quiz.feature.interview.mapper;

import com.cheatsheet.quiz.domain.BlockType;
import com.cheatsheet.quiz.domain.ContentBlock;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.service.diagram.AsciiDiagramDetector;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Собирает типизированные {@link ContentBlock} из плоских полей {@link Question}
 * (хендофф-3, Этап 8). Тип блока определяется здесь, на backend, и отдаётся фронту
 * явно ({@code data-content-type}) — клиент больше не угадывает Mermaid/код по
 * содержимому (см. {@code docs/07}, {@code METRICS_GLOSSARY.md}).
 *
 * <p>Это единственное место, где вкладка получает свой тип и заголовок. Шаблон
 * {@code focus-training.html} ходит по этому списку, а не по полям вопроса: раньше
 * он вычислял {@code hasCode}/{@code hasDiagram} сам, и логика жила в двух местах
 * сразу.</p>
 *
 * <p>Поле {@code codeSnippet} не обязано содержать код. Импортёр кладёт туда любой
 * не-mermaid блок ```…```, включая нарисованные псевдографикой схемы — их отделяет
 * {@link AsciiDiagramDetector}. Рисунок становится схемой, а не «примером кода»;
 * если у вопроса уже есть mermaid, дублирующий рисунок отбрасывается — на 70
 * вопросах это была буквально та же топология в двух вкладках подряд.</p>
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
        boolean hasMermaid = hasText(question.diagramMermaid());
        String snippet = question.codeSnippet();
        boolean snippetIsDrawing = hasText(snippet) && AsciiDiagramDetector.isDrawing(snippet);

        if (hasText(snippet) && !snippetIsDrawing) {
            blocks.add(ContentBlock.builder()
                    .id(CODE_BLOCK_ID)
                    .type(BlockType.CODE)
                    .title("Пример кода")
                    .body(snippet)
                    // Язык не хранится в данных — highlight.js определит автоматически.
                    .language(null)
                    .build());
        }
        if (hasMermaid) {
            blocks.add(ContentBlock.builder()
                    .id(DIAGRAM_BLOCK_ID)
                    .type(BlockType.MERMAID)
                    .title("Схема")
                    .body(question.diagramMermaid())
                    .accessibilityDescription(
                            "Диаграмма к вопросу. Текст исходника доступен во вкладке «Исходник».")
                    .build());
        } else if (snippetIsDrawing) {
            blocks.add(ContentBlock.builder()
                    .id(DIAGRAM_BLOCK_ID)
                    .type(BlockType.ASCII_DIAGRAM)
                    .title("Схема")
                    .body(snippet)
                    .accessibilityDescription(
                            "Текстовая схема к вопросу, нарисованная символами псевдографики.")
                    .build());
        }
        return blocks;
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
