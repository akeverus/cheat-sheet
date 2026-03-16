package com.cheatsheet.quiz.config.ai;

import com.cheatsheet.quiz.config.app.AppProperties;
import com.cheatsheet.quiz.domain.AiProvider;
import com.cheatsheet.quiz.service.ai.AiQuestionClient;
import com.cheatsheet.quiz.service.ai.client.DeepSeekClient;
import com.cheatsheet.quiz.service.ai.client.OpenAiClient;
import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

/**
 * Конфигурация WebClient-ов для AI-провайдеров (DeepSeek, OpenAI)
 * и выбор единственного {@link AiQuestionClient} без fallback.
 */
@Configuration
@Slf4j
public class AiClientConfig {

    @Bean
    WebClient deepseekWebClient(AppProperties props) {
        AppProperties.DeepSeek cfg = props.getDeepseek();
        String baseUrl = StringUtils.hasText(cfg.getBaseUrl()) ? cfg.getBaseUrl() : "https://api.deepseek.com";
        String apiKey = StringUtils.hasText(cfg.getApiKey()) ? cfg.getApiKey() : "";
        return aiWebClient(baseUrl, apiKey, props.getAi().getTimeoutSeconds());
    }

    @Bean
    WebClient openaiWebClient(AppProperties props) {
        AppProperties.OpenAi cfg = props.getOpenai();
        String baseUrl = StringUtils.hasText(cfg.getBaseUrl()) ? cfg.getBaseUrl() : "https://api.openai.com";
        String apiKey = StringUtils.hasText(cfg.getApiKey()) ? cfg.getApiKey() : "";
        return aiWebClient(baseUrl, apiKey, props.getAi().getTimeoutSeconds());
    }

    private WebClient aiWebClient(String baseUrl, String apiKey, int timeoutSeconds) {
        Duration timeout = Duration.ofSeconds(Math.max(5, timeoutSeconds));
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) timeout.toMillis())
                .responseTimeout(timeout);
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .build();
    }

    @Bean
    AiQuestionClient aiQuestionClient(
            AppProperties props,
            DeepSeekClient deepSeekClient,
            OpenAiClient openAiClient
    ) {
        AiProvider primary = props.getAiProvider();
        AiQuestionClient client = toGenerator(primary, deepSeekClient, openAiClient);
        log.info("AI provider выбран: {}", primary);
        return client;
    }

    private AiQuestionClient toGenerator(
            AiProvider provider,
            DeepSeekClient deepSeekClient,
            OpenAiClient openAiClient
    ) {
        return switch (provider) {
            case OPENAI -> openAiClient;
            case DEEPSEEK -> deepSeekClient;
        };
    }
}
