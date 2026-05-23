package com.cheatsheet.quiz.api;

import com.cheatsheet.quiz.TestInterviewPath;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Контракт «public read-only страницы и health отдают 200 без AI-ключа».
 *
 * <p>Покрывает регрессию вида «кто-то сделал страницу зависящей от AI» —
 * в seed-first-режиме все эти URL должны работать.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PublicEndpointsSmokeTest {

    @DynamicPropertySource
    static void setProps(DynamicPropertyRegistry registry) {
        TestInterviewPath.register(registry);
    }

    @Autowired
    MockMvc mockMvc;

    @ParameterizedTest(name = "{0} → 200")
    @ValueSource(strings = {
            "/",
            "/stats",
            "/settings",
            "/review"
    })
    void publicPageReturnsHtml(String path) throws Exception {
        mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"));
    }

    @org.junit.jupiter.api.Test
    void actuatorHealthReturnsUp() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                // Spring Boot actuator V3 negotiates its own vendor media type.
                .andExpect(content().contentTypeCompatibleWith(
                        "application/vnd.spring-boot.actuator.v3+json"));
    }
}
