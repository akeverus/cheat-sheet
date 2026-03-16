/**
 * Конфигурация приложения (Spring Boot, свойства, бины).
 *
 * <p>Содержит:</p>
 * <ul>
 *   <li>{@link com.cheatsheet.quiz.config.app.AppProperties} — корневые настройки {@code app.*};</li>
 *   <li>{@link com.cheatsheet.quiz.config.app.InfrastructureConfig} — WebClient, кэш, пул предзагрузки;</li>
 *   <li>{@link com.cheatsheet.quiz.config.web.OpenApiConfig} — настройки Swagger/OpenAPI.</li>
 * </ul>
 */
package com.cheatsheet.quiz.config;
