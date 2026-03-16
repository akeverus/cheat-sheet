package com.cheatsheet.quiz.feature.question.engine.metadata;

import lombok.RequiredArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.time.Clock;

/**
 * Фабрика генерации slug для AI-сгенерированных вопросов.
 */
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuestionGeneratedSlugFactory {

    private final Clock clock;

    /**
     * Формирует детерминируемый slug на основе текущего времени из {@link Clock}.
     *
     * @return slug вида {@code generated:<epochMillis>}
     */
    public String nextSlug() {
        return "generated:" + clock.millis();
    }
}
