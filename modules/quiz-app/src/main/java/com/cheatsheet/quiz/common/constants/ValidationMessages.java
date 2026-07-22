package com.cheatsheet.quiz.common.constants;

import lombok.experimental.UtilityClass;

/**
 * Сообщения Bean Validation, разделяемые несколькими request-DTO.
 *
 * <p>Вынесены сюда, чтобы одинаковый текст не дублировался по классам и не
 * расходился при правке. Значения — компайл-тайм-константы (можно использовать
 * в атрибутах аннотаций {@code @Positive}/{@code @Size}).</p>
 */
@UtilityClass
public class ValidationMessages {

    public static final String QUESTION_ID_POSITIVE = "questionId must be greater than 0";
    public static final String QUESTION_REVISION_ID_POSITIVE = "questionRevisionId must be greater than 0";
    public static final String CLIENT_ATTEMPT_ID_MAX = "clientAttemptId must be at most 64 characters";
    public static final String OPENED_CONTENT_BLOCK_IDS_MAX =
            "openedContentBlockIds must be at most 2000 characters";
}
