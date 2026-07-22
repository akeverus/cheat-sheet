package com.cheatsheet.quiz.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Hybrid multi-user skeleton: HTTP Basic + CSRF for mutating endpoints.
 */
@SpringBootTest(properties = {
        "app.admin-token=test-admin-token",
        "app.security.mode=multi",
        "app.security.multi.username=trainer",
        "app.security.multi.password=trainer",
        "app.security.multi.extra-users[0].username=alice",
        "app.security.multi.extra-users[0].password=alice"
})
@AutoConfigureMockMvc
class MultiUserSecurityWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void progressPagesRequireAuthInMulti() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/")
                        .accept(org.springframework.http.MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(result -> assertThat(result.getResponse().getRedirectedUrl())
                        .contains("/login"));
        mockMvc.perform(MockMvcRequestBuilders.get("/settings")
                        .accept(org.springframework.http.MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(result -> assertThat(result.getResponse().getRedirectedUrl())
                        .contains("/login"));
        mockMvc.perform(MockMvcRequestBuilders.get("/stats")
                        .accept(org.springframework.http.MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(result -> assertThat(result.getResponse().getRedirectedUrl())
                        .contains("/login"));
    }

    @Test
    void mutatingApiWithoutAuthChallengesWithBasic() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/favorite")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("WWW-Authenticate", org.hamcrest.Matchers.containsString("Basic")));
    }

    @Test
    void mutatingApiWithBasicAuthPassesAuthGate() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/favorite")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(httpBasic("trainer", "trainer")))
                .andExpect(result -> assertThat(result.getResponse().getStatus())
                        .isNotIn(401, 403));
    }

    @Test
    void extraUserCanAuthenticate() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/favorite")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(httpBasic("alice", "alice")))
                .andExpect(result -> assertThat(result.getResponse().getStatus())
                        .isNotIn(401, 403));
    }

    @Test
    void loginPageIsPublic() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/login"))
                .andExpect(status().isOk())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString())
                        .contains("login-form"));
    }

    @Test
    void formLoginAuthenticatesAndRedirectsHome() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .param("username", "trainer")
                        .param("password", "trainer")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(result -> assertThat(result.getResponse().getRedirectedUrl()).isEqualTo("/"));
    }

    @Test
    void formLoginRestoresSavedRequest() throws Exception {
        var challenge = mockMvc.perform(MockMvcRequestBuilders.get("/dashboard")
                        .accept(org.springframework.http.MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andReturn();
        var session = challenge.getRequest().getSession(false);
        assertThat(session).isNotNull();

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .session((org.springframework.mock.web.MockHttpSession) session)
                        .param("username", "trainer")
                        .param("password", "trainer")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(result -> assertThat(result.getResponse().getRedirectedUrl())
                        .contains("/dashboard"));
    }

    @Test
    void protectedHtmlRedirectsToLogin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/dashboard")
                        .accept(org.springframework.http.MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(result -> assertThat(result.getResponse().getRedirectedUrl())
                        .contains("/login"));
    }

    @Test
    void wrongPasswordIsRejected() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/favorite")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(httpBasic("trainer", "wrong")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminExportStillRequiresAdminTokenEvenWhenAuthenticated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/export")
                        .with(httpBasic("trainer", "trainer")))
                .andExpect(status().isForbidden());
    }

    @Test
    void actuatorMetricsRequireAuthInMulti() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/actuator/metrics")
                        .accept(org.springframework.http.MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(result -> assertThat(result.getResponse().getRedirectedUrl())
                        .contains("/login"));
        mockMvc.perform(MockMvcRequestBuilders.get("/actuator/metrics")
                        .accept(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(MockMvcRequestBuilders.get("/actuator/health"))
                .andExpect(status().isOk());
    }

    @Test
    void resetOptionsRequiresAdminGateEvenWhenAuthenticated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/settings/reset-options")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(httpBasic("trainer", "trainer")))
                .andExpect(status().isForbidden());
    }
}
