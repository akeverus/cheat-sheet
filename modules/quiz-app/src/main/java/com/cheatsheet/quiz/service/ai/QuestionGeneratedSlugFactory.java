package com.cheatsheet.quiz.service.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;

/**
 * Фабрика генерации slug для AI-сгенерированных вопросов.
 */
@Component
@RequiredArgsConstructor
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
