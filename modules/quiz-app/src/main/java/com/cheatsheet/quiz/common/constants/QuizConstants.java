package com.cheatsheet.quiz.common.constants;

import lombok.experimental.UtilityClass;

/**
 * Shared constants for quiz services.
 */
@UtilityClass
public class QuizConstants {

    /** Accuracy threshold (percent) below which a topic is considered weak. */
    public static final double WEAK_TOPIC_THRESHOLD = 70.0;

    /** Макс. длина clientAttemptId (BE-004, идемпотентность ответа). */
    public static final int CLIENT_ATTEMPT_ID_MAX_LENGTH = 64;

    /** Макс. длина openedContentBlockIds (comma-separated telemetry). */
    public static final int OPENED_CONTENT_BLOCK_IDS_MAX_LENGTH = 2000;

    /** Верхняя граница think-time ответа, мс (30 мин). */
    public static final int RESPONSE_TIME_MS_MAX = 1_800_000;

}
