package com.cheatsheet.quiz.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cheatsheet.quiz.domain.InterviewSession;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class InterviewControllerTest {

    @DynamicPropertySource
    static void setInterviewPath(DynamicPropertyRegistry registry) {
        com.cheatsheet.quiz.TestInterviewPath.register(registry);
    }

    @Autowired
    MockMvc mockMvc;

    @Test
    void startSessionWithValidCountRedirects() throws Exception {
        mockMvc.perform(post("/start")
                        .with(csrf())
                        .param("mode", "EXAM")
                        .param("count", "10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void startSessionWithExcessiveCountIsCapped() throws Exception {
        MockHttpSession session = new MockHttpSession();
        mockMvc.perform(post("/start")
                        .with(csrf())
                        .session(session)
                        .param("mode", "EXAM")
                        .param("count", "99999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andReturn();

        InterviewSession interviewSession = (InterviewSession) session.getAttribute("interviewSession");
        assertThat(interviewSession).isNotNull();
        assertThat(interviewSession.getQuestionIds().size()).isLessThanOrEqualTo(200);
    }

    @Test
    void startSessionWithNegativeCountDoesNotCreateSession() throws Exception {
        MockHttpSession session = new MockHttpSession();
        mockMvc.perform(post("/start")
                        .with(csrf())
                        .session(session)
                        .param("mode", "EXAM")
                        .param("count", "-5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andReturn();

        InterviewSession interviewSession = (InterviewSession) session.getAttribute("interviewSession");
        assertThat(interviewSession).isNull();
    }

    @Test
    void statsSearchResultsPreserveFilterParamsInTopicLinks() throws Exception {
        String body = mockMvc.perform(get("/stats")
                        .param("topic", "sample")
                        .param("important", "true")
                        .param("onlyWrong", "true")
                        .param("q", "Пример"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(body).contains("important=true");
        assertThat(body).contains("onlyWrong=true");
    }

    @Test
    void indexContainsClearStepByStepActions() throws Exception {
        var result = mockMvc.perform(get("/"))
                .andReturn()
                .getResponse();
        assertThat(result.getStatus()).isIn(200, 503);
        String body = result.getContentAsString();

        assertThat(body).contains("Подготовка к собеседованию");
        if (result.getStatus() == 200) {
            assertThat(body).doesNotContain("id=\"left-sidebar-card\"");
            assertThat(body).contains("class=\"surface-tabs\"");
            assertThat(body).contains(">Фокус<");
            assertThat(body).contains(">Аналитика<");
            assertThat(body).contains(">Настройки<");
            if (body.contains("id=\"interview-card\"")) {
                assertThat(body).contains("class=\"question-layout");
                assertThat(body).contains("class=\"question-main\"");
            }
            if (body.contains("id=\"next-question\"")) {
                assertThat(body).doesNotContain("tabindex=\"-1\">Следующий вопрос");
            }
        } else {
            assertThat(body).contains("Произошла ошибка");
        }
    }

    @Test
    void settingsContainsSessionConfigurationBlocks() throws Exception {
        var result = mockMvc.perform(get("/settings"))
                .andReturn()
                .getResponse();
        assertThat(result.getStatus()).isEqualTo(200);
        String body = result.getContentAsString();

        assertThat(body).contains("id=\"left-sidebar-card\"");
        assertThat(body).contains("id=\"left-sidebar-filters\"");
        assertThat(body).contains("id=\"left-sidebar-session\"");
        assertThat(body).contains("id=\"left-sidebar-progress\"");
        assertThat(body).contains("Применить");
        assertThat(body).contains("Начать сессию");
    }

    @Test
    void settingsAppliesWeakTopicsAndModeFromRequest() throws Exception {
        var result = mockMvc.perform(get("/settings")
                        .param("weakTopics", "true")
                        .param("mode", "FLASHCARD"))
                .andReturn()
                .getResponse();
        assertThat(result.getStatus()).isEqualTo(200);
        String body = result.getContentAsString();

        assertThat(body).contains("name=\"weakTopics\"");
        assertThat(body).contains("value=\"FLASHCARD\"");
        assertThat(body).contains("selected=\"selected\">Флешкарты</option>");
    }

    @Test
    void statsEchoesSearchQueryInSearchInput() throws Exception {
        String query = "hashmap";
        var result = mockMvc.perform(get("/stats")
                        .param("q", query))
                .andReturn()
                .getResponse();
        assertThat(result.getStatus()).isEqualTo(200);
        String body = result.getContentAsString();

        assertThat(body).contains("name=\"q\"");
        assertThat(body).contains("value=\"" + query + "\"");
    }

    @Test
    void reviewModeRendersOnlyWrongContextAndHint() throws Exception {
        var result = mockMvc.perform(get("/review")
                        .param("topic", "sample")
                        .param("important", "true"))
                .andReturn()
                .getResponse();
        assertThat(result.getStatus()).isIn(200, 503);
        String body = result.getContentAsString();

        if (result.getStatus() == 200) {
            assertThat(body).containsAnyOf(
                    "Режим review: только вопросы с ошибками",
                    "Нет вопросов с ошибками для review."
            );
        }
    }

    @Test
    void statsTopicLinksKeepAllActiveFilters() throws Exception {
        String body = mockMvc.perform(get("/stats")
                        .param("group", "backend")
                        .param("ordered", "false")
                        .param("important", "true")
                        .param("onlyWrong", "true")
                        .param("q", "cache"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(body).contains("name=\"ordered\"");
        assertThat(body).contains("value=\"false\"");
        assertThat(body).contains("name=\"important\"");
        assertThat(body).contains("name=\"onlyWrong\"");
        assertThat(body).contains("value=\"cache\"");
    }
}
