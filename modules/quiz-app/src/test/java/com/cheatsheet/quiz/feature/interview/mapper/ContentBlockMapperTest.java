package com.cheatsheet.quiz.feature.interview.mapper;

import com.cheatsheet.quiz.domain.BlockType;
import com.cheatsheet.quiz.domain.ContentBlock;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.TestQuestionBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Ключевой контракт вкладок «Пример кода / Схема»: заголовок обязан совпадать с
 * тем, что внутри. Импортёр складывает в {@code codeSnippet} любой не-mermaid
 * блок ```…```, поэтому там встречаются нарисованные псевдографикой схемы.
 */
class ContentBlockMapperTest {

    private final ContentBlockMapper mapper = new ContentBlockMapper();

    private static final String DRAWING = """
            ┌─────────────┐        ┌─────────────┐
            │  Sentinel 1 │◀──────▶│  Sentinel 2 │
            └──────┬──────┘        └──────┬──────┘
                   │                      │
                   ▼                      ▼
            ┌─────────────────────────────────────┐
            │             Redis master            │
            └─────────────────────────────────────┘
            """;

    private static final String JAVA_CODE = """
            public class OrderService {
                private final OrderRepository repository;

                public Order find(long id) {
                    return repository.findById(id);
                }
            }
            """;

    private Question question(String codeSnippet, String diagramMermaid) {
        return TestQuestionBuilder.aQuestion()
                .withCodeSnippet(codeSnippet)
                .withDiagramMermaid(diagramMermaid)
                .build();
    }

    @Test
    @DisplayName("настоящий код остаётся вкладкой «Пример кода»")
    void realCodeStaysCode() {
        List<ContentBlock> blocks = mapper.toContentBlocks(question(JAVA_CODE, null));

        assertThat(blocks).singleElement()
                .satisfies(b -> {
                    assertThat(b.type()).isEqualTo(BlockType.CODE);
                    assertThat(b.id()).isEqualTo(ContentBlockMapper.CODE_BLOCK_ID);
                    assertThat(b.title()).isEqualTo("Пример кода");
                });
    }

    @Test
    @DisplayName("рисунок псевдографикой становится схемой, а не «примером кода»")
    void drawingBecomesDiagram() {
        List<ContentBlock> blocks = mapper.toContentBlocks(question(DRAWING, null));

        assertThat(blocks).singleElement()
                .satisfies(b -> {
                    assertThat(b.type()).isEqualTo(BlockType.ASCII_DIAGRAM);
                    assertThat(b.id()).isEqualTo(ContentBlockMapper.DIAGRAM_BLOCK_ID);
                    assertThat(b.title()).isEqualTo("Схема");
                    assertThat(b.body()).isEqualTo(DRAWING);
                });
    }

    @Test
    @DisplayName("рисунок рядом с mermaid отбрасывается как дубль той же схемы")
    void drawingDroppedWhenMermaidPresent() {
        List<ContentBlock> blocks = mapper.toContentBlocks(question(DRAWING, "graph LR; A-->B"));

        assertThat(blocks).singleElement()
                .satisfies(b -> {
                    assertThat(b.type()).isEqualTo(BlockType.MERMAID);
                    assertThat(b.body()).isEqualTo("graph LR; A-->B");
                });
    }

    @Test
    @DisplayName("код и mermaid дают обе вкладки, код первым")
    void codeAndMermaidGiveTwoBlocks() {
        List<ContentBlock> blocks = mapper.toContentBlocks(question(JAVA_CODE, "graph LR; A-->B"));

        assertThat(blocks).extracting(ContentBlock::type)
                .containsExactly(BlockType.CODE, BlockType.MERMAID);
    }

    @Test
    @DisplayName("код с нарисованным комментарием внутри остаётся кодом")
    void codeWithDecorativeCommentStaysCode() {
        String snippet = """
                // ┌──────────┐
                // │  кеш     │
                // └──────────┘
                public class CacheConfig {
                    private final int size = 500;

                    public int size() {
                        return size;
                    }
                }
                """;

        List<ContentBlock> blocks = mapper.toContentBlocks(question(snippet, null));

        assertThat(blocks).singleElement()
                .satisfies(b -> assertThat(b.type()).isEqualTo(BlockType.CODE));
    }

    @Test
    @DisplayName("стрелка в русском комментарии не делает код схемой")
    void arrowInsideCommentStaysCode() {
        String snippet = """
                // map — синхронное преобразование каждого элемента
                Flux.just("alice", "bob")
                    .map(String::toUpperCase)
                    // → "ALICE", "BOB"

                // NULLIF: 0 → NULL
                SELECT NULLIF(discount, 0) AS discount FROM customers;
                """;

        List<ContentBlock> blocks = mapper.toContentBlocks(question(snippet, null));

        assertThat(blocks).singleElement()
                .satisfies(b -> assertThat(b.type()).isEqualTo(BlockType.CODE));
    }

    @Test
    @DisplayName("цепочка со стрелками-связками на отдельных строках — схема")
    void arrowChainBecomesDiagram() {
        String snippet = """
                Event (like/comment/follow/dm)
                    ↓
                NotificationService
                    ↓
                APNs (iOS) / FCM (Android) / WebPush
                """;

        List<ContentBlock> blocks = mapper.toContentBlocks(question(snippet, null));

        assertThat(blocks).singleElement()
                .satisfies(b -> assertThat(b.type()).isEqualTo(BlockType.ASCII_DIAGRAM));
    }

    @Test
    @DisplayName("без кода и схемы блоков нет")
    void emptyQuestionGivesNoBlocks() {
        assertThat(mapper.toContentBlocks(question(null, null))).isEmpty();
        assertThat(mapper.toContentBlocks(null)).isEmpty();
    }
}
