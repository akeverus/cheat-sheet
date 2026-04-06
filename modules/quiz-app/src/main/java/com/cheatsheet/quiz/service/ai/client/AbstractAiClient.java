package com.cheatsheet.quiz.service.ai.client;

import com.cheatsheet.quiz.config.app.AppProperties;
import com.cheatsheet.quiz.llm.LlmClient;
import com.cheatsheet.quiz.llm.LlmRequestBuilder;
import com.cheatsheet.quiz.llm.LlmResponseParser;
import com.cheatsheet.quiz.service.ai.dto.ChatMessage;
import com.cheatsheet.quiz.service.ai.dto.ChatRequest;
import com.cheatsheet.quiz.service.ai.AiQuestionClient;
import com.cheatsheet.quiz.service.ai.parser.AiResponseParser;
import com.cheatsheet.quiz.service.ai.prompt.AiPrompts;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.concurrent.TimeoutException;

/**
 * Базовая реализация AI-клиента для OpenAI-совместимых API (DeepSeek, OpenAI).
 *
 * <p>Инкапсулирует общую логику:</p>
 * <ul>
 *   <li>построение промпта с инструкцией для модели;</li>
 *   <li>отправку HTTP-запроса через {@link WebClient} (single-attempt);</li>
 *   <li>парсинг JSON-ответа и извлечение вариантов.</li>
 * </ul>
 *
 * <p>Наследники ({@link DeepSeekClient}, {@link OpenAiClient}) предоставляют
 * конфигурацию (URL, модель, ключ API, температуру) и идентификатор провайдера.</p>
 *
 * <p><strong>Потоковая модель (blocking calls):</strong> Методы генерации используют {@code Mono.block(timeout)} для синхронного получения результата
 * от реактивного {@link WebClient}. Это блокирует вызывающий поток на время AI-запроса.</p>
 *
 * <p>Текущий дизайн безопасен, т.к.:</p>
 * <ul>
 *   <li>При <b>предзагрузке</b> (warmup) вызовы выполняются на bounded {@code preloadExecutor}
 *       (настраивается через {@code app.preload.*}), который изолирует AI-нагрузку от web-потоков;</li>
 *   <li>При <b>обработке HTTP-запроса</b> (on-demand) блокируется один поток Tomcat, что допустимо
 *       при локальном использовании с низким параллелизмом.</li>
 * </ul>
 *
 * <p>Для высоконагруженного production-деплоя рекомендуется:</p>
 * <ul>
 *   <li>Перевести вызовы на полностью реактивный pipeline (без {@code .block()});</li>
 *   <li>Или выполнять AI-вызовы на выделенном {@code Schedulers.boundedElastic()} scheduler.</li>
 * </ul>
 *
 * @see AiQuestionClient
 */
@Slf4j
public abstract class AbstractAiClient implements AiQuestionClient, LlmClient {

    // Промпты вынесены в AiPrompts для единого источника истины

    // --- Абстрактные методы, реализуемые наследниками ---

    /** WebClient, сконфигурированный для конкретного провайдера. */
    protected abstract WebClient webClient();

    /** API-ключ провайдера. */
    protected abstract String apiKey();

    /** Название модели (например, {@code deepseek-chat}, {@code gpt-4o-mini}). */
    protected abstract String model();

    /** Путь к эндпоинту чата (например, {@code /v1/chat/completions}). */
    protected abstract String endpoint();

    /** Температура генерации. */
    protected abstract double temperature();

    /** Общие настройки AI-запросов. */
    protected abstract AppProperties.Ai aiConfig();

    /** Общий {@link ObjectMapper} приложения. */
    protected abstract ObjectMapper objectMapper();

    /** Дефолтный эндпоинт (OpenAI-совместимый). */
    private static final String DEFAULT_ENDPOINT = "/v1/chat/completions";

    /**
     * Проверяет, задан ли API-ключ. Если ключ отсутствует или пустой, методы генерации должны возвращать empty.
     */
    protected boolean isApiKeyMissing() {
        String key = apiKey();
        return key == null || key.isBlank();
    }

    /**
     * Возвращает эндпоинт: из конфигурации или дефолтный {@value #DEFAULT_ENDPOINT}.
     */
    private String resolveEndpoint() {
        String ep = endpoint();
        return (ep == null || ep.isBlank()) ? DEFAULT_ENDPOINT : ep;
    }

    private int maxInputLength() {
        return aiConfig().getMaxInputLength();
    }

    private Duration requestTimeout() {
        return Duration.ofSeconds(aiConfig().getTimeoutSeconds());
    }

    private Optional<String> requestContent(ChatRequest request) {
        return sendChatRequest(request, requestTimeout());
    }

    private ChatRequest buildChatRequest(String prompt, double requestTemperature) {
        return new ChatRequest(
                model(),
                List.of(
                        new ChatMessage("system", AiPrompts.SYSTEM_PROMPT),
                        new ChatMessage("user", prompt)
                ),
                requestTemperature
        );
    }

    /**
     * Отправляет chat completion запрос к провайдеру и возвращает извлечённый текст ответа модели.
     *
     * <p>Используется всеми методами генерации (варианты, подсказки, диаграммы и т.д.) для устранения дублирования.</p>
     *
     * @param request   тело запроса (модель, сообщения, температура)
     * @param timeout   таймаут ожидания ответа
     * @return содержимое {@code choices[0].message.content} или пустой Optional при ошибке/пустом ответе
     */
    @Override
    public Optional<String> sendChatRequest(ChatRequest request, Duration timeout) {
        try {
            var mono = webClient().post()
                    .uri(resolveEndpoint())
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(timeout);
            String response = mono.block();
            if (response == null || response.isBlank()) {
                return Optional.empty();
            }
            String content = LlmResponseParser.extractContent(response, objectMapper());
            return (content == null || content.isBlank()) ? Optional.empty() : Optional.of(content);
        } catch (Exception e) {
            Throwable root = rootCause(e);
            if (root instanceof WebClientResponseException wce) {
                log.error("[{}/{}] HTTP ошибка {} на {}: {}", sourceId(), model(), wce.getStatusCode(),
                        resolveEndpoint(), sanitizeLogValue(wce.getMessage()));
                return Optional.empty();
            }
            if (root instanceof TimeoutException) {
                log.error("[{}/{}] Таймаут запроса на {} ({} сек)", sourceId(), model(), resolveEndpoint(), timeout.getSeconds());
                return Optional.empty();
            }
            if (root instanceof WebClientRequestException wcre) {
                log.error("[{}/{}] Ошибка соединения на {}: {}", sourceId(), model(), resolveEndpoint(),
                        sanitizeLogValue(wcre.getMessage()));
                return Optional.empty();
            }
            log.error("[{}/{}] Запрос не удался на {}: {}", sourceId(), model(), resolveEndpoint(),
                    sanitizeLogValue(root.getMessage()), root);
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> generateStructuredJson(String prompt) {
        if (isApiKeyMissing() || prompt == null || prompt.isBlank()) {
            return Optional.empty();
        }
        ChatRequest request = LlmRequestBuilder.build(
                model(),
                AiPrompts.SYSTEM_PROMPT,
                LlmRequestBuilder.withStrictJsonContract(prompt),
                temperature()
        );
        return requestContent(request);
    }

    private static String sanitizeLogValue(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        String compact = value
                .replace('\n', ' ')
                .replace('\r', ' ')
                .replace('\t', ' ')
                .trim();
        return AiResponseParser.preview(compact, 500);
    }

    private static Throwable rootCause(Throwable throwable) {
        Throwable root = throwable;
        if (root instanceof CompletionException && root.getCause() != null) {
            root = root.getCause();
        }
        while (root.getCause() != null && root.getCause() != root) {
            root = root.getCause();
        }
        return root;
    }
}
