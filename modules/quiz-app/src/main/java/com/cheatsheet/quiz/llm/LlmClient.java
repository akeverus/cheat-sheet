package com.cheatsheet.quiz.llm;

import com.cheatsheet.quiz.service.ai.dto.ChatRequest;

import java.time.Duration;
import java.util.Optional;

/**
 * Контракт низкоуровневого LLM-клиента, отправляющего chat-запросы.
 */
public interface LlmClient {

    /**
     * Отправляет запрос к LLM и возвращает уже извлечённый контент сообщения.
     *
     * @param request запрос к chat endpoint
     * @param timeout таймаут выполнения запроса
     * @return распарсенный контент модели или пустой Optional при ошибке
     */
    Optional<String> sendChatRequest(ChatRequest request, Duration timeout);
}
