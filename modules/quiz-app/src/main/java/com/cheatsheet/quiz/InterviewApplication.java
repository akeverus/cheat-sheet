package com.cheatsheet.quiz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Точка входа в приложение тестирования перед интервью.
 *
 * <p>Spring Boot приложение для подготовки к собеседованиям
 * с интервальным повторением (SM-2). Вопросы и варианты берутся из
 * markdown-файлов и JSON-сидеров ({@code seed/mcq/**}).</p>
 *
 * @see com.cheatsheet.quiz.config.app.AppProperties
 */
@SpringBootApplication
@EnableAsync
public class InterviewApplication {

    public static void main(String[] args) {
        SpringApplication.run(InterviewApplication.class, args);
    }
}
