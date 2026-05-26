package com.cheatsheet.quiz.bootstrap;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Приложение должно стартовать и отдавать главную даже без AI-ключей.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "app.openai.api-key=",
        "app.deepseek.api-key=",
        "app.preload.startup-preload=false",
        "app.preload.full-warmup=false"
})
class NoAiModeStartupIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void indexPageOpens_inNoAiMode() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("Вопросы временно недоступны"))));
    }

    @Test
    void readinessProbeIsUp_inNoAiMode() throws Exception {
        // Без AI-ключей и при дефолтном AI_FALLBACK_ENABLED=false:
        // readiness обязан быть UP (db + seedCoverage оба ОК), потому что
        // sample-interview сидер уже загружен из test-resources.
        mockMvc.perform(get("/actuator/health/readiness"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void indexPageShowsFlashcardMode_whenQuestionsExist() throws Exception {
        String body = mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // В no-AI режиме при наличии вопроса должна показываться flashcard-фаза,
        // а не страница «Вопросы временно недоступны»
        if (body.contains("class=\"question-text\"") || body.contains("id=\"interview-card\"")) {
            org.assertj.core.api.Assertions.assertThat(body)
                    .contains("flashcard-phase")
                    .doesNotContain("Вопросы временно недоступны");
        }
    }
}
