package com.cheatsheet.quiz.service.ai.client;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import com.cheatsheet.quiz.config.app.AppProperties;
import com.cheatsheet.quiz.domain.OptionSource;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
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
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
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

    @Override
    public OptionSource sourceId() {
        return source;
    }

    @Override
    public WebClient webClient() {
        return webClient;
    }

    @Override
    public String apiKey() {
        return apiKey;
    }

    @Override
    public String model() {
        return model;
    }

    @Override
    public String endpoint() {
        return endpoint;
    }

    @Override
    public double temperature() {
        return temperature;
    }

    @Override
    public AppProperties.Ai aiConfig() {
        return aiConfig;
    }

    @Override
    public ObjectMapper objectMapper() {
        return objectMapper;
    }
}
