package com.cheatsheet.quiz.service.ai;

import com.cheatsheet.quiz.config.app.AppProperties;
import com.cheatsheet.quiz.domain.OptionSource;
import com.cheatsheet.quiz.service.ai.client.ConfigurableAiClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigurableAiClientTest {

    @Test
    void exposesConfiguredProviderIdentityAndSettings() {
        WebClient webClient = WebClient.builder().baseUrl("http://localhost").build();
        AppProperties.Ai aiConfig = new AppProperties.Ai();
        ObjectMapper objectMapper = new ObjectMapper();

        ConfigurableAiClient client = new ConfigurableAiClient(
                webClient,
                "secret-key",
                "gpt-test",
                "/v1/chat/completions",
                0.3,
                aiConfig,
                objectMapper,
                OptionSource.OPENAI
        );

        assertThat(client.sourceId()).isEqualTo(OptionSource.OPENAI);
        assertThat(client.webClient()).isSameAs(webClient);
        assertThat(client.apiKey()).isEqualTo("secret-key");
        assertThat(client.model()).isEqualTo("gpt-test");
        assertThat(client.endpoint()).isEqualTo("/v1/chat/completions");
        assertThat(client.temperature()).isEqualTo(0.3);
        assertThat(client.aiConfig()).isSameAs(aiConfig);
        assertThat(client.objectMapper()).isSameAs(objectMapper);
    }
}
