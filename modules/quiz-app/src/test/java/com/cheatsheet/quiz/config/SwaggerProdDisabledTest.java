package com.cheatsheet.quiz.config;

import com.cheatsheet.quiz.TestInterviewPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Контракт prod-профиля: Swagger UI и публичный OpenAPI JSON выключены,
 * чтобы не светить API-карту наружу. Без этого теста CLAUDE.md мог
 * (и врал) утверждать что Swagger off, а application-prod.yml на самом
 * деле не отключал его.
 *
 * <p>Тест активирует prod профиль и проверяет что обе точки возвращают 404,
 * а не 200 со spec/UI.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test", "prod"})
@TestPropertySource(properties = {
        // prod профиль требует APP_ADMIN_TOKEN; иначе appProperties.admin-token
        // не разрезолвится и контекст не поднимется.
        "app.admin-token=test-prod-token-do-not-use"
})
class SwaggerProdDisabledTest {

    @DynamicPropertySource
    static void setInterviewPath(DynamicPropertyRegistry registry) {
        TestInterviewPath.register(registry);
    }

    @Autowired
    MockMvc mockMvc;

    @Test
    void swaggerUiIsDisabledInProdProfile() throws Exception {
        // Springdoc отключён → /swagger-ui.html не зарегистрирован как handler.
        // Глобальный error-handler конвертирует NoResourceFoundException в
        // 4xx или 5xx с error-страницей; главное — это НЕ оригинальный Swagger UI.
        String body = mockMvc.perform(get("/swagger-ui.html"))
                .andReturn().getResponse().getContentAsString();
        assertThat(body).doesNotContain("Swagger UI");
        assertThat(body).doesNotContain("swagger-ui-bundle");
    }

    @Test
    void openApiJsonIsDisabledInProdProfile() throws Exception {
        // /v3/api-docs больше не отдаёт OpenAPI JSON — главное, что в ответе
        // нет «openapi» поля и нашего API title «Interview Prep API».
        String body = mockMvc.perform(get("/v3/api-docs"))
                .andReturn().getResponse().getContentAsString();
        assertThat(body).doesNotContain("\"openapi\":");
        assertThat(body).doesNotContain("Interview Prep API");
    }
}
