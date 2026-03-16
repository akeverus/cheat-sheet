package com.cheatsheet.quiz.domain;

import java.util.Objects;
import lombok.Builder;

/**
 * Строка экспорта прогресса: один вопрос со статистикой повторений.
 *
 * <p>Используется в репозитории статистики и в контроллере экспорта
 * для выгрузки данных в JSON/CSV.</p>
 *
 * @param slug         уникальный ключ вопроса
 * @param topic        тема
 * @param correctCount количество правильных ответов
 * @param wrongCount   количество неправильных ответов
 * @param nextReviewAt epoch-секунды следующего повторения
 * @param repetitions  количество последовательных правильных ответов */
@Builder(toBuilder = true)
public record ProgressExportRow(
        String slug,
        String topic,
        int correctCount,
        int wrongCount,
        long nextReviewAt,
        int repetitions
) {

    public ProgressExportRow {
        Objects.requireNonNull(slug, "slug must not be null");
        Objects.requireNonNull(topic, "topic must not be null");
        if (correctCount < 0) {
            throw new IllegalArgumentException("correctCount must be >= 0");
        }
        if (wrongCount < 0) {
            throw new IllegalArgumentException("wrongCount must be >= 0");
        }
    }
}
