package com.cheatsheet.quiz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Точка входа в приложение тестирования перед интервью.
 *
 * <p>Spring Boot приложение для подготовки к собеседованиям
 * с интервальным повторением (SM-2) и AI-генерацией вариантов ответов.</p>
 *
 * <p>Поддерживаемые AI-провайдеры:</p>
 * <ul>
 *   <li>Spring AI — кастомная модель (по умолчанию);</li>
 *   <li>OpenAI — ChatGPT;</li>
 *   <li>DeepSeek.</li>
 * </ul>
 *
 * @see com.cheatsheet.quiz.config.app.AppProperties
 * @see com.cheatsheet.quiz.domain.AiProvider
 */
@SpringBootApplication
@EnableAsync
public class InterviewApplication {

    public static void main(String[] args) {
        SpringApplication.run(InterviewApplication.class, args);
    }
}
