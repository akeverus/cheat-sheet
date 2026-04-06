package com.cheatsheet.quiz.ui;

import com.cheatsheet.quiz.TestInterviewPath;
import com.cheatsheet.quiz.domain.OptionSource;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.persistence.AnswerOptionRepository;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.service.ai.AiQuestionClient;
import com.cheatsheet.quiz.service.ai.dto.GeneratedOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
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
    @Autowired
    QuestionRepository questionRepository;
    @Autowired
    AnswerOptionRepository answerOptionRepository;

    @MockBean(name = "aiQuestionClient")
    AiQuestionClient aiQuestionClient;

    @BeforeEach
    void setUpAiClient() {
        when(aiQuestionClient.sourceId()).thenReturn(OptionSource.OPENAI);
        when(aiQuestionClient.generateOptions(anyString(), anyString()))
                .thenReturn(java.util.Optional.of(new GeneratedOptions(
                        List.of(
                                new GeneratedOptions.GeneratedOption("Верный вариант про Elasticsearch.", true),
                                new GeneratedOptions.GeneratedOption("Неверный, но правдоподобный вариант про брокеры сообщений.", false),
                                new GeneratedOptions.GeneratedOption("Неверный вариант про транзакционные контроллеры.", false),
                                new GeneratedOptions.GeneratedOption("Неверный вариант про прокси и балансировщики.", false)
                        )
                )));
        ensureQuestionWithOptions();
    }

    @Test
    void focusShellMatchesVisualBaselineContract() throws Exception {
        String body = mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String focusState = body.contains("id=\"interview-form\"") ? "question" : "empty";
        assertThat(focusState).isIn("question", "empty");

        String signature = String.join("\n",
                "title=" + firstGroup(body, "<title>([^<]+)</title>"),
                "bodyClass=" + firstGroup(body, "<body class=\"([^\"]+)\""),
                "navLabels=" + String.join("|", allGroups(body, "<nav[^>]*aria-label=\"([^\"]+)\"")),
                "fragments=" + String.join("|", allGroups(body, "data-ui-fragment=\"([^\"]+)\"")),
                "hasSurfaceToolbar=" + body.contains("surface-toolbar card"),
                "hasFocusTabs=" + body.contains("data-ui-fragment=\"focus-surface-tabs\""),
                "focusState=" + focusState
        );

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
        assertThat(focusBody).contains("id=\"interview-form\"");

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
                "hasStatusLine=" + body.contains("class=\"status\""),
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

    private void ensureQuestionWithOptions() {
        for (long id = 1; id <= 200; id++) {
            if (questionRepository.findById(id).isPresent() && !answerOptionRepository.findByQuestionId(id).isEmpty()) {
                return;
            }
        }

        List<Long> idsWithoutOptions = questionRepository.findAllQuestionIdsWithoutOptions();
        long questionId;
        if (!idsWithoutOptions.isEmpty()) {
            questionId = idsWithoutOptions.get(0);
        } else {
            String slug = "visual-baseline-" + System.nanoTime();
            questionId = questionRepository.insert(new Question(
                    0L,
                    slug,
                    slug,
                    "visual-baseline.md",
                    "sample",
                    "Какой компонент отвечает за полнотекстовый поиск в этом приложении?",
                    "Полнотекстовый поиск здесь опирается на search-индекс и связанную сервисную логику.",
                    false,
                    slug,
                    QuestionType.TEXT,
                    null,
                    null,
                    0,
                    null
            ));
        }

        if (answerOptionRepository.findByQuestionId(questionId).isEmpty()) {
            answerOptionRepository.insertAll(questionId, List.of(
                    new AnswerOptionRepository.AnswerOptionCreate(
                            "Полнотекстовый поиск отвечает за индекс и поиск по сохранённым вопросам.",
                            true,
                            0,
                            OptionSource.OPENAI.name(),
                            null,
                            1,
                            1
                    ),
                    new AnswerOptionRepository.AnswerOptionCreate(
                            "Поиск строится только на HTTP-кэше и не использует индекс вопросов.",
                            false,
                            1,
                            OptionSource.OPENAI.name(),
                            null,
                            1,
                            1
                    ),
                    new AnswerOptionRepository.AnswerOptionCreate(
                            "Поиск полностью делегирован браузеру и не зависит от серверной БД.",
                            false,
                            2,
                            OptionSource.OPENAI.name(),
                            null,
                            1,
                            1
                    ),
                    new AnswerOptionRepository.AnswerOptionCreate(
                            "Поиск работает только через Mermaid-диаграммы и не читает текст вопросов.",
                            false,
                            3,
                            OptionSource.OPENAI.name(),
                            null,
                            1,
                            1
                    )
            ));
        }
    }
}

