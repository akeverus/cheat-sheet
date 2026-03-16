package com.cheatsheet.quiz.service.diagram;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MermaidValidatorTest {

    private final MermaidValidator validator = new MermaidValidator();

    @Test
    void acceptsRelevantMermaidWithDomainTerms() {
        String code = """
                flowchart TD
                    A[TTL cache key] --> B{ttl expired}
                    B -- yes --> C[invalidate cache]
                    B -- no --> D[return cached value]
                    C --> E[load from database]
                """;

        boolean relevant = validator.isRelevantMermaid(
                code,
                "Как работает механизм TTL в Redis cache?",
                "TTL определяет время жизни key, после истечения нужен invalidate и чтение из базы."
        );

        assertThat(relevant).isTrue();
    }

    @Test
    void rejectsGenericMermaidWithoutQuestionContextOverlap() {
        String code = """
                flowchart TD
                    A[Обработка] --> B{Этап}
                    B -- yes --> C[Логика]
                    B -- no --> D[Процесс]
                """;

        boolean relevant = validator.isRelevantMermaid(
                code,
                "Как работает механизм TTL в Redis cache?",
                "Нужно проверить ttl и удалить просроченный ключ."
        );

        assertThat(relevant).isFalse();
    }
}
