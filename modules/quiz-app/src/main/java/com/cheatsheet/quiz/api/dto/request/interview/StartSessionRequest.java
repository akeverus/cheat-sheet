package com.cheatsheet.quiz.api.dto.request.interview;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Параметры старта сессии.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Accessors(chain = true)
public class StartSessionRequest {

    @Pattern(regexp = "(?i)TRAINING|EXAM|MARATHON|STUDY|FLASHCARD", message = "mode must be TRAINING, EXAM, MARATHON, STUDY or FLASHCARD")
    private String mode;

    @Positive(message = "count must be greater than 0")
    private Integer count;

    private String topic;
    private String group;
    private Boolean important;
    private Boolean onlyWrong;
    private Boolean shuffle;
    private Boolean ordered;

    /**
     * Лимит времени на вопрос в секундах (хендофф-3, настройка «Таймер»). {@code null}
     * или 0 — без таймера. Обратный отсчёт ведёт клиент; поле принимается для
     * полноты контракта конструктора сессии.
     */
    @Min(0) @Max(600)
    private Integer timerSeconds;
}
