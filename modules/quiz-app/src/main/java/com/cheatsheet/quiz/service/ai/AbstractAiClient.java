package com.cheatsheet.quiz.service.ai;

import com.cheatsheet.quiz.config.AppProperties;
import com.cheatsheet.quiz.llm.LlmClient;
import com.cheatsheet.quiz.llm.LlmRequestBuilder;
import com.cheatsheet.quiz.llm.LlmResponseParser;
import com.cheatsheet.quiz.service.ai.dto.ChatMessage;
import com.cheatsheet.quiz.service.ai.dto.ChatRequest;
import com.cheatsheet.quiz.service.ai.dto.GeneratedOptions;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.util.concurrent.TimeoutException;

/**
 * Базовая реализация AI-клиента для OpenAI-совместимых API (DeepSeek, OpenAI).
 *
 * <p>Инкапсулирует общую логику:</p>
 * <ul>
 *   <li>построение промпта с инструкцией для модели;</li>
 *   <li>отправку HTTP-запроса через {@link WebClient} (реактивный, с retry);</li>
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
 * @see OptionGenerator
 */
@Slf4j
public abstract class AbstractAiClient implements OptionGenerator, LlmClient {

    private final LlmRequestBuilder llmRequestBuilder = new LlmRequestBuilder();
    private final LlmResponseParser llmResponseParser = new LlmResponseParser();

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

    /**
     * Отправляет chat completion запрос к провайдеру и возвращает извлечённый текст ответа модели.
     *
     * <p>Используется всеми методами генерации (варианты, подсказки, диаграммы и т.д.) для устранения дублирования.</p>
     *
     * @param request   тело запроса (модель, сообщения, температура)
     * @param timeout   таймаут ожидания ответа
     * @param withRetry {@code true} — при 429/5xx выполнять retry с exponential backoff; {@code false} — без retry
     * @return содержимое {@code choices[0].message.content} или пустой Optional при ошибке/пустом ответе
     */
    @Override
    public Optional<String> sendChatRequest(ChatRequest request, Duration timeout, boolean withRetry) {
        try {
            var mono = webClient().post()
                    .uri(resolveEndpoint())
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(timeout);
            if (withRetry) {
                AppProperties.Ai cfg = aiConfig();
                int maxRetries = cfg.getMaxRetries();
                Duration retryBackoff = Duration.ofSeconds(cfg.getRetryBackoffSeconds());
                mono = mono.retryWhen(Retry.backoff(maxRetries, retryBackoff)
                        .filter(AiRetryUtils::isRetryable)
                        .doBeforeRetry(signal -> log.warn(
                                "[{}/{}] retry #{} для {}: {}",
                                sourceId(),
                                model(),
                                signal.totalRetries() + 1,
                                resolveEndpoint(),
                                AiRetryUtils.retryReason(signal.failure())
                        )));
            }
            String response = mono.block();
            if (response == null || response.isBlank()) {
                return Optional.empty();
            }
            String content = llmResponseParser.extractContent(response, objectMapper());
            return (content == null || content.isBlank()) ? Optional.empty() : Optional.of(content);
        } catch (Exception e) {
            Throwable root = AiRetryUtils.rootCause(e);
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

    /**
     * Генерирует варианты ответов, обращаясь к AI-провайдеру.
     *
     * <p>Алгоритм:</p>
     * <ol>
     *   <li>Проверяет наличие API-ключа;</li>
     *   <li>Собирает промпт и отправляет POST-запрос;</li>
     *   <li>При ошибке 429 (rate limit) — выполняет retry с exponential backoff;</li>
     *   <li>Парсит JSON-ответ и возвращает {@link GeneratedOptions}.</li>
     * </ol>
     *
     * @param questionText   текст вопроса
     * @param answerMarkdown markdown правильного ответа
     * @return варианты или {@link Optional#empty()} при ошибке/отсутствии ключа
     */
    @Override
    public Optional<GeneratedOptions> generateOptions(String questionText, String answerMarkdown, String codeSnippet) {
        return generateOptions(questionText, answerMarkdown, codeSnippet, null);
    }

    @Override
    public Optional<GeneratedOptions> generateOptions(
            String questionText,
            String answerMarkdown,
            String codeSnippet,
            String qualityFixContext
    ) {
        if (isApiKeyMissing()) {
            log.debug("{}: API-ключ не задан, генерация пропущена", sourceId());
            return Optional.empty();
        }

        int maxLen = maxInputLength();
        String safeQuestionText = AiResponseParser.truncate(questionText, maxLen);
        String safeAnswerMarkdown = AiResponseParser.truncate(answerMarkdown, maxLen);

        String questionPreview = AiResponseParser.preview(safeQuestionText, aiConfig().getQuestionPreviewLength());
        log.info("[{}/{}] Генерация вариантов для: '{}'", sourceId(), model(), questionPreview);

        long startMs = System.currentTimeMillis();
        ChatRequest request = buildOptionsRequest(safeQuestionText, safeAnswerMarkdown, codeSnippet, qualityFixContext);
        Duration timeout = Duration.ofSeconds(aiConfig().getTimeoutSeconds());
        Optional<String> contentOpt = sendChatRequest(request, timeout, true);
        if (contentOpt.isEmpty()) {
            log.warn("[{}/{}] Пустой content в ответе модели", sourceId(), model());
            return Optional.empty();
        }

        Optional<GeneratedOptions> opts = parseAndValidateOptions(contentOpt.get());
        if (opts.isPresent()) {
            long elapsedMs = System.currentTimeMillis() - startMs;
            String correctPreview = AiResponseParser.preview(opts.get().correct(), aiConfig().getCorrectPreviewLength());
            log.info("[{}/{}] Ответ получен за {}ms: correct='{}', wrong=[{} шт]",
                    sourceId(), model(), elapsedMs, correctPreview, opts.get().wrong().size());
            log.debug("[{}/{}] Ответ модели (preview): {}", sourceId(), model(),
                    sanitizeLogValue(AiResponseParser.preview(contentOpt.get(), 300)));
        }
        return opts;
    }

    /**
     * Собирает промпт и запрос для генерации вариантов ответов.
     */
    private static final int OPTIONS_MAX_TOKENS = 4096;
    private static final int CANONICALIZE_MAX_TOKENS = 4096;

    private ChatRequest buildOptionsRequest(
            String questionText,
            String answerMarkdown,
            String codeSnippet,
            String qualityFixContext
    ) {
        String prompt = buildOptionsPrompt(questionText, answerMarkdown, codeSnippet, qualityFixContext);
        return llmRequestBuilder.buildWithMaxTokens(
                model(),
                AiPrompts.SYSTEM_PROMPT,
                prompt,
                temperature(),
                OPTIONS_MAX_TOKENS
        );
    }

    /**
     * Собирает промпт для генерации вариантов ответов (с кодом или без).
     */
    private String buildOptionsPrompt(
            String questionText,
            String answerMarkdown,
            String codeSnippet,
            String qualityFixContext
    ) {
        String safeQuestion = AiPrompts.wrapUserInput(questionText);
        String safeAnswer = AiPrompts.wrapUserInput(answerMarkdown);
        String safeCodeSnippet = AiResponseParser.truncate(codeSnippet, maxInputLength());
        if (!safeCodeSnippet.isEmpty()) {
            String basePrompt = AiPrompts.CODE_OPTIONS_PROMPT_TEMPLATE.formatted(
                    safeQuestion, AiPrompts.wrapUserInput(safeCodeSnippet), safeAnswer);
            return appendQualityFixContext(basePrompt, qualityFixContext);
        }
        String basePrompt = AiPrompts.USER_PROMPT_TEMPLATE.formatted(safeQuestion, safeAnswer);
        return appendQualityFixContext(basePrompt, qualityFixContext);
    }

    private static String appendQualityFixContext(String basePrompt, String qualityFixContext) {
        if (qualityFixContext == null || qualityFixContext.isBlank()) {
            return basePrompt;
        }
        return basePrompt + "\n\n" +
                "Дополнительные требования quality retry (исправь и верни новый JSON):\n" +
                AiPrompts.wrapUserInput(qualityFixContext);
    }

    /**
     * Парсит JSON-ответ модели и проверяет наличие correct и минимум 2 wrong-вариантов.
     * При ошибке пытается извлечь JSON из текста и распарсить повторно.
     */
    private Optional<GeneratedOptions> parseAndValidateOptions(String content) {
        GeneratedOptions opts = tryParseOptions(content);
        if (opts == null && !content.trim().startsWith("{")) {
            String extracted = AiResponseParser.extractJsonObject(content);
            if (!extracted.equals(content)) {
                log.debug("[{}/{}] Извлечён JSON из текстового ответа модели", sourceId(), model());
                opts = tryParseOptions(extracted);
            }
        }
        if (opts == null) {
            log.warn("[{}/{}] Не удалось распарсить ответ модели. Сырой ответ (первые 500 символов): {}",
                    sourceId(), model(), sanitizeLogValue(AiResponseParser.preview(content, 500)));
            return Optional.empty();
        }
        if (opts.correct() == null || opts.correct().isBlank()) {
            log.warn("[{}/{}] AI вернул пустой correct, ответ отброшен. Сырой ответ: {}",
                    sourceId(), model(), sanitizeLogValue(AiResponseParser.preview(content, 500)));
            return Optional.empty();
        }
        return Optional.of(opts);
    }

    private GeneratedOptions tryParseOptions(String content) {
        try {
            return AiResponseParser.parseOptions(content, objectMapper());
        } catch (Exception e) {
            log.debug("[{}/{}] Ошибка парсинга JSON: {}", sourceId(), model(), sanitizeLogValue(e.getMessage()));
            return null;
        }
    }

    @Override
    public Optional<CanonicalQuestion> canonicalizeQuestion(
            String rawQuestionTitle,
            String rawAnswerMarkdown,
            String sourcePath
    ) {
        if (isApiKeyMissing()) {
            log.debug("{}: API-ключ не задан, канонизация вопроса пропущена", sourceId());
            return Optional.empty();
        }

        String safeQuestion = AiPrompts.wrapUserInput(AiResponseParser.truncate(rawQuestionTitle, maxInputLength()));
        String safeAnswer = AiPrompts.wrapUserInput(AiResponseParser.truncate(rawAnswerMarkdown, maxInputLength()));
        String safeSourcePath = sourcePath == null ? "" : sourcePath;
        String prompt = AiPrompts.CANONICALIZE_QUESTION_PROMPT_TEMPLATE.formatted(safeSourcePath, safeQuestion, safeAnswer);

        ChatRequest request = llmRequestBuilder.buildWithMaxTokens(
                model(),
                AiPrompts.SYSTEM_PROMPT,
                prompt,
                temperature(),
                CANONICALIZE_MAX_TOKENS
        );
        Duration timeout = Duration.ofSeconds(aiConfig().getTimeoutSeconds());
        Optional<String> contentOpt = sendChatRequest(request, timeout, true);
        if (contentOpt.isEmpty()) {
            return Optional.empty();
        }
        try {
            Optional<CanonicalQuestion> parsed = AiResponseParser.parseCanonicalQuestion(contentOpt.get(), objectMapper());
            if (parsed.isEmpty()) {
                log.warn("[{}/{}] Канонизация вернула пустой payload", sourceId(), model());
            }
            return parsed;
        } catch (Exception e) {
            log.warn("[{}/{}] Ошибка парсинга канонизации вопроса: {}", sourceId(), model(),
                    sanitizeLogValue(e.getMessage()));
            return Optional.empty();
        }
    }

    @Override
    public Optional<List<String>> generateAlternativeQuestions(String questionText, String answerMarkdown, int count) {
        if (isApiKeyMissing()) {
            log.debug("{}: API-ключ не задан, генерация альтернативных вопросов пропущена", sourceId());
            return Optional.empty();
        }
        if (count < 1 || count > 5) {
            return Optional.empty();
        }

        String shortAnswer = AiResponseParser.truncate(answerMarkdown, aiConfig().getAnswerPreviewLength());
        String prompt = AiPrompts.ALTERNATIVE_QUESTIONS_PROMPT_TEMPLATE.formatted(
                count, AiPrompts.wrapUserInput(questionText), AiPrompts.wrapUserInput(shortAnswer));
        ChatRequest request = new ChatRequest(
                model(),
                List.of(
                        new ChatMessage("system", AiPrompts.SYSTEM_PROMPT),
                        new ChatMessage("user", prompt)
                ),
                temperature()
        );
        Duration timeout = Duration.ofSeconds(aiConfig().getTimeoutSeconds());
        Optional<String> contentOpt = sendChatRequest(request, timeout, true);
        if (contentOpt.isEmpty()) {
            return Optional.empty();
        }
        try {
            List<String> questions = AiResponseParser.parseAlternativeQuestions(contentOpt.get(), objectMapper());
            return questions.isEmpty() ? Optional.empty() : Optional.of(questions);
        } catch (Exception e) {
            log.error("{}: парсинг альтернативных вопросов не удался: {}", sourceId(), sanitizeLogValue(e.getMessage()));
            return Optional.empty();
        }
    }

    @Override
    public Optional<List<String>> generateHints(String questionText, String answerMarkdown, List<String> wrongOptions) {
        if (isApiKeyMissing()) {
            log.debug("{}: API-ключ не задан, генерация подсказок пропущена", sourceId());
            return Optional.empty();
        }

        String questionPreview = AiResponseParser.preview(questionText, aiConfig().getQuestionPreviewLength());
        log.info("[{}/{}] Генерация подсказок для: '{}'", sourceId(), model(), questionPreview);

        String shortAnswer = AiResponseParser.preview(answerMarkdown, aiConfig().getAnswerPreviewLength());
        String wrongStr = wrongOptions != null && !wrongOptions.isEmpty()
                ? String.join("; ", wrongOptions.subList(0, Math.min(3, wrongOptions.size())))
                : "нет вариантов";

        String prompt = AiPrompts.HINTS_PROMPT_TEMPLATE.formatted(
                AiPrompts.wrapUserInput(questionText), AiPrompts.wrapUserInput(shortAnswer), wrongStr);
        ChatRequest request = new ChatRequest(
                model(),
                List.of(
                        new ChatMessage("system", AiPrompts.SYSTEM_PROMPT),
                        new ChatMessage("user", prompt)
                ),
                temperature()
        );
        Duration timeout = Duration.ofSeconds(aiConfig().getTimeoutSeconds());
        Optional<String> contentOpt = sendChatRequest(request, timeout, true);
        if (contentOpt.isEmpty()) {
            return Optional.empty();
        }
        try {
            List<String> hints = AiResponseParser.parseHints(contentOpt.get(), objectMapper());
            if (hints.isEmpty()) {
                log.warn("[{}/{}] AI вернул пустой набор подсказок", sourceId(), model());
                return Optional.empty();
            }
            log.info("[{}/{}] Подсказки сгенерированы: {} шт", sourceId(), model(), hints.size());
            return Optional.of(hints);
        } catch (Exception e) {
            log.error("[{}/{}] Генерация подсказок не удалась: {}", sourceId(), model(), sanitizeLogValue(e.getMessage()), e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> generateDiagram(String questionText, String answerMarkdown, String topic) {
        if (isApiKeyMissing()) {
            return Optional.empty();
        }

        String questionPreview = AiResponseParser.preview(questionText, aiConfig().getQuestionPreviewLength());
        log.info("[{}/{}] Генерация диаграммы для: '{}'", sourceId(), model(), questionPreview);

        String shortAnswer = AiResponseParser.preview(answerMarkdown, aiConfig().getAnswerPreviewLength());
        String topicStr = topic != null ? topic : "general";

        String prompt = AiPrompts.DIAGRAM_PROMPT_TEMPLATE.formatted(
                AiPrompts.wrapUserInput(questionText), AiPrompts.wrapUserInput(shortAnswer), topicStr);
        ChatRequest request = new ChatRequest(
                model(),
                List.of(
                        new ChatMessage("system", AiPrompts.SYSTEM_PROMPT),
                        new ChatMessage("user", prompt)
                ),
                temperature()
        );
        Duration timeout = Duration.ofSeconds(aiConfig().getTimeoutSeconds());
        Optional<String> contentOpt = sendChatRequest(request, timeout, true);
        if (contentOpt.isEmpty()) {
            return Optional.empty();
        }
        try {
            Optional<String> mermaidOpt = AiResponseParser.parseDiagram(contentOpt.get(), objectMapper());
            if (mermaidOpt.isEmpty()) {
                log.debug("[{}/{}] AI решил, что диаграмма не нужна", sourceId(), model());
                return Optional.empty();
            }
            String mermaid = mermaidOpt.get();
            log.info("[{}/{}] Диаграмма сгенерирована: {} символов", sourceId(), model(), mermaid.length());
            return Optional.of(mermaid);
        } catch (Exception e) {
            log.error("[{}/{}] Генерация диаграммы не удалась: {}", sourceId(), model(), sanitizeLogValue(e.getMessage()), e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> cleanCodeSnippet(String code, String questionText) {
        if (isApiKeyMissing() || code == null || code.isBlank()) {
            return Optional.empty();
        }

        log.info("[{}/{}] Очистка code snippet от подсказок для: '{}'",
                sourceId(), model(), AiResponseParser.preview(questionText, aiConfig().getQuestionPreviewLength()));

        String prompt = AiPrompts.CLEAN_CODE_PROMPT_TEMPLATE.formatted(
                AiPrompts.wrapUserInput(questionText != null ? questionText : ""), AiPrompts.wrapUserInput(code));
        ChatRequest request = new ChatRequest(
                model(),
                List.of(
                        new ChatMessage("system", AiPrompts.CODE_ONLY_SYSTEM_PROMPT),
                        new ChatMessage("user", prompt)
                ),
                0.1 // низкая температура — нужен точный результат
        );
        Duration timeout = Duration.ofSeconds(aiConfig().getTimeoutSeconds());
        Optional<String> contentOpt = sendChatRequest(request, timeout, true);
        if (contentOpt.isEmpty()) {
            return Optional.empty();
        }
        String cleaned = AiResponseParser.stripCodeFences(contentOpt.get()).trim();
        if (cleaned.isBlank() || cleaned.length() < 10) {
            log.warn("[{}/{}] Очищенный код слишком короткий ({} символов), игнорируем",
                    sourceId(), model(), cleaned.length());
            return Optional.empty();
        }
        log.info("[{}/{}] Код очищен: {} → {} символов", sourceId(), model(), code.length(), cleaned.length());
        return Optional.of(cleaned);
    }

    @Override
    public Optional<String> generateWrongAnswerFeedback(String questionText, String selectedOptionText,
                                                         String correctOptionText, String answerMarkdown) {
        if (isApiKeyMissing()) {
            return Optional.empty();
        }

        String questionPreview = AiResponseParser.preview(questionText, aiConfig().getQuestionPreviewLength());
        log.info("[{}/{}] Генерация фидбэка при ошибке для: '{}'", sourceId(), model(), questionPreview);

        String prompt = AiPrompts.WRONG_ANSWER_FEEDBACK_TEMPLATE.formatted(
                AiPrompts.wrapUserInput(AiResponseParser.truncate(questionText, maxInputLength())),
                AiPrompts.wrapUserInput(AiResponseParser.truncate(selectedOptionText, 500)),
                AiPrompts.wrapUserInput(AiResponseParser.truncate(correctOptionText, 500)));
        ChatRequest request = new ChatRequest(
                model(),
                List.of(
                        new ChatMessage("system", AiPrompts.SYSTEM_PROMPT),
                        new ChatMessage("user", prompt)
                ),
                temperature()
        );
        Duration timeout = Duration.ofSeconds(aiConfig().getTimeoutSeconds());
        return sendChatRequest(request, timeout, true);
    }

    @Override
    public Optional<String> reformatCorrectAnswer(String correctPlain, String questionText, List<String> wrongSamples) {
        if (isApiKeyMissing() || correctPlain == null || correctPlain.isBlank()) {
            return Optional.empty();
        }
        String samplesStr = wrongSamples != null && !wrongSamples.isEmpty()
                ? String.join("; ", wrongSamples.subList(0, Math.min(3, wrongSamples.size())))
                : "нет примеров";
        String prompt = AiPrompts.REFORMAT_CORRECT_PROMPT_TEMPLATE.formatted(
                AiPrompts.wrapUserInput(questionText), AiPrompts.wrapUserInput(correctPlain), samplesStr);
        ChatRequest request = new ChatRequest(
                model(),
                List.of(
                        new ChatMessage("system", AiPrompts.SYSTEM_PROMPT),
                        new ChatMessage("user", prompt)
                ),
                temperature()
        );
        Duration timeout = Duration.ofSeconds(aiConfig().getTimeoutSeconds());
        Optional<String> contentOpt = sendChatRequest(request, timeout, true);
        if (contentOpt.isEmpty()) {
            return Optional.empty();
        }
        try {
            return AiResponseParser.parseReformatted(contentOpt.get(), objectMapper());
        } catch (Exception e) {
            log.error("{}: переформатирование правильного ответа не удалось: {}", sourceId(),
                    sanitizeLogValue(e.getMessage()), e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> generateTakeaway(String questionText, String answerMarkdown) {
        if (isApiKeyMissing()) {
            log.debug("{}: API-ключ не задан, генерация takeaway пропущена", sourceId());
            return Optional.empty();
        }

        String questionPreview = AiResponseParser.preview(questionText, aiConfig().getQuestionPreviewLength());
        log.info("[{}/{}] Генерация takeaway для: '{}'", sourceId(), model(), questionPreview);

        String answerTruncated = AiResponseParser.truncate(answerMarkdown, maxInputLength());
        String prompt = AiPrompts.TAKEAWAY_PROMPT_TEMPLATE.formatted(
                AiPrompts.wrapUserInput(questionText), AiPrompts.wrapUserInput(answerTruncated));
        ChatRequest request = new ChatRequest(
                model(),
                List.of(
                        new ChatMessage("system", AiPrompts.SYSTEM_PROMPT),
                        new ChatMessage("user", prompt)
                ),
                temperature()
        );
        Duration timeout = Duration.ofSeconds(aiConfig().getTimeoutSeconds());
        Optional<String> contentOpt = sendChatRequest(request, timeout, false);
        if (contentOpt.isEmpty()) {
            return Optional.empty();
        }
        try {
            return AiResponseParser.parseTakeaway(contentOpt.get(), objectMapper());
        } catch (Exception e) {
            log.error("[{}/{}] Ошибка парсинга takeaway: {}", sourceId(), model(), sanitizeLogValue(e.getMessage()));
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> generateComparison(String questionText, String selectedOptionText,
                                               String correctOptionText) {
        if (isApiKeyMissing()) {
            log.debug("{}: API-ключ не задан, генерация comparison пропущена", sourceId());
            return Optional.empty();
        }

        String questionPreview = AiResponseParser.preview(questionText, aiConfig().getQuestionPreviewLength());
        log.info("[{}/{}] Генерация comparison для: '{}'", sourceId(), model(), questionPreview);

        String prompt = AiPrompts.COMPARISON_PROMPT_TEMPLATE.formatted(
                AiPrompts.wrapUserInput(AiResponseParser.truncate(questionText, maxInputLength())),
                AiPrompts.wrapUserInput(AiResponseParser.truncate(selectedOptionText, 500)),
                AiPrompts.wrapUserInput(AiResponseParser.truncate(correctOptionText, 500)));
        ChatRequest request = new ChatRequest(
                model(),
                List.of(
                        new ChatMessage("system", AiPrompts.SYSTEM_PROMPT),
                        new ChatMessage("user", prompt)
                ),
                temperature()
        );
        Duration timeout = Duration.ofSeconds(aiConfig().getTimeoutSeconds());
        return sendChatRequest(request, timeout, false);
    }

    @Override
    public Optional<String> generateCodeTrace(String questionText, String codeSnippet) {
        if (isApiKeyMissing() || codeSnippet == null || codeSnippet.isBlank()) {
            log.debug("{}: API-ключ не задан или код пуст, генерация code trace пропущена", sourceId());
            return Optional.empty();
        }

        String questionPreview = AiResponseParser.preview(questionText, aiConfig().getQuestionPreviewLength());
        log.info("[{}/{}] Генерация code trace для: '{}'", sourceId(), model(), questionPreview);

        String prompt = AiPrompts.CODE_TRACE_PROMPT_TEMPLATE.formatted(
                AiPrompts.wrapUserInput(questionText), AiPrompts.wrapUserInput(AiResponseParser.truncate(codeSnippet, maxInputLength())));
        ChatRequest request = new ChatRequest(
                model(),
                List.of(
                        new ChatMessage("system", AiPrompts.SYSTEM_PROMPT),
                        new ChatMessage("user", prompt)
                ),
                temperature()
        );
        Duration timeout = Duration.ofSeconds(aiConfig().getTimeoutSeconds());
        return sendChatRequest(request, timeout, false);
    }

    @Override
    public Optional<String> generateStructuredJson(String prompt) {
        if (isApiKeyMissing() || prompt == null || prompt.isBlank()) {
            return Optional.empty();
        }
        ChatRequest request = llmRequestBuilder.build(
                model(),
                AiPrompts.SYSTEM_PROMPT,
                llmRequestBuilder.withStrictJsonContract(prompt),
                temperature()
        );
        Duration timeout = Duration.ofSeconds(aiConfig().getTimeoutSeconds());
        return sendChatRequest(request, timeout, true);
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
}
