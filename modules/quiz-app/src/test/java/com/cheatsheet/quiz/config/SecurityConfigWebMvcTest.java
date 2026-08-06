package com.cheatsheet.quiz.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "app.admin-token=test-admin-token")
@AutoConfigureMockMvc
class SecurityConfigWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void csrfIsRequiredForMvcPostEndpoints() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/start"))
                .andExpect(status().isForbidden());

        mockMvc.perform(MockMvcRequestBuilders.post("/start")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(result -> assertThat(result.getResponse().getStatus()).isNotEqualTo(403));
    }

    @Test
    void csrfIsRequiredForApiMutatingEndpoints() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/favorite"))
                .andExpect(status().isForbidden());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/favorite")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(result -> assertThat(result.getResponse().getStatus()).isNotEqualTo(403));
    }

    @Test
    void sensitiveEndpointsAreBlockedWithoutAdminToken() throws Exception {
        // Отказ из security-фильтра идёт в том же формате RFC-7807, что и ошибки
        // из контроллеров: клиенту не нужно различать источник ошибки.
        mockMvc.perform(MockMvcRequestBuilders.get("/export"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("FORBIDDEN"))
                .andExpect(jsonPath("$.detail").isString());
    }

    @Test
    void sensitiveEndpointsAllowValidAdminToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/export")
                        .header("X-Admin-Token", "test-admin-token"))
                .andExpect(status().isOk());
    }

    @Test
    void destructiveResetIsBlockedWithoutAdminTokenInSingleMode() throws Exception {
        // Собственная проверка токена из контроллера убрана — гейт остался один, в
        // SecurityConfig. Отказ в дефолтном (single) режиме до этого утверждался только
        // тестом multi-режима, который по документам живой проверкой не покрыт. Эндпоинт
        // сносит весь банк MCQ-вариантов, поэтому отказ фиксируем явно и здесь.
        mockMvc.perform(MockMvcRequestBuilders.post("/settings/reset-options")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminTokenFromQueryStringIsRejected() throws Exception {
        // CWE-598: в query string секрет оседает в access-логах, заголовке Referer и
        // истории браузера. Гейт фильтрует по ИСТОЧНИКУ, а не по методу: getParameter
        // сливает query и тело формы, поэтому проверка «метод не GET» пропускала
        // POST с токеном в URL. queryParam (в отличие от param) кладёт значение
        // именно в строку запроса — это и есть воспроизведение дефекта.
        mockMvc.perform(MockMvcRequestBuilders.get("/export")
                        .queryParam("adminToken", "test-admin-token"))
                .andExpect(status().isForbidden());

        mockMvc.perform(MockMvcRequestBuilders.post("/settings/reset-options")
                        .queryParam("adminToken", "test-admin-token")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminTokenFromFormBodyIsAccepted() throws Exception {
        // Обратная сторона того же гейта: форма настроек шлёт токен скрытым полем,
        // и этот путь ломать нельзя. Бьём по /export, а не по reset-options: сброс
        // банка вариантов необратим и испортил бы общий тестовый контекст. POST на
        // /export не замаплен — важно лишь, что ответ НЕ 403, то есть авторизация
        // пропустила запрос дальше в диспетчер.
        mockMvc.perform(MockMvcRequestBuilders.post("/export")
                        .param("adminToken", "test-admin-token")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(result -> assertThat(result.getResponse().getStatus()).isNotEqualTo(403));
    }

    @Test
    void contentSecurityPolicyIsSelfHostedOnly() throws Exception {
        // OPS-005: hljs/mermaid/chart/fonts self-hosted; CSP не должен whitelisting'ить CDN.
        String csp = mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andReturn()
                .getResponse()
                .getHeader("Content-Security-Policy");

        assertThat(csp).isNotNull();
        assertThat(csp).doesNotContain("https://cdnjs.cloudflare.com");
        assertThat(csp).doesNotContain("https://cdn.jsdelivr.net");
        assertThat(csp).doesNotContain("https://fonts.googleapis.com");
        assertThat(csp).doesNotContain("https://fonts.gstatic.com");
        assertThat(csp).contains("script-src 'self'");
        assertThat(csp).contains("style-src 'self'");
        assertThat(csp).contains("font-src 'self'");
        assertThat(csp).contains("connect-src 'self'");
        assertThat(csp).contains("script-src-elem");
        assertThat(csp).contains("style-src-elem");
        assertThat(csp).contains("style-src-attr 'none'");
        assertThat(csp).contains("'nonce-");
        assertThat(csp).doesNotContain("script-src 'self' 'unsafe-inline'");
        assertThat(csp).doesNotContain("unsafe-inline");
    }

    @Test
    void exportRemainsCsrfIgnoredButAdminGated() throws Exception {
        // CSRF ignored for /export; without admin token still 403.
        mockMvc.perform(MockMvcRequestBuilders.get("/export"))
                .andExpect(status().isForbidden());
    }
}
