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

    private int countOccurrences(String text, String fragment) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(fragment, index)) != -1) {
            count++;
            index += fragment.length();
        }
        return count;
    }

    private void assertNoInlineUiAttributes(String body) {
        assertThat(body).doesNotContain(" style=\"");
        assertThat(body).doesNotContain(" onclick=\"");
        assertThat(body).doesNotContain(" onchange=\"");
        assertThat(body).doesNotContain(" oninput=\"");
        assertThat(body).doesNotContain(" onsubmit=\"");
        assertThat(body).doesNotContain(" onkeydown=\"");
        assertThat(body).doesNotContain(" onkeyup=\"");
    }

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
            assertThat(body).contains("aria-label=\"Основная навигация\"");
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
    void indexFallbackStateKeepsDegradedUxContract() throws Exception {
        var result = mockMvc.perform(get("/"))
                .andReturn()
                .getResponse();
        assertThat(result.getStatus()).isIn(200, 503);
        if (result.getStatus() == 200) {
            String body = result.getContentAsString();
            if (body.contains("AI временно недоступен") || body.contains("Нет доступных вопросов")) {
                assertThat(body).containsAnyOf(
                        "AI временно недоступен",
                        "Нет доступных вопросов по выбранным фильтрам."
                );
                assertThat(body).contains("empty-action-settings");
                assertThat(body).contains("empty-action-retry");
                assertThat(body).doesNotContain("id=\"interview-form\"");
                assertThat(body).doesNotContain("id=\"interview-options\"");
                assertThat(body).doesNotContain("id=\"interview-submit\"");
            }
        }
    }

    @Test
    void settingsContainsSessionConfigurationBlocks() throws Exception {
        var result = mockMvc.perform(get("/settings"))
                .andReturn()
                .getResponse();
        assertThat(result.getStatus()).isEqualTo(200);
        String body = result.getContentAsString();
        // Редизайн на вкладки: лаунчер сессии живёт в панели «Сессия».
        assertThat(body).contains("id=\"panel-session\"");
        assertThat(body).contains("id=\"filters-form\"");
        assertThat(body).contains("id=\"session-form\"");
        assertThat(body).contains("Применить");
        assertThat(body).contains("Начать сессию");
    }

    @Test
    void settingsTabStructureKeepsAccessibilityContract() throws Exception {
        String body = mockMvc.perform(get("/settings"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(body).contains("type=\"button\"");
        assertThat(body).contains("id=\"main-content\"");
        // Вкладки с корректным ARIA: tablist + три tabpanel.
        assertThat(body).contains("role=\"tablist\"");
        assertThat(body).contains("role=\"tab\"");
        assertThat(body).contains("role=\"tabpanel\"");
        assertThat(body).contains("aria-controls=\"panel-session\"");
        assertThat(body).contains("id=\"panel-appearance\"");
        assertThat(body).contains("id=\"panel-data\"");
        // Старый sidebar-каркас и мёртвый тоггл сворачивания убраны редизайном.
        assertThat(body).doesNotContain("id=\"left-sidebar-card\"");
        assertThat(body).doesNotContain("id=\"sidebar-collapse-toggle\"");
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
    void keyPagesExposeExpectedBodyPageNamespaces() throws Exception {
        String statsBody = mockMvc.perform(get("/stats"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThat(statsBody).contains("<body class=\"stats-page\">");

        String settingsBody = mockMvc.perform(get("/settings"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThat(settingsBody).contains("<body class=\"settings-page\">");
    }

    @Test
    void exportActionsRenderOnlyOnSettingsRoute() throws Exception {
        // Экспорт прогресса (JSON/CSV) живёт на /settings → «Управление данными».
        // Раньше дублировался ссылками nav-export в шапке; перенесён в единую точку,
        // шапка очищена (header.html). Маркер теперь — data-export-format на кнопках.
        String settingsBody = mockMvc.perform(get("/settings"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThat(settingsBody).contains("data-export-format=\"json\"");
        assertThat(settingsBody).contains("data-export-format=\"csv\"");
        assertThat(settingsBody).contains("Экспорт JSON");
        assertThat(settingsBody).contains("Экспорт CSV");

        String statsBody = mockMvc.perform(get("/stats"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThat(statsBody).doesNotContain("data-export-format=");

        var indexResult = mockMvc.perform(get("/")).andReturn().getResponse();
        assertThat(indexResult.getStatus()).isIn(200, 503);
        if (indexResult.getStatus() == 200) {
            assertThat(indexResult.getContentAsString()).doesNotContain("data-export-format=");
        }

        var trainingResult = mockMvc.perform(get("/training")).andReturn().getResponse();
        assertThat(trainingResult.getStatus()).isIn(200, 503);
        if (trainingResult.getStatus() == 200) {
            assertThat(trainingResult.getContentAsString()).doesNotContain("data-export-format=");
        }
    }

    @Test
    void statsRouteExposesChartFallbackAccessibilityContract() throws Exception {
        String body = mockMvc.perform(get("/stats"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThat(body).contains("id=\"topicProgressChart\"");
        assertThat(body).contains("id=\"topicAccuracyChart\"");
        assertThat(body).contains("id=\"topicProgressChartFallback\"");
        assertThat(body).contains("id=\"topicAccuracyChartFallback\"");
        assertThat(body).contains("class=\"chart-fallback hidden\"");
        assertThat(body).contains("role=\"status\"");
        assertThat(body).contains("aria-live=\"polite\"");
    }

    @Test
    void statsFilterActionsExposeClearCtaContract() throws Exception {
        String body = mockMvc.perform(get("/stats"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(body).contains("stats-filters-form");
        assertThat(body).contains("stats-search-form");
        assertThat(countOccurrences(body, "stats-action-btn")).isGreaterThanOrEqualTo(2);
        assertThat(body).contains("stats-apply-action");
        assertThat(body).contains("aria-label=\"Применить фильтры статистики\"");
        assertThat(body).contains("Применить фильтры");
        assertThat(body).contains("stats-search-action");
        assertThat(body).contains("aria-label=\"Искать по вопросам\"");
    }

    @Test
    void focusRoutesExposeFocusPageNamespaceWhenRendered() throws Exception {
        var indexResult = mockMvc.perform(get("/")).andReturn().getResponse();
        assertThat(indexResult.getStatus()).isIn(200, 503);
        if (indexResult.getStatus() == 200) {
            assertThat(indexResult.getContentAsString()).contains("<body class=\"focus-page\">");
        }

        var trainingResult = mockMvc.perform(get("/training")).andReturn().getResponse();
        assertThat(trainingResult.getStatus()).isIn(200, 503);
        if (trainingResult.getStatus() == 200) {
            assertThat(trainingResult.getContentAsString()).contains("<body class=\"focus-page\">");
        }
    }

    @Test
    void focusRoutesKeepKeyboardHintAndTimerLiveRegionContracts() throws Exception {
        var indexResult = mockMvc.perform(get("/")).andReturn().getResponse();
        assertThat(indexResult.getStatus()).isIn(200, 503);
        if (indexResult.getStatus() == 200) {
            String body = indexResult.getContentAsString();
            if (body.contains("id=\"interview-form\"")) {
                assertThat(body).contains("class=\"keyboard-hint\"");
                assertThat(body).contains("id=\"question-timer\"");
                assertThat(body).contains("aria-live=\"polite\"");
            }
        }

        var trainingResult = mockMvc.perform(get("/training")).andReturn().getResponse();
        assertThat(trainingResult.getStatus()).isIn(200, 503);
        if (trainingResult.getStatus() == 200) {
            String body = trainingResult.getContentAsString();
            if (body.contains("id=\"interview-form\"")) {
                assertThat(body).contains("class=\"keyboard-hint\"");
                assertThat(body).contains("id=\"question-timer\"");
                assertThat(body).contains("aria-live=\"polite\"");
            }
        }
    }

    @Test
    void trainingProgressTrackKeepsBoundedAriaValuesContract() throws Exception {
        var result = mockMvc.perform(get("/training"))
                .andReturn()
                .getResponse();
        assertThat(result.getStatus()).isIn(200, 503);
        if (result.getStatus() == 200) {
            String body = result.getContentAsString();
            if (body.contains("aria-label=\"Прогресс сессии\"")) {
                assertThat(body).contains("aria-valuemin=\"0\"");
                assertThat(body).containsPattern("aria-valuemax=\"\\d+\"");
                assertThat(body).containsPattern("aria-valuenow=\"\\d+\"");
                assertThat(body).doesNotContain("aria-valuemax=\"100\"");
            }
        }
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
            if (body.contains("class=\"zone-chip\"")) {
                assertThat(body).contains("Повтор ошибок");
                assertThat(body).contains("Режим review: отвечай на вопросы с ошибками.");
            }
        }
    }

    @Test
    void reviewPageAvoidsInlineStylesAndInlineEventsWhenRendered() throws Exception {
        var result = mockMvc.perform(get("/review"))
                .andReturn()
                .getResponse();
        assertThat(result.getStatus()).isIn(200, 503);
        if (result.getStatus() == 200) {
            assertNoInlineUiAttributes(result.getContentAsString());
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

    @Test
    void indexFavoriteButtonExposesPressedStateForAssistiveTech() throws Exception {
        var result = mockMvc.perform(get("/"))
                .andReturn()
                .getResponse();
        assertThat(result.getStatus()).isIn(200, 503);
        if (result.getStatus() == 200) {
            String body = result.getContentAsString();
            if (body.contains("id=\"btn-favorite\"")) {
                assertThat(body).contains("id=\"btn-favorite\"");
                assertThat(body).contains("aria-pressed=");
                assertThat(body).containsAnyOf(
                        "aria-label=\"Добавить в избранное\"",
                        "aria-label=\"Убрать из избранного\""
                );
            }
        }
    }

    @Test
    void statsTableHeadersExposeKeyboardSortableContract() throws Exception {
        String body = mockMvc.perform(get("/stats"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Сервер рендерит «скелет» сортируемой таблицы: класс .sortable + data-метки
        // на каждой сортируемой колонке + скрытый live-region для анонса сортировки.
        // Клавиатурные ARIA-атрибуты (role=columnheader / tabindex / aria-sort /
        // aria-keyshortcuts="Enter Space") навешивает stats.js при инициализации —
        // это progressive enhancement, в серверном HTML их НЕТ (round-01 C28:
        // role=columnheader у нативного <th> избыточен, состояние держит aria-sort).
        assertThat(body).contains("th class=\"sortable\"");
        assertThat(countOccurrences(body, "class=\"sortable")).isGreaterThanOrEqualTo(4);
        assertThat(body).contains("data-sort-label=");
        assertThat(body).contains("id=\"table-sort-status\"");
        assertThat(body).contains("id=\"table-sort-status\" class=\"visually-hidden\"");
        assertThat(body).contains("aria-live=\"polite\"");
        assertThat(body).contains("aria-atomic=\"true\"");
    }

    @Test
    void indexActionFooterExposesKeyboardShortcuts() throws Exception {
        var result = mockMvc.perform(get("/"))
                .andReturn()
                .getResponse();
        assertThat(result.getStatus()).isIn(200, 503);
        if (result.getStatus() == 200) {
            String body = result.getContentAsString();
            if (body.contains("id=\"interview-submit\"")) {
                assertThat(body).contains("id=\"interview-submit\"");
                assertThat(body).contains("aria-keyshortcuts=\"Enter\"");
            }
            if (body.contains("id=\"next-question\"")) {
                assertThat(body).contains("id=\"next-question\"");
                // next-question — это <a href>: нативно активируется Enter, не Space
                // (app.js на Space для тегов A обработчик не вешает). aria-keyshortcuts
                // честно обещает только Enter — паритет с submit-кнопкой выше.
                assertThat(body).contains("aria-keyshortcuts=\"Enter\"");
            }
        }
    }

    @Test
    void trainingPageAvoidsInlineOnchangeHandlers() throws Exception {
        var result = mockMvc.perform(get("/training"))
                .andReturn()
                .getResponse();
        assertThat(result.getStatus()).isIn(200, 503);
        if (result.getStatus() == 200) {
            String body = result.getContentAsString();
            assertThat(body).doesNotContain("onchange=\"enableSubmit()\"");
        }
    }

    @Test
    void trainingPageUsesUnifiedKeyboardHintClass() throws Exception {
        var result = mockMvc.perform(get("/training"))
                .andReturn()
                .getResponse();
        assertThat(result.getStatus()).isIn(200, 503);
        if (result.getStatus() == 200) {
            String body = result.getContentAsString();
            if (body.contains("id=\"interview-form\"")) {
                assertThat(body).contains("class=\"keyboard-hint\"");
            }
            assertThat(body).doesNotContain("class=\"kbd-hint\"");
        }
    }

    @Test
    void trainingPageUsesUnifiedSubmitIdAndHotkeyHintContract() throws Exception {
        var result = mockMvc.perform(get("/training"))
                .andReturn()
                .getResponse();
        assertThat(result.getStatus()).isIn(200, 503);
        if (result.getStatus() == 200) {
            String body = result.getContentAsString();
            if (body.contains("id=\"interview-submit\"")) {
                assertThat(body).contains("id=\"interview-submit\"");
                assertThat(body).contains("aria-keyshortcuts=\"Enter\"");
                assertThat(body).containsAnyOf("Клавиши 1-", "Клавиши 1–");
            }
            assertThat(body).doesNotContain("id=\"submitBtn\"");
        }
    }

    @Test
    void trainingPageAvoidsRedundantLabelRadioRolesAndTabStops() throws Exception {
        var result = mockMvc.perform(get("/training"))
                .andReturn()
                .getResponse();
        assertThat(result.getStatus()).isIn(200, 503);
        if (result.getStatus() == 200) {
            String body = result.getContentAsString();
            if (body.contains("id=\"interview-options\"")) {
                assertThat(body).contains("aria-describedby=\"options-flow-hint\"");
                assertThat(body).doesNotContain("role=\"radio\"");
                assertThat(body).doesNotContain("tabindex=\"0\"");
            }
        }
    }

    @Test
    void trainingFallbackStateExposesRecoveryActionsWhenNoQuestion() throws Exception {
        var result = mockMvc.perform(get("/training"))
                .andReturn()
                .getResponse();
        assertThat(result.getStatus()).isIn(200, 503);
        if (result.getStatus() == 200) {
            String body = result.getContentAsString();
            if (body.contains("Нет доступных вопросов") || body.contains("AI временно недоступен")) {
                assertThat(body).contains("empty-action-settings");
                assertThat(body).contains("empty-action-retry");
                assertThat(body).contains("href=\"/training\"");
                assertThat(body).contains("Обновить тренировку");
            }
            if (body.contains("Сессия запущена, но вопрос пока недоступен.")) {
                assertThat(body).contains("Сессия запущена, но вопрос пока недоступен. Попробуй обновить тренировку.");
                assertThat(body).contains("empty-action-retry");
            }
        }
    }

    @Test
    void reviewFallbackStateKeepsReviewRecoveryActionWhenNoWrongQuestions() throws Exception {
        var result = mockMvc.perform(get("/review"))
                .andReturn()
                .getResponse();
        assertThat(result.getStatus()).isIn(200, 503);
        if (result.getStatus() == 200) {
            String body = result.getContentAsString();
            if (body.contains("Нет вопросов с ошибками для review.")) {
                assertThat(body).contains("empty-action-retry");
                assertThat(body).contains("href=\"/review\"");
                assertThat(body).contains("Обновить review");
            }
        }
    }

    @Test
    void flashcardModeKeepsContextualCopyAndRecoveryContract() throws Exception {
        var result = mockMvc.perform(get("/training")
                        .param("mode", "FLASHCARD"))
                .andReturn()
                .getResponse();
        assertThat(result.getStatus()).isIn(200, 503);
        if (result.getStatus() == 200) {
            String body = result.getContentAsString();
            if (body.contains("class=\"zone-chip\"")) {
                assertThat(body).contains("Флешкарты");
                assertThat(body).contains("Флешкарты: сначала вспомни ответ, затем раскрой и оцени себя.");
                assertThat(body).doesNotContain("Выбери один вариант. Проверка и разбор идут по шагам.");
            }
            if (body.contains("Нет доступных вопросов") || body.contains("AI временно недоступен")) {
                assertThat(body).contains("empty-action-retry");
                assertThat(body).contains("href=\"/training?mode=FLASHCARD\"");
                assertThat(body).contains("Обновить флешкарты");
            }
        }
    }

    @Test
    void studyModeKeepsContextualCopyAndRecoveryContract() throws Exception {
        var result = mockMvc.perform(get("/training")
                        .param("mode", "STUDY"))
                .andReturn()
                .getResponse();
        assertThat(result.getStatus()).isIn(200, 503);
        if (result.getStatus() == 200) {
            String body = result.getContentAsString();
            if (body.contains("class=\"zone-chip\"")) {
                assertThat(body).contains("Изучение");
                assertThat(body).contains("Изучение: разберись с материалом, затем переходи к проверке.");
                assertThat(body).doesNotContain("Выбери один вариант. Проверка и разбор идут по шагам.");
            }
            if (body.contains("Нет доступных вопросов") || body.contains("AI временно недоступен")) {
                assertThat(body).contains("empty-action-retry");
                assertThat(body).contains("href=\"/training?mode=STUDY\"");
                assertThat(body).contains("Обновить изучение");
            }
        }
    }

    @Test
    void indexAndTrainingKeepSharedAnswerFlowHintContract() throws Exception {
        var index = mockMvc.perform(get("/")).andReturn().getResponse();
        assertThat(index.getStatus()).isIn(200, 503);
        if (index.getStatus() == 200) {
            String body = index.getContentAsString();
            if (body.contains("id=\"interview-form\"")) {
                assertThat(body).contains("id=\"answer-flow-hint\"");
                assertThat(body).contains("result-zone-head hidden");
            }
        }

        var training = mockMvc.perform(get("/training")).andReturn().getResponse();
        assertThat(training.getStatus()).isIn(200, 503);
        if (training.getStatus() == 200) {
            String body = training.getContentAsString();
            if (body.contains("id=\"interview-form\"")) {
                assertThat(body).contains("id=\"answer-flow-hint\"");
                assertThat(body).contains("result-zone-head hidden");
            }
        }
    }

    @Test
    void indexAndTrainingKeepSharedPostAnswerControlsContract() throws Exception {
        var index = mockMvc.perform(get("/")).andReturn().getResponse();
        assertThat(index.getStatus()).isIn(200, 503);
        if (index.getStatus() == 200) {
            String body = index.getContentAsString();
            if (body.contains("id=\"interview-form\"")) {
                assertThat(body).contains("id=\"interview-alert\"");
                assertThat(body).contains("id=\"result-feedback\"");
                assertThat(body).contains("id=\"extra-analysis-toggle\"");
                assertThat(body).contains("aria-controls=\"extra-analysis-content\"");
                assertThat(body).contains("aria-atomic=\"true\"");
                assertThat(body).contains("id=\"extra-analysis-content\"");
            }
        }

        var training = mockMvc.perform(get("/training")).andReturn().getResponse();
        assertThat(training.getStatus()).isIn(200, 503);
        if (training.getStatus() == 200) {
            String body = training.getContentAsString();
            if (body.contains("id=\"interview-form\"")) {
                assertThat(body).contains("id=\"interview-alert\"");
                assertThat(body).contains("id=\"result-feedback\"");
                assertThat(body).contains("id=\"extra-analysis-toggle\"");
                assertThat(body).contains("aria-controls=\"extra-analysis-content\"");
                assertThat(body).contains("aria-atomic=\"true\"");
                assertThat(body).contains("id=\"extra-analysis-content\"");
            }
        }
    }

    @Test
    void indexAndTrainingUseUnifiedPostAnswerCopyContract() throws Exception {
        var index = mockMvc.perform(get("/")).andReturn().getResponse();
        assertThat(index.getStatus()).isIn(200, 503);
        if (index.getStatus() == 200) {
            String body = index.getContentAsString();
            if (body.contains("id=\"interview-form\"")) {
                assertThat(body).contains("Пост-разбор");
                assertThat(body).contains("Сначала итог, затем объяснение и дополнительные блоки");
                assertThat(body).contains("Показать доп. анализ");
            }
        }

        var training = mockMvc.perform(get("/training")).andReturn().getResponse();
        assertThat(training.getStatus()).isIn(200, 503);
        if (training.getStatus() == 200) {
            String body = training.getContentAsString();
            if (body.contains("id=\"interview-form\"")) {
                assertThat(body).contains("Пост-разбор");
                assertThat(body).contains("Сначала итог, затем объяснение и дополнительные блоки");
                assertThat(body).contains("Показать доп. анализ");
                assertThat(body).doesNotContain("Подробнее");
            }
        }
    }

    @Test
    void indexAndTrainingExposeSharedFragmentMarkers() throws Exception {
        var index = mockMvc.perform(get("/")).andReturn().getResponse();
        assertThat(index.getStatus()).isIn(200, 503);
        if (index.getStatus() == 200) {
            String body = index.getContentAsString();
            if (body.contains("id=\"interview-form\"")) {
                assertThat(body).contains("data-ui-fragment=\"training-actions\"");
                assertThat(body).contains("data-ui-fragment=\"result-zone-head\"");
                assertThat(body).contains("data-ui-fragment=\"post-answer-controls\"");
                assertThat(body).contains("data-ui-fragment=\"inline-alert\"");
            }
        }

        var training = mockMvc.perform(get("/training")).andReturn().getResponse();
        assertThat(training.getStatus()).isIn(200, 503);
        if (training.getStatus() == 200) {
            String body = training.getContentAsString();
            if (body.contains("id=\"interview-form\"")) {
                assertThat(body).contains("data-ui-fragment=\"training-actions\"");
                assertThat(body).contains("data-ui-fragment=\"result-zone-head\"");
                assertThat(body).contains("data-ui-fragment=\"post-answer-controls\"");
                assertThat(body).contains("data-ui-fragment=\"inline-alert\"");
            }
        }
    }

    @Test
    void focusPagesExposeSingleUnifiedPrimaryNavigation() throws Exception {
        // Editorial IA: дубль-навигация устранена — единая «Основная навигация» в
        // шапке на всех страницах; surface-tabs («Навигация по режимам фокуса») удалена.
        var index = mockMvc.perform(get("/")).andReturn().getResponse();
        assertThat(index.getStatus()).isIn(200, 503);
        if (index.getStatus() == 200) {
            String body = index.getContentAsString();
            assertThat(body).doesNotContain("data-ui-fragment=\"focus-surface-tabs\"");
            assertThat(countOccurrences(body, "aria-label=\"Основная навигация\"")).isEqualTo(1);
            assertThat(countOccurrences(body, "aria-label=\"Навигация по режимам фокуса\"")).isEqualTo(0);
        }

        var training = mockMvc.perform(get("/training")).andReturn().getResponse();
        assertThat(training.getStatus()).isIn(200, 503);
        if (training.getStatus() == 200) {
            String body = training.getContentAsString();
            assertThat(body).doesNotContain("data-ui-fragment=\"focus-surface-tabs\"");
            assertThat(countOccurrences(body, "aria-label=\"Основная навигация\"")).isEqualTo(1);
            assertThat(countOccurrences(body, "aria-label=\"Навигация по режимам фокуса\"")).isEqualTo(0);
        }
    }

    @Test
    void keyPagesAvoidInlineStylesAndInlineEvents() throws Exception {
        var index = mockMvc.perform(get("/")).andReturn().getResponse();
        assertThat(index.getStatus()).isIn(200, 503);
        if (index.getStatus() == 200) {
            assertNoInlineUiAttributes(index.getContentAsString());
        }

        var training = mockMvc.perform(get("/training")).andReturn().getResponse();
        assertThat(training.getStatus()).isIn(200, 503);
        if (training.getStatus() == 200) {
            assertNoInlineUiAttributes(training.getContentAsString());
        }

        var stats = mockMvc.perform(get("/stats")).andReturn().getResponse();
        assertThat(stats.getStatus()).isEqualTo(200);
        assertNoInlineUiAttributes(stats.getContentAsString());

        var settings = mockMvc.perform(get("/settings")).andReturn().getResponse();
        assertThat(settings.getStatus()).isEqualTo(200);
        assertNoInlineUiAttributes(settings.getContentAsString());
    }
}
