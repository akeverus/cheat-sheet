package com.cheatsheet.quiz.domain;

import java.util.Objects;
import lombok.Builder;

/**
 * Связанный вопрос для блока "Похожие вопросы для закрепления".
 *
 * @param id           ID вопроса
 * @param questionText текст вопроса
 * @param topic        тема
 * @param accuracy     точность ответов (%) или -1 если нет данных
 */
@Builder(toBuilder = true)
public record RelatedQuestion(long id, String questionText, String topic, double accuracy) {

    public RelatedQuestion {
        Objects.requireNonNull(questionText, "questionText must not be null");
    }
}
