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
    void csrfIsIgnoredForApiEndpoints() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/favorite"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void sensitiveEndpointsAreBlockedWithoutAdminToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/senior-rules"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("FORBIDDEN"))
                .andExpect(jsonPath("$.message").isString());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/regenerate"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("FORBIDDEN"));

        mockMvc.perform(MockMvcRequestBuilders.get("/export"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("FORBIDDEN"));
    }

    @Test
    void sensitiveEndpointsAllowValidAdminToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/admin/senior-rules")
                        .header("X-Admin-Token", "test-admin-token"))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/regenerate")
                        .header("X-Admin-Token", "test-admin-token"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(MockMvcRequestBuilders.get("/export")
                        .header("X-Admin-Token", "test-admin-token"))
                .andExpect(status().isOk());
    }

    @Test
    void contentSecurityPolicyAllowsKnownCdnDeps() throws Exception {
        // Шаблоны грузят шрифты с Google Fonts, highlight.js с cdnjs и
        // mermaid с jsdelivr. Если кто-то снова сожмёт CSP до "self
        // 'unsafe-inline'" — страницы посыпятся console errors. Этот тест —
        // защита от такой регрессии.
        String csp = mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andReturn()
                .getResponse()
                .getHeader("Content-Security-Policy");

        assertThat(csp).isNotNull();
        assertThat(csp).contains("https://cdnjs.cloudflare.com");
        assertThat(csp).contains("https://cdn.jsdelivr.net");
        assertThat(csp).contains("https://fonts.googleapis.com");
        assertThat(csp).contains("https://fonts.gstatic.com");
        assertThat(csp).contains("script-src-elem");
        assertThat(csp).contains("style-src-elem");
        assertThat(csp).contains("font-src");
    }
}
