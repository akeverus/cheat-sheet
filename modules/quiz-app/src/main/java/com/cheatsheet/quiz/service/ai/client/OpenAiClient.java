package com.cheatsheet.quiz.service.ai.client;

import com.cheatsheet.quiz.config.app.AppProperties;
import com.cheatsheet.quiz.domain.OptionSource;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * AI-клиент для OpenAI API (ChatGPT).
 *
 * <p>Использует модели OpenAI (gpt-4o-mini, gpt-4o и др.).
 * Конфигурация считывается из {@code app.openai.*} в {@code application.yml}.</p>
 *
 * <p>Реализация делегирована {@link ConfigurableAiClient} для устранения
 * дублирования с {@link DeepSeekClient}.</p>
 *
 * @see AbstractAiClient
 * @see AppProperties.OpenAi
 */
@Component
public class OpenAiClient extends ConfigurableAiClient {

    public OpenAiClient(
            @Qualifier("openaiWebClient") WebClient webClient,
            AppProperties props,
            ObjectMapper objectMapper
    ) {
        super(
                webClient,
                props.getOpenai().getApiKey(),
                props.getOpenai().getModel(),
                props.getOpenai().getEndpoint(),
                props.getOpenai().getTemperature(),
                props.getAi(),
                objectMapper,
                OptionSource.OPENAI
        );
    }
}
