package com.cheatsheet.quiz.config.web;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация OpenAPI 3 (Swagger UI).
 *
 * <p>Swagger UI доступен по адресу {@code /swagger-ui.html}.
 * API-документация: {@code /v3/api-docs}.</p>
 */
@Configuration
public class OpenApiConfig {

    /** Версия приложения (подставляется из {@code spring.application.version}). */
    private final String appVersion;

    public OpenApiConfig(@Value("${spring.application.version:0.0.1-SNAPSHOT}") String appVersion) {
        this.appVersion = appVersion;
    }

    /**
     * Конфигурирует OpenAPI-спецификацию с заголовком и версией.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Interview Prep API")
                        .version(appVersion)
                        .description("API для экспорта прогресса и тестирования перед интервью"));
    }
}
