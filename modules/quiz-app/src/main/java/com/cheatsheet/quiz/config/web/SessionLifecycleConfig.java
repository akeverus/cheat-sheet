package com.cheatsheet.quiz.config.web;

import com.cheatsheet.quiz.feature.interview.controller.HttpSessionStateService;
import com.cheatsheet.quiz.feature.interview.controller.InterviewSessionExpiryListener;
import com.cheatsheet.quiz.feature.interview.service.flow.PauseService;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * FLOW-04: регистрация {@link InterviewSessionExpiryListener} в embedded-контейнере.
 *
 * <p>Регистрируем ЯВНО через {@link ServletListenerRegistrationBean}, а не полагаемся
 * на авто-детект бинов-листенеров: явная регистрация однозначна и не зависит от
 * порядка/условий авто-конфигурации.</p>
 */
@Configuration
public class SessionLifecycleConfig {

    @Bean
    public ServletListenerRegistrationBean<InterviewSessionExpiryListener> interviewSessionExpiryListener(
            PauseService pauseService,
            HttpSessionStateService httpSessionStateService) {
        return new ServletListenerRegistrationBean<>(
                new InterviewSessionExpiryListener(pauseService, httpSessionStateService));
    }
}
