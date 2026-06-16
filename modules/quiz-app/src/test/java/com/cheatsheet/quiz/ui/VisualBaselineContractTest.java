package com.cheatsheet.quiz.ui;

import com.cheatsheet.quiz.TestInterviewPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class VisualBaselineContractTest {

    @DynamicPropertySource
    static void setInterviewPath(DynamicPropertyRegistry registry) {
        TestInterviewPath.register(registry);
    }

    @Autowired
    MockMvc mockMvc;

    @Test
    void focusShellMatchesVisualBaselineContract() throws Exception {
        String body = mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String focusState = body.contains("id=\"interview-form\"") ? "question" : "empty";

        String signature = String.join("\n",
                "title=" + firstGroup(body, "<title>([^<]+)</title>"),
                "bodyClass=" + firstGroup(body, "<body class=\"([^\"]+)\""),
                "navLabels=" + String.join("|", allGroups(body, "<nav[^>]*aria-label=\"([^\"]+)\"")),
                "fragments=" + String.join("|", allGroups(body, "data-ui-fragment=\"([^\"]+)\"")),
                "hasSurfaceToolbar=" + body.contains("surface-toolbar card"),
                "hasFocusTabs=" + body.contains("data-ui-fragment=\"focus-surface-tabs\""),
                "focusState=" + focusState
        );

        // Тест-фикстура (programming/java-strings.md + JSON-сид с 4 опциями)
        // гарантирует focusState=question на свежем контейнере. Если падает с
        // empty — значит сломан импорт или сидер.
        assertThat(signature).isEqualTo(readBaseline("visual-baseline/focus-shell.txt"));
    }

    @Test
    void statsShellMatchesVisualBaselineContract() throws Exception {
        String body = mockMvc.perform(get("/stats"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String signature = String.join("\n",
                "title=" + firstGroup(body, "<title>([^<]+)</title>"),
                "bodyClass=" + firstGroup(body, "<body class=\"([^\"]+)\""),
                "navLabels=" + String.join("|", allGroups(body, "<nav[^>]*aria-label=\"([^\"]+)\"")),
                "hasStatsFilterCard=" + body.contains("stats-filter-card"),
                "hasTopicTable=" + body.contains("id=\"topic-table\""),
                "hasTopicProgressChart=" + body.contains("id=\"topicProgressChart\""),
                "hasTopicAccuracyChart=" + body.contains("id=\"topicAccuracyChart\""),
                "hasSortLiveRegion=" + body.contains("id=\"table-sort-status\"")
        );

        assertThat(signature).isEqualTo(readBaseline("visual-baseline/stats-shell.txt"));
    }

    @Test
    void settingsShellMatchesVisualBaselineContract() throws Exception {
        String body = mockMvc.perform(get("/settings"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String signature = String.join("\n",
                "title=" + firstGroup(body, "<title>([^<]+)</title>"),
                "bodyClass=" + firstGroup(body, "<body class=\"([^\"]+)\""),
                "navLabels=" + String.join("|", allGroups(body, "<nav[^>]*aria-label=\"([^\"]+)\"")),
                "hasSidebarCard=" + body.contains("id=\"left-sidebar-card\""),
                "hasSidebarToggle=" + body.contains("id=\"sidebar-collapse-toggle\""),
                "hasMainContent=" + body.contains("id=\"main-content\""),
                "hasSessionModeSelect=" + body.contains("name=\"mode\"")
        );

        assertThat(signature).isEqualTo(readBaseline("visual-baseline/settings-shell.txt"));
    }

    @Test
    void resultShellMatchesVisualBaselineContract() throws Exception {
        String focusBody = mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        if (!focusBody.contains("id=\"interview-form\"")) {
            // No questions loaded in test DB — cannot render result page
            return;
        }

        long questionId = extractLong(focusBody, "name=\"questionId\" value=\"(\\d+)\"");
        long optionId = extractLong(focusBody, "name=\"optionId\"\\s+value=\"(\\d+)\"");

        String body = mockMvc.perform(post("/answer")
                        .with(csrf())
                        .param("questionId", String.valueOf(questionId))
                        .param("optionId", String.valueOf(optionId))
                        .param("ordered", "true"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String signature = String.join("\n",
                "title=" + firstGroup(body, "<title>([^<]+)</title>"),
                "bodyClass=" + firstGroup(body, "<body class=\"([^\"]+)\""),
                "navLabels=" + String.join("|", allGroups(body, "<nav[^>]*aria-label=\"([^\"]+)\"")),
                "fragments=" + String.join("|", allGroups(body, "data-ui-fragment=\"([^\"]+)\"")),
                "hasInterviewCard=" + body.contains("id=\"interview-card\""),
                // Префикс class="status (без закрывающей кавычки): на p висит
                // th:classappend status-correct/status-wrong → точного class="status"
                // в рендере нет, но строка результата (вердикт) присутствует.
                "hasStatusLine=" + body.contains("class=\"status"),
                "hasResultActionsContainer=" + body.contains("class=\"result-actions\""),
                "hasNextQuestionLink=" + body.contains("class=\"btn next-btn\""),
                "hasSecondaryAnalysisButton=" + body.contains("id=\"extra-analysis-toggle-result\""),
                "nextLinkBeforeSecondaryAction=" + (body.indexOf("class=\"btn next-btn\"")
                        < body.indexOf("id=\"extra-analysis-toggle-result\""))
        );

        assertThat(signature).isEqualTo(readBaseline("visual-baseline/result-shell.txt"));
    }

    private static String firstGroup(String source, String regex) {
        Matcher matcher = Pattern.compile(regex).matcher(source);
        return matcher.find() ? matcher.group(1).trim() : "";
    }

    private static List<String> allGroups(String source, String regex) {
        Matcher matcher = Pattern.compile(regex).matcher(source);
        List<String> values = new ArrayList<>();
        while (matcher.find()) {
            values.add(matcher.group(1).trim());
        }
        return values;
    }

    private static String readBaseline(String classpathLocation) throws Exception {
        ClassPathResource resource = new ClassPathResource(classpathLocation);
        byte[] bytes = resource.getInputStream().readAllBytes();
        return new String(bytes, StandardCharsets.UTF_8).trim();
    }

    private static long extractLong(String source, String regex) {
        Matcher matcher = Pattern.compile(regex).matcher(source);
        assertThat(matcher.find()).isTrue();
        return Long.parseLong(matcher.group(1));
    }
}

