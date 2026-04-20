package com.cheatsheet.quiz.bootstrap;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
                .andExpect(status().isOk());
    }
}
