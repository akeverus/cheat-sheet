package com.cheatsheet.quiz.service.ai;

import com.cheatsheet.quiz.config.AppProperties;
import com.cheatsheet.quiz.domain.OptionSource;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Параметризованная реализация {@link AbstractAiClient}, принимающая
 * все настройки через конструктор.
 *
 * <p>Устраняет дублирование кода между {@link DeepSeekClient} и {@link OpenAiClient},
 * которые отличались только источником конфигурации.</p>
 *
 * @see DeepSeekClient
 * @see OpenAiClient
 */
public class ConfigurableAiClient extends AbstractAiClient {

    private final WebClient webClient;
    private final String apiKey;
    private final String model;
    private final String endpoint;
    private final double temperature;
    private final AppProperties.Ai aiConfig;
    private final ObjectMapper objectMapper;
    private final OptionSource source;

    /**
     * Создаёт экземпляр AI-клиента с указанными настройками.
     *
     * @param webClient     сконфигурированный WebClient для провайдера
     * @param apiKey        API-ключ (null — генерация пропускается)
     * @param model         название модели
     * @param endpoint      путь к эндпоинту чата
     * @param temperature   температура генерации
     * @param aiConfig      общие настройки AI-запросов
     * @param objectMapper  общий ObjectMapper
     * @param source        идентификатор провайдера
     */
    public ConfigurableAiClient(
            WebClient webClient,
            String apiKey,
            String model,
            String endpoint,
            double temperature,
            AppProperties.Ai aiConfig,
            ObjectMapper objectMapper,
            OptionSource source
    ) {
        this.webClient = webClient;
        this.apiKey = apiKey;
        this.model = model;
        this.endpoint = endpoint;
        this.temperature = temperature;
        this.aiConfig = aiConfig;
        this.objectMapper = objectMapper;
        this.source = source;
    }

    @Override
    public OptionSource sourceId() {
        return source;
    }

    @Override
    protected WebClient webClient() {
        return webClient;
    }

    @Override
    protected String apiKey() {
        return apiKey;
    }

    @Override
    protected String model() {
        return model;
    }

    @Override
    protected String endpoint() {
        return endpoint;
    }

    @Override
    protected double temperature() {
        return temperature;
    }

    @Override
    protected AppProperties.Ai aiConfig() {
        return aiConfig;
    }

    @Override
    protected ObjectMapper objectMapper() {
        return objectMapper;
    }
}
