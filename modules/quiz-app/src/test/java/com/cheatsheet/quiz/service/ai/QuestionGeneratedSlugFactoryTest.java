package com.cheatsheet.quiz.service.ai;

import com.cheatsheet.quiz.feature.question.engine.metadata.QuestionGeneratedSlugFactory;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class QuestionGeneratedSlugFactoryTest {

    @Test
    void nextSlugUsesClockMillis() {
        QuestionGeneratedSlugFactory factory = new QuestionGeneratedSlugFactory(
                Clock.fixed(Instant.parse("2024-02-01T10:11:12Z"), ZoneOffset.UTC)
        );

        String slug = factory.nextSlug();

        assertThat(slug).isEqualTo("generated:1706782272000");
    }
}
