package com.cheatsheet.quiz.common.exception;

/**
 * Клиент превысил лимит обращений к чувствительному endpoint. Обрабатывается
 * глобально: 429 {@code ProblemDetail} + заголовок {@code Retry-After}.
 */
public class RateLimitExceededException extends RuntimeException {

    private final int retryAfterSeconds;

    public RateLimitExceededException(int retryAfterSeconds) {
        super("Слишком много запросов, повторите позже");
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public int retryAfterSeconds() {
        return retryAfterSeconds;
    }
}
