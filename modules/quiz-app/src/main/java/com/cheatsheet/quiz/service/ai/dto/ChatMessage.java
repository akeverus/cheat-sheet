package com.cheatsheet.quiz.service.ai.dto;
import lombok.Builder;

/**
 * Сообщение для OpenAI-совместимого Chat API.
 *
 * @param role    роль отправителя ({@code "system"}, {@code "user"}, {@code "assistant"})
 * @param content текст сообщения
 */
@Builder(toBuilder = true)
public record ChatMessage(String role, String content) {}
