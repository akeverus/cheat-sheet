package com.cheatsheet.quiz.service.ai.client;

import com.cheatsheet.quiz.config.app.AppProperties;
import com.cheatsheet.quiz.domain.OptionSource;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * AI-клиент для DeepSeek API.
 *
 * <p>Использует OpenAI-совместимый формат запросов/ответов.
 * Конфигурация считывается из {@code app.deepseek.*} в {@code application.yml}.</p>
 *
 * <p>Реализация делегирована {@link ConfigurableAiClient} для устранения
 * дублирования с {@link OpenAiClient}.</p>
 *
 * @see AbstractAiClient
 * @see AppProperties.DeepSeek
 */
@Component
public class DeepSeekClient extends ConfigurableAiClient {

    public DeepSeekClient(
            @Qualifier("deepseekWebClient") WebClient webClient,
            AppProperties props,
            ObjectMapper objectMapper
    ) {
        super(
                webClient,
                props.getDeepseek().getApiKey(),
                props.getDeepseek().getModel(),
                props.getDeepseek().getEndpoint(),
                props.getDeepseek().getTemperature(),
                props.getAi(),
                objectMapper,
                OptionSource.DEEPSEEK
        );
    }
}
