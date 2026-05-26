package com.cheatsheet.quiz.infrastructure.health;

import com.cheatsheet.quiz.TestInterviewPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Контракт /actuator/info: блок {@code seeds} обязан присутствовать, иметь
 * положительное {@code totalTopics} и непустой {@code byCategory}.
 *
 * <p>Фиксирует структуру ответа от {@link SeedInfoContributor} — если кто-то
 * переименует поля или сломает classpath-сканирование, тест упадёт.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ActuatorInfoIntegrationTest {

    @DynamicPropertySource
    static void setInterviewPath(DynamicPropertyRegistry registry) {
        TestInterviewPath.register(registry);
    }

    @Autowired
    MockMvc mockMvc;

    @Test
    void actuatorInfoExposesSeedsSummary() throws Exception {
        mockMvc.perform(get("/actuator/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seeds.totalTopics").value(greaterThan(0)))
                .andExpect(jsonPath("$.seeds.byCategory").isMap());
    }
}
